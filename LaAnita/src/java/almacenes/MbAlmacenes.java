package almacenes;

import almacenes.dao.DAOAlmacenes;
import almacenes.dominio.Almacen;
import direccion.MbDireccion;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

/**
 *
 * @author jsolis
 */
@Named(value = "mbAlmacenes")
@SessionScoped
public class MbAlmacenes implements Serializable {
    private Almacen almacen;
    private ArrayList<Almacen> almacenes;
    private DAOAlmacenes dao;
    
    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion;
    
    public MbAlmacenes() throws NamingException {
        this.almacen=new Almacen();
    }
    
    public void copia(Almacen almacen) {
        
    }
    
    public void modificar(Almacen almacen) {
        
    }

    public void nuevoAlmacen() throws SQLException {
        Almacen a = new Almacen();
        a.setIdAlmacen(0);
        a.setAlmacen("");
        a.setIdCedis(0);
        a.setIdEmpresa(0);
        a.setEncargado("");
        a.setDireccion(this.mbDireccion.nuevaDireccion());
    }
    public void inicializarVariables() {
        Almacen a = new Almacen();
        a.setAlmacen("");
        a.setEncargado("");
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }
    
    private void cargaAlmacenes() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.almacenes = new ArrayList<Almacen>();
        try {
            this.dao=new DAOAlmacenes();
            this.almacenes=this.dao.obtenerAlmacenes();
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

    public ArrayList<Almacen> getAlmacenes() {
        if(this.almacenes==null) {
            this.cargaAlmacenes();
        }
        return almacenes;
    }

    public void setAlmacenes(ArrayList<Almacen> almacenes) {
        this.almacenes = almacenes;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }
}
