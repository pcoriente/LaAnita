package entradas;

import almacenes.dao.DAOAlmacenes;
import almacenes.dominio.Almacen;
import cedis.MbMiniCedis;
import cedis.dominio.MiniCedis;
import empresas.MbMiniEmpresas;
import empresas.dominio.MiniEmpresa;
import entradas.dao.DAOEntradas;
import entradas.dominio.Entrada;
import entradas.dominio.EntradaProducto;
import entradas.dominio.Factura;
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
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import ordenesDeCompra.MbOrdenCompra;
import ordenesDeCompra.dominio.OrdenCompraDetalle;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import productos.MbBuscarEmpaques;
import productos.dao.DAOEmpaques;
import proveedores.MbMiniProveedor;
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
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbMiniCedis}")
    private MbMiniCedis mbCedis;
    @ManagedProperty(value = "#{mbMiniEmpresas}")
    private MbMiniEmpresas mbEmpresas;
    @ManagedProperty(value = "#{mbMiniProveedor}")
    private MbMiniProveedor mbProveedores;
    @ManagedProperty(value="#{mbBuscarEmpaques}")
    private MbBuscarEmpaques mbBuscar;
    @ManagedProperty(value="#{mbFacturas}")
    private MbFacturas mbFacturas;
    @ManagedProperty(value="#{mbOrdenCompra}")
    private MbOrdenCompra mbOrdenCompra;
    
    private DAOAlmacenes daoAlmacenes;
    private ArrayList<SelectItem> listaAlmacenes;
    private ArrayList<Almacen> almacenes;
    private Almacen almacen;
    
    private OrdenCompraEncabezado ordenCompra;
    private Factura factura;
    private Entrada entrada;
    private ArrayList<Entrada> entradas;
    private EntradaProducto entradaProducto;
    private EntradaProducto resEntradaProducto;
    private ArrayList<EntradaProducto> entradaDetalle;
    private Date fechaIniPeriodo=new Date();
    private Date fechaFinPeriodo=new Date();
    private DAOEntradas dao;
    private DAOEmpaques daoEmpaques;
    private DAOImpuestosProducto daoImps;
    private boolean sinOrden;
    
    public MbEntradas() throws NamingException {
        this.modoEdicion = false;
        this.factura=new Factura();
        this.resEntradaProducto=new EntradaProducto();
        this.ordenCompra=new OrdenCompraEncabezado();
        
        this.mbAcciones = new MbAcciones();
        this.mbCedis = new MbMiniCedis();
        this.mbEmpresas = new MbMiniEmpresas();
        this.mbProveedores = new MbMiniProveedor();
        this.mbBuscar=new MbBuscarEmpaques();
        this.mbFacturas=new MbFacturas();
        this.mbOrdenCompra=new MbOrdenCompra();
    }
    
    public void cargaDetalleOrdenCompra(SelectEvent event) {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.ordenCompra = (OrdenCompraEncabezado) event.getObject();
        try {
            double unitario;
            this.entrada=new Entrada();
            this.entrada.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
            this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
            this.entrada.setIdImpuestoZona(this.mbProveedores.getMiniProveedor().getIdImpuestoZona());
            this.entrada.setIdAlmacen(this.almacen.getIdAlmacen());
            this.entrada.setDesctoComercial(this.ordenCompra.getDesctoComercial());
            this.entrada.setDesctoProntoPago(this.ordenCompra.getDesctoProntoPago());
            this.entrada.setIdFactura(this.factura.getIdFactura());
            this.entrada.setIdOrdenCompra(this.ordenCompra.getIdOrdenCompra());
            
            this.dao=new DAOEntradas();
            this.daoEmpaques=new DAOEmpaques();
            this.entrada.setIdEntrada(this.dao.buscarEntrada(this.factura.getIdFactura(), this.ordenCompra.getIdOrdenCompra()));
            if(this.entrada.getIdEntrada()==0) {
                this.mbOrdenCompra.setOrdenElegida(this.ordenCompra);
                this.mbOrdenCompra.obtenerDetalleOrdenCompra();
                
                EntradaProducto prod;
                this.entradaDetalle=new ArrayList<EntradaProducto>();
                for(OrdenCompraDetalle d: this.mbOrdenCompra.getListaOrdenDetalle()) {
                    prod=new EntradaProducto();
                    prod.setCantOrdenada(d.getCantOrdenada());
                    prod.setCantRecibida(d.getCantOrdenada());
                    prod.setPrecio(d.getCostoOrdenado());
                    prod.setDesctoProducto1(d.getDescuentoProducto());
                    prod.setDesctoProducto2(d.getDescuentoProducto2());
                    prod.setDesctoConfidencial(d.getDesctoConfidencial());
                    unitario=d.getCostoOrdenado();
                    unitario*=(1-this.entrada.getDesctoComercial()/100.00);
                    unitario*=(1-this.entrada.getDesctoProntoPago()/100.00);
                    unitario*=(1-d.getDescuentoProducto()/100.00);
                    unitario*=(1-d.getDescuentoProducto2()/100.00);
                    unitario*=(1-d.getDesctoConfidencial()/100.00);
                    unitario=Math.round(unitario*100.00)/100.00;
                    prod.setUnitario(unitario);
                    prod.setEmpaque(this.daoEmpaques.obtenerEmpaque(d.getSku()));
                    this.entradaDetalle.add(prod);
                }
                this.entrada.setIdEntrada(this.dao.agregarEntrada(this.entrada, this.entradaDetalle, this.mbProveedores.getMiniProveedor().getIdImpuestoZona()));
            } //else {
                this.daoImps=new DAOImpuestosProducto();
                this.entradaDetalle=this.dao.obtenerDetalleEntrada(this.entrada.getIdEntrada());
                for(EntradaProducto p:this.entradaDetalle) {
                    p.setEmpaque(this.daoEmpaques.obtenerEmpaque(p.getEmpaque().getIdEmpaque()));
                    p.setImpuestos(this.daoImps.obtenerImpuestosProducto(this.entrada.getIdEntrada(), p.getEmpaque().getIdEmpaque()));
                }
            //}
            this.sinOrden=false;
            this.cambiaPrecios();
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
    
    public void grabarFactura() {
        if(this.mbFacturas.grabarFactura()) {
            this.factura=this.mbFacturas.getFactura();
        }
    }
    
    public void buscarFacturas() {
        boolean ok = false;
        boolean abrir = false;
        RequestContext context = RequestContext.getCurrentInstance();
        int idProveedor=this.mbProveedores.getMiniProveedor().getIdProveedor();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(idProveedor==0) {
            fMsg.setDetail("Se requiere seleccionar un proveedor");
        } else {
            if(this.factura.getSerie().isEmpty() && this.factura.getNumero().isEmpty()) {
                this.mbFacturas.obtenerFacturas(idProveedor);
                if(this.mbFacturas.getFacturas().isEmpty()) {
                    fMsg.setDetail("No se encontraron facturas del proveedor");
                } else {
                    ok=true;
                    abrir=true;
                }
            } else {
                Factura f=this.mbFacturas.obtenerFactura(idProveedor, this.factura.getSerie(), this.factura.getNumero());
                if(f==null) {
                    fMsg.setDetail("No se encontro la factura con el proveedor especificado");
                } else {
                    this.factura=f;
                    ok=true;
                }
            }
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okBuscarFactura", abrir);
    }
    
    public void cambiaPrecios() {
        this.entrada.setSubTotal(0.00);
        this.entrada.setDescuento(0.00);
        this.entrada.setImpuesto(0.00);
        this.entrada.setTotal(0.00);
        
        EntradaProducto ep=this.entradaProducto;
        for(EntradaProducto p: this.entradaDetalle) {
            this.entradaProducto=p;
            calculaProducto();
            sumaTotales();
        }
        for(EntradaProducto p: this.entradaDetalle) {
            if(p.equals(ep)) {
                this.entradaProducto=p;
                this.respaldaFila();
            }
        }
    }
    
    public double calculaImpuestos() {
        double impuestos=0.00;
        for(ImpuestosProducto i: this.entradaProducto.getImpuestos()) {
            if(i.isAplicable()) {
                if(i.getModo()==1) {
                    i.setImporte(this.entradaProducto.getUnitario()*i.getValor()/100.00);
                } else {
                    i.setImporte(this.entradaProducto.getEmpaque().getPiezas()*i.getValor());
                }
            } else {
                i.setImporte(0.00);
            }
            impuestos+=i.getImporte();
        }
        return impuestos;
    }
    /*
    public double calculaImpuestosAnterior() {
        return this.resEntradaProducto.getUnitario()*0.16;
    }
    * */
    
    private void sumaTotales() {
        double suma;
        suma=this.entradaProducto.getPrecio()*this.entradaProducto.getCantRecibida();
        this.entrada.setSubTotal(this.entrada.getSubTotal()+Math.round(suma*100.00)/100.00);
        suma=this.entradaProducto.getPrecio()-this.entradaProducto.getUnitario();
        suma=suma*this.entradaProducto.getCantRecibida();
        this.entrada.setDescuento(this.entrada.getDescuento()+Math.round(suma*100.00)/100.00);
        suma=this.entradaProducto.getNeto()-this.entradaProducto.getUnitario();
        suma=suma*this.entradaProducto.getCantRecibida();
        this.entrada.setImpuesto(this.entrada.getImpuesto()+Math.round(suma*100.00)/100.00);
        suma=this.entradaProducto.getNeto()*this.entradaProducto.getCantRecibida();
        this.entrada.setTotal(this.entrada.getTotal()+Math.round(suma*100.00)/100.00);
    }
    
    private void restaTotales() {
        double resta;
        resta=this.resEntradaProducto.getPrecio()*this.resEntradaProducto.getCantRecibida();
        this.entrada.setSubTotal(this.entrada.getSubTotal()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getPrecio()-this.resEntradaProducto.getUnitario();
        resta=resta*this.resEntradaProducto.getCantRecibida();
        this.entrada.setDescuento(this.entrada.getDescuento()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getNeto()-this.resEntradaProducto.getUnitario();
        resta=resta*this.entradaProducto.getCantRecibida();
        this.entrada.setImpuesto(this.entrada.getImpuesto()-Math.round(resta*100.00)/100.00);
        resta=this.resEntradaProducto.getNeto()*this.resEntradaProducto.getCantRecibida();
        this.entrada.setTotal(this.entrada.getTotal()-Math.round(resta*100.00)/100.00);
    }
    
    public void cambiaPrecio() {
        restaTotales();
        calculaImpuestos();
        calculaProducto();
        sumaTotales();
    }
    
    public void cambiaCantRecibida() {
        restaTotales();
        calculaProducto();
        sumaTotales();
    }
    
    public void respaldaFila() {
        this.resEntradaProducto.setCantOrdenada(this.entradaProducto.getCantOrdenada());
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
        unitario=Math.round(unitario*100.00)/100.00;
        this.entradaProducto.setUnitario(unitario);
        double neto=Math.round((this.entradaProducto.getUnitario()+calculaImpuestos())*100.00)/100.00;
        this.entradaProducto.setNeto(neto);
        double subTotal=Math.round(this.entradaProducto.getUnitario()*this.entradaProducto.getCantRecibida()*100.00)/100.00;
        this.entradaProducto.setImporte(subTotal);
        this.respaldaFila();
    }
    
    public void actualizaProductoSeleccionado() {
        boolean nuevo=true;
        EntradaProducto producto=new EntradaProducto();
        producto.setEmpaque(this.mbBuscar.getProducto());
        for(EntradaProducto p:this.entradaDetalle) {
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
    }
    
    public void cargaOrdenes() {
        
    }
    
    public void entradas() {
//        boolean ok = false;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if(this.mbFacturas.cerrada()) {
//            fMsg.setDetail("La factura ya se encuentra cerrada, no puede ser modificada");
//        } else {
//            try {
//                this.dao=new DAOEntradas();
                this.entrada=new Entrada();
//                this.entrada=this.dao.modificarEntrada(this.almacen.getIdAlmacen(), this.mbFacturas.getFactura().getIdFactura(), this.ordenCompra.getIdOrdenCompra());
                this.entrada.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
                this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
                this.entrada.setIdAlmacen(this.almacen.getIdAlmacen());
                this.entrada.setDesctoComercial(0);
                this.entrada.setDesctoProntoPago(0);
                this.entradaDetalle=new ArrayList<EntradaProducto>();
                this.ordenCompra=new OrdenCompraEncabezado();
                this.sinOrden=false;
                this.modoEdicion=true;
//                ok=true;
//            } catch (SQLException ex) {
//                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//            } catch (NamingException ex) {
//                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//                fMsg.setDetail(ex.getMessage());
//            }
//        }
//        if (!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
    }
    
    public void mttoFacturas() {
        if(this.factura.getIdFactura()==0) {
            this.mbFacturas.setFactura(new Factura(this.mbProveedores.getMiniProveedor().getIdProveedor()));
         } else {
            this.mbFacturas.copia(this.factura);
        }
//        this.entrada=new Entrada();
//        this.entrada.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
//        this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
//        this.ordenCompra=new OrdenCompraEncabezado();
//        this.entradaDetalle=new ArrayList<EntradaProducto>();
    }
    
    // Este metodo se ejecutaba al seleccionar un alacen de la lista
    public String inicializarEntrada() {
        this.modoEdicion=true;
        this.entrada=new Entrada();
        this.ordenCompra=new OrdenCompraEncabezado();
        this.entrada.setIdProveedor(this.mbProveedores.getMiniProveedor().getIdProveedor());
        this.entrada.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
        this.entradaDetalle=new ArrayList<EntradaProducto>();
        return "entradas.xhtml";
    }
    
    public String salir() {
        this.modoEdicion=false;
        this.ordenCompra=new OrdenCompraEncabezado();
        return "entradas.xhtml";
    }
    
    public String terminar() {
        this.modoEdicion = false;
        this.almacen=null;
        this.acciones = null;
        this.mbCedis.setCedis(new MiniCedis());
        this.mbCedis.setListaMiniCedis(null);
        this.mbEmpresas.setEmpresa(new MiniEmpresa());
        this.mbEmpresas.setListaEmpresas(null);
        return "index.xhtml";
    }
    
    public void cargaListaAlmacenes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if (this.mbCedis.getCedis().getIdCedis() != 0 && this.mbEmpresas.getEmpresa().getIdEmpresa() != 0) {
                this.daoAlmacenes = new DAOAlmacenes();
                this.almacenes = this.daoAlmacenes.obtenerAlmacenes(this.mbCedis.getCedis().getIdCedis(), this.mbEmpresas.getEmpresa().getIdEmpresa());
            } else {
                this.almacenes=new ArrayList<Almacen>();
            }
            this.listaAlmacenes=new ArrayList<SelectItem>();
            Almacen xAlm=new Almacen();
            xAlm.setAlmacen("Seleccione un Almacén");
            SelectItem a0=new SelectItem(xAlm, xAlm.toString());
            this.listaAlmacenes.add(a0);
            
            for(Almacen a: this.almacenes) {
                this.listaAlmacenes.add(new SelectItem(a, a.toString()));
            }
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
    
    public void cargaAlmacenes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if (this.mbCedis.getCedis().getIdCedis() != 0 && this.mbEmpresas.getEmpresa().getIdEmpresa() != 0) {
                this.daoAlmacenes = new DAOAlmacenes();
                this.almacenes = this.daoAlmacenes.obtenerAlmacenes(this.mbCedis.getCedis().getIdCedis(), this.mbEmpresas.getEmpresa().getIdEmpresa());
            } else {
                this.almacenes=new ArrayList<Almacen>();
            }
            this.listaAlmacenes=null;
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

    public ArrayList<Almacen> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(ArrayList<Almacen> almacenes) {
        this.almacenes = almacenes;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(15);
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

    public MbMiniCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbMiniCedis mbCedis) {
        this.mbCedis = mbCedis;
    }

    public MbMiniEmpresas getMbEmpresas() {
        return mbEmpresas;
    }

    public void setMbEmpresas(MbMiniEmpresas mbEmpresas) {
        this.mbEmpresas = mbEmpresas;
    }

    public OrdenCompraEncabezado getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompraEncabezado ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public MbMiniProveedor getMbProveedores() {
        return mbProveedores;
    }

    public void setMbProveedores(MbMiniProveedor mbProveedores) {
        this.mbProveedores = mbProveedores;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public EntradaProducto getEntradaProducto() {
        return entradaProducto;
    }

    public void setEntradaProducto(EntradaProducto entradaProducto) {
        this.entradaProducto = entradaProducto;
    }

    public ArrayList<EntradaProducto> getEntradaDetalle() {
        return entradaDetalle;
    }

    public void setEntradaDetalle(ArrayList<EntradaProducto> entradaDetalle) {
        this.entradaDetalle = entradaDetalle;
    }

    public MbBuscarEmpaques getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbBuscarEmpaques mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public EntradaProducto getResEntradaProducto() {
        return resEntradaProducto;
    }

    public void setResEntradaProducto(EntradaProducto resEntradaProducto) {
        this.resEntradaProducto = resEntradaProducto;
    }

    public MbFacturas getMbFacturas() {
        return mbFacturas;
    }

    public void setMbFacturas(MbFacturas mbFacturas) {
        this.mbFacturas = mbFacturas;
    }

    public ArrayList<SelectItem> getListaAlmacenes() {
        if(this.listaAlmacenes==null) {
            this.cargaListaAlmacenes();
        }
        return listaAlmacenes;
    }

    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
        this.listaAlmacenes = listaAlmacenes;
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
}
