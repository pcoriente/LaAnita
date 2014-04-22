package producto2.to;

/**
 *
 * @author jesc
 */
public class TOProductoCombo {
    private int idProducto;
    private int piezas;
    
    public TOProductoCombo() {}
    
    public TOProductoCombo(int idProducto, int piezas) {
        this.idProducto=idProducto;
        this.piezas=piezas;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getPiezas() {
        return piezas;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }
}
