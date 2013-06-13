package usuarios;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import usuarios.dominio.Usuario;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "usuarioSesion")
@SessionScoped
public class UsuarioSesion {
    private Usuario usuario;
    private String jndi;
    
    public String getJndi() {
        return jndi;
    }

    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
