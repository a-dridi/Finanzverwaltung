/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.controller;

import at.adridi.finanzverwaltung.db.DAO;
import at.adridi.finanzverwaltung.model.Benutzer;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Objects;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Führt Registrierungen durch
 *
 * @author adridi
 */
@Named(value = "registrationController")
@RequestScoped
public class RegistrationController implements Serializable {

    private String benutzername;
    private String passwort1;
    private String passwort2;

    /**
     * Alle in der DB verfügbaren Veranstaltungstypen initialisieren
     */
    public RegistrationController() {
        DAO dao = new DAO();

    }

    /**
     * Überprüft ob das Passwort 2mal eingegeben wurde und führt Registration
     * durch
     *
     * Überprüfung der Email-Addresse passiert in eigenen Validator
     *
     * @return String zur Login Seite
     */
    public String registrieren() {

        if (!this.passwort1.equals(this.passwort2)) { //Passwoerter nicht gleich
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwoerter sind unterschiedlich!!", "Bitte 2 Mal das gleiche Passwort eingeben."));

            return "registration.xhtml";
        } else {
            //Benutzer in die DB speichern
            DAO dao = new DAO();
            Benutzer ben1 = new Benutzer();
            ben1.setUsername(this.benutzername);
            ben1.setPassword(this.passwort1);

            if (dao.insertBenutzer(ben1)) //Erfolgreiches Speichern
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registrierung war erfolgreich!", "Sie können sich nun einloggen"));
                System.out.println("*Neue Registration! - User: " + this.benutzername);
                return "index.xhtml";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Speichern!", "Bitte ihre Eingabe ueberprüfen"));
                System.out.println(" *** Fehler bei Registration! - ");
                return "registration.xhtml";
            }
        }
    }

    public String getPasswort1() {
        return passwort1;
    }

    public void setPasswort1(String passwort1) {
        this.passwort1 = passwort1;
    }

    public String getPasswort2() {
        return passwort2;
    }

    public void setPasswort2(String passwort2) {
        this.passwort2 = passwort2;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.benutzername);
        hash = 47 * hash + Objects.hashCode(this.passwort1);
        hash = 47 * hash + Objects.hashCode(this.passwort2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegistrationController other = (RegistrationController) obj;
        if (!Objects.equals(this.benutzername, other.benutzername)) {
            return false;
        }
        if (!Objects.equals(this.passwort1, other.passwort1)) {
            return false;
        }
        if (!Objects.equals(this.passwort2, other.passwort2)) {
            return false;
        }
        return true;
    }

 

  
}
