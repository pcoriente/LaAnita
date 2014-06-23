/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import Message.Mensajes;
import clientesListas.dominio.ClientesFormatos;
import formatos.dominio.ClientesFormato;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author Usuario
 */
@Named(value = "mbFormatos")
@Dependent
public class MbFormatos {

    ClientesFormatos clientesFormatoa = new ClientesFormatos();
    
    /**
     * Creates a new instance of MbFormatos
     */
    public MbFormatos() {
        
    }

}
