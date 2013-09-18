package proveedores.dominio;

import contribuyentes.Contribuyente;
import contribuyentes.TOContribuyente;
import direccion.dominio.Direccion;
import impuestos.dominio.ImpuestoZona;

/**
 *
 * @author Julio
 */
public class Proveedor {
    private int idProveedor;
    private Contribuyente contribuyente;
    private Clasificacion clasificacion;
    private SubClasificacion subClasificacion;
    private TipoOperacion tipoOperacion;
    private TipoTercero tipoTercero;
    private ImpuestoZona impuestoZona;
    private Direccion direccionFiscal;
    private Direccion direccionEntrega;
    private String telefono;
    private String fax;
    private String correo;
    private int diasCredito;
    private double limiteCredito;
    private String fechaAlta;

    public Proveedor() {
        this.idProveedor=0;
        this.contribuyente=new Contribuyente();
        this.clasificacion=new Clasificacion();
        this.subClasificacion=new SubClasificacion();
        this.tipoOperacion=new TipoOperacion();
        this.tipoTercero=new TipoTercero();
        this.impuestoZona=new ImpuestoZona(0, "");
        this.direccionEntrega=new Direccion();
        this.telefono="";
        this.fax="";
        this.correo="";
        this.diasCredito=0;
        this.limiteCredito=0.00;
        this.fechaAlta="";
    }
    
    @Override
    public String toString() {
        return this.contribuyente.getContribuyente();
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public TipoTercero getTipoTercero() {
        return tipoTercero;
    }

    public void setTipoTercero(TipoTercero tipoTercero) {
        this.tipoTercero = tipoTercero;
    }

    public ImpuestoZona getImpuestoZona() {
        return impuestoZona;
    }

    public void setImpuestoZona(ImpuestoZona impuestoZona) {
        this.impuestoZona = impuestoZona;
    }

    public Direccion getDireccionFiscal() {
        return direccionFiscal;
    }

    public void setDireccionFiscal(Direccion direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    public Direccion getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(Direccion direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getDiasCredito() {
        return diasCredito;
    }

    public void setDiasCredito(int diasCredito) {
        this.diasCredito = diasCredito;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public SubClasificacion getSubClasificacion() {
        return subClasificacion;
    }

    public void setSubClasificacion(SubClasificacion subClasificacion) {
        this.subClasificacion = subClasificacion;
    }
}
