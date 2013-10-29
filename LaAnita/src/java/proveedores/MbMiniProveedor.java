package proveedores;

import cotizaciones.dao.DAOCotizaciones;
import dominios.Moneda;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import proveedores.dao.DAOMiniProveedores;
import proveedores.dominio.MiniProveedor;

@Named(value = "mbMiniProveedor")
@SessionScoped
public class MbMiniProveedor implements Serializable {

    private ArrayList<SelectItem> listaMiniProveedores = new ArrayList<SelectItem>();
    private MiniProveedor miniProveedor = new MiniProveedor();
    private ArrayList<SelectItem> listaMonedas = new ArrayList<SelectItem>();
    private Moneda moneda = new Moneda();

    public MbMiniProveedor() {
    }

    //////////////////////////////M E T O D O S
    public ArrayList<SelectItem> obtenerListaMiniProveedor() throws NamingException {
        try {
            this.listaMiniProveedores=new ArrayList<SelectItem>();
            
            MiniProveedor p0 = new MiniProveedor();
            p0.setIdProveedor(0);
            p0.setProveedor("Proveedor....");
            listaMiniProveedores.add(new SelectItem(p0, p0.toString()));
            
            DAOMiniProveedores daoP = new DAOMiniProveedores();
            ArrayList<MiniProveedor> proveedores = daoP.obtenerProveedores();
            for (MiniProveedor e : proveedores) {
                listaMiniProveedores.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMiniProveedor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMiniProveedores;
    }

    private ArrayList<SelectItem> obtenerListaMonedas() throws NamingException {
        Moneda m0 = new Moneda();
        try {
            m0.setIdMoneda(0);
            m0.setMoneda("Moneda: ");
            listaMonedas.add(new SelectItem(m0, m0.toString()));
            DAOCotizaciones daoC = new DAOCotizaciones();
            ArrayList<Moneda> monedas = daoC.obtenerMonedas();
            for (Moneda e : monedas) {
                listaMonedas.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMiniProveedor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listaMonedas;
    }

    ///////////////////////////////// GETS Y SETS
    public ArrayList<SelectItem> getListaMiniProveedores() throws SQLException, NamingException {

        listaMiniProveedores = this.obtenerListaMiniProveedor();
        return listaMiniProveedores;
    }

    public void setListaMiniProveedores(ArrayList<SelectItem> listaMiniProveedores) {
        this.listaMiniProveedores = listaMiniProveedores;
    }

    public MiniProveedor getMiniProveedor() {
        return miniProveedor;
    }

    public void setMiniProveedor(MiniProveedor miniProveedor) {
        this.miniProveedor = miniProveedor;
    }

    public ArrayList<SelectItem> getListaMonedas() throws NamingException {
      
        listaMonedas = new ArrayList<SelectItem>();
        
        listaMonedas = this.obtenerListaMonedas();

        return listaMonedas;
    }

    public void setListaMonedas(ArrayList<SelectItem> listaMonedas) {
        this.listaMonedas = listaMonedas;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
}