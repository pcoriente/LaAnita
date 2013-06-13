package dbs;

import dbs.dao.DAODbs;
import dbs.dominio.Dbs;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import usuarios.UsuarioSesion;
import usuarios.dominio.Usuario;

@ManagedBean(name = "mbDbs")
@RequestScoped
public class MbDbs implements Serializable {
    private Long id;
    private String login;
    private String password;
    @ManagedProperty(value = "#{usuarioSesion}")
    private UsuarioSesion usuarioSesion;
    private Dbs dbs;
    private List<SelectItem> listaDbs;
    DAODbs dao;

    public MbDbs() throws NamingException {
        dao=new DAODbs();
    }

    public Dbs getDbs() {
        return dbs;
    }

    public void setDbs(Dbs dbs) {
        this.dbs = dbs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsuarioSesion getUsuarioSesion() {
        return usuarioSesion;
    }

    public void setUsuarioSesion(UsuarioSesion usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    public List<SelectItem> getListaDbs() {
        try {
            this.listaDbs = obtenerBases();
        } catch (NamingException ex) {
            Logger.getLogger(MbDbs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbDbs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDbs;
    }

    public List<SelectItem> obtenerBases() throws NamingException, SQLException {
        List<SelectItem> bases = new ArrayList<SelectItem>();
        
        Dbs db = new Dbs();
        db.setIdDbs(0);
        db.setNombreBds("Seleccione la Base: ");
        SelectItem p0 = new SelectItem(db, db.getNombreBds());
        bases.add(p0);

        DAODbs daoDbs = new DAODbs();
        Dbs[] rDbs = daoDbs.obtenerDbs();

        for (Dbs po : rDbs) {
            bases.add(new SelectItem(po, po.getNombreBds()));
        }
        return bases;
    }
    
    public String doLogin() throws SQLException, NamingException {
        FacesMessage mensajeErrorLogin = new FacesMessage();
        String outcome;

        Usuario usuario=this.dao.login(this.login, this.password, this.dbs.getJndiDbs());
        if (usuario == null) {
            mensajeErrorLogin.setDetail("Usuario no válido !!!");
            FacesContext.getCurrentInstance().addMessage(null, mensajeErrorLogin);
            outcome = "fallo.login";
        } else if(usuario.getId()==0) {
            mensajeErrorLogin.setDetail("Clave incorrecta !!!");
            FacesContext.getCurrentInstance().addMessage(null, mensajeErrorLogin);
            outcome = "fallo.login";
        } else {
            /*
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion userSession = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            userSession.setUsuario(usuario);
            userSession.setJndi(this.dbs.getJndiDbs());
            httpSession.setAttribute("usuarioSesion", userSession);
             * 
             */
            this.usuarioSesion.setUsuario(usuario);
            this.usuarioSesion.setJndi(this.dbs.getJndiDbs());
            outcome = "exito.login";
        }
        return outcome;
    }
}
