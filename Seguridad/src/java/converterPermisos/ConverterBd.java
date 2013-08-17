/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.BaseDato;
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
public class ConverterBd implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int idBaseDatos = Integer.parseInt(value);
        BaseDato bd = new BaseDato();
        DaoPer daoPermisos = new DaoPer();
        try {
            bd = daoPermisos.dameBaseDatos(idBaseDatos);
        } catch (SQLException ex) {
            Logger.getLogger(ConverterBd.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bd;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String idBaseDatos;
        BaseDato b = (BaseDato) value;

        idBaseDatos = Integer.toString(b.getIdBaseDatos());
        return idBaseDatos;
    }
}
