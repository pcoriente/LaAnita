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
public class ModuloMenu implements Serializable {
    private int idMenu;
    private String menu;
    
    public ModuloMenu() {
        this.idMenu=0;
        this.menu="";
    }

    @Override
    public String toString() {
        return this.menu;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.idMenu;
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
        final ModuloMenu other = (ModuloMenu) obj;
        if (this.idMenu != other.idMenu) {
            return false;
        }
        return true;
    }
    
    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
