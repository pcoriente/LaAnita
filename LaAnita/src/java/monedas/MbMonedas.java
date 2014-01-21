package monedas;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author jesc
 */
@Named(value = "mbMonedas")
@Dependent
public class MbMonedas implements Serializable {
    
    private ArrayList<SelectItem> listaMonedas = new ArrayList<SelectItem>();
    private Moneda moneda = new Moneda();

    public MbMonedas() {
    }
    
    private ArrayList<SelectItem> obtenerListaMonedas() throws NamingException {
        Moneda m0 = new Moneda();
        try {
            m0.setIdMoneda(0);
            m0.setMoneda("Moneda: ");
            listaMonedas.add(new SelectItem(m0, m0.toString()));
            DAOMonedas dao=new DAOMonedas();
            ArrayList<Moneda> monedas = dao.obtenerMonedas();
            for (Moneda e : monedas) {
                listaMonedas.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMonedas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMonedas;
    }
    
    public ArrayList<SelectItem> getListaMonedas() throws NamingException {
        if(this.listaMonedas==null) {
            listaMonedas = this.obtenerListaMonedas();
        }
        return listaMonedas;
    }

    public void setListaMonedas(ArrayList<SelectItem> listaMonedas) {
        this.listaMonedas = listaMonedas;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
}
