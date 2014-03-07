package entradas.to;

import java.util.Date;

/**
 *
 * @author jesc
 */
public class TOComprobante {
    private int idComprobante;
    private int idAlmacen;
    private int idProveedor;
    private int tipoComprobante;
    private String serie;
    private String numero;
    private Date fecha;
    private boolean cerradaOficina;
    private boolean cerradaAlmacen;

    public TOComprobante() {
        this.idComprobante=0;
        this.idAlmacen=0;
        this.idProveedor=0;
        this.tipoComprobante=0;
        this.serie="";
        this.numero="";
        this.fecha=new Date();
        this.cerradaOficina=false;
        this.cerradaAlmacen=false;
    }
    
    public TOComprobante(int idAlmacen, int idProveedor, int tipoComprobante) {
        this.idComprobante=0;
        this.idAlmacen=idAlmacen;
        this.idProveedor=idProveedor;
        this.tipoComprobante=tipoComprobante;
        this.serie="";
        this.numero="";
        this.fecha=new Date();
        this.cerradaOficina=false;
        this.cerradaAlmacen=false;
    }
    
    @Override
    public String toString() {
        return (this.serie.isEmpty()?"":this.serie+"-")+this.numero;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.idComprobante;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TOComprobante other = (TOComprobante) obj;
        if (this.idComprobante != other.idComprobante) {
            return false;
        }
        return true;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public int getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(int tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isCerradaOficina() {
        return cerradaOficina;
    }

    public void setCerradaOficina(boolean cerradaOficina) {
        this.cerradaOficina = cerradaOficina;
    }

    public boolean isCerradaAlmacen() {
        return cerradaAlmacen;
    }

    public void setCerradaAlmacen(boolean cerradaAlmacen) {
        this.cerradaAlmacen = cerradaAlmacen;
    }
}
