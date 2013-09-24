package cotizaciones.dao;

import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
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
import productos.dao.DAOProductos;
import productos.dominio.Producto;
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

    public ArrayList<CotizacionDetalle> consultaCotizacionesProveedores(int idReq) throws SQLException, NamingException {
        ArrayList<CotizacionDetalle> lista = new ArrayList<CotizacionDetalle>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select c.idProveedor, c.descuentoCotizacion, cd.* from cotizacionesDetalle cd\n"
                    + "inner join cotizaciones c on c.idCotizacion = cd.idCotizacion\n"
                    + "where c.idRequisicion=" + idReq;

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

        ce.setIdProveedor(rs.getInt("idProveedor"));
        ce.setDescuentoProveedor(rs.getDouble("descuentoCotizacion"));
        cd.setIdCotizacion(rs.getInt("idCotizacion"));
        cd.setProducto(daoP.obtenerProducto(rs.getInt("idProducto")));
        cd.setCantidadCotizada(rs.getDouble("cantidadCotizada"));
        cd.setCostoCotizado(rs.getDouble("costoCotizado"));
        cd.setDescuentoProducto(rs.getDouble("descuentoProducto"));
        cd.setNeto(rs.getDouble("neto"));
        cd.setSubtotal(rs.getDouble("subtotal"));
        cd.setCotizacionEncabezado(ce);
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
}
