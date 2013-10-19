package productosOld;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import productos.MbBuscarProd;
import productos.MbEmpaque;
import productos.MbProducto;
import productos.dao.DAOTipos;
import productos.dominio.Empaque;
import productos.dominio.Grupo;
import productos.dominio.SubEmpaque;
import productos.dominio.SubGrupo;
import productos.dominio.Presentacion;
import productos.dominio.UnidadEmpaque;
import productos.dominio.Upc;
import productosOld.dominio.ProductoOld;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbProductoOld")
@SessionScoped
public class MbProductoOld implements Serializable {
    /*
     private MiniEmpresa empresa;
     private ArrayList<SelectItem> lstEmpresas;
     @ManagedProperty(value = "#{mbMiniEmpresa}")
     private MbMiniEmpresa mbMiniEmpresa;
    
     private ArrayList<ProductoOld> lstProductosOld;
     * */

    private ProductoOld productoOld;
    @ManagedProperty(value = "#{mbBuscarOld}")
    private MbBuscarOld mbBuscarOld;
    @ManagedProperty(value = "#{mbEmpaque}")
    private MbEmpaque mbEmpaque;
    @ManagedProperty(value = "#{mbProducto}")
    private MbProducto mbProducto;
    @ManagedProperty(value = "#{mbBuscarProd}")
    private MbBuscarProd mbBuscarProd;

    public MbProductoOld() {
        //try {
            /*
         this.empresa=new MiniEmpresa();
         this.empresa.setIdEmpresa(0);
         this.empresa.setCodigoEmpresa("0");
         this.empresa.setNombreComercial("SELECCIONE UNA EMPRESA");
         this.mbMiniEmpresa = new MbMiniEmpresa();
         */
        this.mbBuscarOld = new MbBuscarOld();
        this.mbBuscarProd = new MbBuscarProd();
        this.mbEmpaque = new MbEmpaque();
        this.mbProducto = new MbProducto();
        this.mbEmpaque.setCod_emp("");
        //this.mbEmpaque.setOld(true);
        //} catch (NamingException ex) {
        //    Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        //}
    }

    public String terminar() {
        String destino = "menuProductosOld.terminar";
        /*
         this.empresa=new MiniEmpresa();
         this.empresa.setIdEmpresa(0);
         this.empresa.setCodigoEmpresa("0");
         this.empresa.setNombreComercial("SELECCIONE UNA EMPRESA");
         this.lstEmpresas=null;
         this.lstProductosOld=null;
         * */
        return destino;
    }

    public String salir() {
        String destino = this.mbEmpaque.salir();
        try {
            this.mbBuscarOld.buscar();
        } catch (SQLException ex) {
            destino = null;
            Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.lstProductosOld=null;
        return destino;
    }
    
    public void eliminarProductoUpc() {
        if(this.mbProducto.eliminarUpc()) {
            this.mbEmpaque.getEmpaque().getProducto().setUpcs(this.mbProducto.getProducto().getUpcs());
            this.mbEmpaque.cargaListaUpcs();
        }
    }
    
    public void agregarProductoUpc() {
        if(this.mbProducto.agregarUpc()) {
            this.mbEmpaque.getEmpaque().getProducto().setUpcs(this.mbProducto.getProducto().getUpcs());
            this.mbEmpaque.cargaListaUpcs();
        }
    }
    
    public void grabarProducto() {
        RequestContext context = RequestContext.getCurrentInstance();
        boolean oki = this.mbProducto.grabar();
        if (oki) {
            this.mbEmpaque.getEmpaque().setProducto(this.mbProducto.getProducto());
            this.mbEmpaque.cargaListaUpcs();
        }
        context.addCallbackParam("okProducto", oki);
    }
    
    public void buscarProducto() {
        this.mbBuscarProd.inicializar();
    }
    
    public void buscar() {
        this.mbBuscarProd.buscarLista();
        if(this.mbBuscarProd.getProducto()!=null) {
            this.mbEmpaque.getEmpaque().setProducto(this.mbBuscarProd.getProducto());
        }
    }
    
    public void actualizaProductoSeleccionado() {
        this.mbEmpaque.getEmpaque().setProducto(this.mbBuscarProd.obtenerSeleccionado());
    }
    
    public void mttoProducto() {
        this.mbProducto.setProducto(this.mbProducto.miCopia(this.mbEmpaque.getEmpaque().getProducto()));
        this.mbProducto.cargaListaUpcs();
        if(this.mbProducto.getProducto().getUpcs().isEmpty()) {
            this.mbProducto.getMbUpc().setUpc(new Upc(this.mbProducto.getProducto().getIdProducto()));
        } else {
            this.mbProducto.getMbUpc().setUpc(this.mbProducto.getProducto().getUpcs().get(this.mbProducto.getProducto().getUpcs().size()-1));
        }
        this.mbProducto.cargaSubGrupos();
    }

    public String mantenimiento(ProductoOld productoOld) {
        String destino = "productosOld.mantenimiento";
        this.productoOld = productoOld;
        try {
            this.mbEmpaque.setCod_emp(this.productoOld.getCod_emp());
            Empaque empaque = this.mbEmpaque.obtenerEmpaque(this.productoOld.getCod_pro());
            if (empaque == null) {
                empaque = new Empaque(0);
                empaque.setIdEmpresa(Integer.parseInt(this.productoOld.getCod_emp()));
                empaque.setCod_pro(this.productoOld.getCod_pro());
                if(!this.productoOld.getCodbar().trim().isEmpty()) {
                    empaque.getProducto().getUpcs().add(new Upc(0, this.productoOld.getCodbar().trim() ,0));
                }
                empaque.getProducto().setDescripcion("");
                int idTipo;
                String cod_inv;
                cod_inv = productoOld.getGrupo().substring(1, 3);
                if (cod_inv.equals("01")) {
                    idTipo = 2;   // Materia Prima
                } else if (cod_inv.equals("02")) {
                    idTipo = 3;   // Material de Empaque
                } else if (cod_inv.equals("03") || cod_inv.equals("04")) {
                    idTipo = 4;   // Semiterminados
                } else if (cod_inv.equals("90")) {
                    idTipo = 6;   // Servicios
                } else {
                    idTipo = 5;   // Terminados
                }
                try {
                    DAOTipos daoTipo = new DAOTipos();
                    empaque.getProducto().setTipo(daoTipo.obtenerTipo(idTipo));
                } catch (NamingException ex) {
                    throw (ex);
                }
                empaque.getProducto().setGrupo(new Grupo(0, 0, ""));
                empaque.getProducto().setSubGrupo(new SubGrupo(0, ""));
                empaque.getProducto().setPresentacion(new Presentacion(0, "", ""));
                empaque.setUnidadEmpaque(new UnidadEmpaque(0, "", ""));
                empaque.setPiezas(0);
                empaque.setSubEmpaque(new SubEmpaque(0, 0, new UnidadEmpaque(0, "", "")));
                empaque.setPeso(this.productoOld.getPeso());
                empaque.setVolumen(this.productoOld.getVolumen());
            }
            //this.mbEmpaque.setOk(true);
            this.mbEmpaque.setEmpaque(empaque);
            //this.mbEmpaque.cargaMarcas();
            this.mbEmpaque.cargaListaUpcs();
            if (this.mbProducto.getListaTipos() == null) {
                this.mbProducto.cargaTipos();
                this.mbProducto.cargaGrupos();
                this.mbProducto.cargaUnidadesMedida();
            }
            this.mbProducto.getProducto().setGrupo(empaque.getProducto().getGrupo());
            this.mbProducto.cargaSubGrupos();
            this.mbProducto.cargaMarcas();
        } catch (SQLException ex) {
            Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destino;
    }
    /*
     public void cargaListaProductosOld() {
     try {
     this.dao = new DAOProductoOld();
     this.lstProductosOld = this.dao.obtenerProductos(this.empresa.getCodigoEmpresa());
     } catch (NamingException ex) {
     Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
     } catch (SQLException ex) {
     Logger.getLogger(MbProductoOld.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
    
     public MiniEmpresa getEmpresa() {
     return empresa;
     }

     public void setEmpresa(MiniEmpresa empresa) {
     this.empresa = empresa;
     }
     * */

    public MbEmpaque getMbEmpaque() {
        return mbEmpaque;
    }

    public void setMbEmpaque(MbEmpaque mbEmpaque) {
        this.mbEmpaque = mbEmpaque;
    }
    /*
     public ArrayList<SelectItem> getLstEmpresas() throws SQLException {
     if (lstEmpresas == null) {
     this.lstEmpresas = this.mbMiniEmpresa.obtenerListaMiniEmpresas();
     }
     return lstEmpresas;
     }

     public void setLstEmpresas(ArrayList<SelectItem> lstEmpresas) {
     this.lstEmpresas = lstEmpresas;
     }

     public MbMiniEmpresa getMbMiniEmpresa() {
     return mbMiniEmpresa;
     }

     public void setMbMiniEmpresa(MbMiniEmpresa mbMiniEmpresa) {
     this.mbMiniEmpresa = mbMiniEmpresa;
     }
     */

    public ProductoOld getProductoOld() {
        return productoOld;
    }

    public void setProductoOld(ProductoOld productoOld) {
        this.productoOld = productoOld;
    }
    /*
     public ArrayList<ProductoOld> getLstProductosOld() {
     if(this.lstProductosOld==null) {
     this.cargaListaProductosOld();
     }
     return lstProductosOld;
     }

     public void setLstProductosOld(ArrayList<ProductoOld> lstProductosOld) {
     this.lstProductosOld = lstProductosOld;
     }
     * */

    public MbBuscarOld getMbBuscarOld() {
        return mbBuscarOld;
    }

    public void setMbBuscarOld(MbBuscarOld mbBuscarOld) {
        this.mbBuscarOld = mbBuscarOld;
    }

    public MbProducto getMbProducto() {
        return mbProducto;
    }

    public void setMbProducto(MbProducto mbProducto) {
        this.mbProducto = mbProducto;
    }

    public MbBuscarProd getMbBuscarProd() {
        return mbBuscarProd;
    }

    public void setMbBuscarProd(MbBuscarProd mbBuscarProd) {
        this.mbBuscarProd = mbBuscarProd;
    }
}
