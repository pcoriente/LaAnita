/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.dominio;

import contribuyentes.Contribuyente;
import direccion.dominio.Direccion;
import direccion.to.TODireccion;
import java.io.Serializable;

/**
 *
 * @author Anita
 */
public class Agentes implements Serializable {

    private int idAgente;
    private String agente;
    private int idContribuyente;
    private int idDireccion;
    private int idCedis;
    private String cedis;
    private String rfc;
//    private Direccion direccion = new Direccion();
    private Contribuyente contribuyente = new Contribuyente();
    private TODireccion toDireccion = new TODireccion();

    public int getIdAgente() {
        return idAgente;
    }

    public void setIdAgente(int idAgente) {
        this.idAgente = idAgente;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public int getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(int idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public int getIdCedis() {
        return idCedis;
    }

    public void setIdCedis(int idCedis) {
        this.idCedis = idCedis;
    }

    public String getCedis() {
        return cedis;
    }

    public void setCedis(String cedis) {
        this.cedis = cedis;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public TODireccion getToDireccion() {
        return toDireccion;
    }

    public void setToDireccion(TODireccion toDireccion) {
        this.toDireccion = toDireccion;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }
}
