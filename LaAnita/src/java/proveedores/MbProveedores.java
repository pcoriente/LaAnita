package proveedores;

import contactos.MbContactos;
import contactos.MbTelefonos;
import contactos.dominio.Contacto;
import contactos.dominio.Telefono;
import contactos.dominio.TelefonoTipo;
import contribuyentes.Contribuyente;
import contribuyentes.MbBuscarContribuyente;
import contribuyentes.MbContribuyentes;
import direccion.MbDireccion;
import impuestos.MbZonas;
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
import proveedores.dao.DAOProveedores;
import proveedores.dominio.Clasificacion;
import proveedores.dominio.Proveedor;
import proveedores.dominio.SubClasificacion;
import proveedores.dominio.TipoOperacion;
import proveedores.dominio.TipoTercero;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;
import utilerias.Utilerias;
/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbProveedores")
@SessionScoped
public class MbProveedores implements Serializable {
    private int idTipo=1;
    private Proveedor proveedor;
    private Contacto contacto;
    private Telefono telefono;
    private ArrayList<Proveedor> listaProveedores;
    private DAOProveedores dao;
//    private DAOAcciones daoAcciones;
    
    @ManagedProperty(value="#{mbDireccion}")
    private MbDireccion mbDireccion;
    @ManagedProperty(value="#{mbContribuyentes}")
    private MbContribuyentes mbContribuyentes;
    private ArrayList<SelectItem> listaContribuyentes;
    @ManagedProperty(value="#{mbBuscarContribuyente}")
    private MbBuscarContribuyente mbBuscarContribuyente;
    @ManagedProperty(value="#{mbClasificaciones}")
    private MbClasificaciones mbClasificaciones;
    private ArrayList<SelectItem> listaClasificaciones;
    private ArrayList<SelectItem> listaSubClasificaciones;
    @ManagedProperty(value="#{mbTipoTerceros}")
    private MbTipoTerceros mbTipoTerceros;
    private ArrayList<SelectItem> listaTipoTerceros;
    @ManagedProperty(value="#{mbTipoOperacion}")
    private MbTipoOperacion mbTipoOperacion;
    private ArrayList<SelectItem> listaTipoOperaciones;
    @ManagedProperty(value="#{mbZonas}")
    private MbZonas mbImpuestoZona;
    @ManagedProperty(value="#{mbContactos}")
    private MbContactos mbContactos;
    @ManagedProperty(value="#{mbTelefonos}")
    private MbTelefonos mbTelefonos;
    private ArrayList<Accion> acciones;
    @ManagedProperty(value="#{mbAcciones}")
    private MbAcciones mbAcciones;
    
    public MbProveedores() {
        this.mbContribuyentes=new MbContribuyentes();
        this.mbClasificaciones=new MbClasificaciones();
        this.mbTipoTerceros=new MbTipoTerceros();
        this.mbTipoOperacion=new MbTipoOperacion();
        this.mbImpuestoZona=new MbZonas();
        this.mbDireccion=new MbDireccion();
        this.mbContactos=new MbContactos();
        this.mbTelefonos=new MbTelefonos();
        this.mbAcciones=new MbAcciones();
//        this.mbAcciones.setIdModulo(10);
        this.cargaClasificaciones();
        this.obtenerListaTipoTerceros();
        this.obtenerListaTipoOperaciones();
        this.listaContribuyentes=new ArrayList<SelectItem>();
        //this.obtenerAcciones();
    }
    
//    public boolean validarAccion(String idComando) {
//        boolean ok=false;
//        for(Accion a: this.acciones) {
//            if(a.getIdBoton().equals(idComando)) {
//                ok=true;
//                break;
//            }
//        }
//        return ok;
//    }
//    
//    private void obtenerAcciones() {
//        try {
//            this.daoAcciones=new DAOAcciones();
//            this.acciones=this.daoAcciones.obtenerAcciones(10);
//        } catch (SQLException ex) {
//            Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NamingException ex) {
//            Logger.getLogger(MbProveedores.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    public void eliminarTelefonoTipo() {
        if(this.mbTelefonos.eliminarTipo(this.mbTelefonos.getTipo().getIdTipo())) {
            this.mbTelefonos.getTelefono().setTipo(new TelefonoTipo(this.mbTelefonos.isCelular()));
            this.mbTelefonos.cargaTipos();
        }
    }
    
    public void grabarTelefonoTipo() {
        if(this.mbTelefonos.grabarTipo()) {
            this.mbTelefonos.getTelefono().setTipo(this.mbTelefonos.getTipo());
            this.mbTelefonos.cargaTipos();
        }
    }
    
    public void mttoTelefonoTipo() {
        if(this.mbTelefonos.getTelefono().getTipo().getIdTipo()==0) {
            this.mbTelefonos.setTipo(new TelefonoTipo(this.mbTelefonos.isCelular()));
        } else {
            this.mbTelefonos.setTipo(this.mbTelefonos.copiaTipo(this.mbTelefonos.getTelefono().getTipo()));
        }
    }
    
    public void eliminarTelefono() {
        if(this.mbTelefonos.eliminar(this.mbTelefonos.getTelefono().getIdTelefono())) {
            this.telefono=new Telefono();
            this.mbTelefonos.cargaTelefonos(this.contacto.getIdContacto());
        }
    }
    
    public void grabarTelefono() {
        if(this.mbTelefonos.grabar(this.contacto.getIdContacto())) {
            this.telefono=this.mbTelefonos.getTelefono();
            this.mbTelefonos.cargaTelefonos(this.contacto.getIdContacto());
        }
    }
    
    public void mttoTelefono() {
        if(this.telefono.getIdTelefono()==0) {
            this.mbTelefonos.setTelefono(new Telefono());
        } else {
            this.mbTelefonos.setTelefono(this.mbTelefonos.copia(this.telefono));
        }
        this.mbTelefonos.setCelular(this.mbTelefonos.getTelefono().getTipo().isCelular());
        this.mbTelefonos.cargaTipos();
    }
    
    public void cargarTelefonos() {
        this.mbTelefonos.cargaTelefonos(this.contacto.getIdContacto());
    }
    
    public void eliminarContacto() {
        if(this.mbContactos.eliminar(this.contacto.getIdContacto())) {
            this.contacto=new Contacto();
            this.mbContactos.cargaContactos(this.idTipo, this.proveedor.getIdProveedor());
        }
    }
    
    public void grabarContacto() {
        if(this.mbContactos.grabar(this.idTipo, this.proveedor.getIdProveedor())) {
            this.contacto=this.mbContactos.getContacto();
            this.mbContactos.cargaContactos(this.idTipo, this.proveedor.getIdProveedor());
        }
    }
    
    public void mttoContacto() {
        if(this.contacto.getIdContacto()==0) {
            this.mbContactos.setContacto(new Contacto());
        } else {
            this.mbContactos.setContacto(this.mbContactos.copia(this.contacto));
        }
    }
    
    public void actualizarContribuyente() {
        if(this.mbContribuyentes.valida()) {
            this.proveedor.setContribuyente(this.mbContribuyentes.getContribuyente());
        }
    }
    
    public void agregarContribuyente() {
        this.listaContribuyentes.clear();
        this.mbContribuyentes.getContribuyentes().clear();
        int idRfc=this.mbContribuyentes.getContribuyente().getIdRfc();
        String rfc=this.mbContribuyentes.getContribuyente().getRfc();

        Contribuyente c=new Contribuyente();
        c.setIdRfc(idRfc);
        c.setRfc(rfc);
        this.mbContribuyentes.setContribuyente(c);
    }
    
    public void aceptarContribuyente() {
        this.proveedor.setContribuyente(this.mbContribuyentes.getContribuyente());
    }
    
    public void obtenerRfc() {
        this.mbContribuyentes.obtenerContribuyentesRFC();
        if(!this.mbContribuyentes.getContribuyentes().isEmpty()) {
            this.mbContribuyentes.setContribuyente(this.mbContribuyentes.getContribuyentes().get(0));
        }
        this.listaContribuyentes=new ArrayList<SelectItem>();
        for(Contribuyente c: this.mbContribuyentes.getContribuyentes()) {
            this.listaContribuyentes.add(new SelectItem(c, c.toString()));
        }
    }
    
    public void nuevoContribuyente() {
        this.listaContribuyentes.clear();
        this.mbContribuyentes.getContribuyentes().clear();
        this.mbContribuyentes.setContribuyente(new Contribuyente());
    }
    
    public void mttoContribuyente() {
        this.listaContribuyentes.clear();
        this.mbContribuyentes.getContribuyentes().clear();
        Contribuyente c=this.mbContribuyentes.copia(this.proveedor.getContribuyente());
        this.mbContribuyentes.setContribuyente(c);
    }
    
    public void eliminarTipoOperacion() {
        this.mbTipoOperacion.eliminar();
        this.proveedor.setTipoOperacion(new TipoOperacion());
        this.listaTipoOperaciones.clear();
        for(TipoOperacion t: this.mbTipoOperacion.getTipoOperaciones()) {
            this.listaTipoOperaciones.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void grabarTipoOperacion() {
        this.mbTipoOperacion.grabar();
        this.proveedor.setTipoOperacion(this.mbTipoOperacion.getTipoOperacion());
        this.listaTipoOperaciones.clear();
        for(TipoOperacion t: this.mbTipoOperacion.getTipoOperaciones()) {
            this.listaTipoOperaciones.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void mttoTipoOperacion() {
        if(this.proveedor.getTipoOperacion().getIdTipoOperacion()==0) {
            this.mbTipoOperacion.setTipoOperacion(new TipoOperacion());
        } else {
            this.mbTipoOperacion.setTipoOperacion(this.mbTipoOperacion.copia(this.proveedor.getTipoOperacion()));
        }
    }
    
    private void obtenerListaTipoOperaciones() {
        this.listaTipoOperaciones=new ArrayList<SelectItem>();
        
        TipoOperacion tt=new TipoOperacion(0, "00", "Nuevo Tipo de Operación");
        this.listaTipoOperaciones.add(new SelectItem(tt, tt.toString()));
        
        ArrayList<TipoOperacion> ts=this.mbTipoOperacion.obtenerTipoOperaciones();
        for(TipoOperacion t: ts) {
            this.listaTipoOperaciones.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void eliminarTipoTercero() {
        this.mbTipoTerceros.eliminar();
        this.proveedor.setTipoTercero(new TipoTercero());
        this.listaTipoTerceros.clear();
        for(TipoTercero t: this.mbTipoTerceros.getTipoTerceros()) {
            this.listaTipoTerceros.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void grabarTipoTercero() {
        this.mbTipoTerceros.grabar();
        this.proveedor.setTipoTercero(this.mbTipoTerceros.getTipoTercero());
        this.listaTipoTerceros.clear();
        for(TipoTercero t: this.mbTipoTerceros.getTipoTerceros()) {
            this.listaTipoTerceros.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void mttoTipoTercero() {
        if(this.proveedor.getTipoTercero().getIdTipoTercero()==0) {
            this.mbTipoTerceros.setTipoTercero(new TipoTercero());
        } else {
            this.mbTipoTerceros.setTipoTercero(this.mbTipoTerceros.copia(this.proveedor.getTipoTercero()));
        }
    }
    
    private void obtenerListaTipoTerceros() {
        this.listaTipoTerceros=new ArrayList<SelectItem>();
        
        TipoTercero tt=new TipoTercero(0, "00", "Nuevo Tipo de Tercero");
        this.listaTipoTerceros.add(new SelectItem(tt, tt.toString()));
        
        ArrayList<TipoTercero> ts=this.mbTipoTerceros.obtenerTipoTerceros();
        for(TipoTercero t: ts) {
            this.listaTipoTerceros.add(new SelectItem(t, t.toString()));
        }
    }
    
    public void cargaSubClasificaciones() {
        this.listaSubClasificaciones=new ArrayList<SelectItem>();
        
        SubClasificacion sc=new SubClasificacion(0, "Nueva SubClasificacion");
        this.listaSubClasificaciones.add(new SelectItem(sc, sc.toString()));
        
        ArrayList<SubClasificacion> scs=this.mbClasificaciones.obtenerSubClasificaciones(this.proveedor.getClasificacion().getIdClasificacion());
        for(SubClasificacion s: scs) {
            this.listaSubClasificaciones.add(new SelectItem(s, s.toString()));
        }
    }
    
    public void eliminarSubClasificacion() {
        this.mbClasificaciones.eliminarSubClasificacion(this.proveedor.getClasificacion().getIdClasificacion());
        this.proveedor.setSubClasificacion(new SubClasificacion());
        this.cargaSubClasificaciones();
    }
    
    public void grabarSubClasificacion() {
        this.mbClasificaciones.grabarSubClasificacion(this.proveedor.getClasificacion().getIdClasificacion());
        this.proveedor.setSubClasificacion(this.mbClasificaciones.getSubClasificacion());
        this.cargaSubClasificaciones();
    }
    
    public void mttoSubClasificacion() {
        if(this.proveedor.getSubClasificacion().getIdSubClasificacion()==0) {
            this.mbClasificaciones.setSubClasificacion(new SubClasificacion());
        } else {
            this.mbClasificaciones.setSubClasificacion(this.mbClasificaciones.copia(this.proveedor.getSubClasificacion()));
        }
    }
    
    public void eliminarClasificacion() {
        this.mbClasificaciones.eliminar();
        this.proveedor.setClasificacion(new Clasificacion());
        this.cargaClasificaciones();
    }
    
    public void grabarClasificacion() {
        this.mbClasificaciones.grabar();
        this.proveedor.setClasificacion(this.mbClasificaciones.getClasificacion());
        this.cargaClasificaciones();
    }
    
    public void mttoClasificacion() {
        if(this.proveedor.getClasificacion().getIdClasificacion()==0) {
            this.mbClasificaciones.setClasificacion(new Clasificacion());
        } else {
            this.mbClasificaciones.setClasificacion(this.mbClasificaciones.copia(this.proveedor.getClasificacion()));
        }
    }
    
    private void cargaClasificaciones() {
        this.listaClasificaciones=new ArrayList<SelectItem>();
        
        Clasificacion cl=new Clasificacion(0, "Nueva Clasificación");
        this.listaClasificaciones.add(new SelectItem(cl, cl.toString()));
        
        ArrayList<Clasificacion> cs=this.mbClasificaciones.obtenerClasificaciones();
        for(Clasificacion c: cs) {
            this.listaClasificaciones.add(new SelectItem(c, c.toString()));
        }
    }
    
    public void seleccionaContribuyente() {
        this.proveedor.setContribuyente(this.mbBuscarContribuyente.getContribuyente());
        int idDireccion=this.proveedor.getContribuyente().getDireccion().getIdDireccion();
        this.proveedor.getContribuyente().setDireccion(this.mbDireccion.obtener(idDireccion));
    }
    
    public void buscar() {
        this.mbBuscarContribuyente.buscar();
        if(this.mbBuscarContribuyente.getContribuyente()!=null) {
            this.proveedor.setContribuyente(this.mbBuscarContribuyente.getContribuyente());
        }
    }
    
    public void eliminar() {
        // Probar buscar contribuyente cuando solo se encuentra un contribuyente por rfc 
        // y tambien cuando se encuentran varios y se selecciona de la lista
    }
    
    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        
        String strContribuyente=Utilerias.Acentos(this.proveedor.getContribuyente().getContribuyente());
        this.proveedor.getContribuyente().setContribuyente(strContribuyente);
        
        if(this.proveedor.getContribuyente().getRfc().equals("")) {
            fMsg.setDetail("Se requiere el RFC del contribuyente !!");
        } else if(strContribuyente.isEmpty()) {
            fMsg.setDetail("Se requiere el nombre del proveedor !!");   
        } else if(this.proveedor.getContribuyente().getDireccion().getIdDireccion()==0) {
            fMsg.setDetail("Se requiere la dirección fiscal del contribuyente !!");
        } else if(this.proveedor.getClasificacion().getIdClasificacion()==0) {
            fMsg.setDetail("Se requiere la clasificacion del proveedor !!");
        } else if(this.proveedor.getImpuestoZona().getIdZona()==0) {
            fMsg.setDetail("Se requiere la zona de impuestos del proveedor !!");
        } else if(this.proveedor.getDiasCredito() < 0) {
            fMsg.setDetail("Los dias de crédito no debe ser menores que cero !!");
        } else if(this.proveedor.getLimiteCredito() < 0.00) {
            fMsg.setDetail("El límite de crédito no debe ser menor que cero !!");
        } else {
            try {
                this.dao=new DAOProveedores();
                int idProveedor=this.proveedor.getIdProveedor();
                if (idProveedor == 0) {
                    idProveedor=this.dao.agregar(this.proveedor);
                } else {
                    this.dao.modificar(this.proveedor);
                }
                this.proveedor=this.convertir(this.dao.obtenerProveedor(idProveedor));
                this.listaProveedores=null;
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("El proveedor se grabó correctamente !!");
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
    
    public String salir() {
        if(this.proveedor.getIdProveedor() == 0 && this.proveedor.getDireccionEntrega().getIdDireccion() > 0) {
            mbDireccion.eliminar(this.proveedor.getDireccionEntrega().getIdDireccion());
        }
        this.listaProveedores=null;
        return "proveedor.salir";
    }
    
    public String terminar() {
        this.listaClasificaciones=null;
        this.listaTipoTerceros=null;
        this.listaTipoOperaciones=null;
//        this.acciones=null;
        this.acciones=null;
        return "menuProveedores.terminar";
    }
    
    public String mantenimiento(int idProveedor) {
        boolean ok=false;
        String destino="proveedor.mantenimiento";
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            if(idProveedor == 0) {
                this.proveedor=new Proveedor();
                this.proveedor.getContribuyente().setDireccion(this.mbDireccion.nuevaDireccion());
                this.proveedor.setDireccionEntrega(this.mbDireccion.nuevaDireccion());
            } else {
                Proveedor p=this.dao.obtenerProveedor(idProveedor);
                this.proveedor=convertir(p);
                this.cargaSubClasificaciones();
            }
            this.mbContactos.cargaContactos(this.idTipo, this.proveedor.getIdProveedor());
            if(this.mbContactos.getContactos().isEmpty()) {
                this.contacto=new Contacto();
            } else {
                this.contacto=this.mbContactos.getContactos().get(0);
            }
            this.mbTelefonos.cargaTelefonos(this.contacto.getIdContacto());
            ok=true;
        } catch (SQLException ex) {
            destino=null;
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return destino;
    }
    
    public ArrayList<Proveedor> getListaProveedores() {
        if(listaProveedores == null) {
            this.cargaProveedores();
        }
        return listaProveedores;
    }
    
    private void cargaProveedores() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.listaProveedores=new ArrayList<Proveedor>();
            this.dao=new DAOProveedores();
            ArrayList<Proveedor> proveedores=dao.obtenerProveedores();
            for(Proveedor c: proveedores) {
                this.listaProveedores.add(convertir(c));
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
    
    private Proveedor convertir(Proveedor proveedor) {
        int idDireccionFiscal=proveedor.getContribuyente().getDireccion().getIdDireccion();
        int idDireccionEntrega=proveedor.getDireccionEntrega().getIdDireccion();
        proveedor.getContribuyente().setDireccion(this.mbDireccion.obtener(idDireccionFiscal));
        proveedor.setDireccionEntrega(this.mbDireccion.obtener(idDireccionEntrega));
        return proveedor;
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

    public MbBuscarContribuyente getMbBuscarContribuyente() {
        return mbBuscarContribuyente;
    }

    public void setMbBuscarContribuyente(MbBuscarContribuyente mbBuscarContribuyente) {
        this.mbBuscarContribuyente = mbBuscarContribuyente;
    }

    public ArrayList<SelectItem> getListaClasificaciones() {
        if(this.listaClasificaciones==null) {
            this.cargaClasificaciones();
        }
        return listaClasificaciones;
    }

    public void setListaClasificaciones(ArrayList<SelectItem> listaClasificaciones) {
        this.listaClasificaciones = listaClasificaciones;
    }

    public MbClasificaciones getMbClasificaciones() {
        return mbClasificaciones;
    }

    public void setMbClasificaciones(MbClasificaciones mbClasificaciones) {
        this.mbClasificaciones = mbClasificaciones;
    }

    public ArrayList<SelectItem> getListaTipoTerceros() {
        return listaTipoTerceros;
    }

    public void setListaTipoTerceros(ArrayList<SelectItem> listaTipoTerceros) {
        this.listaTipoTerceros = listaTipoTerceros;
    }

    public MbTipoTerceros getMbTipoTerceros() {
        return mbTipoTerceros;
    }

    public void setMbTipoTerceros(MbTipoTerceros mbTipoTerceros) {
        this.mbTipoTerceros = mbTipoTerceros;
    }

    public MbTipoOperacion getMbTipoOperacion() {
        return mbTipoOperacion;
    }

    public void setMbTipoOperacion(MbTipoOperacion mbTipoOperacion) {
        this.mbTipoOperacion = mbTipoOperacion;
    }

    public ArrayList<SelectItem> getListaTipoOperaciones() {
        return listaTipoOperaciones;
    }

    public void setListaTipoOperaciones(ArrayList<SelectItem> listaTipoOperaciones) {
        this.listaTipoOperaciones = listaTipoOperaciones;
    }

    public MbZonas getMbImpuestoZona() {
        return mbImpuestoZona;
    }

    public void setMbImpuestoZona(MbZonas mbImpuestoZona) {
        this.mbImpuestoZona = mbImpuestoZona;
    }

    public MbContribuyentes getMbContribuyentes() {
        return mbContribuyentes;
    }

    public void setMbContribuyentes(MbContribuyentes mbContribuyentes) {
        this.mbContribuyentes = mbContribuyentes;
    }

    public ArrayList<SelectItem> getListaContribuyentes() {
        return listaContribuyentes;
    }

    public void setListaContribuyentes(ArrayList<SelectItem> listaContribuyentes) {
        this.listaContribuyentes = listaContribuyentes;
    }

    public ArrayList<SelectItem> getListaSubClasificaciones() {
        return listaSubClasificaciones;
    }

    public void setListaSubClasificaciones(ArrayList<SelectItem> listaSubClasificaciones) {
        this.listaSubClasificaciones = listaSubClasificaciones;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Telefono getTelefono() {
        return telefono;
    }

    public void setTelefono(Telefono telefono) {
        this.telefono = telefono;
    }

    public MbContactos getMbContactos() {
        return mbContactos;
    }

    public void setMbContactos(MbContactos mbContactos) {
        this.mbContactos = mbContactos;
    }

    public MbTelefonos getMbTelefonos() {
        return mbTelefonos;
    }

    public void setMbTelefonos(MbTelefonos mbTelefonos) {
        this.mbTelefonos = mbTelefonos;
    }

    public ArrayList<Accion> getAcciones() {
        if(this.acciones==null) {
            this.acciones=this.mbAcciones.obtenerAcciones(10);
//            this.obtenerAcciones();
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }
}
