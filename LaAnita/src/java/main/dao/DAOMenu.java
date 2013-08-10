package main.dao;

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
import main.dominio.Menu;
import usuarios.UsuarioSesion;

/**
 *
 * @author JULIOS
 */
public class DAOMenu {
    private DataSource ds;
    private int idPerfil=0;
    
    public DAOMenu() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            if(usuarioSesion.getUsuario()==null) {
                idPerfil=0;
            } else {
                idPerfil=usuarioSesion.getUsuario().getIdPerfil();
            }
            
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
    public ArrayList<Menu> obtenermenu() throws SQLException {
        ArrayList<Menu> menuItems=new ArrayList<Menu>();
        String strSQL="select x.idMenu, mm.menu, x.idSubmenu, isnull(ms.submenu, '') as subMenu, " +
                                "x.idModulo, m.modulo, m.url\n" +
                        "from (select distinct m.idMenu, m.idSubMenu, m.idModulo \n" +
                        "		from usuarioPerfil p\n" +
                        "		inner join systemWeb.dbo.modulos m on m.idModulo=p.idModulo\n" +
                        "		where p.idPerfil="+idPerfil+") x\n" +
                        "inner join systemWeb.dbo.modulosMenus mm on mm.idMenu=x.idMenu\n" +
                        "left join systemWeb.dbo.modulosSubMenus ms on ms.idSubMenu=x.idSubMenu\n" +
                        "inner join systemWeb.dbo.modulos m on m.idModulo=x.idModulo";
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                menuItems.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return menuItems;
    }
    
    private Menu construir(ResultSet rs) throws SQLException {
        Menu menu=new Menu();
        menu.setIdMenu(rs.getInt("idMenu"));
        menu.setMenu(rs.getString("menu"));
        menu.setIdSubMenu(rs.getInt("idSubMenu"));
        menu.setSubMenu(rs.getString("subMenu"));
        menu.setIdModulo(rs.getInt("idModulo"));
        menu.setModulo(rs.getString("modulo"));
        menu.setUrl(rs.getString("url"));
        return menu;
        
    }
}
