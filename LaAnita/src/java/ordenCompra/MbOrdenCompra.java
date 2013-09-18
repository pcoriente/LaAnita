package ordenCompra;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedProperty;
import ordenCompra.dominio.OrdenCompra;
import ordenCompra.dominio.ProductoOC;
import proveedores.MbProveedores;

/**
 *
 * @author jsolis
 */
@Named(value = "mbOrdenCompra")
@SessionScoped
public class MbOrdenCompra implements Serializable {
    @ManagedProperty(value = "#{mbFacturas}")
    private MbFacturas mbFacturas;
    @ManagedProperty(value = "#{mbProveedores}")
    private MbProveedores mbProveedores;
    private OrdenCompra ordenCompra;
    private ArrayList<ProductoOC> productos;
    
    public MbOrdenCompra() {
        this.mbFacturas=new MbFacturas();
        this.mbProveedores=new MbProveedores();
        this.ordenCompra=new OrdenCompra();
        this.productos=new ArrayList<ProductoOC>();
    }

    public MbFacturas getMbFacturas() {
        return mbFacturas;
    }

    public void setMbFacturas(MbFacturas mbFacturas) {
        this.mbFacturas = mbFacturas;
    }

    public MbProveedores getMbProveedores() {
        return mbProveedores;
    }

    public void setMbProveedores(MbProveedores mbProveedores) {
        this.mbProveedores = mbProveedores;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public ArrayList<ProductoOC> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<ProductoOC> productos) {
        this.productos = productos;
    }
}
