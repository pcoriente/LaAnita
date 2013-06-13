package productos;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOPartes;
import productos.dominio.Parte;
import productos.dominio.Parte2;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbParte")
@SessionScoped
public class MbParte implements Serializable {
    @ManagedProperty(value = "#{parte}")
    private Parte2 parte;
    DAOPartes dao;

    public MbParte() {
        this.parte=new Parte2(0, "");
    }
    
    public boolean eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOPartes();
            this.dao.eliminar(this.parte.getIdParte());
            this.parte=new Parte2(0, "");
            ok=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okParte", ok);
        return ok;
    }
    
    public boolean grabar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.parte==null || this.parte.getParte().equals("")) {
            fMsg.setDetail("Se requiere identificar la parte");
        } else {
            try {
                this.dao = new DAOPartes();
                if(this.parte.getIdParte()==0) {
                    this.parte.setIdParte(this.dao.agregar(this.parte.getParte()));
                } else {
                    this.dao.modificar(this.parte);
                }
                ok=true;
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okParte", ok);
        return ok;
    }

    public ArrayList<Parte2> completePartes(String query) {
        ArrayList<Parte2> partes = null;
        try {
            DAOPartes dao = new DAOPartes();
            partes = dao.completePartes(query);
        } catch (SQLException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return partes;
    }

    public Parte2 getParte2() {
        return parte;
    }

    public void setParte2(Parte2 parte) {
        this.parte = parte;
    }
}
