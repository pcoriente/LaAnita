package contribuyentes;

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
 * @author jsolis
 */
public class DAOContribuyentes {

    private DataSource ds;

    public DAOContribuyentes() throws NamingException {
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

    public int grabarRFC(String rfc) throws SQLException {
        int idRfc = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate("INSERT INTO contribuyentesRfc (rfc) values ('" + rfc + "')");
            ResultSet rs = st.executeQuery("SELECT @@IDENTITY AS idRfc");
            if (rs.next()) {
                idRfc = rs.getInt("idRfc");
            }
            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
        return idRfc;
    }

    public int obtenerRfc(String rfc) throws SQLException {
        int idRfc = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            String strSQL = "SELECT idRfc FROM contribuyentesRfc WHERE rfc='" + rfc + "'";
            ResultSet rs = st.executeQuery(strSQL);
            if (rs.next()) {
                idRfc = rs.getInt("idRfc");
            }
        } finally {
            cn.close();
        }
        return idRfc;
    }
    
    public ArrayList<Contribuyente> obtenerContribuyentesCliente() throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL="SELECT c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion "
                + "FROM contribuyentes c "
                + "inner join contribuyentesrfc cr on cr.idRfc=c.idRfc "
                + "WHERE c.idContribuyente IN (SELECT DISTINCT idContribuyente FROM clientes)";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    public ArrayList<Contribuyente> obtenerContribuyentes(String cadena) throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion "
                + "from contribuyentes c "
                + "inner join contribuyentesrfc cr on cr.idRfc=c.idRfc "
                + "where c.contribuyente like '%" + cadena + "%'";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    public Contribuyente obtenerContribuyente(int idContribuyente) throws SQLException {
        Contribuyente to = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, c.contribuyente, cr.idRfc, cr.rfc, c.idDireccion "
                + "from contribuyentes c "
                + "inner join contribuyentesrfc cr on cr.idRfc=c.idRfc "
                + "where c.idContribuyente=" + idContribuyente;
        try {
            ResultSet rs = st.executeQuery(strSQL);
            if (rs.next()) {
                to = construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }

    public ArrayList<Contribuyente> obtenerContribuyentesRFC(String rfc) throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion "
                + "from contribuyentes c "
                + "inner join contribuyentesrfc cr on cr.idRfc=c.idRfc "
                + "where cr.rfc='" + rfc + "'";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    private Contribuyente construir(ResultSet rs) throws SQLException {
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setIdContribuyente(rs.getInt("idContribuyente"));
        contribuyente.setContribuyente(rs.getString("contribuyente"));
        contribuyente.setIdRfc(rs.getInt("idRfc"));
        contribuyente.setRfc(rs.getString("rfc"));
        contribuyente.setDireccion(new Direccion());
        contribuyente.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
        return contribuyente;
    }

    public void actualizarContribuyente(Contribuyente contribuyente) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE contribuyentes set contribuyente = '"+contribuyente.getContribuyente()+"' WHERE idContribuyente = "+contribuyente.getIdContribuyente();
        try{
        st.executeUpdate(sql);
        }
        finally{
        cn.close();
        st.close();
        }
    }
    
    public void actualizarContribuyenteRfc(Contribuyente contribuyente) throws SQLException{
    Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE contribuyentesRfc set rfc = '"+contribuyente.getRfc()+"', curp='"+contribuyente.getCurp()+"' WHERE idRfc = "+contribuyente.getIdRfc();
        try{
        st.executeUpdate(sql);
        }
        finally{
        cn.close();
        st.close();
        }
    }
}
