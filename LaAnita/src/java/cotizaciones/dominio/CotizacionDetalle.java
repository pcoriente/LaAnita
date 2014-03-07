package cotizaciones.dominio;

import java.io.Serializable;
import productos.dominio.Empaque;
import proveedores.dominio.Proveedor;
import requisiciones.dominio.RequisicionDetalle;

public class CotizacionDetalle implements Serializable {

    //  private RequisicionDetalle requisicionDetalle;
    private CotizacionEncabezado cotizacionEncabezado;
    private Proveedor proveedor = new Proveedor();
    private int idCotizacion;  //??
    private int idRequisicion; //??
    private Empaque empaque;  //??
    private String sku;
    private double cantidadAutorizada;
    private double cantidadCotizada;
    private double costoCotizado;
    private double descuentoProducto;
    private double descuentoProducto2; //Solicitud del Dr.
    private double neto;
    private double subtotal;
    private double iva;
    private double total;
    private String costoCotizadoF;
    private String netoF;
    private String subtotalF;
    private String ivaF;
    private String totalF;

//    public RequisicionDetalle getRequisicionDetalle() {
//        return requisicionDetalle;
//    }
//
//    public void setRequisicionDetalle(RequisicionDetalle requisicionDetalle) {
//        this.requisicionDetalle = requisicionDetalle;
//    }
    public CotizacionEncabezado getCotizacionEncabezado() {
        return cotizacionEncabezado;
    }

    public void setCotizacionEncabezado(CotizacionEncabezado cotizacionEncabezado) {
        this.cotizacionEncabezado = cotizacionEncabezado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getCantidadAutorizada() {
        return cantidadAutorizada;
    }

    public void setCantidadAutorizada(double cantidadAutorizada) {
        this.cantidadAutorizada = cantidadAutorizada;
    }

    public double getCantidadCotizada() {
        return cantidadCotizada;
    }

    public void setCantidadCotizada(double cantidadCotizada) {
        this.cantidadCotizada = cantidadCotizada;
    }

    public double getCostoCotizado() {
        return costoCotizado;
    }

    public void setCostoCotizado(double costoCotizado) {
        this.costoCotizado = costoCotizado;
    }

    public double getDescuentoProducto() {
        return descuentoProducto;
    }

    public void setDescuentoProducto(double descuentoProducto) {
        this.descuentoProducto = descuentoProducto;
    }

    public double getDescuentoProducto2() {
        return descuentoProducto2;
    }

    public void setDescuentoProducto2(double descuentoProducto2) {
        this.descuentoProducto2 = descuentoProducto2;
    }

    public double getNeto() {
        return neto;
    }

    public void setNeto(double neto) {
        this.neto = neto;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCostoCotizadoF() {
        costoCotizadoF = utilerias.Utilerias.formatoMonedas(this.getCostoCotizado());
        return costoCotizadoF;
    }

    public void setCostoCotizadoF(String costoCotizadoF) {
        this.costoCotizadoF = costoCotizadoF;
    }

    public String getNetoF() {
        netoF = utilerias.Utilerias.formatoMonedas(this.getNeto());
        return netoF;
    }

    public void setNetoF(String netoF) {
        this.netoF = netoF;
    }

    public String getSubtotalF() {
        subtotalF = utilerias.Utilerias.formatoMonedas(this.subtotal);
        return subtotalF;
    }

    public void setSubtotalF(String subtotalF) {
        this.subtotalF = subtotalF;
    }

    public String getIvaF() {
        ivaF = utilerias.Utilerias.formatoMonedas(this.getIva());
        return ivaF;
    }

    public void setIvaF(String ivaF) {
        this.ivaF = ivaF;
    }

    public String getTotalF() {
        totalF = utilerias.Utilerias.formatoMonedas(this.getTotal());
        return totalF;
    }

    public void setTotalF(String totalF) {
        this.totalF = totalF;
    }

    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public int getIdRequisicion() {
        return idRequisicion;
    }

    public void setIdRequisicion(int idRequisicion) {
        this.idRequisicion = idRequisicion;
    }

    public Empaque getEmpaque() {
        return empaque;
    }

    public void setEmpaque(Empaque empaque) {
        this.empaque = empaque;
    }
}
