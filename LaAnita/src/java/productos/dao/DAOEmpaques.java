package productos.dao;

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
import productos.dominio.Empaque;
import productos.dominio.SubEmpaque;
import productos.dominio.UnidadEmpaque;
import productos.to.TOEmpaque;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOEmpaques {
    private DataSource ds;
    
    public DAOEmpaques() throws NamingException {
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
    
    public boolean eliminar(int idEmpaque) throws SQLException {
        ResultSet rs;
        String strSQL;
        boolean ok=true;
        
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="SELECT * FROM empaques WHERE idSubEmpaque="+idEmpaque;
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                ok=false;
            } else {
                strSQL="DELETE FROM empaques WHERE idEmpaque="+idEmpaque;
                st.executeUpdate(strSQL);
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(ex);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public void modificar(String cod_emp, Empaque e) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate("UPDATE empaques "
                    + "SET cod_pro='"+e.getCod_pro()+"', idProducto="+e.getProducto().getIdProducto() + ", "
                    + "piezas="+e.getPiezas()+", idUnidadEmpaque="+e.getUnidadEmpaque().getIdUnidad()+", idSubEmpaque="+e.getSubEmpaque().getIdEmpaque()+", "
                    + "dun14='"+e.getDun14()+"', peso="+e.getPeso()+", volumen= "+e.getVolumen()+ " "
                    + "WHERE idEmpaque="+e.getIdEmpaque());
            if(!cod_emp.isEmpty()) {
                ResultSet rs=st.executeQuery("SELECT idEmpaque FROM productosOld WHERE cod_emp='"+cod_emp+"' AND cod_pro='"+e.getCod_pro()+"'");
                if(!rs.next()) {
                    st.executeUpdate(""
                            + "INSERT INTO productosOld (cod_emp, cod_pro, idEmpaque) "
                            + "VALUES ('"+cod_emp+"', '"+e.getCod_pro()+"', "+e.getIdEmpaque()+")");
                }
            }
            st.executeUpdate("commit transaction");
        } catch(SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw(ex);
        } finally {
            cn.close();
        }
    }
    
    public int agregar(String cod_emp, Empaque empaque) throws SQLException {
        int idEmpaque=0;
        String strSQL="INSERT INTO empaques (cod_pro, idProducto, piezas, idUnidadEmpaque, idSubEmpaque, dun14, peso, volumen) "
                + "VALUES ('"+empaque.getCod_pro() + "', " + empaque.getProducto().getIdProducto() + ", "
                + " "+empaque.getPiezas() + ", " + empaque.getUnidadEmpaque().getIdUnidad() + ", " + empaque.getSubEmpaque().getIdEmpaque() + ","
                + " '"+empaque.getDun14() + "', " + empaque.getPeso() + ", " + empaque.getVolumen() + ")";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate(strSQL);
            ResultSet rs=st.executeQuery("SELECT MAX(idEmpaque) AS idEmpaque FROM empaques");
            if(rs.next()) {
                idEmpaque=rs.getInt("idEmpaque");
            }
            if(!cod_emp.isEmpty()) {
                st.executeUpdate(""
                        + "INSERT INTO productosOld (cod_emp, cod_pro, idEmpaque) "
                        + "VALUES ('"+cod_emp+"', '"+empaque.getCod_pro()+"', "+idEmpaque+")");
            }
            st.executeUpdate("commit transaction");
        } catch(SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        return idEmpaque;
    }
    
    public ArrayList<TOEmpaque> obtenerEmpaquesClasificacion(int idGrupo, int idSubGrupo) throws NamingException, SQLException {
//        Producto p;
        TOEmpaque epq;
        ArrayList<TOEmpaque> lstEmpaques=new ArrayList<TOEmpaque>();
//        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte " +
            "WHERE p.idGrupo="+idGrupo+" OR p.idSubGrupo="+idSubGrupo+" " +
            "ORDER BY pp.parte, p.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
//            p=null;
//            int idProducto=0;
            ResultSet rs=st.executeQuery(strSQL); 
            while(rs.next()) {
//                if(idProducto!=rs.getInt("idProducto")) {
//                    idProducto=rs.getInt("idProducto");
//                    p=daoProductos.obtenerProducto(idProducto);
//                }
                epq=construir(rs);
//                epq.setProducto(p);
                lstEmpaques.add(epq);
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public ArrayList<TOEmpaque> obtenerEmpaquesDescripcion(String descripcion) throws NamingException, SQLException {
//        Producto p;
        TOEmpaque epq;
        ArrayList<TOEmpaque> lstEmpaques=new ArrayList<TOEmpaque>();
//        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte " +
            "WHERE p.descripcion like '%"+descripcion+"%' " +
            "ORDER BY pp.parte, p.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
//            p=null;
//            int idProducto=0;
            ResultSet rs=st.executeQuery(strSQL); 
            while(rs.next()) {
//                if(idProducto!=rs.getInt("idProducto")) {
//                    idProducto=rs.getInt("idProducto");
//                    p=daoProductos.obtenerProducto(idProducto);
//                }
                epq=construir(rs);
//                epq.setProducto(p);
                lstEmpaques.add(epq);
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public ArrayList<TOEmpaque> obtenerEmpaquesParte(int idParte) throws NamingException, SQLException {
//        Producto p;
        TOEmpaque epq;
        ArrayList<TOEmpaque> lstEmpaques=new ArrayList<TOEmpaque>();
//        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte " +
            "WHERE pp.idParte="+idParte+" " +
            "ORDER BY pp.parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
//            p=null;
//            int idProducto=0;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
//                if(idProducto!=rs.getInt("idProducto")) {
//                    idProducto=rs.getInt("idProducto");
//                    p=daoProductos.obtenerProducto(idProducto);
//                }
                epq=construir(rs);
//                epq.setProducto(p);
                lstEmpaques.add(epq);
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public ArrayList<TOEmpaque> obtenerEmpaques(int idProducto) throws SQLException, NamingException {
//        Producto p;
        TOEmpaque epq;
        ArrayList<TOEmpaque> lstEmpaques=new ArrayList<TOEmpaque>();
//        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+ " WHERE e.idProducto="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
//            p=null;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
//                if(p==null) {
//                    p=daoProductos.obtenerProducto(rs.getInt("idProducto"));
//                }
                epq=construir(rs);
//                epq.setProducto(p);
//                lstEmpaques.add(epq);
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public TOEmpaque obtenerEmpaque(String cod_pro) throws SQLException, NamingException {
        TOEmpaque epq=null;
//        DAOProductos daoProductos;
        String strSQL=sqlEmpaque()+" WHERE e.cod_pro='"+cod_pro+"'";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
//                daoProductos=new DAOProductos();
                epq=construir(rs);
//                epq.setProducto(daoProductos.obtenerProducto(rs.getInt("idProducto")));
            }
        } finally {
            cn.close();
        }
        return epq;
    }
    
    public TOEmpaque obtenerEmpaque(int idEmpaque) throws SQLException, NamingException {
        TOEmpaque epq=null;
//        DAOProductos daoProductos;
        String strSQL=sqlEmpaque()+" WHERE e.idEmpaque="+idEmpaque;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
//                daoProductos=new DAOProductos();
                epq=construir(rs);
//                epq.setProducto(daoProductos.obtenerProducto(rs.getInt("idProducto")));
            }
        } finally {
            cn.close();
        }
        return epq;
    }
    
    public TOEmpaque construir(ResultSet rs) throws SQLException {
        TOEmpaque epq=new TOEmpaque();
        epq.setIdEmpaque(rs.getInt("idEmpaque"));
        epq.setCod_pro(rs.getString("cod_pro"));
        epq.setIdProducto(rs.getInt("idProducto"));
        epq.setPiezas(rs.getInt("piezas"));
        UnidadEmpaque unidadEmpaque=new UnidadEmpaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
        epq.setUnidadEmpaque(unidadEmpaque);
        SubEmpaque sub=new SubEmpaque(rs.getInt("idSubEmpaque"), rs.getInt("piezas"), new UnidadEmpaque(rs.getInt("idUnidadSubEmpaque"), rs.getString("unidadSubEmpaque"), rs.getString("abreviaturaSubEmpaque")));
        epq.setSubEmpaque(sub);
        epq.setDun14(rs.getString("dun14"));
        epq.setPeso(rs.getDouble("peso"));
        epq.setVolumen(rs.getDouble("volumen"));
        return epq;
    }
    
    private String sqlEmpaque() {
        String strSQL=""
                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.dun14, e.peso, e.volumen"
                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque"
                + "     , isnull(se.idEmpaque, 0) as idSubEmpaque, se.piezas as piezasSubEmpaque"
                + "     , su.idUnidad as idUnidadSubEmpaque, su.unidad as unidadSubEmpaque, su.abreviatura as abreviaturaSubEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "LEFT JOIN empaques se ON se.idEmpaque=e.idSubEmpaque "
                + "LEFT JOIN empaquesUnidades su ON su.idUnidad=se.idUnidadEmpaque";
//        String strSQL=""
//                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, idSubEmpaque, e.dun14, e.peso, e.volumen"
//                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque "
//                + "FROM empaques e "
//                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque";
        return strSQL;
    }
    
    public ArrayList<SubEmpaque> obtenerListaSubEmpaques(int idProducto) throws SQLException {
        ArrayList<SubEmpaque> lstSubEmpaques=new ArrayList<SubEmpaque>();
        String strSQL="SELECT e.idEmpaque, e.piezas, u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "WHERE e.idProducto="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            UnidadEmpaque uEpq;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                uEpq=new UnidadEmpaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviaturaEmpaque"));
                lstSubEmpaques.add(new SubEmpaque(rs.getInt("idEmpaque"), rs.getInt("piezas"), uEpq));
            }
        } finally {
            cn.close();
        }
        return lstSubEmpaques;
    }
    
    public SubEmpaque obtenerSubEmpaque(int idSubEmpaque) throws SQLException {
        SubEmpaque subEpq=null;
        String strSQL=""
                + "SELECT e.idEmpaque, e.piezas, u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "WHERE e.idEmpaque="+idSubEmpaque;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            UnidadEmpaque uEpq;
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                uEpq=new UnidadEmpaque(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviaturaEmpaque"));
                subEpq=new SubEmpaque(idSubEmpaque, rs.getInt("piezas"), uEpq);
            }
        } finally {
            cn.close();
        }
        return subEpq;
    }
}
