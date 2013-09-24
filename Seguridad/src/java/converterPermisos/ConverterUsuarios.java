/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.DominioUsuario;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Comodoro
 */
public class ConverterUsuarios implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DaoPer d = new DaoPer();
        int id = Integer.parseInt(value);

        DominioUsuario u = new DominioUsuario();
        try {
            u = d.dameUsuarios(id);
        } catch (SQLException ex) {
            Logger.getLogger(ConverterUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }

        return u;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        DominioUsuario u = (DominioUsuario) value;
        String valor = null;
        int id = u.getIdUsuario();
        try {
            valor = Integer.toString(id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return valor;
    }
}
