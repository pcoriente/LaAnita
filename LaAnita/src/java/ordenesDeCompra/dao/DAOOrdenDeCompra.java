package ordenesDeCompra.dao;

import contactos.dominio.Contacto;
import cotizaciones.dao.DAOCotizaciones;
import direccion.dao.DAOPais;
import direccion.dominio.Direccion;
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
import monedas.Moneda;
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import productos.dao.DAOProductos;
import proveedores.dao.DAOProveedores;
import proveedores.dominio.Proveedor;
import usuarios.UsuarioSesion;

public class DAOOrdenDeCompra {

    private final DataSource ds;
    private UsuarioSesion usuarioSesion;

    public DAOOrdenDeCompra() throws NamingException {
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

    public ArrayList<OrdenCompraEncabezado> listaOrdenes() throws SQLException, NamingException {
        ArrayList<OrdenCompraEncabezado> lista = new ArrayList<OrdenCompraEncabezado>();
        Connection cn = ds.getConnection();
        Statement sentencia = cn.createStatement();
        try {

            String stringSQL = "select oc.idOrdenCompra, oc.fechaCreacion, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado, oc.idMoneda \n"
                    + "                                       , m.idMoneda, m.Moneda, m.codigoIso\n"
                    + "                                       , isnull(c.idCotizacion, 0) as idCotizacion, isnull(c.idRequisicion,0) as idRequisicion, isnull(c.desctoComercial,0.00) as desctoComercial, isnull(c.desctoProntoPago,0.00) as desctoProntoPago\n"
                    + "                                       , isnull(c.idProveedor,0) as idProveedor, isnull(c.idDireccionEntrega,0) as idDireccionEntrega\n"
                    + "                                       , isnull(c.nombreComercial,'') as nombreComercial, isnull(c.idDireccion, 0) as idDireccion\n"
                    + "                               from ordenCompra oc\n"
                    + "                               inner join webSystem.dbo.monedas m on m.idMoneda=oc.idMoneda\n"
                    + "                               left join (select c.idCotizacion, c.idRequisicion, c.descuentoCotizacion as desctoComercial, c.descuentoProntoPago as desctoProntoPago\n"
                    + "                                               , p.idProveedor, p.idDireccionEntrega, eg.nombreComercial, d.idDireccion\n"
                    + "                                           from cotizaciones c\n"
                    + "                                           inner join proveedores p on p.idProveedor = c.idProveedor\n"
                    + "                                           inner join contribuyentes co on co.idContribuyente = p.idContribuyente\n"
                    + "                                           inner join requisiciones r on r.idRequisicion = c.idRequisicion\n"
                    + "                                           inner join empresasGrupo eg on eg.idEmpresa = r.idEmpresa\n"
                    + "                                           inner join direcciones d on d.idDireccion = co.idDireccion) c on c.idCotizacion=oc.idCotizacion\n"
                    + "                               where oc.estado >0\n"
                    + "                               order by oc.idOrdenCompra desc";

//            //Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirOCEncabezado(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    public ArrayList<OrdenCompraEncabezado> listaOrdenes(int idProveedor, int status) throws SQLException, NamingException {
        ArrayList<OrdenCompraEncabezado> lista = new ArrayList<OrdenCompraEncabezado>();
        Connection cn = ds.getConnection();
        Statement sentencia = cn.createStatement();
        try {

            String stringSQL = "select oc.idOrdenCompra, oc.fechaCreacion, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado, oc.idMoneda \n"
                    + "                                       , m.idMoneda, m.Moneda, m.codigoIso\n"
                    + "                                       , isnull(c.idCotizacion, 0) as idCotizacion, isnull(c.idRequisicion,0) as idRequisicion, isnull(c.desctoComercial,0.00) as desctoComercial, isnull(c.desctoProntoPago,0.00) as desctoProntoPago\n"
                    + "                                       , isnull(c.idProveedor,0) as idProveedor, isnull(c.idDireccionEntrega,0) as idDireccionEntrega\n"
                    + "                                       , isnull(c.nombreComercial,'') as nombreComercial, isnull(c.idDireccion, 0) as idDireccion\n"
                    + "                               from ordenCompra oc\n"
                    + "                               inner join webSystem.dbo.monedas m on m.idMoneda=oc.idMoneda\n"
                    + "                               left join (select c.idCotizacion, c.idRequisicion, c.descuentoCotizacion as desctoComercial, c.descuentoProntoPago as desctoProntoPago\n"
                    + "                                               , p.idProveedor, p.idDireccionEntrega, eg.nombreComercial, d.idDireccion\n"
                    + "                                           from cotizaciones c\n"
                    + "                                           inner join proveedores p on p.idProveedor = c.idProveedor\n"
                    + "                                           inner join contribuyentes co on co.idContribuyente = p.idContribuyente\n"
                    + "                                           inner join requisiciones r on r.idRequisicion = c.idRequisicion\n"
                    + "                                           inner join empresasGrupo eg on eg.idEmpresa = r.idEmpresa\n"
                    + "                                           inner join direcciones d on d.idDireccion = co.idDireccion) c on c.idCotizacion=oc.idCotizacion\n"
                    + "                               where oc.idProveedor="+idProveedor+" and oc.estado=" + status + "\n"
                    + "                               order by oc.idOrdenCompra desc";

            //Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirOCEncabezado(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private OrdenCompraEncabezado construirOCEncabezado(ResultSet rs) throws SQLException, NamingException {
        OrdenCompraEncabezado oce = new OrdenCompraEncabezado();
        Moneda moneda = new Moneda();
        moneda.setIdMoneda(rs.getInt("idMoneda"));
        moneda.setMoneda(rs.getString("moneda"));
        moneda.setCodigoIso(rs.getString("codigoIso"));

        DAOProveedores daoP = new DAOProveedores();
        oce.setIdOrdenCompra(rs.getInt("idOrdenCompra"));
        oce.setIdCotizacion(rs.getInt("idCotizacion"));
        oce.setIdRequisicion(rs.getInt("idRequisicion"));

        oce.setNombreComercial(rs.getString("nombreComercial"));
        oce.setDesctoComercial(rs.getDouble("desctoComercial"));
        oce.setDesctoProntoPago(rs.getDouble("desctoProntoPago"));

        int idProveedor = rs.getInt("idProveedor");
        if (idProveedor == 0) {
            oce.setProveedor(new Proveedor());
        } else {
            oce.setProveedor(daoP.obtenerProveedor(idProveedor));
        }
        int idDireccion = oce.getProveedor().getContribuyente().getIdContribuyente();
        if (idDireccion != 0) {
            oce.getProveedor().getContribuyente().setDireccion(this.obtenerDireccion(idDireccion));
        }
        int idDireccionEntrega = oce.getProveedor().getDireccionEntrega().getIdDireccion();
        if (idDireccionEntrega != 0) {
            oce.getProveedor().setDireccionEntrega(this.obtenerDireccion(idDireccionEntrega));
        }
        oce.setFechaCreacion(utilerias.Utilerias.date2String(rs.getDate("fechaCreacion")));
        oce.setFechaFinalizacion(utilerias.Utilerias.date2String(rs.getDate("fechaFinalizacion")));
        oce.setFechaPuesta(utilerias.Utilerias.date2String(rs.getDate("fechaPuesta")));
        oce.setFechaEntrega(utilerias.Utilerias.date2String(rs.getDate("fechaEntrega")));
        oce.setEstado(rs.getInt("estado"));
        switch (rs.getInt("estado")) {
            case 0:
                oce.setStatus("Rechazado");
                break;
            case 1:
                oce.setStatus("Activado");
                break;
            case 2:
                oce.setStatus("Ordenado");
                break;
            case 3:
                oce.setStatus("No Aprobado");
                break;
            default:
                oce.setStatus("Cerrado");
        }
        oce.setMoneda(moneda);
        return oce;
    }
    public ArrayList<OrdenCompraDetalle> consultaOrdenCompra(int idOC) throws SQLException, NamingException {
        ArrayList<OrdenCompraDetalle> lista = new ArrayList<OrdenCompraDetalle>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select oc.idOrdenCompra, oc.idCotizacion"
                    + "             , ocd.idProducto, ocd.cantOrdenada, ocd.costoOrdenado, ocd.descuentoProducto, ocd.descuentoProducto2, ocd.sku\n"
                    + "         from ordencompra oc\n"
                    + "         inner join ordenCompraDetalle ocd on ocd.idOrdenCompra = oc.idOrdenCompra\n"
                    + "         where oc.idOrdenCompra=" + idOC;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirOCDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private OrdenCompraDetalle construirOCDetalle(ResultSet rs) throws SQLException, NamingException {
        OrdenCompraDetalle ocd = new OrdenCompraDetalle();

        DAOCotizaciones daoC = new DAOCotizaciones();
        DAOProductos daoP = new DAOProductos();
        ocd.setCotizacionDetalle(daoC.dameCotizacion(rs.getInt("idCotizacion")));
        ocd.setProducto(daoP.obtenerProducto(rs.getInt("idProducto")));
        ocd.setSku(rs.getString("sku"));
        ocd.setIdOrdenCompra(rs.getInt("idOrdenCompra"));
        ocd.setCantOrdenada(rs.getDouble("cantOrdenada"));
        ocd.setCantidadSolicitada(rs.getDouble("cantOrdenada"));
        ocd.setCostoOrdenado(rs.getDouble("costoOrdenado"));
        ocd.setDescuentoProducto(rs.getDouble("descuentoProducto"));
        ocd.setDescuentoProducto2(rs.getDouble("descuentoProducto2"));
        return ocd;
    }

    public Direccion obtenerDireccion(int idDireccion) throws SQLException, NamingException {
        Direccion toDir = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM direcciones WHERE idDireccion=" + idDireccion);
            if (rs.next()) {
                toDir = construirDireccion(rs);
            }
        } finally {
            cn.close();
        }
        return toDir;
    }

    private Direccion construirDireccion(ResultSet rs) throws SQLException, NamingException {
        Direccion toDir = new Direccion();
        DAOPais daoP = new DAOPais();
        toDir.setIdDireccion(rs.getInt("idDireccion"));
        toDir.setCalle(rs.getString("calle"));
        toDir.setNumeroExterior(rs.getString("numeroExterior"));
        toDir.setNumeroInterior(rs.getString("numeroInterior"));
        toDir.setReferencia(rs.getString("referencia"));
        toDir.setPais(daoP.obtener(rs.getInt("idPais")));
        toDir.setCodigoPostal(rs.getString("codigoPostal"));
        toDir.setEstado(rs.getString("estado"));
        toDir.setMunicipio(rs.getString("municipio"));
        toDir.setLocalidad(rs.getString("localidad"));
        toDir.setColonia(rs.getString("colonia"));
        toDir.setNumeroLocalizacion(rs.getString("numeroLocalizacion"));
        return toDir;
    }

    public void actualizarCantidadOrdenada(int idOrden, int idProd, double cc) throws SQLException {

        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {

            //CABECERO
            String strSQL2 = "UPDATE ordenCompraDetalle SET cantOrdenada=" + cc + "  WHERE idOrdenCompra=" + idOrden + " and idProducto=" + idProd + "";
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw (e);
        } finally {
            cn.close();
        }

    }

    public void procesarOrdenCompra(int idOrden) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {

            //CABECERO
            String strSQL2 = "UPDATE ordenCompra SET estado=2  WHERE idOrdenCompra=" + idOrden;
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw (e);
        } finally {
            cn.close();
        }


    }

    public void cancelarOrdenCompra(int idOrden) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps2;
        try {

            //CABECERO
            String strSQL2 = "UPDATE ordenCompra SET estado=0  WHERE idOrdenCompra=" + idOrden;
            ps2 = cn.prepareStatement(strSQL2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw (e);
        } finally {
            cn.close();
        }


    }

    public ArrayList<Contacto> obtenerContactos(int idOC) throws SQLException {
        ArrayList<Contacto> lista = new ArrayList<Contacto>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select * from ordenCompra oc\n"
                    + "inner join cotizaciones c on c.idCotizacion=oc.idCotizacion\n"
                    + "inner join contactos con on con.idPadre=c.idProveedor\n"
                    + "where oc.idOrdenCompra=" + idOC;

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirContactos(rs));
            }
        } finally {
            cn.close();
        }
        return lista;

    }

    private Contacto construirContactos(ResultSet rs) throws SQLException {
        Contacto cont = new Contacto();
        cont.setCorreo(rs.getString("correo"));
        return cont;
    }

    //DAVID
//    public ArrayList<OrdenCompraEncabezado> listaOrdenes() throws SQLException, NamingException {
//        ArrayList<OrdenCompraEncabezado> lista = new ArrayList<OrdenCompraEncabezado>();
//        Connection cn = ds.getConnection();
//        Statement sentencia = cn.createStatement();
//        try {
//
//            String stringSQL = "select oc.idOrdenCompra, oc.fechaCreacion, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado, oc.idMoneda \n"
//                    + "                                       , m.idMoneda, m.Moneda, m.codigoIso\n"
//                    + "                                       , isnull(c.idCotizacion, 0) as idCotizacion, isnull(c.idRequisicion,0) as idRequisicion, isnull(c.desctoComercial,0.00) as desctoComercial, isnull(c.desctoProntoPago,0.00) as desctoProntoPago\n"
//                    + "                                       , isnull(c.idProveedor,0) as idProveedor, isnull(c.idDireccionEntrega,0) as idDireccionEntrega\n"
//                    + "                                       , isnull(c.nombreComercial,'') as nombreComercial, isnull(c.idDireccion, 0) as idDireccion\n"
//                    + "                               from ordenCompra oc\n"
//                    + "                               inner join webSystem.dbo.monedas m on m.idMoneda=oc.idMoneda\n"
//                    + "                               left join (select c.idCotizacion, c.idRequisicion, c.descuentoCotizacion as desctoComercial, c.descuentoProntoPago as desctoProntoPago\n"
//                    + "                                               , p.idProveedor, p.idDireccionEntrega, eg.nombreComercial, d.idDireccion\n"
//                    + "                                           from cotizaciones c\n"
//                    + "                                           inner join proveedores p on p.idProveedor = c.idProveedor\n"
//                    + "                                           inner join contribuyentes co on co.idContribuyente = p.idContribuyente\n"
//                    + "                                           inner join requisiciones r on r.idRequisicion = c.idRequisicion\n"
//                    + "                                           inner join empresasGrupo eg on eg.idEmpresa = r.idEmpresa\n"
//                    + "                                           inner join direcciones d on d.idDireccion = co.idDireccion) c on c.idCotizacion=oc.idCotizacion\n"
//                    + "                               where oc.estado >0\n"
//                    + "                               order by oc.idOrdenCompra desc";
//
//            //Statement sentencia = cn.createStatement();
//            ResultSet rs = sentencia.executeQuery(stringSQL);
//            while (rs.next()) {
//                lista.add(construirOCEncabezado(rs));
//            }
//        } finally {
//            cn.close();
//        }
//        return lista;
//    }
//
//    private OrdenCompraEncabezado construirOCEncabezado(ResultSet rs) throws SQLException, NamingException {
//        OrdenCompraEncabezado oce = new OrdenCompraEncabezado();
//
//        Moneda moneda = new Moneda();
//        moneda.setIdMoneda(rs.getInt("idMoneda"));
//        moneda.setMoneda(rs.getString("moneda"));
//        moneda.setCodigoIso(rs.getString("codigoIso"));
//
//        DAOProveedores daoP = new DAOProveedores();
//        oce.setIdOrdenCompra(rs.getInt("idOrdenCompra"));
//        oce.setIdCotizacion(rs.getInt("idCotizacion"));
//        oce.setIdRequisicion(rs.getInt("idRequisicion"));
//        oce.setNombreComercial(rs.getString("nombreComercial"));
//        oce.setDesctoComercial(rs.getDouble("desctoComercial"));
//        oce.setDesctoProntoPago(rs.getDouble("desctoProntoPago"));
//
//        int idProveedor = rs.getInt("idProveedor");
//        if (idProveedor == 0) {
//            oce.setProveedor(new Proveedor());
//        } else {
//            oce.setProveedor(daoP.obtenerProveedor(idProveedor));
//        }
//        int idDireccion = oce.getProveedor().getContribuyente().getIdContribuyente();
//        if (idDireccion != 0) {
//            oce.getProveedor().getContribuyente().setDireccion(this.obtenerDireccion(idDireccion));
//        }
//        int idDireccionEntrega = oce.getProveedor().getDireccionEntrega().getIdDireccion();
//        if (idDireccionEntrega != 0) {
//            oce.getProveedor().setDireccionEntrega(this.obtenerDireccion(idDireccionEntrega));
//        }
//        oce.setFechaCreacion(utilerias.Utilerias.date2String(rs.getDate("fechaCreacion")));
//        oce.setFechaFinalizacion(utilerias.Utilerias.date2String(rs.getDate("fechaFinalizacion")));
//        oce.setFechaPuesta(utilerias.Utilerias.date2String(rs.getDate("fechaPuesta")));
//        oce.setFechaEntrega(utilerias.Utilerias.date2String(rs.getDate("fechaEntrega")));
//        oce.setEstado(rs.getInt("estado"));
//        switch (rs.getInt("estado")) {
//            case 0:
//                oce.setStatus("Rechazado");
//                break;
//            case 1:
//                oce.setStatus("Activado");
//                break;
//            case 2:
//                oce.setStatus("Ordenado");
//                break;
//            default:
//                String noAprobado = "No Aprobado";
//        }
//        return oce;
//    }
}
