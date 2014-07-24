package contribuyentes;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import utilerias.Utilerias;

/**
 *
 * @author jsolis
 */
@Named(value = "mbContribuyentes")
@SessionScoped
public class MbContribuyentes implements Serializable {

    private Contribuyente contribuyente;
    private ArrayList<Contribuyente> contribuyentes;
    private DAOContribuyentes dao;
    private ArrayList<SelectItem> listaContribuyentes;

    public MbContribuyentes() {
        this.contribuyente = new Contribuyente();
        this.contribuyentes = new ArrayList<Contribuyente>();
    }

    public void cancelar() {
        boolean ok = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("okContribuyente", ok);
    }

    public boolean valida() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.contribuyente.getRfc().isEmpty()) {
            fMsg.setDetail("Se requiere el RFC !!");
        } else if (this.contribuyente.getContribuyente().isEmpty()) {
            fMsg.setDetail("Se requiere el nombre comercial o razón social del contribuyente !!");
        } else if (this.contribuyente.getRfc().length() > 0) {
            Utilerias utilerias = new Utilerias();
            String mensaje = utilerias.verificarRfc(this.getContribuyente().getRfc());
            if (mensaje.equals("") && this.getContribuyente().getRfc().trim().length() == 13) {
                boolean validacion = utilerias.validarCurp(this.contribuyente.getCurp());
                if (validacion == false) {
                    fMsg.setDetail("Error! Curp no valido");
                } else {
                    ok = true;
                }
            } else if (this.getContribuyente().getRfc().trim().length() == 12 && mensaje.equals("")) {
                ok = true;
            } else {
                fMsg.setDetail(mensaje);
            }
        } else {
            ok = true;
        }
        if (!ok) {

            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okContribuyente", ok);
        return ok;
    }

    public Contribuyente copia(Contribuyente contribuyente) {
        Contribuyente c = new Contribuyente();
        c.setIdContribuyente(contribuyente.getIdContribuyente());
        c.setIdRfc(contribuyente.getIdRfc());
        c.setRfc(contribuyente.getRfc());
        c.setContribuyente(contribuyente.getContribuyente());
        c.setDireccion(contribuyente.getDireccion());
        return c;
    }

    public Contribuyente obtenerContribuyente(int idContribuyente) {
        Contribuyente c = null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            c = this.dao.obtenerContribuyente(idContribuyente);
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        return c;
    }

    public void obtenerContribuyentesRFC() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            int idRfc = this.dao.obtenerRfc(this.contribuyente.getRfc());
            if (idRfc == 0) {
                idRfc = this.dao.grabarRFC(this.contribuyente.getRfc());
                this.contribuyente.setIdRfc(idRfc);
                this.contribuyentes = new ArrayList<Contribuyente>();
            } else {
                this.contribuyentes = this.dao.obtenerContribuyentesRFC(this.contribuyente.getRfc());
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
    }

    public boolean verificar(String rfc) {
        boolean ok = false;

        return ok;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public ArrayList<Contribuyente> getContribuyentes() {
        return contribuyentes;
    }

    public void setContribuyentes(ArrayList<Contribuyente> contribuyentes) {
        this.contribuyentes = contribuyentes;
    }

    public void limpiarContribuyente() {
        contribuyente = new Contribuyente();
    }

    public ArrayList<SelectItem> getListaContribuyentes() {
        if (listaContribuyentes == null) {
            try {
                listaContribuyentes = new ArrayList<SelectItem>();
                DAOContribuyentes dao = new DAOContribuyentes();
                Contribuyente c = new Contribuyente();
                c.setIdContribuyente(0);
                c.setContribuyente("Seleccione Contribuyente");
                listaContribuyentes.add(new SelectItem(c, c.getContribuyente()));
                for (Contribuyente contr : dao.dameContribuyentes()) {
                    listaContribuyentes.add(new SelectItem(contr, contr.getContribuyente()));
                }
            } catch (NamingException ex) {
                Message.Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Message.Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return listaContribuyentes;
    }

    public void setListaContribuyentes(ArrayList<SelectItem> listaContribuyentes) {
        this.listaContribuyentes = listaContribuyentes;
    }

}
