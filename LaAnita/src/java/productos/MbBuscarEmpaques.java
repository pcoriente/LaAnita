package productos;

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
import productos.dao.DAOEmpaques;
import productos.dao.DAOPartes;
import productos.dominio.Empaque;
import productos.dominio.Parte2;

/**
 *
 * @author jsolis
 */
@Named(value = "mbBuscarEmpaques")
@SessionScoped
public class MbBuscarEmpaques implements Serializable {
    private String tipoBuscar;
    private String strBuscar;
    private Parte2 parte;
    private Empaque producto;
    private ArrayList<Empaque> productos;
    
    public MbBuscarEmpaques() {
        this.inicializa();
    }
    
    public void buscarLista() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            DAOEmpaques dao=new DAOEmpaques();
            if (this.tipoBuscar.equals("1")) {
                this.producto=dao.obtenerEmpaque(this.strBuscar);
                if (this.producto == null) {
                    fMsg.setDetail("No se encontró producto con el SKU proporcionado");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                } else {
                    ok = true;
                }
            } else {
                this.producto = null;
                this.productos = dao.obtenerEmpaquesParte(this.parte.getIdParte());
            }
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okBuscar", ok);
    }
    
    public void verCambio() {
        if (this.tipoBuscar.equals("2")) {
            this.parte = new Parte2(0, "");
        } else {
            this.strBuscar = "";
        }
        this.productos=null;
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
    
    public void inicializar() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.tipoBuscar = "2";
        this.strBuscar = "";
        this.parte = new Parte2(0, "");
    }
    
    public void obtenerEmpaquesParte(int idParte) {
        
    }

    public String getTipoBuscar() {
        return tipoBuscar;
    }

    public void setTipoBuscar(String tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    public String getStrBuscar() {
        return strBuscar;
    }

    public void setStrBuscar(String strBuscar) {
        this.strBuscar = strBuscar;
    }

    public Parte2 getParte() {
        return parte;
    }

    public void setParte(Parte2 parte) {
        this.parte = parte;
    }

    public Empaque getProducto() {
        return producto;
    }

    public void setProducto(Empaque producto) {
        this.producto = producto;
    }

    public ArrayList<Empaque> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Empaque> productos) {
        this.productos = productos;
    }
}
