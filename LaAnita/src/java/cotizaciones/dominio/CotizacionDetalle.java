package cotizaciones.dominio;

import java.io.Serializable;
import productos.dominio.Producto;
import proveedores.dominio.Proveedor;

public class CotizacionDetalle implements Serializable {

    private int idCotizacion;
    private int RequisicionProducto;
    private String sku;
    private Producto producto;
    private double cantidadAutorizada;
    private double cantidadCotizada;
    private double costoCotizado;
    private double descuentoProducto;
    private double descuentoProducto2; //Solicitud del Dr.
    private double neto;
    private double subtotal;
    private double iva;
    private double total;
    private CotizacionEncabezado cotizacionEncabezado;
    private Proveedor proveedor = new Proveedor();
    private String costoCotizadoF;
    private String netoF;
    private String subtotalF;
    private String ivaF;
    private String totalF;

    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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

    public double getCantidadAutorizada() {
        return cantidadAutorizada;
    }

    public void setCantidadAutorizada(double cantidadAutorizada) {
        this.cantidadAutorizada = cantidadAutorizada;
    }

    public int getRequisicionProducto() {
        return RequisicionProducto;
    }

    public void setRequisicionProducto(int RequisicionProducto) {
        this.RequisicionProducto = RequisicionProducto;
    }

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

    public String getNetoF() {
        netoF = utilerias.Utilerias.formatoMonedas(this.getNeto());
        return netoF;
    }

    public String getSubtotalF() {
         subtotalF = utilerias.Utilerias.formatoMonedas(this.subtotal);
        return subtotalF;
    }

    public String getIvaF() {
         ivaF = utilerias.Utilerias.formatoMonedas(this.getIva());
        return ivaF;
    }

    public String getTotalF() {
         totalF = utilerias.Utilerias.formatoMonedas(this.getTotal());
        return totalF;
    }
    
    
}
