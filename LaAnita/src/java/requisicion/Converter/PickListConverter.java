/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.Converter;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import requisicion.dao.DaoRequisicion;
import requisicion.dominio.DominioPartes;

/**
 *
 * @author Comodoro
 */
public class PickListConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int id = Integer.parseInt(value);
        DominioPartes dp = new DominioPartes();
        DaoRequisicion d = new DaoRequisicion();
        try {
            dp = d.damePartes(id);
        } catch (SQLException ex) {
            Logger.getLogger(PickListConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dp;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        DominioPartes p = (DominioPartes) value;
        int id = p.getIdParte();
        String idP = Integer.toString(id);
        return idP;
    }
}
