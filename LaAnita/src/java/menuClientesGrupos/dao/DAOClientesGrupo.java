/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package menuClientesGrupos.dao;

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
import menuClientesGrupos.dominio.ClientesGrupos;
import usuarios.UsuarioSesion;

/**
 *
 * @author Usuario
 */
public class DAOClientesGrupo {

    private DataSource ds;
    private int idPerfil = 0;

    public DAOClientesGrupo() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            if (usuarioSesion.getUsuario() == null) {
                idPerfil = 0;
            } else {
                idPerfil = usuarioSesion.getUsuario().getIdPerfil();
            }
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public ArrayList<ClientesGrupos> dameListaClientesGrupos() throws SQLException {
        ArrayList<ClientesGrupos> lstClientesGrupos = new ArrayList();
        String sql = "SELECT * FROM clientesGrupos";
        Connection cn = ds.getConnection();
        Statement dt = cn.createStatement();
        try {
            ResultSet rs = dt.executeQuery(sql);
            while (rs.next()) {
                ClientesGrupos clientesGrupos = new ClientesGrupos();
                clientesGrupos.setIdGrupoCte(rs.getInt("idGrupoCte"));
                clientesGrupos.setGrupoCte(rs.getString("grupoCte"));
                lstClientesGrupos.add(clientesGrupos);
            }
        } finally {
            cn.close();
        }
        return lstClientesGrupos;
    }

    public void guardarClientesGrupo(ClientesGrupos clientesGrupos) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "INSERT INTO clientesGrupos (grupoCte) VALUES('" + clientesGrupos.getGrupoCte() + "')";
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }

    public void actualizar(ClientesGrupos clientesGrupos) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE clientesGrupos set grupoCte = '" + clientesGrupos.getGrupoCte() + "' WHERE idGrupoCte = '" + clientesGrupos.getIdGrupoCte() + "'";
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }
}
