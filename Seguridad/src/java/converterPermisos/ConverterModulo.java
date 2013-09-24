/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.Modulo;
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
public class ConverterModulo implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Modulo modulo = new Modulo();
        int idModulo = Integer.parseInt(value);
        DaoPer dao = new DaoPer();
        try {
            modulo = dao.dameModulo(idModulo);
        } catch (SQLException ex) {
            Logger.getLogger(ConverterModulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modulo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String idModulo = null;
        Modulo m = (Modulo) value;
        int idMod = m.getIdModulo();
        idModulo = Integer.toString(idMod);
        return idModulo;
    }
}
