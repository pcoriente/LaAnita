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
import productos.dominio.Parte;
import productos.dominio.Parte2;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOPartes {
    private DataSource ds;
    
    public DAOPartes() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
    public Parte2 obtenerParte(int idParte) throws SQLException {
        Parte2 parte=null;
        String strSQL="SELECT idParte, parte FROM productosPartes WHERE idParte="+idParte;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                parte=new Parte2(rs.getInt("idparte"), rs.getString("parte"));
                //parte.setIdParte(rs.getInt("idParte"));
                //parte.setParte(rs.getString("parte"));
            }
        } finally {
            cn.close();
        }
        return parte;
    }
    
    public ArrayList<Parte> obtenerPartes() throws SQLException {
        ArrayList<Parte> partes=new ArrayList<Parte>();
        String strSQL="SELECT idParte, parte FROM productosPartes ORDER BY parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                partes.add(new Parte(rs.getInt("idparte"), rs.getString("parte")));
            }
        } finally {
            cn.close();
        }
        return partes;
    }
    
    public void eliminar(int idParte) throws SQLException {
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("DELETE FROM productosPartes WHERE idMarca="+idParte);
        } finally {
            cn.close();
        }
    }
    
    public void modificar(Parte2 p) throws SQLException {
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("UPDATE productosPartes SET parte='"+p.getParte()+"' WHERE idParte="+p.getIdParte());
        } finally {
            cn.close();
        }
    }
    
    public int agregar(String parte) throws SQLException {
        int idParte=0;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("INSERT INTO productosPartes (parte) VALUES ('"+parte+"')");
            ResultSet rs=st.executeQuery("SELECT MAX(idParte) AS idParte FROM productosPartes");
            if(rs.next()) {
                idParte=rs.getInt("idParte");
            }
        } finally {
            cn.close();
        }
        return idParte;
    }
    
    public ArrayList<Parte2> completePartes(String strParte) throws SQLException {
        Parte2 p;
        ArrayList<Parte2> partes=new ArrayList<Parte2>();
        String strSQL="SELECT idParte, parte FROM productosPartes WHERE parte like '%"+strParte+"%' ORDER BY parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                p=new Parte2(rs.getInt("idparte"), rs.getString("parte"));
                //p.setIdParte(rs.getInt("idParte"));
                //p.setParte(rs.getString("parte"));
                partes.add(p);
            }
        } finally {
            cn.close();
        }
        return partes;
    }
    
    public Parte2 obtenerParte2(int idParte) throws SQLException {
        Parte2 parte=null;
        String strSQL="SELECT idParte, parte FROM productosPartes WHERE idParte="+idParte;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                parte=new Parte2(rs.getInt("idparte"), rs.getString("parte"));
            }
        } finally {
            cn.close();
        }
        return parte;
    }
}
