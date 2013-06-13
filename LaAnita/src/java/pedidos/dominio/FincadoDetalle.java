package pedidos.dominio;

/**
 *
 * @author Julio
 */
public class FincadoDetalle {
    private String cod_emp;
    private String cod_pro;
    private String descripcion;
    private int orden;
    private double cantPed;
    private double peso;
    private int pzasepq;
    private boolean agregado;
    private boolean cambio;

    public int getPzasepq() {
        return pzasepq;
    }

    public void setPzasepq(int pzasepq) {
        this.pzasepq = pzasepq;
    }

    public boolean isCambio() {
        return cambio;
    }

    public void setCambio(boolean cambio) {
        this.cambio = cambio;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
    
    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    public double getCantPed() {
        return cantPed;
    }

    public void setCantPed(double cantPed) {
        this.cantPed = cantPed;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public String getCod_pro() {
        return cod_pro;
    }

    public void setCod_pro(String cod_pro) {
        this.cod_pro = cod_pro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }
}
