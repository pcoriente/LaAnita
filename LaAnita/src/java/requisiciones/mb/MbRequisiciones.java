package requisiciones.mb;

import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
import empresas.MbMiniEmpresa;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import monedas.MbMonedas;
import org.primefaces.event.RowEditEvent;
import productos.FrmProducto;
import productos.MbBuscarEmpaques;
import productos.dominio.Empaque;
import proveedores.MbMiniProveedor;
import requisiciones.dao.DAORequisiciones;
import requisiciones.dominio.RequisicionDetalle;
import requisiciones.dominio.RequisicionEncabezado;
import usuarios.dominio.Usuario;

@Named(value = "mbRequisiciones")
@SessionScoped
public class MbRequisiciones implements Serializable {

    @ManagedProperty(value = "#{mbMiniEmpresa}")
    private MbMiniEmpresa mbMiniEmpresa;
    @ManagedProperty(value = "#{mbDepto}")
    private MbDepto mbDepto = new MbDepto();
    @ManagedProperty(value = "#{mbUsuarios}")
    private MbUsuarios mbUsuarios;
    private ArrayList<SelectItem> listaSubUsuarios = new ArrayList<SelectItem>();
    private ArrayList<RequisicionEncabezado> listaRequisicionesEncabezado;
    private RequisicionEncabezado requisicionEncabezado;
    private ArrayList<RequisicionEncabezado> requisicionesFiltradas;
    //CAMBIANDO DE PRODUCTOS A EMPAQUES
    @ManagedProperty(value = "#{mbBuscarEmpaques}")
    private MbBuscarEmpaques mbBuscar;
    private ArrayList<Empaque> listaEmpaque = new ArrayList<Empaque>();
    private Empaque empaque;
    private RequisicionDetalle requisicionDetalle;
    private ArrayList<RequisicionDetalle> requisicionDetalles;
    private RequisicionDetalle empaqueElegido = new RequisicionDetalle();
    //COTIZACION
    private ArrayList<SelectItem> listaMini = new ArrayList<SelectItem>();
    private ArrayList<CotizacionDetalle> cotizacionDetalles;
    private CotizacionDetalle cotizacionDetalle = new CotizacionDetalle();
    private int numCotizacion = 0;
    private double subtotalGeneral;
    private double sumaDescuentosProductos;
    private double impuesto;
    private double total;
    private double iva = 0.16;
    private String subtotF;
    private String descF;
    private String impF;
    private String totalF;
    private double descuentoGeneralAplicado;
    private String descGralAplicF;
    private double sumaDescuentoTotales;
    private String desctoTotalesF;
    @ManagedProperty(value = "#{mbMiniProveedor}")
    private MbMiniProveedor mbMiniProveedor = new MbMiniProveedor();
    @ManagedProperty(value = "#{mbMonedas}")
    private MbMonedas mbMonedas = new MbMonedas();
    private ArrayList<CotizacionEncabezado> cotizacionesEncabezado;
    private CotizacionEncabezado cotizacionEncabezado = new CotizacionEncabezado();
    private double subtotalBruto;
    private String subtotalBrutoF;
    //  DAORequisiciones daoReq;
    private String navega;

    //CONSTRUCTOR
    public MbRequisiciones() throws NamingException {
        this.mbMiniEmpresa = new MbMiniEmpresa();
        this.mbDepto = new MbDepto();
        this.mbUsuarios = new MbUsuarios();
        this.mbMiniProveedor = new MbMiniProveedor();
        this.mbMonedas = new MbMonedas();
        this.mbBuscar = new MbBuscarEmpaques();


    }

    //GET Y SETS REQUISICIONES
    public MbDepto getMbDepto() {
        return mbDepto;
    }

    public void setMbDepto(MbDepto mbDepto) {
        this.mbDepto = mbDepto;
    }

    public MbMiniEmpresa getMbMiniEmpresa() {
        return mbMiniEmpresa;
    }

    public void setMbMiniEmpresa(MbMiniEmpresa mbMiniEmpresa) {
        this.mbMiniEmpresa = mbMiniEmpresa;
    }

    public MbUsuarios getMbUsuarios() {
        return mbUsuarios;
    }

    public void setMbUsuarios(MbUsuarios mbUsuarios) {
        this.mbUsuarios = mbUsuarios;
    }

    public ArrayList<SelectItem> getListaSubUsuarios() {
        return listaSubUsuarios;
    }

    public void setListaSubUsuarios(ArrayList<SelectItem> listaSubUsuarios) {
        this.listaSubUsuarios = listaSubUsuarios;
    }

    public ArrayList<RequisicionEncabezado> getListaRequisicionesEncabezado() throws NamingException {
        try {
            if (listaRequisicionesEncabezado == null) {
                cargaRequisiciones();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaRequisicionesEncabezado;
    }

    public void setListaRequisicionesEncabezado(ArrayList<RequisicionEncabezado> listaRequisicionesEncabezado) {
        this.listaRequisicionesEncabezado = listaRequisicionesEncabezado;
    }

    public RequisicionEncabezado getRequisicionEncabezado() {
        return requisicionEncabezado;
    }

    public void setRequisicionEncabezado(RequisicionEncabezado requisicionEncabezado) {
        this.requisicionEncabezado = requisicionEncabezado;
    }

    public String salirMenuRequisiciones() throws NamingException {
        this.limpiaRequisicion();
        String salir = "index.xhtml";
        return salir;
    }

    public ArrayList<RequisicionEncabezado> getRequisicionesFiltradas() {
        return requisicionesFiltradas;
    }

    public void setRequisicionesFiltradas(ArrayList<RequisicionEncabezado> requisicionesFiltradas) {
        this.requisicionesFiltradas = requisicionesFiltradas;
    }

    public ArrayList<SelectItem> getListaMini() throws SQLException {
        listaMini = this.mbMiniEmpresa.obtenerListaMiniEmpresas();
        return listaMini;
    }

    public void setListaMini(ArrayList<SelectItem> listaMini) {
        this.listaMini = listaMini;
    }

    public CotizacionDetalle getCotizacionDetalle() {
        return cotizacionDetalle;
    }

    public void setCotizacionDetalle(CotizacionDetalle cotizacionDetalle) {
        this.cotizacionDetalle = cotizacionDetalle;
    }

    public int getNumCotizacion() {
        return numCotizacion;
    }

    public void setNumCotizacion(int numCotizacion) {
        this.numCotizacion = numCotizacion;
    }

    public ArrayList<CotizacionDetalle> getCotizacionDetalles() {
        return cotizacionDetalles;
    }

    public void setCotizacionDetalles(ArrayList<CotizacionDetalle> cotizacionDetalles) {
        this.cotizacionDetalles = cotizacionDetalles;
    }

    public double getSubtotalGeneral() {
        this.calculoSubtotalGeneral();
        return subtotalGeneral;
    }

    public void setSubtotalGeneral(double subtotalGeneral) {
        this.subtotalGeneral = subtotalGeneral;
    }

    public double getSumaDescuentosProductos() {
        this.calculoDescuentoProducto();
        return sumaDescuentosProductos;
    }

    public void setSumaDescuentosProductos(double sumaDescuentosProductos) {
        this.sumaDescuentosProductos = sumaDescuentosProductos;
    }

    public double getImpuesto() {
        this.calculoIVA();
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        this.calculoTotal();
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getSubtotF() {
        subtotF = utilerias.Utilerias.formatoMonedas(this.getSubtotalGeneral());
        return subtotF;
    }

    public String getDescF() {
        descF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentosProductos());
        return descF;
    }

    public String getImpF() { //IVA
        impF = utilerias.Utilerias.formatoMonedas(this.getImpuesto());
        return impF;
    }

    public String getTotalF() {
        totalF = utilerias.Utilerias.formatoMonedas(this.getTotal());
        return totalF;
    }

    public double getDescuentoGeneralAplicado() {
        this.calculoDescuentoGeneral();
        return descuentoGeneralAplicado;
    }

    public void setDescuentoGeneralAplicado(double descuentoGeneralAplicado) {
        this.descuentoGeneralAplicado = descuentoGeneralAplicado;
    }

    public String getDescGralAplicF() {
        descGralAplicF = utilerias.Utilerias.formatoMonedas(this.getDescuentoGeneralAplicado());
        return descGralAplicF;
    }

    public void setDescGralAplicF(String descGralAplicF) {
        this.descGralAplicF = descGralAplicF;
    }

    public double getSumaDescuentoTotales() {
        this.calculoDescuentoTotales();
        return sumaDescuentoTotales;
    }

    public void setSumaDescuentoTotales(double sumaDescuentoTotales) {
        this.sumaDescuentoTotales = sumaDescuentoTotales;
    }

    public String getDesctoTotalesF() {
        desctoTotalesF = utilerias.Utilerias.formatoMonedas(this.getSumaDescuentoTotales());
        return desctoTotalesF;
    }

    public void setDesctoTotalesF(String desctoTotalesF) {

        this.desctoTotalesF = desctoTotalesF;
    }

    public MbMiniProveedor getMbMiniProveedor() {
        return mbMiniProveedor;
    }

    public void setMbMiniProveedor(MbMiniProveedor mbMiniProveedor) {
        this.mbMiniProveedor = mbMiniProveedor;
    }

    public ArrayList<CotizacionEncabezado> getCotizacionesEncabezado() {
        return cotizacionesEncabezado;
    }

    public void setCotizacionesEncabezado(ArrayList<CotizacionEncabezado> cotizacionesEncabezado) {
        this.cotizacionesEncabezado = cotizacionesEncabezado;
    }

    public CotizacionEncabezado getCotizacionEncabezado() {
        return cotizacionEncabezado;
    }

    public void setCotizacionEncabezado(CotizacionEncabezado cotizacionEncabezado) {
        this.cotizacionEncabezado = cotizacionEncabezado;
    }

    public double getSubtotalBruto() {
        this.calcularSubtotalBruto();
        return subtotalBruto;
    }

    public void setSubtotalBruto(double subtotalBruto) {
        this.subtotalBruto = subtotalBruto;
    }

    public MbMonedas getMbMonedas() {
        return mbMonedas;
    }

    public void setMbMonedas(MbMonedas mbMonedas) {
        this.mbMonedas = mbMonedas;
    }

    public String getSubtotalBrutoF() {
        subtotalBrutoF = utilerias.Utilerias.formatoMonedas(this.getSubtotalBruto());
        return subtotalBrutoF;
    }

    public void setSubtotalBrutoF(String subtotalBrutoF) {

        this.subtotalBrutoF = subtotalBrutoF;
    }

    //EMPAQUES
    public MbBuscarEmpaques getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbBuscarEmpaques mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public ArrayList<Empaque> getListaEmpaque() {
        return listaEmpaque;
    }

    public void setListaEmpaque(ArrayList<Empaque> listaEmpaque) {
        this.listaEmpaque = listaEmpaque;
    }

    public Empaque getEmpaque() {
        return empaque;
    }

    public void setEmpaque(Empaque empaque) {
        this.empaque = empaque;
    }

    public RequisicionDetalle getRequisicionDetalle() {
        return requisicionDetalle;
    }

    public void setRequisicionDetalle(RequisicionDetalle requisicionDetalle) {
        this.requisicionDetalle = requisicionDetalle;
    }

    public ArrayList<RequisicionDetalle> getRequisicionDetalles() {
        return requisicionDetalles;
    }

    public void setRequisicionDetalles(ArrayList<RequisicionDetalle> requisicionDetalles) {
        this.requisicionDetalles = requisicionDetalles;
    }

    public RequisicionDetalle getEmpaqueElegido() {
        return empaqueElegido;
    }

    public void setEmpaqueElegido(RequisicionDetalle empaqueElegido) {
        this.empaqueElegido = empaqueElegido;
    }

//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------
    //METODOS REQUISICIONES
    public void cargaSubUsuarios() throws SQLException {
        this.listaSubUsuarios = new ArrayList<SelectItem>();
        Usuario usu = new Usuario(0, "Seleccione un usuario: ");
        this.listaSubUsuarios.add(new SelectItem(usu, usu.toString()));
        for (Usuario us : this.mbUsuarios.obtenerSubUsuarios(this.mbDepto.getDeptos().getIdDepto())) {
            listaSubUsuarios.add(new SelectItem(us, us.toString()));
        }
    }

    public void guardaRequisicion() throws NamingException, SQLException {
        int idEmpresa;
        int idDepto;
        int idUsuario;
        FacesMessage fMsg = null;
        DAORequisiciones daoReq = new DAORequisiciones();
        try {
            idEmpresa = this.mbMiniEmpresa.getEmpresa().getIdEmpresa();
        } catch (Exception e) {
            idEmpresa = 0;
        }
        idDepto = this.mbDepto.getDeptos().getIdDepto();
        try {
            idUsuario = this.mbUsuarios.getUsuario().getId();
        } catch (Exception e) {
            idUsuario = 0;
        }

        if (idEmpresa == 0) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Seleccione una Empresa.");
        } else if (idDepto == 0) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Seleccione un depto.");
        } else if (idUsuario == 0) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Seleccione un Usuario.");
        } else if (this.requisicionDetalles.isEmpty()) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Especificar detalle");
        } else {
            boolean control = false;
            for (RequisicionDetalle p : this.requisicionDetalles) {
                if (p.getCantidad() <= 0) {
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso: Para el producto " + p.getEmpaque().toString(), " Capture una cantidad numérica y positiva. ");
                    control = true;
                    break;
                }
            }
            if (control == false) {
                daoReq.guardarRequisicion(idEmpresa, idDepto, idUsuario, this.requisicionDetalles);
                this.limpiaRequisicion();
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La requisición se ha generado...");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void cargaRequisiciones() throws NamingException, SQLException {
        listaRequisicionesEncabezado = new ArrayList<RequisicionEncabezado>();
        DAORequisiciones daoReq = new DAORequisiciones();
        ArrayList<RequisicionEncabezado> toLista = daoReq.dameRequisicion();
        for (RequisicionEncabezado e : toLista) {
            listaRequisicionesEncabezado.add(e);
        }

    }

    private void cargaRequisicionesDetalle(int id) throws NamingException, SQLException {
        requisicionDetalles = new ArrayList<RequisicionDetalle>();
        DAORequisiciones daoReq = new DAORequisiciones();
        ArrayList<RequisicionDetalle> detallado = daoReq.dameRequisicionDetalle(id);
        for (RequisicionDetalle rd : detallado) {
            requisicionDetalles.add(rd);
        }
    }

    public void cargaRequisicionesDetalleAprobar(int id) throws NamingException, SQLException {
        requisicionDetalles = new ArrayList<RequisicionDetalle>();
        DAORequisiciones daoReq = new DAORequisiciones();
        ArrayList<RequisicionDetalle> rsDetalle = daoReq.dameRequisicionDetalleAprobar(id);
        for (RequisicionDetalle rd : rsDetalle) {
            requisicionDetalles.add(rd);
        }
    }

    public String salir() throws NamingException {

        this.limpiaRequisicion();
        navega = "menuRequisiciones.xhtml";
        return navega;
    }

    public String salirMenu(int opcion) throws NamingException {
        this.limpiaRequisicion();
        this.listaRequisicionesEncabezado = null;
        if (opcion == 0) {
            navega = "index.xhtml";
        } else if (opcion == 1) {
            navega = "menuRequisiciones.xhtml";
        } else if (opcion == 2) {
            navega = "menuCotizaciones.xhtml";
        }
        return navega;
    }

    public String nuevo() throws NamingException {
        this.limpiaRequisicion();
        navega = "requisiciones.xhtml";
        return navega;
    }

    public void limpiaRequisicion() throws NamingException {
        navega = "";
        this.requisicionDetalle = new RequisicionDetalle();
        requisicionDetalles = new ArrayList<RequisicionDetalle>();
        this.listaRequisicionesEncabezado = null;
        this.requisicionesFiltradas = null;
        this.mbMiniEmpresa = new MbMiniEmpresa();
        this.mbDepto = new MbDepto();
        this.mbUsuarios = new MbUsuarios();

    }

    public void requisicionMas(int idRequi) throws NamingException, SQLException {
        this.requisicionDetalle = null;
        this.cargaRequisicionesDetalle(idRequi);
    }

    public void eliminarProducto(int idEmpaque) {
        for (RequisicionDetalle d : requisicionDetalles) {
            if (d.getEmpaque().getIdEmpaque() == idEmpaque) {
                requisicionDetalles.remove(d);
                break;
            }
        }
    }

    public void aprobarRequisicion(int idReq, int estado) throws SQLException, NamingException {
        DAORequisiciones daoReq = new DAORequisiciones();
        FacesMessage msg = null;
        try {
            int longitud = requisicionDetalles.size();
            for (int y = 0; y < longitud; y++) {
                int ca = requisicionDetalles.get(y).getCantidadAutorizada();
                if (ca < 0) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "No sé realizó la operación de aprobación");
                    break;
                } else if (estado == 2) {
                    daoReq.actualizaRequisicion(idReq, estado);
                    this.requisicionEncabezado.setStatus(estado);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La aprobación se ha realizado..");
                    this.cargaRequisiciones();
                    this.cargaRequisicionesDetalle(idReq);
                } else if (estado == 0) {
                    daoReq.actualizaRequisicion(idReq, estado);
                    this.requisicionEncabezado.setStatus(estado);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La requisición ha sido RECHAZADA..");
                    this.cargaRequisiciones();
                    this.cargaRequisicionesDetalle(idReq);
                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Error en la aprobación, verifique su información...");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminaProductoAprobar(int idReq, int idProd) throws NamingException, SQLException {
        DAORequisiciones daoReq = new DAORequisiciones();
        int longitud = requisicionDetalles.size();
        for (int y = 0; y < longitud; y++) {
            int idProducto = requisicionDetalles.get(y).getEmpaque().getIdEmpaque();
            if (idProducto == idProd) {
                requisicionDetalles.remove(y);
                daoReq.eliminaProductoAprobar(idReq, idProd);
                break;
            }
        }
    }

    public void modificaProductoAprobar(int idReq, int idProd) throws NamingException, SQLException {
        DAORequisiciones daoReq = new DAORequisiciones();
        int longitud = requisicionDetalles.size();
        for (int y = 0; y < longitud; y++) {
            int idProducto = requisicionDetalles.get(y).getEmpaque().getIdEmpaque();
            int idRequi = requisicionDetalles.get(y).getIdRequisicion();
            if (idProducto == idProd || idRequi == idReq) {
                int cantidad = requisicionDetalles.get(y).getCantidadAutorizada();
                daoReq.modificaProductoAprobar(idReq, idProd, cantidad);
                break;
            }
        }
    }

    public void onEdit(RowEditEvent event) throws NamingException, SQLException {
        DAORequisiciones daoReq = new DAORequisiciones();
        RequisicionEncabezado encab = new RequisicionEncabezado();
        RequisicionDetalle deta = (RequisicionDetalle) event.getObject();
        FacesMessage msg = null;
        int idReq = deta.getIdRequisicion();
        int idProd = deta.getEmpaque().getIdEmpaque();
        int cantidad = deta.getCantidadAutorizada();
        if (cantidad > 0) {
            daoReq.modificaProductoAprobar(idReq, idProd, cantidad);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Modificación exitosa");
        } else if (cantidad < 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Capture cantidades positivas");
            deta.getCantidadAutorizada();
        } else if (cantidad == 0) {
            daoReq.modificaProductoAprobar(idReq, idProd, cantidad);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "El producto ha sido eliminado");
        }
    }

    public void modificarRequisicionStatus(int idReq, int status, int cant) throws SQLException, NamingException {
        DAORequisiciones daoReq = new DAORequisiciones();
        FacesMessage msg;
        try {
            daoReq.modificarAprobacion(idReq, status, cant);
            this.cargaRequisiciones();
            this.requisicionDetalle = null;
            this.cargaRequisicionesDetalleAprobar(idReq);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Modificación de status exitosa");
        } catch (NamingException ex) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se realizó la modificación de status..");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    //COTIZACIONES
    public void guardaCotizacion(int idReq, double dc, double dpp) throws SQLException, NamingException {
        DAORequisiciones daoReq = new DAORequisiciones();
        FacesMessage msg;
        try {
            int idProv = this.mbMiniProveedor.getMiniProveedor().getIdProveedor();
            int idMon = this.mbMonedas.getMoneda().getIdMoneda();
            if (idProv != 0 && idMon != 0) {
                if (this.total != 0) {
                    //         numCotizacion += 1;
                    this.cotizacionEncabezado.setIdRequisicion(idReq);
                    this.cotizacionEncabezado.setIdProveedor(idProv);
                    this.cotizacionEncabezado.setDescuentoCotizacion(dc);
                    this.cotizacionEncabezado.setDescuentoProntoPago(dpp);
                    this.cotizacionEncabezado.setIdMoneda(idMon);
                    //  this.cotizacionEncabezado.setNumCotizaciones(numCotizacion);
                    daoReq.grabarCotizacion(this.cotizacionEncabezado, this.cotizacionDetalles);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La cotización ha sido registrada..");
                    //     this.setNumCotizacion(numCotizacion);
                    this.limpiaCotizacion();
                    mbMiniProveedor.getMiniProveedor().setIdProveedor(0);
                    mbMonedas.getMoneda().setIdMoneda(0);

                    int coti = daoReq.numCotizaciones(idReq);
                    this.setNumCotizacion(coti);
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Capture al menos la cotización para un empaque..");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Falta información para realizar la cotización");
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Error en la aprobación, verifique su información...");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void limpiaCotizacion() throws NamingException {
        //ACTUALIZACION DE CODIGO
        for (CotizacionDetalle d : cotizacionDetalles) {
            d.setCantidadCotizada(0);
            d.setCostoCotizado(0);
            d.setDescuentoProducto(0);
            d.setDescuentoProducto2(0);
            d.setNeto(0);
            d.setSubtotal(0);
        }
        this.subtotalGeneral = 0;
        this.sumaDescuentosProductos = 0;
        this.descuentoGeneralAplicado = 0;
        this.sumaDescuentoTotales = 0;
        this.impuesto = 0;
        this.total = 0;
        this.mbMiniProveedor = new MbMiniProveedor();
    }

    ///nueva propuesta
    public void cargaRequisicionesDetalleCotizar(int id, int modi) throws NamingException, SQLException {
        DAORequisiciones daoReq = new DAORequisiciones();
        this.setNumCotizacion(0);
        this.subtotalGeneral = 0;
        this.sumaDescuentosProductos = 0;
        this.descuentoGeneralAplicado = 0;
        this.sumaDescuentoTotales = 0;
        this.impuesto = 0;
        this.total = 0;
        mbMiniProveedor = new MbMiniProveedor();
        cotizacionDetalles = new ArrayList<CotizacionDetalle>();
        ArrayList<CotizacionDetalle> lc = daoReq.dameRequisicionDetalleCotizar(id);
        for (CotizacionDetalle rd : lc) {
            cotizacionDetalles.add(rd);
        }
        //  cotizacionEncabezado= new CotizacionEncabezado(); ;
        int coti = daoReq.numCotizaciones(id);
        this.setNumCotizacion(coti);


    }

    public void calculoSubtotalGeneral() {
        subtotalGeneral = 0;
        for (CotizacionDetalle e : cotizacionDetalles) {
            subtotalGeneral = subtotalGeneral + e.getSubtotal();
        }
    }

    public void calculoIVA() {
        impuesto = 0;
        double desc = this.subtotalBruto;                           //subtotalGeneral - descuentoGeneralAplicado;
        if (desc > 0) {
            impuesto = (desc) * this.iva;
        } else {
            impuesto = 0;
        }
    }

    public void calculoTotal() {
        total = 0;
        double desc = this.subtotalBruto;                                           //subtotalGeneral - descuentoGeneralAplicado;
        if (desc > 0) {
            total = (desc) + this.getImpuesto();
        } else {
            total = 0;
        }
    }

    public void calculoDescuentoGeneral() {
        descuentoGeneralAplicado = 0;
        double sumaCostoCotizado = 0;
        double descuentoC;
        double descuentoPP;
        double descuentoGA;
        for (CotizacionDetalle e : cotizacionDetalles) {
            sumaCostoCotizado += (e.getCantidadCotizada() * e.getNeto());
        }
        descuentoC = sumaCostoCotizado * (this.mbMiniProveedor.getMiniProveedor().getDesctoComercial() / 100);
        sumaCostoCotizado = sumaCostoCotizado - descuentoC;
        descuentoPP = sumaCostoCotizado * (this.mbMiniProveedor.getMiniProveedor().getDesctoProntoPago() / 100);
        descuentoGA = descuentoC + descuentoPP;
        this.setDescuentoGeneralAplicado(descuentoGA);
    }

    public void calculaPrecioDescuento(int idEmp) {
        subtotalGeneral = 0;
        try {
            for (CotizacionDetalle e : cotizacionDetalles) {
                if (e.getEmpaque().getIdEmpaque() == idEmp) {
                    double neto = e.getCostoCotizado() - (e.getCostoCotizado() * (e.getDescuentoProducto() / 100));

                    double neto2 = neto - neto * (e.getDescuentoProducto2() / 100);
                    e.setNeto(neto2);
                    e.setSubtotal(e.getNeto() * e.getCantidadCotizada());
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void calculoDescuentoProducto() {
        sumaDescuentosProductos = 0;
        for (CotizacionDetalle e : cotizacionDetalles) {
            sumaDescuentosProductos += (e.getCantidadCotizada() * (e.getCostoCotizado() - e.getNeto()));
        }
    }

    public void calculoDescuentoTotales() {
        sumaDescuentoTotales = 0;
        sumaDescuentoTotales = this.getSumaDescuentosProductos() + (this.getDescuentoGeneralAplicado());
    }

    public void limpiaDetalle() throws NamingException {
        //ACTUALIZACION CODIGO
        for (CotizacionDetalle d : cotizacionDetalles) {
            d.setCostoCotizado(0);
            d.setDescuentoProducto(0);
            d.setNeto(0);
            d.setSubtotal(0);
        }
        this.subtotalGeneral = 0;
        this.sumaDescuentosProductos = 0;
        this.descuentoGeneralAplicado = 0;
        this.sumaDescuentoTotales = 0;
        this.impuesto = 0;
        this.total = 0;
    }

    public void calcularSubtotalBruto() {
        subtotalBruto = this.subtotalGeneral - this.descuentoGeneralAplicado;
    }

    public void irMenuCotizaciones() {
    }
    // EMPAQUES

    public void buscar() {
        this.mbBuscar.buscarLista();
        if (this.mbBuscar.getProducto() != null) {
            this.actualizaProductoSeleccionado();
        }
    }

    public void actualizaProductosSeleccionados() {
        for (Empaque e : this.mbBuscar.getSeleccionados()) {
            this.mbBuscar.setProducto(e);
            this.actualizaProductoSeleccionado();
        }
    }

    public void actualizaProductoSeleccionado() {
          FacesMessage msg = null;
        boolean nuevo = true;
        RequisicionDetalle rd = new RequisicionDetalle();
        int idEmp = this.mbBuscar.getProducto().getIdEmpaque();
        rd.setEmpaque(this.mbBuscar.getProducto());
        for (RequisicionDetalle p : this.requisicionDetalles) {
            if (p.getEmpaque().getIdEmpaque() == idEmp) {
                System.out.println("somos iguales");
                nuevo = false;
                 msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Ya existe el producto..");
                break;
            }
        }
        if (nuevo) {
            this.requisicionDetalles.add(rd);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void cerrarCotizacion(int idReq) throws SQLException {
        FacesMessage msg = null;
        try {
            if (numCotizacion == 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "No ha realizado ninguna cotización, por lo que no puede ser CERRADA..");
            } else {
                DAORequisiciones daoReq = new DAORequisiciones();
                daoReq.cerrarCotizacion(idReq);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La cotización ha sido CERRADA..");
                //    this.setNumCotizacion(numCotizacion);
                this.limpiaCotizacion();
                mbMiniProveedor.getMiniProveedor().setIdProveedor(0);
                mbMonedas.getMoneda().setIdMoneda(0);
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Error en la aprobación, verifique su información...");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
