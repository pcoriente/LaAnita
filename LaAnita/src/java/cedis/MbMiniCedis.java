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
    private ArrayList<SelectItem> listaCedis;
    private DAOMiniCedis dao;
    
    public MbMiniCedis() {
        this.cedis=new MiniCedis();
    }
    
    public ArrayList<SelectItem> obtenerListaMiniCedis() throws SQLException {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        ArrayList<SelectItem> listaMiniCedis=new ArrayList<SelectItem>();
        try {
            MiniCedis p0 = new MiniCedis();
            p0.setIdCedis(0);
            p0.setCedis("Seleccione un CEDIS");
            SelectItem cero = new SelectItem(p0, p0.toString());
            listaMiniCedis.add(cero);
            this.dao=new DAOMiniCedis();
            ArrayList<MiniCedis> lstMiniCedis=this.dao.obtenerListaMiniCedis();
            for (MiniCedis m : lstMiniCedis) {
                listaMiniCedis.add(new SelectItem(m, m.toString()));
            }
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
        return listaMiniCedis;
    }

    public MiniCedis getCedis() {
        return cedis;
    }

    public void setCedis(MiniCedis cedis) {
        this.cedis = cedis;
    }

    public ArrayList<SelectItem> getListaCedis() {
        return listaCedis;
    }

    public void setListaCedis(ArrayList<SelectItem> listaCedis) {
        this.listaCedis = listaCedis;
    }
}
