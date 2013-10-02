/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proveedores;

import cotizaciones.dao.DAOCotizaciones;
import dominios.Moneda;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import proveedores.dao.DAOMiniProveedores;
import proveedores.dominio.MiniProveedor;

@Named(value = "mbMiniProveedor")
@SessionScoped
public class MbMiniProveedor implements Serializable {

    private ArrayList<SelectItem> listaMiniProveedores;
    private MiniProveedor miniProveedor;
    private ArrayList<SelectItem> listaMonedas;
    private Moneda moneda;

    public MbMiniProveedor() {
        this.miniProveedor=new MiniProveedor();
        this.moneda=new Moneda();
    }

    public void obtenerListaMiniProveedor() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
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
            ok=true;
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

    private void obtenerListaMonedas() throws NamingException{
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        try {
            this.listaMonedas=new ArrayList<SelectItem>();

            Moneda m0 = new Moneda();
            m0.setIdMoneda(0);
            m0.setMoneda("Moneda: ");
            listaMonedas.add(new SelectItem(m0, m0.toString()));
            
            DAOCotizaciones daoC = new DAOCotizaciones();
            ArrayList<Moneda> monedas = daoC.obtenerMonedas();
            for (Moneda e : monedas) {
                listaMonedas.add(new SelectItem(e, e.toString()));
            }
            ok=true;
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

    public ArrayList<SelectItem> getListaMiniProveedores() {
        if(this.listaMiniProveedores==null) {
            this.obtenerListaMiniProveedor();
        }
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
        if(this.listaMonedas==null) {
            this.obtenerListaMonedas();
        }
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
