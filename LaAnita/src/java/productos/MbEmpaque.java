package productos;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.dao.DAOEmpaques;
import productos.dao.DAOMarcas;
import productos.dao.DAOUnidadesEmpaque;
import productos.dominio.Empaque;
import productos.dominio.Marca;
import productos.dominio.Producto;
import productos.dominio.SubEmpaque;
import productos.dominio.UnidadEmpaque;
import productos.dominio.Upc;

/**
 *
 * @author JULIOS
 */
@ManagedBean(name = "mbEmpaque")
@SessionScoped
public class MbEmpaque implements Serializable {

    //private boolean ok;
    private String cod_emp;
    private Empaque empaque;
    private Empaque selEpq;
    private ArrayList<Empaque> empaques;
    
    
    @ManagedProperty(value = "#{mbUnidadEmpaque}")
    private MbUnidadEmpaque mbUnidadEmpaque;
    
    private ArrayList<SelectItem> listaUpcs;
    private ArrayList<SelectItem> listaUnidades;
    private ArrayList<SelectItem> listaSubEmpaques;
    //private boolean old;
    private DAOEmpaques dao;

    public MbEmpaque() {
        //try {
        //this.mbProducto = new MbProducto();
        //this.mbBuscar = new MbBuscarProd();
        //this.mbMarca = new MbMarca();
        this.mbUnidadEmpaque = new MbUnidadEmpaque();
        this.empaque = new Empaque(0);
        //this.old=false;
        this.cod_emp = "";    // Sirve para el traspaso en la captura de catalogo viejo a nuevo.
        //this.ok = true;
        //this.dao = new DAOEmpaques();
        //} catch (NamingException ex) {
        //    Logger.getLogger(DAOEmpaques.class.getName()).log(Level.SEVERE, null, ex);
        //}
    }
    
    public void nuevoEmpaque(Producto producto) {
        this.empaque=new Empaque(0);
        this.empaque.setProducto(producto);
    }

    public String salir() {
        String destino = "productosOld.menu";
        this.empaque = new Empaque(0);
        //this.mbProducto = new MbProducto();
        //this.mbMarca = new MbMarca();
        this.mbUnidadEmpaque = new MbUnidadEmpaque();
        return destino;
    }
    
    public void actualizarEmpaque() {
        this.empaque=this.selEpq;
    }
    
    public void eliminarUnidadEmpaque() {
        if(this.mbUnidadEmpaque.eliminar()) {
            this.empaque.setUnidadEmpaque(this.mbUnidadEmpaque.getUnidad());
            this.listaUnidades=null;
        }
    }
    
    public void grabarUnidadEmpaque() {
        if(this.mbUnidadEmpaque.grabar()) {
            this.empaque.setUnidadEmpaque(this.mbUnidadEmpaque.getUnidad());
            this.listaUnidades=null;
        }
    }
    
    public void mttoUnidadesEmpaque() {
        if(this.empaque.getUnidadEmpaque().getIdUnidad()==0) {
            this.mbUnidadEmpaque.setUnidad(new UnidadEmpaque(0, "", ""));
        } else {
            this.mbUnidadEmpaque.copia(this.empaque.getUnidadEmpaque());
        }
    }

    public void cargaListaSubEmpaques() {
        boolean oki = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");

        this.listaSubEmpaques = new ArrayList<SelectItem>();
        SubEmpaque subEpq = new SubEmpaque(0, 0, new UnidadEmpaque(0, "SELECCIONE UN SUBEMPAQUE", ""));
        this.listaSubEmpaques.add(new SelectItem(subEpq, subEpq.toString()));

        try {
            this.dao = new DAOEmpaques();
            ArrayList<SubEmpaque> lstSubEmpaques = dao.obtenerListaSubEmpaques(this.empaque.getProducto().getIdProducto(), this.empaque.getPiezas());
            for (SubEmpaque u : lstSubEmpaques) {
                if (u.getIdEmpaque() != this.empaque.getIdEmpaque()) {
                    listaSubEmpaques.add(new SelectItem(u, u.toString()));
                }
            }
            oki = true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public void eliminar() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOEmpaques();
            if(this.dao.eliminar(this.empaque.getIdEmpaque())) {
                fMsg.setDetail("El empaque se ha eliminado con exito");
                this.empaque.setIdEmpaque(0);
                this.empaque.setCod_pro("");
                this.empaque.setDun14("");
                this.empaque.setPeso(0);
                this.empaque.setPiezas(1);
                this.empaque.setSubEmpaque(new SubEmpaque(0));
                this.empaque.setUnidadEmpaque(new UnidadEmpaque());
                this.empaque.setVolumen(0);
                ok=true;
            } else {
                fMsg.setDetail("El empaque no se puede eliminar, esta contenido en otro empaque !!!");
            }
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        context.addCallbackParam("okBuscar", ok);
    }

    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");

        if (this.empaque.getCod_pro().equals("")) {
            fMsg.setDetail("Se requiere una parte !!");
        } else if (this.empaque.getProducto() == null || this.empaque.getProducto().getIdProducto() == 0) {
            fMsg.setDetail("Se requiere un producto !!");
        //} else if (this.empaque.getMarca() == null || this.empaque.getMarca().getIdMarca() == 0) {
        //    fMsg.setDetail("Se requiere una marca !!");
        } else if (this.empaque.getUnidadEmpaque() == null || this.empaque.getUnidadEmpaque().getIdUnidad() == 0) {
            fMsg.setDetail("Se requiere la unidad de empaque !!");
        } else if (this.empaque.getPiezas() <= 0) {
            fMsg.setDetail("Las piezas deben ser mayor que cero !!");
        } else {
            try {
                this.dao = new DAOEmpaques();
                if (this.empaque.getIdEmpaque() == 0) {
                    this.empaque.setIdEmpaque(this.dao.agregar(this.cod_emp, this.empaque));
                } else {
                    this.dao.modificar(this.cod_emp, this.empaque);
                }
                this.cod_emp="";
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("El producto se grabó correctamente !!");
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void piezasUnidad() {
        this.empaque.setPiezas(1);
        if (this.empaque.getProducto().getIdProducto() > 0) {
            this.empaque.setSubEmpaque(new SubEmpaque(0, 0, new UnidadEmpaque(0, "SELECCIONE UN EMPAQUE", "")));
            this.cargaListaSubEmpaques();
        }
    }

    public void ponProducto() {
        Producto prod = new Producto(0);
        this.empaque.setProducto(prod);
    }

    public Empaque obtenerEmpaque(String cod_pro) {
        boolean oki=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        Empaque epq = null;
        try {
            this.dao=new DAOEmpaques();
            epq = this.dao.obtenerEmpaque(cod_pro);
            oki=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return epq;
    }
    
    public void buscarEmpaques() {
        boolean oki=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            this.dao=new DAOEmpaques();
            if(this.empaque.getProducto().getIdProducto()==0) {
                fMsg.setDetail("Se requiere tener un producto");
            } else {
                this.empaques=this.dao.obtenerEmpaques(this.empaque.getProducto().getIdProducto());
                if(this.empaques.isEmpty()) {
                    fMsg.setDetail("No se encontraron empaques con el producto seleccionado");
                } else {
                    this.selEpq=null;
                    oki=true;
                }
            }
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okEmpaque", oki);
    }
    
    public void cargaListaUpcs() {
        this.listaUpcs=new ArrayList<SelectItem>();
        if(this.empaque.getProducto().getUpcs().isEmpty()) {
            Upc xUpc=new Upc(this.empaque.getProducto().getIdProducto());
            this.listaUpcs.add(new SelectItem(xUpc, xUpc.toString()));
        } else {
            for(Upc u: this.empaque.getProducto().getUpcs()) {
                this.listaUpcs.add(new SelectItem(u, u.toString()));
            }
        }
    }

    public void cargaUnidadesEmpaque() {
        boolean oki=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        this.listaUnidades = new ArrayList<SelectItem>();
        UnidadEmpaque unidad = new UnidadEmpaque(0, "SELECCIONE UN EMPAQUE", "");
        this.listaUnidades.add(new SelectItem(unidad, unidad.toString()));

        try {
            DAOUnidadesEmpaque daoUnidad = new DAOUnidadesEmpaque();
            ArrayList<UnidadEmpaque> lstUnidades = daoUnidad.obtenerUnidadesEmpaque();
            for (UnidadEmpaque u : lstUnidades) {
                listaUnidades.add(new SelectItem(u, u.toString()));
            }
            oki=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!oki) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public Empaque getEmpaque() {
        return empaque;
    }

    public void setEmpaque(Empaque empaque) {
        this.empaque = empaque;
    }
    /*
    public ArrayList<Empaque> getEmpaques() {
        return empaques;
    }

    public void setEmpaques(ArrayList<Empaque> empaques) {
        this.empaques = empaques;
    }
    * */
    /*
    public MbProducto getMbProducto() {
        return mbProducto;
    }

    public void setMbProducto(MbProducto mbProducto) {
        this.mbProducto = mbProducto;
    }
    */
    public ArrayList<SelectItem> getListaUnidades() {
        if (this.listaUnidades == null) {
            this.cargaUnidadesEmpaque();
        }
        return listaUnidades;
    }

    public void setListaUnidades(ArrayList<SelectItem> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public ArrayList<SelectItem> getListaSubEmpaques() {
        if (this.listaSubEmpaques == null) {
            this.cargaListaSubEmpaques();
        }
        return listaSubEmpaques;
    }

    public void setListaSubEmpaques(ArrayList<SelectItem> listaSubEmpaques) {
        this.listaSubEmpaques = listaSubEmpaques;
    }
    /*
    public MbBuscarProd getMbBuscarProd() {
        return mbBuscarProd;
    }

    public void setMbBuscarProd(MbBuscarProd mbBuscarProd) {
        this.mbBuscarProd = mbBuscarProd;
    }
    * */
    /*
    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
    
     public boolean isOld() {
        return old;
     }

     public void setOld(boolean old) {
        this.old = old;
     }
     * */

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public ArrayList<SelectItem> getListaUpcs() {
        return listaUpcs;
    }

    public void setListaUpcs(ArrayList<SelectItem> listaUpcs) {
        this.listaUpcs = listaUpcs;
    }

    public MbUnidadEmpaque getMbUnidadEmpaque() {
        return mbUnidadEmpaque;
    }

    public void setMbUnidadEmpaque(MbUnidadEmpaque mbUnidadEmpaque) {
        this.mbUnidadEmpaque = mbUnidadEmpaque;
    }

    public ArrayList<Empaque> getEmpaques() {
        return empaques;
    }

    public void setEmpaques(ArrayList<Empaque> empaques) {
        this.empaques = empaques;
    }

    public Empaque getSelEpq() {
        return selEpq;
    }

    public void setSelEpq(Empaque selEpq) {
        this.selEpq = selEpq;
    }
}
