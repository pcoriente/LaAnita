/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeas;

import daoPermisos.DaoPer;
import dominios.Accion;
import dominios.BaseDato;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Comodoro
 */
@ManagedBean
@SessionScoped
public class MbAcciones implements Serializable {

    Accion acion = new Accion();
    DualListModel<Accion> pickAcciones = new DualListModel<Accion>();
    ArrayList<Accion> accionesOrigen = new ArrayList<Accion>();
    ArrayList<Accion> accionesDestino = new ArrayList<Accion>();

    /**
     * Creates a new instance of MbAcciones
     */
    public MbAcciones() {
        pickAcciones = new DualListModel<Accion>(accionesOrigen, accionesDestino);
    }

    public Accion getAcion() {
        return acion;
    }

    public void setAcion(Accion acion) {
        this.acion = acion;
    }

    public DualListModel<Accion> getPickAcciones() throws SQLException {

        return pickAcciones;
    }

    public void setPickAcciones(DualListModel<Accion> pickAcciones) {
        this.pickAcciones = pickAcciones;
    }

    public ArrayList<Accion> getAccionesOrigen() {
        return accionesOrigen;
    }

    public void setAccionesOrigen(ArrayList<Accion> accionesOrigen) {
        this.accionesOrigen = accionesOrigen;
    }

    public ArrayList<Accion> getAccionesDestino() {
        pickAcciones = new DualListModel<Accion>(accionesOrigen, accionesDestino);
        return accionesDestino;
    }

    public void setAccionesDestino(ArrayList<Accion> accionesDestino) {
        this.accionesDestino = accionesDestino;
    }
}
