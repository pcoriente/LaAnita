package pedidos.dominio;

import java.util.Date;

/**
 *
 * @author Julio
 */
public class Pedido {
    private String cod_bod;
    private String cod_env;
    //private Date fechagen;
    private String fechagen;
    private Date fechaenv;
    private double peso;
    private String estado;
    private String editable;
    private double costo;
    private int prioridad;
    private String chofer;
    private double pesoE;
    private Date fechadist;
    private Date fechapani;
    private Date fechapqui;
    private Date fechaalm;
    private String tipoent;
    private int diasInventario;
    private boolean soloLectura;

    @Override
    public String toString() {
        //return "-- " + String.format("%06d", conse) + " --";
        //String xCod_env=cod_env.equals("") ? "000000" : cod_env;
        return "-- " + cod_env + " --";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pedido other = (Pedido) obj;
        if ((this.cod_bod == null) ? (other.cod_bod != null) : !this.cod_bod.equals(other.cod_bod)) {
            return false;
        }
        if ((this.cod_env == null) ? (other.cod_env != null) : !this.cod_env.equals(other.cod_env)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.cod_bod != null ? this.cod_bod.hashCode() : 0);
        hash = 97 * hash + (this.cod_env != null ? this.cod_env.hashCode() : 0);
        return hash;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getCod_env() {
        return cod_env;
    }

    public void setCod_env(String cod_env) {
        this.cod_env = cod_env;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getDiasInventario() {
        return diasInventario;
    }

    public void setDiasInventario(int diasInventario) {
        this.diasInventario = diasInventario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaalm() {
        return fechaalm;
    }

    public void setFechaalm(Date fechaalm) {
        this.fechaalm = fechaalm;
    }

    public Date getFechadist() {
        return fechadist;
    }

    public void setFechadist(Date fechadist) {
        this.fechadist = fechadist;
    }

    public Date getFechaenv() {
        return fechaenv;
    }

    public void setFechaenv(Date fechaenv) {
        this.fechaenv = fechaenv;
    }
    /*
    public Date getFechagen() {
        return fechagen;
    }

    public void setFechagen(Date fechagen) {
        this.fechagen = fechagen;
    }
    */

    public String getFechagen() {
        return fechagen;
    }

    public void setFechagen(String fechagen) {
        this.fechagen = fechagen;
    }
    
    public Date getFechapani() {
        return fechapani;
    }

    public void setFechapani(Date fechapani) {
        this.fechapani = fechapani;
    }

    public Date getFechapqui() {
        return fechapqui;
    }

    public void setFechapqui(Date fechapqui) {
        this.fechapqui = fechapqui;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPesoE() {
        return pesoE;
    }

    public void setPesoE(double pesoE) {
        this.pesoE = pesoE;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isSoloLectura() {
        return soloLectura;
    }

    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
    }

    public String getTipoent() {
        return tipoent;
    }

    public void setTipoent(String tipoent) {
        this.tipoent = tipoent;
    }
    
    public String getCod_bod() {
        return cod_bod;
    }

    public void setCod_bod(String cod_bod) {
        this.cod_bod = cod_bod;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }
}
