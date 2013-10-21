package productos;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author jsolis
 */
@Named(value = "mbBuscarEmpaqes")
@SessionScoped
public class MbBuscarEmpaqes implements Serializable {
    
    public MbBuscarEmpaqes() {
    }
    
    public void obtenerEmpaquesParte(int idParte) {
        
    }
}
