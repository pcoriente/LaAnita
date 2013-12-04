package entradas.dao;

import entradas.dominio.Entrada;
import entradas.dominio.EntradaProducto;
import impuestos.dao.DAOImpuestosProducto;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
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
    /*
    public void agregarMovimientoDetalle(int idMovto, ArrayList<EntradaProducto> productos) throws SQLException {
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, desctoProducto1, desctoProducto2, desctoConfidencial, costo, cantOrdenada, cantRecibida, idImpuestoGrupo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            for(EntradaProducto p: productos) {
                ps.setInt(1, idMovto);
                ps.setInt(2, p.getEmpaque().getIdEmpaque());
                ps.setDouble(3, p.getDesctoProducto1());
                ps.setDouble(4, p.getDesctoProducto2());
                ps.setDouble(5, p.getDesctoConfidencial());
                ps.setDouble(6, p.getPrecio());
                ps.setDouble(7, p.getCantOrdenada());
                ps.setDouble(8, p.getCantRecibida());
                ps.setInt(9, p.getEmpaque().getProducto().getImpuesto().getIdGrupo());
                ps.executeUpdate();
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            ps.close();
            cn.close();
        }
    }
    * */
    
    public int agregarEntrada(Entrada entrada, ArrayList<EntradaProducto> productos, int idImpuestoZona) throws SQLException, NamingException {
        int idMovto=0;
        int idEmpaque;
//        double unitario;
        int idImpuestoGrupo;
//        DAOImpuestosProducto daoImps=new DAOImpuestosProducto();
        
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, cantOrdenada, cantRecibida, idImpuestoGrupo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="INSERT INTO movimientos (idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, idUsuario) "
                    + "VALUES (1, "+entrada.getIdAlmacen()+", "+entrada.getIdFactura()+", "+entrada.getIdOrdenCompra()+", "+entrada.getDesctoComercial()+", "+entrada.getDesctoProntoPago()+", "+this.idUsuario+")";
            st.executeUpdate(strSQL);
            
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO facturasOrdenesCompra (idFactura, idOrdenCompra, idEntrada) " +
                   "VALUES ("+entrada.getIdFactura()+", "+entrada.getIdOrdenCompra()+", "+idMovto+")";
            st.executeUpdate(strSQL);
            
            for(EntradaProducto p: productos) {
                ps.setInt(1, idMovto);
                ps.setInt(2, p.getEmpaque().getIdEmpaque());
                ps.setDouble(3, p.getPrecio());
                ps.setDouble(4, p.getDesctoProducto1());
                ps.setDouble(5, p.getDesctoProducto2());
                ps.setDouble(6, p.getDesctoConfidencial());
//                unitario=p.getPrecio();
//                unitario*=(1-entrada.getDesctoComercial()/100.00);
//                unitario*=(1-entrada.getDesctoProntoPago()/100.00);
//                unitario*=(1-p.getDesctoProducto1()/100.00);
//                unitario*=(1-p.getDesctoProducto2()/100.00);
//                unitario*=(1-p.getDesctoConfidencial()/100.00);
//                unitario=Math.round(unitario*100.00)/100.00;
//                ps.setDouble(7, unitario);
                ps.setDouble(7, p.getUnitario());
                ps.setDouble(8, p.getCantOrdenada());
                ps.setDouble(9, 0);
                ps.setInt(10, p.getEmpaque().getProducto().getImpuestoGrupo().getIdGrupo());
                ps.executeUpdate();
                
                idEmpaque=p.getEmpaque().getIdEmpaque();
                idImpuestoGrupo=p.getEmpaque().getProducto().getImpuestoGrupo().getIdGrupo();
                this.agregarImpuestosProducto(cn, idImpuestoGrupo, idImpuestoZona, idMovto, idEmpaque);
                this.calculaImpuestosProducto(cn, idMovto, idEmpaque, p.getUnitario(), p.getEmpaque().getPiezas());
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
    
    private void calculaImpuestosProducto(Connection cn, int idMovto, int idEmpaque, double unitario, double piezas) throws SQLException {
        Statement st=cn.createStatement();
        String strSQL="UPDATE movimientosDetalleImpuestos " +
                        "SET importe=CASE WHEN aplicable=0 THEN 0 " +
                                        "WHEN modo=1 THEN " + unitario + "*valor/100.00 " +
                                        "ELSE "+piezas+"*valor END " +
                        "WHERE idMovto="+idMovto+" AND idEmpaque="+idEmpaque;
        st.executeUpdate(strSQL);
    }
    
    private void agregarImpuestosProducto(Connection cn, int idImpuestoGrupo, int idZona, int idMovto, int idEmpaque) throws SQLException {
        Statement st=cn.createStatement();
        String strSQL="insert into movimientosDetalleImpuestos (idMovto, idEmpaque, idImpuesto, impuesto, valor, aplicable, modo, acreditable, importe) " +
                        "select "+idMovto+", "+idEmpaque+", id.idImpuesto, i.impuesto, id.valor, i.aplicable, i.modo, i.acreditable, 0.00 as importe " +
                        "from impuestosDetalle id " +
                        "inner join impuestos i on i.idImpuesto=id.idImpuesto " +
                        "where id.idGrupo="+idImpuestoGrupo+" and id.idZona="+idZona+" and GETDATE() between fechaInicial and fechaFinal";
        st.executeUpdate(strSQL);
    }
    
    public int buscarEntrada(int idFactura, int idOrdenCompra) throws SQLException {
        int idEntrada=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idEntrada FROM facturasOrdenesCompra " +
                                         "WHERE idFactura="+idFactura+" AND idOrdenCompra="+idOrdenCompra);
            if(rs.next()) {
                idEntrada=rs.getInt("idEntrada");
            }
        } finally {
            cn.close();
        }
        return idEntrada;
    }
    
    public ArrayList<EntradaProducto> obtenerDetalleEntrada(int idMovto) throws SQLException, NamingException {
        ArrayList<EntradaProducto> lstProductos=new ArrayList<EntradaProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            EntradaProducto prod;
            ResultSet rs=st.executeQuery("SELECT * FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                prod=construirProducto(rs);
                lstProductos.add(prod);
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
    
    public ArrayList<Entrada> obtenerEntradas(int idFactura) throws SQLException {
        ArrayList<Entrada> entradas=new ArrayList<Entrada>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idMovto, idTipo, idAlmacen, idFactura, idOrdenCompra, desctoComercial, desctoProntoPago, fecha, idUsuario "
                    + "FROM movimientos "
                    + "WHERE idTipo=1 AND idFactura="+idFactura);
            while(rs.next()) {
                entradas.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return entradas;
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
        Date fechaEntrada=rs.getDate("fecha");
        entrada.setFecha(new java.util.Date(fechaEntrada.getTime()));
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
        producto.setPrecio(rs.getDouble("costo"));
        return producto;
    }
}
