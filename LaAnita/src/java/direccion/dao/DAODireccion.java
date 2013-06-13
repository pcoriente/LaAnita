package direccion.dao;

import direccion.to.TODireccion;
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
/**
 *
 * @author Julio
 */
public class DAODireccion {
    private DataSource ds;
    
    public DAODireccion() throws NamingException {
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
    
    public int agregar(String calle, String numeroExterior, String numeroInterior, String referencia, int idPais, String codigoPostal, String estado, String municipio, String localidad, String colonia, String numeroLocalizacion) throws SQLException {
        int idDireccion=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            // Hay que agregar todos los campos, ya que ninguno acepta nulos
            cn.setAutoCommit(false);
            st.executeUpdate("INSERT INTO direcciones (calle, numeroExterior, numeroInterior, referencia, idPais, codigoPostal, estado, municipio, localidad, colonia, numeroLocalizacion) "
                    + "VALUES('"+calle+"', '"+numeroExterior+"', '"+numeroInterior+"', '"+referencia+"', "+idPais+", '"+codigoPostal+"', '"+estado+"', '"+municipio+"', '"+localidad+"', '"+colonia+"', '"+numeroLocalizacion+"')");
            ResultSet rs=st.executeQuery("SELECT MAX(idDireccion) as idDireccion FROM direcciones");
            if(rs.next()) idDireccion=rs.getInt("idDireccion");
          //  cn.commit();
        } catch (SQLException ex) {
            cn.rollback();
            throw(ex);
        } finally {
            cn.close();
        }
        return idDireccion;
    }
    
    public void modificar(int idDireccion, String calle, String numeroExterior, String numeroInterior, String referencia, int idPais, String codigoPostal, String estado, String municipio, String localidad, String colonia, String numeroLocalizacion) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("UPDATE direcciones "
                    + "SET calle='"+calle+"', numeroExterior='"+numeroExterior+"', numeroInterior='"+numeroInterior+"', referencia='"+referencia+"', idPais="+idPais+", codigoPostal='"+codigoPostal+"', estado='"+estado+"', municipio='"+municipio+"', localidad='"+localidad+"', colonia='"+colonia+"', numeroLocalizacion='"+numeroLocalizacion+"' "
                    + "WHERE idDireccion="+idDireccion);
        } finally {
            cn.close();
        }
    }
    
    public void eliminar(int idDireccion) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        st.executeUpdate("DELETE FROM direcciones WHERE idDireccion="+idDireccion);
        cn.close();
    }
    
    public TODireccion obtener(int idDireccion) throws SQLException {
        TODireccion toDir=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM direcciones WHERE idDireccion="+idDireccion);
            if(rs.next()) toDir=construir(rs);
        } finally {
            cn.close();
        }
        return toDir;
    }
    
    private TODireccion construir(ResultSet rs) throws SQLException {
        TODireccion toDir=new TODireccion();
        toDir.setIdDireccion(rs.getInt("idDireccion"));
        toDir.setCalle(rs.getString("calle"));
        toDir.setNumeroExterior(rs.getString("numeroExterior"));
        toDir.setNumeroInterior(rs.getString("numeroInterior"));
        toDir.setReferencia(rs.getString("referencia"));
        toDir.setIdPais(rs.getInt("idPais"));
        toDir.setCodigoPostal(rs.getString("codigoPostal"));
        toDir.setEstado(rs.getString("estado"));
        toDir.setMunicipio(rs.getString("municipio"));
        toDir.setLocalidad(rs.getString("localidad"));
        toDir.setColonia(rs.getString("colonia"));
        toDir.setNumeroLocalizacion(rs.getString("numeroLocalizacion"));
        return toDir;
    }
}
