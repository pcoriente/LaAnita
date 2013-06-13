package cedis.dao;

import cedis.dominio.MiniCedis;
import cedis.to.TOCedis;
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
import usuarios.UsuarioSesion;
/**
 *
 * @author Julio
 */
public class DAOCedis {
    private DataSource ds;
    
    public DAOCedis() throws NamingException {
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
    
    public int agregar(int codigo, String cedis, int idDireccion, String telefono, String fax, String correo, String representante) throws SQLException {
        System.out.println("idDireccion: "+idDireccion);
        int idCedis=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("INSERT INTO cedis (codigo, cedis, idDireccion, telefono, fax, eMail, representante) "
                    + "VALUES ("+codigo+", '"+cedis+"', "+idDireccion+", '"+telefono+"', '"+fax+"', '"+correo+"', '"+representante+"')");
            ResultSet rs=st.executeQuery("SELECT MAX(idCedis) AS idCedis FROM cedis");
            if(rs.next()) idCedis=rs.getInt("idCedis");
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        return idCedis;
    }
    
    public void modificar(int idCedis, String cedis, int idDireccion, String telefono, String fax, String correo, String representante) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("UPDATE cedis SET cedis='"+cedis+"', idDireccion="+idDireccion+", telefono='"+telefono+"', fax='"+fax+"', eMail='"+correo+"', representante='"+representante+"' "
                    + "WHERE idCedis="+idCedis);
        } finally {
            cn.close();
        }
    }
    
    public int ultimoCedis() throws SQLException {
        int ultimo=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT MAX(codigo) as ultimo FROM cedis");
            if(rs.next()) ultimo=rs.getInt("ultimo");
        } finally {
            cn.close();
        }
        return ultimo;
    }
    
    public TOCedis obtenerUnCedis(int idCedis) throws SQLException {
        TOCedis to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM cedis WHERE idCedis="+idCedis);
            if(rs.next()) to=construir(rs);
        } finally {
            cn.close();
        }
        return to;
    }
    /*
    public TOCedis obtenerUnCedis(String cod_bod) throws SQLException {
        TOCedis to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM cedis WHERE codigo="+Integer.parseInt(cod_bod));
            if(rs.next()) to=construir(rs);
        } finally {
            cn.close();
        }
        return to;
    }
    */
    public ArrayList<TOCedis> obtenerCedis() throws SQLException {
        ArrayList<TOCedis> lista=new ArrayList<TOCedis>();
        ResultSet rs=null;
        
        Connection cn=ds.getConnection();
        String strSQL="SELECT * FROM cedis ORDER BY cedis";
        try {
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(strSQL);
            while(rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    private TOCedis construir(ResultSet rs) throws SQLException {
        TOCedis to=new TOCedis();
        to.setIdCedis(rs.getInt("idCedis"));
        to.setCodigo(rs.getInt("codigo"));
        to.setCedis(rs.getString("cedis"));
        to.setIdDireccion(rs.getInt("idDireccion"));
        to.setTelefono(rs.getString("telefono"));
        to.setFax(rs.getString("fax"));
        to.setCorreo(rs.getString("eMail"));
        to.setRepresentante(rs.getString("representante"));
        return to;
    }
    
    public MiniCedis obtenerMiniCedis(int idCedis) throws SQLException {
        MiniCedis to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idCedis, codigo, cedis FROM cedis WHERE idCedis="+idCedis);
            if(rs.next()) to=construirMini(rs);
        } finally {
            cn.close();
        }
        return to;
    }
    
    public ArrayList<MiniCedis> obtenerListaMiniCedis() throws SQLException {
        ArrayList<MiniCedis> lstMiniCedis=new ArrayList<MiniCedis>();
        ResultSet rs=null;
        
        Connection cn=ds.getConnection();
        Statement sentencia = cn.createStatement();
        String strSQL="SELECT idCedis, codigo, cedis FROM cedis ORDER BY codigo";
        try {
            rs = sentencia.executeQuery(strSQL);
            while(rs.next()) {
                lstMiniCedis.add(construirMini(rs));
            }
        } finally {
            cn.close();
        }
        return lstMiniCedis;
    }
    
    private MiniCedis construirMini(ResultSet rs) throws SQLException {
        MiniCedis to=new MiniCedis();
        to.setIdCedis(rs.getInt("idCedis"));
        to.setCodigo(String.format("%02d", rs.getInt("codigo")));
        to.setCedis(rs.getString("cedis"));
        return to;
    }
}
