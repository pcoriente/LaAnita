/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import agentes.dao.DaoAgentes;
import agentes.dominio.Agentes;
import cedis.MbMiniCedis;
import cedis.dao.DAOCedis;
import cedis.dao.DAOMiniCedis;
import cedis.dominio.MiniCedis;
import contactos.MbContactos;
import contactos.dao.DAOContactos;
import contactos.dao.DAOTelefonos;
import contactos.dominio.Contacto;
import contactos.dominio.Telefono;
import contactos.dominio.TelefonoTipo;
import contribuyentes.DAOContribuyentes;
import contribuyentes.MbContribuyentes;
import direccion.MbDireccion;
import direccion.dao.DAODireccion;
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
import utilerias.Utilerias;

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
    private Agentes seleccionListaAgentes = new Agentes();
    private Agentes agente = new Agentes();
    private ArrayList<SelectItem> listaAsentamientos = new ArrayList<SelectItem>();
    private int personaFisica = 0;
    private ArrayList<SelectItem> listaMiniCedis = null;
    private DAOMiniCedis dao;
    private int flgDireccion = 0;
    boolean editarAsentamiento = false;
    private String lblCancelar = "";
    private String titleCancelar = "";
    private String lblnuevoAgente = "";
    private String lblNuevaDireccionAgente = "";
    private int actualizar = 0;
    ArrayList<SelectItem> listaTelefonos = new ArrayList<SelectItem>();

    public MbAgentes() {
        titleCancelar = "Cancelar Contacto";
        lblCancelar = "ui-icon-arrowreturnthick-1-w";
        lblnuevoAgente = "ui-icon-disk";
        lblNuevaDireccionAgente = "ui-icon-disk";
    }

    private void cargarTablaAgentes() {
        try {
//            if (listaAgentes == null) {
            DaoAgentes daoAgentes = new DaoAgentes();
            listaAgentes = daoAgentes.listaAgentes();
//            }
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
            agente.getContribuyente().setContribuyente(mbContribuyente.getContribuyente().getContribuyente());
            agente.getContribuyente().setRfc(mbContribuyente.getContribuyente().getRfc());
            agente.getContribuyente().setCurp(mbContribuyente.getContribuyente().getCurp());
            if (agente.getContribuyente().getDireccion().getCalle().equals("")) {
                ok = false;
                fMsg.setDetail("Se requiere una Dirección!!");
                if (!ok) {
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                }
                context.addCallbackParam("okContribuyente", ok);
            } else {
                ok = this.validarAgente();
                if (ok == true) {
                    if (agente.getMiniCedis().getIdCedis() == 0) {
                        ok = false;
                        fMsg.setDetail("Se requiere un Cedis!!");
                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                    } else {
                        if (ok == true) {
                            try {
                                if (this.agente.getContacto().getCorreo().equals("")) {
                                    ok = false;
                                    fMsg.setDetail("Error!! Correo Requerido");
                                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                                } else {
                                    Utilerias u = new Utilerias();
                                    boolean paso = u.validarEmail(this.agente.getContacto().getCorreo());
                                    if (paso == true) {
                                        listaAgentes = null;
                                        DaoAgentes daoAgentes = new DaoAgentes();
                                        if (actualizar == 0) {
                                            boolean okExito = daoAgentes.guardarAgentes(agente);
                                            if (okExito == true) {
                                                ok = true;
                                                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                                                fMsg.setDetail("Exito!! Nuevo Agente Disponible");
                                                FacesContext.getCurrentInstance().addMessage(null, fMsg);
                                            }
                                        } else {
                                            try {
                                                DAOContactos daoContactos = new DAOContactos();
                                                daoContactos.modificar(agente.getContacto());
                                                DAOContribuyentes daoContribuyente = new DAOContribuyentes();
                                                DaoAgentes daoAgente = new DaoAgentes();
//                                                DAOMiniCedis daoMinicedis = new DAOMinCedis();
//                                                daoMinicedis.
                                                daoContribuyente.actualizarContribuyente(mbContribuyente.getContribuyente());
                                                daoContribuyente.actualizarContribuyenteRfc(mbContribuyente.getContribuyente());
                                                daoAgente.actualizarAgente(agente);
                                                this.setActualizar(0);
                                                ok = true;
                                                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                                                fMsg.setDetail("Exito!! Nuevo Agente Actualizado");
                                                FacesContext.getCurrentInstance().addMessage(null, fMsg);

                                            } catch (NamingException ex) {
                                                Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    } else {
                                        ok = false;
                                        fMsg.setDetail("Error!! Correo no Valido");
                                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                                    }
                                }
                                context.addCallbackParam("okContribuyente", ok);
                            } catch (SQLException ex) {
                                Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
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
        if (this.getActualizar() == 1) {
            try {
                DAODireccion daoDireccion = new DAODireccion();
                try {
                    daoDireccion.modificar(mbDireccion.getDireccion().getIdDireccion(), mbDireccion.getDireccion().getCalle(), mbDireccion.getDireccion().getNumeroExterior(), mbDireccion.getDireccion().getNumeroInterior(), mbDireccion.getDireccion().getReferencia(), mbDireccion.getDireccion().getPais().getIdPais(), mbDireccion.getDireccion().getCodigoPostal(), mbDireccion.getDireccion().getEstado(), mbDireccion.getDireccion().getMunicipio(), mbDireccion.getDireccion().getLocalidad(), mbDireccion.getDireccion().getColonia(), mbDireccion.getDireccion().getNumeroLocalizacion());
                } catch (SQLException ex) {
                    Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public void cargarDatosActualizar() {
        try {
            this.setActualizar(1);
            ArrayList<Telefono> telefono = new ArrayList<Telefono>();
            mbContribuyente.getContribuyente().setIdContribuyente(seleccionListaAgentes.getContribuyente().getIdContribuyente());
            mbContribuyente.getContribuyente().setRfc(seleccionListaAgentes.getContribuyente().getRfc());
            mbContribuyente.getContribuyente().setCurp(seleccionListaAgentes.getContribuyente().getCurp());
            mbContribuyente.getContribuyente().setContribuyente(seleccionListaAgentes.getContribuyente().getContribuyente());
            mbContribuyente.getContribuyente().setIdRfc(seleccionListaAgentes.getContribuyente().getIdRfc());
            this.agente.getMiniCedis().setIdCedis(seleccionListaAgentes.getMiniCedis().getIdCedis());
            this.agente.setAgente(seleccionListaAgentes.getAgente());
            this.agente.setIdAgente(seleccionListaAgentes.getIdAgente());
            this.agente.setDireccionAgente(seleccionListaAgentes.getDireccionAgente());
            this.agente.getContribuyente().setDireccion(seleccionListaAgentes.getContribuyente().getDireccion());
            DAOTelefonos telefonos = new DAOTelefonos();
            this.agente.getContacto().setCorreo(seleccionListaAgentes.getContacto().getCorreo());
            this.agente.getContacto().setIdContacto(seleccionListaAgentes.getContacto().getIdContacto());
            try {
                telefono = telefonos.obtenerTelefonos(seleccionListaAgentes.getContacto().getIdContacto());
                TelefonoTipo t0 = new TelefonoTipo(false);
                t0.setTipo("Nuevo Tipo");
                listaTelefonos = new ArrayList<SelectItem>();
                listaTelefonos.add(new SelectItem(t0, t0.toString()));
                for (Telefono t : telefono) {
                    listaTelefonos.add(new SelectItem(t, t.toString()));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbAgentes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deseleccionar() {
        if (this.getActualizar() == 1) {
            seleccionListaAgentes = null;
            this.setActualizar(0);
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
        limpiarCampos();
        this.getMbContactos().cargaContactos(2, 0);
        this.getMbContactos().getMbTelefonos().cargaTelefonos(0);
    }

    public void validarTelefonos() {
        boolean ok = false;
        ok = this.mbContactos.getMbTelefonos().validarTelefonos();
        if (ok == true) {
            Telefono t = new Telefono();
            mbContactos.getMbTelefonos().getTelefono();
            t.setLada(mbContactos.getMbTelefonos().getTelefono().getLada());
            t.setTelefono(mbContactos.getMbTelefonos().getTelefono().getTelefono());
            t.setTipo(mbContactos.getMbTelefonos().getTelefono().getTipo());
            agente.getContacto().getTelefonos().add(t);
            cargaListaTelefonos();
        }
    }

    public void cargaListaTelefonos() {
        TelefonoTipo t0 = new TelefonoTipo(false);
        t0.setTipo("Nuevo Tipo");
        listaTelefonos = new ArrayList<SelectItem>();
        listaTelefonos.add(new SelectItem(t0, t0.toString()));
        for (Telefono t : this.agente.getContacto().getTelefonos()) {
            listaTelefonos.add(new SelectItem(t, t.toString()));
        }
    }

    public void limpiarCampos() {
        mbContribuyente.getContribuyente().setRfc("");
        mbContribuyente.getContribuyente().setCurp("");
        mbContribuyente.getContribuyente().setContribuyente("");
//      mbCedis.getCedis().setIdCedis(0);
        this.agente.getMiniCedis().setIdCedis(0);
        this.agente.getTelefono().setIdTelefono(0);
        this.agente.setAgente("");
        this.agente.getContacto().setCorreo("");
        this.agente.setDireccionAgente(new Direccion());
        this.agente.getContribuyente().setDireccion(new Direccion());
    }

    public void cargaTipos() {
        mbContactos.getMbTelefonos().cargaTipos();
        if (agente.getIdAgente() > 0) {
//            agente.getTelefono().get;
            mbContactos.getMbTelefonos().setTelefono(agente.getTelefono());
//            mbContactos.getMbTelefonos().getTelefono();
        }
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

    public ArrayList<SelectItem> getListaTipos() {
        return listaTelefonos;
    }

    public void setListaTipos(ArrayList<SelectItem> listaTipos) {
        this.listaTelefonos = listaTipos;
    }

    public String getLblnuevoAgente() {
        return lblnuevoAgente;
    }

    public void setLblnuevoAgente(String lblnuevoAgente) {
        this.lblnuevoAgente = lblnuevoAgente;
    }

    public String getLblNuevaDireccionAgente() {
        return lblNuevaDireccionAgente;
    }

    public void setLblNuevaDireccionAgente(String lblNuevaDireccionAgente) {
        this.lblNuevaDireccionAgente = lblNuevaDireccionAgente;
    }

    public int getActualizar() {
        return actualizar;
    }

    public void setActualizar(int actualizar) {
        this.actualizar = actualizar;
    }
}
