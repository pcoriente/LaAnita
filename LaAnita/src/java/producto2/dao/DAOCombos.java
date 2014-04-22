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
import producto2.to.TOProductoCombo;
import usuarios.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOCombos {
    private DataSource ds;
    
    public DAOCombos() throws NamingException {
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
    
    public ArrayList<TOProductoCombo> obtenerCombo(int idProducto) throws SQLException {
        ArrayList<TOProductoCombo> productos=new ArrayList<TOProductoCombo>();
        String strSQL="SELECT idSubempaque AS idProducto, piezas FROM empaquesCombos WHERE idEmpaque="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(new TOProductoCombo(rs.getInt("idProducto"), rs.getInt("piezas")));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
}
