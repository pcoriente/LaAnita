package productos.dominio;

import java.io.Serializable;

/**
 *
 * @author JULIOS
 */
public class ProdStr implements Serializable {
    private int idProducto;
    private String tipo;
    private String grupo;
    private String subGrupo;
    private String producto;

    public ProdStr(int idProducto, String tipo, String grupo, String subGrupo, String producto) {
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.grupo = grupo;
        this.subGrupo = subGrupo;
        this.producto = producto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(String subGrupo) {
        this.subGrupo = subGrupo;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }
}
