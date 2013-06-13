package direccion.dao;

import direccion.dominio.Pais;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class DAOPais {
    private DataSource ds;
    
    public DAOPais() throws NamingException {
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
    
    public Pais obtener(int idPais) throws SQLException {
        Pais pais=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM paises WHERE idPais="+idPais);
            if(rs.next()) pais=construir(rs);
        } finally {
            cn.close();
        }
        return pais;
    }
    
    public Pais[] obtenerPaises() throws SQLException {
        Pais[] paises=null;
        ResultSet rs=null;
        
        Connection cn=ds.getConnection();
        String strSQL="SELECT * FROM paises";
        try {
            Statement sentencia = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = sentencia.executeQuery(strSQL);
            if(rs.next()) {
                int i=0;
                rs.last();
                paises=new Pais[rs.getRow()];
                
                rs.beforeFirst();
                while(rs.next()) {
                    paises[i++]=construir(rs);
                }
            }
        } finally {
            cn.close();
        }
        return paises;
    }
    
    private Pais construir(ResultSet rs) throws SQLException {
        Pais pais=new Pais();
        pais.setIdPais(rs.getInt("idPais"));
        pais.setPais(rs.getString("pais"));
        return pais;
    }
}
