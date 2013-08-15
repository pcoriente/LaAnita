/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.dao;

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
import usuarios.dominio.Usuario;

/**
 *
 * @author daap
 */
public class DAOUsuarioRequisiciones {
    private final DataSource ds;

    public DAOUsuarioRequisiciones() throws NamingException {
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
    
        private Usuario convertirAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("idUsuario"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setCorreo(rs.getString("email"));
        return usuario;
    }

    public ArrayList<Usuario> obtenerUsuarios() throws SQLException {
         ArrayList<Usuario> usu = new ArrayList<Usuario>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM usuarios");
            while (rs.next()) {
                usu.add(convertirAUsuario(rs));
            }
        } finally {
            cn.close();
        }
        return usu;
    }
    
    public ArrayList<Usuario> obtenerSubUsuario(int idDepto) throws SQLException {
        ArrayList<Usuario> su=new ArrayList<Usuario>();
        String strSQL=""
                + "SELECT sg.idUsuario, sg.usuario "
                + "FROM usuarios sg "
                + "WHERE sg.idDepto="+idDepto+" "
                + "ORDER BY sg.usuario";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                su.add(new Usuario(rs.getInt("idUsuario"), rs.getString("usuario")));
            }
        } finally {
            cn.close();
        }
        return su;
    }
    
    public Usuario obtenerUsuarioConverter(int idUsuario) throws SQLException {
        Usuario usu=null;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM Usuarios WHERE idUsuario="+idUsuario);
            if(rs.next()) {
                usu=construir(rs);
            }
        } finally {
            cn.close();
        }
        return usu;
    }
    
    private Usuario construir(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("idUsuario"), rs.getString("usuario"));
    }
    
}
