/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import daoPermisos.DaoPer;
import dominios.ModuloSubMenu;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author Comodoro
 */
public class ConverterModuloSubMenu implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int id = Integer.parseInt(value);
        ModuloSubMenu moduloSubMenu=null;
        try {
            if(id==0) {
                moduloSubMenu=new ModuloSubMenu();
            } else {
                DaoPer daoPermi = new DaoPer();
                moduloSubMenu = daoPermi.dameSubModulosMenu(id);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_SubMenu_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return moduloSubMenu;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        ModuloSubMenu m;
        String id = null;
        try {
            m =  (ModuloSubMenu) value;
            id = Integer.toString(m.getIdSubMenu());
        } catch (Exception e) {
            System.err.println(e);
        }
        return id;
    }
}
