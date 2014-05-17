package movimientos.dao;

import java.sql.Connection;
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
import movimientos.dominio.Lote;
import usuarios.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOLotes {

    private String strSQL;
    private DataSource ds = null;

    public DAOLotes() throws NamingException {
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

    public void gestionar(Lote lote, int idMovto, double cantidad) {
    }

    public void liberaEnvio(Lote lote, int idMovto, double cantidad) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.execute("BEGIN TRANSACTION");
            this.libera(lote, idMovto, cantidad, cn);

            this.strSQL = "UPDATE movimientosDetalle SET cantFacturada=cantFacturada-" + cantidad + " "
                    + "WHERE idMovto=" + idMovto + " AND idEmpaque=" + lote.getIdProducto();
            st.executeUpdate(this.strSQL);

            st.execute("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.execute("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }

    private void libera(Lote lote, int idMovto, double cantidad, Connection cn) throws SQLException {
        Statement st = cn.createStatement();
        
        this.strSQL = "UPDATE almacenesEmpaques SET separados=separados-" + cantidad + " "
                + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idEmpaque=" + lote.getIdProducto();
        st.executeUpdate(this.strSQL);

        this.strSQL = "UPDATE lotesAlmacenes SET separados=separados-" + cantidad + " "
                + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
        st.executeUpdate(this.strSQL);

        if (lote.getSeparados() == 0) {
            this.strSQL = "DELETE FROM lotesKardex "
                    + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idMovto=" + idMovto + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
        } else {
            this.strSQL = "UPDATE lotesKardex SET cantidad=cantidad-" + cantidad + " "
                    + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idMovto=" + idMovto + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
        }
        st.executeUpdate(this.strSQL);
    }

    public double separaEnvio(Lote lote, int idMovto, double cantidad) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.execute("BEGIN TRANSACTION");
            cantidad = this.separa(lote, idMovto, cantidad, cn);

            this.strSQL = "UPDATE movimientosDetalle SET cantFacturada=cantFacturada+" + cantidad + " "
                    + "WHERE idMovto=" + idMovto + " AND idEmpaque=" + lote.getIdProducto();
            st.executeUpdate(this.strSQL);

            st.execute("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.execute("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
        return cantidad;
    }

    private double separa(Lote lote, int idMovto, double cantidad, Connection cn) throws SQLException {
        Statement st = cn.createStatement();
        
        this.strSQL = "SELECT existenciaOficina-separados AS saldo "
                + "FROM almacenesEmpaques "
                + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idEmpaque=" + lote.getIdProducto();
        ResultSet rs = st.executeQuery(this.strSQL);
        if (rs.next()) {
            if (rs.getDouble("saldo") < cantidad) {
                cantidad = rs.getDouble("saldo");
            }
        } else {
            throw new SQLException("No se encontro el producto en oficina");
        }
        this.strSQL = "SELECT saldo-separados AS saldo "
                + "FROM lotesAlmacenes "
                + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
        rs = st.executeQuery(this.strSQL);
        if (rs.next()) {
            if (rs.getDouble("saldo") < cantidad) {
                cantidad = rs.getDouble("saldo");
            }
        } else {
            throw new SQLException("No se encontro el lote");
        }
        if (cantidad > 0) {
            this.strSQL = "UPDATE almacenesEmpaques SET separados=separados+" + cantidad + " "
                    + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idEmpaque=" + lote.getIdProducto();
            st.executeUpdate(this.strSQL);

            this.strSQL = "UPDATE lotesAlmacenes SET separados=separados+" + cantidad + " "
                    + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
            st.executeUpdate(this.strSQL);

            if (lote.getSeparados() == cantidad) {
                this.strSQL = "INSERT INTO lotesKardex (idAlmacen, idMovto, idProducto, lote, cantidad, suma, fecha) "
                        + "VALUES(" + lote.getIdAlmacen() + ", " + idMovto + ", " + lote.getIdProducto() + ", '" + lote.getLote() + "', " + cantidad + ", 0, GETDATE())";
            } else {
                this.strSQL = "UPDATE lotesKardex SET cantidad=cantidad+" + cantidad + " "
                        + "WHERE idAlmacen=" + lote.getIdAlmacen() + " AND idMovto=" + idMovto + " AND idProducto=" + lote.getIdProducto() + " AND lote='" + lote.getLote() + "'";
            }
            st.executeUpdate(this.strSQL);
        } else {
            cantidad = 0;
        }
        return cantidad;
    }

    public ArrayList<Lote> obtenerLotes(int idAlmacen, int idMovto, int idProducto) throws SQLException {
        ArrayList<Lote> lotes = new ArrayList<Lote>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
//            st.execute("BEGIN TRANSACTION");
            this.strSQL = "SELECT idAlmacen, idProducto, lote, cantidad, saldo, separados, fechaCaducidad "
                    + "FROM lotesAlmacenes "
                    + "WHERE idalmacen=" + idAlmacen + " AND idProducto=" + idProducto + " AND saldo > 0 "
                    + "ORDER BY lote";
            ResultSet rs = st.executeQuery(this.strSQL);
            while (rs.next()) {
                lotes.add(this.construir(rs));
            }
            this.strSQL = "SELECT * FROM lotesKardex "
                    + "WHERE idAlmacen=" + idAlmacen + " AND idMovto=" + idMovto + " AND idProducto=" + idProducto;
            rs = st.executeQuery(this.strSQL);
            while (rs.next()) {
                for (Lote l : lotes) {
                    if (l.getLote().equals(rs.getString("lote"))) {
                        l.setSeparados(rs.getDouble("cantidad"));
                        break;
                    }
                }
            }
//            st.execute("COMMIT TRANSACTION");
//        } catch (SQLException ex) {
//            st.execute("ROLLBACK TRANSACTION");
//            throw ex;
        } finally {
            cn.close();
        }
        return lotes;
    }

    private Lote construir(ResultSet rs) throws SQLException {
        Lote lote = new Lote();
        lote.setIdAlmacen(rs.getInt("idAlmacen"));
        lote.setIdProducto(rs.getInt("idProducto"));
        lote.setLote(rs.getString("lote"));
        lote.setCantidad(rs.getDouble("cantidad"));
        lote.setSaldo(rs.getDouble("saldo") - rs.getDouble("separados"));
        lote.setSeparados(0);
        lote.setFechaCaducidad(new java.util.Date(rs.getDate("fechaCaducidad").getTime()));
        return lote;
    }
}
