package productos;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOPartes;
import productos.dao.DAOProductos;
import productos.dominio.Grupo;
import productos.dominio.Parte2;
import productos.dominio.ProdStr;
import productos.dominio.Producto;
import productos.dominio.SubGrupo;
import productos.dominio.Tipo;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbBuscarProd")
@SessionScoped
public class MbBuscarProd implements Serializable {
    private String tipoBuscar;
    private String strBuscar;
    private Parte2 parte;
    private ProdStr prodStr;
    private Producto producto;
    private ArrayList<ProdStr> productos;
    private ArrayList<ProdStr> filtrados;
    private SelectItem[] arrayTipos;
    private SelectItem[] arrayGrupos;
    private SelectItem[] arraySubGrupos;
    private int borrarVariable;
    public MbBuscarProd() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.tipoBuscar = "2";
        this.strBuscar = "";
        this.parte = new Parte2(0, "");
        this.producto = new Producto(0);
        this.prodStr=null;
        this.productos = null;
        this.filtrados = null;
        this.arrayTipos = new SelectItem[1];
        this.arrayTipos[0] = new SelectItem("", "Seleccione un tipo");
        this.arrayGrupos = new SelectItem[1];
        this.arrayGrupos[0] = new SelectItem("", "Seleccione un grupo");
        this.arraySubGrupos = new SelectItem[1];
        this.arraySubGrupos[0] = new SelectItem("", "Seleccione un subgrupo");
    }
    
    public void inicializar() {
        this.inicializa();
    }

    public Producto obtenerSeleccionado() {
        try {
            DAOProductos dao = new DAOProductos();
            this.producto = dao.obtenerProducto(this.prodStr.getIdProducto());
        } catch (NamingException ex) {
            Logger.getLogger(MbBuscarProd.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbBuscarProd.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.producto;
    }
    
    public void buscarLista() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            DAOProductos dao = new DAOProductos();
            if (this.tipoBuscar.equals("1")) {
                this.producto = dao.obtenerProducto(this.strBuscar);
                if (this.producto == null) {
                    fMsg.setDetail("No se encontró producto con el código de barras proporcionado");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                } else {
                    ok = true;
                }
            } else {
                this.producto = null;
                this.productos = new ArrayList<ProdStr>();
                ArrayList<Tipo> lstTipos = new ArrayList<Tipo>();
                ArrayList<Grupo> lstGrupos = new ArrayList<Grupo>();
                ArrayList<SubGrupo> lstSubGrupos = new ArrayList<SubGrupo>();
                for (Producto p : dao.obtenerProductos(this.parte.getIdParte())) {
                    if (lstTipos.indexOf(p.getTipo()) == -1) {
                        lstTipos.add(p.getTipo());
                    }
                    if (lstGrupos.indexOf(p.getGrupo()) == -1) {
                        lstGrupos.add(p.getGrupo());
                    }
                    if (lstSubGrupos.indexOf(p.getSubGrupo()) == -1) {
                        lstSubGrupos.add(p.getSubGrupo());
                    }
                    this.productos.add(new ProdStr(p.getIdProducto(), p.getTipo().getTipo(), p.getGrupo().getGrupo(), p.getSubGrupo().getSubGrupo(), p.toString()));
                }
                int i = 0;
                this.arrayTipos = new SelectItem[lstTipos.size() + 1];
                this.arrayTipos[i++] = new SelectItem("", "Seleccione un tipo");
                for (Tipo t : lstTipos) {
                    this.arrayTipos[i++] = new SelectItem(t.getTipo(), t.getTipo());
                }
                i = 0;
                this.arrayGrupos = new SelectItem[lstGrupos.size() + 1];
                this.arrayGrupos[i++] = new SelectItem("", "Seleccione un grupo");
                for (Grupo g : lstGrupos) {
                    this.arrayGrupos[i++] = new SelectItem(g.getGrupo(), g.getGrupo());
                }
                i = 0;
                this.arraySubGrupos = new SelectItem[lstSubGrupos.size() + 1];
                this.arraySubGrupos[i++] = new SelectItem("", "Seleccione un subGrupo");
                for (SubGrupo sg : lstSubGrupos) {
                    this.arraySubGrupos[i++] = new SelectItem(sg.getSubGrupo(), sg.getSubGrupo());
                }
                if(this.productos.isEmpty()) {
                    fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                    fMsg.setDetail("No se encontraron productos en la busqueda2");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                }
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

    public ArrayList<Parte2> completePartes(String query) {
        ArrayList<Parte2> partes = null;
        try {
            DAOPartes dao = new DAOPartes();
            partes = dao.completePartes(query);
        } catch (SQLException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return partes;
    }
    
    public void verCambio() {
        if (this.tipoBuscar.equals("2")) {
            this.parte = new Parte2(0, "");
        } else {
            this.strBuscar = "";
        }
        this.prodStr=null;
        this.productos=null;
        this.filtrados=null;
        this.arrayTipos=new SelectItem[0];
        this.arrayGrupos=new SelectItem[0];
        this.arraySubGrupos=new SelectItem[0];
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

    public ProdStr getProdStr() {
        return prodStr;
    }

    public void setProdStr(ProdStr prodStr) {
        this.prodStr = prodStr;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ArrayList<ProdStr> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<ProdStr> productos) {
        this.productos = productos;
    }

    public ArrayList<ProdStr> getFiltrados() {
        return filtrados;
    }

    public void setFiltrados(ArrayList<ProdStr> filtrados) {
        this.filtrados = filtrados;
    }

    public SelectItem[] getArrayTipos() {
        return arrayTipos;
    }

    public void setArrayTipos(SelectItem[] arrayTipos) {
        this.arrayTipos = arrayTipos;
    }

    public SelectItem[] getArrayGrupos() {
        return arrayGrupos;
    }

    public void setArrayGrupos(SelectItem[] arrayGrupos) {
        this.arrayGrupos = arrayGrupos;
    }

    public SelectItem[] getArraySubGrupos() {
        return arraySubGrupos;
    }

    public void setArraySubGrupos(SelectItem[] arraySubGrupos) {
        this.arraySubGrupos = arraySubGrupos;
    }
}
