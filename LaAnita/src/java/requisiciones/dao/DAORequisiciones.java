/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package requisiciones.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import requisiciones.dominio.RequisicionProducto;
import requisiciones.to.TORequisicionProducto;
import requisiciones.to.TORequisicionEncabezado;
import usuarios.UsuarioSesion;


public class DAORequisiciones {

    private final DataSource ds;

    public DAORequisiciones() throws NamingException {
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

    public void guardarRequisicion(int idEmpresa, int idDepto, int idSolicitante, ArrayList<RequisicionProducto> pr) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement ps1;
        PreparedStatement ps2;

        try {
            //  st.executeUpdate("begin transaction");
            //CABECERO
            String strSQL1 = "INSERT INTO requisiciones(idEmpresa, idDepto, idSolicitante,fechaRequisicion) VALUES (" + idEmpresa + ", " + idDepto + ", " + idSolicitante + ",GETDATE())";
            String strSQLIdentity = "SELECT @@IDENTITY as idReq";
            ps1 = cn.prepareStatement(strSQL1);
            ps1.executeUpdate();
            ps1 = cn.prepareStatement(strSQLIdentity);
            ResultSet rs = ps1.executeQuery();
            int identity = 0;
            if (rs.next()) {
                identity = rs.getInt("idReq");
            }
            // DETALLE
            String strSQL2 = "INSERT INTO requisicionDetalle1(idRequisicion,idProducto, cantidad) VALUES (?,?,?)";
            ps2 = cn.prepareStatement(strSQL2);

            for (RequisicionProducto e : pr) {
                ps2.setInt(1, identity);
                ps2.setInt(2, e.getProducto().getIdProducto());
                ps2.setInt(3, e.getCantidad());
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            // st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
    }

    public ArrayList<TORequisicionEncabezado> dameRequisicion() throws SQLException {
        ArrayList<TORequisicionEncabezado> lista = new ArrayList<TORequisicionEncabezado>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "select r.idRequisicion, eg.empresa, ed.depto, e.empleado,  r.fechaRequisicion from requisiciones r\n" +
"                    inner join empresasGrupo eg on r.idEmpresa=eg.idEmpresa\n" +
"                    inner join empleados e on r.idSolicitante= e.idEmpleado\n" +
"                    inner join empleadosDeptos ed on ed.idDepto = e.idDepto\n" +
"                     order by idRequisicion";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirEncabezado(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private TORequisicionEncabezado construirEncabezado(ResultSet rs) throws SQLException {
        TORequisicionEncabezado to = new TORequisicionEncabezado();

        to.setIdRequisicion(rs.getInt("idRequisicion"));
        to.setNombreComercial(rs.getString("empresa"));
        to.setDepto(rs.getString("depto"));
        to.setUsuario(rs.getString("empleado"));
        to.setFecha(rs.getDate("fechaRequisicion"));

        return to;
    }

    public ArrayList<TORequisicionProducto> dameRequisicionDetalle(int idRequisi) throws SQLException {

        ArrayList<TORequisicionProducto> lista = new ArrayList<TORequisicionProducto>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "select rd.idRequisicion,rd.idProducto,rd.cantidad from requisicionDetalle1 rd\n"
                
                    + "where idRequisicion="+idRequisi;
                   
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return lista;

    }
    
     private TORequisicionProducto construirDetalle(ResultSet rs) throws SQLException {
        TORequisicionProducto to = new TORequisicionProducto();

        to.setIdProducto(rs.getInt("idProducto"));
        to.setCantidad(rs.getInt("cantidad"));

        return to;
    }
}
