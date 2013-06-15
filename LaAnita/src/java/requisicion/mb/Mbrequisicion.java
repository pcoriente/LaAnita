/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.mb;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Comodoro
 */
@Named(value = "mbrequisicion")
@SessionScoped
public class Mbrequisicion implements Serializable {

    /**
     * Creates a new instance of Mbrequisicion
     */
    private DualListModel<String> cities;

    public DualListModel<String> getCities() {
        return cities;
    }

    public void setCities(DualListModel<String> cities) {
        this.cities = cities;
    }
    

    public Mbrequisicion() {
        List<String> citiesSource = new ArrayList<String>();
        List<String> citiesTarget = new ArrayList<String>();
        citiesSource.add("Istanbul");
        citiesSource.add("Ankara");
        citiesSource.add("Izmir");
        citiesSource.add("Antalya");
        citiesSource.add("Bursa");
        cities = new DualListModel<String>(citiesSource, citiesTarget);

    }
}
