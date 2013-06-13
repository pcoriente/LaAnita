package productos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productos.dao.DAOUnidades;
import productos.dominio.Unidad;

/**
 *
 * @author JULIOS
 */
public class UnidadConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Unidad unidad=null;
        try {
            int idUnidad=Integer.parseInt(value);
            if(idUnidad==0) {
                unidad=new Unidad(0, "", "");
            } else {
                DAOUnidades dao=new DAOUnidades();
                unidad=dao.obtenerUnidad(idUnidad);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_UnidadConverter_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return unidad;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            Unidad unidad=(Unidad) value;
            val=Integer.toString(unidad.getIdUnidad());
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_UnidadConverter_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
    
}
