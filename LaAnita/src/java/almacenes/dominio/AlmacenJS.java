package almacenes.dominio;

import cedis.dominio.MiniCedis;
import direccion.dominio.Direccion;
import empresas.dominio.MiniEmpresa;

/**
 *
 * @author jesc
 */
public class AlmacenJS {
    private int idAlmacen;
    private String almacen;
    private MiniCedis cedis;
    private MiniEmpresa empresa;
    private Direccion direccion;

    public AlmacenJS() {
        this.idAlmacen=0;
        this.almacen="";
        this.cedis=new MiniCedis();
        this.empresa=new MiniEmpresa();
        this.direccion=new Direccion();
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public MiniCedis getCedis() {
        return cedis;
    }

    public void setCedis(MiniCedis cedis) {
        this.cedis = cedis;
    }

    public MiniEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(MiniEmpresa empresa) {
        this.empresa = empresa;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}
