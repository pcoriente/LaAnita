package movimientos;

import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.dominio.Comprobante;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
import impuestos.dao.DAOImpuestosProducto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import org.primefaces.event.SelectEvent;
import producto2.MbProductosBuscar;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbEnvios")
@SessionScoped
public class MbEnvios implements Serializable {
    private boolean modoEdicion;
    private TOMovimiento envio;
    private ArrayList<MovimientoProducto> envioDetalle;
    private MovimientoProducto envioProducto;
    private MovimientoProducto resEnvioProducto;
    private DAOMovimientos dao;
    private DAOImpuestosProducto daoImps;
    
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value="#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value="#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;
    
    public MbEnvios() throws NamingException {
        this.modoEdicion = false;
        this.resEnvioProducto=new MovimientoProducto();
        
        this.mbAcciones = new MbAcciones();
        this.mbComprobantes=new MbComprobantes();
        this.mbBuscar=new MbProductosBuscar();
        this.inicializa();
    }
    
    public boolean compara(MovimientoProducto prod) {
        if(prod.getProducto().getIdProducto()==this.envioProducto.getProducto().getIdProducto()) {
            return false;
        } else {
            return true;
        }
    }
    
//    private Empaque convertir(TOEmpaque to, Producto p) throws SQLException {
//        Empaque e=new Empaque();
//        e.setIdEmpaque(to.getIdEmpaque());
//        e.setCod_pro(to.getCod_pro());
//        e.setProducto(p);
//        e.setPiezas(to.getPiezas());
//        e.setUnidadEmpaque(to.getUnidadEmpaque());
//        e.setSubEmpaque(to.getSubEmpaque());
//        e.setDun14(to.getDun14());
//        e.setPeso(to.getPeso());
//        e.setVolumen(to.getVolumen());
//        return e;
//    }
    
    public void cargaDetalleSolicitud(SelectEvent event) {
        this.mbComprobantes.setComprobante((Comprobante) event.getObject());
        
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMovimientos();
//            DAOProductos daoProds=new DAOProductos();
            this.envio=this.dao.obtenerSolicitudTraspaso(this.mbComprobantes.getComprobante().getIdComprobante());
            this.envio.setIdAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen());
            this.envio.setIdTipo(3);
//            this.envioDetalle=this.dao.obtenerDetalleMovimiento(this.envio.getIdMovto());
//            this.daoEmpaques=new DAOEmpaques();
            this.daoImps=new DAOImpuestosProducto();
            this.envioDetalle=new ArrayList<MovimientoProducto>();
            for(TOMovimientoDetalle p: this.dao.obtenerDetalleMovimiento(this.envio.getIdMovto())) {
                this.envioDetalle.add(convertir(p));
            }
            this.envioProducto=new MovimientoProducto();
            this.modoEdicion=true;
            ok=true;
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    private MovimientoProducto convertir(TOMovimientoDetalle to) throws SQLException {
        MovimientoProducto p=new MovimientoProducto();
        p.setProducto(this.mbBuscar.obtenerProducto(to.getIdProducto()));
        p.setCantFacturada(to.getCantFacturada());
        p.setCantOrdenada(to.getCantOrdenada());
        p.setCantRecibida(to.getCantRecibida());
        p.setCantSinCargo(to.getCantSinCargo());
        p.setCostoOrdenado(to.getCostoOrdenado());
        p.setDesctoConfidencial(to.getDesctoConfidencial());
        p.setDesctoProducto1(to.getDesctoProducto1());
        p.setDesctoProducto2(to.getDesctoProducto2());
        p.setImporte(to.getImporte());
        p.setImpuestos(this.daoImps.obtenerImpuestosProducto(this.envio.getIdMovto(), to.getIdProducto()));
        p.setNeto(to.getNeto());
        p.setUnitario(to.getUnitario());
        return p;
    }
    
    public void grabarEnvio() {
        
    }
    
    public void salir() {
        this.inicializa();
        this.modoEdicion=false;
    }
    
    public void respaldaFila() {
        this.resEnvioProducto.setCantOrdenada(this.envioProducto.getCantOrdenada());
        this.resEnvioProducto.setCantFacturada(this.envioProducto.getCantFacturada());
        this.resEnvioProducto.setCantRecibida(this.envioProducto.getCantRecibida());
        this.resEnvioProducto.setDesctoConfidencial(this.envioProducto.getDesctoConfidencial());
        this.resEnvioProducto.setDesctoProducto1(this.envioProducto.getDesctoProducto1());
        this.resEnvioProducto.setDesctoProducto2(this.envioProducto.getDesctoProducto2());
        this.resEnvioProducto.setProducto(this.envioProducto.getProducto());
        this.resEnvioProducto.setImporte(this.envioProducto.getImporte());
        this.resEnvioProducto.setNeto(this.envioProducto.getNeto());
        this.resEnvioProducto.setUnitario(this.envioProducto.getUnitario());
        this.resEnvioProducto.setPrecio(this.envioProducto.getPrecio());
    }
    
    public String terminar() {
        this.modoEdicion = false;
        this.acciones = null;
        this.inicializa();
        return "index.xhtml";
    }
    
    public void envio() {
        this.envioDetalle=new ArrayList<MovimientoProducto>();
        this.modoEdicion=true;
    }
    
    private void inicializa() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        this.mbBuscar.inicializar();
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<MovimientoProducto> getEnvioDetalle() {
        return envioDetalle;
    }

    public void setEnvioDetalle(ArrayList<MovimientoProducto> envioDetalle) {
        this.envioDetalle = envioDetalle;
    }

    public MovimientoProducto getEnvioProducto() {
        return envioProducto;
    }

    public void setEnvioProducto(MovimientoProducto envioProducto) {
        this.envioProducto = envioProducto;
    }

    public MovimientoProducto getResEnvioProducto() {
        return resEnvioProducto;
    }

    public void setResEnvioProducto(MovimientoProducto resEnvioProducto) {
        this.resEnvioProducto = resEnvioProducto;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(18);
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

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }
}
