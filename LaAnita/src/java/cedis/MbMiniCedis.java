package cedis;

import cedis.dao.DAOMiniCedis;
import cedis.dominio.MiniCedis;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author jsolis
 */
@Named(value = "mbMiniCedis")
@SessionScoped
public class MbMiniCedis implements Serializable {
    private MiniCedis cedis;
    private ArrayList<SelectItem> listaMiniCedis =null;
    private DAOMiniCedis dao;
    
    public MbMiniCedis() {
        this.cedis=new MiniCedis();
    }
    
    private void obtenerListaMiniCedis() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.listaMiniCedis=new ArrayList<SelectItem>();
        try {
            MiniCedis p0 = new MiniCedis();
            p0.setIdCedis(0);
            p0.setCedis("Seleccione un CEDIS");
            SelectItem cero = new SelectItem(p0, p0.toString());
            listaMiniCedis.add(cero);
            
            this.dao=new DAOMiniCedis();
            for (MiniCedis m : this.dao.obtenerListaMiniCedis()) {
                listaMiniCedis.add(new SelectItem(m, m.toString()));
            }
            ok=true;
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
    }

    public MiniCedis getCedis() {
        return cedis;
    }

    public void setCedis(MiniCedis cedis) {
        this.cedis = cedis;
    }

    public ArrayList<SelectItem> getListaMiniCedis() {
        if(this.listaMiniCedis==null) {
            this.obtenerListaMiniCedis();
        }
        return listaMiniCedis;
    }

    public void setListaMiniCedis(ArrayList<SelectItem> listaMiniCedis) {
        this.listaMiniCedis = listaMiniCedis;
    }
}
