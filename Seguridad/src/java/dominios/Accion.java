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
public class Accion implements Serializable {

    private int idAccion;
    private String accion;
    private int status;
    private boolean Sta;
    private String idBoton;
    private int idMOdulo;
    private String acciones;
    private int idPerfil;

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getAcciones() {
        return acciones;
    }

    public void setAcciones(String acciones) {
        this.acciones = acciones;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSta() {
        return Sta;
    }

    public void setSta(boolean Sta) {
        this.Sta = Sta;
    }

    public int getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(int idAccion) {
        this.idAccion = idAccion;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getIdBoton() {
        return idBoton;
    }

    public void setIdBoton(String idBoton) {
        this.idBoton = idBoton;
    }

    public int getIdMOdulo() {
        return idMOdulo;
    }

    public void setIdMOdulo(int idMOdulo) {
        this.idMOdulo = idMOdulo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.idAccion;
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
        final Accion other = (Accion) obj;
        if (this.idAccion != other.idAccion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return accion;
    }
}
