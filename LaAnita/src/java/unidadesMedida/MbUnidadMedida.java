package unidadesMedida;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbUnidadMedida")
@SessionScoped
public class MbUnidadMedida implements Serializable {
    private UnidadMedida unidadMedida;
    private ArrayList<UnidadMedida> unidadesMedida;
    private DAOUnidadesMedida dao;
    
    public MbUnidadMedida() {}
    
    public String grabar() {
        String destino=null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.unidadMedida.getUnidadMedida().equals("")) {
            fMsg.setDetail("Se requiere la descripcion de la unidad de medida !!");
        } else if(this.unidadMedida.getAbreviatura().equals("")) {
            fMsg.setDetail("Se requiere la abreviatura de la unidad de medida !!");
        } else {
            try {
                this.dao=new DAOUnidadesMedida();
                if(this.unidadMedida.getIdUnidadMedida()==0) {
                    this.unidadMedida.setIdUnidadMedida(this.dao.agregar(this.unidadMedida));
                } else {
                    this.dao.modificar(this.unidadMedida);
                } // 987-23-38 HSBC Plaza Dorada - Lesfer Lopez - Mes de Julio. Cliente Advance.
                this.unidadesMedida=null;
                destino="unidadesMedida.salir";
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        if(destino==null) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return destino;
    }
    
    public String cancelar() {
        return "unidadesMedida.salir";
    }
    
    public String terminar() {
        return "menuUnidadesMedida.terminar";
    }
    
    public String mantenimiento(int idUnidadMedida) {
        String destino=null;
        try {
            if(idUnidadMedida==0) {
                this.unidadMedida=new UnidadMedida(0, "", "", 0);
            } else {
                this.dao=new DAOUnidadesMedida();
                this.unidadMedida=this.dao.obtenerUnidad(idUnidadMedida);
            }
            destino="unidadesMedida.mantenimiento";
        } catch (SQLException ex) {
            Logger.getLogger(MbUnidadMedida.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbUnidadMedida.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }
    
    public void cargaUnidadesMedida() {
        try {
            this.dao=new DAOUnidadesMedida();
            this.unidadesMedida=this.dao.obtenerUnidades();
        } catch (SQLException ex) {
            Logger.getLogger(MbUnidadMedida.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbUnidadMedida.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public ArrayList<UnidadMedida> getUnidadesMedida() {
        if(this.unidadesMedida==null) {
            this.cargaUnidadesMedida();
        }
        return unidadesMedida;
    }

    public void setUnidadesMedida(ArrayList<UnidadMedida> unidadesMedida) {
        this.unidadesMedida = unidadesMedida;
    }
}
