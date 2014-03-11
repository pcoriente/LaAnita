package entradas.dao;

import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import impuestos.dominio.ImpuestosProducto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
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
 * @author jesc
 */
public class DAOMovimientos {
    int idUsuario;
    private DataSource ds = null;

    public DAOMovimientos() throws NamingException {
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
    
    public boolean grabarEntradaAlmacen(TOMovimiento m, ArrayList<MovimientoProducto> productos) throws SQLException {
        int capturados;
        boolean ok=false;
        int idLote;
        String strSQL;
        java.util.Date fechaCaducidad;
        fechaCaducidad = new java.util.Date();
        Format formatter=new SimpleDateFormat("yyyy-MM-dd");
        
        Connection cn=this.ds.getConnection();
        String strSQL1="UPDATE movimientosDetalle " +
                    "SET cantRecibida=? " +
                    "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque=?";
        PreparedStatement ps1=cn.prepareStatement(strSQL1);
        
        String strSQL3="INSERT INTO kardexAlmacen (idAlmacen, idMovto, idTipoMovto, idEmpaque, fecha, existenciaAnterior, cantidad) " +
                    "VALUES ("+m.getIdAlmacen()+", "+m.getIdMovto()+", 2, ?, GETDATE(), ?, ?)";
        PreparedStatement ps3=cn.prepareStatement(strSQL3);
        
        String strSQL4="INSERT INTO almacenesEmpaques (idAlmacen, idEmpaque, existencia, existenciaOficina, promedioPonderado, existenciaMinima, existenciaMaxima, idMovtoEntrada) " +
                    "VALUES ("+m.getIdAlmacen()+", ?, ?, 0, 0, 0, 0, 0)";
        PreparedStatement ps4=cn.prepareStatement(strSQL4);
        
        String strSQL5="UPDATE almacenesEmpaques " +
                    "SET existencia=existencia+? " +
                    "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque=?";
        PreparedStatement ps5=cn.prepareStatement(strSQL5);
        
        ResultSet rs;
        int idEmpaque, diasCaducidad;
        double existenciaAnterior;
        Statement st=cn.createStatement();
        try {
            strSQL="SELECT idLote FROM lotes WHERE fecha=CONVERT(date, GETDATE())";
            rs = st.executeQuery(strSQL);
            if(rs.next()) {
                idLote = rs.getInt("idLote");
            } else {
                idLote = 0;
            }
            String strSQL2="INSERT INTO almacenesLotes (idAlmacen, idEmpaque, idLote, fecha, fechaCaducidad, existencia, saldo) "
                    + "VALUES ("+m.getIdAlmacen()+", ?, "+idLote+", CONVERT(DATE, GETDATE()), DATEADD(day, ?, CONVERT(DATE, GETDATE())), ?, ?)";
            PreparedStatement ps2=cn.prepareStatement(strSQL2);
            
            capturados=0;
            st.executeUpdate("BEGIN TRANSACTION");
            
            for(MovimientoProducto p: productos) {
                idEmpaque=p.getEmpaque().getIdEmpaque();
//                diasCaducidad=p.getEmpaque().getProducto().getDiasCaducidad();
                diasCaducidad=365;
                
                if(p.getCantFacturada()> 0) {
                    capturados++;
                    
                    ps1.setDouble(1, p.getCantRecibida());
                    ps1.setInt(2, idEmpaque);
                    ps1.executeUpdate();
                    
                    strSQL="SELECT idAlmacen FROM almacenesLotes WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idEmpaque+" AND idLote="+idLote;
                    rs=st.executeQuery(strSQL);
                    if(rs.next()) {
                        strSQL="UPDATE existencia=existencia+"+p.getCantRecibida()+", saldo=saldo+"+p.getCantRecibida()+" " +
                               "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idEmpaque+" AND idLote="+idLote;
                        st.executeUpdate(strSQL);
                    } else {
                        ps2.setInt(1, idEmpaque);
                        ps2.setInt(2, diasCaducidad);
                        ps2.setDouble(3, p.getCantRecibida());
                        ps2.setDouble(4, p.getCantRecibida());
                        ps2.executeUpdate();
                    }
                    strSQL="SELECT existencia FROM almacenesEmpaques WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idEmpaque;
                    rs=st.executeQuery(strSQL);
                    if(rs.next()) {
                        existenciaAnterior=rs.getDouble("existencia");
                        
                        ps5.setDouble(1, p.getCantRecibida());
                        ps5.setInt(2, idEmpaque);
                        ps5.executeUpdate();
                    } else {
                        existenciaAnterior=0;
                        
                        ps4.setInt(1, idEmpaque);
                        ps4.setDouble(2, p.getCantRecibida());
                        ps4.executeUpdate();
                    }
                    ps3.setInt(1, idEmpaque);
                    ps3.setDouble(2, existenciaAnterior);
                    ps3.setDouble(3, p.getCantRecibida());
                    ps3.executeUpdate();
                }
            }
            if(capturados>0) {
                strSQL="UPDATE comprobantes SET cerradaAlmacen=1 WHERE idComprobante="+m.getIdReferencia();
                st.executeUpdate(strSQL);
            }
            st.executeUpdate("COMMIT TRANSACTION");
            ok=true;
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public boolean grabarEntradaOficina(TOMovimiento m, ArrayList<MovimientoProducto> productos) throws SQLException {
        int capturados;
        boolean ok=false;
        ArrayList<ImpuestosProducto> impuestos;
        
        Connection cn=this.ds.getConnection();
        String strSQL1="UPDATE movimientosDetalle " +
                    "SET costo=?, desctoProducto1=?, desctoProducto2=?, desctoConfidencial=?, unitario=?, cantFacturada=?, cantSinCargo=? " +
                    "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque=?";
        PreparedStatement ps1=cn.prepareStatement(strSQL1);
        
        String strSQL2="UPDATE movimientosDetalleImpuestos " +
                    "SET importe=? " +
                    "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque=?";
        PreparedStatement ps2=cn.prepareStatement(strSQL2);
        
        String strSQL3="INSERT INTO kardexOficina (idAlmacen, idMovto, idTipoMovto, idEmpaque, fecha, existenciaAnterior, cantidad) " +
                    "VALUES ("+m.getIdAlmacen()+", "+m.getIdMovto()+", 1, ?, GETDATE(), ?, ?)";
        PreparedStatement ps3=cn.prepareStatement(strSQL3);
        
        String strSQL4="INSERT INTO almacenesEmpaques (idAlmacen, idEmpaque, existencia, existenciaOficina, promedioPonderado, existenciaMinima, existenciaMaxima, idMovtoEntrada) " +
                    "VALUES (?, ?, 0, ?, ?, 0, 0, ?)";
        PreparedStatement ps4=cn.prepareStatement(strSQL4);
        
        String strSQL5="UPDATE almacenesEmpaques " +
                    "SET existenciaOficina=existenciaOficina+?, " +
                        "promedioPonderado=(existenciaOficina*promedioPonderado+?*?)/(existenciaOficina+?+?) " +
                    "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque=?";
        PreparedStatement ps5=cn.prepareStatement(strSQL5);
        
        ResultSet rs;
        int idEmpaque;
        double existenciaAnterior;
        Statement st=cn.createStatement();
        try {
            capturados=0;
            st.executeUpdate("BEGIN TRANSACTION");
            
            st.executeUpdate("UPDATE movimientos SET tipoCambio="+m.getTipoCambio()+" WHERE idMovto="+m.getIdMovto());
            
            //rs=st.executeQuery("select DATEPART(weekday, getdate()-1) AS DIA, DATEPART(week, GETDATE()) AS SEM, DATEPART(YEAR, GETDATE())%10 AS ANIO");
            //lote=""+rs.getInt("DIA")+String.format("%02d", rs.getInt("SEM"))+rs.getInt("ANIO")+"1";
            
            for(MovimientoProducto p: productos) {
                idEmpaque=p.getEmpaque().getIdEmpaque();
                
                if(p.getCantFacturada()> 0) {
                    capturados++;
                    
                    ps1.setDouble(1, p.getPrecio());
                    ps1.setDouble(2, p.getDesctoProducto1());
                    ps1.setDouble(3, p.getDesctoProducto2());
                    ps1.setDouble(4, p.getDesctoConfidencial());
                    ps1.setDouble(5, p.getUnitario());
                    ps1.setDouble(6, p.getCantFacturada());
                    ps1.setDouble(7, p.getCantSinCargo());
                    ps1.setInt(8, idEmpaque);
                    ps1.executeUpdate();

                    impuestos=p.getImpuestos();
                    for(ImpuestosProducto i:impuestos) {
                        ps2.setDouble(1, i.getImporte());
                        ps2.setInt(2, idEmpaque);
                        ps2.executeUpdate();
                    }
                    
                    rs=st.executeQuery("SELECT existenciaOficina " +
                                        "FROM almacenesEmpaques " +
                                        "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idEmpaque);
                    if(rs.next()) {
                        existenciaAnterior=rs.getDouble("existenciaOficina");
                        
                        ps5.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                        ps5.setDouble(2, p.getCantFacturada());
                        ps5.setDouble(3, p.getUnitario());
                        ps5.setDouble(4, p.getCantFacturada());
                        ps5.setDouble(5, p.getCantSinCargo());
                        ps5.setInt(6, idEmpaque);
                        ps5.executeUpdate();
                    } else {
                        existenciaAnterior=0;
                        
                        ps4.setInt(1, m.getIdAlmacen());
                        ps4.setInt(2, idEmpaque);
                        ps4.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                        ps4.setDouble(4, p.getUnitario()*p.getCantFacturada()/(p.getCantFacturada()+p.getCantSinCargo()));
                        ps4.setInt(5, m.getIdMovto());                 // El id del ultimo movimiento es el id de esta entrada
                        ps4.executeUpdate();
                    }
                    ps3.setInt(1, idEmpaque);
                    ps3.setDouble(2, existenciaAnterior);
                    ps3.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                    ps3.executeUpdate();
                }
            }
            if(capturados>0) {
                st.executeUpdate("UPDATE comprobantes SET cerradaOficina=1 WHERE idComprobante="+m.getIdReferencia());
            }
            st.executeUpdate("COMMIT TRANSACTION");
            ok=true;
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public int agregarEntrada(TOMovimiento m, ArrayList<MovimientoProducto> productos, int idOrdenCompra) throws SQLException, NamingException {
        int idMovto=0;
        int idEmpaque;
        int idImpuestoGrupo;
        
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, cantOrdenada, cantRecibida, idImpuestoGrupo, cantSinCargo, fecha) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, getdate())";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="INSERT INTO movimientos (idTipo, idAlmacen, idReferencia, idImpuestoZona, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, idUsuario, fecha) "
                    + "VALUES (1, "+m.getIdAlmacen()+", "+m.getIdReferencia()+", "+m.getIdImpuestoZona()+", "+m.getIdMoneda()+", "+m.getTipoCambio()+", "+m.getDesctoComercial()+", "+m.getDesctoProntoPago()+", "+this.idUsuario+", getdate())";
            st.executeUpdate(strSQL);
            
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO comprobantesOrdenesCompra (idComprobante, idOrdenCompra, idEntrada) " +
                   "VALUES ("+m.getIdReferencia()+", "+idOrdenCompra+", "+idMovto+")";
            st.executeUpdate(strSQL);
            
            for(MovimientoProducto p: productos) {
                ps.setInt(1, idMovto);
                ps.setInt(2, p.getEmpaque().getIdEmpaque());
                ps.setDouble(3, p.getPrecio());
                ps.setDouble(4, p.getDesctoProducto1());
                ps.setDouble(5, p.getDesctoProducto2());
                ps.setDouble(6, p.getDesctoConfidencial());
                ps.setDouble(7, p.getUnitario());
                ps.setDouble(8, p.getCantOrdenada());
                ps.setDouble(9, 0);
                ps.setInt(10, p.getEmpaque().getProducto().getImpuestoGrupo().getIdGrupo());
                ps.setDouble(11, p.getCantSinCargo());
                ps.executeUpdate();
                
                idEmpaque=p.getEmpaque().getIdEmpaque();
                idImpuestoGrupo=p.getEmpaque().getProducto().getImpuestoGrupo().getIdGrupo();
                this.agregarImpuestosProducto(cn, idImpuestoGrupo, m.getIdImpuestoZona(), idMovto, idEmpaque);
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
    
    public ArrayList<MovimientoProducto> obtenerDetalleMovimiento(int idMovto) throws SQLException, NamingException {
        ArrayList<MovimientoProducto> lstProductos=new ArrayList<MovimientoProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            MovimientoProducto prod;
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
    
    public MovimientoProducto construirProducto(ResultSet rs) throws SQLException {
        MovimientoProducto producto=new MovimientoProducto();
        producto.setEmpaque(new Empaque(rs.getInt("idEmpaque")));
        producto.setDesctoProducto1(rs.getDouble("desctoProducto1"));
        producto.setDesctoProducto2(rs.getDouble("desctoProducto2"));
        producto.setDesctoConfidencial(rs.getDouble("desctoConfidencial"));
        producto.setCantOrdenada(rs.getDouble("cantOrdenada"));
        producto.setCantFacturada(rs.getDouble("cantFacturada"));
        producto.setCantSinCargo(rs.getDouble("cantSinCargo"));
        producto.setCantRecibida(rs.getDouble("cantRecibida"));
        producto.setPrecio(rs.getDouble("costo"));
        return producto;
    }
    
    public TOMovimiento obtenerMovimiento(int idMovto) throws SQLException {
        TOMovimiento to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
             ResultSet rs=st.executeQuery("SELECT idMovto, idTipo, idAlmacen, idImpuestoZona, idReferencia, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, fecha, idUsuario "
                        + "FROM movimientos "
                        + "WHERE idMovto="+idMovto);
            if(rs.next()) {
                to=construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    private TOMovimiento construir(ResultSet rs) throws SQLException {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(rs.getInt("idMovto"));
        to.setIdTipo(rs.getInt("idTipo"));
        to.setIdAlmacen(rs.getInt("idAlmacen"));
        to.setIdImpuestoZona(rs.getInt("idImpuestoZona"));
        to.setIdReferencia(rs.getInt("idReferencia"));
        to.setIdMoneda(rs.getInt("idMoneda"));
        to.setTipoCambio(rs.getDouble("tipoCambio"));
        to.setDesctoComercial(rs.getDouble("desctoComercial"));
        to.setDesctoProntoPago(rs.getDouble("desctoprontoPago"));
        java.sql.Date f=rs.getDate("fecha");
        to.setFecha(new java.util.Date(f.getTime()));
//        to.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
        to.setIdUsuario(rs.getInt("idUsuario"));
        return to;
    }
    
    public int buscarEntrada(int idComprobante, int idOrdenCompra) throws SQLException {
        int idEntrada=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idEntrada FROM comprobantesOrdenesCompra " +
                                         "WHERE idComprobante="+idComprobante+" AND idOrdenCompra="+idOrdenCompra);
            if(rs.next()) {
                idEntrada=rs.getInt("idEntrada");
            }
        } finally {
            cn.close();
        }
        return idEntrada;
    }
}
