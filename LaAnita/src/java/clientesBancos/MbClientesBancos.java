/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesBancos;

import Message.Mensajes;
import bancos.MbBanco;
import clientesBancos.DAOClientesBancos.DAOClientesBancos;
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
import leyenda.dominio.ClientesBancos;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Usuario
 */
@Named(value = "mbClientesBancos")
@SessionScoped
public class MbClientesBancos implements Serializable {

    @ManagedProperty(value = "#{mbBancosSat}")
    private MbBanco mbBanco = new MbBanco();
    private ClientesBancos clientesBancos = new ClientesBancos();
    private ArrayList<SelectItem> lstClientesBancos = new ArrayList<SelectItem>();

    /**
     * Creates a new instance of MbClientesBancos
     */
    public MbClientesBancos() {
    }

    public boolean validar() {
        boolean ok = false;
////        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error:", "");
        if (mbBanco.getObjBanco().getIdBanco() == 0) {
            fMsg.setDetail("Banco Requerido !!");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } else if (clientesBancos.getNumCtaPago().equals("")) {
            fMsg.setDetail("Numero de Cuenta Requerido !!");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } else if (clientesBancos.getMedioPago().equals("")) {
            fMsg.setDetail("Medio de Pago Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } else {
            ok = true;
            clientesBancos.setIdBanco(mbBanco.getObjBanco().getIdBanco());
        }
        return ok;
    }

    public void cargarBancos(int codigoCliente) {
        try {
            lstClientesBancos = new ArrayList<SelectItem>();
            ArrayList<ClientesBancos> lstClientes = new ArrayList<ClientesBancos>();
            DAOClientesBancos daoClientesBancos = new DAOClientesBancos();
            lstClientes = daoClientesBancos.dameBancos(codigoCliente);
            ClientesBancos c = new ClientesBancos();
            c.setIdClienteBanco(0);
            c.getBancoLeyenda().setNombreCorto("Nueva Cuenta Bancaria");
            SelectItem selec = new SelectItem(c, c.getBancoLeyenda().getNombreCorto());
            lstClientesBancos.add(selec);
//            lstClientesBancos.add(new SelectItem(c, c.getBancoLeyenda().getNombreCorto()));
            for (ClientesBancos cli : lstClientes) {
                lstClientesBancos.add(new SelectItem(cli, cli.getBancoLeyenda().getNombreCorto()));
            }
        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbClientesBancos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClientesBancos getClientesBancos() {
        return clientesBancos;
    }

    public void setClientesBancos(ClientesBancos clientesBancos) {
        this.clientesBancos = clientesBancos;
    }

    public MbBanco getMbBanco() {
        return mbBanco;
    }

    public void setMbBanco(MbBanco mbBanco) {
        this.mbBanco = mbBanco;
    }

    public ArrayList<SelectItem> getLstClientesBancos() {
        return lstClientesBancos;
    }

    public void setLstClientesBancos(ArrayList<SelectItem> lstClientesBancos) {
        this.lstClientesBancos = lstClientesBancos;
    }

}
