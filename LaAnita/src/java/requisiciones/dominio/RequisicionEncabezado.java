package requisiciones.dominio;

import java.util.ArrayList;

public class RequisicionEncabezado {

    private int idRequisicion;
    private int idEmpresa;
    private String nombreComercial;
    private int idDepto;
    private String Depto;
    private int idUsuario;
    private String Usuario;
    private String fecha;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
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
