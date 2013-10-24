package cotizaciones.dao;

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
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
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

    public ArrayList<OrdenCompraEncabezado> listaOrdenes() throws SQLException {
        ArrayList<OrdenCompraEncabezado> lista = new ArrayList<OrdenCompraEncabezado>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {

            String stringSQL = "select oc.idOrdenCompra, c.idCotizacion, c.idRequisicion, eg.nombreComercial,  co.contribuyente, oc.fechaFinalizacion, oc.fechaPuesta, oc.fechaEntrega, oc.estado from ordencompra oc\n"
                    + "inner join cotizaciones c on c.idCotizacion= oc.idCotizacion\n"
                    + "inner join proveedores p on p.idProveedor = c.idProveedor\n"
                    + "inner join contribuyentes co on co.idContribuyente = p.idContribuyente\n"
                    + "inner join requisiciones r on r.idRequisicion = c.idRequisicion\n"
                    + "inner join empresasGrupo eg on eg.idEmpresa =r.idEmpresa";

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

    private OrdenCompraEncabezado construir(ResultSet rs) throws SQLException {
        OrdenCompraEncabezado oc = new OrdenCompraEncabezado();
        
        
//        ce.setIdRequisicion(rs.getInt("idRequisicion"));
//        ce.setDepto(rs.getString("depto"));
//        ce.setFechaRequisicion(utilerias.Utilerias.date2String(rs.getDate("fechaRequisicion")));
//        ce.setFechaAprobacion(utilerias.Utilerias.date2String(rs.getDate("fechaAprobacion")));
//        ce.setNumCotizaciones(rs.getInt("numCotizaciones"));
//        ce.setNumProductos(rs.getInt("numProductos"));
//        ce.setEstado(rs.getInt("estado"));

        return oc;

    }

 
}
