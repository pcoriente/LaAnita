package main;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import usuarios.UsuarioSesion;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbMain")
@ViewScoped
public class MbMain {
    private Date date;
    private String comenta;

    public String getComenta() {
        return comenta;
    }

    public void setComenta(String comenta) {
        this.comenta = comenta;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpSession httpSession = (HttpSession) externalContext.getSession(false);
        
        UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
        if (usuarioSesion == null) {
            usuarioSesion = new UsuarioSesion();
            httpSession.setAttribute("usuarioSesion", usuarioSesion);
        } else if(usuarioSesion.getUsuario()!=null) {
            usuarioSesion.setUsuario(null);
        }
        usuarioSesion.setJndi("jdbc/__systemWeb");
        httpSession.invalidate();
        return "login.xhtml";
    }
}
