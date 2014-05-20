package monedas;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author jesc
 */
@Named(value = "mbMonedas")
@Dependent
public class MbMonedas implements Serializable {
    
    private ArrayList<SelectItem> listaMonedas;
    private Moneda moneda;
    private DAOMonedas dao;

    public MbMonedas() {
        this.moneda=new Moneda();
    }
    
    public Moneda obtenerMoneda(int idMoneda) {
        boolean ok = false;
        Moneda m=null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "cerradaOficina");
        try {
            this.dao=new DAOMonedas();
            m=this.dao.obtenerMoneda(idMoneda);
            if(m==null) {
                fMsg.setDetail("No se encontro la moneda solicitada");
            } else {
                ok=true;
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
        return m;
    }
    
    private ArrayList<SelectItem> obtenerListaMonedas() throws NamingException {
        listaMonedas=new ArrayList<SelectItem>();
        try {
            Moneda moneda = new Moneda();
            moneda.setIdMoneda(0);
            moneda.setMoneda("Monedas: ");
            listaMonedas.add(new SelectItem(moneda, moneda.toString()));
            DAOMonedas dao=new DAOMonedas();
            ArrayList<Moneda> monedas = dao.obtenerMonedas();
            for (Moneda e : monedas) {
                listaMonedas.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMonedas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMonedas;
    }
    
    public ArrayList<SelectItem> getListaMonedas() throws NamingException {
        if(this.listaMonedas==null) {
            listaMonedas = this.obtenerListaMonedas();
        }
        return listaMonedas;
    }

    public void setListaMonedas(ArrayList<SelectItem> listaMonedas) {
        this.listaMonedas = listaMonedas;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
    
    
    
}
