/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesListas.formatosDetalleDominio;

import java.io.Serializable;
import java.util.Date;
import producto2.dominio.Empaque;
import producto2.dominio.Producto;

/**
 *
 * @author Usuario
 */
public class ClienteListasDetalle implements Serializable {

    private int idFormato;
    private Producto producto = new Producto();
    private double precioLista;
    private double descuentos;
    private Date finVigencia;

    public int getIdFormato() {
        return idFormato;
    }

    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }

    public double getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(double precioLista) {
        this.precioLista = precioLista;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(double descuentos) {
        this.descuentos = descuentos;
    }

    public Date getFinVigencia() {
        return finVigencia;
    }

    public void setFinVigencia(Date finVigencia) {
        this.finVigencia = finVigencia;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.producto != null ? this.producto.hashCode() : 0);
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
        final ClienteListasDetalle other = (ClienteListasDetalle) obj;
        if (this.producto != other.producto && (this.producto == null || !this.producto.equals(other.producto))) {
            return false;
        }
        return true;
    }

}
