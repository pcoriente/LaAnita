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
import productos.dominio.UnidadEmpaque;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOUnidadesEmpaque {
    private String tabla="empaquesUnidades";
    private DataSource ds;
    
    public DAOUnidadesEmpaque() throws NamingException {
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
    
    public void modificar(UnidadEmpaque u) throws SQLException {
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
    
    public int agregar(UnidadEmpaque u) throws SQLException {
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
    
    public UnidadEmpaque obtenerUnidadEmpaque(int idUnidad) throws SQLException {
        UnidadEmpaque unidad=null;
        String strSQL="SELECT * FROM empaquesUnidades WHERE idUnidad="+idUnidad;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                unidad=new UnidadEmpaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviatura"));
            }
        } finally {
            cn.close();
        }
        return unidad;
    }
    
    public ArrayList<UnidadEmpaque> obtenerUnidadesEmpaque() throws SQLException {
        ArrayList<UnidadEmpaque> unidades=new ArrayList<UnidadEmpaque>();
        String strSQL="SELECT * FROM empaquesUnidades ORDER BY unidad";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                unidades.add(new UnidadEmpaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviatura")));
            }
        } finally {
            cn.close();
        }
        return unidades;
    }
}
