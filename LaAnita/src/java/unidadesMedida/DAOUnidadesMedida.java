package unidadesMedida;

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
 * @author JULIOS
 */
public class DAOUnidadesMedida {
    private DataSource ds;
    
    public DAOUnidadesMedida() throws NamingException {
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
    
    public ArrayList<UnidadMedida> obtenerUnidades() throws SQLException {
        ArrayList<UnidadMedida> unidades=new ArrayList<UnidadMedida>();
        String strSQL="SELECT * FROM unidadesMedida ORDER BY unidadMedida";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                unidades.add(new UnidadMedida(rs.getInt("idUnidadMedida"), rs.getString("unidadMedida"), rs.getString("abreviatura"), 0));
            }
        } finally {
            cn.close();
        }
        return unidades;
    }
    
    public UnidadMedida obtenerUnidad(int idUnidad) throws SQLException {
        UnidadMedida unidad=null;
        String strSQL="SELECT * FROM unidadesMedida WHERE idUnidadMedida="+idUnidad;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                unidad=new UnidadMedida(rs.getInt("idUnidadMedida"), rs.getString("unidadMedida"), rs.getString("abreviatura"), 0);
            }
        } finally {
            cn.close();
        }
        return unidad;
    }
}
