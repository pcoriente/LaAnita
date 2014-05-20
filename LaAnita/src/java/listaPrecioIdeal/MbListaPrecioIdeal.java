/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listaPrecioIdeal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import listaPrecioIdeal.DAO.DAOListaPrecio;
import listaPrecioIdeal.dominio.ListaPrecioIdeal;
import listaPrecioIdeal.to.TOPrecioListaIdeal;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import producto2.MbProductosBuscar;
import producto2.dominio.Producto;

/**
 *
 * @author Usuario
 */
@Named(value = "mbListaPrecioIdeal")
@SessionScoped
public class MbListaPrecioIdeal implements Serializable {

    /**
     * Creates a new instance of MbListaPrecioIdeal
     */
    private ArrayList<Producto> lstProducto = new ArrayList<Producto>();
    private ArrayList<ListaPrecioIdeal> lstListaPrecioIdeal = new ArrayList<ListaPrecioIdeal>();
    private ListaPrecioIdeal precioIdeal = new ListaPrecioIdeal();
    @ManagedProperty(value = "#{mbBuscar}")
    private MbProductosBuscar mbBuscar = new MbProductosBuscar();
    private ArrayList<ListaPrecioIdeal> lstListaPrecioTable;
    private ListaPrecioIdeal listaPrecioSeleccionPrincipal;
    private ListaPrecioIdeal listaPrecioSeleccionSecundaria;
    private boolean actualizar = false;

    public MbListaPrecioIdeal() {
    }

    public void buscar() {
        this.mbBuscar.buscarLista();
    }

    public void construir() {
        ArrayList<ListaPrecioIdeal> respaldo = new ArrayList<ListaPrecioIdeal>();
        for (Producto p : mbBuscar.getSeleccionados()) {
            ListaPrecioIdeal listaPrecioIdeal = new ListaPrecioIdeal();
            listaPrecioIdeal.setProducto(p);
            lstListaPrecioIdeal.add(listaPrecioIdeal);
        }
        for (int x = 0; x < lstListaPrecioIdeal.size(); x++) {
            for (int y = 0; y < lstListaPrecioTable.size(); y++) {
                if(lstListaPrecioIdeal.get(x).getProducto().getIdProducto()== lstListaPrecioTable.get(y).getProducto().getIdProducto()){
                    lstListaPrecioIdeal.remove(x);
                    x--;
                    break;
                }
            }
        }
    }

    public void eliminarProducto() {
        for (ListaPrecioIdeal l : lstListaPrecioIdeal) {
            if (l.equals(listaPrecioSeleccionSecundaria)) {
                lstListaPrecioIdeal.remove(l);
                listaPrecioSeleccionSecundaria = null;
                break;
            }
        }
    }

    public void actualizarPrecioLista(ListaPrecioIdeal ls) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            DAOListaPrecio dao = new DAOListaPrecio();
            dao.actualizar(ls);
            context.addMessage(null, new FacesMessage("Exito", "Lista de Precio Actualizado "));
        } catch (NamingException ex) {
            context.addMessage(null, new FacesMessage("Error", ex.getMessage()));
            Logger.getLogger(MbListaPrecioIdeal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            context.addMessage(null, new FacesMessage("Error", ex.getMessage()));
            Logger.getLogger(MbListaPrecioIdeal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelar() {
        this.actualizar = false;
        this.listaPrecioSeleccionPrincipal = null;
        lstListaPrecioIdeal = new ArrayList<ListaPrecioIdeal>();
        listaPrecioSeleccionSecundaria = null;
    }

    public void cargarDatos() {
        this.setActualizar(true);
        lstListaPrecioIdeal.add(this.getListaPrecioSeleccionPrincipal());
    }

    public void guardar() {
        RequestContext context = RequestContext.getCurrentInstance();
        boolean ok = this.validar();
        FacesMessage fMsg = null;
        if (ok == true) {
            try {
                DAOListaPrecio dao = new DAOListaPrecio();
                dao.guardarLista(lstListaPrecioIdeal);
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Nuevas listas de precio disponibles");
            } catch (NamingException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error:", ex.getMessage());
                Logger.getLogger(MbListaPrecioIdeal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error:", ex.getMessage());
            }
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
            this.cancelar();
        }

    }

    public boolean validar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error:", "");
        fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
        for (ListaPrecioIdeal lista : lstListaPrecioIdeal) {
            if (lista.getPrecioLista() == 0.00) {
                ok = false;
                fMsg.setDetail("Producto " + lista.getProducto().toString() + " requiere una lista precio");
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
                break;
            } else {
                ok = true;
            }
        }
        context.addCallbackParam("ok", ok);
        return ok;
    }

    public ArrayList<Producto> getLstProducto() {
        return lstProducto;
    }

    public void setLstProducto(ArrayList<Producto> lstProducto) {
        this.lstProducto = lstProducto;
    }

    public ListaPrecioIdeal getPrecioIdeal() {
        return precioIdeal;
    }

    public void setPrecioIdeal(ListaPrecioIdeal precioIdeal) {
        this.precioIdeal = precioIdeal;
    }

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public ArrayList<ListaPrecioIdeal> getLstListaPrecioIdeal() {
        return lstListaPrecioIdeal;
    }

    public void setLstListaPrecioIdeal(ArrayList<ListaPrecioIdeal> lstListaPrecioIdeal) {
        this.lstListaPrecioIdeal = lstListaPrecioIdeal;
    }

    public ArrayList<ListaPrecioIdeal> getLstListaPrecioTable() {
        if (lstListaPrecioTable == null) {
            try {
                lstListaPrecioTable = new ArrayList<ListaPrecioIdeal>();
                DAOListaPrecio dao = new DAOListaPrecio();
                for (TOPrecioListaIdeal p : dao.dameValores()) {
                    lstListaPrecioTable.add(this.convertir(p));
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbListaPrecioIdeal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MbListaPrecioIdeal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstListaPrecioTable;
    }

    public ListaPrecioIdeal convertir(TOPrecioListaIdeal to) {
        ListaPrecioIdeal precioIdeal = new ListaPrecioIdeal();
        precioIdeal.setProducto(this.mbBuscar.obtenerProducto(to.getIdProducto()));
        precioIdeal.setPrecioLista(to.getPrecioLista());
        return precioIdeal;
    }

    public void setLstListaPrecioTable(ArrayList<ListaPrecioIdeal> lstListaPrecioTable) {
        this.lstListaPrecioTable = lstListaPrecioTable;
    }

    public ListaPrecioIdeal getListaPrecioSeleccionPrincipal() {
        return listaPrecioSeleccionPrincipal;
    }

    public void setListaPrecioSeleccionPrincipal(ListaPrecioIdeal listaPrecioSeleccionPrincipal) {
        this.listaPrecioSeleccionPrincipal = listaPrecioSeleccionPrincipal;
    }

    public ListaPrecioIdeal getListaPrecioSeleccionSecundaria() {
        return listaPrecioSeleccionSecundaria;
    }

    public void setListaPrecioSeleccionSecundaria(ListaPrecioIdeal listaPrecioSeleccionSecundaria) {
        this.listaPrecioSeleccionSecundaria = listaPrecioSeleccionSecundaria;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }
}