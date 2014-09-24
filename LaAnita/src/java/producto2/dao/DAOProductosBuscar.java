package producto2.dao;

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
import producto2.dominio.Empaque;
import producto2.to.TOProducto;
import usuarios.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOProductosBuscar {
    private DataSource ds;
    
    public DAOProductosBuscar() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
//    public ArrayList<TOProducto> obtenerCombo(int idProducto) throws SQLException {
//        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
//        String strSQL=""
//                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.dun14, e.peso, e.volumen"
//                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque"
//                + "     , isnull(se.idEmpaque, 0) as idSubEmpaque, se.piezas as piezasSubEmpaque"
//                + "     , su.idUnidad as idUnidadSubEmpaque, su.unidad as unidadSubEmpaque, su.abreviatura as abreviaturaSubEmpaque "
//                + "FROM empaquesCombos c "
//                + "INNER JOIN empaques e ON e.idEmpaque=c.idSubEmpaque "
//                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
//                + "LEFT JOIN empaques se ON se.idEmpaque=e.idSubEmpaque "
//                + "LEFT JOIN empaquesUnidades su ON su.idUnidad=se.idUnidadEmpaque "
//                + "WHERE c.idEmpaque="+idProducto;
//        Connection cn=ds.getConnection();
//        Statement st=cn.createStatement();
//        try {
//            ResultSet rs=st.executeQuery(strSQL);
//            while(rs.next()) {
//                productos.add(this.construir(rs));
//            }
//        } finally {
//            cn.close();
//        }
//        return productos;
//    }
    
    public ArrayList<TOProducto> obtenerProductosClasificacion(int idGrupo, int idSubGrupo) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos P on P.idProducto=E.idProducto " +
            "LEFT JOIN productosPartes PP on PP.idParte=P.idParte " +
            "WHERE P.idGrupo="+idGrupo+" OR P.idSubGrupo="+idSubGrupo+" " +
            "ORDER BY PP.parte, P.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductosDescripcion(String descripcion) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos P on P.idProducto=E.idProducto " +
            "LEFT JOIN productosPartes PP on PP.idParte=P.idParte " +
            "WHERE P.descripcion like '%"+descripcion+"%' "+
            "ORDER BY PP.parte, P.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductosParte(int idParte) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos P on P.idProducto=E.idProducto " +
            "WHERE P.idParte="+idParte+" "+
            "ORDER BY P.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductos(int idArticulo) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+ " WHERE E.idProducto="+idArticulo+" ORDER BY E.cod_pro";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public TOProducto obtenerProductoSku(String sku) throws SQLException {
        TOProducto to=null;
        String strSQL=sqlEmpaque()+ " WHERE E.cod_pro='"+sku+"'";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=this.construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    public TOProducto obtenerProducto(int idProducto) throws SQLException {
        TOProducto to=null;
        String strSQL=sqlEmpaque()+ " WHERE E.idEmpaque="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=this.construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
//    public TOProducto construir(ResultSet rs) throws SQLException {
//        TOProducto to=new TOProducto();
//        to.setIdProducto(rs.getInt("idEmpaque"));
//        to.setCod_pro(rs.getString("cod_pro"));
//        to.setIdArticulo(rs.getInt("idProducto"));
//        to.setPiezas(rs.getInt("piezas"));
//        Empaque empaque=new Empaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
//        to.setEmpaque(empaque);
//        SubProducto sub=new SubProducto(rs.getInt("idSubEmpaque"), rs.getInt("piezasSubEmpaque"), new Empaque(rs.getInt("idUnidadSubEmpaque"), rs.getString("unidadSubEmpaque"), rs.getString("abreviaturaSubEmpaque")));
//        to.setSubProducto(sub);
//        to.setDun14(rs.getString("dun14"));
//        to.setPeso(rs.getDouble("peso"));
//        to.setVolumen(rs.getDouble("volumen"));
//        return to;
//    }
    
    public TOProducto construir(ResultSet rs) throws SQLException {
        TOProducto to=new TOProducto();
        to.setIdProducto(rs.getInt("idEmpaque"));
        to.setCod_pro(rs.getString("cod_pro"));
        to.setIdArticulo(rs.getInt("idProducto"));
        to.setPiezas(rs.getInt("piezas"));
        Empaque empaque=new Empaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
        to.setEmpaque(empaque);
//        SubProducto sub=new SubProducto(rs.getInt("idSubEmpaque"));
//        to.setSubProducto(sub);
        to.setIdSubProducto(rs.getInt("idSubEmpaque"));
        to.setSubProducto(rs.getString("descripcion"));
        to.setPiezasSubEmpaque(rs.getDouble("piezasSubEmpaque"));
        to.setPeso(rs.getDouble("peso"));
        to.setVolumen(rs.getDouble("volumen"));
        to.setDun14(rs.getString("dun14"));
        return to;
    }
    
//    private String sqlEmpaque() {
//        String strSQL=""
//                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.dun14, e.peso, e.volumen"
//                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque"
//                + "     , isnull(se.idEmpaque, 0) as idSubEmpaque, se.piezas as piezasSubEmpaque"
//                + "     , su.idUnidad as idUnidadSubEmpaque, su.unidad as unidadSubEmpaque, su.abreviatura as abreviaturaSubEmpaque "
//                + "FROM empaques e "
//                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
//                + "LEFT JOIN empaques se ON se.idEmpaque=e.idSubEmpaque "
//                + "LEFT JOIN empaquesUnidades su ON su.idUnidad=se.idUnidadEmpaque";
//        return strSQL;
//    }
    
     private String sqlEmpaque() {
        String strSQL="" +
                "SELECT E.idEmpaque, E.cod_pro, E.idProducto, E.piezas " +
"                     , U.idUnidad as idUnidadEmpaque, U.unidad as unidadEmpaque, U.abreviatura as abreviaturaEmpaque " +
"                     , E.idSubEmpaque, ISNULL(ES.descripcion,'') AS descripcion, ISNULL(ES.piezas,1)*ISNULL(ES.piezasSubEmpaque,1) AS piezasSubEmpaque " +
"                     , E.peso, E.volumen, E.dun14 " +
"                FROM empaques E " +
"                LEFT JOIN empaquesSubEmpaques ES ON ES.idEmpaque=E.idSubEmpaque " +
"                INNER JOIN empaquesUnidades U ON U.idUnidad=E.idUnidadEmpaque";
        return strSQL;
    }
}
