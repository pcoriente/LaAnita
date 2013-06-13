package pedidos.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import pedidos.dao.DAOPedidos;
import pedidos.dominio.Pedido;

/**
 *
 * @author Julio
 */
public class PedidoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        Pedido pedido = null;
        try {
            String cod_bod=value.substring(0, 2);
            String cod_env=value.substring(2);
            if(cod_env.equals("000000")) {
                pedido=new Pedido();
                pedido.setCod_bod(cod_bod);
                pedido.setCod_env(cod_env);
            } else {
                DAOPedidos dao=new DAOPedidos();
                pedido=dao.obtenerPedido(cod_bod, cod_env);
            }
        }catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Pais_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return pedido;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object value) {
        String val = null;
        try {
            Pedido pedido = (Pedido) value;
            val = pedido.getCod_bod()+pedido.getCod_env();
        }catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Pedido_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
    
}
