package ordenesDeCompra;

import cotizaciones.MbCotizaciones;
import cotizaciones.dao.DAOOrdenDeCompra;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.naming.NamingException;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import org.primefaces.event.SelectEvent;
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
    @ManagedProperty(value = "#{mbCotizaciones}")
    private MbCotizaciones mbCotizaciones;
    private ArrayList<OrdenCompraEncabezado> listaOrdenesEncabezado;
    private OrdenCompraEncabezado ordenElegida = new OrdenCompraEncabezado();
    private ArrayList<OrdenCompraDetalle> listaOrdenDetalle;
    private OrdenCompraDetalle ordenCompraDetalle;

    public MbOrdenCompra() {
        this.mbFacturas = new MbFacturas();
        this.mbProveedores = new MbProveedores();
        this.ordenCompraEncabezado = new OrdenCompraEncabezado();
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

    public void dameOrdenCompra(SelectEvent event) throws SQLException {
       this.ordenElegida=(OrdenCompraEncabezado) event.getObject();
        
        listaOrdenDetalle = new ArrayList<OrdenCompraDetalle>();
        try {
            
            int idOC =  ordenElegida.getIdOrdenCompra();
            DAOOrdenDeCompra daoOC = new DAOOrdenDeCompra();
            ArrayList<OrdenCompraDetalle> lista = daoOC.consultaOrdenCompra(idOC);

            for (OrdenCompraDetalle d : lista) {
                listaOrdenDetalle.add(d);

            }

        } catch (NamingException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
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

    public ArrayList<OrdenCompraEncabezado> getListaOrdenesEncabezado() throws NamingException {
        if (listaOrdenesEncabezado == null) {
            try {
                this.cargaOrdenesEncabezado();
            } catch (SQLException ex) {
                Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listaOrdenesEncabezado;
    }

    public void setListaOrdenesEncabezado(ArrayList<OrdenCompraEncabezado> listaOrdenesEncabezado) {
        this.listaOrdenesEncabezado = listaOrdenesEncabezado;
    }

    public OrdenCompraEncabezado getOrdenElegida() {
        return ordenElegida;
    }

    public void setOrdenElegida(OrdenCompraEncabezado ordenElegida) {
        this.ordenElegida = ordenElegida;
    }

    

    public ArrayList<OrdenCompraDetalle> getListaOrdenDetalle() {
        return listaOrdenDetalle;
    }

    public void setListaOrdenDetalle(ArrayList<OrdenCompraDetalle> listaOrdenDetalle) {
        this.listaOrdenDetalle = listaOrdenDetalle;
    }

    public OrdenCompraDetalle getOrdenCompraDetalle() {
        return ordenCompraDetalle;
    }

    public void setOrdenCompraDetalle(OrdenCompraDetalle ordenCompraDetalle) {
        this.ordenCompraDetalle = ordenCompraDetalle;
    }
}
