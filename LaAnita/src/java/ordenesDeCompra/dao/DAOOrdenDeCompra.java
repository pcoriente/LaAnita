package cotizaciones.dao;

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
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import productos.dao.DAOProductos;
import proveedores.dao.DAOProveedores;
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
            /*
            String stringSQL = "select oc.idOrdenCompra, c.idCotizacion, c.idRequisicion, eg.nombreComercial,  p.idProveedor, c.descuentoCotizacion, c.descuentoProntoPago, isnull(d.idDireccion, 0) as idDireccion, p.idDireccionEntrega, oc.fechaCreacion, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado\n"
                    + "from ordencompra oc\n" +
"                    left join cotizaciones c on c.idCotizacion= oc.idCotizacion\n" +
"                    left join proveedores p on p.idProveedor = c.idProveedor\n" +
"                    left join contribuyentes co on co.idContribuyente = p.idContribuyente\n" +
"                    left join requisiciones r on r.idRequisicion = c.idRequisicion\n" +
"                    left join empresasGrupo eg on eg.idEmpresa =r.idEmpresa\n" +
"                    left join direcciones d on d.idDireccion = co.idDireccion";
* */
            String stringSQL="select oc.idOrdenCompra, oc.idCotizacion, oc.desctoComercial, oc.desctoProntoPago, oc.fechaCreacion, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado "
                    + "FROM ordenCompra oc";
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

        DAOProveedores daoP = new DAOProveedores();
        
      

        oce.setIdOrdenCompra(rs.getInt("idOrdenCompra"));
        oce.setIdCotizacion(rs.getInt("idCotizacion"));
        //oce.setIdRequisicion(rs.getInt("idRequisicion"));
        //oce.setNombreComercial(rs.getString("nombreComercial"));
        //oce.setProveedor(daoP.obtenerProveedor(rs.getInt("idProveedor")));
        oce.setDesctoComercial(rs.getDouble("desctoComercial"));
        oce.setDesctoProntoPago(rs.getDouble("desctoProntoPago"));
        //oce.getProveedor().getContribuyente().setDireccion( this.obtenerDireccion(rs.getInt("idDireccion")));
        //oce.getProveedor().setDireccionEntrega(this.obtenerDireccion(rs.getInt("idDireccionEntrega")));

        oce.setFechaCreacion(utilerias.Utilerias.date2String(rs.getDate("fechaCreacion")));
        oce.setFechaFinalizacion(utilerias.Utilerias.date2String(rs.getDate("fechaFinalizacion")));
        oce.setFechaPuesta(utilerias.Utilerias.date2String(rs.getDate("fechaPuesta")));
        oce.setFechaEntrega(utilerias.Utilerias.date2String(rs.getDate("fechaEntrega")));
        oce.setEstado(rs.getInt("estado"));
        return oce;
    }

    public ArrayList<OrdenCompraDetalle> consultaOrdenCompra(int idOC) throws SQLException, NamingException {
        ArrayList<OrdenCompraDetalle> lista = new ArrayList<OrdenCompraDetalle>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select oc.idOrdenCompra, oc.idCotizacion, ocd.idProducto, ocd.cantOrdenada, ocd.costoOrdenado, ocd.descuentoProducto, ocd.descuentoProducto2\n"
                    + "                    from ordencompra oc\n"
                    + "                    inner join ordenCompraDetalle ocd on ocd.idOrdenCompra = oc.idOrdenCompra\n"
                    + "                    where oc.idOrdenCompra=" + idOC;

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
        ocd.setIdOrdenCompra(rs.getInt("idOrdenCompra"));
        ocd.setCantOrdenada(rs.getDouble("cantOrdenada"));
        ocd.setCantidadSolicitada(rs.getDouble("cantOrdenada"));
        ocd.setCostoOrdenado(rs.getDouble("costoOrdenado"));
        ocd.setDescuentoProducto(rs.getDouble("descuentoProducto"));
        ocd.setDescuentoProducto2(rs.getDouble("descuentoProducto2"));
        return ocd;
    }
    
    public Direccion obtenerDireccion(int idDireccion) throws SQLException {
        Direccion toDir=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM direcciones WHERE idDireccion="+idDireccion);
            if(rs.next()) toDir=construir(rs);
        } finally {
            cn.close();
        }
        return toDir;
    }
    
    private Direccion construir(ResultSet rs) throws SQLException {
        Direccion toDir=new Direccion();
        toDir.setIdDireccion(rs.getInt("idDireccion"));
        toDir.setCalle(rs.getString("calle"));
        toDir.setNumeroExterior(rs.getString("numeroExterior"));
        toDir.setNumeroInterior(rs.getString("numeroInterior"));
        toDir.setReferencia(rs.getString("referencia"));
        toDir.getPais().setIdPais(rs.getInt("idPais"));
    //    toDir.setIdPais(rs.getInt("idPais"));
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
    
    
}