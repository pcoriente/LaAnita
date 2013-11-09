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
    private ArrayList<Menu> menuItems;
//    private ArrayList<Accion> acciones;
    private MenuModel model; 
    private DAOMenu dao;

    public MbMenu() {
        this.construirMenu();
    }
    
    public void actualizarMenu() {
        this.construirMenu();
    }
    
    private void construirMenu() {
        //FacesContext facesCtx = FacesContext.getCurrentInstance();
        //ELContext elCtx = facesCtx.getELContext();
        //ExpressionFactory expFact = facesCtx.getApplication().getExpressionFactory();

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
//                    item.addActionListener(createMethodActionListener("#{mbMenu.changePageAction}", Void.TYPE, new Class[]{ActionEvent.class}));
//                    int idModulo=this.menuItems.get(i).getIdModulo();
//                    item.getAttributes().put("idModulo", String.valueOf(idModulo));
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
    
//    public void changePageAction(ActionEvent event) {
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        MenuItem selectedMenuItem = (MenuItem) event.getComponent();
//        String idtoidentify = selectedMenuItem.getAttributes().get("idModulo").toString() ;
//        int idModulo = Integer.parseInt(idtoidentify);
//        try {
//            this.dao = new DAOMenu();
//            this.acciones=this.dao.obtenerAcciones(idModulo);
//        } catch (NamingException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getMessage());
//        } catch (SQLException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//        }
//    }
//    
//    public void obtenerAcciones(ActionEvent event) {
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        MenuItem selectedMenuItem = (MenuItem) event.getComponent();
//        String strIdModulo = selectedMenuItem.getAttributes().get("idModulo").toString() ;
//        try {
//            this.dao = new DAOMenu();
//            this.acciones=this.dao.obtenerAcciones(Integer.parseInt(strIdModulo));
//        } catch (NamingException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getMessage());
//        } catch (SQLException ex) {
//            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
//        }
//    }
//    
//    private MethodExpression createMethodExpression(String valueExpression, Class<?> valueType, Class<?>[] expectedParamTypes) {
//
//        MethodExpression methodExpression = null;
//        try {
//            ExpressionFactory factory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
//            methodExpression = factory.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), valueExpression, valueType, expectedParamTypes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return methodExpression;
//    }
//
//    private MethodExpressionActionListener createMethodActionListener(String valueExpression, Class<?> valueType, Class<?>[] expectedParamTypes) {
//
//        MethodExpressionActionListener actionListener = null;
//        try {
//            actionListener = new MethodExpressionActionListener(createMethodExpression(valueExpression, valueType, expectedParamTypes));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return actionListener;
//    }
    
    private void obtenerMenu() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao=new DAOMenu();
            menuItems=this.dao.obtenermenu();
            this.dao.cargarUsuarioConfig();
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

//    public ArrayList<Accion> getAcciones() {
//        return acciones;
//    }
//
//    public void setAcciones(ArrayList<Accion> acciones) {
//        this.acciones = acciones;
//    }
}
