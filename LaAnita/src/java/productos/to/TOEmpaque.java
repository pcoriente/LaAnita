package productos.to;

import productos.dominio.SubEmpaque;
import productos.dominio.UnidadEmpaque;

/**
 *
 * @author jesc
 */
public class TOEmpaque {
    private int idEmpaque;
    private String cod_pro;
    private int idProducto;
    private int piezas;
    private UnidadEmpaque unidadEmpaque;
    private SubEmpaque subEmpaque;
    private String dun14;
    private double peso;
    private double volumen;
    
    public TOEmpaque() {
        this.cod_pro="";
        this.unidadEmpaque=new UnidadEmpaque();
        this.dun14="";
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }

    public String getCod_pro() {
        return cod_pro;
    }

    public void setCod_pro(String cod_pro) {
        this.cod_pro = cod_pro;
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

    public UnidadEmpaque getUnidadEmpaque() {
        return unidadEmpaque;
    }

    public void setUnidadEmpaque(UnidadEmpaque unidadEmpaque) {
        this.unidadEmpaque = unidadEmpaque;
    }

    public SubEmpaque getSubEmpaque() {
        return subEmpaque;
    }

    public void setSubEmpaque(SubEmpaque subEmpaque) {
        this.subEmpaque = subEmpaque;
    }

    public String getDun14() {
        return dun14;
    }

    public void setDun14(String dun14) {
        this.dun14 = dun14;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }
}
