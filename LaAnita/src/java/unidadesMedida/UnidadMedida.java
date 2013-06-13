package unidadesMedida;

/**
 *
 * @author JULIOS
 */
public class UnidadMedida {
    private int idUnidadMedida;
    private String unidadMedida;
    private String abreviatura;
    private int idTipo;

    public UnidadMedida(int idUnidadMedida, String unidadMedida, String abreviatura, int idTipo) {
        this.idUnidadMedida = idUnidadMedida;
        this.unidadMedida = unidadMedida;
        this.abreviatura = abreviatura;
        this.idTipo = idTipo;
    }

    @Override
    public String toString() {
        return unidadMedida;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.idUnidadMedida;
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
        final UnidadMedida other = (UnidadMedida) obj;
        if (this.idUnidadMedida != other.idUnidadMedida) {
            return false;
        }
        return true;
    }

    public int getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(int idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }
}
