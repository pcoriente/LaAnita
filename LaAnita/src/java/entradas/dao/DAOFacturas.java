package entradas.dao;

import entradas.dominio.Factura;
import java.sql.Connection;
import java.sql.Date;
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
 * @author jsolis
 */
public class DAOFacturas {
    int idUsuario;
    private DataSource ds = null;

    public DAOFacturas() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            this.idUsuario=usuarioSesion.getUsuario().getId();

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }
    
    public boolean obtenerEstado(int idFactura) throws SQLException {
        boolean cerrada=false;   // 0.-Abierta; 1.-Cerrada
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT cerrada FROM facturas WHERE idFactura="+idFactura);
            if(rs.next()) {
                cerrada=rs.getBoolean("cerrada");
            }
        } finally {
            cn.close();
        }
        return cerrada;
    }
    
    public int agregarFactura(Factura factura) throws SQLException {
        int idFactura=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            Date fechaFactura=new java.sql.Date(factura.getFecha().getTime());
            st.executeUpdate("INSERT INTO facturas (idProveedor, serie, numero, fecha, idUsuario) "
                            + "VALUES ("+factura.getIdProveedor()+", '"+factura.getSerie()+"', '"+factura.getNumero()+"', '"+fechaFactura.toString()+"', "+this.idUsuario+")");
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idFactura");
            if(rs.next()) {
                idFactura=rs.getInt("idFactura");
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return idFactura;
    }
    
    public Factura obtenerFactura(int idProveedor, String serie, String numero) throws SQLException {
        Factura f=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM facturas "
                    + "WHERE idProveedor="+idProveedor+" AND serie='"+serie+"' AND numero='"+numero+"'");
            if(rs.next()) {
                f=construir(rs);
            }
        } finally {
            cn.close();
        }
        return f;
    }
    
    public Factura obtenerFactura(int idFactura) throws SQLException {
        Factura f=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM facturas "
                    + "WHERE idFactura="+idFactura);
            if(rs.next()) {
                f=construir(rs);
            }
        } finally {
            cn.close();
        }
        return f;
    }
    
    public ArrayList<Factura> obtenerFacturas(int idProveedor) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        ArrayList<Factura> facturas=new ArrayList<Factura>();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM facturas "
                    + "WHERE idProveedor="+idProveedor+" ORDER BY FECHA DESC");
            while(rs.next()) {
                facturas.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return facturas;
    }
    
    private Factura construir(ResultSet rs) throws SQLException {
        Factura f=new Factura();
        f.setIdFactura(rs.getInt("idFactura"));
        f.setIdProveedor(rs.getInt("idProveedor"));
        f.setSerie(rs.getString("serie"));
        f.setNumero(rs.getString("numero"));
        f.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
        return f;
    }
}
