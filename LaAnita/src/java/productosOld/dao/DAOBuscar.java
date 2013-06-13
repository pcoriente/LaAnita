package productosOld.dao;

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
import productosOld.dominio.ProductoOld;
import usuarios.UsuarioSesion;

/**
 *
 * @author Julio
 */
public class DAOBuscar {
    private DataSource ds;
    
    public DAOBuscar() throws NamingException {
        try {
            //Context cI = new InitialContext();
            //ds = (DataSource) cI.lookup("java:comp/env/jdbc/__anita");
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
    
    public ProductoOld obtenerProducto(String cod_emp, String cod_pro) throws SQLException {
        ProductoOld productoOld=null;
        String strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen"
                + "     , isnull(po.idEmpaque, 0) as idEmpaque "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "left join productosOld po on po.cod_emp=p.cod_emp and po.cod_pro=p.cod_pro "
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
        
        String strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen"
                + "     , isnull(po.idEmpaque, 0) as idEmpaque "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "left join productosOld po on po.cod_emp=p.cod_emp and po.cod_pro=p.cod_pro "
                + "where p.descrip like '%"+cadena+"%' ";
        if(tipoBusqueda.equals("2")) {
            strSQL="select p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen"
                + "     , isnull(po.idEmpaque, 0) as idEmpaque "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "left join productosOld po on po.cod_emp=p.cod_emp and po.cod_pro=p.cod_pro "
                + "where p.cod_pro='"+cadena+"' ";
        }
        strSQL+="order by p.cod_pro, cod_emp";
        
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
        
        String strSQL="select top 10 p.cod_emp, p.cod_pro, p.descrip as descripcion, p.codbar, p.cod_inv, g.descrip as gpo_descrip, p.peso, p.volumen"
                + "     , isnull(po.idEmpaque, 0) as idEmpaque "
                + "from anita.dbo.producto p "
                + "inner join anita.dbo.gruprodu g on g.cod_inv=p.cod_inv "
                + "left join productosOld po on po.cod_emp=p.cod_emp and po.cod_pro=p.cod_pro "
                + "where p.cod_emp='"+cod_emp+"' and p.cod_pro not in (select cod_pro from anitaNuevo.dbo.empaques where idEmpresa="+cod_emp+") "
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
        producto.setIdEmpaque(rs.getInt("idEmpaque"));
        return producto;
    }
}
