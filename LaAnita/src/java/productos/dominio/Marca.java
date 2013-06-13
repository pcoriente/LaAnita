package productos.dominio;

/**
 *
 * @author JULIOS
 */
public class Marca {
    private int idMarca;
    //private int codigoMarca;
    private String marca;
    //private int idFabricante;
    private boolean produccion;
    
    public Marca() {
        this.idMarca=0;
        //this.codigoMarca=0;
        this.marca="SELECCIONE UNA MARCA";
        this.produccion=true;
        //this.idFabricante=0;
    }

    public Marca(int idMarca, String marca, boolean produccion) {
        this.idMarca = idMarca;
        //this.codigoMarca = codigoMarca;
        this.marca = marca;
        this.produccion = produccion;
        //this.idFabricante = idFabricante;
    }

    @Override
    public String toString() {
        return marca;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.idMarca;
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
        final Marca other = (Marca) obj;
        if (this.idMarca != other.idMarca) {
            return false;
        }
        return true;
    }
    /*
    public int getCodigoMarca() {
        return codigoMarca;
    }

    public void setCodigoMarca(int codigoMarca) {
        this.codigoMarca = codigoMarca;
    }
    * */

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
    /*
    public int getIdFabricante() {
        return idFabricante;
    }

    public void setIdFabricante(int idFabricante) {
        this.idFabricante = idFabricante;
    }
    * */

    public boolean isProduccion() {
        return produccion;
    }

    public void setProduccion(boolean produccion) {
        this.produccion = produccion;
    }
}
