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
import productos.dao.DAOSubGrupos;
import productos.dominio.SubGrupo;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbSubGrupo")
@SessionScoped
public class MbSubGrupo implements Serializable {
    private SubGrupo subGrupo;
    private ArrayList<SubGrupo> subGrupos;
    private DAOSubGrupos dao;
    
    public MbSubGrupo() {
        this.subGrupo = new SubGrupo();
    }
    
    public boolean eliminar(int idSubGrupo) {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOSubGrupos();
            this.dao.eliminar(this.subGrupo.getIdSubGrupo());
            this.subGrupos=this.dao.obtenerSubGrupos(idSubGrupo);
            this.subGrupo=new SubGrupo(0, "");
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
        context.addCallbackParam("okSubGrupo", ok);
        return ok;
    }
    
    public boolean grabar(int idGrupo) {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.subGrupo.getSubGrupo().isEmpty()) {
            fMsg.setDetail("Se requiere el SubGrupo");
        } else {
            try {
                this.dao = new DAOSubGrupos();
                if(this.subGrupo.getIdSubGrupo()==0) {
                    this.subGrupo.setIdSubGrupo(this.dao.agregar(this.subGrupo, idGrupo));
                } else {
                    this.dao.modificar(this.subGrupo);
                }
                this.subGrupos=this.dao.obtenerSubGrupos(idGrupo);
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
        context.addCallbackParam("okSubGrupo", ok);
        return ok;
    }
    
    public SubGrupo copia(SubGrupo s) {
        return new SubGrupo(s.getIdSubGrupo(), s.getSubGrupo());
    } 
    
    public ArrayList<SubGrupo> obtenerSubGrupos(int idGrupo) {
        boolean ok = false;
        this.subGrupos=new ArrayList<SubGrupo>();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOSubGrupos();
            this.subGrupos = this.dao.obtenerSubGrupos(idGrupo);
            ok = true;
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
        context.addCallbackParam("okSubGrupo", ok);
        return this.subGrupos;
    }

    public SubGrupo getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(SubGrupo subGrupo) {
        this.subGrupo = subGrupo;
    }

    public ArrayList<SubGrupo> getSubGrupos() {
        return subGrupos;
    }

    public void setSubGrupos(ArrayList<SubGrupo> subGrupos) {
        this.subGrupos = subGrupos;
    }
}
