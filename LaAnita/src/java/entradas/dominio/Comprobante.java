package entradas.dominio;

import almacenes.dominio.Almacen;
import almacenes.dominio.AlmacenJS;
import java.util.Date;
import proveedores.dominio.MiniProveedor;
import proveedores.dominio.Proveedor;

/**
 *
 * @author jesc
 */
public class Comprobante {
    private int idComprobante;
    private AlmacenJS almacen;
    private MiniProveedor proveedor;
    private int tipoComprobante;
    private String serie;
    private String numero;
    private Date fecha;
    private boolean cerradaOficina;
    private boolean cerradaAlmacen;

    public Comprobante() {
        this.idComprobante=0;
        this.almacen=new AlmacenJS();
        this.proveedor=new MiniProveedor();
        this.tipoComprobante=0;
        this.serie="";
        this.numero="";
        this.fecha=new Date();
        this.cerradaOficina=false;
        this.cerradaAlmacen=false;
    }
    
    public Comprobante(AlmacenJS almacen, MiniProveedor proveedor, int tipoComprobante) {
        this.idComprobante=0;
        this.almacen=almacen;
        this.proveedor=proveedor;
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

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public AlmacenJS getAlmacen() {
        return almacen;
    }

    public void setAlmacen(AlmacenJS almacen) {
        this.almacen = almacen;
    }

    public MiniProveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(MiniProveedor proveedor) {
        this.proveedor = proveedor;
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
