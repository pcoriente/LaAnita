package cotizaciones.dominio;

import productos.dominio.Producto;

public class CotizacionDetalle {

    private int idCotizacion;
    private int RequisicionProducto;
    private Producto producto;
    private double cantidadAutorizada;
    private double cantidadCotizada;
    private double costoCotizado;
    private double descuentoProducto;
    private double neto;
    private double subtotal;
    private CotizacionEncabezado cotizacionEncabezado;

   
    
    
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
    
    

}
