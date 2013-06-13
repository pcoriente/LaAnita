package productos.dominio;

/**
 *
 * @author JULIOS
 */
public class Unidad {
    private int idUnidad;
    private String unidad;
    private String abreviatura;
    
    public Unidad() {
        this.idUnidad=0;
        this.unidad="";
        this.abreviatura="";
    }

    public Unidad(int idUnidad, String unidad, String abreviatura) {
        this.idUnidad = idUnidad;
        this.unidad = unidad;
        this.abreviatura = abreviatura;
    }

    @Override
    public String toString() {
        return unidad;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.idUnidad;
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
        final Unidad other = (Unidad) obj;
        if (this.idUnidad != other.idUnidad) {
            return false;
        }
        return true;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }
}
