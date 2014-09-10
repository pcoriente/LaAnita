/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import Message.Mensajes;
import clientesListas.dominio.ClientesFormatos;
import formatos.DAOFormatos.DAOFormatos;
import formatos.dominio.ClientesFormato;
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

    ClientesFormato clientesFormatos = new ClientesFormato();
    ClientesFormato cmbClientesFormatos = new ClientesFormato();
    ArrayList<SelectItem> lstFormatos = null;

    /**
     * Creates a new instance of MbFormatos
     */
    public MbFormatos() {
        this.inicializa();
    }
    
    public void inicializar() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.clientesFormatos=new ClientesFormato();
        this.cmbClientesFormatos=new ClientesFormato();
        this.lstFormatos=null;
        this.cargarListaFormatos(0);
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

    public void cargarListaFormatos(int idGrupoClte) {
        if (lstFormatos == null) {
            try {
                lstFormatos = new ArrayList<SelectItem>();
                DAOFormatos dao = new DAOFormatos();
                ClientesFormato cli = new ClientesFormato();
                cli.setIdFormato(0);
                cli.setFormato("Nuevo Formato");
                lstFormatos.add(new SelectItem(cli, cli.getFormato()));
                for (ClientesFormato clientes : dao.dameFormatos(idGrupoClte)) {
                    lstFormatos.add(new SelectItem(clientes, clientes.getFormato()));
                }
            } catch (NamingException ex) {
                Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException e) {
                Mensajes.mensajeError(e.getMessage());
            }
        }
    }

   public ClientesFormato obtenerFormato(int idFormato) {
       ClientesFormato formato=null;
       try {
           DAOFormatos dao = new DAOFormatos();
           formato=dao.obtenerClientesFormato(idFormato);
       } catch (NamingException ex) {
            Mensajes.mensajeError(ex.getMessage());
        } catch (SQLException e) {
            Mensajes.mensajeError(e.getMessage());
        }
       return formato;
   }

    public ArrayList<SelectItem> getLstFormatos() {
        return lstFormatos;
    }

    public void setLstFormatos(ArrayList<SelectItem> lstFormatos) {
        this.lstFormatos = lstFormatos;
    }

    public ClientesFormato getClientesFormatos() {
        return clientesFormatos;
    }

    public void setClientesFormatos(ClientesFormato clientesFormatos) {
        this.clientesFormatos = clientesFormatos;
    }

    public ClientesFormato getCmbClientesFormatos() {
        return cmbClientesFormatos;
    }

    public void setCmbClientesFormatos(ClientesFormato cmbClientesFormatos) {
        this.cmbClientesFormatos = cmbClientesFormatos;
    }
}
