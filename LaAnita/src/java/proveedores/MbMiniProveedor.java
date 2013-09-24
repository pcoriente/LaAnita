/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proveedores;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import proveedores.dao.DAOMiniProveedores;
import proveedores.dominio.MiniProveedor;

@Named(value = "mbMiniProveedor")
@SessionScoped
public class MbMiniProveedor implements Serializable{

    private ArrayList<SelectItem> listaMiniProveedores = new ArrayList<SelectItem>();
    private MiniProveedor miniProveedor = new MiniProveedor();

    public MbMiniProveedor() {
    }

    public ArrayList<SelectItem> obtenerListaMiniProveedor() throws SQLException, NamingException {
        try {
            MiniProveedor p0 = new MiniProveedor();
            p0.setIdProveedor(0);
            p0.setProveedor("Proveedor....");
            listaMiniProveedores.add(new SelectItem(p0, p0.toString()));
            DAOMiniProveedores daoP = new DAOMiniProveedores();
            ArrayList<MiniProveedor> proveedores = daoP.obtenerProveedores();
            for (MiniProveedor e : proveedores) {
                listaMiniProveedores.add(new SelectItem(e, e.toString()));
            }
        } catch (SQLException e) {
            Logger.getLogger(MbMiniProveedor.class.getName()).log(Level.SEVERE, null, e);
        }
        return listaMiniProveedores;
    }

    public ArrayList<SelectItem> getListaMiniProveedores() throws SQLException, NamingException {

        listaMiniProveedores = this.obtenerListaMiniProveedor();
        return listaMiniProveedores;
    }

    public void setListaMiniProveedores(ArrayList<SelectItem> listaMiniProveedores) {
        this.listaMiniProveedores = listaMiniProveedores;
    }

    public MiniProveedor getMiniProveedor() {
        return miniProveedor;
    }

    public void setMiniProveedor(MiniProveedor miniProveedor) {
        this.miniProveedor = miniProveedor;
    }
}
