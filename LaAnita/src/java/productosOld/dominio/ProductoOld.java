package productosOld.dominio;

/**
 *
 * @author Julio
 */
public class ProductoOld {
    private String cod_emp;
    private String cod_pro;
    private String descripcion;
    private String codbar;
    private String grupo;
    private double peso;
    private double volumen;
    private int idEmpaque;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductoOld other = (ProductoOld) obj;
        if ((this.cod_emp == null) ? (other.cod_emp != null) : !this.cod_emp.equals(other.cod_emp)) {
            return false;
        }
        if ((this.cod_pro == null) ? (other.cod_pro != null) : !this.cod_pro.equals(other.cod_pro)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.cod_emp != null ? this.cod_emp.hashCode() : 0);
        hash = 79 * hash + (this.cod_pro != null ? this.cod_pro.hashCode() : 0);
        return hash;
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

    public String getCodbar() {
        return codbar;
    }

    public void setCodbar(String codbar) {
        this.codbar = codbar;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }
}
