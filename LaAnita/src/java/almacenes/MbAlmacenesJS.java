package almacenes;

import almacenes.dao.DAOAlmacenesJS;
import almacenes.dominio.AlmacenJS;
import almacenes.to.TOAlmacenJS;
import cedis.MbMiniCedis;
import direccion.dominio.Direccion;
import empresas.MbMiniEmpresas;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author jesc
 */
@Named(value = "mbAlmacenesJS")
@SessionScoped
public class MbAlmacenesJS implements Serializable {
    private AlmacenJS almacen;
    private TOAlmacenJS toAlmacen;
    private ArrayList<AlmacenJS> almacenes;
    private ArrayList<SelectItem> listaAlmacenes;
    private DAOAlmacenesJS dao;
    
    @ManagedProperty(value = "#{mbMiniCedis}")
    private MbMiniCedis mbCedis;
    @ManagedProperty(value = "#{mbMiniEmpresas}")
    private MbMiniEmpresas mbEmpresas;
    
    public MbAlmacenesJS() throws NamingException {
        this.almacen=new AlmacenJS();
        this.toAlmacen=new TOAlmacenJS();
        
        this.mbEmpresas=new MbMiniEmpresas();
        this.mbCedis=new MbMiniCedis();
    }
    
    public void inicializaAlmacen() {
        this.almacen=new AlmacenJS();
    }
    
    public void cargaAlmacenes() {
        this.cargaListaAlmacenes();
        this.almacen=new AlmacenJS();
    }
    
    public void cargaListaAlmacenes() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "cargaListaAlmacenes");
        this.listaAlmacenes=new ArrayList<SelectItem>();
        try {
            TOAlmacenJS a0=new TOAlmacenJS();
            a0.setIdAlmacen(0);
            a0.setAlmacen("Seleccione un almacen");
            this.listaAlmacenes.add(new SelectItem(a0, a0.toString()));
            
            if(this.mbCedis.getCedis().getIdCedis()!=0) {
                this.dao = new DAOAlmacenesJS();
                for (TOAlmacenJS a: this.dao.obtenerAlmacenes(this.mbCedis.getCedis().getIdCedis())) {
                    listaAlmacenes.add(new SelectItem(a, a.toString()));
                }
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
    
    private AlmacenJS convertir(TOAlmacenJS to) {
        AlmacenJS a=new AlmacenJS();
        a.setIdAlmacen(to.getIdAlmacen());
        a.setAlmacen(to.getAlmacen());
        a.setEmpresa(this.mbEmpresas.obtenerEmpresa(to.getIdEmpresa()));
        a.setCedis(this.mbCedis.obtenerCedis(to.getIdCedis()));
        a.setDireccion(new Direccion());
        a.getDireccion().setIdDireccion(to.getIdDireccion());
        return a;
    }
    
    public AlmacenJS obtenerAlmacen(int idAlmacen) {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "obtenerAlmacen");
        AlmacenJS a=null;
        try {
            this.dao=new DAOAlmacenesJS();
            a=convertir(this.dao.obtenerAlmacen(idAlmacen));
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
        return a;
    }
    
//    public boolean grabar() {
//        boolean ok = true;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if (this.almacen.getAlmacen().isEmpty()) {
//            fMsg.setDetail("Se requiere el nombre del almacén !!");
//        } else if (this.almacen.getDireccion().getIdDireccion() == 0) {
//            fMsg.setDetail("Se requiere la dirección del almacén !!");
//        } else {
//            try {
//                this.dao = new DAOAlmacenesJS();
//                if (this.almacen.getIdAlmacen() == 0) {
//                    this.almacen.setIdAlmacen(this.dao.agregar(this.convertirTO(almacen)));
//                } else {
//                    this.dao.modificar(this.convertirTO(almacen));
//                }
//                ok = false;
//            } catch (SQLException ex) {
//                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//            } catch (NamingException ex) {
//                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//                fMsg.setDetail(ex.getMessage());
//            }
//        }
//        if (!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        return ok;
//    }
//    
//    private TOAlmacenJS convertirTO(AlmacenJS almacen) {
//        TOAlmacenJS to=new TOAlmacenJS();
//        to.setIdAlmacen(almacen.getIdAlmacen());
//        to.setAlmacen(almacen.getAlmacen());
//        to.setIdEmpresa(almacen.getEmpresa().getIdEmpresa());
//        to.setIdCedis(almacen.getCedis().getIdCedis());
//        to.setIdDireccion(almacen.getDireccion().getIdDireccion());
//        return to;
//    }
    
//    public AlmacenJS obtenerAlmacen(int idAlmacen) {
//        boolean ok = true;
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        this.almacen=null;
//        try {
//            this.dao=new DAOAlmacenesJS();
//            TOAlmacenJS to=this.dao.obtenerAlmacen(idAlmacen);
//            if(to==null) {
//                fMsg.setDetail("No se encontro el almacen solicitado");
//            } else {
//                this.almacen=convertir(to);
//                ok=true;
//            }
//        } catch (SQLException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//        } catch (NamingException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getMessage());
//        }
//        if (!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        }
//        return almacen;
//    }
//    
//    private AlmacenJS convertir(TOAlmacenJS to) {
//        AlmacenJS a=new AlmacenJS();
//        a.setIdAlmacen(to.getIdAlmacen());
//        a.setAlmacen(to.getAlmacen());
//        a.setEmpresa(this.mbEmpresas.getEmpresa());
//        a.setCedis(this.mbCedis.getCedis());
//        a.setDireccion(new Direccion());
//        a.getDireccion().setIdDireccion(to.getIdDireccion());
//        return a;
//    }

    public AlmacenJS getAlmacen() {
        return almacen;
    }

    public void setAlmacen(AlmacenJS almacen) {
        this.almacen = almacen;
    }

    public ArrayList<SelectItem> getListaAlmacenes() {
        if(this.listaAlmacenes==null) {
            this.cargaListaAlmacenes();
        }
        return listaAlmacenes;
    }

    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
        this.listaAlmacenes = listaAlmacenes;
    }

    public MbMiniCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbMiniCedis mbCedis) {
        this.mbCedis = mbCedis;
    }

    public MbMiniEmpresas getMbEmpresas() {
        return mbEmpresas;
    }

    public void setMbEmpresas(MbMiniEmpresas mbEmpresas) {
        this.mbEmpresas = mbEmpresas;
    }

    public ArrayList<AlmacenJS> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(ArrayList<AlmacenJS> almacenes) {
        this.almacenes = almacenes;
    }

    public TOAlmacenJS getToAlmacen() {
        return toAlmacen;
    }

    public void setToAlmacen(TOAlmacenJS toAlmacen) {
        this.toAlmacen = toAlmacen;
    }
}
