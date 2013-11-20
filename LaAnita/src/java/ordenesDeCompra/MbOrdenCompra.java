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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import ordenDeCompra.Reporte.OrdenCompraReporte;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import org.primefaces.event.SelectEvent;
import proveedores.MbProveedores;
import utilerias.Numero_a_Letra;
import utilerias.Utilerias;

@Named(value = "mbOrdenCompra")
@SessionScoped
public class MbOrdenCompra implements Serializable {

    @ManagedProperty(value = "#{mbProveedores}")
    private MbProveedores mbProveedores;
    private OrdenCompraEncabezado ordenCompraEncabezado;
    @ManagedProperty(value = "#{mbCotizaciones}")
    private MbCotizaciones mbCotizaciones;
    private ArrayList<OrdenCompraEncabezado> listaOrdenesEncabezado;
    private OrdenCompraEncabezado ordenElegida;
    private ArrayList<OrdenCompraDetalle> listaOrdenDetalle;
    private OrdenCompraDetalle ordenCompraDetalle;
    private double subtotalGeneral;
    private double sumaDescuentosProductos;
    private double sumaDescuentosGenerales;
    private double subtotalBruto;
    private double impuesto;
    private double total;
    private String subtotF;
    private String descF;
    private String subtotalBrutoF;
    private String impF;
    private String totalF;
    private String sumaDescuentosProductosF;
    private String sumaDescuentosGeneralesF;
    private double iva = 0.16;
    private double sumaDescuentoTotales;
    private String sumaDescuentosTotalesF;
    DAOOrdenDeCompra daoO = new DAOOrdenDeCompra();

    public MbOrdenCompra() throws NamingException {
        this.mbProveedores = new MbProveedores();
        this.ordenCompraEncabezado = new OrdenCompraEncabezado();
        this.mbCotizaciones = new MbCotizaciones();
        this.ordenCompraDetalle = new OrdenCompraDetalle();
        this.ordenElegida = new OrdenCompraEncabezado();

    }

    //M E T O D O S 
    private void cargaOrdenesEncabezado() {
        try {
            DAOOrdenDeCompra daoOC = new DAOOrdenDeCompra();
            this.listaOrdenesEncabezado = daoOC.listaOrdenes();
        } catch (NamingException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void obtenerDetalleOrdenCompra() throws SQLException {
        listaOrdenDetalle = new ArrayList<OrdenCompraDetalle>();
        this.subtotalGeneral = 0;

        try {
            int idOC = ordenElegida.getIdOrdenCompra();
            DAOOrdenDeCompra daoOC = new DAOOrdenDeCompra();
            ArrayList<OrdenCompraDetalle> lista = daoOC.consultaOrdenCompra(idOC);
            for (OrdenCompraDetalle d : lista) {
                listaOrdenDetalle.add(d);
                this.calculosOrdenCompra(d.getProducto().getIdProducto());
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dameOrdenCompra(SelectEvent event) throws SQLException {
        this.ordenElegida = (OrdenCompraEncabezado) event.getObject();
        obtenerDetalleOrdenCompra();
    }

    public void calculosOrdenCompra(int idProd) {

        try {

            for (OrdenCompraDetalle e : listaOrdenDetalle) {
                int idProducto = e.getProducto().getIdProducto();
                if (idProd == idProducto) {
                    double neto = e.getCostoOrdenado() - (e.getCostoOrdenado() * (e.getDescuentoProducto() / 100));
                    double neto2 = neto - neto * (e.getDescuentoProducto2() / 100);
                    e.setNeto(neto2);
//                    e.setSubtotal(e.getNeto() * e.getCantidadSolicitada());
                    e.setSubtotal(e.getCantidadSolicitada() * e.getCostoOrdenado());
                    daoO.actualizarCantidadOrdenada(e.getIdOrdenCompra(), idProd, e.getCantidadSolicitada());
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calculoSubtotalGeneral() {
        subtotalGeneral = 0;
        sumaDescuentosProductos = 0;
        sumaDescuentosGenerales = 0;
        double descProds = 0;
        double descuentoC = 0;
        double descuentoPP = 0;
        double subt = 0;

        if (this.listaOrdenDetalle != null) {
            for (OrdenCompraDetalle oc : listaOrdenDetalle) {
                subtotalGeneral += oc.getSubtotal();
                descProds += (oc.getCantidadSolicitada() * (oc.getCostoOrdenado() - oc.getNeto()));
            }
            setSumaDescuentosProductos(descProds);
            double dc = this.ordenElegida.getDesctoComercial();
            double dpp = this.ordenElegida.getDesctoProntoPago();
            subt = subtotalGeneral;
            descuentoC = subt * (dc / 100);

            subt = subt - descuentoC;

            descuentoPP = subt * (dpp / 100);
            double descuentosGenerales = descuentoC + descuentoPP;

            setSumaDescuentosGenerales(descuentosGenerales);

            //    setSumaDescuentoTotales(sumaDescuentosProductos + sumaDescuentosGenerales);

        } else {
            System.out.println("No hay valores en el arraylist");
        }
    }

    public void calcularSumaDescuentosTotales() {
        sumaDescuentoTotales = 0;
        setSumaDescuentoTotales(sumaDescuentosProductos + sumaDescuentosGenerales);
    }

    public void calcularSubtotalBruto() {
        subtotalBruto = 0;
        //  subtotalBruto = this.subtotalGeneral - this.sumaDescuentosGenerales;
        subtotalBruto = this.subtotalGeneral - this.sumaDescuentoTotales;
    }

    public void calculoIVA() {
        impuesto = 0;
        double desc = this.subtotalBruto;
        if (desc > 0) {
            impuesto = (desc) * iva;
        } else {
            impuesto = 0;
        }
    }

    public void calculoTotal() {
        total = 0;
        double desc = subtotalBruto;
        if (desc > 0) {
            total = (desc) + impuesto;
        } else {
            total = 0;
        }
    }

    public void guardarOrden(int idOrden, int estado) throws NamingException {
        FacesMessage msg = null;
        try {
            if (estado == 1) {
                daoO.procesarOrdenCompra(idOrden);
                this.setListaOrdenesEncabezado(null);
                this.cargaOrdenesEncabezado();

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se ha guardado con satisfactoriamente...");
            } else if (estado == 2) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "La orden se ha registrado con anterioridad");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se realizó el registro de la orden de compra..");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void cancelarOrden(int idOrden, int estado) throws NamingException {
        FacesMessage msg = null;
        try {
            if (estado == 0) {
                daoO.cancelarOrdenCompra(idOrden);
                this.setListaOrdenesEncabezado(null);
                this.cargaOrdenesEncabezado();

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se ha CANCELADO");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se realizó la cancelación de la orden de compra..");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);


    }

    public String irMenu() throws NamingException {
        // this.limpiaRequisicion();
        String navega = "menuOrdenesDeCompra.xhtml";
        return navega;
    }

    // GET Y SETS
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
            this.cargaOrdenesEncabezado();
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

    public double getSubtotalGeneral() {
        calculoSubtotalGeneral();
        return subtotalGeneral;
    }

    public void setSubtotalGeneral(double subtotalGeneral) {
        this.subtotalGeneral = subtotalGeneral;
    }

    public double getSumaDescuentosProductos() {
        return sumaDescuentosProductos;
    }

    public void setSumaDescuentosProductos(double sumaDescuentosProductos) {
        this.sumaDescuentosProductos = sumaDescuentosProductos;
    }

    public double getImpuesto() {
        calculoIVA();
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        calculoTotal();
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getSubtotF() {
        //     Utilerias u = new Utilerias();
        //       Numero_a_Letra num = new Numero_a_Letra();
        subtotF = utilerias.Utilerias.formatoMonedas(this.getSubtotalGeneral());
        //     String letra = u.quitarComasNumero(subtotF);
        //      letra = num.Convertir(letra, true);
        //    System.out.println("Letras: " + letra);
        return subtotF;
    }

    public String getDescF() {
        descF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentosProductos());
        return descF;
    }

    public String getImpF() { //IVA
        impF = utilerias.Utilerias.formatoMonedas(this.getImpuesto());
        return impF;
    }

    public String getTotalF() {
        totalF = utilerias.Utilerias.formatoMonedas(this.getTotal());
        return totalF;
    }

    public double getSumaDescuentosGenerales() {
        return sumaDescuentosGenerales;
    }

    public void setSumaDescuentosGenerales(double sumaDescuentosGenerales) {
        this.sumaDescuentosGenerales = sumaDescuentosGenerales;
    }

    public String getSumaDescuentosProductosF() {
        sumaDescuentosProductosF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentosProductos());
        return sumaDescuentosProductosF;
    }

    public String getSumaDescuentosGeneralesF() {
        sumaDescuentosGeneralesF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentosGenerales());
        return sumaDescuentosGeneralesF;
    }

    public double getSubtotalBruto() {
        calcularSubtotalBruto();
        return subtotalBruto;
    }

    public void setSubtotalBruto(double subtotalBruto) {
        this.subtotalBruto = subtotalBruto;
    }

    public String getSubtotalBrutoF() {
        subtotalBrutoF = utilerias.Utilerias.formatoMonedas(this.getSubtotalBruto());
        return subtotalBrutoF;
    }

    public double getSumaDescuentoTotales() {
        calcularSumaDescuentosTotales();
        return sumaDescuentoTotales;
    }

    public void setSumaDescuentoTotales(double sumaDescuentoTotales) {
        this.sumaDescuentoTotales = sumaDescuentoTotales;
    }

    public String getSumaDescuentosTotalesF() {
        sumaDescuentosTotalesF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentoTotales());

        return sumaDescuentosTotalesF;
    }

    public void generarReporte() {
        OrdenCompraReporte reporte = new OrdenCompraReporte();
        reporte.generarReporte(listaOrdenDetalle);

    }
}
