package usuarios;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import usuarios.dao.DAOAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbAcciones")
@SessionScoped
public class MbAcciones implements Serializable {
    private int idModulo;
    private ArrayList<Accion> acciones;
    private DAOAcciones dao;
    
    public MbAcciones(int idModulo) {
        this.idModulo=idModulo;
        this.obtenerAcciones();
    }
    
    public void inicializar(int idModulo) {
        this.idModulo=idModulo;
        this.obtenerAcciones();
    }
    
    public String obtenerAcciones(int idModulo) {
        String navega=null;
        try {
            this.dao=new DAOAcciones();
            this.acciones=this.dao.obtenerAcciones(idModulo);
            switch (idModulo) {
            case 1:
                navega="menuCedis.xhtml";
                break;
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbAcciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbAcciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return navega;
    }
    
    private void obtenerAcciones() {
        try {
            this.dao=new DAOAcciones();
            this.acciones=this.dao.obtenerAcciones(idModulo);
        } catch (NamingException ex) {
            Logger.getLogger(MbAcciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbAcciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }

    public ArrayList<Accion> getAcciones() {
        if(this.acciones==null) {
            this.obtenerAcciones();
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }
}
