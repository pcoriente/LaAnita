package productos.dao;

import impuestos.dominio.ImpuestoGrupo;
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
import productos.dominio.Grupo;
import productos.dominio.Marca;
import productos.dominio.Parte;
import productos.dominio.Parte2;
import productos.dominio.Producto;
import productos.dominio.SubGrupo;
import productos.dominio.Tipo;
import productos.dominio.Unidad;
import productos.dominio.Upc;
import unidadesMedida.UnidadMedida;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOProductos {
    private DataSource ds;
    
    public DAOProductos() throws NamingException {
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
    
    public void modificar(Producto p) throws SQLException, NamingException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        DAOUpcs daoUpcs=new DAOUpcs();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate("UPDATE productos "
                    + "SET idParte="+p.getParte2().getIdParte()+", descripcion='"+p.getDescripcion()+"', "
                    + "idTipo="+p.getTipo().getIdTipo()+", idSubGrupo="+p.getSubGrupo().getIdSubGrupo()+", "
                    + "idGrupo="+p.getGrupo().getIdGrupo() + ", "
                    + "idUnidadProducto="+p.getUnidad().getIdUnidad()+", idMarca=" + p.getMarca().getIdMarca() + ", "
                    + "contenido="+p.getContenido()+", idUnidadMedida="+p.getUnidadMedida().getIdUnidadMedida()+", idImpuesto= "+p.getImpuesto().getIdGrupo()+ " "
                    + "WHERE idProducto="+p.getIdProducto());
            for(Upc u: p.getUpcs()) {
                if(u.getIdUpc()==0) {
                    u.setIdUpc(daoUpcs.agregar(u));
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
    
    public int agregar(Producto p) throws SQLException, NamingException {
        int idProducto=0;
        String strSQL=""
                + "INSERT INTO productos (idParte, descripcion, idTipo, idGrupo, idSubGrupo, idMarca, idUnidadProducto, contenido, idUnidadMedida, idImpuesto) "
                + "VALUES ("+ p.getParte2().getIdParte() + ", '" + p.getDescripcion() + "', "
                + p.getTipo().getIdTipo() + ", " + p.getGrupo().getIdGrupo() + ", " + p.getSubGrupo().getIdSubGrupo() + ", " + p.getMarca().getIdMarca() + ", "
                + p.getUnidad().getIdUnidad() + ", " + p.getContenido() + ", " + p.getUnidadMedida().getIdUnidadMedida() + ","
                + p.getImpuesto().getIdGrupo() + ")";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate(strSQL);
            ResultSet rs=st.executeQuery("SELECT max(idProducto) AS idProducto FROM productos");
            if(rs.next()) {
                idProducto=rs.getInt("idProducto");
            }
            st.executeUpdate("commit transaction");
        } catch(SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw(ex);
        } finally {
            cn.close();
        }
        return idProducto;
    }
    
    public Producto obtenerProducto(String codBar) throws SQLException, NamingException {
        Producto producto=null;
        int idProducto=this.obtenerIdProducto(codBar);
        if(idProducto!=0) {
            producto=this.obtenerProducto(idProducto);
        }
        return producto;
    }
    
    private int obtenerIdProducto(String codBar) throws SQLException {
        int idProducto=0;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idProducto FROM productosUpcs WHERE upc='"+codBar+"'");
            if(rs.next()) {
                idProducto=rs.getInt("idProducto");
            }
        } finally {
            cn.close();
        }
        return idProducto;
    }
    
    public ArrayList<Producto> obtenerProductos(int idParte) throws SQLException, NamingException {
        ArrayList<Producto> productos=new ArrayList<Producto>();
        String strSQL=""
                + "SELECT p.idProducto, pa.idParte, pa.parte, p.descripcion, t.idTipo, t.tipo,"
                + "     isnull(sg.idSubGrupo, 0) as idSubGrupo, isnull(sg.subGrupo, '') as subGrupo, "
                + "     isnull(g.idGrupo, 0) as idGrupo, isnull(g.codigoGrupo, 0) as codigoGrupo, isnull(g.grupo, '') as grupo, "
                + "     isnull(m.idMarca, 0) as idMarca, isnull(m.marca, '') as marca, "
                + "     COALESCE(u.idUnidad, 0) AS idUnidad, COALESCE(u.unidad, '') AS UNIDAD, COALESCE(u.abreviatura, '') AS abreviatura, p.contenido,"
                + "     COALESCE(um.idUnidadMedida, 0) AS idUnidadMedida, COALESCE(um.unidadMedida, '') as unidadMedida, COALESCE(um.abreviatura, '') as medAbrev, 0 as idTipoUnidadMedida,"
                + "     i.idGrupo as idImpuestoGrupo, i.grupo as impuestoGrupo "
                + "FROM productos p "
                + "INNER JOIN productosPartes pa ON pa.idParte=p.idParte "
                + "INNER JOIN productosTipos t ON t.idTipo=p.idTipo "
                + "LEFT JOIN productosSubGrupos sg ON sg.idSubGrupo=p.idSubGrupo "
                + "LEFT JOIN productosGrupos g ON g.idGrupo=p.idGrupo "
                + "LEFT JOIN productosUnidades u ON u.idUnidad=p.idUnidadProducto "
                + "LEFT JOIN unidadesMedida um ON um.idUnidadMedida=p.idUnidadMedida "
                + "LEFT JOIN productosMarcas m on m.idMarca=p.idMarca "
                + "INNER JOIN impuestosGrupos i ON i.idGrupo=p.idImpuesto "
                + "WHERE p.idParte="+idParte;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            Producto producto;
            DAOUpcs daoUpcs=new DAOUpcs();
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                producto=construir(rs);
                producto.setUpcs(daoUpcs.obtenerUpcs(producto.getIdProducto()));
                productos.add(producto);
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public Producto obtenerProducto(int idProducto) throws SQLException, NamingException {
        Producto producto=null;
        String strSQL=""
                + "SELECT p.idProducto, pa.idParte, pa.parte, p.descripcion, t.idTipo, t.tipo,"
                + "     isnull(sg.idSubGrupo, 0) as idSubGrupo, isnull(sg.subGrupo, '') as subGrupo, "
                + "     isnull(g.idGrupo, 0) as idGrupo, isnull(g.codigoGrupo, 0) as codigoGrupo, isnull(g.grupo, '') as grupo, "
                + "     isnull(m.idMarca, 0) as idMarca, isnull(m.marca, '') as marca, "
                + "     COALESCE(u.idUnidad, 0) AS idUnidad, COALESCE(u.unidad, '') AS UNIDAD, COALESCE(u.abreviatura, '') AS abreviatura, p.contenido,"
                + "     COALESCE(um.idUnidadMedida, 0) AS idUnidadMedida, COALESCE(um.unidadMedida, '') as unidadMedida, COALESCE(um.abreviatura, '') as medAbrev, 0 as idTipoUnidadMedida,"
                + "     i.idGrupo as idImpuestoGrupo, i.grupo as impuestoGrupo "
                + "FROM productos p "
                + "INNER JOIN productosPartes pa ON pa.idParte=p.idParte "
                + "INNER JOIN productosTipos t ON t.idTipo=p.idTipo "
                + "LEFT JOIN productosSubGrupos sg ON sg.idSubGrupo=p.idSubGrupo "
                + "LEFT JOIN productosGrupos g ON g.idGrupo=p.idGrupo "
                + "LEFT JOIN productosUnidades u ON u.idUnidad=p.idUnidadProducto "
                + "LEFT JOIN unidadesMedida um ON um.idUnidadMedida=p.idUnidadMedida "
                + "LEFT JOIN productosMarcas m on m.idMarca=p.idMarca "
                + "INNER JOIN impuestosGrupos i ON i.idGrupo=p.idImpuesto "
                + "WHERE p.idProducto="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                producto=construir(rs);
                DAOUpcs daoUpcs=new DAOUpcs();
                producto.setUpcs(daoUpcs.obtenerUpcs(idProducto));
            }
        } finally {
            cn.close();
        }
        return producto;
    }
    
    private Producto construir(ResultSet rs) throws SQLException {
        Producto prod=new Producto(rs.getInt("idProducto"));
        prod.setParte2(new Parte2(rs.getInt("idParte"), rs.getString("parte")));
        prod.setDescripcion(rs.getString("descripcion"));
        prod.setMarca(new Marca(rs.getInt("idMarca"), rs.getString("marca"), false));
        //prod.setCodigoBarras(rs.getString("codigoBarras"));
        prod.setTipo(new Tipo(rs.getInt("idTipo"), rs.getString("tipo")));
        prod.setGrupo(new Grupo(rs.getInt("idGrupo"), rs.getInt("codigoGrupo"), rs.getString("grupo")));
        prod.setSubGrupo(new SubGrupo(rs.getInt("idSubGrupo"), rs.getString("subGrupo")));
        prod.setUnidad(new Unidad(rs.getInt("idUnidad"), rs.getString("unidad"), rs.getString("abreviatura")));
        //Double d=rs.getDouble("contenido");
        //prod.setContenido(d.toString());
        prod.setContenido(rs.getDouble("contenido"));
        prod.setUnidadMedida(new UnidadMedida(rs.getInt("idUnidadMedida"), rs.getString("unidadMedida"), rs.getString("medAbrev"), rs.getInt("idTipo")));
        prod.setImpuesto(new ImpuestoGrupo(rs.getInt("idImpuestoGrupo"), rs.getString("impuestoGrupo")));
        return prod;
    }
    
    public ArrayList<Parte> completePartes(String strParte) throws SQLException {
        ArrayList<Parte> partes=new ArrayList<Parte>();
        String strSQL="SELECT idParte, parte FROM productosPartes WHERE parte like '%"+strParte+"%' ORDER BY parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                partes.add(new Parte(rs.getInt("idparte"), rs.getString("parte")));
            }
        } finally {
            cn.close();
        }
        return partes;
    }
    
    public Parte obtenerParte(int idParte) throws SQLException {
        Parte parte=null;
        String strSQL="SELECT idParte, parte FROM productosPartes WHERE idParte="+idParte;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                parte=new Parte(rs.getInt("idparte"), rs.getString("parte"));
            }
        } finally {
            cn.close();
        }
        return parte;
    }
    
    public ArrayList<Parte> obtenerPartes() throws SQLException {
        ArrayList<Parte> partes=new ArrayList<Parte>();
        String strSQL="SELECT idParte, parte FROM productosPartes ORDER BY parte";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                partes.add(new Parte(rs.getInt("idparte"), rs.getString("parte")));
            }
        } finally {
            cn.close();
        }
        return partes;
    }
}
