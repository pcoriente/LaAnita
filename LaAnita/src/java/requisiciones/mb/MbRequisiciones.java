package requisiciones.mb;

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
import productos.FrmProducto;
import productos.MbBuscarProd;
import productos.dao.DAOProductos;
import productos.dominio.ProdStr;
import productos.dominio.Producto;
import requisiciones.dao.DAORequisiciones;
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
    private MbDepto mbDepto;
    @ManagedProperty(value = "#{mbUsuarios}")
    private MbUsuarios mbUsuarios;
    private ArrayList<SelectItem> listaSubUsuarios = new ArrayList<SelectItem>();
    ArrayList<RequisicionEncabezado> listaRequisicionesEncabezado;
    private RequisicionEncabezado requisicionEncabezado;
    private ArrayList<RequisicionEncabezado> requisicionesFiltradas;
    private ArrayList<RequisicionProducto> requisicionProductos = new ArrayList<RequisicionProducto>();
    @ManagedProperty(value = "#{mbBuscarProd}")
    private MbBuscarProd mbBuscarProd;
    private Producto producto;
    private ProdStr prodStr;

    public MbRequisiciones() throws NamingException {
        this.mbMiniEmpresa = new MbMiniEmpresa();
        this.mbDepto = new MbDepto();
        this.mbUsuarios = new MbUsuarios();
        this.mbBuscarProd = new MbBuscarProd();

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
        ArrayList<Producto> pr;
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

       // pr = this.getRequisicionProductos();

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
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "Captura la contidad en el producto: " + p.getProducto().getIdProducto());
                    control = true;
                    break;
                }
            }
            if (control == false) {
                daoReq.guardarRequisicion(idEmpresa, idDepto, idUsuario, this.requisicionProductos);
                this.limpiaRequisicion();
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "Exitosamente");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);

    }

    private void cargaRequisiciones() throws NamingException, SQLException {
        listaRequisicionesEncabezado = new ArrayList<RequisicionEncabezado>();
        DAORequisiciones daoLista = new DAORequisiciones();
        ArrayList<TORequisicionEncabezado> toLista = daoLista.dameRequisicion();
        for (TORequisicionEncabezado e : toLista) {
            listaRequisicionesEncabezado.add(convertir(e));
        }

    }

    private RequisicionEncabezado convertir(TORequisicionEncabezado to) {
        RequisicionEncabezado re = new RequisicionEncabezado();
        re.setIdRequisicion(to.getIdRequisicion());
        re.setFecha(utilerias.Utilerias.date2String(to.getFecha()));
        re.setNombreComercial(to.getNombreComercial());
        re.setDepto(to.getDepto());
        re.setUsuario(to.getUsuario());
        return re;
    }

    private void cargaRequisicionesDetalle(int id) throws NamingException, SQLException {
       requisicionProductos = new ArrayList<RequisicionProducto>();
        DAORequisiciones daoListaDetalle = new DAORequisiciones();
        ArrayList<TORequisicionProducto> toLista = daoListaDetalle.dameRequisicionDetalle(id);
        DAOProductos daoPro =new DAOProductos();
        for (TORequisicionProducto e : toLista) {
            requisicionProductos.add(convertir(e,daoPro));
        }
    }

    private RequisicionProducto convertir(TORequisicionProducto to, DAOProductos daoPro) throws SQLException, NamingException {
        RequisicionProducto rd = new RequisicionProducto(daoPro.obtenerProducto(to.getIdProducto()));
        rd.setCantidad(to.getCantidad());
        return rd;
    }

    public String salir() throws NamingException {
        this.limpiaRequisicion();
        String navega = "menuRequisiciones.xhtml";
        return navega;
    }

    public void limpiaRequisicion() throws NamingException {
        this.inicializaRequisicion();
        this.listaRequisicionesEncabezado = null;
        this.requisicionesFiltradas = null;
        mbMiniEmpresa = new MbMiniEmpresa();
        mbDepto = new MbDepto();
        mbUsuarios = new MbUsuarios();
    }

    public void onRowSelect(int idRequi) throws NamingException, SQLException {
//        try {
//            RequisicionEncabezado re = (RequisicionEncabezado) event.getObject();
//            this.cargaRequisicionesDetalle(re.getIdRequisicion());
//        } catch (NamingException ex) {
//            Logger.getLogger(FrmProducto.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SQLException ex) {
//            Logger.getLogger(FrmProducto.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        this.cargaRequisicionesDetalle(idRequi);
        
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

    public void inicializaRequisicion() {
        this.requisicionProductos =new ArrayList<RequisicionProducto>();
    }

    public void buscar() {
        this.mbBuscarProd.buscarLista();
        if (this.mbBuscarProd.getProducto() != null) {
            this.setProducto(this.mbBuscarProd.getProducto());
        }
    }
}
