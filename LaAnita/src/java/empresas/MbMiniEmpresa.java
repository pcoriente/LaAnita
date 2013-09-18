package empresas;

import empresas.dao.DAOMiniEmpresas;
import empresas.dominio.MiniEmpresa;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbMiniEmpresa")
@SessionScoped
public class MbMiniEmpresa implements Serializable {
    private MiniEmpresa empresa;
    private ArrayList<MiniEmpresa> lstEmpresas;
    private DAOMiniEmpresas dao = new DAOMiniEmpresas();
    
    public MbMiniEmpresa() throws NamingException {
        this.dao = new DAOMiniEmpresas();
    }
    
    public ArrayList<SelectItem> obtenerListaMiniEmpresas() throws SQLException {
        ArrayList<SelectItem> listaEmpresas=new ArrayList<SelectItem>();
        try {
            MiniEmpresa e0=new MiniEmpresa();
            e0.setIdEmpresa(0);
            e0.setCodigoEmpresa("0");
            e0.setNombreComercial("Empresa");
            listaEmpresas.add(new SelectItem(e0, e0.toString()));

            ArrayList<MiniEmpresa> empresas=this.dao.obtenerMiniEmpresas();
            for (MiniEmpresa e : empresas) {
                listaEmpresas.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException e) {
            Logger.getLogger(MbMiniEmpresas.class.getName()).log(Level.SEVERE, null, e);
        }
        return listaEmpresas;
    }

    public MiniEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(MiniEmpresa empresa) {
        this.empresa = empresa;
    }

    public ArrayList<MiniEmpresa> getLstEmpresas() throws SQLException {
        if(lstEmpresas==null) {
            this.obtenerListaMiniEmpresas();
        }
        return lstEmpresas;
    }

    public void setLstEmpresas(ArrayList<MiniEmpresa> lstEmpresas) {
        this.lstEmpresas = lstEmpresas;
    }

    public DAOMiniEmpresas getDao() {
        return dao;
    }

    public void setDao(DAOMiniEmpresas dao) {
        this.dao = dao;
    }
}
