package proveedores.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import proveedores.dominio.MiniProveedor;
import usuarios.UsuarioSesion;

/**
 *
 * @author jsolis
 */
public class DAOMiniProveedores {

    private DataSource ds;

    public DAOMiniProveedores() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public MiniProveedor obtenerProveedor(int idProveedor) throws SQLException {
        MiniProveedor to = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("select p.idProveedor,  c.contribuyente, p.desctoComercial, p.desctoProntoPago from proveedores p\n"
                    + "inner join contribuyentes c on c.idContribuyente = p.idContribuyente\n"
                    + "where p.idProveedor" + idProveedor + "\n"
                    + "order by c.contribuyente"); //MODIFICO DAVID
            if (rs.next()) {
                to = construirConverter(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }

    public ArrayList<MiniProveedor> obtenerProveedores() throws SQLException {
        ArrayList<MiniProveedor> lista = new ArrayList<MiniProveedor>();
        ResultSet rs = null;

        Connection cn = ds.getConnection();
        String strSQL = "select p.idProveedor,  c.contribuyente from proveedores p\n" +
"                    inner join contribuyentes c on c.idContribuyente = p.idContribuyente order by p.idProveedor";  //Modifico DAVID
        try {
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(strSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    public ArrayList<MiniProveedor> obtenerProveedores(String cadena) throws SQLException {
        ArrayList<MiniProveedor> lista = new ArrayList<MiniProveedor>();
        ResultSet rs = null;

        Connection cn = ds.getConnection();
        String strSQL = "SELECT * "
                + "FROM proveedoresRfc "
                + "WHERE contribuyente like '%" + cadena + "%' "
                + "ORDER BY proveedor";
        try {
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(strSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private MiniProveedor construir(ResultSet rs) throws SQLException {
        MiniProveedor to = new MiniProveedor();
        to.setIdProveedor(rs.getInt("idProveedor"));
        to.setProveedor(rs.getString("contribuyente"));
    //    to.setRfc(rs.getString("rfc"));
        return to;
    }

    private MiniProveedor construirConverter(ResultSet rs) throws SQLException {
        MiniProveedor to = new MiniProveedor();
        to.setIdProveedor(rs.getInt("idProveedor"));
        to.setProveedor(rs.getString("contribuyente"));
        to.setDesctoComercial(rs.getDouble("desctoComercial"));
        to.setDesctoProntoPago(rs.getDouble("desctoProntoPago"));
        return to;
    }
}
