package requisiciones.dominio;

import productos.dominio.Producto;

public class RequisicionProducto {
    private int idRequisicion;
    private Producto producto;
    private int cantidad;
    private int cantidadAutorizada;

    public RequisicionProducto(Producto producto) {
        this.producto = producto;
    }

    public RequisicionProducto() {
    }

    public int getIdRequisicion() {
        return idRequisicion;
    }

    public void setIdRequisicion(int idRequisicion) {
        this.idRequisicion = idRequisicion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidadAutorizada() {
        return cantidadAutorizada;
    }

    public void setCantidadAutorizada(int cantidadAutorizada) {
        this.cantidadAutorizada = cantidadAutorizada;
    }
}
