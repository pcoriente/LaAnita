package entradas;

import entradas.dao.DAOMovimientos;
import entradas.dominio.Entrada;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
import impuestos.dao.DAOImpuestosProducto;
import impuestos.dominio.ImpuestosProducto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import monedas.MbMonedas;
import ordenesDeCompra.MbOrdenCompra;
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import producto2.MbProductosBuscar;
import producto2.dominio.Producto;
import proveedores.dominio.MiniProveedor;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jsolis
 */
@Named(value = "mbEntradas")
@SessionScoped
public class MbEntradas implements Serializable {
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;
    @ManagedProperty(value = "#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value = "#{mbOrdenCompra}")
    private MbOrdenCompra mbOrdenCompra;
    @ManagedProperty(value = "#{mbMonedas}")
    private MbMonedas mbMonedas;
    
    private boolean modoEdicion;
//    private OrdenCompraEncabezado ordenCompra;
    private Entrada entrada;
    private Entrada selEntrada;
    private ArrayList<Entrada> entradas;
    private MovimientoProducto entradaProducto;
    private MovimientoProducto resEntradaProducto;
    private ArrayList<MovimientoProducto> entradaDetalle;
    private Date fechaIniPeriodo = new Date();
    private Date fechaFinPeriodo = new Date();
    private boolean sinOrden;
    private double tipoCambio;  // Solo sirve para cuando hay cambio en el valor del tipo de cambio
    private int idModulo;
    private DAOMovimientos dao;
    private DAOImpuestosProducto daoImps;

    public MbEntradas() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbBuscar = new MbProductosBuscar();
        this.mbComprobantes = new MbComprobantes();
        this.mbOrdenCompra = new MbOrdenCompra();
        this.mbMonedas = new MbMonedas();
        this.inicializaLocales();
    }
    
    public void inicializar() {
        this.mbBuscar.inicializar();
        this.mbComprobantes.getMbAlmacenes().inicializaAlmacen();
        this.mbComprobantes.getMbProveedores().cargaListaProveedores();
        this.mbComprobantes.getMbProveedores().inicializaProveedor();
        this.mbComprobantes.setTipoComprobante(1);
        this.mbComprobantes.cargaListaComprobantes();
        this.inicializaLocales();
    }
    
    private void inicializaLocales() {
        this.modoEdicion = false;
        this.entrada = new Entrada();
        this.resEntradaProducto = new MovimientoProducto();
    }

//    private boolean validaEntradaOficina1x() {
//        boolean ok = false;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "validaComprobante");
//        if(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen()==0) {
//            fMsg.setDetail("Se requiere tener seleccionado un almacen");
//        } else if(this.mbComprobantes.getMbProveedores().getMiniProveedor().getIdProveedor()==0) {
//            fMsg.setDetail("Se requiere tener seleccionado un proveedor");
//        } else {
//            this.mbComprobantes.setTipoComprobante(1);
//            ok=true;
//        }
//        if(!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        return ok;
//    }
//    public void cargaListaComprobantes() {
//        if(this.mbComprobantes.validaComprobante()) {
//            this.mbComprobantes.setTipoComprobante(1);
//            this.mbComprobantes.cargaListaComprobantes();
//        }
//    }
//    public void mttoComprobanteOficina() {
//        boolean ok=false;
//        RequestContext context = RequestContext.getCurrentInstance();
//        if(this.mbComprobantes.validaComprobante()) {
//            this.mbComprobantes.setTipoComprobante(1);
//            this.mbComprobantes.mttoComprobante();
//            ok=true;
//        }
//        context.addCallbackParam("okComprobante", ok);
//    }
    public void cargaOrdenes() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.mbOrdenCompra.cargaOrdenesEncabezado(this.mbComprobantes.getComprobante().getProveedor().getIdProveedor(), 2);
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
    }

    public boolean validaCantidadRecibida() {
        boolean ok = true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (!ok) {
            this.entradaProducto.setCantRecibida(0.00);
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return ok;
    }

    public void grabarEntradaAlmacen() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOMovimientos();
            TOMovimiento toEntrada = convertirTO(this.entrada);
            if (this.dao.grabarEntradaAlmacen(toEntrada, this.entradaDetalle)) {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La entrada se grabo correctamente !!!");
                this.modoEdicion = false;
                ok = true;
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

    public void grabarEntradaOficina() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOMovimientos();
            TOMovimiento toEntrada = convertirTO(this.entrada);
            if (this.dao.grabarEntradaOficina(toEntrada, this.entradaDetalle)) {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La entrada se grabo correctamente !!!");
                this.mbComprobantes.cargaListaComprobantes();
                this.modoEdicion = false;
                ok = true;
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
    
//    private Producto convertir(TOProducto to, Articulo a) throws SQLException {
//        Producto p=new Producto();
//        p.setIdProducto(to.getIdProducto());
//        p.setCod_pro(to.getCod_pro());
//        p.setArticulo(a);
//        p.setPiezas(to.getPiezas());
//        p.setEmpaque(to.getEmpaque());
//        p.setSubProducto(to.getSubProducto());
//        p.setDun14(to.getDun14());
//        p.setPeso(to.getPeso());
//        p.setVolumen(to.getVolumen());
//        return p;
//    }

    public void cargaDetalleOrdenCompra(SelectEvent event) {
        boolean ok = false;
        int idMovto;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        this.ordenCompra = (OrdenCompraEncabezado) event.getObject();
        this.mbOrdenCompra.setOrdenElegida((OrdenCompraEncabezado) event.getObject());
        try {
            double unitario;
            this.dao = new DAOMovimientos();
            
            this.daoImps = new DAOImpuestosProducto();
//            this.daoEmpaques = new DAOEmpaques();
            idMovto = this.dao.buscarEntrada(this.entrada.getComprobante().getIdComprobante(), this.mbOrdenCompra.getOrdenElegida().getIdOrdenCompra());
            if (idMovto == 0) {
                if (this.mbOrdenCompra.aseguraOrdenCompra(this.mbOrdenCompra.getOrdenElegida().getIdOrdenCompra())) {
                    this.entrada.setIdOrdenCompra(this.mbOrdenCompra.getOrdenElegida().getIdOrdenCompra());
                    this.entrada.setDesctoComercial(this.mbOrdenCompra.getOrdenElegida().getDesctoComercial());
                    this.entrada.setDesctoProntoPago(this.mbOrdenCompra.getOrdenElegida().getDesctoProntoPago());
                    this.entrada.setMoneda(this.mbOrdenCompra.getOrdenElegida().getMoneda());
                    this.entrada.setIdImpuestoZona(this.entrada.getComprobante().getProveedor().getIdImpuestoZona());

//                    this.mbOrdenCompra.setOrdenElegida(this.ordenCompra);
                    this.mbOrdenCompra.obtenerDetalleOrdenCompra();
                    MovimientoProducto prod;
                    for (OrdenCompraDetalle d : this.mbOrdenCompra.getListaOrdenDetalle()) {
                        prod = new MovimientoProducto();
                        if (this.idModulo == 13) {
                            prod.setCostoOrdenado(d.getCostoOrdenado());
                            prod.setCantFacturada(d.getCantRecibida());
                            prod.setCantOrdenada(d.getCantOrdenada());
                            prod.setCantSinCargo(0);
                            prod.setCantRecibida(d.getCantOrdenada());
                            prod.setPrecio(d.getCostoOrdenado());
                            prod.setDesctoProducto1(d.getDescuentoProducto());
                            prod.setDesctoProducto2(d.getDescuentoProducto2());
                            prod.setDesctoConfidencial(d.getDesctoConfidencial());
                            unitario = prod.getCostoOrdenado();
                            unitario *= (1 - this.entrada.getDesctoComercial() / 100.00);
                            unitario *= (1 - this.entrada.getDesctoProntoPago() / 100.00);
                            unitario *= (1 - prod.getDesctoProducto1() / 100.00);
                            unitario *= (1 - prod.getDesctoProducto2() / 100.00);
                            unitario *= (1 - prod.getDesctoConfidencial() / 100.00);
                            prod.setUnitario(unitario);
                        } else {
                            prod.setCantOrdenada(d.getCantOrdenada() - d.getCantRecibida());
                            prod.setCantFacturada(0);
                        }
//                        prod.setEmpaque(convertir(this.daoEmpaques.obtenerEmpaque(d.getSku()),daoProds.obtenerProducto(d.getEmpaque().getProducto().getIdProducto())));
                        prod.setProducto(d.getProducto());
                        this.entradaDetalle.add(prod);
                    }
                    TOMovimiento toMovimiento = convertirTO(this.entrada);
                    idMovto = this.dao.agregarEntrada(toMovimiento, this.entradaDetalle, this.entrada.getIdOrdenCompra());
                }
            }
            TOMovimiento to = this.dao.obtenerMovimiento(idMovto);
            this.entrada = this.convertir(to);
            this.cargaDatosFactura(this.entrada.getIdEntrada());

            this.sinOrden = false;
            this.tipoCambio = this.entrada.getTipoCambio();
            this.cambiaPrecios();
            this.entradaProducto = null;
            ok = true;
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

    private void cargaDatosFactura(int idEntrada) throws NamingException, SQLException {
//        TOEmpaque to;
//        DAOProductos daoProds=new DAOProductos();
//        this.entradaDetalle = this.dao.obtenerDetalleMovimiento(idEntrada);
        this.entradaDetalle=new ArrayList<MovimientoProducto>();
        this.dao.obtenerDetalleMovimiento(idEntrada);
        for (TOMovimientoDetalle to : this.dao.obtenerDetalleMovimiento(idEntrada)) {
            this.convertir(idEntrada, to);
//            to=this.daoEmpaques.obtenerEmpaque(p.getEmpaque().getIdEmpaque());
//            p.setEmpaque(convertir(to, daoProds.obtenerProducto(to.getIdProducto())));
//            p.setImpuestos(this.daoImps.obtenerImpuestosProducto(idEntrada, p.getEmpaque().getIdEmpaque()));
        }
    }
    
    private MovimientoProducto convertir(int idEntrada, TOMovimientoDetalle to) throws SQLException {
        MovimientoProducto p=new MovimientoProducto();
        p.setProducto(this.mbBuscar.obtenerProducto(to.getIdProducto()));
        p.setCantFacturada(to.getCantFacturada());
        p.setCantOrdenada(to.getCantOrdenada());
        p.setCantRecibida(to.getCantRecibida());
        p.setCantSinCargo(to.getCantSinCargo());
        p.setDesctoProducto1(to.getDesctoProducto1());
        p.setDesctoProducto2(to.getDesctoProducto2());
        p.setDesctoConfidencial(to.getDesctoConfidencial());
        p.setImporte(to.getImporte());
        p.setImpuestos(this.daoImps.obtenerImpuestosProducto(idEntrada, to.getIdProducto()));
        p.setNeto(to.getNeto());
        p.setUnitario(to.getUnitario());
        return p;
    }

    public void cancelarEntrada() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOMovimientos();
//            this.daoImps = new DAOImpuestosProducto();
//            this.daoEmpaques = new DAOEmpaques();
            this.dao.cancelarEntrada(this.selEntrada.getIdEntrada());
            ok = true;
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
        context.addCallbackParam("okBuscar", ok);
    }

    public void cargaFactura() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOMovimientos();
            this.daoImps = new DAOImpuestosProducto();
//            this.daoEmpaques = new DAOEmpaques();
            this.cargaDatosFactura(this.selEntrada.getIdEntrada());
            ok = true;
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
        context.addCallbackParam("okBuscar", ok);
    }

    public void obtenerEntradas() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOMovimientos();
            this.entradas = new ArrayList<Entrada>();
            for (TOMovimiento m : this.dao.obtenerEntradas(this.mbComprobantes.getToComprobante().getIdComprobante())) {
                this.entradas.add(convertir(m));
            }
            this.selEntrada = null;
            ok = true;
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
        context.addCallbackParam("okBuscar", ok);
    }

    public TOMovimiento convertirTO(Entrada entrada) {
        TOMovimiento to = new TOMovimiento();
        to.setIdMovto(entrada.getIdEntrada());
        to.setIdTipo(1);
        to.setIdCedis(entrada.getComprobante().getAlmacen().getCedis().getIdCedis());
        to.setIdEmpresa(entrada.getComprobante().getAlmacen().getEmpresa().getIdEmpresa());
        to.setIdAlmacen(entrada.getComprobante().getAlmacen().getIdAlmacen());
        to.setFolio(entrada.getFolio());
        to.setIdReferencia(entrada.getComprobante().getIdComprobante());
        to.setIdImpuestoZona(entrada.getIdImpuestoZona());
        to.setIdMoneda(entrada.getMoneda().getIdMoneda());
        to.setTipoCambio(entrada.getTipoCambio());
        to.setDesctoComercial(entrada.getDesctoComercial());
        to.setDesctoProntoPago(entrada.getDesctoProntoPago());
        to.setFecha(entrada.getFecha());
        to.setIdUsuario(entrada.getIdUsuario());
        return to;
    }

    public Entrada convertir(TOMovimiento to) throws SQLException {
        Entrada e = new Entrada();
        e.setIdEntrada(to.getIdMovto());
        e.setComprobante(this.mbComprobantes.obtenerComprobante(to.getIdReferencia()));
        e.setIdOrdenCompra(this.dao.obtenerIdOrdenCompra(to.getIdReferencia(), to.getIdMovto()));
        e.setFolio(to.getFolio());
        e.setIdImpuestoZona(to.getIdImpuestoZona());
        e.setMoneda(this.mbMonedas.obtenerMoneda(to.getIdMoneda()));
        e.setTipoCambio(to.getTipoCambio());
        e.setDesctoComercial(to.getDesctoComercial());
        e.setDesctoProntoPago(to.getDesctoProntoPago());
        e.setFecha(to.getFecha());
        e.setIdUsuario(to.getIdUsuario());
        return e;
    }

    public void cambiaPrecios() {
        this.entrada.setSubTotal(0.00);
        this.entrada.setDescuento(0.00);
        this.entrada.setImpuesto(0.00);
        this.entrada.setTotal(0.00);

        MovimientoProducto ep = this.entradaProducto;
        for (MovimientoProducto p : this.entradaDetalle) {
            this.entradaProducto = p;
            this.entradaProducto.setPrecio(this.entradaProducto.getPrecio() / this.tipoCambio);
            this.entradaProducto.setPrecio(this.entradaProducto.getPrecio() * this.entrada.getTipoCambio());
            calculaProducto();
            sumaTotales();
        }
        this.tipoCambio = this.entrada.getTipoCambio();

        for (MovimientoProducto p : this.entradaDetalle) {
            if (p.equals(ep)) {
                this.entradaProducto = p;
                this.respaldaFila();
            }
        }
    }

    public double calculaImpuestos() {
        double impuestos = 0.00;
        double precioConImpuestos = this.entradaProducto.getUnitario();
        for (ImpuestosProducto i : this.entradaProducto.getImpuestos()) {
            if (i.isAcumulable()) {
                if (i.isAplicable()) {
                    if (i.getModo() == 1) {
                        i.setImporte(precioConImpuestos * i.getValor() / 100.00);
                    } else {
                        i.setImporte(this.entradaProducto.getProducto().getPiezas() * i.getValor());
                    }
                    precioConImpuestos += i.getImporte();
                } else {
                    i.setImporte(0.00);
                }
                impuestos += i.getImporte();
            }
        }
        for (ImpuestosProducto i : this.entradaProducto.getImpuestos()) {
            if (!i.isAcumulable()) {
                if (i.isAplicable()) {
                    if (i.getModo() == 1) {
                        i.setImporte(precioConImpuestos * i.getValor() / 100.00);
                    } else {
                        i.setImporte(this.entradaProducto.getProducto().getPiezas() * i.getValor());
                    }
                } else {
                    i.setImporte(0.00);
                }
                impuestos += i.getImporte();
            }
        }
        return impuestos;
    }

    private void sumaTotales() {
        double suma;
        suma = this.entradaProducto.getPrecio() * this.entradaProducto.getCantFacturada();   // Calcula el subTotal
        this.entrada.setSubTotal(this.entrada.getSubTotal() + Math.round(suma * 100.00) / 100.00);    // Suma el importe el subtotal

        suma = this.entradaProducto.getPrecio() - this.entradaProducto.getUnitario();   // Obtine el descuento por diferencia.
        suma = suma * this.entradaProducto.getCantFacturada();                           // Calcula el importe de descuento
        this.entrada.setDescuento(this.entrada.getDescuento() + Math.round(suma * 100.00) / 100.00);  // Suma el descuento

        suma = this.entradaProducto.getNeto() - this.entradaProducto.getUnitario();     // Obtiene el impuesto por diferencia
        suma = suma * this.entradaProducto.getCantFacturada();                           // Calcula el importe de impuestos
        this.entrada.setImpuesto(this.entrada.getImpuesto() + Math.round(suma * 100.00) / 100.00);    // Suma los impuestos

        suma = this.entradaProducto.getNeto() * this.entradaProducto.getCantFacturada(); // Calcula el importe total
        this.entrada.setTotal(this.entrada.getTotal() + Math.round(suma * 100.00) / 100.00);          // Suma el importe al total
    }

    private void restaTotales() {
        double resta;
        resta = this.resEntradaProducto.getPrecio() * this.resEntradaProducto.getCantFacturada();
        this.entrada.setSubTotal(this.entrada.getSubTotal() - Math.round(resta * 100.00) / 100.00);
        resta = this.resEntradaProducto.getPrecio() - this.resEntradaProducto.getUnitario();
        resta = resta * this.resEntradaProducto.getCantFacturada();
        this.entrada.setDescuento(this.entrada.getDescuento() - Math.round(resta * 100.00) / 100.00);
        resta = this.resEntradaProducto.getNeto() - this.resEntradaProducto.getUnitario();
        resta = resta * this.resEntradaProducto.getCantFacturada();
        this.entrada.setImpuesto(this.entrada.getImpuesto() - Math.round(resta * 100.00) / 100.00);
        resta = this.resEntradaProducto.getNeto() * this.resEntradaProducto.getCantFacturada();
        this.entrada.setTotal(this.entrada.getTotal() - Math.round(resta * 100.00) / 100.00);
    }

    public void cambiaDescto() {
        restaTotales();
        calculaProducto();
        sumaTotales();
    }

    public void cambiaPrecio() {
        restaTotales();
        this.entradaProducto.setPrecio(this.entradaProducto.getPrecio() * this.entrada.getTipoCambio());
        calculaProducto();
        sumaTotales();
    }

    public void cambiaCantSinCargo() {
        boolean ok = true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.entradaProducto.getCantSinCargo() > this.entradaProducto.getCantFacturada()) {
            ok = false;
            fMsg.setDetail("La cantidad sin cargo no puede ser mayor a la cantidad facturada");
        }
        if (!ok) {
            this.entradaProducto.setCantFacturada(0.00);
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public void cambiaCantFacturada() {
        boolean ok = true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        restaTotales();
        calculaProducto();
        sumaTotales();
        if (!ok) {
            this.entradaProducto.setCantFacturada(0.00);
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public void respaldaFila() {
        this.resEntradaProducto.setCantOrdenada(this.entradaProducto.getCantOrdenada());
        this.resEntradaProducto.setCantFacturada(this.entradaProducto.getCantFacturada());
        this.resEntradaProducto.setCantRecibida(this.entradaProducto.getCantRecibida());
        this.resEntradaProducto.setDesctoConfidencial(this.entradaProducto.getDesctoConfidencial());
        this.resEntradaProducto.setDesctoProducto1(this.entradaProducto.getDesctoProducto1());
        this.resEntradaProducto.setDesctoProducto2(this.entradaProducto.getDesctoProducto2());
        this.resEntradaProducto.setProducto(this.entradaProducto.getProducto());
        this.resEntradaProducto.setImporte(this.entradaProducto.getImporte());
        this.resEntradaProducto.setNeto(this.entradaProducto.getNeto());
        this.resEntradaProducto.setUnitario(this.entradaProducto.getUnitario());
        this.resEntradaProducto.setPrecio(this.entradaProducto.getPrecio());
    }

    private void calculaProducto() {
        double unitario = this.entradaProducto.getPrecio();
        unitario *= (1 - this.entrada.getDesctoComercial() / 100.00);
        unitario *= (1 - this.entrada.getDesctoProntoPago() / 100.00);
        unitario *= (1 - this.entradaProducto.getDesctoProducto1() / 100.00);
        unitario *= (1 - this.entradaProducto.getDesctoProducto2() / 100.00);
        unitario *= (1 - this.entradaProducto.getDesctoConfidencial() / 100.00);
        this.entradaProducto.setUnitario(unitario);
        double neto = unitario + calculaImpuestos();
        this.entradaProducto.setNeto(neto);
        double subTotal = this.entradaProducto.getUnitario() * this.entradaProducto.getCantFacturada();
        this.entradaProducto.setImporte(subTotal);
        this.respaldaFila();
    }

    public void actualizaProductosSeleccionados() {
        for (Producto p : this.mbBuscar.getSeleccionados()) {
            this.mbBuscar.setProducto(p);
            this.actualizaProductoSeleccionado();
        }
    }

    public void actualizaProductoSeleccionado() {
        boolean nuevo = true;
        MovimientoProducto producto = new MovimientoProducto();
        producto.setProducto(this.mbBuscar.getProducto());
        for (MovimientoProducto p : this.entradaDetalle) {
            if (p.equals(producto)) {
                this.entradaProducto = p;
                nuevo = false;
                break;
            }
        }
        if (nuevo) {
            boolean ok = false;
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
            try {
                this.dao = new DAOMovimientos();
                producto.setImpuestos(this.dao.generarImpuestosProducto(producto.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo(), this.entrada.getIdImpuestoZona()));
                producto.setPrecio(this.dao.obtenerPrecioUltimaCompra(this.entrada.getComprobante().getAlmacen().getEmpresa().getIdEmpresa(), producto.getProducto().getIdProducto()));
                this.entradaDetalle.add(producto);
                this.entradaProducto = producto;
                this.calculaProducto();
                ok = true;
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
    }

    public void buscar() {
        this.mbBuscar.buscarLista();
        if (this.mbBuscar.getProducto() != null) {
            this.actualizaProductoSeleccionado();
        }
    }

    public void entradas() {
        this.mbComprobantes.convertirComprobante();
        this.entrada = new Entrada();
        this.entrada.setComprobante(this.mbComprobantes.getComprobante());
        this.entrada.setIdImpuestoZona(this.mbComprobantes.getMbProveedores().getMiniProveedor().getIdImpuestoZona());
        this.entradaDetalle = new ArrayList<MovimientoProducto>();
//        this.ordenCompra = new OrdenCompraEncabezado();
        this.tipoCambio = 1;
        this.sinOrden = true;
        this.modoEdicion = true;
    }

    // Este metodo se ejecutaba al seleccionar un almacen de la lista
//    public void inicializarEntrada() {
//        this.modoEdicion=true;
//        this.entrada=new Entrada();
//        this.ordenCompra=new OrdenCompraEncabezado();
//        this.entradaDetalle=new ArrayList<MovimientoProducto>();
//    }
    public void salir() {
        this.modoEdicion = false;
//        this.ordenCompra = new OrdenCompraEncabezado();
        this.entrada = new Entrada();
    }

    public String terminar() {
        this.modoEdicion = false;
        this.acciones = null;

        this.mbComprobantes.getMbAlmacenes().inicializaAlmacen();
        this.mbComprobantes.getMbProveedores().setMiniProveedor(new MiniProveedor());
        return "index.xhtml";
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.idModulo = idModulo;
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
            this.inicializar();
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(13);
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

//    public OrdenCompraEncabezado getOrdenCompra() {
//        return ordenCompra;
//    }
//
//    public void setOrdenCompra(OrdenCompraEncabezado ordenCompra) {
//        this.ordenCompra = ordenCompra;
//    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public MovimientoProducto getEntradaProducto() {
        return entradaProducto;
    }

    public void setEntradaProducto(MovimientoProducto entradaProducto) {
        this.entradaProducto = entradaProducto;
    }

    public ArrayList<MovimientoProducto> getEntradaDetalle() {
        return entradaDetalle;
    }

    public void setEntradaDetalle(ArrayList<MovimientoProducto> entradaDetalle) {
        this.entradaDetalle = entradaDetalle;
    }

    public MovimientoProducto getResEntradaProducto() {
        return resEntradaProducto;
    }

    public void setResEntradaProducto(MovimientoProducto resEntradaProducto) {
        this.resEntradaProducto = resEntradaProducto;
    }

    public MbComprobantes getMbComprobantes() {
        return mbComprobantes;
    }

    public void setMbComprobantes(MbComprobantes mbComprobantes) {
        this.mbComprobantes = mbComprobantes;
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

    public MbOrdenCompra getMbOrdenCompra() {
        return mbOrdenCompra;
    }

    public void setMbOrdenCompra(MbOrdenCompra mbOrdenCompra) {
        this.mbOrdenCompra = mbOrdenCompra;
    }

    public ArrayList<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<Entrada> entradas) {
        this.entradas = entradas;
    }

    public boolean isSinOrden() {
        return sinOrden;
    }

    public void setSinOrden(boolean sinOrden) {
        this.sinOrden = sinOrden;
    }

    public MbMonedas getMbMonedas() {
        return mbMonedas;
    }

    public void setMbMonedas(MbMonedas mbMonedas) {
        this.mbMonedas = mbMonedas;
    }

    public Entrada getSelEntrada() {
        return selEntrada;
    }

    public void setSelEntrada(Entrada selEntrada) {
        this.selEntrada = selEntrada;
    }

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }
}
