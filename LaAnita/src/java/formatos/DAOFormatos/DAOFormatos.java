package formatos.DAOFormatos;

import formatos.dominio.Formato;
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
 * @author Usuario
 */
public class DAOFormatos {

    private DataSource ds;

    public DAOFormatos() throws NamingException {
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

    public ArrayList<Formato> dameListaFormatos() throws SQLException {
        ArrayList<Formato> lstFormato = new ArrayList<Formato>();
        String slq = "select * from clientesFormatos cf\n"
                + "inner join webSystem.dbo.monedas mo\n"
                + "on mo.idMoneda = cf.idMoneda";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(slq);
            while (rs.next()) {
                Formato formato = new Formato();
                formato.setFormato(rs.getString("formato"));
                formato.setIdFormato(rs.getInt("idFormato"));
                formato.getMoneda().setIdMoneda(rs.getInt("idMoneda"));
                formato.getMoneda().setMoneda(rs.getString("moneda"));
//                formato.getClientesGrupo().setIdGrupoCte(rs.getInt("idGrupo"));
                lstFormato.add(formato);
            }
        } finally {
            cn.close();
        }
        return lstFormato;
    }

    public void guardarFormato(Formato formato) throws SQLException {
        Connection cn = ds.getConnection();
        String sql = "INSERT INTO clientesFormatos (formato, idMoneda) VALUES('" + formato.getFormato() + "', '" + formato.getMoneda().getIdMoneda() + "')";
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }

    public Formato dameFormato(int id) throws SQLException {
        Formato formato = new Formato();
        String sql = "SELECT * FROM clientesFormato cf"
                + " inner join clientesGrupos cg\n"
                + "on cf.idGrupo = cg.idGrupoCte "
                + " WHERE idFormato='" + id + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                formato.setIdFormato(rs.getInt("idFormato"));
                formato.setFormato(rs.getString("formato"));
                formato.getClientesGrupo().setIdGrupoCte(rs.getInt("idGrupoCte"));
            }
        } finally {
            cn.close();
        }
        return formato;
    }

    public void actualizar(Formato formato) throws SQLException {
        String sql = "UPDATE clientesFormatos set formato = '" + formato.getFormato() + "', idMoneda = '" + formato.getMoneda().getIdMoneda() + "' WHERE idFormato ='" + formato.getIdFormato() + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }
}