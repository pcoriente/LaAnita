/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisicion.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import requisicion.dominio.DominioEmpresas;
import requisicion.dominio.DominioPartes;
import usuarios.UsuarioSesion;

/**
 *
 * @author Comodoro
 */
public class DaoRequisicion {

    private DataSource ds = null;

    public DaoRequisicion() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
//            throw (ex);
        }
    }

    public ArrayList<DominioEmpresas> dameEmpresas() throws SQLException {
        ArrayList<DominioEmpresas> domE = new ArrayList<DominioEmpresas>();
        Connection c = ds.getConnection();
        String sql = "SELECT * FROM empresasGrupo";
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            DominioEmpresas d = new DominioEmpresas();
            d.setIdEmpresa(rs.getInt("idEmpresa"));
            d.setCodigoEmpresa(rs.getInt("codigoEmpresa"));
            d.setEmpresa(rs.getString("empresa"));
            d.setNombreComercial(rs.getString("nombreComercial"));
            d.setRfc(rs.getString("rfc"));
            d.seteMail(rs.getString("eMail"));
            domE.add(d);
        }
        return domE;
    }

    public DominioEmpresas dameEmpresas(int id) throws SQLException {
        DominioEmpresas d = new DominioEmpresas();
        Connection c = ds.getConnection();
        String sql = "SELECT * FROM empresasGrupo WHERE codigoEmpresa=" + id;
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            d.setIdEmpresa(rs.getInt("idEmpresa"));
            d.setCodigoEmpresa(rs.getInt("codigoEmpresa"));
            d.setEmpresa(rs.getString("empresa"));
            d.setNombreComercial(rs.getString("nombreComercial"));
            d.setRfc(rs.getString("rfc"));
            d.seteMail(rs.getString("eMail"));
        }
        return d;
    }

    public List<DominioPartes> damePartes() throws SQLException {
        ArrayList<DominioPartes> partes = new ArrayList<DominioPartes>();

        String sql = "SELECT * from productosPartes";
        Connection c = ds.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            DominioPartes d = new DominioPartes();
            d.setIdParte(rs.getInt("idParte"));
            d.setParte(rs.getString("parte"));
            partes.add(d);
        }
        return partes;
    }

    public DominioPartes damePartes(int id) throws SQLException {
        String sql = "SELECT * from productosPartes WHERE idParte="+id;
        Connection c = ds.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        DominioPartes d = new DominioPartes();
        while (rs.next()) {
            d.setIdParte(rs.getInt("idParte"));
            d.setParte(rs.getString("parte"));
        }
        return d;
    }
}
