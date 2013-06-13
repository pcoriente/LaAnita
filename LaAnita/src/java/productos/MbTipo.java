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
import productos.dao.DAOTipos;
import productos.dominio.Tipo;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbTipo")
@SessionScoped
public class MbTipo implements Serializable {

    private Tipo tipo;
    private ArrayList<Tipo> tipos;
    private DAOTipos dao;

    public MbTipo() {
        this.tipo = new Tipo(0, "");
    }

    public ArrayList<Tipo> obtenerTipos() {
        boolean ok = false;
        this.tipos=new ArrayList<Tipo>();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOTipos();
            this.tipos = this.dao.obtenerTipos();
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
        context.addCallbackParam("okTipo", ok);
        return this.tipos;
    }

    public Tipo copia(Tipo t) {
        return new Tipo(t.getIdTipo(), t.getTipo());
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public ArrayList<Tipo> getTipos() {
        return tipos;
    }

    public void setTipos(ArrayList<Tipo> tipos) {
        this.tipos = tipos;
    }
}
