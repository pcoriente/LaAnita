package requisiciones.mb;

import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
import empresas.MbMiniEmpresa;
import empresas.dao.DAOMiniEmpresas;
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
import productos.MbBuscarProd;
import productos.dao.DAOProductos;
import productos.dominio.ProdStr;
import productos.dominio.Producto;
import proveedores.MbMiniProveedor;
import requisiciones.dao.DAODepto;
import requisiciones.dao.DAORequisiciones;
import requisiciones.dao.DAOUsuarioRequisiciones;
import requisiciones.dominio.RequisicionEncabezado;
import requisiciones.dominio.RequisicionProducto;
import requisiciones.to.TORequisicionEncabezado;
import requisiciones.to.TORequisicionProducto;
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
    private RequisicionProducto requisicionProducto;
    private ArrayList<RequisicionProducto> requisicionProductos = new ArrayList<RequisicionProducto>();
    @ManagedProperty(value = "#{mbBuscarProd}")
    private MbBuscarProd mbBuscarProd;
    @ManagedProperty(value = "#{mbBuscarEmpaques}")
    private MbBuscarEmpaques mbBuscarEmpaques;
    private Producto producto;
    private ProdStr prodStr;
    private ArrayList<SelectItem> listaMini = new ArrayList<SelectItem>();
    //COTIZACION
    private ArrayList<CotizacionDetalle> cotizacionProductos;
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

    //GETS Y SETS
    public MbRequisiciones() throws NamingException {
        this.mbMiniEmpresa = new MbMiniEmpresa();
        this.mbDepto = new MbDepto();
        this.mbUsuarios = new MbUsuarios();
        this.mbBuscarProd = new MbBuscarProd();
        this.mbMiniProveedor = new MbMiniProveedor();
        this.mbBuscarEmpaques = new MbBuscarEmpaques();
        this.mbMonedas = new MbMonedas();

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

    public ArrayList<RequisicionProducto> getRequisicionProductos() {
        return requisicionProductos;
    }

    public void setRequisicionProductos(ArrayList<RequisicionProducto> requisicionProductos) {
        this.requisicionProductos = requisicionProductos;
    }

    public MbBuscarProd getMbBuscarProd() {
        return mbBuscarProd;
    }

    public void setMbBuscarProd(MbBuscarProd mbBuscarProd) {
        this.mbBuscarProd = mbBuscarProd;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ProdStr getProdStr() {
        return prodStr;
    }

    public void setProdStr(ProdStr prodStr) {
        this.prodStr = prodStr;
    }

    public RequisicionProducto getRequisicionProducto() {
        return requisicionProducto;
    }

    public void setRequisicionProducto(RequisicionProducto requisicionProducto) {
        this.requisicionProducto = requisicionProducto;

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

    public ArrayList<CotizacionDetalle> getCotizacionProductos() {
        return cotizacionProductos;
    }

    public void setCotizacionProductos(ArrayList<CotizacionDetalle> cotizacionProductos) {
        this.cotizacionProductos = cotizacionProductos;
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

    public MbBuscarEmpaques getMbBuscarEmpaques() {
        return mbBuscarEmpaques;
    }

    public void setMbBuscarEmpaques(MbBuscarEmpaques mbBuscarEmpaques) {
        this.mbBuscarEmpaques = mbBuscarEmpaques;
    }
    
    

//------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------
    //METODOS REQUISICIONES
    public void actualizaProductoSeleccionado() {
        RequisicionProducto rp = new RequisicionProducto(this.mbBuscarProd.obtenerSeleccionado());
        this.requisicionProductos.add(rp);
    }

    public void cargaSubUsuarios() throws SQLException {
        this.listaSubUsuarios = new ArrayList<SelectItem>();
        Usuario usu = new Usuario(0, "Seleccione un usuario: ");
        this.listaSubUsuarios.add(new SelectItem(usu, usu.toString()));
        for (Usuario us : this.mbUsuarios.obtenerSubUsuarios(this.mbDepto.getDeptos().getIdDepto())) {
            listaSubUsuarios.add(new SelectItem(us, us.toString()));
        }
    }

    public void agregarRequisicion() throws NamingException, SQLException {
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
        } else if (this.requisicionProductos.isEmpty()) {
            fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Especificar detalle");
        } else {
            boolean control = false;
            for (RequisicionProducto p : this.requisicionProductos) {
                if (p.getCantidad() <= 0) {
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso: Para el producto " + p.getProducto().toString(), " Capture una cantidad numérica y positiva. ");
                    control = true;
                    break;
                }
            }
            if (control == false) {
                daoReq.guardarRequisicion(idEmpresa, idDepto, idUsuario, this.requisicionProductos);
                this.limpiaRequisicion();
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La requisición se ha generado...");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);

    }

    public void cargaRequisiciones() throws NamingException, SQLException {
        listaRequisicionesEncabezado = new ArrayList<RequisicionEncabezado>();
        DAORequisiciones daoLista = new DAORequisiciones();
        ArrayList<TORequisicionEncabezado> toLista = daoLista.dameRequisicion();

        for (TORequisicionEncabezado e : toLista) {
            listaRequisicionesEncabezado.add(convertir(e));
        }
    }

    private void cargaRequisicionesDetalle(int id) throws NamingException, SQLException {
        requisicionProductos = new ArrayList<RequisicionProducto>();
        DAORequisiciones daoListaDetalle = new DAORequisiciones();
        ArrayList<TORequisicionProducto> toLista = daoListaDetalle.dameRequisicionDetalle(id);
        DAOProductos daoPro = new DAOProductos();
        for (TORequisicionProducto e : toLista) {
            requisicionProductos.add(convertir(e, daoPro));
        }
    }

    public void cargaRequisicionesDetalleAprobar(int id) throws NamingException, SQLException {
        requisicionProductos = new ArrayList<RequisicionProducto>();
        DAORequisiciones daoListaDetalle = new DAORequisiciones();
        ArrayList<TORequisicionProducto> toLista = daoListaDetalle.dameRequisicionDetalleAprobar(id);
        DAOProductos daoPro = new DAOProductos();
        for (TORequisicionProducto e : toLista) {
            requisicionProductos.add(convertir(e, daoPro));
        }
    }

    private RequisicionProducto convertir(TORequisicionProducto to, DAOProductos daoPro) throws SQLException, NamingException {
        RequisicionProducto rd = new RequisicionProducto(daoPro.obtenerProducto(to.getIdProducto()));
        rd.setIdRequisicion(to.getIdRequisicion());
        rd.setCantidad(to.getCantidad());
        rd.setCantidadAutorizada(to.getCantidadAutorizada());
        return rd;
    }

    private RequisicionEncabezado convertir(TORequisicionEncabezado to) throws SQLException, NamingException {
        DAOUsuarioRequisiciones daoU = new DAOUsuarioRequisiciones();
        DAODepto daoD = new DAODepto();
        DAOMiniEmpresas daoM = new DAOMiniEmpresas();
        RequisicionEncabezado re = new RequisicionEncabezado();
        re.setIdRequisicion(to.getIdRequisicion());
        re.setFechaRequisicion(utilerias.Utilerias.date2String(to.getFechaRequisicion()));

        re.setMiniEmpresa(daoM.obtenerMiniEmpresa(to.getIdEmpresa()));
        re.setDepto(daoD.obtenerDeptoConverter(to.getIdDepto()));
        re.setUsuario(daoU.obtenerUsuarioConverter(to.getIdSolicito()));
        int state = to.getStatus();
        re.setStatus(state);
        String estado;


        switch (state) {
            case 0:
                estado = "Rechazado";
                re.setEstado(estado);
                re.setEmpleadoAprobo(to.getEmpleadoAprobo());
                re.setFechaAprobacion(utilerias.Utilerias.date2String(to.getFechaAprobacion()));
                break;
            case 1:
                estado = "Solicitado";
                re.setEstado(estado);
                break;
            case 2:
                estado = "Aprobado";
                re.setEstado(estado);
                re.setEmpleadoAprobo(to.getEmpleadoAprobo());
                re.setFechaAprobacion(utilerias.Utilerias.date2String(to.getFechaAprobacion()));
                break;
            default:
                String noAprobado = "No Aprobado";

        }

        return re;

    }

    public String salir() throws NamingException {
        this.limpiaRequisicion();
        String navega = "menuRequisiciones.xhtml";
        return navega;
    }

    public String salirCotizacion() throws NamingException {
        this.limpiaRequisicion();
        
        String navega = "menuCotizaciones.xhtml";
        return navega;
    }

    public String nuevo() throws NamingException {
        this.limpiaRequisicion();
        String navega = "requisiciones.xhtml";
        return navega;
    }

    public void limpiaRequisicion() throws NamingException {
        this.requisicionProducto = new RequisicionProducto();
        this.requisicionProductos = new ArrayList<RequisicionProducto>();
        this.listaRequisicionesEncabezado = null;
        this.requisicionesFiltradas = null;
        this.mbMiniEmpresa = new MbMiniEmpresa();
        this.mbDepto = new MbDepto();
        this.mbUsuarios = new MbUsuarios();
        this.mbBuscarProd = new MbBuscarProd();
    }

    public void requisicionMas(int idRequi) throws NamingException, SQLException {
        this.requisicionProducto = null;
        this.cargaRequisicionesDetalle(idRequi);
    }

    public void requisicionAprobar(int idReq) throws NamingException, SQLException {
        this.requisicionProducto = null;
        this.cargaRequisicionesDetalleAprobar(idReq);
    }

    public void eliminarProducto(int idProd) {
        int longitud = requisicionProductos.size();
        for (int y = 0; y < longitud; y++) {
            int idProducto = requisicionProductos.get(y).getProducto().getIdProducto();
            if (idProducto == idProd) {
                requisicionProductos.remove(y);
                break;
            }
        }
    }

    public void buscar() {
        System.err.println(mbBuscarProd.getParte());
        this.mbBuscarProd.buscarLista();
    }

    public void aprobarRequisicion(int idReq, int estado) throws SQLException {
        DAORequisiciones daoReq;
        FacesMessage msg = null;
        try {
            int longitud = requisicionProductos.size();
            for (int y = 0; y < longitud; y++) {
                int ca = requisicionProductos.get(y).getCantidadAutorizada();
                if (ca < 0) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "No sé realizó la operación de aprobación");
                    break;
                } else if (estado == 2) {
                    daoReq = new DAORequisiciones();
                    daoReq.actualizaRequisicion(idReq, estado);
                    this.requisicionEncabezado.setStatus(estado);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La aprobación se ha realizado..");
                    this.cargaRequisiciones();
                    this.cargaRequisicionesDetalle(idReq);
                } else if (estado == 0) {
                    daoReq = new DAORequisiciones();
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
        int longitud = requisicionProductos.size();
        DAORequisiciones daoR = new DAORequisiciones();
        for (int y = 0; y < longitud; y++) {
            int idProducto = requisicionProductos.get(y).getProducto().getIdProducto();
            if (idProducto == idProd) {
                requisicionProductos.remove(y);
                daoR.eliminaProductoAprobar(idReq, idProd);
                break;
            }
        }
    }

    public void modificaProductoAprobar(int idReq, int idProd) throws NamingException, SQLException {
        int longitud = requisicionProductos.size();
        DAORequisiciones daoR = new DAORequisiciones();
        for (int y = 0; y < longitud; y++) {
            int idProducto = requisicionProductos.get(y).getProducto().getIdProducto();
            int idRequi = requisicionProductos.get(y).getIdRequisicion();
            if (idProducto == idProd || idRequi == idReq) {
                int cantidad = requisicionProductos.get(y).getCantidadAutorizada();
                daoR.modificaProductoAprobar(idReq, idProd, cantidad);
                break;
            }
        }
    }

    public void onEdit(RowEditEvent event) throws NamingException, SQLException {
        DAORequisiciones daoR = new DAORequisiciones();
        RequisicionEncabezado encab = new RequisicionEncabezado();
        RequisicionProducto produc = (RequisicionProducto) event.getObject();
        FacesMessage msg = null;
        int idReq = produc.getIdRequisicion();
        int idProd = produc.getProducto().getIdProducto();
        int cantidad = produc.getCantidadAutorizada();

        if (cantidad > 0) {
            daoR.modificaProductoAprobar(idReq, idProd, cantidad);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Modificación exitosa");
        } else if (cantidad < 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Capture cantidades positivas");
            produc.getCantidadAutorizada();
        } else if (cantidad == 0) {
            daoR.modificaProductoAprobar(idReq, idProd, cantidad);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "El producto ha sido eliminado");
        }

    }

    public void modificarRequisicionStatus(int idReq, int status, int cant) throws SQLException {
        DAORequisiciones daoR;
        FacesMessage msg;
        try {
            daoR = new DAORequisiciones();
            daoR.modificarAprobacion(idReq, status, cant);
            this.cargaRequisiciones();
            this.requisicionProducto = null;
            this.cargaRequisicionesDetalleAprobar(idReq);

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Modificación de status exitosa");
        } catch (NamingException ex) {
            Logger.getLogger(MbRequisiciones.class.getName()).log(Level.SEVERE, null, ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se realizó la modificación de status..");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    //COTIZACIONES
    public void guardaCotizacion(int idReq, double dc, double dpp) throws SQLException {
        DAORequisiciones daoReq;
        FacesMessage msg;
        try {

            int idProv = this.mbMiniProveedor.getMiniProveedor().getIdProveedor();
            int idMon = this.mbMonedas.getMoneda().getIdMoneda();

            daoReq = new DAORequisiciones();
            if (idProv != 0 && idMon != 0) {
                if (this.total != 0) {
                    this.cotizacionEncabezado.setIdRequisicion(idReq);
                    this.cotizacionEncabezado.setIdProveedor(idProv);
                    this.cotizacionEncabezado.setDescuentoCotizacion(dc);
                    this.cotizacionEncabezado.setDescuentoProntoPago(dpp);
                    this.cotizacionEncabezado.setIdMoneda(idMon);
                    daoReq.grabarCotizacion(this.cotizacionEncabezado, this.cotizacionProductos);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "La cotización ha sido registrada..");
                    numCotizacion += 1;
                    this.setNumCotizacion(numCotizacion);
                    this.limpiaCotizacion();
                    mbMiniProveedor.getMiniProveedor().setIdProveedor(0);
                    mbMonedas.getMoneda().setIdMoneda(0);
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Capture al menos la cotización para un producto..");
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
        for(CotizacionDetalle d: cotizacionProductos){
            d.setCantidadCotizada(0);
            d.setCostoCotizado(0);
            d.setDescuentoProducto(0);
            d.setDescuentoProducto2(0);
            d.setNeto(0);
            d.setSubtotal(0);
        
        }
        
//        int longitud = this.cotizacionProductos.size();
//        for (int x = 0; x < longitud; x++) {
//            cotizacionProductos.get(x).setCantidadCotizada(0);
//            cotizacionProductos.get(x).setCostoCotizado(0);
//            cotizacionProductos.get(x).setDescuentoProducto(0);
//            cotizacionProductos.get(x).setDescuentoProducto2(0);
//            cotizacionProductos.get(x).setNeto(0);
//            cotizacionProductos.get(x).setSubtotal(0);
//        }

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
            this.numCotizacion = 0;
            this.subtotalGeneral = 0;
            this.sumaDescuentosProductos = 0;
            this.descuentoGeneralAplicado = 0;
            this.sumaDescuentoTotales = 0;
            this.impuesto = 0;
            this.total = 0;
            mbMiniProveedor = new MbMiniProveedor();

        cotizacionProductos = new ArrayList<CotizacionDetalle>();
        DAORequisiciones daoListaDetalle = new DAORequisiciones();
        ArrayList<TORequisicionProducto> toLista = daoListaDetalle.dameRequisicionDetalleCotizar(id);

        DAOProductos daoPro = new DAOProductos();

        for (TORequisicionProducto e : toLista) {
            requisicionProductos.add(convertir(e, daoPro));
            cotizacionProductos.add(convertirCotizar(e, daoPro, modi));
        }
    }

    private CotizacionDetalle convertirCotizar(TORequisicionProducto to, DAOProductos daoPro, int modi) throws SQLException, NamingException {
        RequisicionProducto rd;
        rd = new RequisicionProducto(daoPro.obtenerProducto(to.getIdProducto()));
        CotizacionDetalle cd = new CotizacionDetalle();
        cd.setProducto(rd.getProducto());
        cd.setCantidadAutorizada(to.getCantidadAutorizada());
        cd.setCantidadCotizada(to.getCantidadAutorizada());

        cd.setCostoCotizado(0);
        cd.setNeto(0);
        cd.setSubtotal(0);
        cd.setDescuentoProducto(0);
        cd.setDescuentoProducto2(0);


        return cd;
    }

    public void calculoSubtotalGeneral() {
        subtotalGeneral = 0;
        for (CotizacionDetalle e : cotizacionProductos) {
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
        for (CotizacionDetalle e : cotizacionProductos) {
            sumaCostoCotizado += (e.getCantidadCotizada() * e.getNeto());
        }
        descuentoC = sumaCostoCotizado * (this.mbMiniProveedor.getMiniProveedor().getDesctoComercial() / 100);
        sumaCostoCotizado = sumaCostoCotizado - descuentoC;
        descuentoPP = sumaCostoCotizado * (this.mbMiniProveedor.getMiniProveedor().getDesctoProntoPago() / 100);
        descuentoGA = descuentoC + descuentoPP;
        this.setDescuentoGeneralAplicado(descuentoGA);
    }

    public void calculaPrecioDescuento(int idProd) {
        subtotalGeneral = 0;
        try {
            for (CotizacionDetalle e : cotizacionProductos) {
                if (e.getProducto().getIdProducto() == idProd) {
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
        for (CotizacionDetalle e : cotizacionProductos) {
            sumaDescuentosProductos += (e.getCantidadCotizada() * (e.getCostoCotizado() - e.getNeto()));
        }
    }

    public void calculoDescuentoTotales() {
        sumaDescuentoTotales = 0;
        sumaDescuentoTotales = this.getSumaDescuentosProductos() + (this.getDescuentoGeneralAplicado());
    }

    public void limpiaDetalle() throws NamingException {
        
        //ACTUALIZACION CODIGO
        for(CotizacionDetalle d: cotizacionProductos ){
            d.setCostoCotizado(0);
            d.setDescuentoProducto(0);
            d.setNeto(0);
            d.setSubtotal(0);
        }
        
//        int longitud = this.cotizacionProductos.size();
//        for (int x = 0; x < longitud; x++) {
//            cotizacionProductos.get(x).setCostoCotizado(0);
//            cotizacionProductos.get(x).setDescuentoProducto(0);
//            cotizacionProductos.get(x).setNeto(0);
//            cotizacionProductos.get(x).setSubtotal(0);
//        }

        this.subtotalGeneral = 0;

        this.sumaDescuentosProductos = 0;
        this.descuentoGeneralAplicado = 0;
        this.sumaDescuentoTotales = 0;

        this.impuesto = 0;

        this.total = 0;
//        ArrayList<SelectItem> listaMonedas = new ArrayList<SelectItem>();
//
//        this.mbMonedas.setListaMonedas(listaMonedas);
    }

    public void calcularSubtotalBruto() {
        subtotalBruto = this.subtotalGeneral - this.descuentoGeneralAplicado;

    }
    public void irMenuCotizaciones(){
    
        
    }

    public MbMonedas getMbMonedas() {
        return mbMonedas;
    }

    public void setMbMonedas(MbMonedas mbMonedas) {
        this.mbMonedas = mbMonedas;
    }
}
