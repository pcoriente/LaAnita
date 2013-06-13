package pedidos.dominio;

/**
 *
 * @author Julio
 */
public class Fincado {
    private String cod_emp;
    private String cod_cli;
    private String tienda;
    private String cod_ped;
    private boolean directo;
    private int orden;
    private double peso;
    private boolean agregado;

    public String getCod_cli() {
        return cod_cli;
    }

    public void setCod_cli(String cod_cli) {
        this.cod_cli = cod_cli;
    }

    public String getCod_emp() {
        return cod_emp;
    }

    public void setCod_emp(String cod_emp) {
        this.cod_emp = cod_emp;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    public String getCod_ped() {
        return cod_ped;
    }

    public void setCod_ped(String cod_ped) {
        this.cod_ped = cod_ped;
    }

    public boolean isDirecto() {
        return directo;
    }

    public void setDirecto(boolean directo) {
        this.directo = directo;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }
}
