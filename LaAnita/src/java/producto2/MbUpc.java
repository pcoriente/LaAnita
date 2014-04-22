package producto2;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import producto2.dao.DAOUpcs;
import producto2.dominio.Upc;

/**
 *
 * @author jesc
 */
@Named(value = "mbUpc")
@SessionScoped
public class MbUpc implements Serializable {
    private Upc upc;
    private ArrayList<SelectItem> listaUpcs;
    private DAOUpcs dao;
    
    public MbUpc() {
        this.upc=new Upc();
    }
    
    public boolean eliminar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "");
        try {
            this.dao = new DAOUpcs();
            this.dao.Eliminar(this.upc.getUpc());
            this.cargaListaUpcs();
            ok=true;
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okUpc", ok);
        return ok;
    }
    
    public boolean agregar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "");
        try {
            this.dao = new DAOUpcs();
            this.dao.agregar(this.upc);
            this.cargaListaUpcs();
            ok=true;
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okUpc", ok);
        return ok;
    }
    
    public void cargaListaUpcs() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "");
        this.listaUpcs=new ArrayList<SelectItem>();
        Upc u0=this.nuevoLista(this.upc.getIdProducto());
        this.listaUpcs.add(new SelectItem(u0, u0.toString()));
        try {
            this.dao=new DAOUpcs();
            for(Upc u:this.dao.obtenerUpcs(this.upc.getIdProducto())) {
                this.listaUpcs.add(new SelectItem(u, u.toString()));
            }
            ok=true;
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okUpc", ok);
    }
    
    public void copia(Upc u) {
        this.upc.setUpc(u.getUpc());
        this.upc.setIdProducto(u.getIdProducto());
    }
    
    public Upc nuevoLista(int idProducto) {
        return new Upc("SELECCIONE", idProducto);
    }
    
    public void nuevo(int idProducto) {
        this.upc.setUpc("");
        this.upc.setIdProducto(idProducto);
    }

    public Upc getUpc() {
        return upc;
    }

    public void setUpc(Upc upc) {
        this.upc = upc;
    }

    public ArrayList<SelectItem> getListaUpcs() {
        return listaUpcs;
    }

    public void setListaUpcs(ArrayList<SelectItem> listaUpcs) {
        this.listaUpcs = listaUpcs;
    }
}
