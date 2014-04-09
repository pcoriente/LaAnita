package productos;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;

/**
 *
 * @author JULIOS
 */
@Named(value = "frmProducto")
@SessionScoped
public class FrmProducto implements Serializable {
    @ManagedProperty(value = "#{mbProducto}")
    private MbProducto mbProducto;
    @ManagedProperty(value = "#{mbBuscarProd}")
    private MbBuscarProd mbBuscarProd;
    @ManagedProperty(value = "#{mbEmpaque}")
    private MbEmpaque mbEmpaque;
    
    public FrmProducto() {
        this.mbProducto = new MbProducto();
        this.mbBuscarProd = new MbBuscarProd();
        this.mbEmpaque = new MbEmpaque();
    }
    
    public void nuevoEmpaque() {
        this.mbEmpaque.nuevoEmpaque(this.mbProducto.getProducto());
    }
    
    public void actualizaProductoSeleccionado() {
        this.mbProducto.setProducto(this.mbBuscarProd.obtenerSeleccionado());
        this.mbProducto.cargaListaUpcs();
        if(!this.mbProducto.getProducto().getUpcs().isEmpty()) {
            this.mbProducto.getMbUpc().setUpc(this.mbProducto.getProducto().getUpcs().get(0));
        }
        this.mbProducto.getMbSubGrupo().cargaSubGrupos(this.mbProducto.getProducto().getGrupo().getIdGrupo());
    }
    
    public void buscar() {
        this.mbBuscarProd.buscarLista();
        if(this.mbBuscarProd.getProducto()!=null) {
            this.mbProducto.setProducto(this.mbBuscarProd.getProducto());
        }
    }
    
    public String terminar() {
        this.mbProducto.setListaTipos(null);
        this.mbProducto.setListaGrupos(null);
        this.mbProducto.getMbPresentacion().setListaPresentaciones(null);
        this.mbProducto.setListaUnidadesMedida(null);
        this.mbProducto.getMbImpuesto().getMbGrupos().setListaGrupos(null);
        return "productos.terminar";
    }
    
    public void eliminarProductoUpc() {
        if(this.mbProducto.eliminarUpc()) {
            //this.mbEmpaque.getEmpaque().getProducto().setUpcs(this.mbProducto.getProducto().getUpcs());
            //this.mbEmpaque.cargaListaUpcs();
        }
    }
    
    public void agregarProductoUpc() {
        if(this.mbProducto.agregarUpc()) {
            //this.mbEmpaque.getEmpaque().getProducto().setUpcs(this.mbProducto.getProducto().getUpcs());
            //this.mbEmpaque.cargaListaUpcs();
        }
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

    public MbEmpaque getMbEmpaque() {
        return mbEmpaque;
    }

    public void setMbEmpaque(MbEmpaque mbEmpaque) {
        this.mbEmpaque = mbEmpaque;
    }
}
