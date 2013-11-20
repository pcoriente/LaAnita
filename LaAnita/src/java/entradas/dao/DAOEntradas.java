package entradas.dao;

import entradas.dominio.Entrada;
import entradas.dominio.EntradaProducto;
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
import productos.dominio.Empaque;
import usuarios.UsuarioSesion;

/**
 *
 * @author jsolis
 */
public class DAOEntradas {
    int idUsuario;
    private DataSource ds = null;

    public DAOEntradas() throws NamingException {
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
    
    public ArrayList<EntradaProducto> obtenerDetalleEntrada(int idMovto) throws SQLException {
        ArrayList<EntradaProducto> lstProductos=new ArrayList<EntradaProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                lstProductos.add(construirProducto(rs));
            }
        } finally {
            cn.close();
        }
        return lstProductos;
    }
    
    public int existeMovimiento(int idAlmacen, int idFactura, int idOrdenCompra) throws SQLException {
        int idMovto=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idMovto "
                    + "FROM movimientos "
                    + "WHERE idTipo=1 AND idAlmacen="+idAlmacen+" AND idFactura="+idFactura+" AND idOrdenCompra="+idOrdenCompra);
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
    public Entrada obtenerMovimiento(int idMovto) throws SQLException {
        Entrada entrada=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
             ResultSet rs=st.executeQuery("SELECT idMovto, idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, fecha, idUsuario "
                        + "FROM movimientos "
                        + "WHERE idMovto="+idMovto);
            if(rs.next()) {
                entrada=construir(rs);
            }
        } finally {
            cn.close();
        }
        return entrada;
    }
    
    public int agregarMovimiento(int idAlmacen, int idFactura, int idOrdenCompra) throws SQLException {
        int idMovto=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            st.executeUpdate("INSERT INTO movimientos (idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, idUsuario) "
                    + "VALUES (1, "+idAlmacen+", "+idFactura+", "+idOrdenCompra+", 0.00, 0.00, "+this.idUsuario+")");
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
    public Entrada modificarEntrada(int idAlmacen, int idFactura, int idOrdenCompra) throws SQLException {
        Entrada entrada=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            ResultSet rs=st.executeQuery("SELECT idMovto, idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, fecha, idUsuario "
                        + "FROM movimientos "
                        + "WHERE idTipo=1 AND idAlmacen="+idAlmacen+" AND idFactura="+idFactura+" AND idOrdenCompra="+idOrdenCompra);
            if(rs.next()) {
                entrada=construir(rs);
            } else {
                st.executeUpdate("INSERT INTO movimientos (idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, idUsuario) "
                        + "VALUES (1, "+idAlmacen+", "+idFactura+", "+idOrdenCompra+", 0.00, 0.00, "+this.idUsuario+")");
                rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
                if(rs.next()) {
                    entrada=new Entrada();
                    entrada.setIdEntrada(rs.getInt("idMovto"));
                    entrada.setDesctoComercial(0.00);
                    entrada.setDesctoProntoPago(0.00);
                    entrada.setIdUsuario(this.idUsuario);
                }
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return entrada;
    }
    
    private Entrada construir(ResultSet rs) throws SQLException {
        Entrada entrada=new Entrada();
        entrada.setIdEntrada(rs.getInt("idMovto"));
        entrada.setDesctoComercial(rs.getDouble("desctoComercial"));
        entrada.setDesctoProntoPago(rs.getDouble("desctoprontoPago"));
        entrada.setIdUsuario(rs.getInt("idUsuario"));
        return entrada;
    }
    /*
    public int agregarEntrada(int idProveedor, int idEmpresa, int idAlmacen, int idFactura) {
        int idEntrada=0;
        return idEntrada;
    }
    */
    public EntradaProducto obtenerProducto(int idEntrada, int idEmpaque) throws SQLException {
        EntradaProducto producto=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM almacenes WHERE idEntrada="+idEntrada+" and idEmpaque="+idEmpaque);
            if(rs.next()) {
                producto=construirProducto(rs);
            }
        } finally {
            cn.close();
        }
        return producto;
    }
    
    public EntradaProducto construirProducto(ResultSet rs) throws SQLException {
        EntradaProducto producto=new EntradaProducto();
        producto.setEmpaque(new Empaque(rs.getInt("idEmpaque")));
        producto.setDesctoProducto1(rs.getDouble("desctoProducto1"));
        producto.setDesctoProducto2(rs.getDouble("desctoProducto2"));
        producto.setDesctoConfidencial(rs.getDouble("desctoConfidencial"));
        producto.setCantOrdenada(rs.getDouble("cantOrdenada"));
        producto.setCantRecibida(rs.getDouble("cantRecibida"));
        producto.setPrecio(rs.getDouble("precio"));
        return producto;
    }
}
