package ordenesDeCompra.dominio;

import java.util.Date;

/**
 *
 * @author jsolis
 */
public class Factura {
    private int idFactura;
    private String factura;
    private int idUsuario;
    private Date fecha;
    private int idProveedor;
    private String proveedor;

    public Factura() {
        this.idFactura=0;
        this.factura="";
        this.idUsuario=0;
        this.fecha=new Date();
        this.idProveedor=0;
        this.proveedor="";
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
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

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
}
