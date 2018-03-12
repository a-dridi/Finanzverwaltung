/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.converter;

import at.adridi.finanzverwaltung.db.DAO;
import at.adridi.finanzverwaltung.model.Ausgabenkategorie;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author adridi
 */
@FacesConverter("susgabenKategorieConverter")
public class AusgabenKategorieConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (value == null || value.trim().isEmpty()) {
            return "";

        } else {

            DAO dao = new DAO();
            List<Ausgabenkategorie> liste = dao.getAllAusgabenkategorie();
            Ausgabenkategorie kategorieAusgewaehlt=null;

            for (Ausgabenkategorie a : liste) {
                if ((a.getKategoriebezeichnung()).equals(value)) {
                    kategorieAusgewaehlt = a;
                }
            }
            return kategorieAusgewaehlt;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return "";
        } else {
            
            return value.toString();
        }

    }

}
