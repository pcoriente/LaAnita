package cotizaciones.dao;

import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
import dominios.Moneda;
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
import productos.dao.DAOProductos;
import usuarios.UsuarioSesion;

public class DAOCotizaciones {

    private final DataSource ds;
    private UsuarioSesion usuarioSesion;

    public DAOCotizaciones() throws NamingException {
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

    public ArrayList<CotizacionEncabezado> listaCotizaciones() throws SQLException {
        ArrayList<CotizacionEncabezado> lista = new ArrayList<CotizacionEncabezado>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "SELECT c.idRequisicion,  ed.depto, r.fechaRequisicion, r.fechaAprobacion,  COUNT(distinct cd.idProducto) as numProductos, COUNT(distinct cd.idCotizacion)as numCotizaciones,c.estado\n"
                    + "FROM cotizaciones c\n"
                    + "INNER JOIN cotizacionesDetalle cd ON cd.idCotizacion= c.idCotizacion\n"
                    + "INNER JOIN requisiciones r ON r.idRequisicion = c.idRequisicion\n"
                    + "INNER JOIN empleadosDeptos ed ON ed.idDepto = r.idDepto\n"
                    + "GROUP BY c.idRequisicion,  ed.depto, r.fechaRequisicion, r.fechaAprobacion, c.estado";

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private CotizacionEncabezado construir(ResultSet rs) throws SQLException {
        CotizacionEncabezado ce = new CotizacionEncabezado();
        ce.setIdRequisicion(rs.getInt("idRequisicion"));
        ce.setDepto(rs.getString("depto"));
        ce.setFechaRequisicion(utilerias.Utilerias.date2String(rs.getDate("fechaRequisicion")));
        ce.setFechaAprobacion(utilerias.Utilerias.date2String(rs.getDate("fechaAprobacion")));
        ce.setNumCotizaciones(rs.getInt("numCotizaciones"));
        ce.setNumProductos(rs.getInt("numProductos"));
        ce.setEstado(rs.getInt("estado"));

        return ce;

    }

    public ArrayList<CotizacionDetalle> dameProductoCotizacionesProveedores(int idReq) throws SQLException, NamingException {
        ArrayList<CotizacionDetalle> lista = new ArrayList<CotizacionDetalle>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select distinct( cd.idProducto),c.idRequisicion from cotizacionesDetalle cd\n"
                    + "       inner join cotizaciones c on c.idCotizacion = cd.idCotizacion\n"
                    + "       where c.idRequisicion=" + idReq;
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirConsultaProducto(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    public ArrayList<CotizacionDetalle> consultaCotizacionesProveedores(int idReq, int idProducto) throws SQLException, NamingException {
        ArrayList<CotizacionDetalle> lista = new ArrayList<CotizacionDetalle>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "select c.idProveedor, c.descuentoCotizacion, c.descuentoProntoPago, c.idProveedor, cd.*, pro.nombreComercial, cb.contribuyente   from cotizacionesDetalle cd\n"
                    + " inner join cotizaciones c on c.idCotizacion = cd.idCotizacion\n"
                    + " inner join proveedores pro on c.idProveedor = pro.idProveedor\n"
                    + " inner join contribuyentes cb on cb.idContribuyente = pro.idContribuyente\n"
                    + " where c.idRequisicion=" + idReq
                    + " and cd.idProducto = " + idProducto;
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirConsulta(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private CotizacionDetalle construirConsulta(ResultSet rs) throws SQLException, NamingException {
        CotizacionDetalle cd = new CotizacionDetalle();
        DAOProductos daoP = new DAOProductos();

        CotizacionEncabezado ce = new CotizacionEncabezado();
        cd.getProveedor().setNombreComercial(rs.getString("contribuyente"));
        ce.setIdProveedor(rs.getInt("idProveedor"));
        ce.setDescuentoCotizacion(rs.getDouble("descuentoCotizacion"));
        ce.setDescuentoProntoPago(rs.getDouble("descuentoProntoPago"));
        cd.setIdCotizacion(rs.getInt("idCotizacion"));
        cd.setProducto(daoP.obtenerProducto(rs.getInt("idProducto")));
        cd.setCantidadCotizada(rs.getDouble("cantidadCotizada"));
        cd.setCostoCotizado(rs.getDouble("costoCotizado"));
        cd.setDescuentoProducto(rs.getDouble("descuentoProducto"));
        cd.setDescuentoProducto2(rs.getDouble("descuentoProducto2"));
        cd.setNeto(rs.getDouble("neto"));
        cd.setSubtotal(rs.getDouble("subtotal"));
        cd.setCotizacionEncabezado(ce);
        cd.getProveedor().setIdProveedor(rs.getInt("idProveedor"));
        return cd;
    }

    private CotizacionDetalle construirConsultaProducto(ResultSet rs) throws SQLException, NamingException {
        CotizacionDetalle cd = new CotizacionDetalle();
        DAOProductos daoP = new DAOProductos();
        CotizacionEncabezado ce = new CotizacionEncabezado();
//        cd.getProducto().setIdProducto(rs.getInt("idProducto"));
        cd.setProducto(daoP.obtenerProducto(rs.getInt("idProducto")));
        cd.setIdCotizacion(rs.getInt("idRequisicion"));
        return cd;

    }

    public ArrayList<CotizacionEncabezado> consultaCotizacionesProveedoresEncabezado(int idReq) throws SQLException, NamingException {
        ArrayList<CotizacionEncabezado> lista = new ArrayList<CotizacionEncabezado>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select cd.* from cotizaciones c\n"
                    + "inner join cotizacionesDetalle cd ON cd.idCotizacion=c.idCotizacion\n"
                    + "where c.idRequisicion=" + idReq;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirConsultaEncabezado(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private CotizacionEncabezado construirConsultaEncabezado(ResultSet rs) throws SQLException, NamingException {
        CotizacionEncabezado ce = new CotizacionEncabezado();
        CotizacionDetalle cd = new CotizacionDetalle();
        DAOProductos daoP = new DAOProductos();
        cd.setIdCotizacion(rs.getInt("idCotizacion"));
        cd.setProducto(daoP.obtenerProducto(rs.getInt("idProducto")));
        cd.setCantidadCotizada(rs.getDouble("cantidadCotizada"));
        cd.setCostoCotizado(rs.getDouble("costoCotizado"));
        cd.setDescuentoProducto(rs.getDouble("descuentoProducto"));
        cd.setNeto(rs.getDouble("neto"));
        cd.setSubtotal(rs.getDouble("subtotal"));

        ce.setCotizacionDetalle(cd);

        return ce;
    }

    public Moneda obtenerMonedasConverter(int idMoneda) throws SQLException, NamingException {
        Context cI = new InitialContext();
        DataSource ds1 = (DataSource) cI.lookup("java:comp/env/jdbc/__systemWeb");
        Moneda mon = null;
        Connection cn = ds1.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT idMoneda, moneda, codigoIso FROM Monedas\n"
                    + "WHERE idMoneda=" + idMoneda);
            if (rs.next()) {
                mon = construirMoneda(rs);
            }
        } finally {
            cn.close();
        }
        return mon;
    }

    private Moneda construirMoneda(ResultSet rs) throws SQLException {
        Moneda mon = new Moneda();
        mon.setIdMoneda(rs.getInt("idMoneda"));
        mon.setMoneda(rs.getString("moneda"));
        mon.setCodigoIso(rs.getString("codigoIso"));
        return mon;
    }

    public ArrayList<Moneda> obtenerMonedas() throws NamingException, SQLException {
        Context cI = new InitialContext();
        DataSource ds2 = (DataSource) cI.lookup("java:comp/env/jdbc/__systemWeb");
        ArrayList<Moneda> lista = new ArrayList<Moneda>();
        ResultSet rs;
        Connection cn = ds2.getConnection();
        try {

            String stringSQL = "SELECT idMoneda, moneda, codigoIso FROM monedas where idMoneda between 0 and 4";

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirMoneda(rs));
            }
        } finally {
            cn.close();
        }
        return lista;

    }

    public void guardarOrdenCompra(ArrayList<CotizacionDetalle> ordenCompra) throws SQLException {
        String StringSql = "INSERT INTO ordenCompra(idCotizacion, fechaCreacion, fechaFinalizacion, fechaPuesta, status, desctoComercial, desctoProntoPago, fechaEntrega)"
                + "VALUES(?,?,?,?,?,?,?,?)";
        Connection cn = ds.getConnection();
        PreparedStatement ps = cn.prepareStatement(StringSql);
        for (CotizacionDetalle cotizacion : ordenCompra) {
        }
    }

    public void guardarOrdenCompraTotal(ArrayList<CotizacionDetalle> ordenCompra) throws SQLException {


        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1;
        PreparedStatement ps2;
        int idProveedor = 0;
        int idCotizacion = 0;
        int ident = 0;
        for (CotizacionDetalle c : ordenCompra) {

            int idCot = c.getIdCotizacion();
            int idProv = c.getCotizacionEncabezado().getIdProveedor();
            double dC = c.getCotizacionEncabezado().getDescuentoCotizacion();
            double dPP = c.getCotizacionEncabezado().getDescuentoProntoPago();

            int identity = 0;
            //CABECERO
            if (idProv != idProveedor) {
                idProveedor = idProv;

                String strSQL1 = "INSERT INTO ordenCompra(idCotizacion, fechaCreacion, fechaFinalizacion, fechaPuesta, estado, desctoComercial, desctoProntoPago,fechaEntrega)"
                        + " VALUES(" + idCot + ", GETDATE(), GETDATE(), GETDATE(), 1," + dC + ", " + dPP + ", GETDATE())";
                ps1 = cn.prepareStatement(strSQL1);
                ps1.executeUpdate();
                String strSQLIdentity = "SELECT @@IDENTITY as idOrd";
                ps1 = cn.prepareStatement(strSQLIdentity);
                ResultSet rs = ps1.executeQuery();
                while (rs.next()) {
                    identity = rs.getInt("idOrd");
                }
                ident = identity;

            }
            // DETALLE
            String stringSQL2 = "INSERT INTO ordenCompraDetalle (idOrdenCompra,interno, idProducto, sku, descuentoProducto, descuentoProducto2, desctoConfidencial, sinCargoBase, sinCargoCant, ptjeOferta, margen, idImpuestosGrupo, idMarca)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(stringSQL2);

            ps.setInt(1, ident);
            ps.setInt(2, 1);
            ps.setInt(3, c.getProducto().getIdProducto());
            ps.setString(4, "null");
            ps.setDouble(5, c.getDescuentoProducto());
            ps.setDouble(6, c.getDescuentoProducto2());
            ps.setDouble(7, 0.00);
            ps.setInt(8, 0);
            ps.setInt(9, 0);
            ps.setDouble(10, 0.00);
            ps.setDouble(11, 0.00);
            ps.setInt(12, 0);
            ps.setInt(13, 0);
            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println(e);
            }
        } //FOR DETALLE
        cn.close();
    }// FOR CABECERO
}