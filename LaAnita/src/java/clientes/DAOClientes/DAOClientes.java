/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientes.DAOClientes;

import clientes.dominio.Cliente;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
 * @author Usuario
 */
public class DAOClientes {

    private DataSource ds = null;

    public DAOClientes() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            Logger.getLogger(cliente.dao2.DAOClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Cliente> lstClientes() throws SQLException {
        ArrayList<Cliente> lstClientes = new ArrayList<Cliente>();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "SELECT *  from clientes cl\n"
                + "INNER JOIN contribuyentes cnt\n"
                + "ON cl.idContribuyente = cnt.idContribuyente\n"
                + "INNER JOIN impuestosZonas iz\n"
                + "ON iz.idZona = cl.idImpuestoZona\n"
                + "INNER JOIN contribuyentesRfc cr \n"
                + "ON cr.idRfc = cnt.idRfc "
                + "inner join direcciones d \n"
                + "on d.idDireccion = cnt.idDireccion";
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("idcliente"));
                cliente.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
                cliente.getContribuyente().setContribuyente(rs.getString("contribuyente"));
                cliente.setCodigoCliente(rs.getInt("codigoCliente"));
                cliente.getImpuestoZona().setIdZona(rs.getInt("idImpuestoZona"));
                cliente.setDiasCredito(rs.getInt("diasCredito"));
                cliente.setLimiteCredito(rs.getFloat("limiteCredito"));
                cliente.setDescuentoComercial(rs.getFloat("desctoComercial"));
                cliente.setDescuentoProntoPago(rs.getDouble("desctoProntoPago"));
                cliente.setDiasBloqueo(rs.getInt("diasBloqueo"));
                cliente.getContribuyente().setIdRfc(rs.getInt("idRfc"));
                cliente.getContribuyente().setRfc(rs.getString("rfc"));
                cliente.getContribuyente().setCurp(rs.getString("curp"));
                cliente.getContribuyente().getDireccion().setIdDireccion(rs.getInt("idDireccion"));
                cliente.getContribuyente().getDireccion().setCalle(rs.getString("calle"));
                cliente.getContribuyente().getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
                cliente.getContribuyente().getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
                cliente.getContribuyente().getDireccion().setColonia(rs.getString("colonia"));
                cliente.getContribuyente().getDireccion().setLocalidad(rs.getString("localidad"));
                cliente.getContribuyente().getDireccion().setReferencia(rs.getString("referencia"));
                cliente.getContribuyente().getDireccion().setMunicipio(rs.getString("municipio"));
                cliente.getContribuyente().getDireccion().setEstado(rs.getString("estado"));
                cliente.getContribuyente().getDireccion().getPais().setIdPais(rs.getInt("idPais"));
                cliente.getContribuyente().getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
                lstClientes.add(cliente);
            }
        } finally {
            cn.close();
        }
        return lstClientes;
    }

    public Cliente dameInformacionCliente(String rfc) throws SQLException {
        Cliente cliente = new Cliente();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "SELECT *  from clientes cl\n"
                + "INNER JOIN contribuyentes cnt\n"
                + "ON cl.idContribuyente = cnt.idContribuyente\n"
                + "INNER JOIN impuestosZonas iz\n"
                + "ON iz.idZona = cl.idImpuestoZona\n"
                + "INNER JOIN contribuyentesRfc cr \n"
                + "ON cr.idRfc = cnt.idRfc "
                + "inner join direcciones d \n"
                + "on d.idDireccion = cnt.idDireccion "
                + " WHERE rfc ='" + rfc + "'";
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cliente.setIdCliente(rs.getInt("idcliente"));
                cliente.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
                cliente.getContribuyente().setContribuyente(rs.getString("contribuyente"));
                cliente.setCodigoCliente(rs.getInt("codigoCliente"));
                cliente.getImpuestoZona().setIdZona(rs.getInt("idImpuestoZona"));
                cliente.setDiasCredito(rs.getInt("diasCredito"));
                cliente.setLimiteCredito(rs.getFloat("limiteCredito"));
                cliente.setDescuentoComercial(rs.getFloat("desctoComercial"));
                cliente.setDescuentoProntoPago(rs.getDouble("desctoProntoPago"));
                cliente.setDiasBloqueo(rs.getInt("diasBloqueo"));
                cliente.getContribuyente().setIdRfc(rs.getInt("idRfc"));
                cliente.getContribuyente().setRfc(rs.getString("rfc"));
                cliente.getContribuyente().setCurp(rs.getString("curp"));
                cliente.getContribuyente().getDireccion().setIdDireccion(rs.getInt("idDireccion"));
                cliente.getContribuyente().getDireccion().setCalle(rs.getString("calle"));
                cliente.getContribuyente().getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
                cliente.getContribuyente().getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
                cliente.getContribuyente().getDireccion().setColonia(rs.getString("colonia"));
                cliente.getContribuyente().getDireccion().setLocalidad(rs.getString("localidad"));
                cliente.getContribuyente().getDireccion().setReferencia(rs.getString("referencia"));
                cliente.getContribuyente().getDireccion().setMunicipio(rs.getString("municipio"));
                cliente.getContribuyente().getDireccion().setEstado(rs.getString("estado"));
                cliente.getContribuyente().getDireccion().getPais().setIdPais(rs.getInt("idPais"));
                cliente.getContribuyente().getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
            }
        } finally {
            cn.close();
        }
        return cliente;
    }

    public void guardarCliente(Cliente cliente) throws SQLException {
        Statement st = null;
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            st = cn.createStatement();
            int idContribuyenteRfc = 0;
            int idContribuyenteDireccion = 0;
            int idContribuyente = 0;
            Date date = new Date();
            String sqlInsertarDireccionContribuyente = "INSERT INTO direcciones "
                    + "(calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado, idPais, codigoPostal, numeroLocalizacion)"
                    + "VALUES('" + cliente.getContribuyente().getDireccion().getCalle() + "', '" + cliente.getContribuyente().getDireccion().getNumeroExterior() + "','" + cliente.getContribuyente().getDireccion().getNumeroInterior() + "'"
                    + ", '" + cliente.getContribuyente().getDireccion().getColonia() + "', '" + cliente.getContribuyente().getDireccion().getLocalidad() + "','" + cliente.getContribuyente().getDireccion().getReferencia() + "',"
                    + "'" + cliente.getContribuyente().getDireccion().getMunicipio() + "', '" + cliente.getContribuyente().getDireccion().getEstado() + "' , '" + cliente.getContribuyente().getDireccion().getPais().getIdPais() + "', "
                    + "'" + cliente.getContribuyente().getDireccion().getCodigoPostal() + "', '" + cliente.getContribuyente().getDireccion().getNumeroLocalizacion() + "')";

            st.executeUpdate("begin transaction");
            st.executeUpdate(sqlInsertarDireccionContribuyente);
            rs = st.executeQuery("SELECT @@IDENTITY AS idDireccionContribuyente");
            while (rs.next()) {
                idContribuyenteDireccion = rs.getInt("idDireccionContribuyente");
            }
            String sqlInsertarContribuyentesRfc = "INSERT INTO contribuyentesRfc (rfc, curp) VALUES('" + cliente.getContribuyente().getRfc() + "', '" + cliente.getContribuyente().getCurp() + "' )";
            st.executeUpdate(sqlInsertarContribuyentesRfc);
            rs = st.executeQuery("SELECT @@IDENTITY AS idRfc");
            while (rs.next()) {
                idContribuyenteRfc = rs.getInt("idRfc");
            }
            String sqlInsertarContribuyentes = "INSERT INTO contribuyentes (contribuyente, idRfc, idDireccion) VALUES ('" + cliente.getContribuyente().getContribuyente() + "', '" + idContribuyenteRfc + "', '" + idContribuyenteDireccion + "') ";
            st.executeUpdate(sqlInsertarContribuyentes);
            rs = st.executeQuery("SELECT @@IDENTITY AS idContribuyente");
            while (rs.next()) {
                idContribuyente = rs.getInt("idContribuyente");
            }
            String sqlInsertarClientes = "INSERT INTO clientes (codigoCliente, idContribuyente, idImpuestoZona, fechaAlta, diasCredito, limiteCredito, desctoComercial, desctoProntoPago,diasBloqueo)"
                    + " VALUES('" + cliente.getCodigoCliente() + "', '" + idContribuyente + "', '" + cliente.getImpuestoZona().getIdZona() + "', GETDATE(), '" + cliente.getDiasCredito() + "', '" + cliente.getLimiteCredito() + "', '" + cliente.getDescuentoComercial() + "', '" + cliente.getDescuentoProntoPago() + "', '" + cliente.getDiasBloqueo() + "')";
            st.executeUpdate(sqlInsertarClientes);
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback transaction");
            Logger.getLogger(DAOClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            cn.close();
        }
    }

    public void actualizarClientes(Cliente cliente) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sqlActualizar = "UPDATE clientes SET codigoCliente = '" + cliente.getCodigoCliente() + "', idImpuestoZona = '" + cliente.getImpuestoZona().getIdZona() + "', diasCredito = '" + cliente.getDiasCredito() + "', "
                + "limiteCredito = '" + cliente.getLimiteCredito() + "', desctoComercial = '" + cliente.getDescuentoComercial() + "', desctoProntoPago ='" + cliente.getDescuentoProntoPago() + "' , "
                + "diasBloqueo = '" + cliente.getDiasBloqueo() + "' WHERE idcliente = '" + cliente.getIdCliente() + "'";
        try {
            st.executeUpdate(sqlActualizar);
        } finally {
            cn.close();
        }
    }
}
