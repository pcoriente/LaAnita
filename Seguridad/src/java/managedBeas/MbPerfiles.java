/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeas;

import daoPermisos.DaoPer;
import dominios.Perfil;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@SessionScoped
public class MbPerfiles implements Serializable {

    private List<SelectItem> listaPerfiles;
    private Perfil perfil = new Perfil();
    private Perfil perfilCmb = new Perfil();

    /**
     * Creates a new instance of MbPerfiles
     */
    public MbPerfiles() {
    }

    public Perfil getPerfilCmb() {
        return perfilCmb;
    }

    public void setPerfilCmb(Perfil perfilCmb) {
        this.perfilCmb = perfilCmb;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public List<SelectItem> getListaPerfiles() {
        try {
            listaPerfiles = damePerfiles();
        } catch (SQLException ex) {
            Logger.getLogger(MbPerfiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaPerfiles;
    }

    public void setListaPerfiles(List<SelectItem> listaPerfiles) {
        this.listaPerfiles = listaPerfiles;
    }

    private List<SelectItem> damePerfiles() throws SQLException {
        List<SelectItem> perfiles = new ArrayList<SelectItem>();
        ArrayList<Perfil> perfil = new ArrayList<Perfil>();
        DaoPer dp = new DaoPer();
        Perfil pF = new Perfil();
        pF.setPerfil("Nuevo Perfil");
        pF.setIdPerfil(0);
        SelectItem itemModulo = new SelectItem(pF, pF.getPerfil());
        perfiles.add(itemModulo);
        perfil = dp.damePefiles();
        for (Perfil pf : perfil) {
            perfiles.add(new SelectItem(pf, pf.getPerfil()));
        }
        return perfiles;
    }
}
