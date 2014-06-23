/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesListas.converters;

import clientesListas.DAOClientesLista.DAOClientesLista;
import clientesListas.dominio.ClientesFormatos;
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
        ClientesFormatos formato = null;
        if (id == 0) {
            formato = new ClientesFormatos();
            formato.setIdFormato(0);
            formato.getClientesGrupo().setIdGrupoCte(0);
        } else {
            try {
                DAOClientesLista dao = new DAOClientesLista();
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
        ClientesFormatos formato = (ClientesFormatos) value;
        return Integer.toString(formato.getIdFormato());
    }

}
