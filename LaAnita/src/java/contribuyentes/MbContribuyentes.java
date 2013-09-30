package contribuyentes;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;

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
    
    public MbContribuyentes() {
        this.contribuyente = new Contribuyente();
        this.contribuyentes = new ArrayList<Contribuyente>();
    }
    
    public void cancelar() {
        boolean ok=true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("okContribuyente", ok);
    }
    
    public boolean valida() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        if(this.contribuyente.getRfc().isEmpty()) {
            fMsg.setDetail("Se requiere el RFC !!");
        } else if(this.contribuyente.getContribuyente().isEmpty()) {
            fMsg.setDetail("Se requiere el nombre comercial o razón social del contribuyente !!");
        } else {
            ok=true;
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okContribuyente", ok);
        return ok;
    }
    
    public Contribuyente copia(Contribuyente contribuyente) {
        Contribuyente c=new Contribuyente();
        c.setIdContribuyente(contribuyente.getIdContribuyente());
        c.setIdRfc(contribuyente.getIdRfc());
        c.setRfc(contribuyente.getRfc());
        c.setContribuyente(contribuyente.getContribuyente());
        c.setDireccion(contribuyente.getDireccion());
        return c;
    }
    
    public Contribuyente obtenerContribuyente(int idContribuyente) {
        Contribuyente c=null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            c=this.dao.obtenerContribuyente(idContribuyente);
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
            this.dao=new DAOContribuyentes();
            int idRfc=this.dao.obtenerRfc(this.contribuyente.getRfc());
            if(idRfc==0) {
                idRfc=this.dao.grabarRFC(this.contribuyente.getRfc());
                this.contribuyente.setIdRfc(idRfc);
                this.contribuyentes=new ArrayList<Contribuyente>();
            } else {
                this.contribuyentes=this.dao.obtenerContribuyentesRFC(this.contribuyente.getRfc());
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
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
}