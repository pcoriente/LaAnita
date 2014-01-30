/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.dominio;

import cedis.dominio.MiniCedis;
import contactos.dominio.Contacto;
import contactos.dominio.Telefono;
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
    private Direccion direccionAgente = new Direccion();
    private Contribuyente contribuyente = new Contribuyente();
    private MiniCedis miniCedis = new MiniCedis();
    private Contacto contacto = new Contacto();
    private Telefono telefono = new Telefono();

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

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Direccion getDireccionAgente() {
        return direccionAgente;
    }

    public void setDireccionAgente(Direccion direccionAgente) {
        this.direccionAgente = direccionAgente;
    }

    public MiniCedis getMiniCedis() {
        return miniCedis;
    }

    public void setMiniCedis(MiniCedis miniCedis) {
        this.miniCedis = miniCedis;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Telefono getTelefono() {
        return telefono;
    }

    public void setTelefono(Telefono telefono) {
        this.telefono = telefono;
    }
    
    
}
