package productosOld;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.NamingException;
import productosOld.dao.DAOBuscar;
import productosOld.dominio.ProductoOld;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbBuscarOld")
@SessionScoped
public class MbBuscarOld implements Serializable {
    private DAOBuscar dao;
    private String cod_emp;
    private ProductoOld productoOld;
    private String tipoBusqueda;
    private String cadena;
    private ArrayList<ProductoOld> listaEncontrados;
    
    public MbBuscarOld() {
        try {
            this.cadena="";
            this.tipoBusqueda="1";
            this.dao = new DAOBuscar();
        } catch (NamingException ex) {
            Logger.getLogger(MbBuscarOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }
    
    public void limpiaBuscar() {
        this.listaEncontrados=new ArrayList<ProductoOld>();
    }
    
    public ProductoOld obtenerProductoOld(String cod_emp, String cod_pro) throws SQLException {
        productoOld=this.dao.obtenerProducto(cod_emp, cod_pro);
        return productoOld;
    }
    
    public void buscar() throws SQLException {
        listaEncontrados=this.dao.obtenerProductos(this.tipoBusqueda, this.cadena);
    }
    
    public void buscar2() throws SQLException {
        listaEncontrados=this.dao.obtenerProductos(this.cod_emp);
    }

    public ProductoOld getProducto() {
        return productoOld;
    }

    public void setProducto(ProductoOld productoOld) {
        this.productoOld = productoOld;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    public ArrayList<ProductoOld> getListaEncontrados() {
        return listaEncontrados;
    }

    public void setListaEncontrados(ArrayList<ProductoOld> listaEncontrados) {
        this.listaEncontrados = listaEncontrados;
    }
}
