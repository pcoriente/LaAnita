/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.dominio;

import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
import productos.dominio.Producto;

/**
 *
 * @author daap
 */
public class RequisicionProducto {
    
   private int idRequisicion;
    private Producto producto;
    private int cantidad;
    private int cantidadAutorizada;
   // private CotizacionDetalle cotizacionDetalle = new CotizacionDetalle();
   
    
    public RequisicionProducto(Producto producto) {
        this.producto = producto;
       // this.cantidad=0;
        
    }
    
    

   
    
    

    public RequisicionProducto() {
//        this.neto=0.0;
//        this.subtotal=0.0;
        
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

   

//    public CotizacionDetalle getCotizacionDetalle() {
//        return cotizacionDetalle;
//    }
//
//    public void setCotizacionDetalle(CotizacionDetalle cotizacionDetalle) {
//        this.cotizacionDetalle = cotizacionDetalle;
//    }

   
    
}
