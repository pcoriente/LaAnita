package productos.dominio;

/**
 *
 * @author JULIOS
 */
public class Upc {

    private int idUpc;
    private String upc;
    private int idProducto;

    public Upc(int idProducto) {
        this.idUpc = 0;
        this.upc = "Nuevo UPC";
        this.idProducto = idProducto;
    }

    public Upc(int idUpc, String upc, int idProducto) {
        this.idUpc = idUpc;
        this.upc = upc;
        this.idProducto = idProducto;
    }

    @Override
    public String toString() {
        return this.upc;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.idUpc;
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
        final Upc other = (Upc) obj;
        if (this.idUpc != other.idUpc) {
            return false;
        }
        return true;
    }

    public int getIdUpc() {
        return idUpc;
    }

    public void setIdUpc(int idUpc) {
        this.idUpc = idUpc;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
