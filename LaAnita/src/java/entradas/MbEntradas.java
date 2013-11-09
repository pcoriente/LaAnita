package entradas;

import almacenes.dao.DAOAlmacenes;
import almacenes.dominio.Almacen;
import cedis.MbMiniCedis;
import cedis.dominio.MiniCedis;
import empresas.MbMiniEmpresas;
import empresas.dominio.MiniEmpresa;
import entradas.dominio.Factura;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import ordenesDeCompra.dominio.OrdenCompraEncabezado;
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
    
    private DAOAlmacenes daoAlmacenes;
    private ArrayList<Almacen> almacenes;
    private Almacen almacen;
    
    private OrdenCompraEncabezado ordenCompra;
    private Factura factura;
    
    public MbEntradas() throws NamingException {
        this.modoEdicion = false;
        
        this.mbAcciones = new MbAcciones();
        this.mbCedis = new MbMiniCedis();
        this.mbEmpresas = new MbMiniEmpresas();
    }
    
    public String inicializarEntrada() {
        this.modoEdicion=true;
        return "entradas.xhtml";
    }
    
    public String salir() {
        this.modoEdicion=false;
        return "entradas.xhtml";
    }
    
    public String terminar() {
        this.modoEdicion = false;
        this.almacenes=null;
        this.acciones = null;
        this.mbCedis.setCedis(new MiniCedis());
        this.mbCedis.setListaMiniCedis(null);
        this.mbEmpresas.setEmpresa(new MiniEmpresa());
        this.mbEmpresas.setListaEmpresas(null);
        return "index.xhtml";
    }
    
    public void cargaAlmacenes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if (this.mbCedis.getCedis().getIdCedis() != 0 && this.mbEmpresas.getEmpresa().getIdEmpresa() != 0) {
                this.daoAlmacenes = new DAOAlmacenes();
                this.almacenes = this.daoAlmacenes.obtenerAlmacenes(this.mbCedis.getCedis().getIdCedis(), this.mbEmpresas.getEmpresa().getIdEmpresa());
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
}
