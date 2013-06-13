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
import productos.dao.DAOUpcs;
import productos.dominio.Upc;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbUpc")
@SessionScoped
public class MbUpc implements Serializable {

    private Upc upc;
    private ArrayList<Upc> upcs;
    //private ArrayList<SelectItem> listaUpcs;

    public MbUpc() {
        this.upc = new Upc(0);
        this.upcs = new ArrayList<Upc>();
        this.upcs.add(upc);
    }

    public boolean eliminar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            int idProducto=this.upc.getIdProducto();
            DAOUpcs daoUpcs = new DAOUpcs();
            daoUpcs.Eliminar(this.upc.getIdUpc());
            this.upcs = daoUpcs.obtenerUpcs(idProducto);
            this.upc=new Upc(idProducto);
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
        context.addCallbackParam("okUpc", ok);
        return ok;
    }

    public boolean agregar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if (this.upc.getUpc().isEmpty()) {
                fMsg.setDetail("Se requiere el UPC !!");
            } else {
                DAOUpcs daoUpcs = new DAOUpcs();
                if (this.upc.getIdProducto() == 0) {
                    int i = 0;
                    for (; i < this.upcs.size(); i++) {
                        if (this.upcs.get(i).getUpc().equals(this.upc.getUpc())) {
                            break;
                        }
                    }
                    if (daoUpcs.existeUpc(this.upc.getUpc())) {
                        fMsg.setDetail("El UPC ya existe en otro producto, no se puede duplicar !!");
                    } else {
                        if (i == this.upcs.size()) {
                            this.upcs.add(this.upc);
                        } else {
                            this.upc = this.upcs.get(i);
                        }
                        ok=true;
                    }
                } else {
                    this.upc.setIdUpc(daoUpcs.agregar(this.upc));
                    this.upcs.add(this.upc);
                    ok=true;
                }
            }
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
        context.addCallbackParam("okUpc", ok);
        return ok;
    }

    public Upc getUpc() {
        return upc;
    }

    public void setUpc(Upc upc) {
        this.upc = upc;
    }

    public ArrayList<Upc> getUpcs() {
        return upcs;
    }

    public void setUpcs(ArrayList<Upc> upcs) {
        this.upcs = upcs;
    }
    /*
     public ArrayList<SelectItem> getListaUpcs() {
     return listaUpcs;
     }

     public void setListaUpcs(ArrayList<SelectItem> listaUpcs) {
     this.listaUpcs = listaUpcs;
     }
     * */
}
