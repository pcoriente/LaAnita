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
    
    public FrmProducto() {
        this.mbProducto = new MbProducto();
        this.mbBuscarProd = new MbBuscarProd();
    }
    
    public String terminar() {
        this.mbProducto.setListaTipos(null);
        this.mbProducto.setListaGrupos(null);
        this.mbProducto.setListaUnidades(null);
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
}
