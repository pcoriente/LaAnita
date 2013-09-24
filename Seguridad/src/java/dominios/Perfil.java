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
public class Perfil implements Serializable {

    private int idPerfil;
    private String perfil;
    private int idUsuario;

    public Perfil() {
        idPerfil = 0;
        perfil = "";
        idUsuario = 0;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.idPerfil;
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
        final Perfil other = (Perfil) obj;
        if (this.idPerfil != other.idPerfil) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return perfil;
    }
}
