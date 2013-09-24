/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeas;

import daoPermisos.DaoPer;
import dominios.DominioUsuario;
import dominios.Perfil;
import dominios.PerfilesAcseso;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@SessionScoped
public class MbUsuarios implements Serializable {

    private DominioUsuario usuario = new DominioUsuario();
    private ArrayList<SelectItem> listaUsuarios = new ArrayList<>();
    private ArrayList<SelectItem> listaPerfiles = new ArrayList<>();
    private DominioUsuario usuarioCmb = new DominioUsuario();
    Perfil perfil = new Perfil();
    Perfil p2 = new Perfil();

    public Perfil getPerfil() {

        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Perfil getP2()  {
//        if (usuarioCmb.getIdUsuario() > 0) {
//            p2 = dameValoresPerfil(usuarioCmb.getIdUsuario());
//        }
        return p2;
    }

    public void setP2(Perfil p2) {
        this.p2 = p2;
    }

    public MbUsuarios() {
    }

    public DominioUsuario getUsuarioCmb() {
        return usuarioCmb;
    }

    public void setUsuarioCmb(DominioUsuario usuarioCmb) {
        this.usuarioCmb = usuarioCmb;
    }

    public ArrayList<SelectItem> getListaPerfiles() {
        try {
            listaPerfiles = damePerfil();
        } catch (SQLException ex) {
            Logger.getLogger(MbUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaPerfiles;
    }

    public void setListaPerfiles(ArrayList<SelectItem> listaPerfiles) {
        this.listaPerfiles = listaPerfiles;
    }

    public ArrayList<SelectItem> getListaUsuarios() {
        listaUsuarios = dameListaUsuarios();
        return listaUsuarios;
    }

    public void setListaUsuarios(ArrayList<SelectItem> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public DominioUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(DominioUsuario usuario) {
        this.usuario = usuario;
    }

    private ArrayList<SelectItem> dameListaUsuarios() {
        ArrayList<DominioUsuario> usuarios = new ArrayList<>();
        DaoPer daoPermisos = new DaoPer();
        ArrayList<SelectItem> lista = new ArrayList<>();
        try {
            DominioUsuario dominioUsuario = new DominioUsuario();
            dominioUsuario.setIdUsuario(0);
            dominioUsuario.setLogin("Seleccione un Usuario");
            SelectItem selectItem = new SelectItem(dominioUsuario, dominioUsuario.getLogin());
            lista.add(selectItem);
//            lista.add(selectItem);
            usuarios = daoPermisos.dameUsuarios();
            for (DominioUsuario dom : usuarios) {
                lista.add(new SelectItem(dom, dom.getLogin()));
            }
        } catch (Exception ex) {
            Logger.getLogger(MbUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    public void damePerfilUsuario() {
        try {
            p2 = dameValoresPerfil(usuarioCmb.getIdUsuario());
        } catch (SQLException ex) {
            Logger.getLogger(MbUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

    public ArrayList<SelectItem> damePerfil() throws SQLException {
        DaoPer daoPermisos = new DaoPer();
        ArrayList<Perfil> perfiles = new ArrayList<>();
        try {
            Perfil perfilN = new Perfil();
            perfilN.setIdPerfil(0);
            perfilN.setPerfil("Seleccione un perfil");
            SelectItem s = new SelectItem(perfilN, perfilN.getPerfil());
            listaPerfiles.add(s);
            perfiles = daoPermisos.damePefiles();
            for (Perfil perfil : perfiles) {
                listaPerfiles.add(new SelectItem(perfil, perfil.getPerfil()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.err.println(e);
        }
        return listaPerfiles;
    }

    private Perfil dameValoresPerfil(int idUsuario) throws SQLException {
        Perfil p = new Perfil();
        DaoPer daoPermisos = new DaoPer();
        PerfilesAcseso perfilAcceso = new PerfilesAcseso();

        perfilAcceso = daoPermisos.damePerfilUsuario(idUsuario);
        p.setIdPerfil(perfilAcceso.getIdPerfil());

        return p;
    }
}
