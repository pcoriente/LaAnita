package productos.dao;

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
import productos.dominio.Upc;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOUpcs {

    private DataSource ds;

    public DAOUpcs() throws NamingException {
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
    
    public void Eliminar(int idUpc) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("DELETE FROM productosUpcs WHERE idUpc="+idUpc);
        } finally {
            cn.close();
        }
    }
    
    public int agregar(Upc u) throws SQLException {
        int idUpc=0;
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            if (u.getIdUpc() == 0 && !u.getUpc().equals(new Upc(u.getIdProducto()).getUpc())) {
                st.executeUpdate("INSERT INTO productosUpcs (upc, idProducto) "
                        + "VALUES ('" + u.getUpc() + "', " + u.getIdProducto() + ")");
                ResultSet rs = st.executeQuery("SELECT max(idUpc) AS idUpc FROM productosUpcs");
                if (rs.next()) {
                    idUpc=rs.getInt("idUpc");
                }
            }
        } finally {
            cn.close();
        }
        return idUpc;
    }

    public ArrayList<Upc> obtenerUpcs(int idProducto) throws SQLException {
        ArrayList<Upc> upcs = new ArrayList<Upc>();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT idUpc, upc, idProducto FROM productosUpcs WHERE idProducto=" + idProducto);
            while (rs.next()) {
                upcs.add(new Upc(rs.getInt("idUpc"), rs.getString("upc"), rs.getInt("idProducto")));
            }
        } finally {
            cn.close();
        }
        return upcs;
    }

    public ArrayList<Upc> agregarUpcs(ArrayList<Upc> upcs) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            for (Upc u : upcs) {
                if (u.getIdUpc() == 0 && !u.getUpc().equals(new Upc(u.getIdProducto()).getUpc())) {
                    st.executeUpdate("INSERT INTO productosUpcs (upc, idProducto) "
                            + "VALUES ('" + u.getUpc() + "', " + u.getIdProducto() + ")");
                    ResultSet rs = st.executeQuery("SELECT max(idUpc) AS idUpc FROM productosUpcs");
                    if (rs.next()) {
                        u.setIdUpc(rs.getInt("idUpc"));
                    }
                }
            }
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return upcs;
    }

    public Upc obtenerUpc(int idUpc) throws SQLException {
        Upc upc = null;
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT idUpc, upc, idProducto FROM productosUpcs WHERE idUpc=" + idUpc);
            if (rs.next()) {
                upc = new Upc(rs.getInt("idUpc"), rs.getString("upc"), rs.getInt("idProducto"));
            }
        } finally {
            cn.close();
        }
        return upc;
    }
    
    public boolean existeUpc(String codBar) throws SQLException {
        boolean existe=false;
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT idUpc FROM productosUpcs WHERE upc='" + codBar + "'");
            if (rs.next()) {
                existe=true;
            }
        } finally {
            cn.close();
        }
        return existe;
    }
}
