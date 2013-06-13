package proveedores;

import direccion.MbDireccion;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import proveedores.dao.DAOProveedores;
import proveedores.dominio.Proveedor;
import proveedores.to.TOProveedor;
import utilerias.Utilerias;
/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbProveedores")
@SessionScoped
public class MbProveedores implements Serializable {
    private Proveedor proveedor;
    private ArrayList<Proveedor> listaProveedores;
    private DAOProveedores dao;
    @ManagedProperty(value="#{mbDireccion}")
    private MbDireccion mbDireccion;
    
    public MbProveedores() {
        try {
            this.dao=new DAOProveedores();
        } catch (NamingException ex) {
            Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        String destino=null;
        int codigo=this.proveedor.getCodigoProveedor();
        String strProveedor=Utilerias.Acentos(this.proveedor.getProveedor());
        String telefono=this.proveedor.getTelefono();
        String rfc=this.proveedor.getRfc();
        String fax=this.proveedor.getFax();
        String correo=this.proveedor.getCorreo();
        int diasCredito=this.proveedor.getDiasCredito();
        double limiteCredito=this.proveedor.getLimiteCredito();
        int idDireccion=this.proveedor.getDireccion().getIdDireccion();
        
        if(strProveedor.isEmpty()) {
            fMsg.setDetail("Se requiere el nombre del proveedor !!");
        } else if(rfc.isEmpty()) {
            fMsg.setDetail("Se requiere el RFC del proveedor !!");
        } else if(correo.isEmpty()) {
            fMsg.setDetail("Se requiere el correo del proveedor !!");
        } else if(diasCredito < 0) {
            fMsg.setDetail("Los dias de crédito no debe ser menores que cero !!");
        } else if(limiteCredito < 0.00) {
            fMsg.setDetail("El límite de crédito no debe ser menor que cero !!");
        } else if(idDireccion == 0) {
            fMsg.setDetail("Se requiere la dirección del proveedor !!");
        } else {
            try {
                int idProveedor=this.proveedor.getIdProveedor();
                if (idProveedor == 0) {
                    idProveedor=this.dao.agregar(codigo, strProveedor, rfc, telefono, fax, correo, diasCredito, limiteCredito, idDireccion);
                } else {
                    this.dao.modificar(this.proveedor.getIdProveedor(), strProveedor, rfc, telefono, fax, correo, diasCredito, limiteCredito, idDireccion);
                }
                this.proveedor=this.convertir(this.dao.obtenerUnProveedor(idProveedor));
                this.listaProveedores=null;
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("El proveedor se grabó correctamente !!");
//                destino="proveedor.salir";
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
                Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        return destino;
    }
    
    public String salir() {
        if(this.proveedor.getIdProveedor() == 0 && this.proveedor.getDireccion().getIdDireccion() > 0) {
            mbDireccion.eliminar(this.proveedor.getDireccion().getIdDireccion());
        }
        return "proveedor.salir";
    }
    
    public String terminar() {
        return "menuProveedores.terminar";
    }
    
    private Proveedor nuevoProveedor() throws SQLException {
        Proveedor p=null;
        int ultimo=this.dao.ultimoProveedor();
            
        p=new Proveedor();
        p.setIdProveedor(0);
        p.setCodigoProveedor(ultimo+1);
        p.setProveedor("");
        p.setRfc("");
        p.setTelefono("");
        p.setFax("");
        p.setCorreo("");
        p.setDiasCredito(0);
        p.setLimiteCredito(0.00);
        p.setFechaAlta(utilerias.Utilerias.date2String(new Date()));
        p.setDireccion(this.mbDireccion.nuevaDireccion());
        return p;
    }
    
    public String mantenimiento(int idProveedor) {
        String destino="proveedor.mantenimiento";
        try {
            if(idProveedor == 0) {
                this.proveedor=nuevoProveedor();
            } else {
                TOProveedor toProveedor=this.dao.obtenerUnProveedor(idProveedor);
                if(toProveedor == null) destino=null;
                else this.proveedor=convertir(toProveedor);
            }
        } catch (SQLException ex) {
            destino=null;
            Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }

    public ArrayList<Proveedor> getListaProveedores() {
        try {
            if(listaProveedores == null) cargaProveedores();
        } catch (SQLException ex) {
            Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaProveedores;
    }
    
    private void cargaProveedores() throws SQLException {
        listaProveedores=new ArrayList<Proveedor>();
        ArrayList<TOProveedor> toLista=dao.obtenerProveedores();
        for(TOProveedor c:toLista) {
            listaProveedores.add(convertir(c));
        }
    }
    
    private Proveedor convertir(TOProveedor to) {
        Proveedor p=new Proveedor();
        p.setIdProveedor(to.getIdProveedor());
        p.setCodigoProveedor(to.getCodigoProveedor());
        p.setProveedor(to.getProveedor());
        p.setRfc(to.getRfc());
        p.setTelefono(to.getTelefono());
        p.setFax(to.getFax());
        p.setCorreo(to.getCorreo());
        p.setDiasCredito(to.getDiasCredito());
        p.setLimiteCredito(to.getLimiteCredito());
        p.setFechaAlta(utilerias.Utilerias.date2String(to.getFechaAlta()));
        p.setDireccion(this.mbDireccion.obtener(to.getIdDireccion()));
        return p;
    }

    public void setListaProveedores(ArrayList<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }
}
