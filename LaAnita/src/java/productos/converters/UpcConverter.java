package productos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productos.dao.DAOUpcs;
import productos.dominio.Upc;

/**
 *
 * @author JULIOS
 */
public class UpcConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Upc upc = null;
        try {
            int pos=value.indexOf("|");
            int idUpc=Integer.parseInt(value.substring(0, pos));
            int idProducto=Integer.parseInt(value.substring(pos+1));
            if(idUpc<=0) {
                upc=new Upc(idProducto);
            } else {
                DAOUpcs dao=new DAOUpcs();
                upc=dao.obtenerUpc(idUpc);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Grupo_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return upc;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val=null;
        try {
            Upc upc = (Upc) value;
            val=Integer.toString(upc.getIdUpc())+"|"+Integer.toString(upc.getIdProducto());
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Grupo_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
    
}
