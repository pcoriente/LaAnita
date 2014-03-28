package productos;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOEmpaques;
import productos.dao.DAOGrupos;
import productos.dao.DAOPartes;
import productos.dao.DAOSubGrupos;
import productos.dominio.Empaque;
import productos.dominio.Grupo;
import productos.dominio.Parte2;
import productos.dominio.SubGrupo;

/**
 *
 * @author jsolis
 */
@Named(value = "mbBuscarEmpaques")
@SessionScoped
public class MbBuscarEmpaques implements Serializable {
    private String tipoBuscar;
    private String strBuscar;
    private Parte2 parte;
    private Empaque producto;
    private ArrayList<Empaque> productos;
    private Grupo grupo;
    private ArrayList<SelectItem> listaGrupos;
    private SubGrupo subGrupo;
    private ArrayList<SelectItem> listaSubGrupos;
    private Empaque[] seleccionados;
//    private EmpaqueDataModel empaquesModelo;
    
    public MbBuscarEmpaques() {
        this.inicializa();
    }
    
    public void buscarLista() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            DAOEmpaques dao=new DAOEmpaques();
            if (this.tipoBuscar.equals("1")) {
                this.producto=dao.obtenerEmpaque(this.strBuscar);
                if (this.producto == null) {
                    fMsg.setDetail("No se encontró producto con el SKU proporcionado");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                } else {
                    ok = true;
                }
            } else if(this.tipoBuscar.equals("2")){
                this.producto = null;
                this.productos = dao.obtenerEmpaquesParte(this.parte.getIdParte());
            } else if(this.tipoBuscar.equals("3")) {
                this.producto = null;
                this.productos = dao.obtenerEmpaquesDescripcion(this.strBuscar);
            } else {
                this.producto = null;
                this.productos = dao.obtenerEmpaquesClasificacion(this.grupo.getIdGrupo(), this.subGrupo.getIdSubGrupo());
////                this.seleccionados=new Empaque[];
//                this.empaquesModelo=new EmpaqueDataModel(productos);
            }
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okBuscar", ok);
    }
    
    public void verCambio() {
        if (this.tipoBuscar.equals("2")) {
            this.parte = new Parte2(0, "");
        } else {
            this.strBuscar = "";
        }
        this.productos=null;
    }
    
    public ArrayList<Parte2> completePartes(String query) {
        boolean ok = false;
        ArrayList<Parte2> partes = null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            DAOPartes dao = new DAOPartes();
            partes = dao.completePartes(query);
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
        return partes;
    }
    
    public void inicializar() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.tipoBuscar = "2";
        this.strBuscar = "";
        this.parte = new Parte2(0, "");
        this.productos = new ArrayList<Empaque>();
        this.seleccionados = new Empaque[]{};
//        this.empaquesModelo=new EmpaqueDataModel(productos);
        this.cargaGrupos();
        this.cargaSubGrupos();
    }
    
    private void cargaListaSubGrupos(ArrayList<SubGrupo> lstSubGrupos) {
        this.listaSubGrupos=new ArrayList<SelectItem>();
        this.subGrupo=new SubGrupo(0, "Seleccione");
        this.listaSubGrupos.add(new SelectItem(this.subGrupo, this.subGrupo.toString()));
        for(SubGrupo s: lstSubGrupos) {
            listaSubGrupos.add(new SelectItem(s, s.toString()));
        }
    }
    
    public void cargaSubGrupos() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "cargaSubGrupos");
        try {
            if(this.grupo.getIdGrupo()==0) {
                this.listaSubGrupos=new ArrayList<SelectItem>();
                this.subGrupo=new SubGrupo(0, "Seleccione");
                this.listaSubGrupos.add(new SelectItem(this.subGrupo, this.subGrupo.toString()));
            } else {
                DAOSubGrupos daoSubGrupos=new DAOSubGrupos();
                cargaListaSubGrupos(daoSubGrupos.obtenerSubGrupos(this.grupo.getIdGrupo()));
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
    
    public void cargaGrupos() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "cargaGrupos");
        try {
            DAOGrupos daoGrupos=new DAOGrupos();
            this.listaGrupos=new ArrayList<SelectItem>();
            this.grupo=new Grupo(0,0,"Seleccione");
            this.listaGrupos.add(new SelectItem(this.grupo, this.grupo.toString()));
            for(Grupo g: daoGrupos.obtenerGrupos()) {
                listaGrupos.add(new SelectItem(g, g.toString()));
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
    
    public void obtenerEmpaquesParte(int idParte) {
        
    }

    public String getTipoBuscar() {
        return tipoBuscar;
    }

    public void setTipoBuscar(String tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    public String getStrBuscar() {
        return strBuscar;
    }

    public void setStrBuscar(String strBuscar) {
        this.strBuscar = strBuscar;
    }

    public Parte2 getParte() {
        return parte;
    }

    public void setParte(Parte2 parte) {
        this.parte = parte;
    }

    public Empaque getProducto() {
        return producto;
    }

    public void setProducto(Empaque producto) {
        this.producto = producto;
    }

    public ArrayList<Empaque> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Empaque> productos) {
        this.productos = productos;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public ArrayList<SelectItem> getListaGrupos() {
        return listaGrupos;
    }

    public void setListaGrupos(ArrayList<SelectItem> listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    public SubGrupo getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(SubGrupo subGrupo) {
        this.subGrupo = subGrupo;
    }

    public ArrayList<SelectItem> getListaSubGrupos() {
        return listaSubGrupos;
    }

    public void setListaSubGrupos(ArrayList<SelectItem> listaSubGrupos) {
        this.listaSubGrupos = listaSubGrupos;
    }

    public Empaque[] getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(Empaque[] seleccionados) {
        this.seleccionados = seleccionados;
    }

//    public EmpaqueDataModel getEmpaquesModelo() {
//        return empaquesModelo;
//    }
//
//    public void setEmpaquesModelo(EmpaqueDataModel empaquesModelo) {
//        this.empaquesModelo = empaquesModelo;
//    }
}
