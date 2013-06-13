package direccion.dominio;

/**
 *
 * @author Julio
 */
public class Direccion {
    private int idDireccion;
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private String referencia;
    private Pais pais;
    private String codigoPostal;
    private String estado;
    private String municipio;
    private String localidad;
    private String colonia;
    private String numeroLocalizacion;
    private Asentamiento selAsentamiento;

    @Override
    public String toString() {
        return calle.isEmpty() ? "" : (" CALLE: "+calle + "\n NUMERO: " + numeroExterior + "\n NO. INTERIOR: " + numeroInterior
                + "\n REFERENCIA: "+ referencia
                + "\n MUNICIPIO: " + municipio + "\n ESTADO: "+ estado + "\n PAIS: " + pais.toString()
                + (localidad.isEmpty() ? "": "\n LOCALIDAD: "+localidad)
                + "\n COLONIA: " + (colonia.isEmpty() ? ": "+codigoPostal : colonia + "\n C.P.:"+codigoPostal));
    }


    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getNumeroExterior() {
        return numeroExterior;
    }

    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public String getNumeroLocalizacion() {
        return numeroLocalizacion;
    }

    public void setNumeroLocalizacion(String numeroLocalizacion) {
        this.numeroLocalizacion = numeroLocalizacion;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Asentamiento getSelAsentamiento() {
        return selAsentamiento;
    }

    public void setSelAsentamiento(Asentamiento selAsentamiento) {
        this.selAsentamiento = selAsentamiento;
    }
}
