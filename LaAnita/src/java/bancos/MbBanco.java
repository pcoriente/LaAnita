package bancos;

import leyenda.dao.DAOBancosLeyendas;
import leyenda.dominio.BancoLeyenda;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name = "mbBanco")
@SessionScoped
public class MbBanco {

    BancoLeyenda b = new BancoLeyenda();

    public BancoLeyenda getB() {
        return b;
    }

    public void setB(BancoLeyenda b) {
        this.b = b;
    }
    private List<SelectItem> listaBancos;

    public List<SelectItem> getListaBancos() {
        if (this.listaBancos == null) {
            try {
                MbBanco cd = new MbBanco();
                this.listaBancos = cd.obtenerBancos();
            } catch (Exception ex) {
            }
        }
        return listaBancos;
    }

    public void setListaBancos(List<SelectItem> listaBancos) {
        this.listaBancos = listaBancos;
    }
    BancoLeyenda banco = new BancoLeyenda();
//CONSTRUCTOR DE LA CLASE

    public MbBanco() {
    }

    public String terminar() {
        return "menuBancos.terminar";
    }

    public ArrayList<BancoLeyenda> verTabla() throws SQLException {
        ArrayList<BancoLeyenda> Tabla;
        DAOBancosLeyendas dao = new DAOBancosLeyendas();
        Tabla = dao.dameBancos();
        return Tabla;
    }

    public String eliminar(int id) throws SQLException {
        DAOBancosLeyendas datos = new DAOBancosLeyendas();
        datos.eliminarUsuario(id);
        String Eliminado = "Dato.eliminado";
        return Eliminado;

    }

    //Codigo Pablo//
    public void guardar() throws SQLException {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", null);
        FacesMessage fMsg2 = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", null);
        DAOBancosLeyendas db = new DAOBancosLeyendas();
        if (b.getIdBanco() == 0) {
            try {
                try {
                    db.agregarDato(b.getRfc(), b.getCodigoBanco(), b.getRazonSocial(), b.getNombreCorto());
                    fMsg2.setDetail("Datos Insertados Correctamente");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg2);
                } catch (Exception e) {
                    System.err.println(e);
                }
            } catch (Exception e) {
                fMsg.setDetail("Codigo de Banco es un número teclee nuevamente");
                FacesContext.getCurrentInstance().addMessage(null, fMsg);
            }
        } else {
            b.getCodigoBanco();
            b.getIdBanco();
            b.getNombreCorto();
            b.getRazonSocial();
            b.getRfc();
            db.dameUsuario(b);
            fMsg2.setDetail("Datos Actualizados Correctamente");
            FacesContext.getCurrentInstance().addMessage(null, fMsg2);
        }


    }

    public List<SelectItem> obtenerBancos() throws SQLException {
        List<SelectItem> bancos = new ArrayList<SelectItem>();

        BancoLeyenda B = new BancoLeyenda();
        B.setIdBanco(0);
        B.setNombreCorto("Seleccione un país");
        B.setNombreCorto("Seleccione un país");
        SelectItem cero = new SelectItem(B, B.getNombreCorto());
        bancos.add(cero);

        DAOBancosLeyendas dao = new DAOBancosLeyendas();
        BancoLeyenda[] aBancos = dao.obtenerPaises();
        for (BancoLeyenda p : aBancos) {
            bancos.add(new SelectItem(p, p.getNombreCorto()));
        }
        return bancos;
    }

    public String nuevoBanco() throws SQLException {
        BancoLeyenda b = new BancoLeyenda();

        b.setIdBanco(0);
        b.setRfc("");
        b.setCodigoBanco(0);
        b.setRazonSocial("");
        b.setNombreCorto("");
        String navegar = "banco.nuevo";

        return navegar;
    }

    public String actualizar(int id) throws SQLException {
        DAOBancosLeyendas banco = new DAOBancosLeyendas();
        String navegar = "menu.banco";
        if (id == 0) {
            b.setCodigoB("");
            b.setCodigoBanco(0);
            b.setIdBanco(0);
            b.setNombreCorto("");
            b.setRazonSocial("");
            b.setRfc("");
        } else {
            b = banco.obtenerDatos(id);
        }
        return navegar;
    }
}