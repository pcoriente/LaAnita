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
import productos.dao.DAOUnidades;
import productos.dao.DAOUnidadesEmpaque;
import productos.dominio.Unidad;
import productos.dominio.UnidadEmpaque;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbUnidadProducto")
@SessionScoped
public class MbUnidadProducto implements Serializable {
    private Unidad unidad;
    private ArrayList<Unidad> unidades;
    private DAOUnidades dao;
    
    public MbUnidadProducto() {
        this.unidad=new Unidad();
    }
    
    public boolean eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOUnidades();
            this.dao.eliminar(this.unidad.getIdUnidad());
            this.unidades=this.dao.obtenerUnidades();
            this.unidad = new Unidad();
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
        context.addCallbackParam("okUnidadProducto", ok);
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
                this.dao = new DAOUnidades();
                if(this.unidad.getIdUnidad()==0) {
                    this.unidad.setIdUnidad(this.dao.agregar(this.unidad));
                } else {
                    this.dao.modificar(this.unidad);
                }
                this.unidades=this.dao.obtenerUnidades();
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
        context.addCallbackParam("okUnidadProducto", ok);
        return ok;
    }
    
    public Unidad copia(Unidad u) {
        return new Unidad(u.getIdUnidad(), u.getUnidad(), u.getAbreviatura());
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public ArrayList<Unidad> getUnidades() {
        return unidades;
    }

    public void setUnidades(ArrayList<Unidad> unidades) {
        this.unidades = unidades;
    }
}
