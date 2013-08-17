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
public class ModuloSubMenu implements Serializable {

    private int idSubMenu;
    private String subMenu;
    private int idMenu;

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public ModuloSubMenu() {
        this.idSubMenu = 0;
        this.subMenu = "";
    }

    public ModuloSubMenu(int idSubMenu, String subMenu) {
        this.idSubMenu = idSubMenu;
        this.subMenu = subMenu;
    }

    @Override
    public String toString() {
        return this.subMenu;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.idSubMenu;
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
        final ModuloSubMenu other = (ModuloSubMenu) obj;
        if (this.idSubMenu != other.idSubMenu) {
            return false;
        }
        return true;
    }

    public int getIdSubMenu() {
        return idSubMenu;
    }

    public void setIdSubMenu(int idSubMenu) {
        this.idSubMenu = idSubMenu;
    }

    public String getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(String subMenu) {
        this.subMenu = subMenu;
    }
}
