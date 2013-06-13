package productosOld.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import productosOld.dominio.ProductoOld;

/**
 *
 * @author JULIOS
 */
public class DAOProductoOld {
    private DataSource ds;
    
    public DAOProductoOld() throws NamingException {
        try {
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/jdbc/__anita");
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
    public ProductoOld obtenerProducto(String cod_emp, String cod_pro) throws SQLException {
        ProductoOld productoOld=null;
        String strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "where p.cod_emp='"+cod_emp+"' and p.cod_pro='"+cod_pro+"'";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                productoOld=construir(rs);
            }
        } finally {
            cn.close();
        }
        return productoOld;
    }
    
    public ArrayList<ProductoOld> obtenerProductos(String tipoBusqueda, String cadena) throws SQLException {
        ArrayList<ProductoOld> listaProductos=new ArrayList<ProductoOld>();
        
        String strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "where p.descrip like '%"+cadena+"%'";
        if(tipoBusqueda.equals("2")) {
            strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "where p.cod_pro='"+cadena+"'";
        }
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaProductos.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return listaProductos;
    }
    
    public ArrayList<ProductoOld> obtenerProductos(String cod_emp) throws SQLException {
        ArrayList<ProductoOld> listaProductos=new ArrayList<ProductoOld>();
        if(cod_emp.equals("0")) {
            return listaProductos;
        }
        
        String strSQL="select top 50 p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "where p.cod_emp='"+cod_emp+"' and p.cod_pro not in (select cod_pro from anitaNuevo.dbo.empaques where idEmpresa="+cod_emp+") and (ltrim(p.descrip)!='' and ltrim(rtrim(p.descrip))!='BAJA') "
                + "order by substring(p.cod_pro, 4, 4), substring(p.cod_pro, 1, 3)";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                listaProductos.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return listaProductos;
    }
    
    private ProductoOld construir(ResultSet rs) throws SQLException {
        ProductoOld producto=new ProductoOld();
        producto.setCod_emp(rs.getString("cod_emp"));
        producto.setCod_pro(rs.getString("cod_pro"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setCodbar(rs.getString("codBar"));
        producto.setGrupo("("+rs.getString("cod_inv")+") "+rs.getString("gpo_descrip"));
        producto.setPeso(rs.getDouble("peso"));
        producto.setVolumen(rs.getDouble("volumen"));
        return producto;
    }
}
