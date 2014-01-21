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
    @ManagedProperty(value = "#{mbAgentes}")
    private MbContribuyentes mbContribuyente = new MbContribuyentes();
    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion = new MbDireccion();
    private ArrayList<Agentes> listaAgentes;
    private Agentes seleccionListaAgentes;
    private Agentes agente = new Agentes();
    private Direccion cmbDireccion = new Direccion();
    private ArrayList<SelectItem> listaAsentamientos = new ArrayList<SelectItem>();
    private boolean editarAsentamiento;
    private String cont;
    private int personaFisica = 0;
    private ArrayList<SelectItem> listaMiniCedis = null;
    private DAOMiniCedis dao;
    private MiniCedis minicedis = new MiniCedis();

    public MbAgentes() {
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
        ok = this.validarAgente();
        if (ok == true) {
            ok = mbContribuyente.valida();
            if (ok == true) {
                if (mbDireccion.getDireccion().getCalle().equals("")) {
                    ok = false;
                    fMsg.setDetail("Se requiere una Dirección!!");
                    if (!ok) {
                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                    }
                    context.addCallbackParam("okContribuyente", ok);
                } else {
                    if (minicedis.getIdCedis() == 0) {
                        ok = false;
                        fMsg.setDetail("Se requiere un Cedis!!");
                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                        context.addCallbackParam("okContribuyente", ok);
                    } else {
                        DaoAgentes daoAgentes = new DaoAgentes();
                        boolean okExito = daoAgentes.guardarAgentes(mbContribuyente.getContribuyente(), mbDireccion.getDireccion(), agente, minicedis);
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

    public boolean isEditarAsentamiento() {
        return editarAsentamiento;
    }

    public void setEditarAsentamiento(boolean editarAsentamiento) {
        this.editarAsentamiento = editarAsentamiento;
    }

    public Direccion getCmbDireccion() {
        return cmbDireccion;
    }

    public void setCmbDireccion(Direccion cmbDireccion) {
        this.cmbDireccion = cmbDireccion;
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

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
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

    public void setListaMiniCedis(ArrayList<SelectItem> listaMiniCedis) {
        this.listaMiniCedis = listaMiniCedis;
    }

    public MiniCedis getMinicedis() {
        return minicedis;
    }

    public void setMinicedis(MiniCedis minicedis) {
        this.minicedis = minicedis;
    }
}
