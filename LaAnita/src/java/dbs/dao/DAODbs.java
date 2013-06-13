package dbs.dao;

import dbs.dominio.Dbs;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import usuarios.UsuarioSesion;
import usuarios.dominio.Usuario;

public class DAODbs {

    private final DataSource ds;

    public DAODbs() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public Dbs[] obtenerDbs() throws SQLException {
        Dbs[] dbs = null;
        Connection cn = ds.getConnection();

        try {
            String strSQL = "SELECT * FROM basesDeDatos";
            Statement sentencia = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = sentencia.executeQuery(strSQL);
            if (rs.next()) {
                int i = 0;
                rs.last();
                dbs = new Dbs[rs.getRow()];

                rs.beforeFirst();
                while (rs.next()) {
                    dbs[i++] = construir(rs);
                }
            }
        } finally {
            cn.close();
        }
        return dbs;
    }

    private Dbs construir(ResultSet rs) throws SQLException {
        Dbs dbs = new Dbs();
        dbs.setIdDbs(rs.getInt("idBaseDeDatos"));
        dbs.setNombreBds(rs.getString("baseDeDatos"));
        dbs.setJndiDbs(rs.getString("jndi"));
        return dbs;
    }

    public Dbs obtener(int idDbs) throws SQLException {
        Dbs dbs = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM basesDeDatos WHERE idBaseDeDatos=" + idDbs);
            if (rs.next()) {
                dbs = construir(rs);
            }
        } finally {
            cn.close();
        }
        return dbs;
    }
    
    public Usuario login(String login, String password, String jndi) throws NamingException, SQLException {
        Usuario usuario=null;
        Context cI = new InitialContext();
        DataSource ds1=(DataSource) cI.lookup("java:comp/env/"+jndi);
        Connection cn1=ds1.getConnection();
        Statement st1 = cn1.createStatement();
        try {
            ResultSet rs1=st1.executeQuery("SELECT * FROM usuarios WHERE login='"+login+"'");
            if(rs1.next()) {
                //if(rs1.getString("password").equals(password)) {
                    usuario=this.construirUsuario(rs1);
                //} else {
                //    usuario=new Usuario();
                //    usuario.setId(0);
                //}
            }
        } finally {
            cn1.close();
        }
        return usuario;
    }
    
    private Usuario construirUsuario(ResultSet rs) throws SQLException {
        Usuario usuario=new Usuario();
        usuario.setId(rs.getInt("idUsuario"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setCorreo(rs.getString("email"));
        usuario.setIdRol(rs.getInt("rol"));
        return usuario;
    }
}
