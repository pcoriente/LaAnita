package proveedores.dao;

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
import proveedores.to.TOProveedor;
import usuarios.UsuarioSesion;

/**
 *
 * @author Julio
 */
public class DAOProveedores {
    private DataSource ds;
    
    public DAOProveedores() throws NamingException {
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
    
    public int agregar(int codigoProveedor, String proveedor, String rfc, String telefono, String fax, String correo, int diasCredito, double limiteCredito, int idDireccion) throws SQLException {
        int idProveedor=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            cn.setAutoCommit(false);
            st.executeUpdate("INSERT INTO proveedores (codigoProveedor, proveedor, rfc, telefono, fax, eMail, diasCredito, limiteCredito, idDireccion, fechaAlta) "
                    + "VALUES("+codigoProveedor+", '"+proveedor+"', '"+rfc+"', '"+telefono+"', '"+fax+"', '"+correo+"', "+diasCredito+", "+limiteCredito+", "+idDireccion+", CURRENT_TIMESTAMP)");
            ResultSet rs=st.executeQuery("SELECT MAX(idProveedor) AS idProveedor FROM proveedores");
            if(rs.next()) {
                idProveedor=rs.getInt("idProveedor");
            }
          //  cn.commit();
        } catch(SQLException ex) {
            cn.rollback();
            throw(ex);
        } finally {
            cn.close();
        }
        return idProveedor;
    }
    
    public void modificar(int idProveedor, String proveedor, String rfc, String telefono, String fax, String correo, int diasCredito, double limiteCredito, int idDireccion) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        st.executeUpdate("UPDATE proveedores SET proveedor='"+proveedor+"', rfc='"+rfc+"', telefono='"+telefono+"', fax='"+fax+"', eMail='"+correo+"', diasCredito="+diasCredito+", limiteCredito="+limiteCredito+", idDireccion="+idDireccion+" "
                + "WHERE idProveedor="+idProveedor);
        cn.close();
    }
    
    public TOProveedor obtenerUnProveedor(int idProveedor) throws SQLException {
        TOProveedor to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM proveedores WHERE idProveedor="+idProveedor);
            if(rs.next()) to=construir(rs);
        } finally {
            cn.close();
        }
        return to;
    }
    
    public ArrayList<TOProveedor> obtenerProveedores() throws SQLException {
        ArrayList<TOProveedor> lista=new ArrayList<TOProveedor>();
        ResultSet rs=null;
        
        Connection cn=ds.getConnection();
        String strSQL="SELECT * FROM proveedores ORDER BY proveedor";
        try {
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(strSQL);
            while(rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    public int ultimoProveedor() throws SQLException {
        int ultimo=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT MAX(codigoProveedor) as ultimo FROM proveedores");
            if(rs.next()) ultimo=rs.getInt("ultimo");
        } finally {
            cn.close();
        }
        return ultimo;
    }
    
    private TOProveedor construir(ResultSet rs) throws SQLException {
        TOProveedor to=new TOProveedor();
        to.setIdProveedor(rs.getInt("idProveedor"));
        to.setCodigoProveedor(rs.getInt("codigoProveedor"));
        to.setProveedor(rs.getString("proveedor"));
        to.setRfc(rs.getString("rfc"));
        to.setTelefono(rs.getString("telefono"));
        to.setFax(rs.getString("fax"));
        to.setCorreo(rs.getString("eMail"));
        to.setDiasCredito(rs.getInt("diasCredito"));
        to.setLimiteCredito(rs.getDouble("limiteCredito"));
        to.setFechaAlta(rs.getDate("fechaAlta"));
        to.setIdDireccion(rs.getInt("idDireccion"));
        return to;
    }
}
