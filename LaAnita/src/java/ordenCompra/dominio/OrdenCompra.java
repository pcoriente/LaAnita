package ordenCompra.dominio;

import java.util.Date;
import proveedores.dominio.Proveedor;

/**
 *
 * @author jsolis
 */
public class OrdenCompra {
    private int idDocto;
    private Factura factura;
    private Date fecha;
    private int idCotizacion;
    //private Moneda moneda;
    private double descuento;
    private Proveedor proveedor;
    
    public OrdenCompra() {
        this.idDocto=0;
        this.factura=new Factura();
        this.fecha=new Date();
        this.idCotizacion=0;
        this.descuento=0.00;
        this.proveedor=new Proveedor();
    }

    public int getIdDocto() {
        return idDocto;
    }

    public void setIdDocto(int idDocto) {
        this.idDocto = idDocto;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
