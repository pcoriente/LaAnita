/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import Message.Mensajes;
import clientesListas.dominio.ClientesFormatos;
import formatos.DAOFormatos.DAOFormatos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;

/**
 *
 * @author Usuario
 */
@Named(value = "mbFormatos")
@Dependent
public class MbFormatos {

    ClientesFormatos clientesFormatos = new ClientesFormatos();
    ArrayList<SelectItem>lstFormatos= new ArrayList<SelectItem>();

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
    
    
    public void cargarListaFormatos(int idGrupoClte){
        try {
            DAOFormatos dao = new DAOFormatos();
            ClientesFormatos cli = new ClientesFormatos();
            cli.setIdFormato(0);
            cli.setFormato("Nuevo Formato");
            lstFormatos.add(new SelectItem(cli, cli.getFormato()));
            for(ClientesFormatos clientes : dao.dameFormatos(idGrupoClte)){
                lstFormatos.add(new SelectItem(clientes, clientes.getFormato()));
            }
        } catch (NamingException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(SQLException e){
            Mensajes.mensajeError(e.getMessage());
        }
    }
    
    

    public ClientesFormatos getClientesFormatos() {
        return clientesFormatos;
    }

    public void setClientesFormatos(ClientesFormatos clientesFormatos) {
        this.clientesFormatos = clientesFormatos;
    }

    public ArrayList<SelectItem> getLstFormatos() {
        return lstFormatos;
    }

    public void setLstFormatos(ArrayList<SelectItem> lstFormatos) {
        this.lstFormatos = lstFormatos;
    }

}
