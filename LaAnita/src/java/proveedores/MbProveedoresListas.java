package proveedores;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import productos.dominio.Marca;
import productos.dominio.UnidadEmpaque;
import proveedores.dao.DAOProveedoresProductos;
import proveedores.dominio.ProveedorProducto;
import unidadesMedida.UnidadMedida;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jsolis
 */
@Named(value = "mbListas")
@SessionScoped
public class MbProveedoresListas implements Serializable {
    private boolean modoEdicion;
    private ArrayList<ProveedorProducto> listaProductos;
    private ArrayList<ProveedorProducto> listaFiltrados;
    private ProveedorProducto producto;
    private DAOProveedoresProductos dao;
    
    @ManagedProperty(value="#{mbMiniProveedor}")
    private MbMiniProveedor mbProveedor;
    
    private ArrayList<Accion> acciones;
    @ManagedProperty(value="#{mbAcciones}")
    private MbAcciones mbAcciones;
    
    public MbProveedoresListas() {
        this.modoEdicion=false;
        this.producto=new ProveedorProducto();
        this.mbProveedor=new MbMiniProveedor();
        this.mbAcciones=new MbAcciones();
    }
    
    public void mantenimiento(int idProducto) {
        if(this.producto.getIdProducto()==0) {
            this.producto=new ProveedorProducto();
        } else {
            this.producto=this.copia();
        }
    }
    
    private ProveedorProducto copia() {
        ProveedorProducto p=new ProveedorProducto();
        p.setIdProducto(this.producto.getIdProducto());
        p.setProducto(this.producto.getProducto());
        Marca m=new Marca();
        m.setIdMarca(this.producto.getMarca().getIdMarca());
        m.setMarca(this.producto.getMarca().getMarca());
        m.setProduccion(this.producto.getMarca().isProduccion());
        p.setMarca(m);
        p.setPiezas(this.producto.getPiezas());
        p.setContenido(this.producto.getContenido());
        p.setSku(this.producto.getSku());
        UnidadEmpaque unidadEmpaque=new UnidadEmpaque();
        unidadEmpaque.setIdUnidad(this.producto.getUnidadEmpaque().getIdUnidad());
        unidadEmpaque.setUnidad(this.producto.getUnidadEmpaque().getUnidad());
        unidadEmpaque.setAbreviatura(this.producto.getUnidadEmpaque().getAbreviatura());
        p.setUnidadEmpaque(unidadEmpaque);
        UnidadMedida unidadMedida=new UnidadMedida(this.producto.getUnidadMedida().getIdUnidadMedida(), this.producto.getUnidadMedida().getUnidadMedida(), this.producto.getUnidadMedida().getAbreviatura(), this.producto.getUnidadMedida().getIdTipo());
        p.setUnidadMedida(unidadMedida);
        UnidadMedida unidadMedida2=new UnidadMedida(this.producto.getUnidadMedida2().getIdUnidadMedida(), this.producto.getUnidadMedida2().getUnidadMedida(), this.producto.getUnidadMedida2().getAbreviatura(), this.producto.getUnidadMedida2().getIdTipo());
        p.setUnidadMedida2(unidadMedida2);
        return p;
    }
    
    public void terminar() {
        this.acciones=null;
    }
    
    public void cargaProductos() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOProveedoresProductos();
            this.listaProductos=this.dao.obtenerProductos(this.mbProveedor.getMiniProveedor().getIdProveedor());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public ProveedorProducto getProducto() {
        return producto;
    }

    public void setProducto(ProveedorProducto producto) {
        this.producto = producto;
    }

    public MbMiniProveedor getMbProveedor() {
        return mbProveedor;
    }

    public void setMbProveedor(MbMiniProveedor mbProveedor) {
        this.mbProveedor = mbProveedor;
    }

    public ArrayList<Accion> getAcciones() {
        if(this.acciones==null) {
            this.acciones=this.mbAcciones.obtenerAcciones(12);
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

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<ProveedorProducto> getListaProductos() {
        if(this.listaProductos==null) {
            this.cargaProductos();
        }
        return listaProductos;
    }

    public void setListaProductos(ArrayList<ProveedorProducto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public ArrayList<ProveedorProducto> getListaFiltrados() {
        return listaFiltrados;
    }

    public void setListaFiltrados(ArrayList<ProveedorProducto> listaFiltrados) {
        this.listaFiltrados = listaFiltrados;
    }
}
