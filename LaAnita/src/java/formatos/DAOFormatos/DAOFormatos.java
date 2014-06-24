/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.DAOFormatos;

import clientesListas.dominio.ClientesFormatos;
import formatos.dominio.ClientesFormato;
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

    int idUsuario;
    private DataSource ds = null;

    public DAOFormatos() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            this.idUsuario = usuarioSesion.getUsuario().getId();

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public void guardarFormato(ClientesFormatos clientesFormatos) throws SQLException {
        String sql = "INSERT INTO clientesFormato (formato, idGrupoCte) VALUES ('" + clientesFormatos.getFormato() + "', '" + clientesFormatos.getClientesGrupo().getIdGrupoCte() + "')";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }

    public ArrayList<ClientesFormatos> dameFormatos(int idGrupoClte) throws SQLException {
        ArrayList<ClientesFormatos> lstFormatos = null;
        String sql = "SELECT * FROM  clientesFormato WHERE idGrupoCte  = '" + idGrupoClte + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try { 
            ResultSet rs  = st.executeQuery(sql);
            lstFormatos= new ArrayList<ClientesFormatos>();
            while(rs.next()){
                ClientesFormatos formatos = new ClientesFormatos();
                formatos.setIdFormato(rs.getInt("idFormato"));
                formatos.setFormato(rs.getString("formato"));
                formatos.getClientesGrupo().setIdGrupoCte(rs.getInt("idGrupoCte"));
                lstFormatos.add(formatos);
            }
        } finally {
            cn.close();
        }
        return lstFormatos;
    }
}
