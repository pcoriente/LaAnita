/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import formatos.DAOFormatos.DAOFormatos;
import formatos.dominio.Formato;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import mbMenuClientesGrupos.MbClientesGrupos;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Usuario
 */
@Named(value = "mbFormatos")
@SessionScoped
public class MbFormatos implements Serializable {

    private ArrayList<SelectItem> lstItems = null;
    @ManagedProperty(value = "#{mbClientesGrupos}")
    private MbClientesGrupos mbClientesGrupos = new MbClientesGrupos();
    private Formato formato = new Formato();
    private Formato cmbFormato = new Formato();

    /**
     * Creates a new instance of MbFormatos
     */
    public MbFormatos() {
    }

    public boolean validar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
        if (formato.getFormato().equals("")) {
            fMsg.setDetail("Se requiere un nombre del Formato");
        } else if (mbClientesGrupos.getCmbClientesGrupos().getIdGrupoCte() == 0) {
            fMsg.setDetail("Seleccione un Grupo");
        } else {
            ok = true;
        }
        if (ok == false) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("ok", ok);
        return ok;
    }

    public ArrayList<SelectItem> getLstItems() {
        if (lstItems == null) {
            try {
                DAOFormatos dao = new DAOFormatos();
                ArrayList<Formato> lstFormato = new ArrayList<Formato>();
                try {
                    lstFormato = dao.dameListaFormatos();
                } catch (SQLException ex) {
                    Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
                }
                Formato format = new Formato();
                format.setFormato("Nuevo Formato");
                format.setIdFormato(0);
                lstItems = new ArrayList<SelectItem>();
                lstItems.add(new SelectItem(format, format.getFormato()));
                for (Formato formato : lstFormato) {
                    SelectItem select = new SelectItem(formato, formato.getFormato());
                    lstItems.add(select);
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstItems;
    }

    public void setLstItems(ArrayList<SelectItem> lstItems) {
        this.lstItems = lstItems;
    }

    public MbClientesGrupos getMbClientesGrupos() {
        return mbClientesGrupos;
    }

    public void setMbClientesGrupos(MbClientesGrupos mbClientesGrupos) {
        this.mbClientesGrupos = mbClientesGrupos;
    }

    public Formato getFormato() {
        return formato;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public Formato getCmbFormato() {
        return cmbFormato;
    }

    public void setCmbFormato(Formato cmbFormato) {
        this.cmbFormato = cmbFormato;
    }

}
