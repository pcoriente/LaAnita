package productos;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOGrupos;
import productos.dominio.Grupo;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbGrupo")
@SessionScoped
public class MbGrupo implements Serializable {

    private Grupo grupo;
    private ArrayList<Grupo> grupos;
    private DAOGrupos dao;

    public MbGrupo() {
        this.grupo = new Grupo(0, 0, "SELECCIONE UN GRUPO");
    }

    public boolean eliminar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOGrupos();
            this.dao.eliminar(this.grupo.getIdGrupo());
            this.grupos = this.dao.obtenerGrupos();
            this.grupo = new Grupo(0, 0, "");
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
        context.addCallbackParam("okGrupo", ok);
        return ok;
    }

    public boolean grabar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.grupo.getGrupo().isEmpty()) {
            fMsg.setDetail("Se requiere el grupo");
        } else {
            try {
                this.dao = new DAOGrupos();
                if (this.grupo.getIdGrupo() == 0) {
                    this.grupo.setIdGrupo(this.dao.agregar(this.grupo));
                } else {
                    this.dao.modificar(this.grupo);
                }
                this.grupos = dao.obtenerGrupos();
                ok = true;
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okGrupo", ok);
        return ok;
    }

    public Grupo copia(Grupo g) {
        return new Grupo(g.getIdGrupo(), g.getCodigo(), g.getGrupo());
    }

    public int obtenerUltimoGrupo() {
        int ultimo = 0;
        boolean ok = false;
        //RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOGrupos();
            ultimo = this.dao.obtenerUltimoCodigoGrupo();
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
        //context.addCallbackParam("okGrupo", ok);
        return ultimo;
    }

    public ArrayList<Grupo> obtenerGrupos() {
        boolean ok = false;
        this.grupos = new ArrayList<Grupo>();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOGrupos();
            this.grupos = this.dao.obtenerGrupos();
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
        context.addCallbackParam("okGrupo", ok);
        return this.grupos;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(ArrayList<Grupo> grupos) {
        this.grupos = grupos;
    }
}
