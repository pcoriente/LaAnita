/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.dominio;

import productos.dominio.Producto;

/**
 *
 * @author daap
 */
public class RequisicionProducto {
    
   
    private Producto producto;
    private int cantidad;
    
    public RequisicionProducto(Producto producto) {
        this.producto = producto;
        this.cantidad=0;
        
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
    
}
