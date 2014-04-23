/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesTienda;

import clientesTienda.DAOClientesTienda.DAOClientesTienda;
import clientesTienda.dominio.ClienteTienda;
import direccion.MbDireccion;
import direccion.dominio.Direccion;
import formatos.DAOFormatos.DAOFormatos;
import formatos.MbFormatos;
import formatos.dominio.Formato;
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
import org.primefaces.context.RequestContext;
import rutas.MbRutas;
import rutas.daoRutas.DAORutas;

/**
 *
 * @author Usuario
 */
@Named(value = "mbClientesTienda")
@SessionScoped
public class MbClientesTienda implements Serializable {

    private ClienteTienda clienteTienda = new ClienteTienda();
    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion = new MbDireccion();
    @ManagedProperty(value = "#{mbRutas}")
    private MbRutas mbRutas = new MbRutas();
    @ManagedProperty(value = "#{mbFormatos}")
    private MbFormatos mbFormatos = new MbFormatos();
    private ArrayList<ClienteTienda> lstClientesGrupos = null;

    public MbClientesTienda() {
    }

    public void validarDireccion() {
        boolean ok = mbDireccion.validarDireccion();
        if (ok == true) {
//           clienteTienda = new ClienteTienda();
            Direccion direccion = new Direccion();
            direccion = mbDireccion.getDireccion();
            clienteTienda.setDireccion(direccion);
            System.out.println("finalizo correctamente");
        }
    }

    public void guardarRutas() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = null;
        ok = mbRutas.validar();
        if (ok == true) {
            try {
                DAORutas dao = new DAORutas();
                dao.guardar(mbRutas.getRuta());
                ArrayList<SelectItem> lst = null;
                mbRutas.setLstRuta(lst);
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Nueva ruta disponible");
            } catch (NamingException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", ex.getMessage());
            } catch (SQLException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", ex.getMessage());
            }
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        }
    }

    public void guardarFormatos() {
        boolean ok = mbFormatos.validar();
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = null;
        if (ok = true) {
            try {
                DAOFormatos dao = new DAOFormatos();
                Formato formato = new Formato();
                formato.setFormato(mbFormatos.getFormato().getFormato());
                formato.getClientesGrupo().setIdGrupoCte(mbFormatos.getMbClientesGrupos().getCmbClientesGrupos().getIdGrupoCte());
                dao.guardarFormato(formato);
                mbFormatos.setLstItems(null);
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Nuevos formatos disponibles");
            } catch (NamingException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", ex.getMessage());
                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", ex.getMessage());
                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
            }
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        }
    }

    public ClienteTienda getClienteTienda() {
        return clienteTienda;
    }

    public void setClienteTienda(ClienteTienda clienteTienda) {
        this.clienteTienda = clienteTienda;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }

    public MbRutas getMbRutas() {
        return mbRutas;
    }

    public void setMbRutas(MbRutas mbRutas) {
        this.mbRutas = mbRutas;
    }

    public MbFormatos getMbFormatos() {
        return mbFormatos;
    }

    public void setMbFormatos(MbFormatos mbFormatos) {
        this.mbFormatos = mbFormatos;
    }

    public ArrayList<ClienteTienda> getLstClientesGrupos() {
        if (lstClientesGrupos == null) {
            try {
                DAOClientesTienda daoClientes = new DAOClientesTienda();
                lstClientesGrupos = daoClientes.listaClientesTienda();
            } catch (SQLException ex) {
                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NamingException ex) {
                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstClientesGrupos;
    }

    public void setLstClientesGrupos(ArrayList<ClienteTienda> lstClientesGrupos) {
        this.lstClientesGrupos = lstClientesGrupos;
    }
}
