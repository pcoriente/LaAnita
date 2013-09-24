package requisiciones.dao;

import cotizaciones.dominio.CotizacionDetalle;
import java.sql.Connection;
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
import requisiciones.dominio.RequisicionProducto;
import requisiciones.to.TORequisicionProducto;
import requisiciones.to.TORequisicionEncabezado;
import usuarios.UsuarioSesion;

public class DAORequisiciones {

    private final DataSource ds;
    private UsuarioSesion usuarioSesion;

    public DAORequisiciones() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public void guardarRequisicion(int idEmpresa, int idDepto, int idSolicito, ArrayList<RequisicionProducto> pr) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1;
        PreparedStatement ps2;

        try {
            //  st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL1 = "INSERT INTO requisiciones(idEmpresa, idDepto, idSolicito,fechaRequisicion) VALUES (" + idEmpresa + ", " + idDepto + ", " + idSolicito + ",GETDATE())";
            String strSQLIdentity = "SELECT @@IDENTITY as idReq";
            ps1 = cn.prepareStatement(strSQL1);
            ps1.executeUpdate();
            ps1 = cn.prepareStatement(strSQLIdentity);
            ResultSet rs = ps1.executeQuery();
            int identity = 0;
            if (rs.next()) {
                identity = rs.getInt("idReq");
            }
            // DETALLE
            String strSQL2 = "INSERT INTO requisicionDetalle(idRequisicion,idProducto, cantidadSolicitada, cantidadAutorizada) VALUES (?,?,?,?)";
            ps2 = cn.prepareStatement(strSQL2);

            for (RequisicionProducto e : pr) {
                ps2.setInt(1, identity);
                ps2.setInt(2, e.getProducto().getIdProducto());
                ps2.setInt(3, e.getCantidad());
                ps2.setInt(4, e.getCantidad());
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            // st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    public ArrayList<TORequisicionEncabezado> dameRequisicion() throws SQLException {
        ArrayList<TORequisicionEncabezado> lista = new ArrayList<TORequisicionEncabezado>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "select r.idRequisicion, r.idEmpresa, r.idDepto, e.idEmpleado, r.idAprobo, r.fechaRequisicion, r.fechaAprobacion, r.estado from requisiciones r\n"
                    + "                    inner join empleados e on r.idSolicito= e.idEmpleado\n"
                    + "                    where r.estado between 0 and 2\n"
                    + "                    order by  idRequisicion desc";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirCabecero(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    public ArrayList<TORequisicionProducto> dameRequisicionDetalle(int idReq) throws SQLException {

        ArrayList<TORequisicionProducto> lista = new ArrayList<TORequisicionProducto>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select rd.idRequisicion,rd.idProducto,rd.cantidadSolicitada, rd.cantidadAutorizada from requisicionDetalle rd\n"
                    + "where idRequisicion=" + idReq;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return lista;

    }

    private TORequisicionProducto construirDetalle(ResultSet rs) throws SQLException {
        TORequisicionProducto to = new TORequisicionProducto();
        to.setIdRequisicion(rs.getInt("idRequisicion"));
        to.setIdProducto(rs.getInt("idProducto"));
        to.setCantidad(rs.getInt("cantidadSolicitada"));
        to.setCantidadAutorizada(rs.getInt("cantidadAutorizada"));

        return to;
    }

    public TORequisicionEncabezado dameRequisicionEncabezado(int idRequisi) throws SQLException {
        TORequisicionEncabezado toRE = new TORequisicionEncabezado();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "select r.idRequisicion, r.idEmpresa, r.IdDepto, e.idEmpleado, r.idAprobo, r.fechaRequisicion, r.fechaAprobacion, r.estado from requisiciones r\n"
                    + "inner join empleados e on r.idSolicito=e.idEmpleado\n"
                    + "where idRequisicion=" + idRequisi;
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            if (rs.next()) {
                toRE = construirCabecero(rs);
            }
        } finally {
            cn.close();
            return toRE;
        }
    }

    private TORequisicionEncabezado construirCabecero(ResultSet rs) throws SQLException {
        TORequisicionEncabezado to = new TORequisicionEncabezado();
        to.setIdRequisicion(rs.getInt("idRequisicion"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setIdDepto(rs.getInt("idDepto"));
        to.setIdSolicito(rs.getInt("idEmpleado"));
        to.setIdAprobo(rs.getInt("idAprobo"));
        to.setFechaRequisicion(rs.getDate("fechaRequisicion"));
        to.setFechaAprobacion(rs.getDate("fechaAprobacion"));
        to.setEmpleadoAprobo(usuarioSesion.getUsuario().getUsuario());
        to.setStatus(rs.getInt("estado"));
        return to;
    }

    public void actualizaRequisicion(int idReq, int estado) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps4;
        try {
            //  st.executeUpdate("begin transaction");

            int us = usuarioSesion.getUsuario().getId();
            if (us == 0) {
                System.out.println("El usuario no existe...");
            } else {
                String strSQL4 = "UPDATE requisiciones SET  fechaAprobacion=GETDATE(),idAprobo='" + us + "', estado='" + estado + "' WHERE idRequisicion=" + idReq;
                ps4 = cn.prepareStatement(strSQL4);
                ps4.executeUpdate();
            }


        } catch (SQLException e) {
            //   st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    public void eliminaProductoAprobar(int idReq, int idProd) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1;
        PreparedStatement ps2;
        try {
            st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL2 = "UPDATE requisicionDetalle SET cantidadAutorizada= 0 WHERE idRequisicion=" + idReq + " and idProducto=" + idProd + "";
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    public ArrayList<TORequisicionProducto> dameRequisicionDetalleAprobar(int idRequisi) throws SQLException {
        ArrayList<TORequisicionProducto> lista = new ArrayList<TORequisicionProducto>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select rd.idRequisicion,rd.idProducto,rd.cantidadSolicitada, rd.cantidadAutorizada from requisicionDetalle rd\n"
                    + "                    where idRequisicion=" + idRequisi;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    public void modificaProductoAprobar(int idReq, int idProd, int cantidad) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {
            st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL2 = "UPDATE requisicionDetalle SET cantidadAutorizada=" + cantidad + "  WHERE idRequisicion=" + idReq + " and idProducto=" + idProd + "";
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    public void modificarAprobacion(int idReq, int idProd, int cant) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2, ps3;
        try {
            if (cant != 0) {
                st.executeUpdate("begin transaction");
                String strSQL2 = "UPDATE requisiciones SET idAprobo=0,estado=1,fechaAprobacion=1900-01-01 WHERE idRequisicion=" + idReq;
                ps2 = cn.prepareStatement(strSQL2);
                ps2.executeUpdate();
                st.executeUpdate("commit transaction");
            } else {
                st.executeUpdate("begin transaction");
                String strSQL3 = "UPDATE requisicionDetalle SET cantidadAutorizada=cantidadSolicitada WHERE idRequisicion=" + idReq + " and idProducto=" + idProd;
                ps3 = cn.prepareStatement(strSQL3);
                ps3.executeUpdate();
                st.executeUpdate("commit transaction");
            }
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    //COTIZACIONES
    public void grabarCotizacion(int idReq, int idProv, double descGral, ArrayList<CotizacionDetalle> cd) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1, ps2, ps3, ps4;

        try {
            st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL1 = "INSERT INTO cotizaciones(idRequisicion, idProveedor, folioProveedor, fechaCotizacion,descuentoCotizacion,observaciones)"
                    + " VALUES (" + idReq + ", " + idProv + ",'Folio' ,GETDATE(), " + descGral + ",'hola')";
            String strSQLIdentity = "SELECT @@IDENTITY as idCot";
            ps1 = cn.prepareStatement(strSQL1);
            ps1.executeUpdate();
            ps3 = cn.prepareStatement(strSQLIdentity);
            ResultSet rs = ps3.executeQuery();
            int identity = 0;
            if (rs.next()) {
                identity = rs.getInt("idCot");

            }
            // DETALLE
            String strSQL2 = "INSERT INTO cotizacionesDetalle(idCotizacion,idProducto, cantidadCotizada, costoCotizado, descuentoProducto,neto,subtotal) VALUES (?,?,?,?,?,?,?)";
            ps2 = cn.prepareStatement(strSQL2);

            for (CotizacionDetalle e : cd) {
                ps2.setInt(1, identity);
                ps2.setInt(2, e.getProducto().getIdProducto());
                ps2.setDouble(3, e.getCantidadCotizada());
                ps2.setDouble(4, e.getCostoCotizado());
                ps2.setDouble(5, e.getDescuentoProducto());
                ps2.setDouble(6, e.getNeto());
                ps2.setDouble(7, e.getSubtotal());
                ps2.executeUpdate();
            }


            String strSQL3 = "UPDATE requisiciones SET estado=3  WHERE idRequisicion=" + idReq;
            ps4 = cn.prepareStatement(strSQL3);
            ps4.executeUpdate();

            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }


    }

    public void grabarCotizacionInicial(int idReq) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1, ps2, ps3;
        ArrayList<CotizacionDetalle> cd = new ArrayList<CotizacionDetalle>();
        try {
            st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL1 = "INSERT INTO cotizaciones(idRequisicion, idProveedor, folioProveedor, fechaCotizacion,descuentoCotizacion,observaciones)"
                    + " VALUES (" + idReq + ", " + 1 + ",'Folio' ,GETDATE(), " + 3 + ",'hola')";
            String strSQLIdentity = "SELECT @@IDENTITY as idCot";
            ps1 = cn.prepareStatement(strSQL1);
            ps1.executeUpdate();
            ps3 = cn.prepareStatement(strSQLIdentity);
            ResultSet rs = ps3.executeQuery();
            int identity = 0;
            if (rs.next()) {
                identity = rs.getInt("idCot");

            }
            // DETALLE
            String strSQL2 = "INSERT INTO cotizacionesDetalle(idCotizacion,idProducto, cantidadCotizada, costoCotizado, descuentoProducto.neto,subtotal) VALUES (?,?,?,?,?,?,?)";
            ps2 = cn.prepareStatement(strSQL2);


            for (CotizacionDetalle e : cd) {
                ps2.setInt(1, identity);
                ps2.setInt(2, e.getProducto().getIdProducto());
                ps2.setDouble(3, e.getCantidadCotizada());
                ps2.setDouble(4, e.getCostoCotizado());
                ps2.setDouble(5, e.getDescuentoProducto());
                ps2.setDouble(6, e.getNeto());
                ps2.setDouble(7, e.getSubtotal());

            }
            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }


    }

    public ArrayList<TORequisicionProducto> dameRequisicionDetalleCotizar(int idRequisi) throws SQLException {
        ArrayList<TORequisicionProducto> lista = new ArrayList<TORequisicionProducto>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        //   this.grabarCotizacionInicial(idRequisi);
        try {

            String stringSQL = "select rd.idRequisicion,rd.idProducto,rd.cantidadSolicitada, rd.cantidadAutorizada from requisicionDetalle rd\n"
                    + "                    where cantidadAutorizada>0 and idRequisicion=" + idRequisi;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    public void actualizarCantidadCotizada(int idCot, int idProd, int cc) throws SQLException {

        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {

            //CABECERO
            String strSQL2 = "UPDATE cotizacionesDetalle SET cantidadCotizada=" + cc + "  WHERE idCotizacion=" + idCot + " and idProducto=" + idProd + "";
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw (e);
        } finally {
            cn.close();
        }

    }

    public void actualizarPrecioDescuento(int idCot, int idProd, int costo, int desc) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {

            //CABECERO
            String strSQL2 = "UPDATE cotizacionesDetalle SET costoCotizado=" + costo + ", descuentoProducto=" + desc + "  WHERE idCotizacion=" + idCot + " and idProducto=" + idProd + "";
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw (e);
        } finally {
            cn.close();
        }

    }
}
