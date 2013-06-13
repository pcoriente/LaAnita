package productos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productos.dao.DAOUnidadesEmpaque;
import productos.dominio.UnidadEmpaque;

/**
 *
 * @author JULIOS
 */
public class UnidadEmpaqueConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        UnidadEmpaque unidad=null;
        try {
            int idUnidad=Integer.parseInt(value);
            if(idUnidad==0) {
                unidad=new UnidadEmpaque(0, "", "");
            } else {
                DAOUnidadesEmpaque dao=new DAOUnidadesEmpaque();
                unidad=dao.obtenerUnidadEmpaque(idUnidad);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_UnidadEmpaque_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return unidad;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            UnidadEmpaque unidad=(UnidadEmpaque) value;
            val=Integer.toString(unidad.getIdUnidad());
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_UnidadEmpaque_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
    
}
