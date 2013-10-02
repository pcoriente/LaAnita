package proveedores.dao;

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
import productos.dominio.Marca;
import proveedores.dominio.Proveedor;
import proveedores.dominio.ProveedorProducto;
import unidadesMedida.UnidadMedida;
import usuarios.UsuarioSesion;

/**
 *
 * @author jsolis
 */
public class DAOProveedoresProductos {
    private DataSource ds;
    
    public DAOProveedoresProductos() throws NamingException {
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
    
    public ArrayList<ProveedorProducto> obtenerProductos(int idProveedor) throws SQLException {
        ArrayList<ProveedorProducto> lista=new ArrayList<ProveedorProducto>();
        
        Connection cn=ds.getConnection();
        String strSQL=this.sqlProducto()+" WHERE p.idProveedor="+idProveedor+" ORDER BY p.sku";
        try {
            Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(strSQL);
            while(rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    public ProveedorProducto obtenerProducto(int idProveedor, int idProducto) throws SQLException {
        ProveedorProducto p=null;
        
        Connection cn=ds.getConnection();
        String strSQL=this.sqlProducto()+" WHERE p.idProveedor="+idProveedor+" AND idProducto="+idProducto;
        try {
            Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(strSQL);
            if(rs.next()) {
                p=construir(rs);
            }
        } finally {
            cn.close();
        }
        return p;
    }
    
    private ProveedorProducto construir(ResultSet rs) throws SQLException {
        ProveedorProducto pp=new ProveedorProducto();
        pp.setIdProducto(rs.getInt("idProducto"));
        pp.setSku(rs.getString("sku"));
        pp.setMarca(new Marca(rs.getInt("idMarca"), rs.getString("marca"), false));
        pp.setProducto(rs.getString("producto"));
        pp.setContenido(rs.getDouble("contenido"));
        pp.setUnidadMedida(new UnidadMedida(rs.getInt("idUnidadMedida"), rs.getString("unidadMedida"), rs.getString("medAbrev"), rs.getInt("idTipo")));
        pp.setImpuestoGrupo(new ImpuestoGrupo(rs.getInt("idImpuestoGrupo"), rs.getString("impuestoGrupo")));
        return pp;
    }
    
    private String sqlProducto() {
        return "select p.idProducto, p.sku, p.producto, p.piezas, p.capacidad\n" +
                "	,isnull(m.idMarca, 0) as idMarca, isnull(m.marca, '') as marca\n" +
                "	,u.idUnidad, u.unidad, u.abreviatura\n" +
                "	,um1.idUnidadMedida as idUnidadMedida1, um1.unidadmedida as unidadMedida1\n" +
                "	,isnull(um2.idUnidadMedida, 0) as idUnidadMedida2, isnull(um2.unidadMedida, '') as unidadMedida2\n" +
                "from proveedoresProductos p\n" +
                "left join productosMarcas m on m.idMarca=p.idMarca\n" +
                "inner join empaquesUnidades u on u.idUnidad=p.idUnidadEmpaque\n" +
                "inner join unidadesMedida um1 on um1.idUnidadMedida=p.idUnidadMedida\n" +
                "left join unidadesMedida um2 on um2.idUnidadMedida=p.idUnidadMedida2\n";
    }
}
