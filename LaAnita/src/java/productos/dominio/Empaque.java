package productos.dominio;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author JULIOS
 */
public class Empaque implements Serializable {
    private int idEmpaque;
    private int idEmpresa;
    private String cod_pro;
    private Producto producto;
    private int piezas;
    private UnidadEmpaque unidadEmpaque;
    private SubEmpaque subEmpaque;
    private ArrayList<SubEmpaque> subEmpaques;
    private String dun14;
    private double peso;
    private double volumen;

    public Empaque(int idEmpaque) {
        this.idEmpaque=idEmpaque;
        this.idEmpresa=0;
        this.cod_pro="";
        this.producto=new Producto(0);
        this.piezas=0;
        this.unidadEmpaque=new UnidadEmpaque();
        this.subEmpaque=new SubEmpaque(0);
        this.subEmpaques=new ArrayList<SubEmpaque>();
        this.dun14="";
        this.peso=0;
        this.volumen=0;
    }

    public Empaque() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    @Override
    public String toString() {
        String empaque=producto.toString();
        if(this.piezas > 1) {
            empaque+=" " + this.unidadEmpaque.toString()+" C/"+Integer.toString(this.piezas);
            if(this.subEmpaque.getIdEmpaque()==0) {
                empaque+="";
            } else if(this.subEmpaque.getIdEmpaque()!=0) {
                empaque+=" "+this.subEmpaque.getUnidadEmpaque().getAbreviatura()+" x "+Integer.toString(this.subEmpaque.getPiezas());
            }
        }
        return empaque;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + this.idEmpaque;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Empaque other = (Empaque) obj;
        if (this.idEmpaque != other.idEmpaque) {
            return false;
        }
        return true;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getCod_pro() {
        return cod_pro;
    }

    public void setCod_pro(String cod_pro) {
        this.cod_pro = cod_pro;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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

    public ArrayList<SubEmpaque> getSubEmpaques() {
        return subEmpaques;
    }

    public void setSubEmpaques(ArrayList<SubEmpaque> subEmpaques) {
        this.subEmpaques = subEmpaques;
    }
}
