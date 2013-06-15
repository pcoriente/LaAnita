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
import javax.faces.model.SelectItem;
import org.primefaces.model.DualListModel;
import requisicion.dominio.DominioEmpresas;

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
    private List<SelectItem> listaDbs;

    public List<SelectItem> getListaDbs() {

        listaDbs = obtenerEmpresas();
        return listaDbs;
    }

    public void setListaDbs(List<SelectItem> listaDbs) {
        this.listaDbs = listaDbs;
    }

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

    private List<SelectItem> obtenerEmpresas() {
        List<SelectItem> empresas = new ArrayList<SelectItem>();
        DominioEmpresas dEmpre = new DominioEmpresas();
        DominioEmpresas d = new DominioEmpresas();
        dEmpre.setCodigoEmpresa(0);
        dEmpre.setNombreComercial("Seleccione Una Empresa");
        d.setCodigoEmpresa(1);
        d.setNombreComercial("pcOriente");
        SelectItem emp = new SelectItem(dEmpre, dEmpre.getNombreComercial());
        SelectItem emp1 = new SelectItem(dEmpre, dEmpre.getNombreComercial());
        empresas.add(emp);
        empresas.add(emp1);
        return empresas;
    }
}
