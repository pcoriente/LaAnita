package managedBeas;

import daoPermisos.DaoPer;
import dominios.Login;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import utilerias.Utilerias;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@RequestScoped
public class MbLogin {

    private Login login = new Login();

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public MbLogin() {
    }

    public String validarLogin() throws SQLException, Exception {
        String pagina = "";
        Utilerias u = new Utilerias();
        DaoPer p = new DaoPer();
        String pass = p.damePassword();
        String pass2 = u.md5(login.getPassword());
        boolean paso = pass.equals(pass2);
        if (paso == true) {
            pagina = "altasPermisos.xhtml";
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,"Denegado!!", "Ingrese la contraseña correcta"));  
        }
        return pagina;
    }
}