/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeas;

import daoPermisos.DaoPer;
import dominios.BaseDato;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@SessionScoped
public class MbBasesDatos implements Serializable {

    private BaseDato baseDatos = new BaseDato();
    private List<SelectItem> listaBaseDatos;
    DualListModel<BaseDato> pickBd = new DualListModel<BaseDato>();
    ArrayList<BaseDato> DestinoBd = new ArrayList<BaseDato>();
    ArrayList<BaseDato> OrigenBd = new ArrayList<BaseDato>();

    public void setPickBd(DualListModel<BaseDato> pickBd) {
        this.pickBd = pickBd;
    }

    public ArrayList<BaseDato> getDestinoBd() {
        return DestinoBd;
    }

    public void setDestinoBd(ArrayList<BaseDato> DestinoBd) {
        this.DestinoBd = DestinoBd;
    }

    public ArrayList<BaseDato> getOrigenBd() {
        return OrigenBd;
    }

    public void setOrigenBd(ArrayList<BaseDato> OrigenBd) {
        this.OrigenBd = OrigenBd;
    }

    /**
     * Creates a new instance of MbBasesDatos
     */
    public MbBasesDatos() {
    }

    public DualListModel<BaseDato> getPickBd() throws SQLException {
        ArrayList<BaseDato> a1 = new ArrayList<BaseDato>();
        ArrayList<BaseDato> a2 = new ArrayList<BaseDato>();
        DaoPer daoPermisos = new DaoPer();
        a1 = daoPermisos.dameListaBds();
        a2 = daoPermisos.dameBaseDatos();
        for (int x = 0; x < a1.size(); x++) {
            for (int y = 0; y < a2.size(); y++) {
                BaseDato b = new BaseDato();
                BaseDato b2 = new BaseDato();
                b.setBaseDatos(a1.get(x).getBaseDatos());
                b2.setBaseDatos(a2.get(y).getBaseDatos());
                String valor1 = a1.get(x).getBaseDatos();
                String valor2 = a2.get(y).getBaseDatos();
                if (valor1.equals(valor2)) {
                    a1.remove(x);
                    x--;
                    break;
                }
            }
        }
        pickBd = new DualListModel<BaseDato>(a1, a2);

        return pickBd;
    }

    public List<SelectItem> getListaBaseDatos() {
        listaBaseDatos = new ArrayList<SelectItem>();
        try {
            listaBaseDatos = dameBd();
        } catch (SQLException ex) {
            Logger.getLogger(MbUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaBaseDatos;
    }

    private List<SelectItem> dameBd() throws SQLException {
        List<SelectItem> Bds = new ArrayList<SelectItem>();
        ArrayList<BaseDato> bds = new ArrayList<BaseDato>();
        DaoPer dp = new DaoPer();
        BaseDato baseDatos = new BaseDato();
        baseDatos.setBaseDatos("Seleccione BD");
        baseDatos.setIdBaseDatos(0);
        SelectItem itemModulo = new SelectItem(baseDatos, baseDatos.getBaseDatos());
        Bds.add(itemModulo);
        bds = dp.dameBaseDatos();
        for (BaseDato bd : bds) {
            Bds.add(new SelectItem(bd, bd.getBaseDatos()));
        }
        return Bds;
    }

    public void setListaBaseDatos(List<SelectItem> listaBaseDatos) {
        this.listaBaseDatos = listaBaseDatos;
    }

    public BaseDato getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(BaseDato baseDatos) {
        this.baseDatos = baseDatos;
    }

    public void dameBdsPickList() throws SQLException {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn;
        ArrayList<BaseDato> bd = new ArrayList<BaseDato>();
        if (bd.size() > 0) {
        } else {
            bd = (ArrayList<BaseDato>) pickBd.getTarget();
            pickBd.getSource();
            DaoPer p = new DaoPer();
            p.insertarBd(bd);
            if (pickBd.getTarget().size() == 0) {
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Removido", "Las bases de Datos fueron removidas");
            } else {
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregadas", "Nuevas Bases de Datos disponibles");
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("Bd´s", loggedIn);
        }
    }
}
