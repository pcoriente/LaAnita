package ordenCompra.dominio;

import java.io.Serializable;
import productos.dominio.Producto;

/**
 *
 * @author jsolis
 */
public class ProductoOC implements Serializable {
    private int idDocto;
    private Producto producto;
    private double cantOrdenada;
    private double cantFacturada;
    private double cantRecibida;
    private double costo;
    private double descuento;
    private double cantSinCargo;
    private double costoNeto;

    public ProductoOC(int idDocto, Producto producto) {
        this.idDocto=idDocto;
        this.producto=producto;
        this.cantOrdenada=0.00;
        this.cantFacturada=0.00;
        this.cantRecibida=0.00;
        this.costo=0.00;
        this.descuento=0.00;
        this.cantSinCargo=0.00;
        this.costoNeto=0.00;
    }

    public int getIdDocto() {
        return idDocto;
    }

    public void setIdDocto(int idDocto) {
        this.idDocto = idDocto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantOrdenada() {
        return cantOrdenada;
    }

    public void setCantOrdenada(double cantOrdenada) {
        this.cantOrdenada = cantOrdenada;
    }

    public double getCantFacturada() {
        return cantFacturada;
    }

    public void setCantFacturada(double cantFacturada) {
        this.cantFacturada = cantFacturada;
    }

    public double getCantRecibida() {
        return cantRecibida;
    }

    public void setCantRecibida(double cantRecibida) {
        this.cantRecibida = cantRecibida;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getCantSinCargo() {
        return cantSinCargo;
    }

    public void setCantSinCargo(double cantSinCargo) {
        this.cantSinCargo = cantSinCargo;
    }

    public double getCostoNeto() {
        return costoNeto;
    }

    public void setCostoNeto(double costoNeto) {
        this.costoNeto = costoNeto;
    }
}
