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
import requisicion.dominio.DominioEmpresas;

/**
 *
 * @author Comodoro
 */
public class RequsicionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DominioEmpresas d = new DominioEmpresas();
        int id = Integer.parseInt(value);
        DaoRequisicion daoRequi = new DaoRequisicion();
        try {
            d = daoRequi.dameEmpresas(id);
        } catch (SQLException ex) {
            Logger.getLogger(RequsicionConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return d;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String x = null;
        DominioEmpresas d = (DominioEmpresas) value;
        x = Integer.toString(d.getCodigoEmpresa());

        return x;

    }
}
