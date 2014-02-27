/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mbMenuClientesGrupos;

import contactos.MbContactos;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import menuClientesGrupos.dao.DAOClientesGrupo;
import menuClientesGrupos.dominio.ClientesGrupos;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Usuario
 */
@Named(value = "mbMenuClientesGrupos")
@SessionScoped
public class MbClientesGrupos implements Serializable {

    @ManagedProperty(value = "#{mbContactos}")
    private MbContactos mbContactos = new MbContactos();
    private ClientesGrupos clientesGrupos = new ClientesGrupos();
    private ArrayList<ClientesGrupos> lstClientesGrupos;
    private ClientesGrupos clienteGrupoSeleccionado = new ClientesGrupos();
    private boolean actualizarx = false;

    public MbClientesGrupos() {
        if (lstClientesGrupos == null) {
            cargarListaGruposClientes();
        }
    }

    public void cargaContactos() {

        this.setActualizarx(false);
    }

    public MbContactos getMbContactos() {
        return mbContactos;
    }

    public void setMbContactos(MbContactos mbContactos) {
        this.mbContactos = mbContactos;
    }

    public ArrayList<ClientesGrupos> getLstClientesGrupos() {
        return lstClientesGrupos;
    }

    public void setLstClientesGrupos(ArrayList<ClientesGrupos> lstClientesGrupos) {
        this.lstClientesGrupos = lstClientesGrupos;
    }

    public ClientesGrupos getClientesGrupos() {
        return clientesGrupos;
    }

    public void setClientesGrupos(ClientesGrupos clientesGrupos) {
        this.clientesGrupos = clientesGrupos;
    }

    private void cargarListaGruposClientes() {
        try {
            DAOClientesGrupo daoClientes = new DAOClientesGrupo();
            lstClientesGrupos = daoClientes.dameListaClientesGrupos();
        } catch (NamingException ex) {
            Logger.getLogger(MbClientesGrupos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbClientesGrupos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClientesGrupos getClienteGrupoSeleccionado() {
        return clienteGrupoSeleccionado;
    }

    public void setClienteGrupoSeleccionado(ClientesGrupos clienteGrupoSeleccionado) {
        this.clienteGrupoSeleccionado = clienteGrupoSeleccionado;
    }

    public void cargarTelefonos() {
        this.mbContactos.getMbTelefonos().cargaTelefonos(this.mbContactos.getContacto().getIdContacto());
    }

    public boolean validarClientesGrupo() {
        boolean okClienteGrupo = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (clientesGrupos.getGrupoCte().equals("")) {
            fMsg.setDetail("Se requiere un Cliente Grupo");
        } else {
            okClienteGrupo = true;
        }
        if (okClienteGrupo == false) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("ok", okClienteGrupo);
        return okClienteGrupo;
    }

    public void guardar() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        boolean ok = false;
        ok = validarClientesGrupo();
        if (ok == true) {
            try {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("Exito Nuevo Grupo de Clientes Disponible");
                DAOClientesGrupo dao = new DAOClientesGrupo();
                dao.guardarClientesGrupo(clientesGrupos);
            } catch (NamingException ex) {
                ok = false;
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(" " + ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(" " + ex.getMessage());
            }
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
            lstClientesGrupos = null;
            cargarListaGruposClientes();
        }
    }

    public void guardarContacto() {
        boolean validar = false;
        validar = this.mbContactos.validarContactos();
        if (validar == true) {
        }
    }

    public void cargarDatos() {
        clientesGrupos.setGrupoCte(clienteGrupoSeleccionado.getGrupoCte());
        clientesGrupos.setIdGrupoCte(clienteGrupoSeleccionado.getIdGrupoCte());
    }

    public void actualizar() {
        this.mbContactos.cargaContactos(4, clientesGrupos.getIdGrupoCte());
        this.setActualizarx(true);
    }

    public boolean isActualizarx() {
        return actualizarx;
    }

    public void setActualizarx(boolean actualizarx) {
        this.actualizarx = actualizarx;
    }
}
