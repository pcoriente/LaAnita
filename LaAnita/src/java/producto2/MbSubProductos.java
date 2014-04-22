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
import producto2.dao.DAOSubProductos;
import producto2.dominio.Empaque;
import producto2.dominio.SubProducto;

/**
 *
 * @author jesc
 */
@Named(value = "mbSubProductos")
@SessionScoped
public class MbSubProductos implements Serializable {
    private ArrayList<SelectItem> listaSubProductos;
    private DAOSubProductos dao;
    
    public MbSubProductos() {
    }
    
    public void cargaListaSubProductos() {
        this.listaSubProductos=new ArrayList<SelectItem>();
        SubProducto sp0=new SubProducto(0, 0, new Empaque(0, "SELECCIONE", ""));
        this.listaSubProductos.add(new SelectItem(sp0, sp0.toString(),"",false));
    }
    
    public void cargaListaSubProductos(int idArticulo, int idProducto) {
        boolean ok = false;
        this.listaSubProductos=new ArrayList<SelectItem>();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "");
        try {
            this.dao=new DAOSubProductos();
            SubProducto sp0=new SubProducto(0, 0, new Empaque(0, "SELECCIONE", ""));
            this.listaSubProductos.add(new SelectItem(sp0, sp0.toString(),"",false));
            for(SubProducto sp: dao.obtenerSubProductos(idArticulo)) {
                this.listaSubProductos.add(new SelectItem(sp, sp.toString(),"",(sp.getIdProducto()==idProducto?true:false)));
            }
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public ArrayList<SelectItem> getListaSubProductos() {
        return listaSubProductos;
    }

    public void setListaSubProductos(ArrayList<SelectItem> listaSubProductos) {
        this.listaSubProductos = listaSubProductos;
    }
}
