/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.ModuloMenu;
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
public class ConverterModuloMenu implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ModuloMenu moduloMenu = new ModuloMenu();
        int id = Integer.parseInt(value);
        
        try {
            if(id==0) {
                moduloMenu = new ModuloMenu();
            } else {
                DaoPer daoPermisos = new DaoPer();
                moduloMenu = daoPermisos.dameModulosMenu(id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConverterModuloMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return moduloMenu;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        ModuloMenu m = (ModuloMenu) value;
        String id = Integer.toString(m.getIdMenu());
        return id;
    }
}
