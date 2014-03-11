/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package menuClientesGrupos.dominio;

import contactos.dominio.Contacto;
import java.io.Serializable;

/**
 *
 * @author Usuario
 */
public class ClientesGrupos implements Serializable{

    private int idGrupoCte;
    private String grupoCte;
    private Contacto contaco;

    public int getIdGrupoCte() {
        return idGrupoCte;
    }

    public void setIdGrupoCte(int idGrupoCte) {
        this.idGrupoCte = idGrupoCte;
    }

    public String getGrupoCte() {
        return grupoCte;
    }

    public void setGrupoCte(String grupoCte) {
        this.grupoCte = grupoCte;
    }

    @Override
    public String toString() {
        return grupoCte;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.idGrupoCte;
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
        final ClientesGrupos other = (ClientesGrupos) obj;
        if (this.idGrupoCte != other.idGrupoCte) {
            return false;
        }
        return true;
    }

    public Contacto getContaco() {
        return contaco;
    }

    public void setContaco(Contacto contaco) {
        this.contaco = contaco;
    }
}