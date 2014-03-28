package productos;

import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;
import productos.dominio.Empaque;

/**
 *
 * @author jesc
 */
public class EmpaqueDataModel extends ListDataModel<Empaque> implements SelectableDataModel<Empaque> {
    
    public EmpaqueDataModel() {}
    
    public EmpaqueDataModel(List<Empaque> data) {
        super(data);
    }

    @Override
    public Object getRowKey(Empaque e) {
        //int idEmpaque=e.getIdEmpaque();
//        String val=Integer.toString(e.getIdEmpaque());
        return e.getCod_pro();
    }

    @Override
    public Empaque getRowData(String cod_pro) {
        List<Empaque> empaques=(List<Empaque>)getWrappedData();
        for(Empaque e:empaques) {
            if(e.getCod_pro().equals(cod_pro)) {
                return e;
            }
        }
        return null;
    }
}
