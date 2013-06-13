package productos.dao;

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
import productos.dominio.Unidad;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOUnidades {
    private DataSource ds;
    private String tabla="productosUnidades";
    
    public DAOUnidades() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
    public void eliminar(int idUnidad) throws SQLException {
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("DELETE FROM "+this.tabla+" WHERE idUnidad="+idUnidad);
        } finally {
            cn.close();
        }
    }
    
    public void modificar(Unidad u) throws SQLException {
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("UPDATE "+this.tabla+" "
                    + "SET unidad='"+u.getUnidad()+"', abreviatura='"+u.getAbreviatura()+"' "
                    + "WHERE idUnidad="+u.getIdUnidad());
        } finally {
            cn.close();
        }
    }
    
    public int agregar(Unidad u) throws SQLException {
        int idUnidad=0;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("INSERT INTO "+this.tabla+" (unidad, abreviatura) "
                    + "VALUES('"+u.getUnidad()+"', '"+u.getAbreviatura()+"')");
            ResultSet rs=st.executeQuery("SELECT max(idUnidad) AS idUnidad FROM "+this.tabla);
            if(rs.next()) {
                idUnidad=rs.getInt("idUnidad");
            }
            st.executeUpdate("commit Transaction");
        } catch(SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        return idUnidad;
    }
    
    public ArrayList<Unidad> obtenerUnidades() throws SQLException {
        ArrayList<Unidad> unidades=new ArrayList<Unidad>();
        String strSQL="SELECT * FROM "+this.tabla+" ORDER BY unidad";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                unidades.add(new Unidad(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviatura")));
            }
        } finally {
            cn.close();
        }
        return unidades;
    }
    
    public Unidad obtenerUnidad(int idUnidad) throws SQLException {
        Unidad unidad=null;
        String strSQL="SELECT * FROM "+this.tabla+" WHERE idUnidad="+idUnidad;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                unidad=new Unidad(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviatura"));
            }
        } finally {
            cn.close();
        }
        return unidad;
    }
}
