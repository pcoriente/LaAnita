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
public class PerfileDetalle implements Serializable {

    private int idPerfil;
    private int idAccion;
    private int idUsuario;

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

    public int getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(int idAccion) {
        this.idAccion = idAccion;
    }
}
