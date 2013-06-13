package pedidos.dominio;

/**
 *
 * @author Julio
 */
public class Envio {
    private String cod_bod;
    private String cod_env;
    private String fechaGen;
    private int prioridad;

    public String getCod_bod() {
        return cod_bod;
    }

    public void setCod_bod(String cod_bod) {
        this.cod_bod = cod_bod;
    }

    public String getCod_env() {
        return cod_env;
    }

    public void setCod_env(String cod_env) {
        this.cod_env = cod_env;
    }

    public String getFechaGen() {
        return fechaGen;
    }

    public void setFechaGen(String fechaGen) {
        this.fechaGen = fechaGen;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}
