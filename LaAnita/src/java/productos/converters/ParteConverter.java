package productos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productos.dao.DAOPartes;
import productos.dominio.Parte;
import productos.dominio.Parte2;

/**
 *
 * @author JULIOS
 */
public class ParteConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Parte2 parte = null;
        try {
            int idParte=1;
            boolean error=false;
            try {
                idParte=Integer.parseInt(value);
            } catch (NumberFormatException e) {
                error=true;
            }
            if( idParte == 0 ) {
                parte=new Parte2(0, "");
            } else {
                DAOPartes dao=new DAOPartes();
                if(error) {
                    idParte=dao.agregar(value.toUpperCase().trim());
                }
                parte=dao.obtenerParte2(idParte);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Parte_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return parte;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            if (value == null || value.equals("")) {  
                return "0";
            } else {
                Parte2 parte=(Parte2) value;
                val = Integer.toString(parte.getIdParte());
            } 
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Parte_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
}
