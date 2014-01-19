/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.dao;

import agentes.dominio.Agentes;
import cedis.dominio.MiniCedis;
import contribuyentes.Contribuyente;
import direccion.dominio.Direccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author Anita
 */
public class DaoAgentes {

    private DataSource ds = null;

    public DaoAgentes() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            try {
                throw (ex);
            } catch (NamingException ex1) {
                Logger.getLogger(DaoAgentes.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public ArrayList<Agentes> listaAgentes() throws SQLException {
        ArrayList<Agentes> listagentes = new ArrayList<Agentes>();
        String slq = "select rfc ,agente, cedis from agentes a\n"
                + "inner join contribuyentes ct\n"
                + "on ct.idContribuyente = a.idContribuyente\n"
                + "inner join contribuyentesRfc crfc\n"
                + "on crfc.idRfc = ct.idRfc\n"
                + "inner join cedis cd\n"
                + "on cd.idCedis = a.idCedis";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(slq);
        while (rs.next()) {
            Agentes agentes = new Agentes();
            agentes.setRfc(rs.getString("rfc"));
            agentes.setAgente(rs.getString("agente"));
            agentes.setCedis(rs.getString("cedis"));
            listagentes.add(agentes);
        }
        return listagentes;
    }

    public boolean guardarAgentes(Contribuyente contribuyente, Direccion direccion, Agentes agente, MiniCedis minicedis) {
        boolean x = false;
        String sqlContribuyente = "";
        String sqlDireccion = "";
        String sqlAgente = "";
        return x;
    }
}
