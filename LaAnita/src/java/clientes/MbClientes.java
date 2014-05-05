package clientes;

import clientes.DAOClientes.DAOClientes;
import clientes.dominio.Cliente;
import clientesBancos.DAOClientesBancos.DAOClientesBancos;
import clientesBancos.MbClientesBancos;
import contribuyentes.Contribuyente;
import contribuyentes.DAOContribuyentes;
import contribuyentes.MbContribuyentes;
import direccion.MbDireccion;
import direccion.dao.DAODireccion;
import direccion.dominio.Direccion;
import impuestos.MbZonas;
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
import leyenda.dominio.ClientesBancos;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Usuario
 */
@Named(value = "mbClientesBueno")
@SessionScoped
public class MbClientes implements Serializable {

    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion = new MbDireccion();
    @ManagedProperty(value = "#{mbContribuyente}")
    private MbContribuyentes mbContribuyente = new MbContribuyentes();
    @ManagedProperty(value = "#{mbZonas}")
    private MbZonas mbZonas = new MbZonas();
    @ManagedProperty(value = "#{mbClientesBancos}")
    private MbClientesBancos mbClientesBancos = new MbClientesBancos();
    private ArrayList<Cliente> lstCliente = null;
    private Cliente clienteSeleccionado = new Cliente();
    private Cliente cliente = new Cliente();
    private int personaFisica = 0;
    private boolean actualizarRfc = false;
    private int controlActualizar = 0;
    private ClientesBancos clientes = new ClientesBancos();
    private boolean actualizar = false;

    /**
     * Creates a new instance of MbClientes
     */
    public MbClientes() {
    }

    public ArrayList<Cliente> getLstCliente() {
        if (lstCliente == null) {
            try {
                DAOClientes daoCliente = new DAOClientes();
                lstCliente = daoCliente.lstClientes();
            } catch (SQLException ex) {
                FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", ex.toString());
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
                Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstCliente;
    }

    public String salir() {
        return "index.xhtml";
    }

    public void validarDireccion() {
        if (mbDireccion.validarDireccion() == true) {
            if (controlActualizar == 1) {
                try {
                    DAODireccion daoDireccion = new DAODireccion();
                    daoDireccion.modificar(mbDireccion.getDireccion().getIdDireccion(), mbDireccion.getDireccion().getCalle(), mbDireccion.getDireccion().getNumeroExterior(), mbDireccion.getDireccion().getNumeroInterior(), mbDireccion.getDireccion().getReferencia(), mbDireccion.getDireccion().getPais().getIdPais(), mbDireccion.getDireccion().getCodigoPostal(), mbDireccion.getDireccion().getEstado(), mbDireccion.getDireccion().getMunicipio(), mbDireccion.getDireccion().getLocalidad(), mbDireccion.getDireccion().getColonia(), mbDireccion.getDireccion().getNumeroLocalizacion());
                } catch (NamingException ex) {
                    Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                this.cliente.getContribuyente().setDireccion(mbDireccion.getDireccion());
            }
        }
    }

    public void cargarDatos() {
        try {
            this.setControlActualizar(1);
            this.setActualizarRfc(true);
            mbZonas.getZona().setIdZona(clienteSeleccionado.getImpuestoZona().getIdZona());
            mbDireccion.setDireccion(clienteSeleccionado.getContribuyente().getDireccion());
            cliente.setCodigoCliente(clienteSeleccionado.getCodigoCliente());
            cliente.getContribuyente().setDireccion(clienteSeleccionado.getContribuyente().getDireccion());
            cliente.setDescuentoComercial(clienteSeleccionado.getDescuentoComercial());
            cliente.setDescuentoProntoPago(clienteSeleccionado.getDescuentoProntoPago());
            cliente.setDiasBloqueo(clienteSeleccionado.getDiasBloqueo());
            cliente.setDiasCredito(clienteSeleccionado.getDiasCredito());
            cliente.setLimiteCredito(clienteSeleccionado.getLimiteCredito());
            cliente.setIdCliente(clienteSeleccionado.getIdCliente());
            mbContribuyente.getContribuyente().setContribuyente(clienteSeleccionado.getContribuyente().getContribuyente());
            mbContribuyente.getContribuyente().setCurp(clienteSeleccionado.getContribuyente().getCurp());
            mbContribuyente.getContribuyente().setRfc(clienteSeleccionado.getContribuyente().getRfc());
            mbContribuyente.getContribuyente().setIdRfc(clienteSeleccionado.getContribuyente().getIdRfc());
            mbContribuyente.getContribuyente().setIdContribuyente(clienteSeleccionado.getContribuyente().getIdContribuyente());
            mbClientesBancos.getMbBanco().obtenerBancos(cliente.getCodigoCliente());
            mbClientesBancos.cargarBancos(cliente.getCodigoCliente());
        } catch (SQLException ex) {
            Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelar() {
        clientes.getBancoLeyenda().setIdBanco(0);
        clientes.setIdBanco(0);
        clienteSeleccionado = null;
        this.controlActualizar = 0;
        this.limpiar();
        this.actualizarRfc = false;
        this.actualizar = false;
    }

    public void obtenerBancosClientes() {
        try {
            mbClientesBancos.getMbBanco().obtenerBancos(cliente.getIdCliente());
        } catch (SQLException ex) {
            Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dameStatusRfc() {
        int longitud = mbContribuyente.getContribuyente().getRfc().length();
        if (longitud == 13) {
            for (Cliente cl : lstCliente) {
                if (cl.getContribuyente().getRfc().equals(mbContribuyente.getContribuyente().getRfc())) {
                    boolean ok = false;
                    RequestContext context = RequestContext.getCurrentInstance();
                    FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
                    fMsg.setDetail("Rfc Existente !!");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                    this.cargarDatosClientes(mbContribuyente.getContribuyente().getRfc());
                    break;
                }
            }
            personaFisica = 1;
        } else {
            for (Cliente cl : lstCliente) {
                if (cl.getContribuyente().getRfc().equals(mbContribuyente.getContribuyente().getRfc())) {
                    boolean ok = false;
                    RequestContext context = RequestContext.getCurrentInstance();
                    FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
                    fMsg.setDetail("Rfc Existente !!");
                    context.addCallbackParam("okContribuyente", ok);
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                    this.cargarDatosClientes(mbContribuyente.getContribuyente().getRfc());
                    break;
                }
            }
            personaFisica = 2;
        }
    }

    public void cargarDatosClientes(String rfc) {
        try {
            DAOClientes dao = new DAOClientes();
            Cliente cl = dao.dameInformacionCliente(rfc);
            this.setControlActualizar(1);
            this.setActualizarRfc(true);
            mbZonas.getZona().setIdZona(cl.getImpuestoZona().getIdZona());
            mbDireccion.setDireccion(cl.getContribuyente().getDireccion());
            cliente.setCodigoCliente(cl.getCodigoCliente());
            cliente.getContribuyente().setDireccion(cl.getContribuyente().getDireccion());
            cliente.setDescuentoComercial(cl.getDescuentoComercial());
            cliente.setDescuentoProntoPago(cl.getDescuentoProntoPago());
            cliente.setDiasBloqueo(cl.getDiasBloqueo());
            cliente.setDiasCredito(cl.getDiasCredito());
            cliente.setLimiteCredito(cl.getLimiteCredito());
            cliente.setIdCliente(cl.getIdCliente());
            mbContribuyente.getContribuyente().setContribuyente(cl.getContribuyente().getContribuyente());
            mbContribuyente.getContribuyente().setCurp(cl.getContribuyente().getCurp());
            mbContribuyente.getContribuyente().setRfc(cl.getContribuyente().getRfc());
            mbContribuyente.getContribuyente().setIdRfc(cl.getContribuyente().getIdRfc());
            mbContribuyente.getContribuyente().setIdContribuyente(cl.getContribuyente().getIdContribuyente());
            mbClientesBancos.getMbBanco().obtenerBancos(cl.getCodigoCliente());
            mbClientesBancos.cargarBancos(cl.getCodigoCliente());
        } catch (SQLException ex) {
            Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardarCuentasBancarias() {
        boolean ok = false;
        FacesMessage fMsg = null;
        RequestContext context = RequestContext.getCurrentInstance();
        ok = mbClientesBancos.validar();
        if (ok == true) {
            try {
                DAOClientesBancos daoClientesBancos = new DAOClientesBancos();
                mbClientesBancos.getClientesBancos().setCodigoCliente(cliente.getCodigoCliente());
                if (clientes.getBancoLeyenda().getIdBanco() == 0) {
                    daoClientesBancos.guardarClientesBancos(mbClientesBancos.getClientesBancos());
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                    fMsg.setDetail("Exito!! Nueva Cuenta Disponible !!");
                } else {
                    mbClientesBancos.getClientesBancos().setIdClienteBanco(clientes.getIdClienteBanco());
                    daoClientesBancos.actualizarClientesBancos(mbClientesBancos.getClientesBancos());
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                    fMsg.setDetail("Exito!! Cuenta Actualizada !!");
                }
                mbClientesBancos.cargarBancos(cliente.getCodigoCliente());
            } catch (SQLException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", ex.getMessage());
                Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        context.addCallbackParam("ok", ok);
    }

    public void guardar() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        boolean ok = false;
        ok = mbContribuyente.valida();
        if (ok == true) {
            cliente.setContribuyente(mbContribuyente.getContribuyente());
            if (mbDireccion.getDireccion().getCalle().equals("")) {
                ok = false;
                fMsg.setDetail("Error!! Direccion Requerida");
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
            } else {
                ok = this.validarClientes();
                if (ok == true) {
                    cliente.setImpuestoZona(mbZonas.getZona());
                    DAOClientes daoCliente = new DAOClientes();
                    try {
                        cliente.getContribuyente().setDireccion(mbDireccion.getDireccion());
                        FacesMessage fMsgs = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                        if (controlActualizar == 0) {
                            daoCliente.guardarCliente(cliente);
                            fMsgs.setDetail("Exito!! Nuevo Cliente dado de Alta");
                        } else {
                            DAOContribuyentes daoContribuyente = new DAOContribuyentes();
                            daoContribuyente.actualizarContribuyente(mbContribuyente.getContribuyente());
                            daoCliente.actualizarClientes(cliente);
                            fMsgs.setDetail("Exito!! Cliente Actualizado");
                        }
                        FacesContext.getCurrentInstance().addMessage(null, fMsgs);
                        lstCliente = null;
                        this.limpiar();
                    } catch (SQLException ex) {
                        ok = false;
                        fMsg.setDetail(ex.getMessage());
                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                        Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NamingException ex) {
                        ok = false;
                        fMsg.setDetail(ex.getMessage());
                        FacesContext.getCurrentInstance().addMessage(null, fMsg);
                        Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
        actualizar = false;
        clienteSeleccionado = null;
        actualizarRfc = false;
        context.addCallbackParam("ok", ok);

    }

    private void limpiar() {
        mbDireccion.setDireccion(new Direccion());
        mbZonas.getZona().setIdZona(0);
        cliente.setCodigoCliente(0);
        mbContribuyente.setContribuyente(new Contribuyente());
        cliente.setDescuentoComercial((float) 0.00);
        cliente.setDescuentoProntoPago(0.00);
        cliente.setDiasBloqueo(0);
        cliente.setDiasCredito(0);
        cliente.setIdCliente(0);
        cliente.setLimiteCredito((float) 0.00);
        cliente.getContribuyente().setDireccion(new Direccion());
    }

    public boolean validarClientes() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (cliente.getCodigoCliente() == 0) {
            fMsg.setDetail("Error!! Codigo Cliente Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else if (!mbZonas.validar()) {
        } else if (cliente.getDiasCredito() == 0) {
            fMsg.setDetail("Error!! Dias de Credito Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else {
            ok = true;
        }
        return ok;
    }

    public void dameInformacion() {
        if (clientes.getBancoLeyenda().getIdBanco() == 0) {
            mbClientesBancos.setClientesBancos(new ClientesBancos());
            mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(0);
        } else {
            mbClientesBancos.setClientesBancos(clientes);
            mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(clientes.getBancoLeyenda().getIdBanco());
        }
    }

    public void limpiarCampos() {
        clientes.setIdBanco(0);
        actualizar = true;
        mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(0);
    }

    public void dameCuentasBancarias() {
        mbClientesBancos.getMbBanco().cargarBancos(cliente.getIdCliente());
    }

    public void setLstCliente(ArrayList<Cliente> lstCliente) {
        this.lstCliente = lstCliente;
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }

    public MbZonas getMbZonas() {
        return mbZonas;
    }

    public void setMbZonas(MbZonas mbZonas) {
        this.mbZonas = mbZonas;
    }

    public MbContribuyentes getMbContribuyente() {
        return mbContribuyente;
    }

    public void setMbContribuyente(MbContribuyentes mbContribuyente) {
        this.mbContribuyente = mbContribuyente;
    }

    public int getPersonaFisica() {
        return personaFisica;
    }

    public void setPersonaFisica(int personaFisica) {
        this.personaFisica = personaFisica;
    }

    public boolean isActualizarRfc() {
        return actualizarRfc;
    }

    public void setActualizarRfc(boolean actualizarRfc) {
        this.actualizarRfc = actualizarRfc;
    }

    public int getControlActualizar() {
        return controlActualizar;
    }

    public void setControlActualizar(int controlActualizar) {
        this.controlActualizar = controlActualizar;
    }

    public MbClientesBancos getMbClientesBancos() {
        return mbClientesBancos;
    }

    public void setMbClientesBancos(MbClientesBancos mbClientesBancos) {
        this.mbClientesBancos = mbClientesBancos;
    }

    public ClientesBancos getClientes() {
        return clientes;
    }

    public void setClientes(ClientesBancos clientes) {
        this.clientes = clientes;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

}
