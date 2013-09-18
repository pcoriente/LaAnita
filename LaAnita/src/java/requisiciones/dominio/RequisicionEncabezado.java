package requisiciones.dominio;

import empresas.dominio.MiniEmpresa;
import usuarios.dominio.Usuario;

public class RequisicionEncabezado {

    private int idRequisicion;
    private int idEmpresa;
    private int idDepto;
    private int idSolicito;
    private int IdAprobo;
    private String fechaRequisicion;
    private String fechaAprobacion;
    private int status;
    private String observaciones;
    private MiniEmpresa miniEmpresa;
    private Depto depto;
    private Usuario Usuario;
    
    private String empleadoAprobo;
    

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

    public int getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(int idDepto) {
        this.idDepto = idDepto;
    }

    public int getIdSolicito() {
        return idSolicito;
    }

    public void setIdSolicito(int idSolicito) {
        this.idSolicito = idSolicito;
    }

    public int getIdAprobo() {
        return IdAprobo;
    }

    public void setIdAprobo(int IdAprobo) {
        this.IdAprobo = IdAprobo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFechaRequisicion() {
        return fechaRequisicion;
    }

    public void setFechaRequisicion(String fechaRequisicion) {
        this.fechaRequisicion = fechaRequisicion;
    }

    public String getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(String fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

   

    public MiniEmpresa getMiniEmpresa() {
        return miniEmpresa;
    }

    public void setMiniEmpresa(MiniEmpresa miniEmpresa) {
        this.miniEmpresa = miniEmpresa;
    }

    public Depto getDepto() {
        return depto;
    }

    public void setDepto(Depto depto) {
        this.depto = depto;
    }

    public Usuario getUsuario() {
        return Usuario;
    }

    public void setUsuario(Usuario Usuario) {
        this.Usuario = Usuario;
    }

//    public RequisicionProducto getRd() {
//        return rd;
//    }
//
//    public void setRd(RequisicionProducto rd) {
//        this.rd = rd;
//    }
//
//    public UsuarioSesion getUsuarioSesion() {
//        return usuarioSesion;
//    }
//
//    public void setUsuarioSesion(UsuarioSesion usuarioSesion) {
//        this.usuarioSesion = usuarioSesion;
 //   }

    public String getEmpleadoAprobo() {
        return empleadoAprobo;
    }

    public void setEmpleadoAprobo(String empleadoAprobo) {
        this.empleadoAprobo = empleadoAprobo;
    }
    
    
    
    
}
