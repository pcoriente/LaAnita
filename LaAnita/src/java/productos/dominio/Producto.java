package productos.dominio;

import impuestos.dominio.ImpuestoGrupo;
import java.io.Serializable;
import java.util.ArrayList;
import unidadesMedida.UnidadMedida;

/**
 *
 * @author JULIOS
 */
public class Producto implements Serializable {
    private int idProducto;
    private Parte2 parte;
    private String descripcion;
    private ArrayList<Upc> upcs;
    private Tipo tipo;
    private Grupo grupo;
    private SubGrupo subGrupo;
    private Marca marca;
    private Presentacion presentacion;
    //private String contenido;
    private double contenido;
    private UnidadMedida unidadMedida;
    private ImpuestoGrupo impuesto;

    public Producto(int idProducto) {
        this.idProducto = idProducto;
        this.parte=new Parte2(0, "");
        this.descripcion="";
        this.upcs=new ArrayList<Upc>();
        this.tipo=new Tipo(0, "");
        this.grupo=new Grupo(0, 0, "");
        this.subGrupo=new SubGrupo(0, "");
        this.marca=new Marca(0, "", false);
        this.presentacion=new Presentacion(0, "", "");
        //this.contenido="0.0";
        this.contenido=0;
        this.unidadMedida=new UnidadMedida(0, "", "");
        this.impuesto=new ImpuestoGrupo(0, "");
    }
    
    @Override
    public String toString() {
        return (this.marca==null || this.marca.getIdMarca()==0 ? "" : this.marca.toString()+" ")
                + this.parte + (this.descripcion.equals("") ? "" : " "+this.descripcion) 
                + (this.presentacion==null || this.presentacion.getIdPresentacion()==1 ? "" : " " + this.presentacion.getAbreviatura()) 
                + (this.contenido == 0 ? "" : " " + Double.toString(this.contenido))
                + (this.contenido == 0 || this.unidadMedida == null ? "" : " " + this.unidadMedida.getAbreviatura());
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Parte2 getParte2() {
        return parte;
    }

    public void setParte2(Parte2 parte) {
        this.parte = parte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    /*
    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
    * */
    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public SubGrupo getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(SubGrupo subGrupo) {
        this.subGrupo = subGrupo;
    }

    public Presentacion getPresentacion() {
        if(this.presentacion==null) {
            this.presentacion=new Presentacion();
        }
        return presentacion;
    }

    public void setPresentacion(Presentacion presentacion) {
        this.presentacion = presentacion;
    }
    /*
    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    * */
    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public ImpuestoGrupo getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(ImpuestoGrupo impuesto) {
        this.impuesto = impuesto;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public ArrayList<Upc> getUpcs() {
        return upcs;
    }

    public void setUpcs(ArrayList<Upc> upcs) {
        this.upcs = upcs;
    }

    public Parte2 getParte() {
        return parte;
    }

    public void setParte(Parte2 parte) {
        this.parte = parte;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public double getContenido() {
        return contenido;
    }

    public void setContenido(double contenido) {
        this.contenido = contenido;
    }
}
