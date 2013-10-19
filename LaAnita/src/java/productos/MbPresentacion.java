package productos;

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
import productos.dao.DAOPresentaciones;
import productos.dominio.Presentacion;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbPresentacion")
@SessionScoped
public class MbPresentacion implements Serializable {
    private Presentacion presentacion;
    private ArrayList<Presentacion> presentaciones;
    private ArrayList<SelectItem> listaPresentaciones;
    private DAOPresentaciones dao;
    
    public MbPresentacion() {
        this.presentacion=new Presentacion();
    }
    
    public boolean eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOPresentaciones();
            this.dao.eliminar(this.presentacion.getIdPresentacion());
            this.presentaciones=this.dao.obtenerPresentaciones();
            this.presentacion = new Presentacion();
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
        context.addCallbackParam("okPresentacion", ok);
        return ok;
    }
    
    public boolean grabar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.presentacion.getPresentacion().isEmpty()) {
            fMsg.setDetail("Se requiere la presentación");
        } else if(this.presentacion.getAbreviatura().isEmpty()) {
            fMsg.setDetail("Se requiere la abreviatura de la presentación");
        } else {
            try {
                this.dao = new DAOPresentaciones();
                if(this.presentacion.getIdPresentacion()==0) {
                    this.presentacion.setIdPresentacion(this.dao.agregar(this.presentacion));
                } else {
                    this.dao.modificar(this.presentacion);
                }
                this.presentaciones=this.dao.obtenerPresentaciones();
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
        context.addCallbackParam("okPresentacion", ok);
        return ok;
    }
    
    public Presentacion copia(Presentacion presentacion) {
        return new Presentacion(presentacion.getIdPresentacion(), presentacion.getPresentacion(), presentacion.getAbreviatura());
    }
    
    private void cargaPresentaciones() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        this.listaPresentaciones = new ArrayList<SelectItem>();
        Presentacion pres = new Presentacion(0, "SELECCIONE", "");
        this.listaPresentaciones.add(new SelectItem(pres, pres.toString()));
        try {
            DAOPresentaciones daoPresentaciones = new DAOPresentaciones();
            ArrayList<Presentacion> lstPresentaciones = daoPresentaciones.obtenerPresentaciones();
            for (Presentacion p : lstPresentaciones) {
                this.listaPresentaciones.add(new SelectItem(p, p.toString()));
            }
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
        context.addCallbackParam("okPresentacion", ok);
    }

    public Presentacion getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(Presentacion presentacion) {
        this.presentacion = presentacion;
    }

    public ArrayList<Presentacion> getPresentaciones() {
        return presentaciones;
    }

    public void setPresentaciones(ArrayList<Presentacion> presentaciones) {
        this.presentaciones = presentaciones;
    }

    public ArrayList<SelectItem> getListaPresentaciones() {
        if(this.listaPresentaciones==null) {
            this.cargaPresentaciones();
        }
        return listaPresentaciones;
    }

    public void setListaPresentaciones(ArrayList<SelectItem> listaPresentaciones) {
        this.listaPresentaciones = listaPresentaciones;
    }
}
