package movimientos;

import almacenes.to.TOAlmacenJS;
import cedis.dominio.MiniCedis;
import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.dominio.Comprobante;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.event.SelectEvent;
import productos.MbBuscarEmpaques;
import productos.dao.DAOEmpaques;
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
//    private MiniCedis cedis;
//    private TOAlmacenJS toAlmacen;
//    private ArrayList<SelectItem> listaAlmacenes;
    private ArrayList<MovimientoProducto> envioDetalle;
    private MovimientoProducto envioProducto;
    private MovimientoProducto resEnvioProducto;
    private DAOMovimientos dao;
    
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
//    @ManagedProperty(value="#{mbBuscarEmpaques}")
//    private MbBuscarEmpaques mbBuscar;
    @ManagedProperty(value="#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    private DAOEmpaques daoEmpaques;
    
    public MbEnvios() throws NamingException {
        this.modoEdicion = false;
        this.resEnvioProducto=new MovimientoProducto();
        
        this.mbAcciones = new MbAcciones();
//        this.mbBuscar=new MbBuscarEmpaques();
        this.mbComprobantes=new MbComprobantes();
        this.inicializa();
    }
    
    public boolean compara(MovimientoProducto prod) {
        // prod.empaque.idEmpaque!=mbEnvios.envioProducto.empaque.idEmpaque
        if(prod.getEmpaque().getIdEmpaque()==this.envioProducto.getEmpaque().getIdEmpaque()) {
            return false;
        } else {
            return true;
        }
    }
    
    public void cargaDetalleSolicitud(SelectEvent event) {
        this.mbComprobantes.setComprobante((Comprobante) event.getObject());
        
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMovimientos();
            this.envio=this.dao.obtenerSolicitudTraspaso(this.mbComprobantes.getComprobante().getIdComprobante());
            this.envio.setIdAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen());
            this.envio.setIdTipo(3);
            this.envioDetalle=this.dao.obtenerDetalleMovimiento(this.envio.getIdMovto());
            
            this.daoEmpaques=new DAOEmpaques();
            for(MovimientoProducto p: this.envioDetalle) {
                p.setEmpaque(this.daoEmpaques.obtenerEmpaque(p.getEmpaque().getIdEmpaque()));
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
        this.resEnvioProducto.setEmpaque(this.envioProducto.getEmpaque());
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
    
//    public void cargaAlmacenesEmpresa() {
//        this.mbComprobantes.getMbAlmacenes().cargaAlmacenesEmpresa(this.toAlmacen.getIdEmpresa());
//    }
    
    private void inicializa() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        
//        this.cedis=this.mbComprobantes.getMbAlmacenes().getMbCedis().getCedis();
//        this.listaAlmacenes=this.mbComprobantes.getMbAlmacenes().getListaAlmacenes();
//        this.toAlmacen=(TOAlmacenJS)this.listaAlmacenes.get(0).getValue();
//        
//        this.mbComprobantes.getMbAlmacenes().getMbCedis().cargaMiniCedisTodos();
//        this.mbComprobantes.getMbAlmacenes().getMbCedis().setCedis((MiniCedis)this.mbComprobantes.getMbAlmacenes().getMbCedis().getListaMiniCedis().get(0).getValue());
//        this.cargaAlmacenesEmpresa();
//        this.mbComprobantes.getMbAlmacenes().setToAlmacen((TOAlmacenJS)this.mbComprobantes.getMbAlmacenes().getListaAlmacenes().get(0).getValue());
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

//    public MiniCedis getCedis() {
//        return cedis;
//    }
//
//    public void setCedis(MiniCedis cedis) {
//        this.cedis = cedis;
//    }
//
//    public TOAlmacenJS getToAlmacen() {
//        return toAlmacen;
//    }
//
//    public void setToAlmacen(TOAlmacenJS toAlmacen) {
//        this.toAlmacen = toAlmacen;
//    }
//
//    public ArrayList<SelectItem> getListaAlmacenes() {
//        return listaAlmacenes;
//    }
//
//    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
//        this.listaAlmacenes = listaAlmacenes;
//    }

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

//    public MbBuscarEmpaques getMbBuscar() {
//        return mbBuscar;
//    }
//
//    public void setMbBuscar(MbBuscarEmpaques mbBuscar) {
//        this.mbBuscar = mbBuscar;
//    }

    public MbComprobantes getMbComprobantes() {
        return mbComprobantes;
    }

    public void setMbComprobantes(MbComprobantes mbComprobantes) {
        this.mbComprobantes = mbComprobantes;
    }
}
