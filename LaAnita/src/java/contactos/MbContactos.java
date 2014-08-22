package contactos;

import contactos.dao.DAOContactos;
import contactos.dominio.Contacto;
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
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import utilerias.Utilerias;

/**
 *
 * @author jsolis
 */
@Named(value = "mbContactos")
@SessionScoped
public class MbContactos implements Serializable {

    private Contacto contacto;
    private ArrayList<Contacto> contactos;
    private ArrayList<SelectItem> listaContactos;
    private ArrayList<SelectItem> listaCorreos;
    @ManagedProperty(value = "#{mbTelefonos}")
    private MbTelefonos mbTelefonos = new MbTelefonos();
    private DAOContactos dao;
    private Contacto correo = new Contacto();
//    private ArrayList<Contacto> listaCorreo;

    public MbContactos() {
        this.contacto = new Contacto();
        this.mbTelefonos = new MbTelefonos();
        listaContactos = new ArrayList<SelectItem>();
        mbTelefonos = new MbTelefonos();
    }

    public boolean eliminar(int idPadre) {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContactos();
            this.dao.eliminar(idPadre);
            ok = true;
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okContacto", ok);
        return ok;
    }

    public boolean grabar(int idTipo, int idPadre) {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if (this.contacto.getContacto().isEmpty()) {
                fMsg.setDetail("Se requiere la descripción del contacto");
            } else {
                this.dao = new DAOContactos();
                if (this.contacto.getIdContacto() == 0) {
                    this.contacto.setIdContacto(this.dao.agregar(this.contacto, idPadre, idTipo));
                } else {
                    this.dao.modificar(contacto);
                }
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okContacto", ok);
        return ok;
    }

    public boolean validarContactos() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.contacto.getContacto().equals("")) {
            fMsg.setDetail("Se requiere la descripción del contacto");
        } else if (this.contacto.getPuesto().equals("")) {
            fMsg.setDetail("Se requiere un puesto");
        } else if (this.contacto.getCorreo().equals("")) {
            fMsg.setDetail("Se requiere un correo");
        } else {
            Utilerias utilerias = new Utilerias();
            boolean validarCorreo = utilerias.validarEmail(this.contacto.getCorreo());
            if (validarCorreo == false) {
                fMsg.setDetail("Correo no valido");
            } else {
                ok = true;
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        context.addCallbackParam("okContacto", ok);
        return ok;
    }

    public ArrayList<Contacto> obtenerContactos(int idTipo, int idPadre) throws NamingException, SQLException {
        this.dao = new DAOContactos();
        ArrayList<Contacto> lstContactos = this.dao.obtenerContactos(idTipo, idPadre);
        for (Contacto c : lstContactos) {
            c.setTelefonos(this.mbTelefonos.obtenerTelefonos(c.getIdContacto()));
        }
        return lstContactos;
    }

    public void obtenerCorreo(int idContacto) throws NamingException, SQLException {
        if (listaCorreos == null) {
            this.dao = new DAOContactos();
            Contacto correo = new Contacto();
            correo.setIdContacto(0);
            correo.setCorreo("Correo disponible");
            listaCorreos = new ArrayList<SelectItem>();
            listaCorreos.add(new SelectItem(correo, correo.getCorreo()));
            for (Contacto c : this.dao.obtenerCorreos(idContacto)) {
                listaCorreos.add(new SelectItem(c, c.getCorreo()));
            }
        }
    }

    public void cargaContactos(int idTipo, int idPadre) {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.contactos = this.obtenerContactos(idTipo, idPadre);
            Contacto c0 = new Contacto();
            c0.setContacto("Nuevo Contacto");
            this.listaContactos = new ArrayList<SelectItem>();
            this.listaContactos.add(new SelectItem(c0, c0.toString()));
            for (Contacto c : this.contactos) {
                this.listaContactos.add(new SelectItem(c, c.toString()));
            }
            ok = true;
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public Contacto copia(Contacto c1) {
        Contacto c = new Contacto();
        c.setIdContacto(c1.getIdContacto());
        c.setContacto(c1.getContacto());
        c.setPuesto(c1.getPuesto());
        c.setCorreo(c1.getCorreo());
        c.setTelefonos(c1.getTelefonos());
//        for(Telefono t:c1.getTelefonos()) {
//            c.getTelefonos().add(this.mbTelefonos.copia(t));
//        }
        return c;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    public void setContactos(ArrayList<Contacto> contactos) {
        this.contactos = contactos;
    }

    public ArrayList<SelectItem> getListaContactos() {

        return listaContactos;
    }

    public void setListaContactos(ArrayList<SelectItem> listaContactos) {
    }

    public MbTelefonos getMbTelefonos() {
        this.listaContactos = listaContactos;
        return mbTelefonos;
    }

    public void setMbTelefonos(MbTelefonos mbTelefonos) {
        this.mbTelefonos = mbTelefonos;
    }

    public ArrayList<SelectItem> getListaCorreos() {
        return listaCorreos;
    }

    public void setListaCorreos(ArrayList<SelectItem> listaCorreos) {
        this.listaCorreos = listaCorreos;
    }

    public Contacto getCorreo() {
        return correo;
    }

    public void setCorreo(Contacto correo) {
        this.correo = correo;
    }
}
