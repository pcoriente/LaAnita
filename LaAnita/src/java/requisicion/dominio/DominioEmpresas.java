/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.dominio;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author Comodoro
 */
@Named(value = "dominioEmpresas")
@SessionScoped
public class DominioEmpresas implements Serializable {

    public DominioEmpresas() {
    }
    private int idEmpresa;
    private int codigoEmpresa;
    private String empresa;
    private String nombreComercial;
    private String rfc;
    private String eMail;

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(int codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.idEmpresa;
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
        final DominioEmpresas other = (DominioEmpresas) obj;
        if (this.idEmpresa != other.idEmpresa) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombreComercial;
    }



}
