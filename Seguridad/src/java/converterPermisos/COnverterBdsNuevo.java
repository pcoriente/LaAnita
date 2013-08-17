/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converterPermisos;

import dominios.BaseDato;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Comodoro
 */
public class COnverterBdsNuevo implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        BaseDato b = new BaseDato();
        b.setBaseDatos(value);
        return b;

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        BaseDato b = (BaseDato) value;
        return b.getBaseDatos();
    }
}
