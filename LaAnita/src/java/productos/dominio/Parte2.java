package productos.dominio;

import java.io.Serializable;

/**
 *
 * @author JULIOS
 */
public class Parte2 implements Serializable {
    private int idParte;
    private String parte;
    
    public Parte2(int idParte, String parte) {
        this.idParte = idParte;
        this.parte = parte;
    }

    @Override
    public String toString() {
        return parte;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.idParte;
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
        final Parte2 other = (Parte2) obj;
        if (this.idParte != other.idParte) {
            return false;
        }
        return true;
    }

    public int getIdParte() {
        return idParte;
    }

    public void setIdParte(int idParte) {
        this.idParte = idParte;
    }

    public String getParte() {
        return parte;
    }

    public void setParte(String parte) {
        this.parte = parte;
    }
}
