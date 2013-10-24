package ordenesDeCompra;

import cotizaciones.MbCotizaciones;
import cotizaciones.dao.DAOOrdenDeCompra;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedProperty;
import javax.naming.NamingException;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import ordenesDeCompra.dominio.ProductoOC;
import proveedores.MbProveedores;

/**
 *
 * @author jsolis
 */
@Named(value = "mbOrdenCompra")
@SessionScoped
public class MbOrdenCompra implements Serializable {

    @ManagedProperty(value = "#{mbFacturas}")
    private MbFacturas mbFacturas;
    @ManagedProperty(value = "#{mbProveedores}")
    private MbProveedores mbProveedores;
    private OrdenCompraEncabezado ordenCompraEncabezado;
    private ArrayList<ProductoOC> productos;
    @ManagedProperty(value = "#{mbCotizaciones}")
    private MbCotizaciones mbCotizaciones;
    private ArrayList<OrdenCompraEncabezado> listaOrdenesEncabezado;

    public MbOrdenCompra() {
        this.mbFacturas = new MbFacturas();
        this.mbProveedores = new MbProveedores();
        this.ordenCompraEncabezado = new OrdenCompraEncabezado();
        this.productos = new ArrayList<ProductoOC>();
        this.mbCotizaciones = new MbCotizaciones();
    }

    //M E T O D O S 
    private void cargaOrdenesEncabezado() throws NamingException, SQLException {

        this.listaOrdenesEncabezado = new ArrayList<OrdenCompraEncabezado>();
        DAOOrdenDeCompra daoOC = new DAOOrdenDeCompra();
        ArrayList<OrdenCompraEncabezado> lista = daoOC.listaOrdenes();
        for (OrdenCompraEncabezado d : lista) {
            listaOrdenesEncabezado.add(d);
        }
    }
    // GET Y SETS

    public MbFacturas getMbFacturas() {
        return mbFacturas;
    }

    public void setMbFacturas(MbFacturas mbFacturas) {
        this.mbFacturas = mbFacturas;
    }

    public MbProveedores getMbProveedores() {
        return mbProveedores;
    }

    public void setMbProveedores(MbProveedores mbProveedores) {
        this.mbProveedores = mbProveedores;
    }

    

    public ArrayList<ProductoOC> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<ProductoOC> productos) {
        this.productos = productos;
    }

    public MbCotizaciones getMbCotizaciones() {
        return mbCotizaciones;
    }

    public void setMbCotizaciones(MbCotizaciones mbCotizaciones) {
        this.mbCotizaciones = mbCotizaciones;
    }

    public OrdenCompraEncabezado getOrdenCompraEncabezado() {
        return ordenCompraEncabezado;
    }

    public void setOrdenCompraEncabezado(OrdenCompraEncabezado ordenCompraEncabezado) {
        this.ordenCompraEncabezado = ordenCompraEncabezado;
    }

    public ArrayList<OrdenCompraEncabezado> getListaOrdenesEncabezado() {
        return listaOrdenesEncabezado;
    }

    public void setListaOrdenesEncabezado(ArrayList<OrdenCompraEncabezado> listaOrdenesEncabezado) {
        this.listaOrdenesEncabezado = listaOrdenesEncabezado;
    }

    
}
