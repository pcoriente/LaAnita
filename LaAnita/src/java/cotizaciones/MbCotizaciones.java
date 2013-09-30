package cotizaciones;

import cotizaciones.dao.DAOCotizaciones;
import cotizaciones.dominio.CotizacionDetalle;
import cotizaciones.dominio.CotizacionEncabezado;
import empresas.MbMiniEmpresa;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;
import javax.naming.NamingException;
import proveedores.MbProveedores;

@Named(value = "mbCotizaciones")
@SessionScoped
public class MbCotizaciones implements Serializable {

    private ArrayList<CotizacionEncabezado> listaCotizacionEncabezado;
    private CotizacionEncabezado cotizacionEncabezado;
    private ArrayList<CotizacionDetalle> listaCotizacionDetalle;
    private CotizacionDetalle cotizacionDetalle;
    private ArrayList<CotizacionEncabezado> miniCotizacionProveedor;
    @ManagedProperty(value = "#{mbProveedores}")
    private MbProveedores mbProveedores;

    //CONSTRUCTORES-------------------------------------------------------------------------------------------------------------------------------------------------------
    public MbCotizaciones() {
    }
    //METODOS ---------------------------------------------------------------------------------------------------------------------------------------------------------

    private void cargaCotizaciones() throws NamingException, SQLException {
        listaCotizacionEncabezado = new ArrayList<CotizacionEncabezado>();
        DAOCotizaciones daoCot = new DAOCotizaciones();
        ArrayList<CotizacionEncabezado> lista = daoCot.listaCotizaciones();
        for (CotizacionEncabezado d : lista) {
            listaCotizacionEncabezado.add(d);
        }
    }

    public void cargaCotizacionesProveedor(int idReq) throws NamingException, SQLException {
        listaCotizacionDetalle = new ArrayList<CotizacionDetalle>();
        DAOCotizaciones daoCot = new DAOCotizaciones();
        ArrayList<CotizacionDetalle> lista = daoCot.consultaCotizacionesProveedores(idReq);
        for (CotizacionDetalle d : lista) {
            listaCotizacionDetalle.add(d);
        }
    }

    public void cargaCotizacionesProveedorEncabezado(int idReq) throws NamingException, SQLException {
        listaCotizacionEncabezado = new ArrayList<CotizacionEncabezado>();
        DAOCotizaciones daoCot = new DAOCotizaciones();
        ArrayList<CotizacionEncabezado> lista = daoCot.consultaCotizacionesProveedoresEncabezado(idReq);
        for (CotizacionEncabezado d : lista) {
            listaCotizacionEncabezado.add(d);
        }
    }

    public void cotizacionxProveedor() {
        miniCotizacionProveedor = new ArrayList<CotizacionEncabezado>();



    }

    public String salirMenuCotizaciones() throws NamingException {
       
        String salir = "index.xhtml";
        return salir;
    }

    //GETS Y SETS------------------------------------------------------------------------------------------------------------------------------------------------------
    public ArrayList<CotizacionEncabezado> getListaCotizacionEncabezado() throws SQLException {
        if (listaCotizacionEncabezado == null) {
            try {
                this.cargaCotizaciones();
            } catch (NamingException ex) {
                Logger.getLogger(MbCotizaciones.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listaCotizacionEncabezado;
    }

    public void setListaCotizacionEncabezado(ArrayList<CotizacionEncabezado> listaCotizacionEncabezado) {
        this.listaCotizacionEncabezado = listaCotizacionEncabezado;
    }

    public CotizacionEncabezado getCotizacionEncabezado() throws SQLException {

        return cotizacionEncabezado;
    }

    public void setCotizacionEncabezado(CotizacionEncabezado cotizacionEncabezado) {
        this.cotizacionEncabezado = cotizacionEncabezado;
    }

    public ArrayList<CotizacionDetalle> getListaCotizacionDetalle() {
        return listaCotizacionDetalle;
    }

    public void setListaCotizacionDetalle(ArrayList<CotizacionDetalle> listaCotizacionDetalle) {
        this.listaCotizacionDetalle = listaCotizacionDetalle;
    }

    public CotizacionDetalle getCotizacionDetalle() {
        return cotizacionDetalle;
    }

    public void setCotizacionDetalle(CotizacionDetalle cotizacionDetalle) {
        this.cotizacionDetalle = cotizacionDetalle;
    }

    public ArrayList<CotizacionEncabezado> getMiniCotizacionProveedor() {
        return miniCotizacionProveedor;
    }

    public void setMiniCotizacionProveedor(ArrayList<CotizacionEncabezado> miniCotizacionProveedor) {
        this.miniCotizacionProveedor = miniCotizacionProveedor;
    }

    public MbProveedores getMbProveedores() {
        return mbProveedores;
    }

    public void setMbProveedores(MbProveedores mbProveedores) {
        this.mbProveedores = mbProveedores;
    }
    
    
    
}
