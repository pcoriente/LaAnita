package movimientos;

import almacenes.to.TOAlmacenJS;
import cedis.dominio.MiniCedis;
import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
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
import productos.MbBuscarEmpaques;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbSolicitudes")
@SessionScoped
public class MbSolicitudes implements Serializable {
    private boolean modoEdicion;
    private MiniCedis cedis;
    private TOAlmacenJS toAlmacen;
    private ArrayList<SelectItem> listaAlmacenes;
    private ArrayList<MovimientoProducto> solicitudDetalle;
    private MovimientoProducto solicitudProducto;
    private MovimientoProducto resSolicitudProducto;
    private DAOMovimientos dao;
    
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value="#{mbBuscarEmpaques}")
    private MbBuscarEmpaques mbBuscar;
    @ManagedProperty(value="#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    
    public MbSolicitudes() throws NamingException {
        this.modoEdicion = false;
        
        this.mbAcciones = new MbAcciones();
        this.mbBuscar=new MbBuscarEmpaques();
        this.mbComprobantes=new MbComprobantes();
        this.inicializa();
    }
    
    public void grabarSolicitud() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMovimientos();
            TOMovimiento solicitud=new TOMovimiento();
            solicitud.setIdAlmacen(this.toAlmacen.getIdAlmacen());
            solicitud.setIdMoneda(1);
            solicitud.setTipoCambio(1);
            solicitud.setIdTipo(2); // Entrada por traspaso
            solicitud.setIdImpuestoZona(1);
            if(this.dao.grabarSolicitudTraspaso(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen(), solicitud, this.solicitudDetalle)) {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La solicitud se grabo correctamente !!!");
                this.modoEdicion=false;
                ok=true;
            }
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
    
    public void salir() {
        this.inicializa();
        this.modoEdicion=false;
    }
    
    public void actualizaProductoSeleccionado() {
        boolean nuevo=true;
        MovimientoProducto producto=new MovimientoProducto();
        producto.setEmpaque(this.mbBuscar.getProducto());
        for(MovimientoProducto p:this.solicitudDetalle) {
            if(p.equals(producto)) {
                this.solicitudProducto=p;
                nuevo=false;
                break;
            }
        }
        if(nuevo) {
            this.solicitudDetalle.add(producto);
            this.solicitudProducto=producto;
        }
        this.respaldaFila();
    }
    
    public void buscar() {
        this.mbBuscar.buscarLista();
        if(this.mbBuscar.getProducto()!=null) {
            this.actualizaProductoSeleccionado();
        }
    }
    
    public void respaldaFila() {
        this.resSolicitudProducto.setCantOrdenada(this.solicitudProducto.getCantOrdenada());
        this.resSolicitudProducto.setCantFacturada(this.solicitudProducto.getCantFacturada());
        this.resSolicitudProducto.setCantRecibida(this.solicitudProducto.getCantRecibida());
        this.resSolicitudProducto.setDesctoConfidencial(this.solicitudProducto.getDesctoConfidencial());
        this.resSolicitudProducto.setDesctoProducto1(this.solicitudProducto.getDesctoProducto1());
        this.resSolicitudProducto.setDesctoProducto2(this.solicitudProducto.getDesctoProducto2());
        this.resSolicitudProducto.setEmpaque(this.solicitudProducto.getEmpaque());
        this.resSolicitudProducto.setImporte(this.solicitudProducto.getImporte());
        this.resSolicitudProducto.setNeto(this.solicitudProducto.getNeto());
        this.resSolicitudProducto.setUnitario(this.solicitudProducto.getUnitario());
        this.resSolicitudProducto.setPrecio(this.solicitudProducto.getPrecio());
    }
    
    public void solicitud() {
        this.solicitudDetalle=new ArrayList<MovimientoProducto>();
        this.modoEdicion=true;
    }
    
    private void inicializa() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        
        this.cedis=this.mbComprobantes.getMbAlmacenes().getMbCedis().getCedis();
        this.listaAlmacenes=this.mbComprobantes.getMbAlmacenes().getListaAlmacenes();
        this.toAlmacen=(TOAlmacenJS)this.listaAlmacenes.get(0).getValue();
        
        this.mbComprobantes.getMbAlmacenes().getMbCedis().cargaMiniCedisTodos();
        this.mbComprobantes.getMbAlmacenes().getMbCedis().setCedis((MiniCedis)this.mbComprobantes.getMbAlmacenes().getMbCedis().getListaMiniCedis().get(0).getValue());
        this.cargaAlmacenesEmpresa();
        this.mbComprobantes.getMbAlmacenes().setToAlmacen((TOAlmacenJS)this.mbComprobantes.getMbAlmacenes().getListaAlmacenes().get(0).getValue());
    }
    
    public String terminar() {
        this.modoEdicion = false;
        this.acciones = null;
        this.inicializa();
        return "index.xhtml";
    }
    
    public void cargaAlmacenesEmpresa() {
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenesEmpresa(this.toAlmacen.getIdEmpresa());
    }

    public MovimientoProducto getResSolicitudProducto() {
        return resSolicitudProducto;
    }

    public void setResSolicitudProducto(MovimientoProducto resSolicitudProducto) {
        this.resSolicitudProducto = resSolicitudProducto;
    }

    public MovimientoProducto getSolicitudProducto() {
        return solicitudProducto;
    }

    public void setSolicitudProducto(MovimientoProducto solicitudProducto) {
        this.solicitudProducto = solicitudProducto;
    }

    public ArrayList<MovimientoProducto> getSolicitudDetalle() {
        return solicitudDetalle;
    }

    public void setSolicitudDetalle(ArrayList<MovimientoProducto> solicitudDetalle) {
        this.solicitudDetalle = solicitudDetalle;
    }

    public MiniCedis getCedis() {
        return cedis;
    }

    public void setCedis(MiniCedis cedis) {
        this.cedis = cedis;
    }

    public TOAlmacenJS getToAlmacen() {
        return toAlmacen;
    }

    public void setToAlmacen(TOAlmacenJS toAlmacen) {
        this.toAlmacen = toAlmacen;
    }

    public ArrayList<SelectItem> getListaAlmacenes() {
        return listaAlmacenes;
    }

    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
        this.listaAlmacenes = listaAlmacenes;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(17);
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

    public MbBuscarEmpaques getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbBuscarEmpaques mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public MbComprobantes getMbComprobantes() {
        return mbComprobantes;
    }

    public void setMbComprobantes(MbComprobantes mbComprobantes) {
        this.mbComprobantes = mbComprobantes;
    }
}
