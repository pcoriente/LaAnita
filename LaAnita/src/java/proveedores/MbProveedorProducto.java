package proveedores;

import impuestos.FrmImpuestos;
import impuestos.MbGrupos;
import impuestos.dominio.ImpuestoGrupo;
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
import org.primefaces.context.RequestContext;
import productos.MbMarca;
import productos.MbProducto;
import productos.MbUnidadEmpaque;
import productos.MbPresentacion;
import productos.dao.DAOEmpaques;
import productos.dao.DAOMarcas;
import productos.dao.DAOPresentaciones;
import productos.dao.DAOUnidadesEmpaque;
import productos.dominio.Marca;
import productos.dominio.Presentacion;
import productos.dominio.UnidadEmpaque;
import proveedores.dao.DAOProveedoresProductos;
import proveedores.dominio.ProveedorProducto;
import unidadesMedida.DAOUnidadesMedida;
import unidadesMedida.MbUnidadMedida;
import unidadesMedida.UnidadMedida;
import utilerias.Utilerias;

/**
 *
 * @author jsolis
 */
@Named(value = "mbProveedorProducto")
@SessionScoped
public class MbProveedorProducto implements Serializable {

    private int idProveedor;
    private ProveedorProducto producto;
    private DAOProveedoresProductos dao;
    private ArrayList<SelectItem> listaMarcas;
    @ManagedProperty(value = "{mbMarca}")
    private MbMarca mbMarca;
    private ArrayList<SelectItem> listaEmpaques;
    @ManagedProperty(value = "#{mbUnidadEmpaque}")
    private MbUnidadEmpaque mbUnidadEmpaque;
    @ManagedProperty(value = "#{mbPresentacion}")
    private MbPresentacion mbPresentacion;
    private ArrayList<SelectItem> listaUnidadesMedida;
    @ManagedProperty(value = "#{mbUnidadMedida}")
    private MbUnidadMedida mbUnidadMedida;
    @ManagedProperty(value = "#{mbGrupos}")
    private MbGrupos mbImpuestoGrupo;

    public MbProveedorProducto(int idProveedor) {
        this.idProveedor = idProveedor;
        this.producto = new ProveedorProducto();
        this.mbMarca = new MbMarca();
        this.mbUnidadEmpaque = new MbUnidadEmpaque();
        this.mbPresentacion = new MbPresentacion();
        this.mbUnidadMedida = new MbUnidadMedida();
        this.mbImpuestoGrupo = new MbGrupos();
    }

    public void salir() {
    }

    public boolean grabar() {
        boolean resultado = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.producto.setContenido(Utilerias.Round(this.producto.getContenido(), 3));
        if (this.producto.getSku().isEmpty()) {
            fMsg.setDetail("Se requiere el código interno del producto para este proveedor");
        } else if (this.producto.getDiasEntrega() <= 0) {
            fMsg.setDetail("Los dias de entrega debe ser un número mayor o igual a cero");
        } else if (this.producto.getUnidadEmpaque().getIdUnidad() == 0) {
            fMsg.setDetail("Se requiere la unidad de empaque del producto");
        } else if (this.producto.getPiezas() < 1) {
            fMsg.setDetail("Las piezas deben ser un número mayor o igual a 1");
        } else if (this.producto.getProducto().isEmpty()) {
            fMsg.setDetail("Se requiere la descripción del producto");
        } else if (this.producto.getPresentacion().getIdPresentacion() == 0) {
            fMsg.setDetail("Se requiere la presentación del producto");
        } else if (this.producto.getPresentacion().getIdPresentacion() > 1 && (this.producto.getContenido() <= 0 || this.producto.getContenido() >= 1000)) {
            this.producto.setContenido(0);
            fMsg.setDetail("El contenido debe ser un número de 0 a 1000");
        } else if (this.producto.getPresentacion().getIdPresentacion() > 1 && this.producto.getUnidadMedida().getIdUnidadMedida() == 0) {
            fMsg.setDetail("Se requiere la unidad de medida !!");
        } else if (this.producto.getImpuestoGrupo().getIdGrupo() == 0) {
            fMsg.setDetail("Se requiere un impuesto !!");
        } else {
            try {
                this.dao = new DAOProveedoresProductos();
                if (this.producto.getIdProducto() == 0) {
                    this.producto.setIdProducto(this.dao.agregar(this.producto, this.idProveedor));
                } else {
                    this.dao.modificar(this.producto, this.idProveedor);
                }
                resultado = true;
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        if (!resultado) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okProveedorProducto", resultado);
        return resultado;
    }

    public ArrayList<ProveedorProducto> obtenerProductos(int idProveedor) {
        boolean resultado=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        ArrayList<ProveedorProducto> productos=new ArrayList<ProveedorProducto>();
        try {
            int idEmpaque;
            this.dao = new DAOProveedoresProductos();
            productos = this.dao.obtenerProductos(idProveedor);
            DAOEmpaques daoEpq=new DAOEmpaques();
            for(ProveedorProducto pp: productos) {
                idEmpaque=pp.getEquivalencia().getIdEmpaque();
                if(idEmpaque!=0) {
                    pp.setEquivalencia(daoEpq.obtenerEmpaque(idEmpaque));
                }
            }
            resultado=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!resultado) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return productos;
    }

    public void eliminarPresentacion() {
        if (this.mbPresentacion.eliminar()) {
            this.producto.setPresentacion(new Presentacion());
            this.producto.setContenido(0);
            this.producto.setUnidadMedida(new UnidadMedida(0, "", ""));
            this.mbPresentacion.setListaPresentaciones(null);
        }
    }

    public void grabarPresentacion() {
        if (this.mbPresentacion.grabar()) {
            this.producto.setPresentacion(this.mbPresentacion.getPresentacion());
            this.mbPresentacion.setListaPresentaciones(null);
        }
    }

    public void mttoPresentacion() {
        if (this.producto.getPresentacion().getIdPresentacion() == 0) {
            this.mbPresentacion.setPresentacion(new Presentacion());
        } else {
            this.mbPresentacion.setPresentacion(this.mbPresentacion.copia(this.producto.getPresentacion()));
        }
    }

    public void eliminarMarca() {
        if (this.mbMarca.eliminar()) {
            this.producto.setMarca(this.mbMarca.getMarca());
            this.listaMarcas = null;
        }
    }

    public void grabarMarca() {
        if (this.mbMarca.grabar()) {
            this.producto.setMarca(this.mbMarca.getMarca());
            this.listaMarcas = null;
        }
    }

    public void mttoMarcas() {
        if (this.producto.getMarca().getIdMarca() == 0) {
            this.mbMarca.setMarca(new Marca(0, "", false));
        } else {
            this.mbMarca.copia(this.producto.getMarca());
        }
    }

    public void eliminarUnidadEmpaque() {
        if (this.mbUnidadEmpaque.eliminar()) {
            this.producto.setUnidadEmpaque(this.mbUnidadEmpaque.getUnidad());
            this.listaEmpaques = null;
        }
    }

    public void grabarUnidadEmpaque() {
        if (this.mbUnidadEmpaque.grabar()) {
            this.producto.setUnidadEmpaque(this.mbUnidadEmpaque.getUnidad());
            this.listaEmpaques = null;
        }
    }

    public void mttoUnidadesEmpaque() {
        if (this.producto.getUnidadEmpaque().getIdUnidad() == 0) {
            this.mbUnidadEmpaque.setUnidad(new UnidadEmpaque(0, "", ""));
        } else {
            this.mbUnidadEmpaque.copia(this.producto.getUnidadEmpaque());
        }
    }

    public void actualizarContenido() {
        if (this.producto.getPresentacion().getIdPresentacion() == 0) {
            this.producto.setContenido(0);
        } else if (this.producto.getPresentacion().getIdPresentacion() == 1) {
            this.producto.setContenido(0);
        } else if (this.producto.getContenido() == 0) {
            this.producto.setContenido(1);
        }
        this.producto.setUnidadMedida(new UnidadMedida(0, "", ""));
    }

    public void actualizarPiezas() {
        if (this.producto.getUnidadEmpaque().getIdUnidad() == 0) {
            this.producto.setPiezas(0);
        } else {
            this.producto.setPiezas(1);
        }
    }

    public void mantenimiento(ProveedorProducto producto) {
        this.copia(producto);
    }

    public void cargaMarcas() {
        boolean oki = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");

        this.listaMarcas = new ArrayList<SelectItem>();
        Marca mark = new Marca(0, "SELECCIONE UNA MARCA", true);
        this.listaMarcas.add(new SelectItem(mark, mark.toString()));
        try {
            DAOMarcas daoMarcas = new DAOMarcas();
            ArrayList<Marca> lstMarcas = daoMarcas.obtenerMarcas();
            for (Marca m : lstMarcas) {
                this.listaMarcas.add(new SelectItem(m, m.toString()));
            }
            oki = true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public void cargaUnidadesMedida() {
        this.listaUnidadesMedida = new ArrayList<SelectItem>();
        UnidadMedida unid = new UnidadMedida(0, "SELECCIONE", "");
        this.listaUnidadesMedida.add(new SelectItem(unid, unid.toString()));
        try {
            DAOUnidadesMedida daoUnidades = new DAOUnidadesMedida();
            ArrayList<UnidadMedida> lstUnidades = daoUnidades.obtenerUnidades();
            for (UnidadMedida u : lstUnidades) {
                this.listaUnidadesMedida.add(new SelectItem(u, u.toString()));
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
     public void cargaPresentaciones() {
     this.listaPresentaciones = new ArrayList<SelectItem>();
     Presentacion unid = new Presentacion(0, "SELECCIONE", "");
     this.listaPresentaciones.add(new SelectItem(unid, unid.toString()));
     try {
     DAOPresentaciones daoUnidades = new DAOPresentaciones();
     ArrayList<Presentacion> lstPresentaciones = daoUnidades.obtenerUnidades();
     for (Presentacion u : lstPresentaciones) {
     this.listaPresentaciones.add(new SelectItem(u, u.toString()));
     }
     } catch (NamingException ex) {
     Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
     } catch (SQLException ex) {
     Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     * */

    public void cargaUnidadesEmpaque() {
        boolean oki = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");

        this.listaEmpaques = new ArrayList<SelectItem>();
        UnidadEmpaque unidad = new UnidadEmpaque(0, "SELECCIONE UN EMPAQUE", "");
        this.listaEmpaques.add(new SelectItem(unidad, unidad.toString()));

        try {
            DAOUnidadesEmpaque daoUnidad = new DAOUnidadesEmpaque();
            ArrayList<UnidadEmpaque> lstUnidades = daoUnidad.obtenerUnidadesEmpaque();
            for (UnidadEmpaque u : lstUnidades) {
                listaEmpaques.add(new SelectItem(u, u.toString()));
            }
            oki = true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    private void copia(ProveedorProducto producto) {
        this.producto = new ProveedorProducto();
        this.producto.setIdProducto(producto.getIdProducto());
        this.producto.setProducto(producto.getProducto());
        this.producto.setDiasEntrega(producto.getDiasEntrega());
        Marca m = new Marca();
        m.setIdMarca(producto.getMarca().getIdMarca());
        m.setMarca(producto.getMarca().getMarca());
        m.setProduccion(producto.getMarca().isProduccion());
        this.producto.setMarca(m);
        this.producto.setPiezas(producto.getPiezas());
        this.producto.setContenido(producto.getContenido());
        this.producto.setSku(producto.getSku());
        UnidadEmpaque unidadEmpaque = new UnidadEmpaque();
        unidadEmpaque.setIdUnidad(producto.getUnidadEmpaque().getIdUnidad());
        unidadEmpaque.setUnidad(producto.getUnidadEmpaque().getUnidad());
        unidadEmpaque.setAbreviatura(producto.getUnidadEmpaque().getAbreviatura());
        this.producto.setUnidadEmpaque(unidadEmpaque);
        UnidadMedida unidadMedida = new UnidadMedida(producto.getUnidadMedida().getIdUnidadMedida(), producto.getUnidadMedida().getUnidadMedida(), producto.getUnidadMedida().getAbreviatura());
        this.producto.setUnidadMedida(unidadMedida);
        UnidadMedida unidadMedida2 = new UnidadMedida(producto.getUnidadMedida2().getIdUnidadMedida(), producto.getUnidadMedida2().getUnidadMedida(), producto.getUnidadMedida2().getAbreviatura());
        this.producto.setUnidadMedida2(unidadMedida2);
        this.producto.setPresentacion(new Presentacion(producto.getPresentacion().getIdPresentacion(), producto.getPresentacion().getPresentacion(), producto.getPresentacion().getAbreviatura()));
        this.producto.setImpuestoGrupo(new ImpuestoGrupo(producto.getImpuestoGrupo().getIdGrupo(), producto.getImpuestoGrupo().getGrupo()));
        this.producto.setEquivalencia(producto.getEquivalencia());
    }

    public ProveedorProducto getProducto() {
        return producto;
    }

    public void setProducto(ProveedorProducto producto) {
        this.producto = producto;
    }

    public ArrayList<SelectItem> getListaMarcas() {
        if (this.listaMarcas == null) {
            this.cargaMarcas();
        }
        return listaMarcas;
    }

    public void setListaMarcas(ArrayList<SelectItem> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }

    public MbMarca getMbMarca() {
        return mbMarca;
    }

    public void setMbMarca(MbMarca mbMarca) {
        this.mbMarca = mbMarca;
    }
    /*
     public ArrayList<SelectItem> getListaPresentaciones() {
     if(this.listaPresentaciones==null) {
     this.cargaPresentaciones();
     }
     return listaPresentaciones;
     }

     public void setListaPresentaciones(ArrayList<SelectItem> listaPresentaciones) {
     this.listaPresentaciones = listaPresentaciones;
     }
     * */

    public MbPresentacion getMbPresentacion() {
        return mbPresentacion;
    }

    public void setMbPresentacion(MbPresentacion mbPresentacion) {
        this.mbPresentacion = mbPresentacion;
    }

    public ArrayList<SelectItem> getListaUnidadesMedida() {
        if (this.listaUnidadesMedida == null) {
            this.cargaUnidadesMedida();
        }
        return listaUnidadesMedida;
    }

    public void setListaUnidadesMedida(ArrayList<SelectItem> listaUnidadesMedida) {
        this.listaUnidadesMedida = listaUnidadesMedida;
    }

    public MbUnidadMedida getMbUnidadMedida() {
        return mbUnidadMedida;
    }

    public void setMbUnidadMedida(MbUnidadMedida mbUnidadMedida) {
        this.mbUnidadMedida = mbUnidadMedida;
    }

    public ArrayList<SelectItem> getListaEmpaques() {
        if (this.listaEmpaques == null) {
            this.cargaUnidadesEmpaque();
        }
        return listaEmpaques;
    }

    public void setListaEmpaques(ArrayList<SelectItem> listaEmpaques) {
        this.listaEmpaques = listaEmpaques;
    }

    public MbUnidadEmpaque getMbUnidadEmpaque() {
        return mbUnidadEmpaque;
    }

    public void setMbUnidadEmpaque(MbUnidadEmpaque mbUnidadEmpaque) {
        this.mbUnidadEmpaque = mbUnidadEmpaque;
    }

    public MbGrupos getMbImpuestoGrupo() {
        return mbImpuestoGrupo;
    }

    public void setMbImpuestoGrupo(MbGrupos mbImpuestoGrupo) {
        this.mbImpuestoGrupo = mbImpuestoGrupo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }
}
