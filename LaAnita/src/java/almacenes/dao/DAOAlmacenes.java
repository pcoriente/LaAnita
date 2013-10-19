/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package almacenes.dao;
import almacenes.to.TOAlmacen;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import usuarios.UsuarioSesion;

/**
 *
 * @author carlosp
 */
public class DAOAlmacenes {

    private DataSource ds = null;

    public DAOAlmacenes() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }
    public TOAlmacen obtenerUnAlmacen(int idAlmacen) throws SQLException {
        TOAlmacen to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM almacenes WHERE idAlmacen="+idAlmacen);
            if(rs.next()) to=construir(rs);
        } finally {
            cn.close();
        }
        return to;
    }

    public ArrayList<TOAlmacen> obtenerAlmacenes() throws SQLException {
        ArrayList<TOAlmacen> lista = new ArrayList<TOAlmacen>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "SELECT * FROM almacenes";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private TOAlmacen construir(ResultSet rs) throws SQLException {
        TOAlmacen to = new TOAlmacen();
        to.setIdAlmacen(rs.getInt("idAlmacen"));
        to.setAlmacen(rs.getString("almacen"));
        to.setIdCedis(rs.getInt("idCedis"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setIdDireccion(rs.getInt("idDireccion"));
        to.setEncargado(rs.getString("encargado"));
        return to;
    }
}
