/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.to;

import java.util.ArrayList;
import java.util.Date;
import requisiciones.dominio.RequisicionProducto;


public class TORequisicionEncabezado {
    
    private int idRequisicion;
    private int idEmpresa;
    private String nombreComercial;
    private int idDepto;
    private String Depto;
    private int idUsuario;
    private String Usuario;
    private Date fecha;
    private RequisicionProducto requisicionProducto;
    private ArrayList<RequisicionProducto> requisicionProductos;

    public int getIdRequisicion() {
        return idRequisicion;
    }

    public void setIdRequisicion(int idRequisicion) {
        this.idRequisicion = idRequisicion;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public int getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(int idDepto) {
        this.idDepto = idDepto;
    }

    public String getDepto() {
        return Depto;
    }

    public void setDepto(String Depto) {
        this.Depto = Depto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String Usuario) {
        this.Usuario = Usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public RequisicionProducto getRequisicionProducto() {
        return requisicionProducto;
    }

    public void setRequisicionProducto(RequisicionProducto requisicionProducto) {
        this.requisicionProducto = requisicionProducto;
    }

    public ArrayList<RequisicionProducto> getRequisicionProductos() {
        return requisicionProductos;
    }

    public void setRequisicionProductos(ArrayList<RequisicionProducto> requisicionProductos) {
        this.requisicionProductos = requisicionProductos;
    }

    
}
