/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cotizaciones.to;

/**
 *
 * @author daap
 */
public class TOCotizacionDetalle {
    private int idCotizacion;
    private int idRequisicion;
    private int cantidadCotizada;
    private int costoCotizado;
    private int descuentoProducto;

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

    public int getCantidadCotizada() {
        return cantidadCotizada;
    }

    public void setCantidadCotizada(int cantidadCotizada) {
        this.cantidadCotizada = cantidadCotizada;
    }

    public int getCostoCotizado() {
        return costoCotizado;
    }

    public void setCostoCotizado(int costoCotizado) {
        this.costoCotizado = costoCotizado;
    }

    public int getDescuentoProducto() {
        return descuentoProducto;
    }

    public void setDescuentoProducto(int descuentoProducto) {
        this.descuentoProducto = descuentoProducto;
    }
    
    
    
}
