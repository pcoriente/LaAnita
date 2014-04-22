/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientes.dominio;

import contribuyentes.Contribuyente;
import impuestos.dominio.ImpuestoZona;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Pjgt
 */
public class Cliente implements Serializable {

    private int idCliente;
    private int codigoCliente;
    private Contribuyente contribuyente = new Contribuyente();
    private ImpuestoZona impuestoZona = new ImpuestoZona();
    private Date fechaAlta;
    private int diasCredito;
    private float limiteCredito;
    private float descuentoComercial;
    private double descuentoProntoPago;
    private int diasBloqueo;

    public Cliente() {
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public ImpuestoZona getImpuestoZona() {
        return impuestoZona;
    }

    public void setImpuestoZona(ImpuestoZona impuestoZona) {
        this.impuestoZona = impuestoZona;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getDiasCredito() {
        return diasCredito;
    }

    public void setDiasCredito(int diasCredito) {
        this.diasCredito = diasCredito;
    }

    public float getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(float limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public float getDescuentoComercial() {
        return descuentoComercial;
    }

    public void setDescuentoComercial(float descuentoComercial) {
        this.descuentoComercial = descuentoComercial;
    }

    public double getDescuentoProntoPago() {
        return descuentoProntoPago;
    }

    public void setDescuentoProntoPago(double descuentoProntoPago) {
        this.descuentoProntoPago = descuentoProntoPago;
    }

    

    public int getDiasBloqueo() {
        return diasBloqueo;
    }

    public void setDiasBloqueo(int diasBloqueo) {
        this.diasBloqueo = diasBloqueo;
    }
}
