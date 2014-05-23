/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos;

import entradas.dao.DAOMovimientos;
import entradas.dominio.MovimientoProducto;
import formatos.DAOFormatos.DAOFormatos;
import formatos.dominio.Formato;
import formatos.formatosDetalleDominio.FormatoDetalle;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import mbMenuClientesGrupos.MbClientesGrupos;
import monedas.MbMonedas;
import org.primefaces.context.RequestContext;
import producto2.MbProductosBuscar;
import producto2.dominio.Producto;

/**
 *
 * @author Usuario
 */
@Named(value = "mbFormatos")
@SessionScoped
public class MbFormatos implements Serializable {

    private ArrayList<SelectItem> lstItems = null;

    @ManagedProperty(value = "#{mbClientesGrupos}")
    private MbClientesGrupos mbClientesGrupos = new MbClientesGrupos();
    @ManagedProperty(value = "#{mbBuscar}")
    private MbProductosBuscar mbBuscar = new MbProductosBuscar();
    private ArrayList<Formato> lstFormato;
    @ManagedProperty(value = "#{mbMonedas}")
    private MbMonedas mbMonedas = new MbMonedas();
    private Formato formato = new Formato();
    private Formato cmbFormato = new Formato();
    private Formato formatoSeleccion = null;
    private boolean actualizar = false;
    ArrayList<FormatoDetalle> lstFormatoDetalle = new ArrayList<FormatoDetalle>();
    private DAOMovimientos dao;

    /**
     * Creates a new instance of MbFormatos
     */
    public MbFormatos() {
    }

    public boolean validar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
        if (formato.getFormato().equals("")) {
            fMsg.setDetail("Se requiere un nombre del Formato");
        } else if (mbMonedas.getMoneda().getIdMoneda() == 0) {
            fMsg.setDetail("Seleccione un tipo de Moneda");
        } else {
            ok = true;
        }
        if (ok == false) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("ok", ok);
        return ok;
    }

    public void actualizar() {
        actualizar = true;
    }

    public void guardarFormato() {
        boolean ok = validar();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
        RequestContext context = RequestContext.getCurrentInstance();
        if (ok == true) {
            try {
                DAOFormatos dao = new DAOFormatos();
                formato.getMoneda().setIdMoneda(mbMonedas.getMoneda().getIdMoneda());
                if (actualizar == false) {
                    dao.guardarFormato(formato);
                    fMsg.setDetail("Nuevo Formato Disponible");
                } else {
                    dao.actualizar(formato);
                    fMsg.setDetail("Formato Actualizado");
                }
                this.setLstFormato(null);
                this.limpiar();
            } catch (NamingException ex) {
                ok = false;
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                ok = false;
                fMsg.setDetail(ex.getMessage());
            }
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        }
    }

    public void limpiar() {
        mbMonedas.getMoneda().setIdMoneda(0);
        formato.setIdFormato(0);
        formato.setFormato("");
        formato.getMoneda().setIdMoneda(0);
        actualizar = false;
        formatoSeleccion = null;
    }

    public void cargarDatos() {
        formato.setIdFormato(formatoSeleccion.getIdFormato());
        formato.setFormato(formatoSeleccion.getFormato());
        mbMonedas.getMoneda().setIdMoneda(formatoSeleccion.getMoneda().getIdMoneda());
    }

    public ArrayList<SelectItem> getLstItems() {
        if (lstItems == null) {
            try {
                DAOFormatos dao = new DAOFormatos();
                ArrayList<Formato> lstFormato = new ArrayList<Formato>();
                try {
                    lstFormato = dao.dameListaFormatos();
                } catch (SQLException ex) {
                    Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
                }
                Formato format = new Formato();
                format.setFormato("Nuevo Formato");
                format.setIdFormato(0);
                lstItems = new ArrayList<SelectItem>();
                lstItems.add(new SelectItem(format, format.getFormato()));
                for (Formato formato : lstFormato) {
                    SelectItem select = new SelectItem(formato, formato.getFormato());
                    lstItems.add(select);
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstItems;
    }

    public void construir() {
        for (Producto p : mbBuscar.getSeleccionados()) {
            FormatoDetalle formato = new FormatoDetalle();
            formato.setProducto(p);
            lstFormatoDetalle.add(formato);
        }
    }

    public void buscar() {
        this.mbBuscar.buscarLista();
        if (this.mbBuscar.getProducto() != null) {
            this.actualizaProductoSeleccionado();
        }
    }

    public void actualizaProductoSeleccionado() {

    }

    public void setLstItems(ArrayList<SelectItem> lstItems) {
        this.lstItems = lstItems;
    }

    public MbClientesGrupos getMbClientesGrupos() {
        return mbClientesGrupos;
    }

    public void setMbClientesGrupos(MbClientesGrupos mbClientesGrupos) {
        this.mbClientesGrupos = mbClientesGrupos;
    }

    public Formato getFormato() {
        return formato;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public Formato getCmbFormato() {
        return cmbFormato;
    }

    public void setCmbFormato(Formato cmbFormato) {
        this.cmbFormato = cmbFormato;
    }

    public MbMonedas getMbMonedas() {
        return mbMonedas;
    }

    public void setMbMonedas(MbMonedas mbMonedas) {
        this.mbMonedas = mbMonedas;
    }

    public ArrayList<Formato> getLstFormato() {
        if (lstFormato == null) {
            lstFormato = new ArrayList<Formato>();
            try {
                DAOFormatos dao = new DAOFormatos();
                lstFormato = dao.dameListaFormatos();
            } catch (NamingException ex) {
                Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MbFormatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lstFormato;
    }

    public void setLstFormato(ArrayList<Formato> lstFormato) {
        this.lstFormato = lstFormato;
    }

    public Formato getFormatoSeleccion() {
        return formatoSeleccion;
    }

    public void setFormatoSeleccion(Formato formatoSeleccion) {
        this.formatoSeleccion = formatoSeleccion;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public ArrayList<FormatoDetalle> getLstFormatoDetalle() {
        return lstFormatoDetalle;
    }

    public void setLstFormatoDetalle(ArrayList<FormatoDetalle> lstFormatoDetalle) {
        this.lstFormatoDetalle = lstFormatoDetalle;
    }

}
