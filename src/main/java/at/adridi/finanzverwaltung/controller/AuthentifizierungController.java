/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.controller;

import at.adridi.finanzverwaltung.db.DAO;
import at.adridi.finanzverwaltung.model.Benutzer;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Führt das Login durch. Authentifikation Es wird die Email-Addresse
 * (Benutzername) und das Passwort von der DB abgefragt.
 *
 * @author adridi
 */
@Named(value = "authentifizierungController")
@SessionScoped
/**
 * Controller für das Einloggen von Benutzern
 */
public class AuthentifizierungController implements Serializable {

    @Inject
    private BenutzerController benutzerCon;
    private String username;
    private String password = ""; //Durch Benutzer eingegebenes Passwort

    /**
     *
     */
    public AuthentifizierungController() {

    }

    /**
     * @PostConstruct public void init() { System.out.println("------
     * Initialsierung von BenutzerController: " + benutzerCon); }
     *
     */
    public String getUsername() {
        return username;
    }

    public BenutzerController getBenutzerCon() {
        return benutzerCon;
    }

    public void setBenutzerCon(BenutzerController benutzerCon) {
        this.benutzerCon = benutzerCon;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Hinweis das die Session abgelaufen ist.
     */
    public void onIdle() {
        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "SIE WURDEN ABGEMELDET!", "Aufgrund Inaktivität müssen Sie sich nocheinmal anmelden. Siehe Menüpunkt: Startseite"));
    }

    /**
     * Überprüfen ob eingegebener Benutzername und Passwort existieren mittels
     * DB-Abfrage. Erfolgs oder Fehlermeldungen werden über FacesMessages
     * geliefert.
     */
    public void login() {

        DAO dao = new DAO();

        List<Benutzer> benList = dao.getAllBenutzer();
        for (Benutzer ben : benList) {
            if (username != null
                    && username.equals(ben.getUsername())
                    && password != null
                    && password.equals(ben.getPassword())) {

                //Überprüfung ob  Passwort in der DB existieren.
                //Benutzer als eingeloggt setzten und Meldung ausgeben.
                Benutzer standardbenutzer = new Benutzer();
                standardbenutzer.setUsername(ben.getUsername());
                standardbenutzer.setPassword(ben.getPassword());
                benutzerCon.setBenutzer(standardbenutzer);
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getSessionMap().put("user", ben.getPassword());

                HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);

                //Session für Benutzer nach 3 Stunden Inaktivität beenden
                s.setMaxInactiveInterval(10800);

                return;
            }
        }
        //Benutzer existiert nicht in DB - Fehlermeldung ausgeben
        FacesMessage m = new FacesMessage("Das Einloggen ist fehlgeschlagen! Bitte Eingaben überprüfen!");
        m.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(null, m);
        username = password = "";

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.benutzerCon);
        hash = 97 * hash + Objects.hashCode(this.username);
        hash = 97 * hash + Objects.hashCode(this.password);
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
        final AuthentifizierungController other = (AuthentifizierungController) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.benutzerCon, other.benutzerCon)) {
            return false;
        }
        return true;
    }

}
