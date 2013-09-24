/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.Perfil;
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
public class ConverterPerfil implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Perfil p = new Perfil();
        int id = Integer.parseInt(value);
        DaoPer daoPermiso = new DaoPer();
        try {
            p = daoPermiso.damePerfil(id);
        } catch (SQLException ex) {
            Logger.getLogger(ConverterPerfil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Perfil p = (Perfil) value;
        String id = Integer.toString(p.getIdPerfil());
        return id;

    }
}
