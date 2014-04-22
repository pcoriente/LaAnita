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
import producto2.dominio.Empaque;
import producto2.dominio.SubProducto;
import usuarios.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOSubProductos {
    private DataSource ds;
    
    public DAOSubProductos() throws NamingException {
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
    
    public ArrayList<SubProducto> obtenerSubProductos(int idArticulo) throws SQLException {
        ArrayList<SubProducto> lstProductos=new ArrayList<SubProducto>();
        String strSQL="SELECT e.idEmpaque, e.piezas, u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "WHERE e.idProducto="+idArticulo;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            Empaque empaque;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                empaque=new Empaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviaturaEmpaque"));
                lstProductos.add(new SubProducto(rs.getInt("idEmpaque"), rs.getInt("piezas"), empaque));
            }
        } finally {
            cn.close();
        }
        return lstProductos;
    }
    
    public SubProducto obtenerSubProducto(int idSubProducto) throws SQLException {
        SubProducto subProducto=null;
        String strSQL=""
                + "SELECT e.idEmpaque, e.piezas, u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "WHERE e.idEmpaque="+idSubProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            Empaque empaque;
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                empaque=new Empaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviaturaEmpaque"));
                subProducto=new SubProducto(idSubProducto, rs.getInt("piezas"), empaque);
            }
        } finally {
            cn.close();
        }
        return subProducto;
    }
}
