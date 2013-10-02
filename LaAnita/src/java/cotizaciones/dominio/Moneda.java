/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominios;

import java.io.Serializable;

/**
 *
 * @author Comodoro
 */
public class Moneda implements Serializable {

    private int idMoneda;
    private String moneda;
    private String codigoIso;
    private String prefijoUnidad;
    private String prefijo;
    private String sufijo;
    private String simbolo;

    public Moneda(int idMoneda, String moneda, String codigoIso) {
        this.idMoneda = idMoneda;
        this.moneda = moneda;
        this.codigoIso= codigoIso;
    }

    public Moneda() {
        this.idMoneda=0;
        this.moneda="";
        this.codigoIso="";
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCodigoIso() {
        return codigoIso;
    }

    public void setCodigoIso(String codigoIso) {
        this.codigoIso = codigoIso;
    }

    public String getPrefijoUnidad() {
        return prefijoUnidad;
    }

    public void setPrefijoUnidad(String prefijoUnidad) {
        this.prefijoUnidad = prefijoUnidad;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getSufijo() {
        return sufijo;
    }

    public void setSufijo(String sufijo) {
        this.sufijo = sufijo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
}
