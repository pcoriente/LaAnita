/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.mb;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import org.primefaces.model.DualListModel;
import productosOld.MbBuscarOld;
import requisicion.dao.DaoRequisicion;
import requisicion.dominio.DominioEmpresas;

/**
 *
 * @author Comodoro
 */
@Named(value = "mbrequisicion")
@SessionScoped
public class Mbrequisicion implements Serializable {

    private DominioEmpresas empresas = new DominioEmpresas();

    public DominioEmpresas getEmpresas() {
        return empresas;
    }

    public void setEmpresas(DominioEmpresas empresas) {
        this.empresas = empresas;
    }
    private DualListModel<String> cities;
    private List<SelectItem> listaDbs;

    /**
     * Creates a new instance of Mbrequisicion
     */
    public List<SelectItem> getListaDbs() throws SQLException {
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

    private List<SelectItem> obtenerEmpresas() throws SQLException {
        List<SelectItem> empresas = new ArrayList<SelectItem>();
        DominioEmpresas dEmpre = new DominioEmpresas();
        dEmpre.setCodigoEmpresa(0);
        dEmpre.setNombreComercial("Seleccione Una Empresa");
        SelectItem emp = new SelectItem(dEmpre, dEmpre.getNombreComercial());
        empresas.add(emp);
        ArrayList<DominioEmpresas> d = new ArrayList<DominioEmpresas>();
        DaoRequisicion daoR = new DaoRequisicion();
        d = daoR.dameEmpresas();
        for (DominioEmpresas e : d) {
            empresas.add(new SelectItem(e, e.getNombreComercial()));
        }
        return empresas;
    }

    public void damevalores() {
        System.out.println(empresas.getEmpresa());
    }
}
