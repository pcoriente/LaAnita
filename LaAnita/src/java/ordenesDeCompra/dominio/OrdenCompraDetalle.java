package ordenesDeCompra.dominio;

import cotizaciones.dominio.CotizacionDetalle;
import java.io.Serializable;
import productos.dominio.Producto;


public class OrdenCompraDetalle implements Serializable {

   
    private Producto producto;
    private CotizacionDetalle cotizacionDetalle;
    private int idOrdenCompra;
    private int idEmpaque;
    private String sku;
    private double cantOrdenada;
    private double costoOrdenado;
    private double descuentoProducto;
    private double descuentoProducto2;
    private double desctoConfidencial;
    private int sinCargoBase;
    private int sinCargoCant;
    private double ptjeOferte;
    private double margen;
    private int IdImpuestosGrupo;
    private int idMarca;
            

    public OrdenCompraDetalle() {
    }

   

    public OrdenCompraDetalle(Producto producto, CotizacionDetalle cotizacionDetalle, int idOrdenCompra, int idEmpaque, String sku, double cantOrdenada, double costoOrdenado, double descuentoProducto, double descuentoProducto2, double desctoConfidencial, int sinCargoBase, int sinCargoCant, double ptjeOferte, double margen, int IdImpuestosGrupo, int idMarca) {
        this.producto = producto;
        this.cotizacionDetalle = cotizacionDetalle;
        this.idOrdenCompra = 0;
        this.idEmpaque = 0;
        this.sku ="";
        this.cantOrdenada =0.00;
        this.costoOrdenado=0.00;
        this.descuentoProducto = 0.00;
        this.descuentoProducto2 = 0.00;
        this.desctoConfidencial = 0.00;
        this.sinCargoBase = 0;
        this.sinCargoCant = 0;
        this.ptjeOferte = 0.00;
        this.margen = 0.00;
        this.IdImpuestosGrupo = 0;
        this.idMarca = 0;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public CotizacionDetalle getCotizacionDetalle() {
        return cotizacionDetalle;
    }

    public void setCotizacionDetalle(CotizacionDetalle cotizacionDetalle) {
        this.cotizacionDetalle = cotizacionDetalle;
    }

    public int getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(int idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getCantOrdenada() {
        return cantOrdenada;
    }

    public void setCantOrdenada(double cantOrdenada) {
        this.cantOrdenada = cantOrdenada;
    }

    public double getCostoOrdenado() {
        return costoOrdenado;
    }

    public void setCostoOrdenado(double costoOrdenado) {
        this.costoOrdenado = costoOrdenado;
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

    
    
    public double getDesctoConfidencial() {
        return desctoConfidencial;
    }

    public void setDesctoConfidencial(double desctoConfidencial) {
        this.desctoConfidencial = desctoConfidencial;
    }

    public int getSinCargoBase() {
        return sinCargoBase;
    }

    public void setSinCargoBase(int sinCargoBase) {
        this.sinCargoBase = sinCargoBase;
    }

    public int getSinCargoCant() {
        return sinCargoCant;
    }

    public void setSinCargoCant(int sinCargoCant) {
        this.sinCargoCant = sinCargoCant;
    }

    public double getPtjeOferte() {
        return ptjeOferte;
    }

    public void setPtjeOferte(double ptjeOferte) {
        this.ptjeOferte = ptjeOferte;
    }

    public double getMargen() {
        return margen;
    }

    public void setMargen(double margen) {
        this.margen = margen;
    }

    public int getIdImpuestosGrupo() {
        return IdImpuestosGrupo;
    }

    public void setIdImpuestosGrupo(int IdImpuestosGrupo) {
        this.IdImpuestosGrupo = IdImpuestosGrupo;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

   
}
