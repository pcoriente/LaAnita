package productos;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;
import productos.dominio.Empaque;
import productos.dominio.Parte;
import productos.dominio.Producto;

/**
 *
 * @author JULIOS
 */
@Named(value = "frmEmpaque")
@SessionScoped
public class FrmEmpaque implements Serializable {
    private Empaque empaque;
    private Producto producto;
    private Parte parte;
    
    @ManagedProperty(value = "#{mbEmpaque}")
    private MbEmpaque mbEmpaque;
    @ManagedProperty(value = "#{mbParte}")
    private MbParte mbParte;
    
    public FrmEmpaque() {
        mbEmpaque=new MbEmpaque();
        mbParte=new MbParte();
    }
    
    public void actualizaProducto() {
        this.producto=this.empaque.getProducto();
    }

    public MbEmpaque getMbEmpaque() {
        return mbEmpaque;
    }

    public void setMbEmpaque(MbEmpaque mbEmpaque) {
        this.mbEmpaque = mbEmpaque;
    }

    public Empaque getEmpaque() {
        return empaque;
    }

    public void setEmpaque(Empaque empaque) {
        this.empaque = empaque;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Parte getParte() {
        return parte;
    }

    public void setParte(Parte parte) {
        this.parte = parte;
    }

    public MbParte getMbParte() {
        return mbParte;
    }

    public void setMbParte(MbParte mbParte) {
        this.mbParte = mbParte;
    }
}
