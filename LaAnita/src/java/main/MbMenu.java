package main;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.naming.NamingException;
import main.dao.DAOMenu;
import main.dominio.Menu;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbMenu")
@SessionScoped
public class MbMenu implements Serializable {
    ArrayList<Menu> menuItems;
    private MenuModel model; 
    private DAOMenu dao;

    public MbMenu() {
        this.construirMenu();
    }
    
    public void actualizarMenu() {
        this.construirMenu();
    }
    
    private void construirMenu() {
        this.obtenerMenu();
        model = new DefaultMenuModel();
        
        Submenu menu; int idMenu;
        Submenu submenu; int idSubmenu;
        MenuItem item;
        int i=0; int n=menuItems.size();
        while(i < n) {
            idMenu=menuItems.get(i).getIdMenu();
            
            menu = new Submenu();
            menu.setLabel(menuItems.get(i).getMenu());
            while(i < n && menuItems.get(i).getIdMenu()==idMenu) {
                idSubmenu=menuItems.get(i).getIdSubMenu();
                
                submenu = new Submenu();
                if(idMenu==0) {
                    submenu.setLabel("");
                } else {
                    submenu.setLabel(menuItems.get(i).getSubMenu());
                }
                while(i < n && menuItems.get(i).getIdMenu()==idMenu && menuItems.get(i).getIdSubMenu()==idSubmenu) {
                    item = new MenuItem();  
                    item.setValue(menuItems.get(i).getModulo());
                    item.setAjax(false);
                    item.setOutcome(menuItems.get(i).getUrl());
                    item.getAttributes().put("idModulo", menuItems.get(i).getIdModulo());
                    if(menuItems.get(i).getIdSubMenu()==0) {
                        menu.getChildren().add(item);
                    } else {
                        submenu.getChildren().add(item);
                    }
                    i++;
                }
                if(idSubmenu!=0) {
                    menu.getChildren().add(submenu);
                }
            }
            model.addSubmenu(menu);
        }
    }
    
    private void obtenerMenu() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMenu();
            menuItems=this.dao.obtenermenu();
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
    }

    public MenuModel getModel() {
        return model;
    }
}
