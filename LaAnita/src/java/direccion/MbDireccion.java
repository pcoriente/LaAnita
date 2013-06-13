package direccion;

import direccion.dao.DAOAsentamiento;
import direccion.dao.DAODireccion;
import direccion.dao.DAOPais;
import direccion.dominio.Asentamiento;
import direccion.dominio.Direccion;
import direccion.dominio.Pais;
import direccion.to.TODireccion;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import utilerias.Utilerias;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbDireccion")
@SessionScoped
public class MbDireccion implements Serializable {
    private Direccion direccion;
    private Direccion respaldo;
    private List<SelectItem> listaPaises;
    private List<SelectItem> listaAsentamientos;
    private boolean editarAsentamiento;
    private DAODireccion dao;
    private String llama;
    
    public MbDireccion() {
        this.editarAsentamiento=true;
//        this.direccion = new Direccion();
        try {
            this.dao = new DAODireccion();
        } catch (NamingException ex) {
            Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
//        String destino=null;
        String calle=Utilerias.Acentos(this.direccion.getCalle().trim());
        String numeroExterior=Utilerias.Acentos(this.direccion.getNumeroExterior().trim());
        String numeroInterior=Utilerias.Acentos(this.direccion.getNumeroInterior().trim());
        String referencia=Utilerias.Acentos(this.direccion.getReferencia().trim());
        int idPais=this.direccion.getPais().getIdPais();
        String codigoPostal=this.direccion.getCodigoPostal().trim();
        String estado=Utilerias.Acentos(this.direccion.getEstado().trim());
        String municipio=Utilerias.Acentos(this.direccion.getMunicipio().trim());
        String localidad=Utilerias.Acentos(this.direccion.getLocalidad().trim());
        String colonia=Utilerias.Acentos(this.direccion.getColonia().trim());
        String numeroLocalizacion=this.direccion.getNumeroLocalizacion().trim();
        
        if(calle.isEmpty()) fMsg.setDetail("Se requiere la calle !!");
        else if(numeroExterior.isEmpty()) fMsg.setDetail("Se requiere el número exterior !!");
        else if(referencia.isEmpty()) fMsg.setDetail("Se requiere la referencia !!");
        else if(idPais == 0) fMsg.setDetail("Se requiere el pais !!");
        else if(codigoPostal.isEmpty()) fMsg.setDetail("Se requiere el códigoPostal");
        else if(estado.isEmpty()) fMsg.setDetail("Se requiere el estado");
        else if(municipio.isEmpty()) fMsg.setDetail("Se requiere el municipio");
        else {
            try {
                int idDireccion=this.direccion.getIdDireccion();
                if (idDireccion == 0) {
                    idDireccion=this.dao.agregar(calle, numeroExterior, numeroInterior, referencia, idPais, codigoPostal, estado, municipio, localidad, colonia, numeroLocalizacion);
                } else {
                    this.dao.modificar(this.direccion.getIdDireccion(), calle, numeroExterior, numeroInterior, referencia, idPais, codigoPostal, estado, municipio, localidad, colonia, numeroLocalizacion);
                }
                this.direccion=this.obtener(idDireccion);
                actualizaDireccion();
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("La dirección se grabó correctamente !!");
//                destino="cedis.mantenimiento";
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
                Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
//        return destino;
    }
    
    public void eliminar(int idDireccion) {
        try {
            this.dao.eliminar(idDireccion);
        } catch (SQLException ex) {
            Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String salir() {
        this.listaPaises=null;
//        restauraDireccion();
        return this.llama+".mantenimiento";
    }
    
//    public void cambioAsentamiento() {
//        Asentamiento selAsentamiento=this.direccion.getSelAsentamiento();
//        
//        if(selAsentamiento != null && !selAsentamiento.getCodAsentamiento().equals("0")) {
//            this.direccion.setEstado(selAsentamiento.getEstado());
//            this.direccion.setMunicipio(selAsentamiento.getMunicipio());
//            this.direccion.setLocalidad(selAsentamiento.getCiudad());
//            this.direccion.setColonia(selAsentamiento.toString());
//        }
//        this.editarAsentamiento = true;
//    }
    
    public String mttoDireccion(Direccion direccion, String llama) {
        copiaDireccion(direccion);
        this.respaldo=direccion;
        this.llama=llama;
        return "direccion.mantenimiento";
    }
    
    private void copiaDireccion(Direccion direccion) {
        this.direccion=new Direccion();
        this.direccion.setIdDireccion(direccion.getIdDireccion());
        this.direccion.setCalle(direccion.getCalle());
        this.direccion.setNumeroExterior(direccion.getNumeroExterior());
        this.direccion.setNumeroInterior(direccion.getNumeroInterior());
        this.direccion.setReferencia(direccion.getReferencia());
        this.direccion.setPais(direccion.getPais());
        this.direccion.setCodigoPostal(direccion.getCodigoPostal());
        this.direccion.setEstado(direccion.getEstado());
        this.direccion.setMunicipio(direccion.getMunicipio());
        this.direccion.setLocalidad(direccion.getLocalidad());
        this.direccion.setColonia(direccion.getColonia());
        this.direccion.setNumeroLocalizacion(direccion.getNumeroLocalizacion());
    }
    
    private void actualizaDireccion() {
        this.respaldo.setIdDireccion(this.direccion.getIdDireccion());
        this.respaldo.setCalle(this.direccion.getCalle());
        this.respaldo.setNumeroExterior(this.direccion.getNumeroExterior());
        this.respaldo.setNumeroInterior(this.direccion.getNumeroInterior());
        this.respaldo.setReferencia(this.direccion.getReferencia());
        this.respaldo.setPais(this.direccion.getPais());
        this.respaldo.setCodigoPostal(this.direccion.getCodigoPostal());
        this.respaldo.setEstado(this.direccion.getEstado());
        this.respaldo.setMunicipio(this.direccion.getMunicipio());
        this.respaldo.setLocalidad(this.direccion.getLocalidad());
        this.respaldo.setColonia(this.direccion.getColonia());
        this.respaldo.setNumeroLocalizacion(this.direccion.getNumeroLocalizacion());
    }
    
    public Direccion nuevaDireccion() {
        Pais p=new Pais();
        p.setIdPais(0);
        p.setPais("");
        
        Direccion dir=new Direccion();
        dir=new Direccion();
        dir.setIdDireccion(0);
        dir.setCalle("");
        dir.setNumeroExterior("");
        dir.setNumeroInterior("");
        dir.setReferencia("");
        dir.setPais(p);
        dir.setCodigoPostal("");
        dir.setEstado("");
        dir.setMunicipio("");
        dir.setLocalidad("");
        dir.setColonia("");
        dir.setNumeroLocalizacion("");
        return dir;
    }
    
    public Direccion obtener(int idDireccion) {
        Direccion dir=null;
        try {
            TODireccion toDir = this.dao.obtener(idDireccion);
            if(toDir == null) {
                dir=nuevaDireccion();
            } else {
                dir=convertir(toDir);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dir;
    }
    
    private Direccion convertir(TODireccion toDir) {
        Direccion dir=new Direccion();
        dir.setIdDireccion(toDir.getIdDireccion());
        dir.setCalle(toDir.getCalle());
        dir.setNumeroExterior(toDir.getNumeroExterior());
        dir.setNumeroInterior(toDir.getNumeroInterior());
        dir.setReferencia(toDir.getReferencia());
        try {
            DAOPais daoPais=new DAOPais();
            Pais pais=daoPais.obtener(toDir.getIdPais());
            if(pais == null) {
                pais=new Pais();
                pais.setIdPais(0);
                pais.setPais("");
            } else dir.setPais(pais);
        } catch (NamingException ex) {
            Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
        }
        dir.setCodigoPostal(toDir.getCodigoPostal());
        dir.setEstado(toDir.getEstado());
        dir.setMunicipio(toDir.getMunicipio());
        dir.setLocalidad(toDir.getLocalidad());
        dir.setColonia(toDir.getColonia());
        dir.setNumeroLocalizacion(toDir.getNumeroLocalizacion());
        return dir;
    }
    
    private List<SelectItem> obtenerAsentamientos(String codigoPostal) throws NamingException, SQLException {
        List<SelectItem> asentamientos = new ArrayList<SelectItem>();
        
        Asentamiento a0=new Asentamiento();
        a0.setCodAsentamiento("0");
        a0.setCodigoPostal("");
        a0.setTipo("");
        a0.setAsentamiento("");
        a0.setCodEstado("");
        a0.setEstado("");
        a0.setCodMunicipio("");
        a0.setMunicipio("");
        a0.setCiudad("");
        SelectItem cero=new SelectItem(a0, "Seleccione un asentamiento");
        asentamientos.add(cero);
        
        DAOAsentamiento daoAsenta=new DAOAsentamiento();
        Asentamiento[] aAsentamientos=daoAsenta.obtenerAsentamientos(codigoPostal);
        for (Asentamiento a : aAsentamientos) {
            asentamientos.add(new SelectItem(a, a.getTipo()+" "+a.getAsentamiento()));
        }
        return asentamientos;
    }
    
    public void buscarAsentamientos() {
        if(this.direccion.getPais().getIdPais() != 1) return;
        
        String codigoPostal=this.direccion.getCodigoPostal();
        if(!codigoPostal.isEmpty()) {
            try {
                this.listaAsentamientos=obtenerAsentamientos(codigoPostal);
                this.editarAsentamiento=false;
            } catch (NamingException ex) {
                Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<SelectItem> getListaAsentamientos() {
        return listaAsentamientos;
    }

    public void setListaAsentamientos(List<SelectItem> listaAsentamientos) {
        this.listaAsentamientos = listaAsentamientos;
    }
    
    public void actualizaAsentamiento() {
        Asentamiento nuevo=this.direccion.getSelAsentamiento();
//        this.direccion.setSelAsentamiento(nuevo);
        String[] localidades={"08", "15", "18", "20", "23", "24", "25", "26", "27", "28", "29", "32"};
        
        if(nuevo.getCodAsentamiento().equals("0")) {
            this.direccion.setEstado("");
            this.direccion.setMunicipio("");
            this.direccion.setLocalidad("");
            this.direccion.setColonia("");
        } else {
            this.direccion.setEstado(nuevo.getEstado());
            this.direccion.setMunicipio(nuevo.getMunicipio());
            if(nuevo.getCiudad().trim().isEmpty()) {
                boolean flag=false;
                for(String s:localidades) {
                    if(s.equals(nuevo.getcTipo())) {
                        flag=true;
                        break;
                    }
                }
                if(flag) {
                    this.direccion.setLocalidad(nuevo.toString());
                    this.direccion.setColonia("");
                } else {
                    this.direccion.setLocalidad("");
                    this.direccion.setColonia(nuevo.toString());
                }
            } else {
                this.direccion.setLocalidad(nuevo.getCiudad().trim());
                this.direccion.setColonia(nuevo.toString());
            }
        }
        this.editarAsentamiento = true;
    }
    
//    public void actualizaAsentamiento1(ValueChangeEvent event) {
//        Asentamiento nuevo=(Asentamiento)event.getNewValue();
////        this.direccion.setSelAsentamiento(nuevo);
//        String[] localidades={"08", "15", "18", "20", "23", "24", "25", "26", "27", "28", "29", "32"};
//        
//        if(nuevo.getCodAsentamiento().equals("0")) {
//            this.direccion.setEstado("");
//            this.direccion.setMunicipio("");
//            this.direccion.setLocalidad("");
//            this.direccion.setColonia("");
//        } else {
//            this.direccion.setEstado(nuevo.getEstado());
//            this.direccion.setMunicipio(nuevo.getMunicipio());
//            if(nuevo.getCiudad().trim().isEmpty()) {
//                boolean flag=false;
//                for(String s:localidades) {
//                    if(s.equals(nuevo.getcTipo())) {
//                        flag=true;
//                        break;
//                    }
//                }
//                if(flag) {
//                    this.direccion.setLocalidad(nuevo.toString());
//                    this.direccion.setColonia("");
//                } else {
//                    this.direccion.setLocalidad("");
//                    this.direccion.setColonia(nuevo.toString());
//                }
//            } else {
//                this.direccion.setLocalidad(nuevo.getCiudad().trim());
//                this.direccion.setColonia(nuevo.toString());
//            }
//        }
//        this.editarAsentamiento = true;
//    }
    
    public boolean isEditarAsentamiento() {
        return editarAsentamiento;
    }
    
    public void setEditarAsentamiento(boolean editarAsentamiento) {
//        String[] localidades={"08", "15", "18", "20", "23", "24", "25", "26", "27", "28", "29", "32"};
//        Asentamiento selAsentamiento=this.direccion.getSelAsentamiento();
//        
//        if(selAsentamiento != null && !selAsentamiento.getCodAsentamiento().equals("0")) {
//            this.direccion.setEstado(selAsentamiento.getEstado());
//            this.direccion.setMunicipio(selAsentamiento.getMunicipio());
////            this.direccion.setLocalidad(selAsentamiento.getCiudad());
////            this.direccion.setColonia(selAsentamiento.toString());
//            if(selAsentamiento.getCiudad().trim().isEmpty()) {
//                boolean flag=false;
//                for(String s:localidades) {
//                    if(s.equals(selAsentamiento.getcTipo())) {
//                        flag=true;
//                        break;
//                    }
//                }
//                if(flag) {
//                    this.direccion.setLocalidad(selAsentamiento.toString());
//                    this.direccion.setColonia("");
//                } else {
//                    this.direccion.setLocalidad("");
//                    this.direccion.setColonia(selAsentamiento.toString());
//                }
//            } else {
//                this.direccion.setLocalidad(selAsentamiento.getCiudad().trim());
//                this.direccion.setColonia(selAsentamiento.toString());
//            }
//        }
        
        if(!editarAsentamiento && (this.listaAsentamientos == null || this.listaAsentamientos.isEmpty())) {
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "No hay asentamientos en la lista, proporcione un código postal y de click al botón BUSCAR");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            editarAsentamiento=true;
        }
        this.editarAsentamiento = editarAsentamiento;
    }

    public List<SelectItem> getListaPaises() {
        if(this.listaPaises == null) {
            try {
                this.listaPaises=obtenerPaises();
            } catch (NamingException ex) {
                Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
            }catch (SQLException ex) {
                Logger.getLogger(MbDireccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listaPaises;
    }
    
    private List<SelectItem> obtenerPaises() throws NamingException, SQLException {
        List<SelectItem> paises = new ArrayList<SelectItem>();
        
        Pais p0=new Pais();
        p0.setIdPais(0);
        p0.setPais("Seleccione un país");
        SelectItem cero=new SelectItem(p0, p0.getPais());
        paises.add(cero);
        
        DAOPais daoPais=new DAOPais();
        Pais[] aPaises=daoPais.obtenerPaises();
        for (Pais p : aPaises) {
            paises.add(new SelectItem(p, p.getPais()));
        }
        return paises;
    }

    public void setListaPaises(List<SelectItem> listaPaises) {
        this.listaPaises = listaPaises;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}
