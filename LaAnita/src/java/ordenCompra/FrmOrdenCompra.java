package ordenCompra;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.bean.ManagedProperty;
import productos.MbBuscarProd;

/**
 *
 * @author jsolis
 */
@Named(value = "frmOrdenCompra")
@SessionScoped
public class FrmOrdenCompra implements Serializable {
    @ManagedProperty(value = "#{mbBuscarProd}")
    private MbBuscarProd mbBuscarProd;
    @ManagedProperty(value = "#{mbOrdenCompra}")
    private MbOrdenCompra mbOrdenCompra;
    
    public FrmOrdenCompra() {
        this.mbBuscarProd=new MbBuscarProd();
        this.mbOrdenCompra=new MbOrdenCompra();
    }
    
    public String terminar() {
        return "productos.terminar";
    }

    public MbBuscarProd getMbBuscarProd() {
        return mbBuscarProd;
    }

    public void setMbBuscarProd(MbBuscarProd mbBuscarProd) {
        this.mbBuscarProd = mbBuscarProd;
    }

    public MbOrdenCompra getMbOrdenCompra() {
        return mbOrdenCompra;
    }

    public void setMbOrdenCompra(MbOrdenCompra mbOrdenCompra) {
        this.mbOrdenCompra = mbOrdenCompra;
    }
}
