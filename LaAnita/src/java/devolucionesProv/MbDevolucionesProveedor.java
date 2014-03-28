package devolucionesProv;

import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.dominio.MovimientoProducto;
import impuestos.dao.DAOImpuestosProducto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.bean.ManagedProperty;
import javax.naming.NamingException;
import monedas.MbMonedas;
import productos.dao.DAOEmpaques;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbDevs")
@SessionScoped
public class MbDevolucionesProveedor implements Serializable {
//    private int idModulo;
    private boolean modoEdicion;
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value="#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value="#{mbMonedas}")
    private MbMonedas mbMonedas;
    
    private Devolucion movto;
    private ArrayList<MovimientoProducto> movtoDetalle;
    private MovimientoProducto producto;
    private MovimientoProducto resProducto;
    private Date fechaIniPeriodo=new Date();
    private Date fechaFinPeriodo=new Date();
    private DAOMovimientos dao;
    private DAOEmpaques daoEmpaques;
    private DAOImpuestosProducto daoImps;
    private double tipoCambio;
    
    public MbDevolucionesProveedor() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbComprobantes=new MbComprobantes();
        this.mbMonedas=new MbMonedas();
        
//        this.inicializa();
    }
    
    public void devoluciones() {}
    
    public String terminar() {
        this.acciones=null;
        return "index.xhtml";
    }
    
    public void cargaListaComprobantes() {
        if(this.mbComprobantes.validaComprobante()) {
            this.mbComprobantes.setTipoComprobante(1);
            this.mbComprobantes.cargaListaComprobantes();
        }
    }
    
    private void inicializa() {
        this.modoEdicion = false;
        this.movto=new Devolucion();
        
        this.mbComprobantes.getMbAlmacenes().inicializaAlmacen();
        this.mbComprobantes.getMbProveedores().cargaListaProveedores();
        this.mbComprobantes.getMbProveedores().inicializaProveedor();
        this.mbComprobantes.setTipoComprobante(1);
        this.mbComprobantes.cargaListaComprobantes();
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
//            this.idModulo=idModulo;
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
            this.inicializa();
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(19);
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }

    public MbComprobantes getMbComprobantes() {
        return mbComprobantes;
    }

    public void setMbComprobantes(MbComprobantes mbComprobantes) {
        this.mbComprobantes = mbComprobantes;
    }

    public MbMonedas getMbMonedas() {
        return mbMonedas;
    }

    public void setMbMonedas(MbMonedas mbMonedas) {
        this.mbMonedas = mbMonedas;
    }

    public Devolucion getMovto() {
        return movto;
    }

    public void setMovto(Devolucion movto) {
        this.movto = movto;
    }

    public ArrayList<MovimientoProducto> getMovtoDetalle() {
        return movtoDetalle;
    }

    public void setMovtoDetalle(ArrayList<MovimientoProducto> movtoDetalle) {
        this.movtoDetalle = movtoDetalle;
    }

    public MovimientoProducto getProducto() {
        return producto;
    }

    public void setProducto(MovimientoProducto producto) {
        this.producto = producto;
    }

    public Date getFechaIniPeriodo() {
        return fechaIniPeriodo;
    }

    public void setFechaIniPeriodo(Date fechaIniPeriodo) {
        this.fechaIniPeriodo = fechaIniPeriodo;
    }

    public Date getFechaFinPeriodo() {
        return fechaFinPeriodo;
    }

    public void setFechaFinPeriodo(Date fechaFinPeriodo) {
        this.fechaFinPeriodo = fechaFinPeriodo;
    }

    public double getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
}
