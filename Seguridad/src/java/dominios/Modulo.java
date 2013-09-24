/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominios;

import java.io.Serializable;

/**
 *
 * @author Comodoro
 */
public class Modulo implements Serializable {

    private int idModulo;
    private String modulo;
    private String url;
    private int idSubMenu;
    private int idMenu;

    public int getIdModulo() {
        return idModulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIdSubMenu() {
        return idSubMenu;
    }

    public void setIdSubMenu(int idSubMenu) {
        this.idSubMenu = idSubMenu;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.idModulo;
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
        final Modulo other = (Modulo) obj;
        if (this.idModulo != other.idModulo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return modulo;
    }
}
