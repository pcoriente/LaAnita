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
public class ConverterPickBds implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        BaseDato bd = new BaseDato();
        bd.setBaseDatos(value);
        return bd;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        BaseDato bd = (BaseDato) value;
        return bd.getBaseDatos();
    }
}
