package productos;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOUnidadesEmpaque;
import productos.dominio.UnidadEmpaque;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbUnidadEmpaque")
@SessionScoped
public class MbUnidadEmpaque implements Serializable {
    private UnidadEmpaque unidad;
    private ArrayList<UnidadEmpaque> unidades;
    private DAOUnidadesEmpaque dao;
    
    public MbUnidadEmpaque() {
        this.unidad =  new UnidadEmpaque();
    }
    
    public boolean eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOUnidadesEmpaque();
            this.dao.eliminar(this.unidad.getIdUnidad());
            this.unidades=this.dao.obtenerUnidadesEmpaque();
            this.unidad = new UnidadEmpaque();
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
        context.addCallbackParam("okUnidadEmpaque", ok);
        return ok;
    }
    
    public boolean grabar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.unidad.getUnidad().isEmpty()) {
            fMsg.setDetail("Se requiere la unidad de empaque");
        } else if(this.unidad.getAbreviatura().isEmpty()) {
            fMsg.setDetail("Se requiere la abreviatura de la unidad de empaque");
        } else {
            try {
                this.dao = new DAOUnidadesEmpaque();
                if(this.unidad.getIdUnidad()==0) {
                    this.unidad.setIdUnidad(this.dao.agregar(this.unidad));
                } else {
                    this.dao.modificar(this.unidad);
                }
                this.unidades=this.dao.obtenerUnidadesEmpaque();
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
        context.addCallbackParam("okUnidadEmpaque", ok);
        return ok;
    }
    
    public void copia(UnidadEmpaque u) {
        this.unidad=new UnidadEmpaque(u.getIdUnidad(), u.getUnidad(), u.getAbreviatura());
    }

    public UnidadEmpaque getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadEmpaque unidad) {
        this.unidad = unidad;
    }

    public ArrayList<UnidadEmpaque> getUnidades() {
        return unidades;
    }

    public void setUnidades(ArrayList<UnidadEmpaque> unidades) {
        this.unidades = unidades;
    }
}
