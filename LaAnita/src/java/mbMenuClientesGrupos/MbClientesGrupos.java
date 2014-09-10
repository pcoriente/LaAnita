/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mbMenuClientesGrupos;

import Message.Mensajes;
import clientesListas.dominio.ClientesFormatos;
import contactos.MbContactos;
import contactos.dao.DAOContactos;
import contactos.dominio.Contacto;
import contactos.dominio.Telefono;
import contactos.dominio.TelefonoTipo;
import formatos.DAOFormatos.DAOFormatos;
import formatos.MbFormatos;
import formatos.dominio.ClientesFormato;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import menuClientesGrupos.dao.DAOClientesGrupo;
import menuClientesGrupos.dominio.ClientesGrupos;
import menuClientesGrupos.dominio.ClientesGruposIconos;
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
    @ManagedProperty(value = "#{mbFormatos}")
    private MbFormatos mbFormatos = new MbFormatos();
    private ClientesGrupos clientesGrupos = new ClientesGrupos();
    private ArrayList<ClientesGrupos> lstClientesGrupos;
    private ClientesGrupos clienteGrupoSeleccionado = null;
    private boolean actualizar = false;
    private String lblNuevoContacto = "ui-icon-document";
    private String lblNuevoTelefono = "ui-icon-document";
    private ClientesGruposIconos iconos = new ClientesGruposIconos();
    private boolean actualizarRfc = false;
    private ArrayList<SelectItem> itemsClientesGrupos = null;
    private ClientesGrupos cmbClientesGrupos = new ClientesGrupos();

    public MbClientesGrupos() {
        mbContactos = new MbContactos();
        if (lstClientesGrupos == null) {
            cargarListaGruposClientes();
        }
    }
    
    public void inicializar() {
        this.mbContactos=new MbContactos();
        lstClientesGrupos=null;
    }

    public void guaradarClientesTienda() {

    }

    public boolean validar() {
        boolean ok = false;

        return ok;
    }

    public void cargaContactos() {
        clientesGrupos.setGrupoCte("");
        this.setActualizar(false);
        clienteGrupoSeleccionado = null;
    }

    public MbContactos getMbContactos() {
        return mbContactos;
    }

    public void setMbContactos(MbContactos mbContactos) {
        this.mbContactos = mbContactos;
    }

    public ArrayList<ClientesGrupos> getLstClientesGrupos() {
        if(this.lstClientesGrupos==null) {
            this.cargarListaGruposClientes();
        }
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
        if (mbContactos.getContacto().getIdContacto() == 0) {
            lblNuevoContacto = "ui-icon-document";
            this.iconos.setBtnGuardarContactos("ui-icon-disk");
            this.iconos.setLblGuardarContactos("Guardar Contacto");
            mbContactos.setContacto(new Contacto());
        } else {
            this.iconos.setBtnGuardarContactos("ui-icon-pencil");
            this.iconos.setLblGuardarContactos("Actualizar Contacto ");
            lblNuevoContacto = "ui-icon-pencil";
        }
        this.mbContactos.getMbTelefonos().cargaTelefonos(this.mbContactos.getContacto().getIdContacto());
    }

    public boolean validarClientesGrupo() {
        boolean okClienteGrupo = false;
        if (clientesGrupos.getGrupoCte().equals("")) {
            Mensajes.mensajeError("Se requiere un Cliente Grupo");
        } else {
            okClienteGrupo = true;
        }
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

                DAOClientesGrupo dao = new DAOClientesGrupo();
                if (clienteGrupoSeleccionado == null) {
                    fMsg.setDetail("Exito Nuevo Grupo de Clientes Disponible");
                    dao.guardarClientesGrupo(clientesGrupos);
                } else {
                    fMsg.setDetail("Exito Grupo de Cliente Actualizado");
                    dao.actualizar(clientesGrupos);
                    clienteGrupoSeleccionado = null;
                }
                this.getIconos().setLblGuardarContactos("ui-icon-document");
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
        clienteGrupoSeleccionado = null;
//        mbContactos.getMbTelefonos().getTelefono().setTipo(new TelefonoTipo(false));
    }

    public void cambiarIconoTelefono() {
        if (this.mbContactos.getMbTelefonos().getTelefono().getIdTelefono() == 0) {
            this.setLblNuevoTelefono("ui-icon-document");

        } else {
            this.iconos.setBtnGuardarContactos("ui-icon-pencil");
            this.iconos.setLblGuardarContactos("Actualizar Contacto ");
            this.setLblNuevoTelefono("ui-icon-pencil");
        }
    }

    public void validarTelefonos() {
        boolean ok = this.mbContactos.getMbTelefonos().validarTelefonos();
        if (ok == true) {
            this.mbContactos.getMbTelefonos().grabar(this.mbContactos.getContacto().getIdContacto());
            this.mbContactos.getMbTelefonos().cargaTelefonos(this.mbContactos.getContacto().getIdContacto());
            lblNuevoTelefono = "ui-icon-pencil";
//            this.mbContactos.getMbTelefonos().setTelefono(new Telefono());
        }
    }

    public void limpiarContacto() {
        if (mbContactos.getContacto().getIdContacto() == 0) {
            mbContactos.setContacto(new Contacto());
        }
    }

    public void eliminarContacto() {
        boolean elimino = mbContactos.eliminar(mbContactos.getContacto().getIdContacto());
        if (elimino == true) {
            mbContactos.cargaContactos(4, clientesGrupos.getIdGrupoCte());
            RequestContext context = RequestContext.getCurrentInstance();
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
            boolean ok = true;
            fMsg.setDetail("Exito Contacto eliminado");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("okContacto", ok);
            mbContactos.setContacto(new Contacto());
        }
    }

    public void eliminarTelefono() {
        mbContactos.getMbTelefonos().eliminar(mbContactos.getMbTelefonos().getTelefono().getIdTelefono());
        mbContactos.getMbTelefonos().cargaTelefonos(mbContactos.getContacto().getIdContacto());
    }

    public void cargarTelefonosTipos() {
        mbContactos.getMbTelefonos().cargaTipos();
        if (this.mbContactos.getMbTelefonos().getTelefono().getIdTelefono() == 0) {
            this.mbContactos.getMbTelefonos().setTelefono(new Telefono());
        }
    }

    public void deseleccionar() {
        this.setActualizar(false);
        clienteGrupoSeleccionado = null;
//        mbContactos= new MbContactos();
    }

    public void informacionTipo() {
        this.mbContactos.getMbTelefonos().getTipo().setTipo(this.mbContactos.getMbTelefonos().getTelefono().getTipo().getTipo());
    }

    public void guardarContacto() {
        try {
            DAOContactos daoContactos = new DAOContactos();
            boolean validar = false;
            validar = this.mbContactos.validarContactos();
            if (validar == true) {
                if (mbContactos.getContacto().getIdContacto() == 0) {
                    daoContactos.agregar(mbContactos.getContacto(), clientesGrupos.getIdGrupoCte(), 4);
                } else {
                    daoContactos.modificar(mbContactos.getContacto());
                }
                this.mbContactos.cargaContactos(4, clientesGrupos.getIdGrupoCte());
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbClientesGrupos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbClientesGrupos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cargarDatos() {
        clientesGrupos.setGrupoCte(clienteGrupoSeleccionado.getGrupoCte());
        clientesGrupos.setIdGrupoCte(clienteGrupoSeleccionado.getIdGrupoCte());
    }

    public String salir() {
        return "index.xhtml";
    }

    public void guardarTelefonoTipo() {
        this.mbContactos.getMbTelefonos().getTipo().setIdTipo(this.mbContactos.getMbTelefonos().getTelefono().getTipo().getIdTipo());
        if (mbContactos.getMbTelefonos().grabarTipo()) {
            mbContactos.getMbTelefonos().cargaTipos();
        }
    }

    public void mantenimientoTelefonoTipo() {
        if (mbContactos.getMbTelefonos().getTelefono().getTipo().getIdTipo() == 0) {
            mbContactos.getMbTelefonos().setTipo(new TelefonoTipo(false));
        }
    }

    public void eliminarTelefonosTipo() {
        this.mbContactos.getMbTelefonos().eliminarTipo(mbContactos.getMbTelefonos().getTelefono().getTipo().getIdTipo());
        this.mbContactos.getMbTelefonos().cargaTipos();
    }

    public void actualizar() {
        mbContactos.getMbTelefonos().setListaTelefonos(new ArrayList<SelectItem>());
        this.mbContactos.cargaContactos(4, clientesGrupos.getIdGrupoCte());
        mbContactos.getContacto().setIdContacto(0);
        lblNuevoContacto = ("ui-icon-document");
        mbFormatos.cargarListaFormatos(clientesGrupos.getIdGrupoCte());
        actualizar = true;
    }

    public ArrayList<SelectItem> getItemsClientesGrupos() {
        if (this.itemsClientesGrupos == null) {
            cargaListaClientesGrupos();
        }
        return this.itemsClientesGrupos;
    }
    
    public void cargaListaClientesGrupos() {
        this.itemsClientesGrupos = new ArrayList<SelectItem>();
        ClientesGrupos clientes = new ClientesGrupos();
        clientes.setIdGrupoCte(0);
        clientes.setGrupoCte("Nuevo Cliente Grupo");

        this.itemsClientesGrupos.add(new SelectItem(clientes, clientes.getGrupoCte()));
        for (ClientesGrupos cl : this.getLstClientesGrupos()) {
            this.itemsClientesGrupos.add(new SelectItem(cl, cl.getGrupoCte()));
        }
    }
    
    public ClientesGrupos obtenerClienteGpo(int idClienteGpo) {
        ClientesGrupos gpo=null;
        try {
            DAOClientesGrupo daoClientes = new DAOClientesGrupo();
            gpo = daoClientes.dameClientesGrupo(idClienteGpo);
        } catch (NamingException ex) {
            Mensajes.mensajeError(ex.getMessage());
        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getErrorCode() + " " + ex.getMessage());
        }
        return gpo;
    }

    public void guardarFormato() throws NamingException {
        boolean ok = false;
        ok = mbFormatos.validarFormatos();
        if (ok == true) {
            try {
                DAOFormatos dao = new DAOFormatos();
                mbFormatos.getClientesFormatos().getClientesGrupos().setIdGrupoCte(clienteGrupoSeleccionado.getIdGrupoCte());
                if (mbFormatos.getCmbClientesFormatos().getIdFormato() == 0) {
                    dao.guardarFormato(mbFormatos.getClientesFormatos());
                    Mensajes.mensajeSucces("Datos Almacenados");
                } else {
                    dao.actulizarFormato(mbFormatos.getClientesFormatos());
                    Mensajes.mensajeSucces("Datos Actualizados Exitosamente");
                }
                mbFormatos.setLstFormatos(null);
                mbFormatos.cargarListaFormatos(clienteGrupoSeleccionado.getIdGrupoCte());
            } catch (SQLException ex) {
                Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbClientesGrupos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void limpiarCamposClientesFormatos() {
        if (mbFormatos.getCmbClientesFormatos().getIdFormato() > 0) {
            mbFormatos.setClientesFormatos(mbFormatos.getCmbClientesFormatos());
        } else {
            mbFormatos.setClientesFormatos(new ClientesFormato());
        }
    }

    public void setItemsClientesGrupos(ArrayList<SelectItem> itemsClientesGrupos) {
        this.itemsClientesGrupos = itemsClientesGrupos;
    }

    public String getLblNuevoContacto() {
        return lblNuevoContacto;
    }

    public void setLblNuevoContacto(String lblNuevoContacto) {
        this.lblNuevoContacto = lblNuevoContacto;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public String getLblNuevoTelefono() {
        return lblNuevoTelefono;
    }

    public void setLblNuevoTelefono(String lblNuevoTelefono) {
        this.lblNuevoTelefono = lblNuevoTelefono;
    }

    public ClientesGruposIconos getIconos() {
        return iconos;
    }

    public void setIconos(ClientesGruposIconos iconos) {
        this.iconos = iconos;
    }

    public boolean isActualizarRfc() {
        return actualizarRfc;
    }

    public void setActualizarRfc(boolean actualizarRfc) {
        this.actualizarRfc = actualizarRfc;
    }

    public ClientesGrupos getCmbClientesGrupos() {
        return cmbClientesGrupos;
    }

    public void setCmbClientesGrupos(ClientesGrupos cmbClientesGrupos) {
        this.cmbClientesGrupos = cmbClientesGrupos;
    }

    public MbFormatos getMbFormatos() {
        return mbFormatos;
    }

    public void setMbFormatos(MbFormatos mbFormatos) {
        this.mbFormatos = mbFormatos;
    }

}
