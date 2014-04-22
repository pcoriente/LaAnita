package entradas.dao;

import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
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
import producto2.dominio.Producto;
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
    
    public boolean cancelarEntrada(int idMovto) throws SQLException {
        ResultSet rs;
        ResultSet rs1;
        String strSQL;
        int idEmpresa, idAlmacen, idEmpaque;
        double existenciaAnterior, cantidad, cantFacturada, unitario;
        
        boolean ok=false;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        PreparedStatement ps;
        PreparedStatement ps1;
        PreparedStatement ps2;
        PreparedStatement ps3;
        
        idAlmacen=0;
        idEmpresa=0;
        rs=st.executeQuery("SELECT idEmpresa, idAlmacen FROM movimientos WHERE idMovto="+idMovto);
        if(rs.next()) {
            idEmpresa=rs.getInt("idEmpresa");
            idAlmacen=rs.getInt("idAlmacen");
        }
        strSQL="SELECT existenciaOficina FROM almacenesEmpaques WHERE idAlmacen="+idAlmacen+" AND idEmpaque=?";
        ps=cn.prepareStatement(strSQL);
        
        strSQL="INSERT INTO kardexOficina (idAlmacen, idEmpaque, fecha, idTipoMovto, idMovto, existenciaAnterior, cantidad) "
                + "VALUES ("+idAlmacen+", ?, GETDATE(), 7, "+idMovto+", ?, ?)";
        ps1=cn.prepareStatement(strSQL);

        strSQL="UPDATE almacenesEmpaques SET existenciaOficina=existenciaOficina-? "
                + "WHERE idAlmacen="+idAlmacen+" AND idEmpaque=?";
        ps2=cn.prepareStatement(strSQL);

        strSQL="UPDATE empresasEmpaques " +
                "SET existenciaOficina=existenciaOficina-?, " +
                    "promedioPonderado=(existenciaOficina*promedioPonderado-?*?)/(existenciaOficina+?) " +
                "WHERE idEmpresa="+idEmpresa+" AND idEmpaque=?";
        ps3=cn.prepareStatement(strSQL);
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            st.executeUpdate("UPDATE movimientos SET status=3 WHERE idMovto="+idMovto);
            
            rs=st.executeQuery("SELECT idEmpaque, cantFacturada, cantSinCargo, unitario "
                            + "FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                idEmpaque=rs.getInt("idEmpaque");
                cantFacturada=rs.getDouble("cantFacturada");
                cantidad=rs.getDouble("cantFacturada")+rs.getDouble("cantSinCargo");
                unitario=rs.getDouble("unitario");
                
                
                
                ps.setInt(1, idEmpaque);
                rs1=ps.executeQuery();
                //rs1=st.executeQuery(strSQL);
                existenciaAnterior=0;
                if(rs1.next()) {
                    existenciaAnterior=rs1.getDouble("existenciaOficina");
                }
                ps1.setInt(1, idEmpaque);
                ps1.setDouble(2, existenciaAnterior);
                ps1.setDouble(3, cantidad);
                ps1.execute();
                
                ps2.setDouble(1, cantidad);
                ps2.setInt(2, idEmpaque);
                ps2.execute();
                
                ps3.setDouble(1, cantidad);
                ps3.setDouble(2, cantFacturada);
                ps3.setDouble(3, unitario);
                ps3.setDouble(4, cantidad);
                ps3.setInt(5, idEmpaque);
                ps3.executeUpdate();
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public double obtenerPrecioUltimaCompra(int idEmpresa, int idEmpaque) throws SQLException {
        double precioLista=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        
        String strSQL="SELECT idMovtoEntrada FROM empresasEmpaques "
                + "WHERE idEmpresa="+idEmpresa+" AND idEmpaque="+idEmpaque;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                int idMovto=rs.getInt("idMovtoEntrada");
                
                strSQL="SELECT costo FROM movimientosDetalle "
                        + "WHERE idMovto="+idMovto+" AND idEmpaque="+idEmpaque;
                rs=st.executeQuery(strSQL);
                if(rs.next()) {
                    precioLista=rs.getDouble("costo");
                }
            }
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return precioLista;
    }
    
    public boolean grabarSolicitudTraspaso(int idAlmacenOrigen, TOMovimiento solicitud, ArrayList<MovimientoProducto> productos) throws SQLException {
        boolean ok=true;
        String strSQL;
        ResultSet rs;
        int folio;
        int idReferencia;
        int idMovto;
        
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=0;
            strSQL="SELECT folio FROM movimientosTipos WHERE idTipo=2";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folio=rs.getInt("folio");
            }
            strSQL="UPDATE movimientosTipos SET folio=folio+1 WHERE idTipo=2";
            st.executeUpdate(strSQL);
            
            strSQL="INSERT INTO comprobantes (idAlmacen, idProveedor, tipoComprobante, serie, numero, idUsuario, fecha, cerradaOficina, cerradaAlmacen) " +
                    "VALUES ("+idAlmacenOrigen+", 0, 2, '', '"+folio+"', "+this.idUsuario+", GETDATE(), 0, 0)";
            st.executeUpdate(strSQL);
            
            idReferencia=0;
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idReferencia=rs.getInt("idMovto");
            }
            idMovto=0;
            strSQL="INSERT INTO movimientos (idTipo, idCedis, idEmpresa, idAlmacen, idReferencia, idImpuestoZona, desctoComercial, desctoProntoPago, fecha, idUsuario, idMoneda, tipoCambio, status) " +
                    "VALUES (2, "+solicitud.getIdCedis()+", "+solicitud.getIdEmpresa()+", "+solicitud.getIdAlmacen()+", "+idReferencia+", 1, 0, 0, GETDATE(), "+this.idUsuario+", 1, 1, 1)";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha) " +
                    "VALUES ("+idMovto+", ?, ?, 0, 0, 0, 0, 0, 0, 0, 0, ?, GETDATE())";
            PreparedStatement ps=cn.prepareStatement(strSQL);
            for(MovimientoProducto p: productos) {
                ps.setInt(1, p.getProducto().getIdProducto());
                ps.setDouble(2, p.getCantOrdenada());
                ps.setInt(3, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo());
                ps.executeUpdate();
                
                // this.agregarImpuestosProducto(cn, idImpuestoGrupo, m.getIdImpuestoZona(), idMovto, idEmpaque);
                this.agregarImpuestosProducto(cn, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo(), 1, idMovto, p.getProducto().getIdProducto());
            }
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
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
                idEmpaque=p.getProducto().getIdProducto();
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
        boolean nueva;
        ArrayList<ImpuestosProducto> impuestos;
        
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantFacturada, cantSinCargo, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, cantOrdenada, cantRecibida, idImpuestoGrupo, fecha) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, getdate())";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        
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
        
        String strSQL4="INSERT INTO almacenesEmpaques (idAlmacen, idEmpaque, existenciaAlmacen, existenciaOficina, existenciaMinima, existenciaMaxima) " +
                    "VALUES (?, ?, 0, ?, 0, 0)";
        PreparedStatement ps4=cn.prepareStatement(strSQL4);
        
        String strSQL5="UPDATE almacenesEmpaques "
                    + "SET existenciaOficina=existenciaOficina+? "
                    + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque=?";
        PreparedStatement ps5=cn.prepareStatement(strSQL5);
        
        String strSQL6="INSERT INTO empresasEmpaques (idEmpresa, idEmpaque, promedioPonderado, existenciaOficina, idMovtoEntrada) "
                    + "VALUES ("+m.getIdEmpresa()+", ?, ?, ?, ?)";
        PreparedStatement ps6=cn.prepareStatement(strSQL6);
        
        String strSQL7="UPDATE empresasEmpaques " +
                    "SET existenciaOficina=existenciaOficina+?" +
                        ", promedioPonderado=(existenciaOficina*promedioPonderado+?*?)/(existenciaOficina+?+?)" +
                        ", idMovtoEntrada=? "+
                    "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque=?";
        PreparedStatement ps7=cn.prepareStatement(strSQL7);
        
        ResultSet rs;
        int idEmpaque, folio, idImpuestoGrupo;
        double existenciaAnterior;
        Statement st=cn.createStatement();
        try {
            capturados=0;
            st.executeUpdate("BEGIN TRANSACTION");
            
            if(m.getIdMovto()==0) {
                folio=0;
                nueva=true;
                rs=st.executeQuery("SELECT folio FROM movimientosTipos WHERE idTipo=1");
                if(rs.next()) {
                    folio=rs.getInt("folio");
                    st.executeUpdate("UPDATE movimientosTipos SET folio=folio+1 WHERE idTipo=1");
                }
                strSQL="INSERT INTO movimientos (idTipo, idCedis, folio, idEmpresa, idAlmacen, idReferencia, idImpuestoZona, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, idUsuario, fecha, status) "
                        + "VALUES (1, "+m.getIdCedis()+", "+folio+", "+m.getIdEmpresa()+", "+m.getIdAlmacen()+", "+m.getIdReferencia()+", "+m.getIdImpuestoZona()+", "+m.getIdMoneda()+", "+m.getTipoCambio()+", "+m.getDesctoComercial()+", "+m.getDesctoProntoPago()+", "+this.idUsuario+", getdate(), 1)";
                st.executeUpdate(strSQL);

                rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
                if(rs.next()) {
                    m.setIdMovto(rs.getInt("idMovto"));
                }
            } else {
                nueva=false;
                st.executeUpdate("UPDATE movimientos "
                        + "SET idMoneda="+m.getIdMoneda()+", tipoCambio="+m.getTipoCambio()+" "
                                + ", desctoComercial="+m.getDesctoComercial()+", desctoProntoPago="+m.getDesctoProntoPago()+" "
                                + ", fecha=GETDATE(), status=1 "
                        + "WHERE idMovto="+m.getIdMovto());
            }
            
            //rs=st.executeQuery("select DATEPART(weekday, getdate()-1) AS DIA, DATEPART(week, GETDATE()) AS SEM, DATEPART(YEAR, GETDATE())%10 AS ANIO");
            //lote=""+rs.getInt("DIA")+String.format("%02d", rs.getInt("SEM"))+rs.getInt("ANIO")+"1";
            
            for(MovimientoProducto p: productos) {
                idEmpaque=p.getProducto().getIdProducto();
                
                if(p.getCantFacturada()> 0) {
                    capturados++;
                    if(nueva) {
                        ps.setInt(1, m.getIdMovto());
                        ps.setInt(2, idEmpaque);
                        ps.setDouble(3, p.getCantFacturada());
                        ps.setDouble(4, p.getCantSinCargo());
                        ps.setDouble(5, p.getPrecio());
                        ps.setDouble(6, p.getDesctoProducto1());
                        ps.setDouble(7, p.getDesctoProducto2());
                        ps.setDouble(8, p.getDesctoConfidencial());
                        ps.setDouble(9, p.getUnitario());
                        ps.setDouble(10, p.getCantOrdenada());
                        ps.setDouble(11, 0);
                        ps.setInt(12, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo());
                        ps.executeUpdate();

                        idImpuestoGrupo=p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo();
                        this.agregarImpuestosProducto(cn, idImpuestoGrupo, m.getIdImpuestoZona(), m.getIdMovto(), idEmpaque);
                        this.calculaImpuestosProducto(cn, m.getIdMovto(), idEmpaque, p.getUnitario(), p.getProducto().getPiezas());
                    } else {
                        ps1.setDouble(1, p.getPrecio());
                        ps1.setDouble(2, p.getDesctoProducto1());
                        ps1.setDouble(3, p.getDesctoProducto2());
                        ps1.setDouble(4, p.getDesctoConfidencial());
                        ps1.setDouble(5, p.getUnitario());
                        ps1.setDouble(6, p.getCantFacturada());
                        ps1.setDouble(7, p.getCantSinCargo());
                        ps1.setInt(8, idEmpaque);
                        ps1.executeUpdate();
                    }
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
                        ps5.setInt(2, idEmpaque);
                        ps5.executeUpdate();
                        
                        ps7.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                        ps7.setDouble(2, p.getCantFacturada());
                        ps7.setDouble(3, p.getUnitario());
                        ps7.setDouble(4, p.getCantFacturada());
                        ps7.setDouble(5, p.getCantSinCargo());
                        ps7.setInt(6, m.getIdMovto());
                        ps7.setInt(7, idEmpaque);
                        ps7.executeUpdate();
                    } else {
                        existenciaAnterior=0;
                        
                        ps4.setInt(1, m.getIdAlmacen());
                        ps4.setInt(2, idEmpaque);
                        ps4.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                        ps4.executeUpdate();
                        
                        rs=st.executeQuery("SELECT existenciaOficina FROM empresasEmpaques "
                                + "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque="+idEmpaque);
                        if(rs.next()) {
                            ps7.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                            ps7.setDouble(2, p.getCantFacturada());
                            ps7.setDouble(3, p.getUnitario());
                            ps7.setDouble(4, p.getCantFacturada());
                            ps7.setDouble(5, p.getCantSinCargo());
                            ps7.setInt(6, m.getIdMovto());
                            ps7.setInt(7, idEmpaque);
                            ps7.executeUpdate();
                        } else {
                            ps6.setInt(1, idEmpaque);
                            ps6.setDouble(2, p.getUnitario());
                            ps6.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                            ps6.setInt(4, m.getIdMovto());
                            ps6.executeUpdate();
                        }
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
        int folio=0;
        int idMovto=0;
        int idEmpaque;
        int idImpuestoGrupo;
        
        ResultSet rs;
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, cantOrdenada, cantRecibida, idImpuestoGrupo, cantSinCargo, fecha) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, getdate())";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            rs=st.executeQuery("SELECT folio FROM movimientosTipos WHERE idTipo=1");
            if(rs.next()) {
                folio=rs.getInt("folio");
                st.executeUpdate("UPDATE movimientosTipos SET folio=folio+1 WHERE idTipo=1");
            }
            strSQL="INSERT INTO movimientos (idTipo, idCedis, folio, idEmpresa, idAlmacen, idReferencia, idImpuestoZona, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, idUsuario, fecha) "
                    + "VALUES (1, "+m.getIdCedis()+", "+folio+", "+m.getIdEmpresa()+", "+m.getIdAlmacen()+", "+m.getIdReferencia()+", "+m.getIdImpuestoZona()+", "+m.getIdMoneda()+", "+m.getTipoCambio()+", "+m.getDesctoComercial()+", "+m.getDesctoProntoPago()+", "+this.idUsuario+", getdate())";
            st.executeUpdate(strSQL);
            
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO comprobantesOrdenesCompra (idComprobante, idOrdenCompra, idEntrada) " +
                   "VALUES ("+m.getIdReferencia()+", "+idOrdenCompra+", "+idMovto+")";
            st.executeUpdate(strSQL);
            
            for(MovimientoProducto p: productos) {
                ps.setInt(1, idMovto);
                ps.setInt(2, p.getProducto().getIdProducto());
                ps.setDouble(3, p.getPrecio());
                ps.setDouble(4, p.getDesctoProducto1());
                ps.setDouble(5, p.getDesctoProducto2());
                ps.setDouble(6, p.getDesctoConfidencial());
                ps.setDouble(7, p.getUnitario());
                ps.setDouble(8, p.getCantOrdenada());
                ps.setDouble(9, 0);
                ps.setInt(10, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo());
                ps.setDouble(11, p.getCantSinCargo());
                ps.executeUpdate();
                
                idEmpaque=p.getProducto().getIdProducto();
                idImpuestoGrupo=p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo();
                this.agregarImpuestosProducto(cn, idImpuestoGrupo, m.getIdImpuestoZona(), idMovto, idEmpaque);
                this.calculaImpuestosProducto(cn, idMovto, idEmpaque, p.getUnitario(), p.getProducto().getPiezas());
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
                        "WHERE idMovto="+idMovto+" AND idEmpaque="+idEmpaque+" AND acumulable=1";
        st.executeUpdate(strSQL);
        
        strSQL="UPDATE d " +
                "SET importe=CASE WHEN aplicable=0 THEN 0 " +
                                 "WHEN modo=1 THEN ("+unitario+"+COALESCE(a.acumulable, 0))*valor/100.00 " +
                		 "ELSE "+piezas+"*valor END " +
                "FROM movimientosDetalleImpuestos d " +
                "LEFT JOIN (SELECT idMovto, idEmpaque, SUM(importe) AS acumulable " +
                	   "FROM movimientosDetalleImpuestos " +
                	   "WHERE idMovto=4 AND idEmpaque=8 AND acumulable=1 " +
                	   "GROUP BY idMovto, idEmpaque) a ON a.idMovto=d.idMovto AND a.idEmpaque=d.idEmpaque " +
                "WHERE d.idMovto="+idMovto+" AND d.idEmpaque="+idEmpaque+" AND d.acumulable=0";
        st.executeUpdate(strSQL);
    }
    
    public ArrayList<ImpuestosProducto> obtenerImpuestosProducto(int idMovto, int idEmpaque) throws SQLException {
        ArrayList<ImpuestosProducto> impuestos=new ArrayList<ImpuestosProducto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            String strSQL="select idImpuesto, impuesto, valor, aplicable, modo, acreditable, importe, acumulable\n" +
                            "from movimientosDetalleImpuestos\n" +
                            "where idMovto="+idMovto+" and idEmpaque="+idEmpaque;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                impuestos.add(construirImpuestosProducto(rs));
            }
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    private ImpuestosProducto construirImpuestosProducto(ResultSet rs) throws SQLException {
        ImpuestosProducto ip=new ImpuestosProducto();
        ip.setIdImpuesto(rs.getInt("idImpuesto"));
        ip.setImpuesto(rs.getString("impuesto"));
        ip.setValor(rs.getDouble("valor"));
        ip.setAplicable(rs.getBoolean("aplicable"));
        ip.setModo(rs.getInt("modo"));
        ip.setAcreditable(rs.getBoolean("acreditable"));
        ip.setImporte(rs.getDouble("importe"));
        ip.setAcumulable(rs.getBoolean("acumulable"));
        return ip;
    }
    
    private void agregarImpuestosProducto(Connection cn, int idImpuestoGrupo, int idZona, int idMovto, int idEmpaque) throws SQLException {
        Statement st=cn.createStatement();
        String strSQL="insert into movimientosDetalleImpuestos (idMovto, idEmpaque, idImpuesto, impuesto, valor, aplicable, modo, acreditable, importe, acumulable) " +
                        "select "+idMovto+", "+idEmpaque+", id.idImpuesto, i.impuesto, id.valor, i.aplicable, i.modo, i.acreditable, 0.00 as importe, i.acumulable " +
                        "from impuestosDetalle id " +
                        "inner join impuestos i on i.idImpuesto=id.idImpuesto " +
                        "where id.idGrupo="+idImpuestoGrupo+" and id.idZona="+idZona+" and GETDATE() between fechaInicial and fechaFinal";
        st.executeUpdate(strSQL);
    }
    
    public ArrayList<ImpuestosProducto> generarImpuestosProducto(int idImpuestoGrupo, int idZona) throws SQLException {
        ArrayList<ImpuestosProducto> impuestos=new ArrayList<ImpuestosProducto>();
        Connection cn = this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT id.idImpuesto, i.impuesto, id.valor, i.aplicable, i.modo, i.acreditable, 0.00 as importe, i.acumulable\n" +
                    "FROM impuestosDetalle id\n" +
                    "INNER JOIN impuestos i ON i.idImpuesto=id.idImpuesto\n" +
                    "WHERE id.idGrupo="+idImpuestoGrupo+" and id.idZona="+idZona+" and GETDATE() between fechaInicial and fechaFinal";
        ResultSet rs=st.executeQuery(strSQL);
        while(rs.next()) {
            impuestos.add(this.construirImpuestosProducto(rs));
        }
        return impuestos;
    }
    
    public ArrayList<TOMovimientoDetalle> obtenerDetalleMovimiento(int idMovto) throws SQLException, NamingException {
        ArrayList<TOMovimientoDetalle> productos=new ArrayList<TOMovimientoDetalle>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                productos.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public TOMovimientoDetalle construirDetalle(ResultSet rs) throws SQLException {
        TOMovimientoDetalle to=new TOMovimientoDetalle();
        to.setIdProducto(rs.getInt("idEmpaque"));
        to.setDesctoProducto1(rs.getDouble("desctoProducto1"));
        to.setDesctoProducto2(rs.getDouble("desctoProducto2"));
        to.setDesctoConfidencial(rs.getDouble("desctoConfidencial"));
        to.setCantOrdenada(rs.getDouble("cantOrdenada"));
        to.setCantFacturada(rs.getDouble("cantFacturada"));
        to.setCantSinCargo(rs.getDouble("cantSinCargo"));
        to.setCantRecibida(rs.getDouble("cantRecibida"));
        to.setPrecio(rs.getDouble("costo"));
        return to;
    } 
    
    public MovimientoProducto construirProducto(ResultSet rs) throws SQLException {
        MovimientoProducto producto=new MovimientoProducto();
        producto.setProducto(new Producto());
        producto.getProducto().setIdProducto(rs.getInt("idEmpaque"));
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
    
    public int obtenerIdOrdenCompra(int idComprobante, int idEntrada) throws SQLException {
        int idOrdenCompra=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idOrdenCompra FROM comprobantesOrdenesCompra " +
                                         "WHERE idComprobante="+idComprobante+" AND idEntrada="+idEntrada);
            if(rs.next()) {
                idOrdenCompra=rs.getInt("idOrdenCompra");
            }
        } finally {
            cn.close();
        }
        return idOrdenCompra;
    } 
    
    public ArrayList<TOMovimiento> obtenerEntradas(int idComprobante) throws SQLException {
        ArrayList<TOMovimiento> lstMovimientos = new ArrayList<TOMovimiento>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            String strSQL="SELECT M.idMovto, M.idTipo, M.folio, M.idCedis, M.idEmpresa, M.idAlmacen, " +
                            "M.idImpuestoZona, M.idReferencia, M.idMoneda, M.tipoCambio, " +
                            "M.desctoComercial, M.desctoProntoPago, M.fecha, M.idUsuario " +
                        "FROM movimientos M " +
                        "INNER JOIN comprobantes C ON C.idComprobante=M.idReferencia " +
                        "LEFT JOIN comprobantesOrdenesCompra O ON O.idComprobante=M.idReferencia " +
                        "WHERE M.idTipo=1 AND C.idComprobante="+idComprobante;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                lstMovimientos.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lstMovimientos;
    }
    
    public TOMovimiento obtenerMovimiento(int idMovto) throws SQLException {
        TOMovimiento to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
             ResultSet rs=st.executeQuery("SELECT idMovto, idTipo, folio, idCedis, idEmpresa, idAlmacen, idImpuestoZona, idReferencia, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, fecha, idUsuario "
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
        to.setFolio(rs.getInt("folio"));
        to.setIdCedis(rs.getInt("idCedis"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
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
    
    public TOMovimiento obtenerSolicitudTraspaso(int idComprobante) throws SQLException {
        TOMovimiento to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            String strSQL="select m.* " +
                        "from movimientos m " +
                        "inner join comprobantes c on c.idComprobante=m.idReferencia " +
                        "where m.idTipo=2 and c.idComprobante="+idComprobante;
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=construir(rs);
            }
        } finally {
            cn.close();
        }
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
    
    public int obtenerIdUsuario() {
        return this.idUsuario;
    }
}
