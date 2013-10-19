/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package almacenes;

import almacenes.dao.DAOAlmacenes;
import almacenes.dominio.Almacenes;
import almacenes.to.TOAlmacen;
import cedis.MbCedis;
import cedis.dominio.MiniCedis;
import empresas.MbMiniEmpresas;
import empresas.dominio.MiniEmpresa;
import direccion.MbDireccion;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author carlosp
 */
@Named(value = "mbAlmacenes")
@SessionScoped
public class MbAlmacenes implements Serializable {

    private TOAlmacen toAlmacen;
    private Almacenes almacenes;
    @ManagedProperty(value = "#{mbCedis}")
    private MbCedis mbCedis;
    @ManagedProperty(value = "#{mbMiniEmpresas}")
    private MbMiniEmpresas mbMiniEmpresas;
    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion;
    private ArrayList<SelectItem> listaCedis;
    private ArrayList<SelectItem> listaEmpresas;
    private ArrayList<Almacenes> listaAlmacenes;
    private MiniCedis cedis;
    private MiniEmpresa empresa;
    private DAOAlmacenes dao;

    /**
     * Creates a new instance of MbAlmacenes
     */
    public MbAlmacenes() throws NamingException {
        this.mbCedis = new MbCedis();
        this.cedis = new MiniCedis();
        this.mbMiniEmpresas = new MbMiniEmpresas();
        this.empresa = new MiniEmpresa();
        this.mbDireccion = new MbDireccion();
    }

    public String regresarSinAcceso() {
        return "index.xhtml";
    }

    public void cargaCedis() {
        try {
            listaCedis = this.mbCedis.obtenerListaMiniCedis();
        } catch (SQLException ex) {
            Logger.getLogger(MbAlmacenes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargaEmpresas() {
        try {
            listaEmpresas = this.mbMiniEmpresas.obtenerListaMiniEmpresas();
        } catch (SQLException ex) {
            Logger.getLogger(MbAlmacenes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Almacenes> traeListaAlmacenes() throws NamingException {
        try {
            if (listaAlmacenes == null) {
                cargaAlmacenes();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbAlmacenes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaAlmacenes;
    }

    private void cargaAlmacenes() throws SQLException, NamingException {
        listaAlmacenes = new ArrayList<Almacenes>();
        ArrayList<TOAlmacen> toLista = dao.obtenerAlmacenes();

        for (TOAlmacen e : toLista) {
            listaAlmacenes.add(convertir(e));
        }
    }

    private Almacenes convertir(TOAlmacen to) {
        Almacenes alm = new Almacenes();
        alm.setIdAlmacen(to.getIdAlmacen());
        alm.setAlmacen(to.getAlmacen());
        /*alm.setMiniCedis(this.mbCedis.(to.getIdCedis()));
         alm.setMiniEmpresa(this.mbMiniEmpresas.getEmpresa(to.getIdEmpresa()));*/
        alm.setDireccion(this.mbDireccion.obtener(to.getIdDireccion()));
        alm.setEncargado(to.getEncargado());
        return alm;
    }

    public String mantenimiento(int idAlmacen) throws SQLException {
        String destino = "almacen.mantenimiento";

        try {
            if (idAlmacen == 0) {
                this.almacenes = nuevoAlmacen();
            } else {
                TOAlmacen toAlmacen = this.dao.obtenerUnAlmacen(idAlmacen);
                if (toAlmacen == null) {
                    destino = null;
                } else {
                    this.almacenes = convertir(toAlmacen);
                }
            }
        } catch (SQLException ex) {
            destino = null;
            Logger.getLogger(MbAlmacenes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }

    private Almacenes nuevoAlmacen() throws SQLException {
        Almacenes a = new Almacenes();
        a.setIdAlmacen(0);
        a.setAlmacen("");
        a.setIdCedis(0);
        a.setIdEmpresa(0);
        a.setEncargado("");
        a.setDireccion(this.mbDireccion.getDireccion());
        return a;
    }
    public void inicializarVariables() {
        Almacenes a = new Almacenes();
        a.setAlmacen("");
        a.setEncargado("");
    }

    public String terminar() {
        this.cedis = new MiniCedis();
        this.listaCedis = null;
        this.empresa = new MiniEmpresa();
        this.listaEmpresas = null;
        return "index.xhtml";
    }

    /* Getter and Setter*/
    public Almacenes getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(Almacenes almacenes) {
        this.almacenes = almacenes;
    }

    public MbCedis getMbCedis() {
        return mbCedis;
    }

    public void setMbCedis(MbCedis mbCedis) {
        this.mbCedis = mbCedis;
    }

    public MbMiniEmpresas getMbMiniEmpresas() {
        return mbMiniEmpresas;
    }

    public void setMbMiniEmpresas(MbMiniEmpresas mbMiniEmpresas) {
        this.mbMiniEmpresas = mbMiniEmpresas;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }

    public ArrayList<SelectItem> getListaCedis() {
        if (this.listaCedis == null) {
            this.cargaCedis();
        }
        return listaCedis;
    }

    public void setListaCedis(ArrayList<SelectItem> listaCedis) {
        this.listaCedis = listaCedis;
    }

    public MiniCedis getCedis() {
        return cedis;
    }

    public void setCedis(MiniCedis cedis) {
        this.cedis = cedis;
    }

    public ArrayList<SelectItem> getListaEmpresas() {
        if (this.listaEmpresas == null) {
            this.cargaEmpresas();
        }

        return listaEmpresas;
    }

    public void setListaEmpresas(ArrayList<SelectItem> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public MiniEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(MiniEmpresa empresa) {
        this.empresa = empresa;
    }

    public ArrayList<Almacenes> getListaAlmacenes() {
        return listaAlmacenes;
    }

    public void setListaAlmacenes(ArrayList<Almacenes> listaAlmacenes) {
        this.listaAlmacenes = listaAlmacenes;
    }

    public DAOAlmacenes getDao() {
        return dao;
    }

    public void setDao(DAOAlmacenes dao) {
        this.dao = dao;
    }
}
