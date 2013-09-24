package contactos.dominio;

import java.util.ArrayList;

/**
 *
 * @author jsolis
 */
public class Contacto {
    private int idContacto;
    private String contacto;
    private String puesto;
    private String correo;
    private ArrayList<Telefono> telefonos;

    public Contacto() {
        this.idContacto=0;
        this.contacto="";
        this.puesto="";
        this.correo="";
        this.telefonos=new ArrayList<Telefono>();
    }

    @Override
    public String toString() {
        return this.contacto;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.idContacto;
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
        final Contacto other = (Contacto) obj;
        if (this.idContacto != other.idContacto) {
            return false;
        }
        return true;
    }

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public ArrayList<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(ArrayList<Telefono> telefonos) {
        this.telefonos = telefonos;
    }
}
