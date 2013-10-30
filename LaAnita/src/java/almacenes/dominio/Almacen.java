package almacenes.dominio;
import cedis.dominio.MiniCedis;
import empresas.dominio.MiniEmpresa;
import direccion.dominio.Direccion;

/**
 *
 * @author carlosp
 */
public class Almacen {

    private int idAlmacen;
    private String almacen;
    private int idCedis;
    private int idEmpresa;
    private Direccion direccion;
    private String encargado;

    public Almacen() {
        idAlmacen = 0;
        almacen = "";
        idCedis = 0;
        idEmpresa = 0;
        direccion = new Direccion();
        encargado = "";
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

    public int getIdCedis() {
        return idCedis;
    }

    public void setIdCedis(int idCedis) {
        this.idCedis = idCedis;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }
}
