package contactos;

import contactos.dao.DAOContactos;
import contactos.dominio.Contacto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;

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
    @ManagedProperty(value = "#{mbTelefonos}")
    private MbTelefonos mbTelefonos;
    private DAOContactos dao;

    public MbContactos() {
        this.contacto = new Contacto();
        this.mbTelefonos = new MbTelefonos();
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
        if (this.contacto.getContacto().isEmpty()) {
            fMsg.setDetail("Se requiere la descripción del contacto");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } else {
            ok = true;
        }
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
}
