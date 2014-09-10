package ventas;

import ventas.dominio.VentaComprobante;
import clientes.dominio.Venta;
import clientes.dominio.VentaProducto;
import entradas.MbComprobantes;
import entradas.dao.DAOComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOComprobante;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
import formatos.MbFormatos;
import formatos.dominio.ClientesFormato;
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
import mbMenuClientesGrupos.MbClientesGrupos;
import movimientos.dao.DAOLotes;
import movimientos.dominio.Lote;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import producto2.MbProductosBuscar;
import salidas.TOSalidaOficinaProducto;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbVentas")
@SessionScoped
public class MbVentas implements Serializable {
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value = "#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;
    private boolean modoEdicion;
    
    @ManagedProperty(value = "#{mbClientesGrupos}")
    private MbClientesGrupos mbGrupos;
    @ManagedProperty(value = "#{mbClientes}")
    private MbClientes mbClientes;
    @ManagedProperty(value = "#{mbFormatos}")
    private MbFormatos mbFormatos;
//    @ManagedProperty(value = "#{mbTiendas}")
//    private MbTiendas mbTiendas;
    private DAOMovimientos dao;
    private DAOImpuestosProducto daoImps;
    
    private Venta venta;
    private ArrayList<Venta> ventas;
    private ArrayList<VentaProducto> ventaDetalle;
    private VentaProducto ventaProducto;
    private VentaProducto resVentaProducto;
    private DAOComprobantes daoComprobantes;
    
    private double sumaLotes;
    private Lote lote;
    private DAOLotes daoLotes;
    private double resSeparados;
    
    public MbVentas() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbGrupos=new MbClientesGrupos();
        this.mbComprobantes = new MbComprobantes();
        this.mbBuscar = new MbProductosBuscar();
//        this.mbClientes = new MbMiniCliente();
        this.mbFormatos = new MbFormatos();
//        this.mbTiendas=new MbTiendas();
        this.mbClientes=new MbClientes();
        this.inicializa();
    }
    
    public void actualizaProductoSeleccionado() {
        boolean nuevo=true;
        VentaProducto producto=new VentaProducto();
        producto.setProducto(this.mbBuscar.getProducto());
        for(VentaProducto p:this.ventaDetalle) {
            if(p.getProducto().equals(producto.getProducto())) {
                this.ventaProducto=p;
                nuevo=false;
                break;
            }
        }
        boolean ok=false;
        if(nuevo) {
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "actualizaProductoSeleccionado");
            try {
                this.dao=new DAOMovimientos();
                this.dao.agregarProductoSalidaOficina(this.venta.getIdMovto(), this.convertirTOProducto(producto));
                this.ventaDetalle.add(producto);
                this.ventaProducto=producto;
                this.respaldaFila();
                ok=true;
            } catch (SQLException ex) {
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setDetail(ex.getMessage());
            }
            if (!ok) {
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
            }
        } else {
            this.respaldaFila();
        }
    }
    
    private TOSalidaOficinaProducto convertirTOProducto(VentaProducto p) {
        TOSalidaOficinaProducto to=new TOSalidaOficinaProducto();
        to.setIdProducto(p.getProducto().getIdProducto());
        to.setCantidad(0);
        return to;
    }
    
    public void buscar() {
        this.mbBuscar.buscarLista();
        if(this.mbBuscar.getProducto()!=null) {
            this.actualizaProductoSeleccionado();
        }
    }
    
    public void cargaDetalleVenta(SelectEvent event) {
        this.venta = (Venta) event.getObject();
        
        int idGrupoCliente=this.venta.getComprobante().getCliente().getIdGrupoCte();
        this.mbGrupos.setClienteGrupoSeleccionado(this.mbGrupos.obtenerClienteGpo(idGrupoCliente));
        this.mbFormatos.setLstFormatos(null);
        int idFormato=this.venta.getComprobante().getCliente().getIdFormato();
        this.mbFormatos.setCmbClientesFormatos(this.mbFormatos.obtenerFormato(idFormato));
        this.mbFormatos.cargarListaFormatos(idGrupoCliente);
        this.mbClientes.setMiniCliente(venta.getComprobante().getCliente());
        this.mbClientes.cargarMiniClientes(idFormato);
        
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "cargaDetalleVenta");
        try {
            this.dao = new DAOMovimientos();
//            this.idMovtoAlmacen=this.dao.obtenerIdMovtoAlmacen(this.envio.getAlmacen().getIdAlmacen(), 35, this.envio.getComprobante().getNumero());
            this.daoLotes = new DAOLotes();
            this.daoImps = new DAOImpuestosProducto();
            this.ventaDetalle = new ArrayList<VentaProducto>();
            for (TOMovimientoDetalle p : this.dao.obtenerDetalleMovimiento(this.venta.getIdMovto())) {
                this.ventaDetalle.add(this.convertirDetalle(p));
            }
            this.ventaProducto = new VentaProducto();
            this.modoEdicion = true;
            ok = true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    private VentaProducto convertirDetalle(TOMovimientoDetalle to) throws SQLException {
        VentaProducto p = new VentaProducto();
        p.setProducto(this.mbBuscar.obtenerProducto(to.getIdProducto()));
        p.setCantOrdenada(to.getCantOrdenada());
        p.setCantFacturada(to.getCantFacturada());
        p.setCantSinCargo(to.getCantSinCargo());
        p.setCantRecibida(to.getCantRecibida());
        p.setCostoOrdenado(to.getCostoOrdenado());
        p.setDesctoConfidencial(to.getDesctoConfidencial());
        p.setDesctoProducto1(to.getDesctoProducto1());
        p.setDesctoProducto2(to.getDesctoProducto2());
        p.setUnitario(to.getUnitario());
        p.setImpuestos(this.daoImps.obtenerImpuestosProducto(this.venta.getIdMovto(), to.getIdProducto()));
        p.setNeto(to.getNeto());
        p.setImporte(to.getImporte());
        p.setLotes(this.daoLotes.obtenerLotes(this.venta.getAlmacen().getIdAlmacen(), this.venta.getIdMovtoAlmacen(), to.getIdProducto()));
        this.sumaLotes = 0;
        for (Lote l : p.getLotes()) {
            this.sumaLotes += l.getSeparados();
        }
        if (p.getCantFacturada()+p.getCantSinCargo() != this.sumaLotes) {
            throw new SQLException("Error de sincronizacion Lotes en producto: " + p.getProducto().getIdProducto());
        }
        return p;
    }
    
    public void obtenerVentas() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "obtenerVentas");
        this.ventas = new ArrayList<Venta>();
        try {
            if(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen()==0) {
                fMsg.setDetail("Se requiere un almacen");
            } else {
                this.dao = new DAOMovimientos();
                this.daoComprobantes=new DAOComprobantes();
                for (TOMovimiento m : this.dao.obtenerMovimientos(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen(), 28, 0)) {
                    this.ventas.add(this.convertir(m));
                }
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public void actualizarCantidad() {
        // Hacer los calculos para saber cuantos se van con cargo y cuentos sin cargo
        this.ventaProducto.setCantFacturada(this.sumaLotes);
        this.resVentaProducto.setCantFacturada(this.sumaLotes);
    }
    
    public void gestionarLotes() {
        boolean cierra = false;
        RequestContext context = RequestContext.getCurrentInstance();
        this.gestionLotes();
        if (this.ventaProducto.getCantFacturada() == this.sumaLotes) {
            cierra = true;
        }
        context.addCallbackParam("okLotes", cierra);
    }

    public void gestionLotes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "gestionLotes");
        try {
            this.daoLotes = new DAOLotes();
            double separar = this.lote.getSeparados() - this.resSeparados;
            if (separar > 0) {
                if (this.ventaProducto.getCantOrdenada()>0 && (this.sumaLotes + separar) > this.ventaProducto.getCantOrdenada()) {
                    fMsg.setDetail("Cantidad enviar mayor que cantidad solicitada");
                    this.lote.setSeparados(this.resSeparados);
                } else {
                    double separados = this.daoLotes.separa(this.venta.getIdMovto(), this.venta.getIdMovtoAlmacen(), this.lote, separar);
                    if (separados < separar) {
                        fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
                        fMsg.setDetail("No se pudieron obtener los lotes solicitados");
                    } else {
                        ok = true;
                    }
                    this.lote.setSeparados(this.resSeparados + separados);
                    this.sumaLotes += separados;
                    this.resSeparados = this.lote.getSeparados();
                }
            } else {
                this.daoLotes.libera(this.venta.getIdMovto(), this.venta.getIdMovtoAlmacen(), this.lote, -separar);
                this.lote.setSeparados(this.resSeparados + separar);    // separar es negativo por esos se suma
                this.sumaLotes += separar;
                this.resSeparados = this.lote.getSeparados();
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public boolean comparaLotes(Lote lote) {
        boolean disable = true;
        if (this.lote.getLote().equals(lote.getLote())) {
            disable = false;
        }
        return disable;
    }
    
    public void respaldaSeparados() {
        this.resSeparados = this.lote.getSeparados();
    }
    
    public void editarLotes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "editarLotes");
        RequestContext context = RequestContext.getCurrentInstance();
        if (this.ventaProducto.getCantFacturada() < 0) {
            fMsg.setDetail("La cantidad enviada no puede ser menor que cero");
        } else if (this.ventaProducto.getCantOrdenada()>0 && this.ventaProducto.getCantFacturada() > this.ventaProducto.getCantOrdenada()) {
            fMsg.setDetail("La cantidad enviada no puede ser mayor a la cantidad solicitada");
        } else if (this.ventaProducto.getCantFacturada() != this.ventaProducto.getSeparados()) {
            try {
                this.sumaLotes = 0;
                this.daoLotes = new DAOLotes();
//                this.envioProducto.setLotes(this.daoLotes.obtenerLotes(this.envio.getAlmacen().getIdAlmacen(), this.idMovtoAlmacen, this.envioProducto.getProducto().getIdProducto()));
                this.ventaProducto.setLotes(this.daoLotes.obtenerLotes(this.venta.getAlmacen().getIdAlmacen(), this.venta.getIdMovtoAlmacen(), this.ventaProducto.getProducto().getIdProducto()));
                for (Lote l : this.ventaProducto.getLotes()) {
                    this.sumaLotes += l.getSeparados();
                }
                this.lote = new Lote();
                ok = true;
            } catch (SQLException ex) {
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okLotes", ok);
    }
    
    public boolean comparaProductos(MovimientoProducto prod) {
        if (prod.getProducto().getIdProducto() == this.ventaProducto.getProducto().getIdProducto()) {
            return false;
        } else {
            return true;
        }
    }
    
    public void respaldaFila() {
        this.resVentaProducto.setProducto(this.ventaProducto.getProducto());
        this.resVentaProducto.setCantOrdenada(this.ventaProducto.getCantOrdenada());
        this.resVentaProducto.setCantFacturada(this.ventaProducto.getCantFacturada());
        this.resVentaProducto.setCantSinCargo(this.ventaProducto.getCantSinCargo());
        this.resVentaProducto.setCosto(this.ventaProducto.getCosto());
        this.resVentaProducto.setDesctoConfidencial(this.ventaProducto.getDesctoConfidencial());
        this.resVentaProducto.setDesctoProducto1(this.ventaProducto.getDesctoProducto1());
        this.resVentaProducto.setDesctoProducto2(this.ventaProducto.getDesctoProducto2());
        this.resVentaProducto.setUnitario(this.ventaProducto.getUnitario());
        this.resVentaProducto.setImpuestos(this.ventaProducto.getImpuestos());
        this.resVentaProducto.setNeto(this.ventaProducto.getNeto());
        this.resVentaProducto.setImporte(this.ventaProducto.getImporte());
        this.resVentaProducto.setLotes(this.ventaProducto.getLotes());
    }
    
    public void salir() {
        this.inicializar();
        this.modoEdicion = false;
    }
    
    public void capturar() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "capturar");
        if(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un almacen");
//        } else if(this.mbClientes.getCliente().getIdContribuyente()==0) {
        } else if(this.mbFormatos.getCmbClientesFormatos().getIdFormato()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un formato");
//        } else if(this.mbTiendas.getMiniTienda().getIdTienda()==0) {
        } else if(this.mbClientes.getMiniCliente().getIdCliente()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un cliente");
        } else {
            this.venta=new Venta();
            this.venta.setAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen());
            try {
                TOComprobante to=new TOComprobante();
//                to.setIdAlmacen(this.mbTiendas.getMiniTienda().getIdTienda());
                to.setIdAlmacen(this.mbClientes.getMiniCliente().getIdCliente());
//                to.setIdProveedor(this.mbClientes.getCliente().getIdContribuyente());
//                to.setRemision("");
                this.dao=new DAOMovimientos();
                this.daoComprobantes=new DAOComprobantes();
                int idMovto=this.dao.agregarMovimientoRelacionado(this.convertirTO(), to);
                this.venta=this.convertir(this.dao.obtenerMovimiento(idMovto));
                //public ArrayList<TOMovimiento> movimientosPendientes(boolean oficina, int entrada)
                this.ventaDetalle=new ArrayList<VentaProducto>();
                this.ventaProducto=new VentaProducto();
                this.modoEdicion=true;
                ok=true;
            } catch (SQLException ex) {
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    private Venta convertir(TOMovimiento to) throws SQLException {
        Venta vta=new Venta();
        vta.setIdMovto(to.getIdMovto());
        vta.setIdMovtoAlmacen(to.getIdMovtoAlmacen());
        vta.setFolio(to.getFolio());
        vta.setAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen());
        TOComprobante toComprobante=this.daoComprobantes.obtenerComprobante(to.getIdReferencia());
        VentaComprobante comprobante=new VentaComprobante();
//        comprobante.setTienda(this.mbTiendas.obtenerMiniTienda(toComprobante.getIdAlmacen()));
        comprobante.setCliente(this.mbClientes.obtenerMiniCliente(toComprobante.getIdAlmacen()));
        comprobante.setRemision(toComprobante.getRemision());
        comprobante.setFecha(toComprobante.getFecha());
        vta.setComprobante(comprobante);
        vta.setDesctoComercial(to.getDesctoComercial());
        vta.setDesctoProntoPago(to.getDesctoProntoPago());
        vta.setFecha(to.getFecha());
        vta.setFolio(to.getFolio());
        vta.setIdImpuestoZona(to.getIdImpuestoZona());
        vta.setIdUsuario(to.getIdUsuario());
        vta.setTipoCambio(to.getTipoCambio());
        return vta;
    }
    
    private TOMovimiento convertirTO() {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(this.venta.getIdMovto());
        to.setIdTipo(28);
        to.setFolio(this.venta.getFolio());
        to.setIdCedis(this.venta.getAlmacen().getIdCedis());
        to.setIdEmpresa(this.venta.getAlmacen().getIdEmpresa());
        to.setIdAlmacen(this.venta.getAlmacen().getIdAlmacen());
        to.setFecha(this.venta.getFecha());
        to.setIdUsuario(this.venta.getIdUsuario());
        return to;
    }
    
//    public void cargaTiendas() {
//        this.mbTiendas.cargaTiendas(this.mbClientes.getCliente().getIdContribuyente());
//    }
    
    public void cargarClientes() {
//        this.mbTiendas.cargarTiendas(this.mbFormatos.getCmbClientesFormatos().getIdFormato());
        this.mbClientes.cargarMiniClientes(this.mbFormatos.getCmbClientesFormatos().getIdFormato());
    }
    
    public void cargarFormatos() {
        int idGrupoCliente=this.mbGrupos.getClienteGrupoSeleccionado().getIdGrupoCte();
        this.mbFormatos.setLstFormatos(null);
        this.mbFormatos.setCmbClientesFormatos(new ClientesFormato());
        this.mbFormatos.cargarListaFormatos(idGrupoCliente);
        this.mbClientes.setIdClienteGpo(idGrupoCliente);
        this.mbClientes.nuevoMiniCliente();
        this.mbClientes.cargarMiniClientes(0);
    }
    
    public String terminar() {
        this.acciones=null;
        this.inicializar();
        return "index.xhtml";
    }
    
    private void inicializa() {
        this.inicializar();
    }
    
    public void inicializar() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        this.mbBuscar.inicializar();
        this.mbGrupos.inicializar();
        this.mbClientes.inicializar();
        this.mbFormatos.inicializar();
//        this.mbTiendas.inicializar();
        this.resVentaProducto=new VentaProducto();
        this.modoEdicion=false;
        this.ventaDetalle=new ArrayList<VentaProducto>();
    }

    public MbClientesGrupos getMbGrupos() {
        return mbGrupos;
    }

    public void setMbGrupos(MbClientesGrupos mbGrupos) {
        this.mbGrupos = mbGrupos;
    }

    public ArrayList<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(ArrayList<Venta> ventas) {
        this.ventas = ventas;
    }

    public double getSumaLotes() {
        return sumaLotes;
    }

    public void setSumaLotes(double sumaLotes) {
        this.sumaLotes = sumaLotes;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public ArrayList<VentaProducto> getVentaDetalle() {
        return ventaDetalle;
    }

    public void setVentaDetalle(ArrayList<VentaProducto> ventaDetalle) {
        this.ventaDetalle = ventaDetalle;
    }

    public VentaProducto getVentaProducto() {
        return ventaProducto;
    }

    public void setVentaProducto(VentaProducto ventaProducto) {
        this.ventaProducto = ventaProducto;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(29);
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

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

//    public MbMiniCliente getMbClientes() {
//        return mbClientes;
//    }
//
//    public void setMbClientes(MbMiniCliente mbClientes) {
//        this.mbClientes = mbClientes;
//    }

//    public MbTiendas getMbTiendas() {
//        return mbTiendas;
//    }
//
//    public void setMbTiendas(MbTiendas mbTiendas) {
//        this.mbTiendas = mbTiendas;
//    }

    public MbFormatos getMbFormatos() {
        return mbFormatos;
    }

    public void setMbFormatos(MbFormatos mbFormatos) {
        this.mbFormatos = mbFormatos;
    }

    public MbClientes getMbClientes() {
        return mbClientes;
    }

    public void setMbClientes(MbClientes mbClientes) {
        this.mbClientes = mbClientes;
    }
}
