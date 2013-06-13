package productos.dominio;

/**
 *
 * @author JULIOS
 */
public class Grupo {
    private int idGrupo;
    private int codigo;
    private String grupo;

    public Grupo(int idGrupo, int codigo, String grupo) {
        this.idGrupo = idGrupo;
        this.codigo = codigo;
        this.grupo = grupo;
    }

    @Override
    public String toString() {
        return grupo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.idGrupo;
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
        final Grupo other = (Grupo) obj;
        if (this.idGrupo != other.idGrupo) {
            return false;
        }
        return true;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }
    /*
    public String getCod_inv() {
        return cod_inv;
    }

    public void setCod_inv(String cod_inv) {
        this.cod_inv = cod_inv;
    }
    * */
    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}
