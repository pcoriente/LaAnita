package entradas.dominio;

import productos.dominio.Empaque;

/**
 *
 * @author jsolis
 */
public class EntradaProducto {
    private int idOC;
    private Empaque empaque;
    private double cantOrdenada;
    private double cantRecibida;
    private double precio;
    private double desctoProducto1;
    private double desctoProducto2;
    private double desctoConfidencial;
    private double unitario;
    private double neto;
    private double importe;

    public EntradaProducto() {
        this.idOC=0;
        this.empaque=new Empaque(0);
        this.cantOrdenada=0.00;
        this.cantRecibida=0.00;
        this.precio=0.00;
        this.desctoProducto1=0.00;
        this.desctoProducto2=0.00;
        this.desctoConfidencial=0.00;
        this.unitario=0.00;
        this.neto=0.00;
        this.importe=0.00;
    }

    @Override
    public String toString() {
        return this.empaque.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.empaque != null ? this.empaque.hashCode() : 0);
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
        final EntradaProducto other = (EntradaProducto) obj;
        if (this.empaque != other.empaque && (this.empaque == null || !this.empaque.equals(other.empaque))) {
            return false;
        }
        return true;
    }

    public Empaque getEmpaque() {
        return empaque;
    }

    public void setEmpaque(Empaque empaque) {
        this.empaque = empaque;
    }

    public double getCantOrdenada() {
        return cantOrdenada;
    }

    public void setCantOrdenada(double cantOrdenada) {
        this.cantOrdenada = cantOrdenada;
    }

    public double getCantRecibida() {
        return cantRecibida;
    }

    public void setCantRecibida(double cantRecibida) {
        this.cantRecibida = cantRecibida;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDesctoProducto1() {
        return desctoProducto1;
    }

    public void setDesctoProducto1(double desctoProducto1) {
        this.desctoProducto1 = desctoProducto1;
    }

    public double getDesctoProducto2() {
        return desctoProducto2;
    }

    public void setDesctoProducto2(double desctoProducto2) {
        this.desctoProducto2 = desctoProducto2;
    }

    public double getDesctoConfidencial() {
        return desctoConfidencial;
    }

    public void setDesctoConfidencial(double desctoConfidencial) {
        this.desctoConfidencial = desctoConfidencial;
    }

    public int getIdOC() {
        return idOC;
    }

    public void setIdOC(int idOC) {
        this.idOC = idOC;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getUnitario() {
        return unitario;
    }

    public void setUnitario(double unitario) {
        this.unitario = unitario;
    }

    public double getNeto() {
        return neto;
    }

    public void setNeto(double neto) {
        this.neto = neto;
    }
}
