/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.dominio;

/**
 *
 * @author Comodoro
 */
public class DominioPartes {

    private int idParte;
    private String parte;

    @Override
    public String toString() {
        return parte;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.idParte;
        hash = 41 * hash + (this.parte != null ? this.parte.hashCode() : 0);
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
        final DominioPartes other = (DominioPartes) obj;
        if (this.idParte != other.idParte) {
            return false;
        }
        if ((this.parte == null) ? (other.parte != null) : !this.parte.equals(other.parte)) {
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
