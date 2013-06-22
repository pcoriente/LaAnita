/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.Converter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import requisicion.dao.DaoRequisicion;
import requisicion.dominio.DominioPartes;
/**
 *
 * @author Comodoro
 */
public class PickListConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DominioPartes dlm= new DominioPartes();
        System.out.println("Si entro hacer su chamba en el converter.");
//        PickList p = (PickList) component;
//        DualListModel dlm = (DualListModel) p.getValue();
//        dlm.getTarget().get(Integer.parseInt(value));
        return dlm;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String x = null;
        PickList p = (PickList) component;
        DualListModel dl = (DualListModel) p.getValue();
        x = Integer.toString(dl.getSource().indexOf(value));
        String c = "Cambio de pablo";
        return x;
    }
}
