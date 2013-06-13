package productos;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOMarcas;
import productos.dominio.Marca;
import utilerias.Utilerias;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbMarca")
@SessionScoped
public class MbMarca {
    private String strFabricante;
    private Marca marca;
    private ArrayList<Marca> marcas;
    private DAOMarcas dao;
    
    public MbMarca() {
        this.strFabricante="1";
        this.marca = new Marca();
        try {
            this.dao=new DAOMarcas();
        } catch (NamingException ex) {
            Logger.getLogger(MbMarca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMarcas();
            this.dao.eliminar(this.marca.getIdMarca());
            this.marcas=this.dao.obtenerMarcas();
            this.marca=new Marca();
            ok=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okMarca", ok);
        return ok;
    }
    
    public boolean grabar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if(this.strFabricante==null || this.strFabricante.equals("0")) {
            fMsg.setDetail("Se requiere identificar al fabricante");
        } else if(this.marca.getMarca().trim().isEmpty()) {
            fMsg.setDetail("Se requiere la marca");
        } else {
            try {
                this.dao=new DAOMarcas();
                //this.marca.setIdFabricante(Integer.parseInt(this.strFabricante));
                if(this.marca.getIdMarca() == 0) {
                    this.marca.setIdMarca(this.dao.agregar(this.marca));
                } else {
                    this.dao.modificar(this.marca);
                }
                this.marcas=dao.obtenerMarcas();
                ok=true;
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okMarca", ok);
        return ok;
    }
    
    public void copia(Marca m) {
        this.marca=new Marca(m.getIdMarca(), m.getMarca(), m.isProduccion());
    }
    
    public String cancelar() {
        return "marcas.salir";
    }
    
    public String terminar() {
        return "menuMarcas.terminar";
    }
    /*
    private Marca nuevaMarca() {
        Marca mca=null;
        try {
            int ultimoCodigo=this.dao.ultimoCodigo();
            mca=new Marca(0, ultimoCodigo+1, "", false);
            return mca;
        } catch (SQLException ex) {
            Logger.getLogger(MbMarca.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mca;
    }
    * */
    
    public String mantenimiento(int idMarca) {
        String destino="marcas.mantenimiento";
        try {
            if(idMarca == 0) {
                //this.marca=nuevaMarca();
                this.marca=new Marca();
            } else {
                this.marca=this.dao.obtenerMarca(idMarca);
                if(this.marca == null) {
                    destino=null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMarca.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }
    /*
    public ArrayList<Marca> getListaMarcas() {
        try {
            if(listaMarcas == null) {
                listaMarcas=dao.obtenerMarcas(Integer.parseInt(this.strFabricante));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbMarca.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMarcas;
    }

    public void setListaMarcas(ArrayList<Marca> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }
    * */
    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getStrFabricante() {
        return strFabricante;
    }

    public void setStrFabricante(String strFabricante) {
        this.strFabricante = strFabricante;
    }

    public ArrayList<Marca> getMarcas() {
        return marcas;
    }

    public void setMarcas(ArrayList<Marca> marcas) {
        this.marcas = marcas;
    }
}
