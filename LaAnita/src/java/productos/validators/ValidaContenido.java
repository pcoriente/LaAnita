package productos.validators;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.context.RequestContext;
import utilerias.Utilerias;

/**
 *
 * @author JULIOS
 */
@FacesValidator("ValidaContenido")
public class ValidaContenido implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        boolean error=true;
        String str=(String)value;
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, "CONTENIDO :", "");
        if(str.isEmpty()) {
            facesMessage.setDetail("Valor requerido !!!");
        } else if(Pattern.matches("^\\d+(\\.\\d+)?$", str)) {
            Double d=Double.parseDouble(str);
            if(d<=0 || d>999.999) {
                facesMessage.setDetail("Se requiere un numero mayor que cero y hasta 999.999 !!!");
            } else {
                error=false;
            }
        } else {
            facesMessage.setDetail("Se requiere un número mayor que cero y hasta 999.999 !!!");
        }
        if(error) {
            throw new ValidatorException(facesMessage);
        }
    }
}
