/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import Message.Mensajes;
import clientesListas.dominio.ClientesFormatos;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author Usuario
 */
@Named(value = "mbFormatos")
@Dependent
public class MbFormatos {

    ClientesFormatos clientesFormatos = new ClientesFormatos();

    /**
     * Creates a new instance of MbFormatos
     */
    public MbFormatos() {

    }

    public boolean validarFormatos() {
        boolean ok = false;
        if (clientesFormatos.getFormato().equals("")) {
           
            Mensajes.mensajeAlert("Formato Requerido"); 
        } else {
            ok = true;
        }
        return ok;
    }

    public ClientesFormatos getClientesFormatos() {
        return clientesFormatos;
    }

    public void setClientesFormatos(ClientesFormatos clientesFormatos) {
        this.clientesFormatos = clientesFormatos;
    }

}
