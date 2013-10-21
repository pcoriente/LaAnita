package productos.dao;

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
import productos.dominio.Empaque;
import productos.dominio.Marca;
import productos.dominio.Producto;
import productos.dominio.SubEmpaque;
import productos.dominio.UnidadEmpaque;
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
        String strSQL="INSERT INTO empaques (cod_pro, idProducto, idMarca, piezas, idUnidadEmpaque, idSubEmpaque, dun14, peso, volumen) "
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
    
    public ArrayList<Empaque> obtenerEmpaquesParte(int idParte) throws NamingException, SQLException {
        Producto p;
        Empaque epq;
        ArrayList<Empaque> lstEmpaques=new ArrayList<Empaque>();
        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto\n" +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte\n" +
            "WHERE pp.idParte="+idParte+"\n" +
            "ORDER BY p.idProducto, pp.parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            p=null;
            int idProducto=0;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                if(idProducto!=rs.getInt("idProducto")) {
                    idProducto=rs.getInt("idProducto");
                    p=daoProductos.obtenerProducto(idProducto);
                }
                epq=construir(rs);
                epq.setProducto(p);
                lstEmpaques.add(epq);
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public ArrayList<Empaque> obtenerEmpaques(int idProducto) throws SQLException, NamingException {
        Producto p;
        Empaque epq;
        ArrayList<Empaque> lstEmpaques=new ArrayList<Empaque>();
        DAOProductos daoProductos=new DAOProductos();
        String strSQL=sqlEmpaque()+ " WHERE e.idProducto="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            p=null;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                if(p==null) {
                    p=daoProductos.obtenerProducto(rs.getInt("idProducto"));
                }
                epq=construir(rs);
                epq.setProducto(p);
                lstEmpaques.add(epq);
                
            }
        } finally {
            cn.close();
        }
        return lstEmpaques;
    }
    
    public Empaque obtenerEmpaque(String cod_pro) throws SQLException, NamingException {
        Empaque epq=null;
        DAOProductos daoProductos;
        String strSQL=sqlEmpaque()+" WHERE e.cod_pro='"+cod_pro+"'";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                daoProductos=new DAOProductos();
                epq=construir(rs);
                epq.setProducto(daoProductos.obtenerProducto(rs.getInt("idProducto")));
            }
        } finally {
            cn.close();
        }
        return epq;
    }
    
    public Empaque construir(ResultSet rs) throws SQLException {
        Empaque epq=new Empaque(rs.getInt("idEmpaque"));
        epq.setCod_pro(rs.getString("cod_pro"));
        epq.setProducto(null);
        //epq.setProducto(daoProductos.obtenerProducto(rs.getInt("idProducto")));
        ////epq.setMarca(new Marca(rs.getInt("idMarca"), rs.getString("marca"), false));
        epq.setPiezas(rs.getInt("piezas"));
        UnidadEmpaque unidadEmpaque=new UnidadEmpaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
        epq.setUnidadEmpaque(unidadEmpaque);
        SubEmpaque subEmpaque=new SubEmpaque(rs.getInt("idSubEmpaque"));
        if(subEmpaque.getIdEmpaque() > 0) {
            UnidadEmpaque unidadSubEmpaque=new UnidadEmpaque(rs.getInt("idUnidadSubEmpaque"), rs.getString("unidadSubEmpaque"), rs.getString("abreviaturaSubEmpaque"));
            subEmpaque.setPiezas(rs.getInt("piezasSubEmpaque"));
            subEmpaque.setUnidadEmpaque(unidadSubEmpaque);
        }
        epq.setSubEmpaque(subEmpaque);
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
        return strSQL;
    }
    
    public ArrayList<SubEmpaque> obtenerListaSubEmpaques(int idProducto, int piezas) throws SQLException {
        ArrayList<SubEmpaque> lstSubEmpaques=new ArrayList<SubEmpaque>();
        String strSQL="SELECT e.idEmpaque, e.piezas, "
                + "     u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
                + "WHERE e.idProducto="+idProducto+" and e.piezas<="+piezas;
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
                + "SELECT e.idEmpaque, e.piezas, "
                + "     u.idUnidad, u.unidad, u.abreviatura as abreviaturaEmpaque "
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
