package impuestos;

import impuestos.dao.DAOImpuestosDetalle;
import impuestos.dominio.ImpuestoDetalle;
import impuestos.dominio.ImpuestoGrupo;
import impuestos.dominio.ImpuestoZona;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import utilerias.Utilerias;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbDetalle")
@SessionScoped
public class FrmImpuestos implements Serializable {
    private ImpuestoZona zona;
    private ImpuestoGrupo grupo;
    private ImpuestoDetalle detalle;
    private ArrayList<ImpuestoDetalle> detalles;
    private String periodo;
    private boolean soloLectura;
    
    @ManagedProperty(value = "#{mbZonas}")
    private MbZonas mbZonas;
    @ManagedProperty(value = "#{mbGrupos}")
    private MbGrupos mbGrupos;
    @ManagedProperty(value = "#{mbImpuestos}")
    private MbImpuestos mbImpuestos;
    
    private Date fechaTope;
    private DAOImpuestosDetalle dao;

    public FrmImpuestos() {
        this.mbZonas = new MbZonas();
        this.mbGrupos = new MbGrupos();
        this.mbImpuestos = new MbImpuestos();
        this.periodo = "1";
        this.soloLectura = true;
        this.detalles = new ArrayList<ImpuestoDetalle>();
        //this.fechaTope = Utilerias.hoy();
    }
    
    public String salir() {
        String destino="index.xhtml";
        this.zona=new ImpuestoZona(0, "");
        this.mbZonas.setListaZonas(null);
        this.grupo=new ImpuestoGrupo(0, "");
        this.mbGrupos.setListaGrupos(null);
        this.detalle=null;
        this.detalles=null;
        this.periodo="1";
        this.soloLectura=true;
        return destino;
    }

    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.zona==null || this.zona.getIdZona()==0) {
            fMsg.setDetail("Seleccione una zona !!");
        } else if (this.grupo==null || this.grupo.getIdGrupo()==0) {
            fMsg.setDetail("Seleccione un grupo !!");
        } else if (this.detalle==null || this.detalle.getImpuesto().getIdImpuesto() == 0) {
            fMsg.setDetail("Seleccione un impuesto de la lista !!");
        } else {
            try {
                this.dao = new DAOImpuestosDetalle();
                this.detalles=this.dao.grabar(zona.getIdZona(), grupo.getIdGrupo(), this.detalle, this.periodo);
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La operación se realizó con éxito !!");
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void eliminarPeriodo() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.zona == null || this.zona.getIdZona() == 0) {
            fMsg.setDetail("Seleccione una zona !!");
        } else if (this.grupo == null || this.grupo.getIdGrupo() == 0) {
            fMsg.setDetail("Seleccione un grupo !!");
        } else if (this.periodo.equals("1")) {
            fMsg.setDetail("No se puede eliminar el período actual !!");
        } else {
            try {
                this.dao = new DAOImpuestosDetalle();
                this.dao.eliminarPeriodo(this.zona.getIdZona(), this.grupo.getIdGrupo());
                this.detalles = new ArrayList<ImpuestoDetalle>();
                this.detalle=null;
                
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La operación se realizó con éxito !!");
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void crearPeriodo() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.zona == null || this.zona.getIdZona() == 0) {
            fMsg.setDetail("Seleccione una zona !!");
        } else if (this.grupo == null || this.grupo.getIdGrupo() == 0) {
            fMsg.setDetail("Seleccione un grupo !!");
        } else if (this.periodo.equals("1")) {
            fMsg.setDetail("No se puede crear un período actual, debe ser uno siguiente !!");
        } else if (this.detalles.isEmpty()) {
            try {
                Date fechaInicial = Utilerias.addDays(fechaTope, 1);

                this.dao = new DAOImpuestosDetalle();
                this.detalles = this.dao.crearPeriodo(this.zona.getIdZona(), this.grupo.getIdGrupo(), this.periodo, new java.sql.Date(fechaInicial.getTime()));
                this.detalle=null;
                
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La operación se realizó con éxito !!");
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        } else {
            fMsg.setDetail("Ya existe un período siguiente, modifique o elimine y velva a crear !!");
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void cargarDetalles(int idZona, int idGrupo) {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.periodo == null) {
            this.periodo = "1";
        }
        try {
            if (idZona == 0 || idGrupo == 0) {
                this.detalles = new ArrayList<ImpuestoDetalle>();
            } else {
                this.dao = new DAOImpuestosDetalle();
                this.detalles = this.dao.obtenerDetalles(idZona, idGrupo, this.periodo);
            }
            ok = true;
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public void cambiarGrupo() {
        this.periodo="2";
        this.cargarImpuestosDetalle();
        this.periodo="1";
        this.cambiarPeriodo();
    }
    
    public void seleccionarGrupo() {
        if(!this.grupo.equals(this.mbGrupos.getGrupo())) {
            this.grupo=this.mbGrupos.getGrupo();
            this.cambiarGrupo();
        }
    }
    
    public void eliminarGrupo() {
        if (this.mbGrupos.eliminar()) {
            this.grupo=this.mbGrupos.getGrupo();
            this.cambiarGrupo();
        }
    }
    
    public void mttoGrupos() {
        ImpuestoGrupo g;
        if (this.grupo == null) {
            g = new ImpuestoGrupo(0, "");
        } else {
            g = new ImpuestoGrupo(this.grupo.getIdGrupo(), this.grupo.getGrupo());
        }
        this.mbGrupos.setGrupo(g);
        this.mbGrupos.setImpuestosAgregados(this.mbGrupos.obtenerImpuestosAgregados());
        this.mbGrupos.setImpuestosDisponibles(this.mbGrupos.obtenerImpuestosDisponibles());
    }
    
    private void cargarImpuestosDetalle() {
        int idZona = 0;
        if (this.zona != null) {
            idZona = this.zona.getIdZona();
        }
        int idGrupo = 0;
        if (this.grupo != null) {
            idGrupo = this.grupo.getIdGrupo();
        }
        this.cargarDetalles(idZona, idGrupo);
    }
    
    public void cambiaPeriodo() {
        if (this.periodo == null) {
            this.periodo = "1";
        }
        if (this.periodo.equals("1")) { // Si me estoy cambiando al periodo 1
            if (this.detalles == null || this.detalles.isEmpty()) { // y el periodo 2 esta vacío
                this.fechaTope=null;
                this.soloLectura = false;   // puedo editar la fecha final
            } else {    // Si no esta vacío el periodo 2
                //Date hoy = Utilerias.hoy();
                this.fechaTope=this.detalles.get(0).getFechaInicial();  // Fecha inicial periodo 2

                //if (Utilerias.addDays(hoy, 1).equals(this.fechaTope)) {
                //    this.soloLectura = true;    // No puedo editar la fecha final
                //} else {
                //    this.soloLectura = false;    // puedo editar fecha final. Tengo tope la fecha inicial del periodo 2
                //}
                this.soloLectura = true;  // No se puede editar ningun campo
            }
        } else if (this.detalles == null || this.detalles.isEmpty()) {  // Si el periodo uno esta vacio
            this.soloLectura = false;   // Se pueden editar todos los campos
            try {
                this.fechaTope = Utilerias.hoy();   // Como tope para fecha inicial es HOY
            } catch (Exception ex) {
                Logger.getLogger(FrmImpuestos.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.soloLectura = true;    // No se puede editar la fecha inicial
            this.fechaTope=null;
            //this.setFechaTope(this.detalles.get(0).getFechaFinal());
        }
    }
    
    public void cambiarPeriodo() {
        this.cambiaPeriodo();
        this.cargarImpuestosDetalle();
        this.setSoloLectura(true);
        this.detalle=null;
    }
    
    public void eliminarZona() {
        if(this.mbZonas.eliminar()) {
            this.zona=this.mbZonas.getZona();
        }
    }
    
    public void grabarZona() {
        if (this.mbZonas.grabar()) {
            this.zona=this.mbZonas.getZona();
        }
    }
    
    public void mttoZonas() {
        ImpuestoZona z = new ImpuestoZona(this.zona.getIdZona(), this.zona.getZona());
        this.mbZonas.setZona(z);
    }
    
    public void cambiarZona() {
        this.periodo="2";
        this.cargarImpuestosDetalle();
        this.periodo="1";
        this.cambiarPeriodo();
    }

    public ImpuestoZona getZona() {
        return zona;
    }

    public void setZona(ImpuestoZona zona) {
        this.zona = zona;
    }

    public ImpuestoGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(ImpuestoGrupo grupo) {
        this.grupo = grupo;
    }

    public ImpuestoDetalle getDetalle() {
        return detalle;
    }

    public void setDetalle(ImpuestoDetalle detalle) {
        this.detalle = detalle;
    }

    public ArrayList<ImpuestoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<ImpuestoDetalle> detalles) {
        this.detalles = detalles;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
    }

    public MbZonas getMbZonas() {
        return mbZonas;
    }

    public void setMbZonas(MbZonas mbZonas) {
        this.mbZonas = mbZonas;
    }

    public MbGrupos getMbGrupos() {
        return mbGrupos;
    }

    public void setMbGrupos(MbGrupos mbGrupos) {
        this.mbGrupos = mbGrupos;
    }

    public MbImpuestos getMbImpuestos() {
        return mbImpuestos;
    }

    public void setMbImpuestos(MbImpuestos mbImpuestos) {
        this.mbImpuestos = mbImpuestos;
    }

    public Date getFechaTope() {
        return fechaTope;
    }

    public void setFechaTope(Date fechaTope) {
        this.fechaTope = fechaTope;
    }
}
