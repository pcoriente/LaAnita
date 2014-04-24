/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.converters;

import formatos.DAOFormatos.DAOFormatos;
import formatos.dominio.Formato;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.naming.NamingException;

/**
 *
 * @author Usuario
 */
public class FormatosConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int id = Integer.parseInt(value);
        Formato formato = null;
        if (id == 0) {
            formato = new Formato();
            formato.setIdFormato(0);
        } else {
            try {
                DAOFormatos dao = new DAOFormatos();
                formato = dao.dameFormato(id);
            } catch (NamingException ex) {
                Logger.getLogger(FormatosConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(FormatosConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return formato;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Formato formato = (Formato) value;
        return Integer.toBinaryString(formato.getIdFormato());
    }

}
