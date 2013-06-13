package cedis;

import cedis.dao.DAOCedis;
import direccion.MbDireccion;
import cedis.dominio.Cedis;
import cedis.dominio.MiniCedis;
import cedis.to.TOCedis;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import utilerias.Utilerias;

/**
 *
 * @author Julio
 */
@ManagedBean(name = "mbCedis")
@SessionScoped
public class MbCedis implements Serializable {
    private Cedis cedis;
    @ManagedProperty(value="#{mbDireccion}")
    private MbDireccion mbDireccion;
    private ArrayList<Cedis> listaCedis;
    private DAOCedis dao;
    
    public MbCedis() {
        try {
            this.dao=new DAOCedis();
        } catch (NamingException ex) {
            Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String grabar() {
        String destino=null;
        int codigo=this.cedis.getCodigo();
        String strCedis=Utilerias.Acentos(this.cedis.getCedis());
        String telefono=this.cedis.getTelefono();
        String fax=this.cedis.getFax();
        String correo=this.cedis.getCorreo();
        String representante=Utilerias.Acentos(this.cedis.getRepresentante());
        int idDireccion=this.cedis.getDireccion().getIdDireccion();
        
        if(strCedis.isEmpty()) return destino;
        else if(correo.isEmpty()) return destino;
        else if(representante.isEmpty()) return destino;
        else if(idDireccion == 0) return destino;
        else {
            try {
                int idCedis=this.cedis.getIdCedis();
                if (idCedis == 0) {
                    idCedis=this.dao.agregar(codigo, strCedis, idDireccion, telefono, fax, correo, representante);
                } else {
                    this.dao.modificar(this.cedis.getIdCedis(), strCedis, idDireccion, telefono, fax, correo, representante);
                }
                this.cedis=this.obtenerCedis(idCedis);
                this.listaCedis=null;
                destino="cedis.salir";
            } catch (SQLException ex) {
                Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return destino;
    }
    
    public String terminar() {
        return "menuCedis.terminar";
    }
    
    public String cancelar() {
        if (this.cedis.getIdCedis() == 0 && this.cedis.getDireccion().getIdDireccion() > 0) {
            mbDireccion.eliminar(this.cedis.getDireccion().getIdDireccion());
        }
        return "cedis.salir";
    }
    
    public String mantenimiento(int idCedis) {
        String destino="cedis.mantenimiento";
        try {
            if(idCedis == 0) {
                this.cedis=nuevoCedis();
            } else {
                TOCedis toCedis=this.dao.obtenerUnCedis(idCedis);
                if(toCedis == null) destino=null;
                else this.cedis=convertir(toCedis);
            }
        } catch (SQLException ex) {
            destino=null;
            Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }
    
    private Cedis nuevoCedis() {
        Cedis c=null;
        try {
            int ultimo=this.dao.ultimoCedis();
            
            c=new Cedis();
            c.setIdCedis(0);
            c.setCodigo(ultimo+1);
            c.setCedis("");
            c.setDireccion(this.mbDireccion.nuevaDireccion());
            c.setTelefono("");
            c.setFax("");
            c.setCorreo("");
            c.setRepresentante("");
        } catch (SQLException ex) {
            Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
    
    public ArrayList<Cedis> getListaCedis() {
        try {
            if(listaCedis == null) cargaCedis();
        } catch (SQLException ex) {
            Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaCedis;
    }
    
    private void cargaCedis() throws SQLException {
        listaCedis=new ArrayList<Cedis>();
        ArrayList<TOCedis> toLista=dao.obtenerCedis();
        for(TOCedis c:toLista) {
            listaCedis.add(convertir(c));
        }
    }
    
    public Cedis obtenerCedis(int idCedis) throws SQLException {
        Cedis xCedis=null;
        xCedis=convertir(this.dao.obtenerUnCedis(idCedis));
        return xCedis;
    }
    /*
    public Cedis obtenerCedis(String cod_bod) throws SQLException {
        Cedis xCedis=null;
        xCedis=convertir(this.dao.obtenerUnCedis(cod_bod));
        return xCedis;
    }
     * 
     */
    
    private Cedis convertir(TOCedis to) {
        Cedis c=new Cedis();
        c.setIdCedis(to.getIdCedis());
        c.setCodigo(to.getCodigo());
        c.setCedis(to.getCedis());
        //c.setIdDireccion(to.getIdDireccion());
        c.setDireccion(this.mbDireccion.obtener(to.getIdDireccion()));
        c.setTelefono(to.getTelefono());
        c.setFax(to.getFax());
        c.setCorreo(to.getCorreo());
        c.setRepresentante(to.getRepresentante());
        return c;
    }
    
    public ArrayList<SelectItem> obtenerListaMiniCedis() throws SQLException {
        ArrayList<SelectItem> listaMiniCedis=new ArrayList<SelectItem>();
        try {
            MiniCedis p0 = new MiniCedis();
            p0.setIdCedis(0);
            p0.setCodigo("00");
            p0.setCedis("Seleccione un CEDIS");
            SelectItem cero = new SelectItem(p0, p0.toString());
            listaMiniCedis.add(cero);

            ArrayList<MiniCedis> lstMiniCedis=this.dao.obtenerListaMiniCedis();
            for (MiniCedis m : lstMiniCedis) {
                listaMiniCedis.add(new SelectItem(m, m.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbCedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMiniCedis;
    }

    public Cedis getCedis() {
        return cedis;
    }

    public void setCedis(Cedis cedis) {
        this.cedis = cedis;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }
}
