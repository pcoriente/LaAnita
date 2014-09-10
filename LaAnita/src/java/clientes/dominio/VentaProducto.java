package clientes.dominio;

import entradas.dominio.MovimientoProducto;
import java.util.ArrayList;
import movimientos.dominio.Lote;
import producto2.dominio.Producto;

/**
 *
 * @author jesc
 */
public class VentaProducto extends MovimientoProducto {
    private double separados;
    private ArrayList<Lote> lotes;
    
    public VentaProducto() {
        super();
        this.lotes=new ArrayList<Lote>();
    }
    
    public VentaProducto(Producto producto) {
        this.setProducto(producto);
    }

    public double getSeparados() {
        return separados;
    }

    public void setSeparados(double separados) {
        this.separados = separados;
    }

    public ArrayList<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(ArrayList<Lote> lotes) {
        this.lotes = lotes;
    }
}
