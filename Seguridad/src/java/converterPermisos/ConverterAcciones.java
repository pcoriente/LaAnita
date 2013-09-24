/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.Accion;
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
public class ConverterAcciones implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int idAccion;
        idAccion= Integer.parseInt(value);
        Accion acciones = new Accion();
        DaoPer daoPermisos = new DaoPer();
        try {
            acciones = daoPermisos.dameAcciones(idAccion);
        } catch (SQLException ex) {
            Logger.getLogger(ConverterAcciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return acciones;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String idAcciones;
        Accion acciones = (Accion) value;
        idAcciones = Integer.toString(acciones.getIdAccion());
        return idAcciones;
    }
}
