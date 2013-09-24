package ordenCompra;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import ordenCompra.dominio.Factura;

/**
 *
 * @author jsolis
 */
@Named(value = "mbFacturas")
@SessionScoped
public class MbFacturas implements Serializable {
    private Factura factura;
    private ArrayList<Factura> facturas;
    
    public MbFacturas() {
        this.factura=new Factura();
        this.facturas=new ArrayList<Factura>();
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public ArrayList<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas;
    }
}
