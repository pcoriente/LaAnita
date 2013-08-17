/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeas;

import daoPermisos.DaoPer;
import dominios.Accion;
import dominios.BaseDato;
import dominios.Modulo;
import dominios.ModuloSubMenu;
import dominios.UsuarioPerfil;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.context.RequestContext;
import utilerias.Utilerias;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@SessionScoped
//@ApplicationScoped
public class MbSeguridad implements Serializable {

    @ManagedProperty(value = "#{mbBasesDatos}")
    private MbBasesDatos mbBasesDatos = new MbBasesDatos();
    @ManagedProperty(value = "#{mbUsuarios}")
    private MbUsuarios mbUsuarios = new MbUsuarios();
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones = new MbAcciones();
    @ManagedProperty(value = "#{mbModulos}")
    private MbModulos mbModulos = new MbModulos();
    @ManagedProperty(value = "#{mbPerfiles}")
    private MbPerfiles mbPerfiles = new MbPerfiles();
    private ModuloSubMenu sub = new ModuloSubMenu();
    private int aparecer;
    private int aparecerSubMenu;

    public int getAparecerSubMenu() {
        return aparecerSubMenu;
    }

    public void setAparecerSubMenu(int aparecerSubMenu) {
        this.aparecerSubMenu = aparecerSubMenu;
    }

    public int getAparecer() {
        return aparecer;
    }

    public void setAparecer(int aparecer) {
        this.aparecer = aparecer;
    }

    public void control() {
        this.setAparecer(1);
        this.setAparecerSubMenu(0);
    }

    public void controlSubMenu() {
        this.setAparecerSubMenu(1);
        this.setAparecer(0);
    }

    public void cancelarDRendereds() {
        this.setAparecerSubMenu(0);
        this.setAparecer(0);
    }

    public ModuloSubMenu getSub() {
        return sub;
    }

    public void setSub(ModuloSubMenu sub) {
        this.sub = sub;
    }

    public MbSeguridad() {
        mbBasesDatos = new MbBasesDatos();
        mbUsuarios = new MbUsuarios();
        mbAcciones = new MbAcciones();
        mbModulos = new MbModulos();
        mbPerfiles = new MbPerfiles();
    }

    public void actualizarUsuarioPerfil() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        try {
            DaoPer daoPermisos = new DaoPer();
            int idPerfil = mbUsuarios.getP2().getIdPerfil();
            mbUsuarios.getUsuarioCmb().getIdUsuario();
            if (idPerfil == 0) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Seleccione un Perfil");
            } else {
                loggedIn = true;
                daoPermisos.ActualizarUsuario(mbUsuarios);
//                mbUsuarios.getUsuarioCmb().setIdUsuario(0);
//                mbUsuarios.getP2().setIdPerfil(0);
                limpiarModificacionesPerfilUsuario();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "El perfil del usuario se ha modificado");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbSeguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void guardarValores() throws SQLException {
        if (mbBasesDatos.getBaseDatos().getIdBaseDatos() == 0 && mbPerfiles.getPerfil().getIdPerfil() == 0 && mbModulos.getModulo().getIdModulo() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Seleccione todas las Opciones"));
        } else if (mbBasesDatos.getBaseDatos().getIdBaseDatos() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Seleccione una Base de Datos"));
        } else if (mbPerfiles.getPerfil().getIdPerfil() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Seleccione un perfil"));
        } else if (mbModulos.getModuloCmb().getIdModulo() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Seleccione un Modulo"));
        } else {
            mbPerfiles.getPerfilCmb().getIdUsuario();
            ArrayList<Accion> acciones = new ArrayList<Accion>();
            acciones = (ArrayList<Accion>) mbAcciones.getPickAcciones().getTarget();
            UsuarioPerfil usuaPerfil = new UsuarioPerfil();
            String jndi = mbBasesDatos.getBaseDatos().getJndi();
            DaoPer daoPermisos = new DaoPer(jndi);
            usuaPerfil.setIdPerfil(mbPerfiles.getPerfilCmb().getIdPerfil());
            usuaPerfil.setIdModulo(mbModulos.getModuloCmb().getIdModulo());
            if (usuaPerfil.getIdModulo() != 0 && usuaPerfil.getIdPerfil() != 0) {
                daoPermisos.insertarUsuarioPerfil(usuaPerfil, acciones);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Se insertaron los datos Correctamente"));
            }
        }
    }

    public void cancelarUsuarioPerfil() {
        mbBasesDatos = new MbBasesDatos();
        mbModulos = new MbModulos();
        mbAcciones = new MbAcciones();
        mbPerfiles = new MbPerfiles();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cancelado", "Datos Cancelados"));
    }

    public void guardarModulo() throws SQLException {
        String strSubMenu = mbModulos.getModuloSubMenuCmb().getSubMenu();
        DaoPer daoPer = new DaoPer();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        if (mbModulos.getModulo().getIdModulo() != 0) {
            mbModulos.getModulo().getModulo();
            mbModulos.getModulo().setIdModulo(mbModulos.getModuloCmb().getIdModulo());
            daoPer.ActualizarModulos(mbModulos.getModulo());
            loggedIn = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "El Modulo fue Actualizado Correctamente");
        } else {
            if (mbModulos.getModulo().getModulo().equals("")) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese un Nombre de Modulo");
            } else {
                int identity = daoPer.guardarModulo(mbModulos.getModulo());
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevo Modulo Disponible");
                mbModulos.getModulo().setIdModulo(identity);
                mbModulos.getModulo().setModulo(mbModulos.getModulo().getModulo());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void guardarModuloMenu() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        String moduloMenu = mbModulos.getModuloMenu().getMenu();
        if (moduloMenu.equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Denegado", "Ingrese un menu");
        } else {
            DaoPer daoPermisos = new DaoPer();
            try {
                daoPermisos.guardarModuloMenu(mbModulos.getModuloMenu());
                mbModulos.getModuloMenu().setMenu("");
                this.setAparecer(0);
            } catch (SQLException ex) {
                Logger.getLogger(MbSeguridad.class.getName()).log(Level.SEVERE, null, ex);
            }
            loggedIn = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevo Menu Disponible");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void guardarModuloSubMenu() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        if (mbModulos.getM().getIdMenu() == 0 && sub.getSubMenu().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Llene todos los campos");
        } else if (mbModulos.getM().getIdMenu() == 0) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Seleccione un Menu");
        } else if (sub.getSubMenu().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Escriba un SubMenu");
        } else {
            try {
                ModuloSubMenu moduloSubMenu = new ModuloSubMenu();
                DaoPer daoPer = new DaoPer();
                moduloSubMenu.setSubMenu(sub.getSubMenu());
                moduloSubMenu.setIdMenu(mbModulos.getM().getIdMenu());
                daoPer.insertarSubMenu(moduloSubMenu);
                sub.setSubMenu("");
                mbModulos.getM().setIdMenu(0);
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevos SubModulos Disponibles");
                this.setAparecerSubMenu(0);
            } catch (SQLException ex) {
                Logger.getLogger(MbSeguridad.class.getName()).log(Level.SEVERE, null, ex);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", ex.toString());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void guardarPerfil() throws SQLException {
        DaoPer daoPer = new DaoPer();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        if (mbPerfiles.getPerfilCmb().getIdPerfil() != 0) {
            mbPerfiles.getPerfil().getPerfil();
            mbPerfiles.getPerfil().setIdPerfil(mbPerfiles.getPerfilCmb().getIdPerfil());
            daoPer.ActualizarPerfiles(mbPerfiles.getPerfil());
            loggedIn = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Perfil Actualizado");
        } else {
            if (mbPerfiles.getPerfil().getPerfil().equals("")) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese un Nombre de Perfil");
            } else {
                mbUsuarios.getUsuario().getIdUsuario();
                String perfil = mbPerfiles.getPerfil().getPerfil();
                int identity = daoPer.insertarPerfil(mbPerfiles.getPerfil());
                dameModulosAcciones(identity);
                mbPerfiles.getPerfilCmb().setIdPerfil(identity);
                mbPerfiles.getPerfil().setPerfil(perfil);
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevo Perfil Disponible");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void limpiarAltasModulos() {
        mbModulos.getModuloCmb().setIdMenu(0);
        mbModulos.getModuloMenucmb2().setIdMenu(0);
        mbModulos.getModulo().setUrl("");
        mbModulos.getModulo().setModulo("");
        mbModulos.getModuloSubMenuCmb().setIdMenu(0);
        ArrayList<SelectItem> select = new ArrayList<>();
        mbModulos.setModuloSubMenuCmb2(select);
    }

    public void guardarValoresModulo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        DaoPer daoPermisos = new DaoPer();
        mbModulos.getModulo().getModulo();
        mbModulos.getModulo().getUrl();
        mbModulos.getModulo().setIdMenu(mbModulos.getModuloMenucmb2().getIdMenu());
        try {
            mbModulos.getModulo().setIdSubMenu(mbModulos.getModuloSubMenuCmb().getIdSubMenu());
        } catch (Exception e) {
            mbModulos.getModulo().setIdSubMenu(0);
        }

        try {
            if (mbModulos.getModulo().getModulo().equals("") && mbModulos.getModulo().getUrl().equals("") && mbModulos.getModulo().getIdMenu() == 0) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese todas las Opciones");
            } else if (mbModulos.getModulo().equals("")) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese un Modulo");
            } else if (mbModulos.getModulo().getUrl().equals("")) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese una Url");
            } else if (mbModulos.getModulo().getIdMenu() == 0) {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Seleccione un Menu Modulo");
            } else {
                int id = daoPermisos.guardarModulo(mbModulos.getModulo());
                mbModulos.getModuloCmb().setIdModulo(id);
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevos modulos Disponibles");
                int x = 0;
                mbModulos.getModuloCmb().setIdMenu(0);
                mbModulos.getModuloMenucmb2().setIdMenu(0);
                mbModulos.getModulo().setUrl("");
                mbModulos.getModulo().setModulo("");
                mbModulos.getModuloSubMenuCmb().setIdMenu(0);
                ArrayList<SelectItem> select = new ArrayList<>();
                mbModulos.setModuloSubMenuCmb2(select);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbModulos.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void guardarAcciones() throws SQLException {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        if (mbAcciones.getAcion().getAccion().equals("") && mbAcciones.getAcion().getIdBoton().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese una Accion y un IdButom");
            dameModulosAcciones(0);
        } else if (mbAcciones.getAcion().getIdBoton().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese el idBotom");
            dameModulosAcciones(0);
        } else if (mbAcciones.getAcion().getAccion().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Ingrese una Acción");
            dameModulosAcciones(0);
        } else {
            loggedIn = true;
            DaoPer daoPermisos = new DaoPer();
            mbAcciones.getAcion().setIdMOdulo(mbModulos.getModuloCmb().getIdModulo());
            daoPermisos.insertarAcciones(mbAcciones.getAcion());
            dameModulosAcciones(0);
//            mbAcciones = new MbAcciones();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevas Acciones Disponibles");
//            RequestContext.getCurrentInstance().execute("dlg.hide();");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void dameBdsPickList() throws SQLException {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn;
        ArrayList<BaseDato> bd = new ArrayList<BaseDato>();
        if (bd.size() > 0) {
        } else {
            bd = (ArrayList<BaseDato>) mbBasesDatos.getPickBd().getTarget();
            mbBasesDatos.getPickBd().getSource();
            DaoPer p = new DaoPer();
            p.insertarBd(bd);
            if (mbBasesDatos.getPickBd().getTarget().size() == 0) {
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

    public void dameModulosAcciones(int id) throws SQLException {
        mbAcciones = new MbAcciones();
        if (mbPerfiles.getPerfilCmb().getIdPerfil() != 0) {
            mbPerfiles.getPerfil().setIdPerfil(mbPerfiles.getPerfilCmb().getIdPerfil());
            mbPerfiles.getPerfil().setPerfil(mbPerfiles.getPerfilCmb().getPerfil());
        }
        if (mbModulos.getModulo().getIdModulo() != 0) {
            mbModulos.getModulo().setModulo(mbModulos.getModuloCmb().getModulo());
        }
        if (mbBasesDatos.getBaseDatos().getIdBaseDatos() == 0) {
            mbBasesDatos = new MbBasesDatos();
        }
        if (mbPerfiles.getPerfilCmb().getIdPerfil() == 0) {
            mbPerfiles = new MbPerfiles();
        }
        if (mbModulos.getModuloCmb().getIdModulo() == 0) {
            mbModulos = new MbModulos();
        }
        String nomBd = mbBasesDatos.getBaseDatos().getBaseDatos();
        int idPerfil = 0;
        if (id > 0) {
            idPerfil = id;
        } else {
            idPerfil = mbPerfiles.getPerfilCmb().getIdPerfil();
        }
        int idModulo = mbModulos.getModuloCmb().getIdModulo();

        if (idPerfil != 0 && idModulo != 0 && nomBd != null) {
            String perfil = mbPerfiles.getPerfilCmb().getPerfil();
            mbPerfiles.getPerfil().setPerfil(perfil);
            DaoPer daoPermisos = new DaoPer();
            ArrayList<Accion> acciones = new ArrayList<Accion>();
            acciones = daoPermisos.dameValores(nomBd, idModulo, idPerfil);
            for (Accion ac : acciones) {
                if (ac.getIdPerfil() == 0) {
                    mbAcciones.accionesOrigen.add(ac);
                } else {
                    mbAcciones.accionesDestino.add(ac);
                }
            }
        }
    }

    public void insertarDatos() throws SQLException, Exception {
        DaoPer daoUsuario = new DaoPer();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean loggedIn = false;
        boolean validarEmail = false;
        Utilerias utilerias = new Utilerias();
        if (mbUsuarios.getUsuario().getUsuario().equals("") && mbUsuarios.getUsuario().getLogin().equals("") && mbUsuarios.getUsuario().getPassword().equals("") && mbUsuarios.getUsuario().getEmail().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Llene todos los Campos");
        }
        if (mbUsuarios.getUsuario().getUsuario().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Escriba un Usuario");
        } else if (mbUsuarios.getUsuario().getLogin().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Escriba un Login");
        } else if (mbUsuarios.getUsuario().getPassword().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Escriba un Password");
        } else if (mbUsuarios.getUsuario().getEmail().equals("")) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Escriba un Email");
        } else {
            validarEmail = utilerias.validarEmail(mbUsuarios.getUsuario().getEmail());
            if (validarEmail == true) {
                String fecha = utilerias.dameFecha();
                daoUsuario.insertarUsuario(mbUsuarios.getUsuario(), mbBasesDatos.getBaseDatos().getIdBaseDatos(), mbPerfiles.getPerfilCmb().getIdPerfil());
//                bd.getIdBaseDatos();
//                perfil2.getIdPerfiles();
                mbUsuarios = new MbUsuarios();
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Nuevo Usuario Disponible");
            } else {
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Ingrese un Email Valido");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);
    }

    public void limpiarPerfiles() {
        if (mbPerfiles.getPerfilCmb().getIdPerfil() == 0) {
            mbPerfiles = new MbPerfiles();
        } else {
            String perfil = mbPerfiles.getPerfilCmb().getPerfil();
            mbPerfiles.getPerfil().setPerfil(perfil);
        }
    }

    public void limpiarModificacionesPerfilUsuario() {
        mbUsuarios.getUsuarioCmb().setIdUsuario(0);
        mbUsuarios.getP2().setIdPerfil(0);
    }

    public void limpiarUsuarios() {
        String perfil = mbPerfiles.getPerfilCmb().getPerfil();
        mbPerfiles.getPerfil().setPerfil(null);
        mbUsuarios = new MbUsuarios();
    }

    public void limpiarSubModulos() {
        mbModulos = new MbModulos();
        sub = new ModuloSubMenu();
    }

    public String home() {
        String pagina = "index.xhtml";
        return pagina;
    }

    public MbBasesDatos getMbBasesDatos() {
        return mbBasesDatos;
    }

    public void limpiarModulos() {
        mbModulos = new MbModulos();
    }

    public void setMbBasesDatos(MbBasesDatos mbBasesDatos) {
        this.mbBasesDatos = mbBasesDatos;
    }

    public MbUsuarios getMbUsuarios() {
        return mbUsuarios;
    }

    public void setMbUsuarios(MbUsuarios mbUsuarios) {
        this.mbUsuarios = mbUsuarios;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }

    public MbModulos getMbModulos() {
        return mbModulos;
    }

    public void setMbModulos(MbModulos mbModulos) {
        this.mbModulos = mbModulos;
    }

    public MbPerfiles getMbPerfiles() {
        return mbPerfiles;
    }

    public void setMbPerfiles(MbPerfiles mbPerfiles) {
        this.mbPerfiles = mbPerfiles;
    }
}
