package almacenes.dao;
import almacenes.dominio.Almacen;
import direccion.dominio.Direccion;
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
 * @author julios
 */
public class DAOAlmacenes {

    private DataSource ds = null;

    public DAOAlmacenes() throws NamingException {
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
    public Almacen obtenerAlmacen(int idAlmacen) throws SQLException {
        Almacen almacen=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM almacenes WHERE idAlmacen="+idAlmacen);
            if(rs.next()) almacen=construir(rs);
        } finally {
            cn.close();
        }
        return almacen;
    }

    public ArrayList<Almacen> obtenerAlmacenes() throws SQLException {
        ArrayList<Almacen> lista = new ArrayList<Almacen>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "SELECT * FROM almacenes";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private Almacen construir(ResultSet rs) throws SQLException {
        Almacen almacen = new Almacen();
        almacen.setIdAlmacen(rs.getInt("idAlmacen"));
        almacen.setAlmacen(rs.getString("almacen"));
        almacen.setIdCedis(rs.getInt("idCedis"));
        almacen.setIdEmpresa(rs.getInt("idEmpresa"));
        almacen.setDireccion(new Direccion());
        almacen.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
        almacen.setEncargado(rs.getString("encargado"));
        return almacen;
    }
}
