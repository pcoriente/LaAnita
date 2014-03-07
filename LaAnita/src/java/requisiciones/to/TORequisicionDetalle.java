/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.to;

public class TORequisicionDetalle {
    
   
    private int idRequisicion;
    private int idEmpaque;
    private int cantidad;
    private int cantidadAutorizada;

    
     public int getIdRequisicion() {
        return idRequisicion;
    }

    public void setIdRequisicion(int idRequisicion) {
        this.idRequisicion = idRequisicion;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
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
