package productosOld.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import productosOld.dao.DAOBuscar;
import productosOld.dominio.ProductoOld;

/**
 *
 * @author JULIOS
 */
public class ProductoOldConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ProductoOld producto = null;
        try {
            String cod_emp=value.substring(0, 2);
            String cod_pro=value.substring(7);
            DAOBuscar dao=new DAOBuscar();
            producto=dao.obtenerProducto(cod_emp, cod_pro);
       }catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Pais_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return producto;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            ProductoOld producto = (ProductoOld) value;
            val = producto.getCod_emp()+producto.getCod_pro();
        }catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Pedido_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
}
