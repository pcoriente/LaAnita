package productos.dominio;

/**
 *
 * @author JULIOS
 */
public class SubEmpaque {
    //private int idEmpresa;
    private int idEmpaque;
    private int piezas;
    private UnidadEmpaque unidadEmpaque;

    public SubEmpaque(int idEmpaque) {
        //this.idEmpresa = idEmpresa;
        this.idEmpaque = idEmpaque;
        this.piezas=0;
        this.unidadEmpaque=new UnidadEmpaque();
    }

    public SubEmpaque(int idEmpaque, int piezas, UnidadEmpaque unidadEmpaque) {
        //this.idEmpresa = idEmpresa;
        this.idEmpaque = idEmpaque;
        this.piezas = piezas;
        this.unidadEmpaque = unidadEmpaque;
    }

    @Override
    public String toString() {
        String empaque=unidadEmpaque.toString();
        if(piezas > 1) {
            empaque+=" x " + Integer.toString(piezas);
        }
        return empaque;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.idEmpaque;
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
        final SubEmpaque other = (SubEmpaque) obj;
        if (this.idEmpaque != other.idEmpaque) {
            return false;
        }
        return true;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }
    /*
    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    * */
    public int getPiezas() {
        return piezas;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }

    public UnidadEmpaque getUnidadEmpaque() {
        return unidadEmpaque;
    }

    public void setUnidadEmpaque(UnidadEmpaque unidadEmpaque) {
        this.unidadEmpaque = unidadEmpaque;
    }
}
