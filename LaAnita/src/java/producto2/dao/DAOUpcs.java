package producto2.dao;

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
import producto2.dominio.Upc;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOUpcs {
    private DataSource ds;
    private String tabla="empaquesUpcs";

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
    
    public void Eliminar(String upc) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("DELETE FROM "+this.tabla+" WHERE upc='"+upc+"'");
        } finally {
            cn.close();
        }
    }
    
    public void agregar(Upc u) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("INSERT INTO "+this.tabla+" (upc, idProducto) VALUES ('" + u.getUpc() + "', " + u.getIdProducto() + ")");
        } finally {
            cn.close();
        }
    }
    
    public Upc obtenerUnUpc(int idProducto) throws SQLException {
        Upc upc=null;
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT upc, idProducto FROM"+this.tabla+" WHERE idProducto="+idProducto);
            if(rs.next()) {
                upc=new Upc(rs.getString("upc"), rs.getInt("idProducto"));
            }
        } finally {
            cn.close();
        }
        return upc;
    }

    public ArrayList<Upc> obtenerUpcs(int idProducto) throws SQLException {
        ArrayList<Upc> upcs = new ArrayList<Upc>();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT upc, idProducto FROM "+this.tabla+" WHERE idProducto=" + idProducto);
            while (rs.next()) {
                upcs.add(new Upc(rs.getString("upc"), rs.getInt("idProducto")));
            }
        } finally {
            cn.close();
        }
        return upcs;
    }
    
    public Upc obtenerUpc(String upc) throws SQLException {
        Upc u = null;
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT upc, idProducto FROM "+this.tabla+" WHERE upc=" + upc);
            if (rs.next()) {
                u = new Upc(rs.getString("upc"), rs.getInt("idProducto"));
            }
        } finally {
            cn.close();
        }
        return u;
    }
}
