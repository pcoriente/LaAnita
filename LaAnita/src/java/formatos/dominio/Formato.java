/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.dominio;

import java.io.Serializable;
import menuClientesGrupos.dominio.ClientesGrupos;

/**
 *
 * @author Usuario
 */
public class Formato implements Serializable {

    private int idFormato;
    private String formato;
    private ClientesGrupos clientesGrupo = new ClientesGrupos();

    public int getIdFormato() {
        return idFormato;
    }

    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public ClientesGrupos getClientesGrupo() {
        return clientesGrupo;
    }

    public void setClientesGrupo(ClientesGrupos clientesGrupo) {
        this.clientesGrupo = clientesGrupo;
    }

    @Override
    public String toString() {
        return formato;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.idFormato;
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
        final Formato other = (Formato) obj;
        if (this.idFormato != other.idFormato) {
            return false;
        }
        return true;
    }
    
    
    
}