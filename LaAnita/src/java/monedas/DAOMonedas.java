package monedas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author jesc
 */
public class DAOMonedas {
    public Moneda obtenerMoneda(int idMoneda) throws SQLException, NamingException {
        Context cI = new InitialContext();
        DataSource ds1 = (DataSource) cI.lookup("java:comp/env/jdbc/__webSystem");
        Moneda mon = null;
        Connection cn = ds1.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT idMoneda, moneda, codigoIso FROM Monedas\n"
                    + "WHERE idMoneda=" + idMoneda);
            if (rs.next()) {
                mon = construirMoneda(rs);
            }
        } finally {
            cn.close();
        }
        return mon;
    }

    private Moneda construirMoneda(ResultSet rs) throws SQLException {
        Moneda mon = new Moneda();
        mon.setIdMoneda(rs.getInt("idMoneda"));
        mon.setMoneda(rs.getString("moneda"));
        mon.setCodigoIso(rs.getString("codigoIso"));
        return mon;
    }

    public ArrayList<Moneda> obtenerMonedas() throws NamingException, SQLException {
        Context cI = new InitialContext();
        DataSource ds2 = (DataSource) cI.lookup("java:comp/env/jdbc/__webSystem");
        ArrayList<Moneda> lista = new ArrayList<Moneda>();
        ResultSet rs;
        Connection cn = ds2.getConnection();
        try {

            String stringSQL = "SELECT idMoneda, moneda, codigoIso FROM monedas where idMoneda between 0 and 4";

            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirMoneda(rs));
            }
        } finally {
            cn.close();
        }
        return lista;

    }
}
