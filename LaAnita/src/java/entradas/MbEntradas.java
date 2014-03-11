package entradas;

import entradas.dao.DAOMovimientos;
import entradas.dominio.Entrada;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
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
import org.primefaces.event.SelectEvent;
import productos.MbBuscarEmpaques;
import productos.dao.DAOEmpaques;
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
    private boolean modoEdicion;
    
    private ArrayList<Accion> acciones;
    private ArrayList<Accion> acciones2;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value="#{mbBuscarEmpaques}")
    private MbBuscarEmpaques mbBuscar;
    @ManagedProperty(value="#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value="#{mbOrdenCompra}")
    private MbOrdenCompra mbOrdenCompra;
    @ManagedProperty(value="#{mbMonedas}")
    private MbMonedas mbMonedas;
    
//    private DAOAlmacenes daoAlmacenes;
//    private ArrayList<SelectItem> listaAlmacenes;
//    private ArrayList<Almacen> almacenes;
//    private Almacen almacen;
    
    private OrdenCompraEncabezado ordenCompra;
//    private Comprobante comprobante; 
    //private TOComprobante comprobante;
    private Entrada entrada;
    private ArrayList<Entrada> entradas;
    private MovimientoProducto entradaProducto;
    private MovimientoProducto resEntradaProducto;
    private ArrayList<MovimientoProducto> entradaDetalle;
    private Date fechaIniPeriodo=new Date();
    private Date fechaFinPeriodo=new Date();
    private DAOMovimientos dao;
    private DAOEmpaques daoEmpaques;
    private DAOImpuestosProducto daoImps;
    private boolean sinOrden;
    private double tipoCambio;  // Solo sirve para cuando hay cambio en el valor del tipo de cambio
//    private String documento;   // Documento que ampara la entrada por almacen
    private int idModulo;
//    private int tipoComprobante;
    
    public MbEntradas() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbBuscar=new MbBuscarEmpaques();
        this.mbComprobantes=new MbComprobantes();
        this.mbOrdenCompra=new MbOrdenCompra();
        this.mbMonedas=new MbMonedas();
        
        this.modoEdicion = false;
//        this.comprobante=new Comprobante();
        this.entrada=new Entrada();
        this.resEntradaProducto=new MovimientoProducto();
        this.ordenCompra=new OrdenCompraEncabezado();
//        this.tipoCambio=1.00;
//        this.tipoComprobante=0;
//        this.documento="";
    }
    
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
        boolean ok=true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(!ok) {
            this.entradaProducto.setCantRecibida(0.00);
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return ok;
    }
    
    public  void grabarEntradaAlmacen() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMovimientos();
            TOMovimiento toEntrada=convertirTO(this.entrada);
            if(this.dao.grabarEntradaAlmacen(toEntrada, this.entradaDetalle)) {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La entrada se grabo correctamente !!!");
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
    
    public void grabarEntradaOficina() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMovimientos();
            TOMovimiento toEntrada=convertirTO(this.entrada);
            if(this.dao.grabarEntradaOficina(toEntrada, this.entradaDetalle)) {
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La entrada se grabo correctamente !!!");
                this.mbComprobantes.cargaListaComprobantes();
                //this.mbComprobantes.setToComprobante(this.mbComprobantes.convertirTO(this.entrada.getComprobante()));
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
    
    public void cargaDetalleOrdenCompra(SelectEvent event) {
        boolean ok = false;
        int idMovto;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.ordenCompra = (OrdenCompraEncabezado) event.getObject();
        try {
            double unitario;
            this.daoEmpaques=new DAOEmpaques();
            this.dao=new DAOMovimientos();
            idMovto=this.dao.buscarEntrada(this.entrada.getComprobante().getIdComprobante(), this.ordenCompra.getIdOrdenCompra());
            if(idMovto==0) {
                this.entrada.setIdOrdenCompra(this.ordenCompra.getIdOrdenCompra());
                this.entrada.setDesctoComercial(this.ordenCompra.getDesctoComercial());
                this.entrada.setDesctoProntoPago(this.ordenCompra.getDesctoProntoPago());
                this.entrada.setMoneda(this.ordenCompra.getMoneda());
                this.entrada.setIdImpuestoZona(this.entrada.getComprobante().getProveedor().getIdImpuestoZona());
                
                this.mbOrdenCompra.setOrdenElegida(this.ordenCompra);
                this.mbOrdenCompra.obtenerDetalleOrdenCompra();
                MovimientoProducto prod;
                for(OrdenCompraDetalle d: this.mbOrdenCompra.getListaOrdenDetalle()) {
                    prod=new MovimientoProducto();
                    if(this.idModulo==13) {
                        prod.setCostoOrdenado(d.getCostoOrdenado());
                        prod.setCantOrdenada(d.getCantOrdenada());
                        prod.setCantSinCargo(0);
                        prod.setCantRecibida(d.getCantOrdenada());
                        prod.setPrecio(d.getCostoOrdenado());
                        prod.setDesctoProducto1(d.getDescuentoProducto());
                        prod.setDesctoProducto2(d.getDescuentoProducto2());
                        prod.setDesctoConfidencial(d.getDesctoConfidencial());
                        unitario=prod.getCostoOrdenado();
                        unitario*=(1-this.entrada.getDesctoComercial()/100.00);
                        unitario*=(1-this.entrada.getDesctoProntoPago()/100.00);
                        unitario*=(1-prod.getDesctoProducto1()/100.00);
                        unitario*=(1-prod.getDesctoProducto2()/100.00);
                        unitario*=(1-prod.getDesctoConfidencial()/100.00);
//                        unitario=Math.round(unitario*100.00)/100.00;
                        prod.setUnitario(unitario);
                    } else {
                        prod.setCantOrdenada(d.getCantOrdenada()-d.getCantRecibida());
                        prod.setCantFacturada(0);
                    }
                    prod.setEmpaque(this.daoEmpaques.obtenerEmpaque(d.getSku()));
                    this.entradaDetalle.add(prod);
                }
                TOMovimiento toMovimiento=convertirTO(this.entrada);
                this.entrada.setIdEntrada(this.dao.agregarEntrada(toMovimiento, this.entradaDetalle, this.entrada.getIdOrdenCompra()));
            } else {
                TOMovimiento to=this.dao.obtenerMovimiento(idMovto);
                this.entrada=convertir(to);
                this.entrada.setIdOrdenCompra(this.ordenCompra.getIdOrdenCompra());
            }
            this.daoImps=new DAOImpuestosProducto();
            this.entradaDetalle=this.dao.obtenerDetalleMovimiento(this.entrada.getIdEntrada());
            for(MovimientoProducto p:this.entradaDetalle) {
                p.setEmpaque(this.daoEmpaques.obtenerEmpaque(p.getEmpaque().getIdEmpaque()));
                p.setImpuestos(this.daoImps.obtenerImpuestosProducto(this.entrada.getIdEntrada(), p.getEmpaque().getIdEmpaque()));
            }
            this.sinOrden=false;
            this.tipoCambio=this.entrada.getTipoCambio();
            this.cambiaPrecios();
            this.entradaProducto=null;
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
    
    public TOMovimiento convertirTO(Entrada entrada) {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(entrada.getIdEntrada());
        to.setIdTipo(1);
        to.setIdAlmacen(entrada.getComprobante().getAlmacen().getIdAlmacen());
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
    
    public Entrada convertir(TOMovimiento to) {
        Entrada e=new Entrada();
        e.setIdEntrada(to.getIdMovto());
        e.setComprobante(this.mbComprobantes.obtenerComprobante(to.getIdReferencia()));
        e.setIdImpuestoZona(to.getIdImpuestoZona());
        e.setMoneda(this.mbMonedas.obtenerMoneda(to.getIdMoneda()));
        e.setTipoCambio(to.getTipoCambio());
        e.setDesctoComercial(to.getDesctoComercial());
        e.setDesctoProntoPago(to.getDesctoProntoPago());
        e.setFecha(to.getFecha());
        e.setIdUsuario(to.getIdUsuario());
        return e;
    }
    
//    public void grabarComprobante() {
//        if(this.mbComprobantes.grabarComprobante()) {
//            this.comprobante.setSerie(this.mbComprobantes.getComprobante().getSerie());
//            this.comprobante.setNumero(this.mbComprobantes.getComprobante().getNumero());
//            this.comprobante.setFecha(this.mbComprobantes.getComprobante().getFecha());
//            this.comprobante.setCerradaOficina(this.mbComprobantes.getComprobante().isCerradaOficina());
//            this.comprobante.setCerradaAlmacen(this.mbComprobantes.getComprobante().isCerradaAlmacen());
//        }
//    }
    
//    public void buscarFacturas() {
//        boolean ok = false;
//        boolean abrir = false;
//        RequestContext context = RequestContext.getCurrentInstance();
//        int idAlmacen=this.almacen.getIdAlmacen();
//        int idProveedor=this.mbProveedores.getMiniProveedor().getIdProveedor();
//        int tipoComprobante=this.mbComprobantes.getComprobante().getTipoComprobante();
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(idProveedor==0) {
//            fMsg.setDetail("Se requiere seleccionar un proveedor");
//        } else if(idAlmacen==0) {
//            fMsg.setDetail("Se requiere seleccionar un almacen");
//        } else {
//            if(this.comprobante.getSerie().isEmpty() && this.comprobante.getNumero().isEmpty()) {
//                this.mbComprobantes.obtenerComprobantes(idAlmacen, idProveedor, tipoComprobante);
//                if(this.mbComprobantes.getComprobantes().isEmpty()) {
//                    fMsg.setDetail("No se encontraron facturas del proveedor");
//                } else {
//                    ok=true;
//                    abrir=true;
//                }
//            } else {
//                Comprobante c=this.mbComprobantes.obtenerComprobante(idAlmacen, idProveedor, tipoComprobante, this.comprobante.getSerie(), this.comprobante.getNumero());
//                if(c==null) {
//                    fMsg.setDetail("No se encontro la factura con el proveedor especificado");
//                } else {
//                    this.comprobante=c;
//                    ok=true;
//                }
//            }
//        }
//        if(!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        context.addCallbackParam("okBuscarComprobante", abrir);
//    }
    
    public void cambiaPrecios() {
        this.entrada.setSubTotal(0.00);
        this.entrada.setDescuento(0.00);
        this.entrada.setImpuesto(0.00);
        this.entrada.setTotal(0.00);
        
        MovimientoProducto ep=this.entradaProducto;
        for(MovimientoProducto p: this.entradaDetalle) {
            this.entradaProducto=p;
            this.entradaProducto.setPrecio(this.entradaProducto.getPrecio()/this.tipoCambio);
            this.entradaProducto.setPrecio(this.entradaProducto.getPrecio()*this.entrada.getTipoCambio());
            calculaProducto();
            sumaTotales();
        }
        this.tipoCambio=this.entrada.getTipoCambio();
        
        for(MovimientoProducto p: this.entradaDetalle) {
            if(p.equals(ep)) {
                this.entradaProducto=p;
                this.respaldaFila();
            }
        }
    }
    
    public double calculaImpuestos() {
        double impuestos=0.00;
        double precioConImpuestos=this.entradaProducto.getUnitario();
        for(ImpuestosProducto i: this.entradaProducto.getImpuestos()) {
            if(i.isAcumulable()) {
                if(i.isAplicable()) {
                    if(i.getModo()==1) {
                        i.setImporte(precioConImpuestos*i.getValor()/100.00);
                    } else {
                        i.setImporte(this.entradaProducto.getEmpaque().getPiezas()*i.getValor());
                    }
                    precioConImpuestos+=i.getImporte();
                } else {
                    i.setImporte(0.00);
                }
                impuestos+=i.getImporte();
            }
        }
        for(ImpuestosProducto i: this.entradaProducto.getImpuestos()) {
            if(!i.isAcumulable()) {
                if(i.isAplicable()) {
                    if(i.getModo()==1) {
                        i.setImporte(precioConImpuestos*i.getValor()/100.00);
                    } else {
                        i.setImporte(this.entradaProducto.getEmpaque().getPiezas()*i.getValor());
                    }
                } else {
                    i.setImporte(0.00);
                }
                impuestos+=i.getImporte();
            }
        }
        return impuestos;
    }
    
    private void sumaTotales() {
        double suma;
        suma=this.entradaProducto.getPrecio()*this.entradaProducto.getCantFacturada();   // Calcula el subTotal
        this.entrada.setSubTotal(this.entrada.getSubTotal()+Math.round(suma*100.00)/100.00);    // Suma el importe el subtotal
        
        suma=this.entradaProducto.getPrecio()-this.entradaProducto.getUnitario();   // Obtine el descuento por diferencia.
        suma=suma*this.entradaProducto.getCantFacturada();                           // Calcula el importe de descuento
        this.entrada.setDescuento(this.entrada.getDescuento()+Math.round(suma*100.00)/100.00);  // Suma el descuento
        
        suma=this.entradaProducto.getNeto()-this.entradaProducto.getUnitario();     // Obtiene el impuesto por diferencia
        suma=suma*this.entradaProducto.getCantFacturada();                           // Calcula el importe de impuestos
        this.entrada.setImpuesto(this.entrada.getImpuesto()+Math.round(suma*100.00)/100.00);    // Suma los impuestos
        
        suma=this.entradaProducto.getNeto()*this.entradaProducto.getCantFacturada(); // Calcula el importe total
        this.entrada.setTotal(this.entrada.getTotal()+Math.round(suma*100.00)/100.00);          // Suma el importe al total
    }
    
    private void restaTotales() {
        double resta;
        resta=this.resEntradaProducto.getPrecio()*this.resEntradaProducto.getCantFacturada();
        this.entrada.setSubTotal(this.entrada.getSubTotal()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getPrecio()-this.resEntradaProducto.getUnitario();
        resta=resta*this.resEntradaProducto.getCantFacturada();
        this.entrada.setDescuento(this.entrada.getDescuento()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getNeto()-this.resEntradaProducto.getUnitario();
        resta=resta*this.resEntradaProducto.getCantFacturada();
        this.entrada.setImpuesto(this.entrada.getImpuesto()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getNeto()*this.resEntradaProducto.getCantFacturada();
        this.entrada.setTotal(this.entrada.getTotal()-Math.round(resta*100.00)/100.00);
    }
    
    public void cambiaDescto() {
        restaTotales();
        calculaProducto();
        sumaTotales();
    }
    
    public void cambiaPrecio() {
        restaTotales();
        this.entradaProducto.setPrecio(this.entradaProducto.getPrecio()*this.entrada.getTipoCambio());
        calculaProducto();
        sumaTotales();
    }
    
    public void cambiaCantSinCargo() {
        boolean ok=true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.entradaProducto.getCantSinCargo() > this.entradaProducto.getCantFacturada()) {
            ok=false;
            fMsg.setDetail("La cantidad sin cargo no puede ser mayor a la cantidad facturada");
        }
        if(!ok) {
            this.entradaProducto.setCantFacturada(0.00);
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public void cambiaCantFacturada() {
        boolean ok=true;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(!isSinOrden() && this.entradaProducto.getCantOrdenada() < this.entradaProducto.getCantFacturada()) {
//            ok=false;
//            fMsg.setDetail("La cantidad recibida no puede ser mayor a la cantidad pendiente por recibir");
//        } else {
            restaTotales();
            calculaProducto();
            sumaTotales();
//        }
        if(!ok) {
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
        this.resEntradaProducto.setEmpaque(this.entradaProducto.getEmpaque());
        this.resEntradaProducto.setImporte(this.entradaProducto.getImporte());
        this.resEntradaProducto.setNeto(this.entradaProducto.getNeto());
        this.resEntradaProducto.setUnitario(this.entradaProducto.getUnitario());
        this.resEntradaProducto.setPrecio(this.entradaProducto.getPrecio());
    }
    
    private void calculaProducto() {
        double unitario=this.entradaProducto.getPrecio();
        unitario*=(1-this.entrada.getDesctoComercial()/100.00);
        unitario*=(1-this.entrada.getDesctoProntoPago()/100.00);
        unitario*=(1-this.entradaProducto.getDesctoProducto1()/100.00);
        unitario*=(1-this.entradaProducto.getDesctoProducto2()/100.00);
        unitario*=(1-this.entradaProducto.getDesctoConfidencial()/100.00);
        //unitario=Math.round(unitario*100.00)/100.00;
        this.entradaProducto.setUnitario(unitario);
        //double neto=Math.round((this.entradaProducto.getUnitario()+calculaImpuestos())*100.00)/100.00;
        double neto=unitario+calculaImpuestos();
        this.entradaProducto.setNeto(neto);
        //double subTotal=Math.round(this.entradaProducto.getUnitario()*this.entradaProducto.getCantFacturada()*100.00)/100.00;
        double subTotal=this.entradaProducto.getUnitario()*this.entradaProducto.getCantFacturada();
        this.entradaProducto.setImporte(subTotal);
        this.respaldaFila();
    }
    
    public void actualizaProductoSeleccionado() {
        boolean nuevo=true;
        MovimientoProducto producto=new MovimientoProducto();
        producto.setEmpaque(this.mbBuscar.getProducto());
        for(MovimientoProducto p:this.entradaDetalle) {
            if(p.equals(producto)) {
                this.entradaProducto=p;
                nuevo=false;
                break;
            }
        }
        if(nuevo) {
            this.entradaDetalle.add(producto);
            this.entradaProducto=producto;
        }
        this.respaldaFila();
    }
    
    public void buscar() {
        this.mbBuscar.buscarLista();
        if(this.mbBuscar.getProducto()!=null) {
            this.actualizaProductoSeleccionado();
        }
    }
    
//    public void cargaOrdenes() {
//        
//    }
    
    public void entradas() {
        this.mbComprobantes.convertirComprobante();
        this.entrada=new Entrada();
        this.entrada.setComprobante(this.mbComprobantes.getComprobante());
        this.entradaDetalle=new ArrayList<MovimientoProducto>();
        this.ordenCompra=new OrdenCompraEncabezado();
        this.tipoCambio=1;
        this.sinOrden=true;
        this.modoEdicion=true;
    }
    
//    public void entradas2() {
//        boolean ok = false;
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(this.mbAlmacenes.getAlmacen().getIdAlmacen()==0) {
//            fMsg.setDetail("Se requiere seleccionar un almacen !!");
//        } else if(this.mbProveedores.getMiniProveedor().getIdProveedor()==0) {
//            fMsg.setDetail("Se requiere seleccionar un proveedor !!");
////        } else if(this.documento.isEmpty()) {
////            fMsg.setDetail("Se requiere capturar el documento de recepcion !!");
//        } else {
//            this.entrada=new Entrada();
//            this.entrada.setIdEntrada(0);
//            //this.entrada.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
////            this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
////            this.entrada.setIdAlmacen(this.mbAlmacenes.getToAlmacen().getIdAlmacen());
//            //this.entrada.setDocumento(this.documento);
//            this.entradaDetalle=new ArrayList<EntradaProducto>();
//            this.ordenCompra=new OrdenCompraEncabezado();
//            this.sinOrden=false;
//            this.modoEdicion=true;
//            ok=true;
//        }
//        if(!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        context.addCallbackParam("okFactura", ok);
//    }
    
//    public void mttoComprobantes() {
//        boolean ok = false;
//        int idAlmacen=this.comprobante.getAlmacen().getIdAlmacen();
//        int idProveedor=this.comprobante.getProveedor().getIdProveedor();
////        int tipoComprobante=this.tipoComprobante;
//        
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(idAlmacen==0) {
//            fMsg.setDetail("Se requiere seleccionar un almacen !!");
//        } else if(idProveedor==0) {
//            fMsg.setDetail("Se requiere seleccionar un proveedor !!");
//        } else if(this.tipoComprobante==0) {
//            fMsg.setDetail("Se requiere seleccionar un tipo de comprobante !!");
//        } else if(this.comprobante.getIdComprobante()==0) {
////            this.mbComprobantes.setComprobante(new TOComprobante(idAlmacen, idProveedor, this.tipoComprobante));
//            ok=true;
//         } else {
//            this.mbComprobantes.copia(this.comprobante);
//            ok=true;
//        }
//        if(!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        context.addCallbackParam("okComprobante", ok);
//    }
    
    // Este metodo se ejecutaba al seleccionar un almacen de la lista
    public void inicializarEntrada() {
        this.modoEdicion=true;
        this.entrada=new Entrada();
        this.ordenCompra=new OrdenCompraEncabezado();
//        this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
//        this.tipoComprobante=0;
        this.entradaDetalle=new ArrayList<MovimientoProducto>();
    }
    
    public void salir() {
        this.modoEdicion=false;
        this.ordenCompra=new OrdenCompraEncabezado();
        this.entrada=new Entrada();
        //return "entradas.xhtml";
    }
    
    public String terminar() {
        this.modoEdicion = false;
        this.acciones = null;
        
        this.mbComprobantes.getMbAlmacenes().inicializaAlmacen();
        this.mbComprobantes.getMbProveedores().setMiniProveedor(new MiniProveedor());
//        this.tipoComprobante=0;
//        this.comprobante=new Comprobante();
        return "index.xhtml";
    }
    
//    public void cargaListaAlmacenes() {
//        boolean ok = false;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        try {
//            if (this.mbCedis.getCedis().getIdCedis() != 0 && this.mbEmpresas.getEmpresa().getIdEmpresa() != 0) {
//                this.daoAlmacenes = new DAOAlmacenes();
//                this.almacenes = this.daoAlmacenes.obtenerAlmacenes(this.mbCedis.getCedis().getIdCedis(), this.mbEmpresas.getEmpresa().getIdEmpresa());
//            } else {
//                this.almacenes=new ArrayList<Almacen>();
//            }
//            this.listaAlmacenes=new ArrayList<SelectItem>();
//            Almacen xAlm=new Almacen();
//            xAlm.setAlmacen("Seleccione un Almacén");
//            SelectItem a0=new SelectItem(xAlm, xAlm.toString());
//            this.listaAlmacenes.add(a0);
//            
//            for(Almacen a: this.almacenes) {
//                this.listaAlmacenes.add(new SelectItem(a, a.toString()));
//            }
//            ok = true;
//        } catch (SQLException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//        } catch (NamingException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getMessage());
//        }
//        if (!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//    }
    
//    public void obtenerListaComprobantes() {
//        boolean ok = false;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(validaComprobante()) {
//            
//        }
//        if(this.mbAlmacenes.getAlmacen().getIdAlmacen()==0) {
//            this.tipoComprobante=0;
//            this.mbComprobantes.setListaComprobantes(null);
//            fMsg.setDetail("Se requiere tener seleccionado un almacen");
//        } else if(this.mbProveedores.getMiniProveedor().getIdProveedor()==0) {
//            this.tipoComprobante=0;
//            this.mbComprobantes.setListaComprobantes(null);
//            fMsg.setDetail("Se requiere tener seleccionado un proveedor");
//        } else if(this.tipoComprobante==0) {
//            this.mbComprobantes.setListaComprobantes(null);
//            fMsg.setDetail("Se requiere tener seleccionado un tipo de comprobante");
//        } else {
////            this.mbComprobantes.obtenerListaComprobantes(this.mbAlmacenes.getAlmacen().getIdAlmacen(), this.mbProveedores.getMiniProveedor().getIdProveedor(), this.tipoComprobante);
//            ok=true;
//        }
//        if(!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//    }
    
//    public void inicializaDocumentos() {
//        this.almacen=new Almacen();
//        this.listaAlmacenes=null;
//        this.entrada.setTipoComprobante(0);
//        this.mbComprobantes.setListaComprobantes(null);
//    }
    
//    public void obtenerAlmacen() {
////        this.comprobante.getAlmacen().setIdAlmacen(this.mbAlmacenes.getToAlmacen().getIdAlmacen());
////        this.comprobante.getAlmacen().setAlmacen(this.mbAlmacenes.getToAlmacen().getAlmacen());
//        this.obtenerListaComprobantes();
//    }

//    public ArrayList<Almacen> getAlmacenes() {
//        return almacenes;
//    }
//
//    public void setAlmacenes(ArrayList<Almacen> almacenes) {
//        this.almacenes = almacenes;
//    }
//
//    public Almacen getAlmacen() {
//        return almacen;
//    }
//
//    public void setAlmacen(Almacen almacen) {
//        this.almacen = almacen;
//    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }
    
    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.idModulo=idModulo;
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
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

//    public MbMiniCedis getMbCedis() {
//        return mbCedis;
//    }
//
//    public void setMbCedis(MbMiniCedis mbCedis) {
//        this.mbCedis = mbCedis;
//    }
//
//    public MbMiniEmpresas getMbEmpresas() {
//        return mbEmpresas;
//    }
//
//    public void setMbEmpresas(MbMiniEmpresas mbEmpresas) {
//        this.mbEmpresas = mbEmpresas;
//    }

    public OrdenCompraEncabezado getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompraEncabezado ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

//    public Comprobante getComprobante() {
//        return comprobante;
//    }
//
//    public void setComprobante(Comprobante comprobante) {
//        this.comprobante = comprobante;
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

    public MbBuscarEmpaques getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbBuscarEmpaques mbBuscar) {
        this.mbBuscar = mbBuscar;
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

//    public ArrayList<SelectItem> getListaAlmacenes() {
//        if(this.listaAlmacenes==null) {
//            this.cargaListaAlmacenes();
//        }
//        return listaAlmacenes;
//    }
//
//    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
//        this.listaAlmacenes = listaAlmacenes;
//    }

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

//    public String getDocumento() {
//        return documento;
//    }
//
//    public void setDocumento(String documento) {
//        this.documento = documento;
//    }

//    public MbMiniAlmacenes getMbAlmacenes() {
//        return mbAlmacenes;
//    }
//
//    public void setMbAlmacenes(MbMiniAlmacenes mbAlmacenes) {
//        this.mbAlmacenes = mbAlmacenes;
//    }

//    public int getTipoComprobante() {
//        return tipoComprobante;
//    }
//
//    public void setTipoComprobante(int tipoComprobante) {
//        this.tipoComprobante = tipoComprobante;
//    }
}
