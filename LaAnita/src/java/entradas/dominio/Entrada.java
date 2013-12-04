package entradas.dominio;

import java.util.Date;

/**
 *
 * @author jsolis
 */
public class Entrada {
    private int idEntrada;
    private int idProveedor;
    private int idImpuestoZona;
    private int idEmpresa;
    private int idAlmacen;
    private int idFactura;
    private int idOrdenCompra;
    private double desctoComercial;
    private double desctoProntoPago;
    private Date fecha;
    private int idUsuario;
    private double subTotal;
    private double descuento;
    private double impuesto;
    private double total;

    public Entrada() {
        this.idEntrada=0;
        this.idProveedor=0;
        this.idImpuestoZona=0;
        this.desctoComercial=0.00;
        this.desctoProntoPago=0.00;
        this.fecha=new Date();
        this.subTotal=0.00;
        this.descuento=0.00;
        this.impuesto=0.00;
        this.total=0.00;
        this.idUsuario=0;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public int getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(int idEntrada) {
        this.idEntrada = idEntrada;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public double getDesctoComercial() {
        return desctoComercial;
    }

    public void setDesctoComercial(double desctoComercial) {
        this.desctoComercial = desctoComercial;
    }

    public double getDesctoProntoPago() {
        return desctoProntoPago;
    }

    public void setDesctoProntoPago(double desctoProntoPago) {
        this.desctoProntoPago = desctoProntoPago;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(int idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public int getIdImpuestoZona() {
        return idImpuestoZona;
    }

    public void setIdImpuestoZona(int idImpuestoZona) {
        this.idImpuestoZona = idImpuestoZona;
    }
}
