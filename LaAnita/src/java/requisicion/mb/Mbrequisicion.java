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
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import productosOld.MbBuscarOld;
import requisicion.dao.DaoRequisicion;
import requisicion.dominio.DominioEmpresas;
import requisicion.dominio.DominioPartes;

/**
 *
 * @author Comodoro
 */
@Named(value = "mbrequisicion")
@SessionScoped
public class Mbrequisicion implements Serializable {

    private DominioEmpresas empre = new DominioEmpresas();
    private DualListModel<DominioPartes> cities;
    private List<SelectItem> listaDbs;
    List<DominioPartes> partes = new ArrayList<DominioPartes>();
    List<DominioPartes> citiesTarget = new ArrayList<DominioPartes>();
    ArrayList<DominioPartes> peticionPartes = new ArrayList<DominioPartes>();
    ArrayList<DominioPartes> peticionPartesRespaldo = new ArrayList<DominioPartes>();

    public ArrayList<DominioPartes> getPeticionPartesRespaldo() {
        return peticionPartesRespaldo;
    }

    public void setPeticionPartesRespaldo(ArrayList<DominioPartes> peticionPartesRespaldo) {
        this.peticionPartesRespaldo = peticionPartesRespaldo;
    }
    
    

    public Mbrequisicion() throws SQLException {

        DaoRequisicion d = new DaoRequisicion();
        partes = d.damePartes();
        cities = new DualListModel<DominioPartes>(partes, citiesTarget);
    }

    public ArrayList<DominioPartes> getPeticionPartes() {
        return peticionPartes;
    }

    public void setPeticionPartes(ArrayList<DominioPartes> peticionPartes) {
        this.peticionPartes = peticionPartes;
    }

    public List<DominioPartes> getPartes() {
        return partes;
    }

    public void setPartes(List<DominioPartes> partes) {
        this.partes = partes;
    }

    public List<DominioPartes> getCitiesTarget() {
        return citiesTarget;
    }

    public void setCitiesTarget(List<DominioPartes> citiesTarget) {
        this.citiesTarget = citiesTarget;
    }

    public DominioEmpresas getEmpresas() {
        return empre;
    }

    public void setEmpresas(DominioEmpresas empresas) {
        this.empre = empresas;
    }

    public List<SelectItem> getListaDbs() throws SQLException {
        listaDbs = obtenerEmpresas();
        return listaDbs;
    }

    public void setListaDbs(List<SelectItem> listaDbs) {
        this.listaDbs = listaDbs;
    }

    public DualListModel<DominioPartes> getCities() {
        return cities;
    }

    public void setCities(DualListModel<DominioPartes> cities) {
        this.cities = cities;
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
        try{
            peticionPartesRespaldo = (ArrayList<DominioPartes>) cities.getTarget();
            System.err.println(peticionPartesRespaldo.size());
        }
        catch(Exception e){
            
        }
        
    }

    public void longitud(TransferEvent event) {
        ArrayList<DominioPartes> p = (ArrayList<DominioPartes>) cities.getTarget();
        p = (ArrayList<DominioPartes>) cities.getSource();
    }
}
