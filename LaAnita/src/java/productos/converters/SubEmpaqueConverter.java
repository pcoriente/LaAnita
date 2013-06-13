package productos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productos.dao.DAOEmpaques;
import productos.dominio.SubEmpaque;

/**
 *
 * @author JULIOS
 */
public class SubEmpaqueConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        SubEmpaque subEmpaque=null;
        try {
            //int i=value.indexOf(";");
            //int idEmpresa=Integer.parseInt(value.substring(0, i));
            //int idSubEmpaque=Integer.parseInt(value.substring(i+1));
            int idSubEmpaque=Integer.parseInt(value);
            if(idSubEmpaque==0) {
                subEmpaque=new SubEmpaque(0);
            } else {
                DAOEmpaques dao=new DAOEmpaques();
                subEmpaque=dao.obtenerSubEmpaque(idSubEmpaque);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_SubEmpaque_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return subEmpaque;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            SubEmpaque subEmpaque = (SubEmpaque) value;
            val = Integer.toString(subEmpaque.getIdEmpaque());
        }catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_SubEmpaque_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
    
}
