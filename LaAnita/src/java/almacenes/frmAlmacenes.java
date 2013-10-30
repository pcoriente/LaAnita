package almacenes;

import almacenes.dominio.Almacen;
import cedis.MbMiniCedis;
import empresas.MbMiniEmpresas;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jsolis
 */
@Named(value = "frmAlmacenes")
@SessionScoped
public class frmAlmacenes implements Serializable {
    private boolean modoEdicion;
    private Almacen almacen;
    
    private ArrayList<SelectItem> listaCedis;
    @ManagedProperty(value = "#{mbMiniCedis}")
    private MbMiniCedis mbCedis;
    
    private ArrayList<SelectItem> listaEmpresas;
    @ManagedProperty(value = "#{mbMiniEmpresas}")
    private MbMiniEmpresas mbEmpresas;
    
    @ManagedProperty(value = "#{mbAlmacenes}")
    private MbAlmacenes mbAlmacenes;
    
    private ArrayList<Accion> acciones;
    @ManagedProperty(value="#{mbAcciones}")
    private MbAcciones mbAcciones;
    
    public frmAlmacenes() throws NamingException {
        this.modoEdicion=false;
        this.mbCedis=new MbMiniCedis();
        this.mbEmpresas=new MbMiniEmpresas();
        this.mbAlmacenes=new MbAlmacenes();
        this.mbAcciones=new MbAcciones();
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public ArrayList<SelectItem> getListaCedis() {
        return listaCedis;
    }

    public void setListaCedis(ArrayList<SelectItem> listaCedis) {
        this.listaCedis = listaCedis;
    }

    public MbMiniCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbMiniCedis mbCedis) {
        this.mbCedis = mbCedis;
    }

    public ArrayList<SelectItem> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(ArrayList<SelectItem> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public MbMiniEmpresas getMbEmpresas() {
        return mbEmpresas;
    }

    public void setMbEmpresas(MbMiniEmpresas mbEmpresas) {
        this.mbEmpresas = mbEmpresas;
    }

    public MbAlmacenes getMbAlmacenes() {
        return mbAlmacenes;
    }

    public void setMbAlmacenes(MbAlmacenes mbAlmacenes) {
        this.mbAlmacenes = mbAlmacenes;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }

    public ArrayList<Accion> getAcciones() {
        if(this.acciones==null) {
            this.acciones=this.mbAcciones.obtenerAcciones(14);
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }
}
