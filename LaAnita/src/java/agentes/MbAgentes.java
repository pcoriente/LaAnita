/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import agentes.dao.DaoAgentes;
import agentes.dominio.Agentes;
import cedis.MbMiniCedis;
import cedis.dao.DAOMiniCedis;
import cedis.dominio.MiniCedis;
import contactos.MbContactos;
import contactos.dao.DAOTelefonos;
import contactos.dominio.TelefonoTipo;
import contribuyentes.MbContribuyentes;
import direccion.MbDireccion;
import direccion.dominio.Direccion;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Anita
 */
@Named(value = "mbAgentes")
@SessionScoped
public class MbAgentes implements Serializable {

    @ManagedProperty(value = "#{mbCedis}")
    private MbMiniCedis mbCedis = new MbMiniCedis();
    @ManagedProperty(value = "#{mbContactos}")
    private MbContactos mbContactos = new MbContactos();
    @ManagedProperty(value = "#{mbAgentes}")
    private MbContribuyentes mbContribuyente = new MbContribuyentes();
    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion = new MbDireccion();
    private ArrayList<Agentes> listaAgentes;
    private Agentes seleccionListaAgentes;
    private Agentes agente = new Agentes();
    private ArrayList<SelectItem> listaAsentamientos = new ArrayList<SelectItem>();
    private int personaFisica = 0;
    private ArrayList<SelectItem> listaMiniCedis = null;
    private DAOMiniCedis dao;
    private int flgDireccion = 0;
    boolean editarAsentamiento = false;
    String lblCancelar = "";
    String titleCancelar = "";

    public MbAgentes() {
        titleCancelar = "Cancelar Contacto";
        lblCancelar = "ui-icon-arrowreturnthick-1-w";
    }

    private void cargarTablaAgentes() {
        try {
            DaoAgentes daoAgentes = new DaoAgentes();
            listaAgentes = daoAgentes.listaAgentes();
        } catch (SQLException ex) {
            Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Agentes> getListaAgentes() {
        if (listaAgentes == null) {
            cargarTablaAgentes();
        }
        return listaAgentes;
    }

    public void agregarNuevoAgente() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        boolean ok = false;
        ok = mbContribuyente.valida();
        if (ok == true) {
            if (agente.getContribuyente().getDireccion().getCalle().equals("")) {
                ok = false;
                fMsg.setDetail("Se requiere una Dirección!!");
                if (!ok) {
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                }
                context.addCallbackParam("okContribuyente", ok);
            } else {
                if (agente.getMiniCedis().getIdCedis() == 0) {
                    ok = false;
                    fMsg.setDetail("Se requiere un Cedis!!");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                    context.addCallbackParam("okContribuyente", ok);
                } else {
                    ok = this.validarAgente();
                    if (ok == true) {
                        DaoAgentes daoAgentes = new DaoAgentes();
//                        boolean okExito = daoAgentes.guardarAgentes(mbContribuyente.getContribuyente(), mbDireccion.getDireccionContribuyente(), mbDireccion.getDireccionAgente(), agente);
                    }
                }
            }
        }
    }

    public boolean validarAgente() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (agente.getAgente().equals("")) {
            fMsg.setDetail("Se requiere el Agente !!");
            if (!ok) {
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
            }
            context.addCallbackParam("okContribuyente", ok);
        } else if (agente.getDireccionAgente().getCalle().equals("")) {
            fMsg.setDetail("Se requiere un Direcciòn !!");
            if (!ok) {
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
            }
            context.addCallbackParam("okContribuyente", ok);
        } else {
            ok = true;
        }
        return ok;
    }

    public void dameStatusRfc() {
        int longitud = mbContribuyente.getContribuyente().getRfc().length();
        if (longitud == 13) {
            personaFisica = 1;
        } else {
            personaFisica = 2;
        }
    }

    public void respaldoDireccionContribuyente() {
        this.flgDireccion = 1;
        mbDireccion.setDireccion(this.agente.getContribuyente().getDireccion());
    }

    public void respaldoDireccionAgente() {
        this.flgDireccion = 2;
        mbDireccion.setDireccion(this.agente.getDireccionAgente());
    }

    public void validarDireccion() {
        boolean paso = mbDireccion.validarDireccion();
        if (paso == true) {
            if (flgDireccion == 1) {
                agente.getContribuyente().setDireccion(mbDireccion.getDireccion());
            } else if (flgDireccion == 2) {
                agente.setDireccionAgente(mbDireccion.getDireccion());
            }
            mbDireccion.setDireccion(new Direccion());
        }
    }

    public void validarContacto() {
        boolean ok = false;

        ok = mbContactos.validarContactos();
        if (ok == true) {
            agente.setContacto(this.mbContactos.getContacto());
        }
    }

    public void dameValor() {
        if (agente.getContacto().getIdContacto() > 0) {
            this.setLblCancelar("ui-icon-trash");
            this.setTitleCancelar("Eliminar Contacto");
            this.mbContactos.getMbTelefonos().cargaTelefonos(agente.getContacto().getIdContacto());
        } else {
            this.setLblCancelar("ui-icon-arrowreturnthick-1-w");
            this.setTitleCancelar("Cancelar Contacto");
        }
    }

    public String getLblCancelar() {
        return lblCancelar;
    }

    public void setLblCancelar(String lblCancelar) {
        this.lblCancelar = lblCancelar;
    }

    public void setListaAgentes(ArrayList<Agentes> listaAgentes) {
        this.listaAgentes = listaAgentes;
    }

    public Agentes getSeleccionListaAgentes() {
        return seleccionListaAgentes;
    }

    public void setSeleccionListaAgentes(Agentes seleccionListaAgentes) {
        this.seleccionListaAgentes = seleccionListaAgentes;
    }

    public MbMiniCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbMiniCedis mbCedis) {
        this.mbCedis = mbCedis;
    }

    public ArrayList<SelectItem> getListaAsentamientos() {
        return listaAsentamientos;
    }

    public void setListaAsentamientos(ArrayList<SelectItem> listaAsentamientos) {
        this.listaAsentamientos = listaAsentamientos;
    }

    public Agentes getAgente() {
        return agente;
    }

    public void setAgente(Agentes agente) {
        this.agente = agente;
    }

    private void obtenerListaMiniCedis() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.listaMiniCedis = new ArrayList<SelectItem>();
        try {
            MiniCedis p0 = new MiniCedis();
            p0.setIdCedis(0);
            p0.setCedis("Seleccione un CEDIS");
            SelectItem cero = new SelectItem(p0, p0.toString());
            listaMiniCedis.add(cero);

            this.dao = new DAOMiniCedis();
            for (MiniCedis m : this.dao.obtenerTodasListaMiniCedis()) {
                listaMiniCedis.add(new SelectItem(m, m.toString()));
            }
            ok = true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public int getPersonaFisica() {
        return personaFisica;
    }

    public void setPersonaFisica(int personaFisica) {
        this.personaFisica = personaFisica;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }

    public MbContribuyentes getMbContribuyente() {
        return mbContribuyente;
    }

    public void setMbContribuyente(MbContribuyentes mbContribuyente) {
        this.mbContribuyente = mbContribuyente;
    }

    public ArrayList<SelectItem> getListaMiniCedis() {
        if (this.listaMiniCedis == null) {
            this.obtenerListaMiniCedis();
        }
        return listaMiniCedis;
    }

    public void cargarAgentes() {
        this.getMbContactos().cargaContactos(2, 0);
        this.getMbContactos().getMbTelefonos().cargaTelefonos(0);
    }

    public void validarTelefonos() {
        boolean ok = false;
        ok = this.mbContactos.getMbTelefonos().validarTelefonos();
        if (ok == true) {
            try {
                DAOTelefonos dao = new DAOTelefonos();
                agente.getContacto().getTelefonos().add(mbContactos.getMbTelefonos().getTelefono());
                try {
                    dao.agregar(mbContactos.getMbTelefonos().getTelefono(), agente.getContacto().getIdContacto());
                    this.mbContactos.getMbTelefonos().cargaTelefonos(agente.getContacto().getIdContacto());
                } catch (SQLException ex) {
                    Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void  limpiarCampos(){
        this.agente.getMiniCedis().setIdCedis(0);
        this.agente.getContacto().setIdContacto(0);
        this.agente.getTelefono().setIdTelefono(0);
    }

    public void cargaTipos() {
        mbContactos.getMbTelefonos().cargaTipos();
    }

    public void setListaMiniCedis(ArrayList<SelectItem> listaMiniCedis) {
        this.listaMiniCedis = listaMiniCedis;
    }

    public DAOMiniCedis getDao() {
        return dao;
    }

    public void setDao(DAOMiniCedis dao) {
        this.dao = dao;
    }

    public boolean isEditarAsentamiento() {
        return editarAsentamiento;
    }

    public void setEditarAsentamiento(boolean editarAsentamiento) {
        this.editarAsentamiento = editarAsentamiento;
    }

    public MbContactos getMbContactos() {
        return mbContactos;
    }

    public void setMbContactos(MbContactos mbContactos) {
        this.mbContactos = mbContactos;
    }

    public String getTitleCancelar() {
        return titleCancelar;
    }

    public void setTitleCancelar(String titleCancelar) {
        this.titleCancelar = titleCancelar;
    }
}
