package proveedores.dominio;

import impuestos.dominio.ImpuestoGrupo;
import productos.dominio.Marca;
import productos.dominio.UnidadEmpaque;
import unidadesMedida.UnidadMedida;

/**
 *
 * @author jsolis
 */
public class ProveedorProducto {
    private int idProducto;
    private String sku;
    private String producto;
    private Marca marca;
    private UnidadEmpaque unidadEmpaque;
    private int piezas;
    private double contenido;
    private UnidadMedida unidadMedida;
    private UnidadMedida unidadMedida2;
    private ImpuestoGrupo impuestoGrupo;

    public ProveedorProducto() {
        this.idProducto=0;
        this.sku="";
        this.producto="";
        this.marca=new Marca();
        this.unidadEmpaque=new UnidadEmpaque();
        this.piezas=0;
        this.contenido=0;
        this.unidadMedida=new UnidadMedida(0, "", "", 0);
        this.unidadMedida2=new UnidadMedida(0, "", "", 0);
        this.impuestoGrupo=new ImpuestoGrupo(0, "");
    }

    @Override
    public String toString() {
        return (this.marca==null || this.marca.getIdMarca()==0 ? "" : this.marca.toString()+" ") + this.producto
                + (this.unidadEmpaque==null || this.unidadEmpaque.getIdUnidad()==1 ? "" : " " + this.unidadEmpaque.getAbreviatura()) 
                + (this.contenido == 0 ? "" : " " + Double.toString(this.contenido))
                + (this.contenido == 0 || this.unidadMedida == null ? "" : " " + this.unidadMedida.getAbreviatura());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.idProducto;
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
        final ProveedorProducto other = (ProveedorProducto) obj;
        if (this.idProducto != other.idProducto) {
            return false;
        }
        return true;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public UnidadEmpaque getUnidadEmpaque() {
        return unidadEmpaque;
    }

    public void setUnidadEmpaque(UnidadEmpaque unidadEmpaque) {
        this.unidadEmpaque = unidadEmpaque;
    }

    public int getPiezas() {
        return piezas;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }

    public double getContenido() {
        return contenido;
    }

    public void setContenido(double contenido) {
        this.contenido = contenido;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public UnidadMedida getUnidadMedida2() {
        return unidadMedida2;
    }

    public void setUnidadMedida2(UnidadMedida unidadMedida2) {
        this.unidadMedida2 = unidadMedida2;
    }

    public ImpuestoGrupo getImpuestoGrupo() {
        return impuestoGrupo;
    }

    public void setImpuestoGrupo(ImpuestoGrupo impuestoGrupo) {
        this.impuestoGrupo = impuestoGrupo;
    }
}
