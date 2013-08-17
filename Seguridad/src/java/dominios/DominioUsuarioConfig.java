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
public class DominioUsuarioConfig implements Serializable {

    private int idUsuario;
    private int idFormulario;
    private int idCedisZona;
    private int statusFormulario;
    private int statusBotones;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(int idFormulario) {
        this.idFormulario = idFormulario;
    }

    public int getIdCedisZona() {
        return idCedisZona;
    }

    public void setIdCedisZona(int idCedisZona) {
        this.idCedisZona = idCedisZona;
    }

    public int getStatusFormulario() {
        return statusFormulario;
    }

    public void setStatusFormulario(int statusFormulario) {
        this.statusFormulario = statusFormulario;
    }

    public int getStatusBotones() {
        return statusBotones;
    }

    public void setStatusBotones(int statusBotones) {
        this.statusBotones = statusBotones;
    }
}
