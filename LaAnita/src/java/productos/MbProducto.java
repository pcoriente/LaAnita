package productos;

import impuestos.FrmImpuestos;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import productos.dao.DAOGrupos;
import productos.dao.DAOPartes;
import productos.dao.DAOProductos;
import productos.dao.DAOSubGrupos;
import productos.dao.DAOTipos;
import productos.dao.DAOUnidades;
import productos.dominio.Grupo;
import productos.dominio.Parte;
import productos.dominio.Parte2;
import productos.dominio.Producto;
import productos.dominio.SubGrupo;
import productos.dominio.Tipo;
import productos.dominio.Unidad;
import productos.dominio.Upc;
import unidadesMedida.DAOUnidadesMedida;
import unidadesMedida.UnidadMedida;
import utilerias.Utilerias;

/**
 *
 * @author JULIOS
 */
@ManagedBean(name = "mbProducto")
@SessionScoped
public class MbProducto implements Serializable {

    private Upc upc;
    private ArrayList<SelectItem> listaUpcs;
    private ArrayList<SelectItem> listaTipos;
    //private Grupo grupo;
    private ArrayList<SelectItem> listaGrupos;
    private ArrayList<SelectItem> listaSubGrupos;
    //private ArrayList<SelectItem> listaMarcas;
    private ArrayList<SelectItem> listaUnidades;
    private ArrayList<SelectItem> listaUnidadesMedida;
    //private SelectItem[] arrayTipos;
    //private SelectItem[] arrayGrupos;
    //private SelectItem[] arraySubGrupos;
    private Producto producto;
    private ArrayList<Producto> productos;
    @ManagedProperty(value = "#{mbUpc}")
    private MbUpc mbUpc;
    @ManagedProperty(value = "#{mbParte}")
    private MbParte mbParte;
    @ManagedProperty(value = "#{mbTipo}")
    private MbTipo mbTipo;
    @ManagedProperty(value = "#{mbGrupo}")
    private MbGrupo mbGrupo;
    @ManagedProperty(value = "#{mbSubGrupo}")
    private MbSubGrupo mbSubGrupo;
    @ManagedProperty(value = "#{mbUnidadProducto}")
    private MbUnidadProducto mbUnidadProducto;
    @ManagedProperty(value = "#{mbImpuesto}")
    private FrmImpuestos mbImpuesto;
    private DAOProductos dao;

    public MbProducto() {
        try {
            this.dao = new DAOProductos();
            this.producto = new Producto(0);
            this.mbUpc = new MbUpc();
            this.mbParte = new MbParte();
            this.mbTipo = new MbTipo();
            this.mbGrupo = new MbGrupo();
            this.mbSubGrupo = new MbSubGrupo();
            this.mbUnidadProducto = new MbUnidadProducto();
            this.mbImpuesto = new FrmImpuestos();
            //this.arrayTipos =  new SelectItem[1];
            //this.arrayTipos[0]=new SelectItem("", "Seleccione");
        } catch (NamingException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
     public void actualizaCambioTipo() {
     if(this.producto.getTipo().getIdTipo()==2) {
     this.producto.setMarca(new Marca(0, 0, "", 0));
     this.producto.setUnidad(new Unidad(0, "", ""));
     this.producto.setContenido(0.0);
     this.producto.setUnidadMedida(new UnidadMedida(0, "", "", 0));
     }
     }
     */

    public void eliminarParte() {
        if (this.mbParte.eliminar()) {
            this.producto.setParte2(this.mbParte.getParte2());
        }
    }

    public void grabarParte() {
        if (this.mbParte.grabar()) {
            this.mbParte.setParte2(this.producto.getParte2());
            this.producto.setParte2(this.mbParte.getParte2());
        }
    }

    public void eliminarUnidadProducto() {
        if (this.mbUnidadProducto.eliminar()) {
            this.producto.setUnidad(new Unidad());
            this.listaUnidades = null;
        }
    }

    public void grabarUnidadProducto() {
        if (this.mbUnidadProducto.grabar()) {
            this.producto.setUnidad(this.mbUnidadProducto.getUnidad());
            this.producto.setContenido(1);
            this.listaUnidades = null;
        }
    }

    public void mttoUnidad() {
        if (this.producto.getUnidad().getIdUnidad() == 0) {
            this.mbUnidadProducto.setUnidad(new Unidad());
        } else {
            this.mbUnidadProducto.setUnidad(this.mbUnidadProducto.copia(this.producto.getUnidad()));
        }
    }

    public void eliminarSubGrupo() {
        if (this.mbSubGrupo.eliminar(this.producto.getGrupo().getIdGrupo())) {
            this.producto.setSubGrupo(new SubGrupo(0, ""));
            this.listaSubGrupos = null;
        }
    }

    public void grabarSubGrupo() {
        if (this.mbSubGrupo.grabar(this.producto.getGrupo().getIdGrupo())) {
            this.producto.setSubGrupo(this.mbSubGrupo.getSubGrupo());
            this.listaSubGrupos = null;
        }
    }

    public void mttoSubGrupo() {
        if (this.producto.getSubGrupo().getIdSubGrupo() == 0) {
            this.mbSubGrupo.setSubGrupo(new SubGrupo(0, ""));
        } else {
            this.mbSubGrupo.setSubGrupo(this.mbSubGrupo.copia(this.producto.getSubGrupo()));
        }
    }

    public void eliminarGrupo() {
        if (this.mbGrupo.eliminar()) {
            this.producto.setGrupo(new Grupo(0, 0, ""));
            this.listaGrupos = null;
            this.listaSubGrupos = null;
        }
    }

    public void grabarGrupo() {
        int idGrupo = this.producto.getGrupo().getIdGrupo();
        if (this.mbGrupo.grabar()) {
            this.producto.setGrupo(this.mbGrupo.getGrupo());
            this.listaGrupos = null;
            if (this.mbGrupo.getGrupo().getIdGrupo() != idGrupo) {
                this.listaSubGrupos = null;
            }
        }
    }

    public void mttoGrupo() {
        if (this.producto.getGrupo().getIdGrupo() == 0) {
            this.mbGrupo.setGrupo(new Grupo(0, this.mbGrupo.obtenerUltimoGrupo(), ""));
        } else {
            this.mbGrupo.setGrupo(this.mbGrupo.copia(this.producto.getGrupo()));
        }
    }
    /*
    public void cargaArreglosBuscar() {
        int i=0;
        SelectItem[] opciones=new SelectItem[this.getMbTipo().getTipos().size()+1];
        opciones[i++]=new SelectItem("", "Seleccione un tipo");
        for(Tipo tp: this.getMbTipo().getTipos()) {
            opciones[i++]=new SelectItem(tp.getTipo(), tp.getTipo());
        }
        this.arrayTipos=opciones;
    }
    * */

    public boolean eliminarUpc() {
        boolean ok = this.mbUpc.eliminar();
        if (ok) {
            actualizaUpcs();
        }
        return ok;
    }

    public boolean agregarUpc() {
        boolean ok = this.mbUpc.agregar();
        if (ok) {
            this.actualizaUpcs();
        }
        return ok;
    }

    private void actualizaUpcs() {
        this.producto.setUpcs(this.mbUpc.getUpcs());
        this.cargaListaUpcs();
    }

    public void mttoUpc() {
        if (this.mbUpc.getUpc().getUpc().equals(new Upc(this.mbUpc.getUpc().getIdProducto()).getUpc())) {
            this.mbUpc.getUpc().setUpc("");
        }
        this.mbUpc.setUpcs(this.producto.getUpcs());
    }

    public void actualizarContenido() {
        if (this.producto.getUnidad().getIdUnidad() == 0) {
            this.producto.setContenido(0);
        } else if(this.producto.getUnidad().getIdUnidad()==1) {
            this.producto.setContenido(0);
        } else if(this.producto.getContenido()==0) {
            this.producto.setContenido(1);
        }
        this.producto.setUnidadMedida(new UnidadMedida(0, "", "", 0));
    }

    public void nuevoProducto() {
        this.producto = new Producto(0);
    }

    public boolean grabar() {
        boolean resultado = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        //try {
            this.producto.setContenido(Utilerias.Round(this.producto.getContenido(), 3));
            if (this.producto.getParte2().getIdParte() == 0) {
                fMsg.setDetail("Se requiere una parte !!");
            } else if (this.producto.getTipo().getIdTipo() == 0) {
                fMsg.setDetail("Se requiere un tipo de producto !!");
            } else if (this.producto.getSubGrupo().getIdSubGrupo() == 0) {
                fMsg.setDetail("Se requiere el subgrupo !!");
            } else if(this.producto.getUnidad().getIdUnidad() == 0) {
                fMsg.setDetail("Se requiere una unidad de empaque");
            } else if(this.producto.getUnidad().getIdUnidad() > 1 && (this.producto.getContenido() <= 0 || this.producto.getContenido() >= 1000)) {
                this.producto.setContenido(0);
                fMsg.setDetail("Se requiere un número mayor que cero y menor a 1000");
            } else if (this.producto.getUnidad().getIdUnidad() > 1 && this.producto.getUnidadMedida().getIdUnidadMedida() == 0) {
                fMsg.setDetail("Se requiere la unidad de medida !!");
            } else if (this.producto.getImpuesto().getIdGrupo() == 0) {
                fMsg.setDetail("Se requiere un impuesto !!");
            } else {
                try {
                    if (this.producto.getIdProducto() == 0) {
                        this.producto.setIdProducto(this.dao.agregar(this.producto));
                        if (this.producto.getUpcs().isEmpty()) {
                            resultado = true;
                        } else {
                            for (Upc u : this.producto.getUpcs()) {
                                u.setIdProducto(this.producto.getIdProducto());
                                this.cargaListaUpcs();
                            }
                            fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                            fMsg.setDetail("El producto se grabó correctamente, hay modificaciones pendientes !!");
                        }
                    } else {
                        this.dao.modificar(producto);
                        resultado = true;
                    }
                } catch (NamingException ex) {
                    fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    fMsg.setDetail(ex.getMessage());
                } catch (SQLException ex) {
                    fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
                }
            }
        //} catch (NumberFormatException ex) {
        //    fMsg.setDetail("CONTENIDO : Se reguiere un número mayor que cero y hasta 999.999 !!");
        //}
        if (!resultado) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return resultado;
    }

    public Producto miCopia(Producto prod2) {
        Producto prod1 = new Producto(prod2.getIdProducto());
        prod1.getParte2().setIdParte(prod2.getParte2().getIdParte());
        prod1.getParte2().setParte(prod2.getParte2().getParte());
        prod1.setDescripcion(prod2.getDescripcion());
        prod1.setUpcs(new ArrayList<Upc>());
        for (Upc u : prod2.getUpcs()) {
            prod1.getUpcs().add(new Upc(u.getIdUpc(), u.getUpc(), u.getIdProducto()));
        }
        prod1.getTipo().setIdTipo(prod2.getTipo().getIdTipo());
        prod1.getTipo().setTipo(prod2.getTipo().getTipo());
        prod1.getGrupo().setIdGrupo(prod2.getGrupo().getIdGrupo());
        prod1.getGrupo().setCodigo(prod2.getGrupo().getCodigo());
        prod1.getGrupo().setGrupo(prod2.getGrupo().getGrupo());
        prod1.getSubGrupo().setIdSubGrupo(prod2.getSubGrupo().getIdSubGrupo());
        prod1.getSubGrupo().setSubGrupo(prod2.getSubGrupo().getSubGrupo());
        prod1.getMarca().setIdMarca(prod2.getMarca().getIdMarca());
        //prod1.getMarca().setCodigoMarca(prod2.getMarca().getCodigoMarca());
        prod1.getMarca().setMarca(prod2.getMarca().getMarca());
        prod1.getMarca().setProduccion(prod2.getMarca().isProduccion());
        prod1.getUnidad().setIdUnidad(prod2.getUnidad().getIdUnidad());
        prod1.getUnidad().setUnidad(prod2.getUnidad().getUnidad());
        prod1.getUnidad().setAbreviatura(prod2.getUnidad().getAbreviatura());
        prod1.setContenido(prod2.getContenido());
        prod1.getUnidadMedida().setIdUnidadMedida(prod2.getUnidadMedida().getIdUnidadMedida());
        prod1.getUnidadMedida().setUnidadMedida(prod2.getUnidadMedida().getUnidadMedida());
        prod1.getUnidadMedida().setAbreviatura(prod2.getUnidadMedida().getAbreviatura());
        prod1.getUnidadMedida().setIdTipo(prod2.getUnidadMedida().getIdTipo());
        prod1.getImpuesto().setIdGrupo(prod2.getImpuesto().getIdGrupo());
        prod1.getImpuesto().setGrupo(prod2.getImpuesto().getGrupo());
        return prod1;
    }

    public void cargaUnidadesMedida() {
        this.listaUnidadesMedida = new ArrayList<SelectItem>();
        UnidadMedida unid = new UnidadMedida(0, "SELECCIONE", "", 0);
        this.listaUnidadesMedida.add(new SelectItem(unid, unid.toString()));
        try {
            DAOUnidadesMedida daoUnidades = new DAOUnidadesMedida();
            ArrayList<UnidadMedida> lstUnidades = daoUnidades.obtenerUnidades();
            for (UnidadMedida u : lstUnidades) {
                this.listaUnidadesMedida.add(new SelectItem(u, u.toString()));
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargaUnidades() {
        this.listaUnidades = new ArrayList<SelectItem>();
        Unidad unid = new Unidad(0, "SELECCIONE", "");
        this.listaUnidades.add(new SelectItem(unid, unid.toString()));
        try {
            DAOUnidades daoUnidades = new DAOUnidades();
            ArrayList<Unidad> lstUnidades = daoUnidades.obtenerUnidades();
            for (Unidad u : lstUnidades) {
                this.listaUnidades.add(new SelectItem(u, u.toString()));
            }
        } catch (NamingException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
     public void cargaMarcas() {
     this.listaMarcas = new ArrayList<SelectItem>();
     Marca mark = new Marca(0, 0, "SELECCIONE UNA MARCA", 0);
     this.listaMarcas.add(new SelectItem(mark, mark.toString()));
     try {
     DAOMarcas daoMarcas = new DAOMarcas();
     ArrayList<Marca> lstMarcas = daoMarcas.obtenerMarcas();
     for (Marca m : lstMarcas) {
     this.listaMarcas.add(new SelectItem(m, m.toString()));
     }
     } catch (NamingException ex) {
     Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
     } catch (SQLException ex) {
     Logger.getLogger(MbProducto.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     * */

    public void cargaSubGrupos() {
        this.listaSubGrupos = new ArrayList<SelectItem>();
        SubGrupo sgpo = new SubGrupo(0, "SELECCIONE");
        this.listaSubGrupos.add(new SelectItem(sgpo, sgpo.toString()));
        
        for (SubGrupo sg : this.mbSubGrupo.obtenerSubGrupos(this.producto.getGrupo().getIdGrupo())) {
            listaSubGrupos.add(new SelectItem(sg, sg.toString()));
        }
    }

    public void cargaGrupos() {
        this.listaGrupos = new ArrayList<SelectItem>();
        Grupo gpo = new Grupo(0, 0, "SELECCIONE");
        this.listaGrupos.add(new SelectItem(gpo, gpo.toString()));
        
        for (Grupo g : this.mbGrupo.obtenerGrupos()) {
            listaGrupos.add(new SelectItem(g, g.toString()));
        }
    }

    public void cargaTipos() {
        this.listaTipos = new ArrayList<SelectItem>();
        Tipo tipo = new Tipo(0, "SELECCIONE");
        this.listaTipos.add(new SelectItem(tipo, tipo.toString()));
        
        for (Tipo t : this.mbTipo.obtenerTipos()) {
            listaTipos.add(new SelectItem(t, t.toString()));
        }
    }

    public void cargaListaUpcs() {
        this.listaUpcs = new ArrayList<SelectItem>();
        if (this.producto.getIdProducto() > 0) {
            Upc xUpc = new Upc(this.producto.getIdProducto());
            xUpc.setIdProducto(this.producto.getIdProducto());
            this.listaUpcs.add(new SelectItem(xUpc, xUpc.toString()));
        }
        for (Upc u : this.producto.getUpcs()) {
            this.listaUpcs.add(new SelectItem(u, u.toString()));
        }
    }

    public ArrayList<Parte2> completePartes(String query) {
        ArrayList<Parte2> partes = null;
        try {
            DAOPartes daoPartes = new DAOPartes();
            partes = daoPartes.completePartes(query);
        } catch (SQLException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbParte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return partes;
    }

    public ArrayList<Parte> buscarPartes() throws SQLException {
        return this.dao.obtenerPartes();
    }

    public void limpiar() {
        this.productos.clear();
    }

    public ArrayList<SelectItem> getListaTipos() {
        if(this.listaTipos==null) {
            this.cargaTipos();
        }
        return listaTipos;
    }

    public void setListaTipos(ArrayList<SelectItem> listaTipos) {
        this.listaTipos = listaTipos;
    }
    /*
     public Grupo getGrupo() {
     return grupo;
     }

     public void setGrupo(Grupo grupo) {
     this.grupo = grupo;
     }
     * */

    public ArrayList<SelectItem> getListaGrupos() {
        if (this.listaGrupos == null) {
            this.cargaGrupos();
        }
        return listaGrupos;
    }

    public void setListaGrupos(ArrayList<SelectItem> listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    public ArrayList<SelectItem> getListaSubGrupos() {
        if (this.listaSubGrupos == null) {
            this.cargaSubGrupos();
        }
        return listaSubGrupos;
    }

    public void setListaSubGrupos(ArrayList<SelectItem> listaSubGrupos) {
        this.listaSubGrupos = listaSubGrupos;
    }
    /*
     public ArrayList<SelectItem> getListaMarcas() {
     return listaMarcas;
     }

     public void setListaMarcas(ArrayList<SelectItem> listaMarcas) {
     this.listaMarcas = listaMarcas;
     }
     * */

    public ArrayList<SelectItem> getListaUnidades() {
        if (this.listaUnidades == null) {
            this.cargaUnidades();
        }
        return listaUnidades;
    }

    public void setListaUnidades(ArrayList<SelectItem> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public ArrayList<SelectItem> getListaUnidadesMedida() {
        if(this.listaUnidadesMedida==null) {
            this.cargaUnidadesMedida();
        }
        return listaUnidadesMedida;
    }

    public void setListaUnidadesMedida(ArrayList<SelectItem> listaUnidadesMedida) {
        this.listaUnidadesMedida = listaUnidadesMedida;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public FrmImpuestos getMbImpuesto() {
        return mbImpuesto;
    }

    public void setMbImpuesto(FrmImpuestos mbImpuesto) {
        this.mbImpuesto = mbImpuesto;
    }

    public MbParte getMbParte() {
        return mbParte;
    }

    public void setMbParte(MbParte mbParte) {
        this.mbParte = mbParte;
    }

    public Upc getUpc() {
        return upc;
    }

    public void setUpc(Upc upc) {
        this.upc = upc;
    }

    public ArrayList<SelectItem> getListaUpcs() {
        return listaUpcs;
    }

    public void setListaUpcs(ArrayList<SelectItem> listaUpcs) {
        this.listaUpcs = listaUpcs;
    }

    public MbUpc getMbUpc() {
        return mbUpc;
    }

    public void setMbUpc(MbUpc mbUpc) {
        this.mbUpc = mbUpc;
    }

    public MbGrupo getMbGrupo() {
        return mbGrupo;
    }

    public void setMbGrupo(MbGrupo mbGrupo) {
        this.mbGrupo = mbGrupo;
    }

    public MbSubGrupo getMbSubGrupo() {
        return mbSubGrupo;
    }

    public void setMbSubGrupo(MbSubGrupo mbSubGrupo) {
        this.mbSubGrupo = mbSubGrupo;
    }

    public MbUnidadProducto getMbUnidadProducto() {
        return mbUnidadProducto;
    }

    public void setMbUnidadProducto(MbUnidadProducto mbUnidadProducto) {
        this.mbUnidadProducto = mbUnidadProducto;
    }

    public MbTipo getMbTipo() {
        return mbTipo;
    }

    public void setMbTipo(MbTipo mbTipo) {
        this.mbTipo = mbTipo;
    }
    /*
    public SelectItem[] getArrayTipos() {
        return arrayTipos;
    }

    public void setArrayTipos(SelectItem[] arrayTipos) {
        this.arrayTipos = arrayTipos;
    }

    public SelectItem[] getArrayGrupos() {
        return arrayGrupos;
    }

    public void setArrayGrupos(SelectItem[] arrayGrupos) {
        this.arrayGrupos = arrayGrupos;
    }

    public SelectItem[] getArraySubGrupos() {
        return arraySubGrupos;
    }

    public void setArraySubGrupos(SelectItem[] arraySubGrupos) {
        this.arraySubGrupos = arraySubGrupos;
    }
    * */
}
