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
