/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.controller;

import at.adridi.finanzverwaltung.db.DAO;
import at.adridi.finanzverwaltung.db.HibernateUtil;
import at.adridi.finanzverwaltung.main.AusgabenKategorienBetraege;
import at.adridi.finanzverwaltung.model.Ausgabenausgabezeitraum;
import at.adridi.finanzverwaltung.model.DatenbankNotizen;
import at.adridi.finanzverwaltung.model.Einnahmen;
import at.adridi.finanzverwaltung.model.EinnahmenKategorie;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.poi.util.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author adridi
 */
@Named(value = "einnahmenController")
@ViewScoped
public class EinnahmenController implements Serializable {

    private PieChartModel chartBarEinnahmenMonatlich;
    private PieChartModel chartBarEinnahmenJaehrlich;

    private EinnahmenKategorie einnahmenkategorie;
    private List<EinnahmenKategorie> einnahmenkategorieList = new ArrayList<>();

    private List<Ausgabenausgabezeitraum> ausgabenausgabezeitraumList = new ArrayList<>();
    private List<Einnahmen> einnahmenList = new ArrayList<>();
    private List<Einnahmen> filteredEinnahmenList = new ArrayList<>();
    private DatenbankNotizen dbnotizEintrag = null;
    private List<Einnahmen> deletedEinnahmenList = new ArrayList<>();

    private Integer rownumbers = 15;
    private Integer insert_rownumber;
    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String tabellenname = "Einnahmen";
    private String datensaetzeAnzahlText;

    //PLEASE CHANGE THESE Variables:
    private String baseUrl = "DAV_URL_NEXTCLOUD_INSTANZ"; // Without / (SLASH) at the end
    private String downloadUrl = "WEBDAV_URL_NEXTCLOUD_INSTANZ"; // Without / (SLASH) at the end
    private String cloudBenutzername = "BENUTZERNAME";
    private String cloudPasswort = "PASSWORT";
    
    private String anhangname;
    private String anhangtype;
    private DAO dao;
    private String notiztext;

    private String deleteID;
    private String anhangID;
    private byte[] anhang;
    private String neuEinnahmenkategorie;
    private String change_einnahmenkategorie;
    private EinnahmenKategorie deleteEinnahmenkategorie;

    private String bezeichnung;
    private Double betrag;
    private Ausgabenausgabezeitraum haeufigkeit;
    private Date eingangsdatum; //(Überschrift: Datum (Eingangsdatum)
    private String informationen;

    private Double einnahmenMonatSumme;
    private Double einnahmenJaehrlichSumme;
    private Double ausgabenMonatSumme;
    private Double ausgabenJaehrlichSumme;
    private Double gewinnMonatlich;
    private Double gewinnJaehrlich;

    private Double durchschnittlicheMonatlicheEinnahmen;
    private String einnahmenBestimmtenJahres;

    private List<AusgabenKategorienBetraege> einnahmenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> einnahmenKategorienBetragJaehrlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragJaehrlichListe = new ArrayList<>();

    private String jahrmonat;
    private Date selectMonatJahr;

    /**
     * Creates a new instance of EinnahmenController
     */
    public EinnahmenController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }
        this.einnahmenkategorieList = dao.getAllEinnahmenKategorie();

        flushAnhang();

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        if (urlName.contains("einnahmen_grafik.xhtml")) {
            createPortfolioWertinEuro();

        } else if (urlName.contains("einnahmen_kategorien.xhtml")) {
            createPortfolioWertinEuro();
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();
        } else { //Tabelle Einnahmen
            this.einnahmenList = dao.getAllEinnahmen();
            this.filteredEinnahmenList = new ArrayList<>(this.einnahmenList);
            this.ausgabenausgabezeitraumList = dao.getAllAusgabenausgabezeitraum();

            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();
            this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            this.jahrmonat = sdf.format(d);

        }

    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showAlteEinnahmen() {
        this.jahrmonat = "ALLE";

        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmenAlle());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showEinnahmenDiesesJahres() {
        Date d = new Date();
        SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");
        this.jahrmonat = sdfJ.format(d);

        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmen());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
    }

    /**
     * Auswahl von Monat und Jahr durch Benutzer über Kalender-Picker
     */
    public void changeMonatJahrAnsicht() {

        if (this.selectMonatJahr != null) {

            SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");
            this.jahrmonat = sdfJ.format(this.selectMonatJahr);
            calculateEinnahmenCustomJahrSumme(Integer.parseInt(this.jahrmonat));
            this.einnahmenList.clear();
            this.filteredEinnahmenList.clear();
            this.einnahmenList.addAll(dao.getAllEinnahmenCustom(null, Integer.parseInt(sdfJ.format(this.selectMonatJahr))));
            this.filteredEinnahmenList.addAll(this.einnahmenList);

            this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

            RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
        }
    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showRegelmaessigeEinnahmen() {
        this.jahrmonat = "Nur Regelmaessige";
        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmenRegelmaessig());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
    }

    /**
     * Barchart für monatl. und jaerhliche Einnahmen
     */
    public void createPortfolioWertinEuro() {
        //Monatliche Einnahmen
        this.chartBarEinnahmenMonatlich = new PieChartModel();
        this.chartBarEinnahmenMonatlich.setTitle("monatl. Einnahmen regelmaessig - Verteilung in EURO");
        this.chartBarEinnahmenMonatlich.setFill(true);
        this.chartBarEinnahmenMonatlich.setLegendPosition("e");
        this.chartBarEinnahmenMonatlich.setShowDataLabels(true);
        this.chartBarEinnahmenMonatlich.setDiameter(450);
        this.chartBarEinnahmenMonatlich.setShowDatatip(true);

        //Jaehrliche Einnahmen
        this.chartBarEinnahmenJaehrlich = new PieChartModel();
        this.chartBarEinnahmenJaehrlich.setTitle("jaehrliche Einnahmen regelmaessig - Verteilung in EURO");
        this.chartBarEinnahmenJaehrlich.setFill(true);
        this.chartBarEinnahmenJaehrlich.setLegendPosition("e");
        this.chartBarEinnahmenJaehrlich.setShowDataLabels(true);
        this.chartBarEinnahmenJaehrlich.setDiameter(450);
        this.chartBarEinnahmenJaehrlich.setShowDatatip(true);

        //SQL Abruf
        // Liefert alle Einträge (Einnahmen) für eine Kategorie sortiert
        // aufsteigend nach dem Datum
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<EinnahmenKategorie> kategorienliste = this.einnahmenkategorieList;
        Double einnahmenSumme;

        abbrechen:
        for (EinnahmenKategorie w : kategorienliste) {
            einnahmenSumme = 0.0;
            try {
                String sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where deleted=false and haeufigkeit= 'monatlich' and kategorie = :kategoriename group by kategorie order by kategorie asc";

                Query qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                List<Object[]> kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1]);
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='woechentlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                            einnahmenSumme += ((Double) o[1] * 4);
                        }
                    }
                }
                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='14-taegig' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1] * 2);
                        }
                    }
                }
                AusgabenKategorienBetraege akb = new AusgabenKategorienBetraege();
                if (einnahmenSumme > 0.0) {
                    this.chartBarEinnahmenMonatlich.set(w.getBezeichnung(), einnahmenSumme);
                    akb.setBetrag(einnahmenSumme);
                    akb.setBezeichnung(w.getBezeichnung());
                    this.einnahmenKategorienBetragMonatlichListe.add(akb);
                }
                //jaehrliche Berechnung:
                einnahmenSumme = (einnahmenSumme * 12);
                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='alle 2 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1] * 6);
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='vierteljaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1] * 4);
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='alle 6 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1] * 2);
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='jaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += ((Double) o[1]);
                        }
                    }
                }
                if (einnahmenSumme > 0.0) {
                    this.chartBarEinnahmenJaehrlich.set(w.getBezeichnung(), einnahmenSumme);
                    akb = new AusgabenKategorienBetraege();
                    akb.setBetrag(einnahmenSumme);
                    akb.setBezeichnung(w.getBezeichnung());
                    this.einnahmenKategorienBetragJaehrlichListe.add(akb);
                }

            } catch (Exception e) {
                System.out.println("Fehler in createPortfolioWertinEuro: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von Einnahmenkategorie:", "" + e));
                break abbrechen;
            }
        }
        this.filteredEinnahmenKategorienBetragJaehrlichListe = new ArrayList<>(this.einnahmenKategorienBetragJaehrlichListe);
        this.filteredEinnahmenKategorienBetragMonatlichListe = new ArrayList<>(this.einnahmenKategorienBetragMonatlichListe);

    }

    public void speichernNotiz() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.dbnotizEintrag = notizList.get(0);
        }

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.dbnotizEintrag != null) {
            //Notiz-Eintrag aktualisieren
            this.dbnotizEintrag.setTabelle(this.tabellenname);
            this.dbnotizEintrag.setDatum(new Date());
            this.dbnotizEintrag.setNotiztext(notiztext);
            this.dao.updateDatenbankNotizen(this.dbnotizEintrag);
        } else {
            //Neuen Notiz-Eintrag erstellen
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext(notiztext);
            this.dao.insertDatenbankNotizen(n);
        }
    }

    public void clearNotizen() {

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.notiztext != null) {
            //Notiz-Eintrag als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.updateDatenbankNotizen(n);
        } else {
            //Neuen Notiz-Eintrag erstellen und als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.insertDatenbankNotizen(n);
        }
    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Einnahmen> liste = new ArrayList<>(this.einnahmenList);
            gefunden:
            for (Einnahmen a : liste) {
                if (a.getEinnahmen_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(cloudBenutzername, cloudPasswort);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getEinnahmen_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateEinnahmen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getEinnahmen_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateEinnahmen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getEinnahmen_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
                    }
                    id_existiert = true;
                    flushAnhang();
                    this.anhangID = "";
                    break gefunden;
                }
            }
            if (!id_existiert) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ID existiert nicht ", "Bitte geben Sie eine existierende ID eines Datensatzes ein"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Keine ID übergeben ", "" + e));

        }
    }

    /**
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            Einnahmen a = (this.dao.getSingleEinnahmen((Integer) tabelle.getRowKey())).get(0);

            if (spaltenname.equals("Bezeichnung")) {
                a.setBezeichnung((String) event.getNewValue());
            }

            if (spaltenname.equals("Kategorie")) {

                String auswahl = (String) event.getNewValue();
                gefunden:
                for (EinnahmenKategorie m : this.einnahmenkategorieList) {
                    if (m.getBezeichnung().equals(auswahl)) {
                        a.setKategorie((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }
            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Haeufigkeit")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (Ausgabenausgabezeitraum m : this.ausgabenausgabezeitraumList) {
                    if (m.getAusgabezeitraumbezeichnung().equals(auswahl)) {
                        a.setHaeufigkeit((String) event.getNewValue());
                        break gefunden;
                    }
                }

            }

            if (spaltenname.equals("Eingangsdatum")) {
                if (event.getNewValue() != null) {
                    a.setEingangsdatum((Date) event.getNewValue());
                }
            }

            if (spaltenname.equals("Informationen")) {
                a.setInformationen((String) event.getNewValue());
            }

            dao.updateEinnahmen(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        this.einnahmenList = dao.getAllEinnahmen();
        this.filteredEinnahmenList = new ArrayList<>(this.einnahmenList);
        this.einnahmenkategorieList = dao.getAllEinnahmenKategorie();
        this.ausgabenausgabezeitraumList = dao.getAllAusgabenausgabezeitraum();
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(cloudBenutzername, cloudPasswort);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        calculateEinnahmenMonatSumme();
        calculateEinnahmenJahrSumme();
        calculateDifferenzAusgabenEinnahmen();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

    }

    public void change_rownumber() {
        this.rownumbers = this.insert_rownumber;
        flushAnhang();
    }

    public void anhangSpeichern(FileUploadEvent event) {
        try {
            InputStream input = event.getFile().getInputstream();
            if (input != null) {

                this.anhang = IOUtils.toByteArray(input);
                this.anhangname = event.getFile().getFileName();
                this.anhangtype = event.getFile().getContentType();

            } else {
                this.anhang = null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + e));
        }

    }

    public void speichern() {
        Einnahmen ausgabe = new Einnahmen();
        ausgabe.setDeleted(false);

        if (this.bezeichnung != null) {
            ausgabe.setBezeichnung(bezeichnung);
        }

        ausgabe.setBetrag(betrag);

        if (this.einnahmenkategorie != null) {
            ausgabe.setKategorie(this.einnahmenkategorie.getBezeichnung());
        }

        if (this.haeufigkeit != null) {
            ausgabe.setHaeufigkeit(this.haeufigkeit.getAusgabezeitraumbezeichnung());
        }

        if (this.eingangsdatum != null) {
            ausgabe.setEingangsdatum(eingangsdatum);
        } else {
            ausgabe.setEingangsdatum(new Date());
        }

        if (this.informationen != null) {
            ausgabe.setInformationen(informationen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            ausgabe.setAnhang(true);
            this.einnahmenList.add(ausgabe);
            this.filteredEinnahmenList.add(ausgabe);
            dao.insertEinnahmen(ausgabe);

            List<Einnahmen> ausgabenListe = new ArrayList<>(this.einnahmenList);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getEinnahmen_id();
                try {
                    Einnahmen a = ausgabenListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(cloudBenutzername, cloudPasswort);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrl + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrl + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);
                    dao.updateEinnahmen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateData();
            }

        } else {
            ausgabe.setAnhang(false);
            this.einnahmenList.add(ausgabe);
            this.filteredEinnahmenList.add(ausgabe);
            dao.insertEinnahmen(ausgabe);
            updateData();
        }
    }

    /**
     * Methode nach dem Speichern
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

    }

    public void kategorieSpeichern() {

        if (!this.neuEinnahmenkategorie.isEmpty()) {
            EinnahmenKategorie ak = new EinnahmenKategorie();
            ak.setBezeichnung(this.neuEinnahmenkategorie);
            dao.insertEinnahmenKategorie(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void kategorieLoeschen() {

        if (this.deleteEinnahmenkategorie != null) {

            List<EinnahmenKategorie> akList = dao.getAllEinnahmenKategorie();
            List<Einnahmen> ausgabenList = dao.getAllEinnahmen();
            boolean kategorieExist = false;

            for (EinnahmenKategorie a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteEinnahmenkategorie.getBezeichnung().toLowerCase())) {
                    dao.deleteEinnahmenKategorie(a);
                    for (Einnahmen ausgabe : ausgabenList) {
                        if ((ausgabe.getKategorie().toLowerCase()).equals(this.deleteEinnahmenkategorie.getBezeichnung().toLowerCase())) {
                            this.einnahmenList.remove(ausgabe);
                            this.filteredEinnahmenList.remove(ausgabe);

                            ausgabe.setKategorie(this.change_einnahmenkategorie);
                            dao.updateEinnahmen(ausgabe);
                            this.einnahmenList.add(ausgabe);
                            this.filteredEinnahmenList.add(ausgabe);

                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_einnahmenkategorie.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                EinnahmenKategorie neu = new EinnahmenKategorie();
                neu.setBezeichnung(this.change_einnahmenkategorie);
                dao.insertEinnahmenKategorie(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Einnahmen a : this.einnahmenList) {
                    if (a.getEinnahmen_id().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteEinnahmen(a);
                        this.deletedEinnahmenList.add(a);
                        this.einnahmenList.remove(a);
                        this.filteredEinnahmenList.remove(a);

                        break gefunden;
                    }
                }
                updateData();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedEinnahmenList.isEmpty()) {
            for (Einnahmen a : this.deletedEinnahmenList) {
                this.einnahmenList.add(a);
                this.filteredEinnahmenList.add(a);
                a.setDeleted(false);
                dao.updateEinnahmen(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void calculateAusgabenJahrSumme() {
        this.ausgabenJaehrlichSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(year FROM zahlungsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 2);
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Berechnung Summe: ", "" + e));

        }

    }

    public void calculateAusgabenMonatSumme() {
        this.ausgabenMonatSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=false";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += waehrunggruppe.get(0);
        }

        //woechentliche Einnahmen
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += (waehrunggruppe.get(0) * 4);
        }

        //14-tägige Einnahmen
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += (waehrunggruppe.get(0) * 2);
        }
        s.close();

    }

    public void calculateEinnahmenMonatSumme() {
        this.einnahmenMonatSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += waehrunggruppe.get(0);

        }
        //woechentliche Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += (waehrunggruppe.get(0) * 4);
        }

        //14-tägige Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += (waehrunggruppe.get(0) * 2);
        }

        s.close();

    }

    public void calculateEinnahmenJahrSumme() {
        this.einnahmenJaehrlichSumme = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='einmalig' and deleted=False and EXTRACT(year FROM eingangsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 2);
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Einnahmen-Berechnung Summe: ", "" + e));

        }

    }

    public void calculateEinnahmenCustomJahrSumme(int jahr) {
        Double einnahmenBestimmtesJahrSumme = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='einmalig' and deleted=False and EXTRACT(year FROM eingangsdatum)  = :jahrWert";
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", jahr);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 2);
            }

            this.einnahmenBestimmtenJahres = "Einnahmen dieses Jahres: " + einnahmenBestimmtesJahrSumme + "€";
            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Einnahmen-Berechnung Summe: ", "" + e));

        }

    }

    /**
     * Rechnet den monatlichen Gewinn/Ertrag - !!! calculateAusgaben.. und
     * calculateEinnahmen... müssen zuvor schon im init() aufgerufen worden sein
     */
    public void calculateDifferenzAusgabenEinnahmen() {
        this.gewinnMonatlich = (this.einnahmenMonatSumme - this.ausgabenMonatSumme);
        this.gewinnJaehrlich = (this.einnahmenJaehrlichSumme - this.ausgabenJaehrlichSumme);
        this.durchschnittlicheMonatlicheEinnahmen = (this.einnahmenJaehrlichSumme / 12);
    }

    //BIETE DIESE IMMER ÜBERPRÜFEN:
    public void scrollTop() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("ueberschriftPanel");
    }

    public void scrollTabelle() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("tabellenAnsicht:listenForm");

    }

    public void scrollHinzufuegen() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("hinzufuegenForm:neuenDatensatzFormular");

    }

    public void scrollKategorieAdd() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("kategorieForm:kategorieAddPanel");

    }

    public void scrollKategorieDel() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("del_kategorieForm:kategorieDelPanel");

    }

    public void scrollAnhangEdit() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.scrollTo("neuAnhangForm:AnhangEditPanel");

    }

    public PieChartModel getChartBarEinnahmenJaehrlich() {
        return chartBarEinnahmenJaehrlich;
    }

    public void setChartBarEinnahmenJaehrlich(PieChartModel chartBarEinnahmenJaehrlich) {
        this.chartBarEinnahmenJaehrlich = chartBarEinnahmenJaehrlich;
    }

    public List<AusgabenKategorienBetraege> getEinnahmenKategorienBetragMonatlichListe() {
        return einnahmenKategorienBetragMonatlichListe;
    }

    public void setEinnahmenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> einnahmenKategorienBetragMonatlichListe) {
        this.einnahmenKategorienBetragMonatlichListe = einnahmenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredEinnahmenKategorienBetragMonatlichListe() {
        return filteredEinnahmenKategorienBetragMonatlichListe;
    }

    public void setFilteredEinnahmenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragMonatlichListe) {
        this.filteredEinnahmenKategorienBetragMonatlichListe = filteredEinnahmenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getEinnahmenKategorienBetragJaehrlichListe() {
        return einnahmenKategorienBetragJaehrlichListe;
    }

    public void setEinnahmenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> einnahmenKategorienBetragJaehrlichListe) {
        this.einnahmenKategorienBetragJaehrlichListe = einnahmenKategorienBetragJaehrlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredEinnahmenKategorienBetragJaehrlichListe() {
        return filteredEinnahmenKategorienBetragJaehrlichListe;
    }

    public void setFilteredEinnahmenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragJaehrlichListe) {
        this.filteredEinnahmenKategorienBetragJaehrlichListe = filteredEinnahmenKategorienBetragJaehrlichListe;
    }

    public EinnahmenKategorie getEinnahmenkategorie() {
        return einnahmenkategorie;
    }

    public void setEinnahmenkategorie(EinnahmenKategorie einnahmenkategorie) {
        this.einnahmenkategorie = einnahmenkategorie;
    }

    public List<EinnahmenKategorie> getEinnahmenkategorieList() {
        return einnahmenkategorieList;
    }

    public void setEinnahmenkategorieList(List<EinnahmenKategorie> einnahmenkategorieList) {
        this.einnahmenkategorieList = einnahmenkategorieList;
    }

    public List<Einnahmen> getEinnahmenList() {
        return einnahmenList;
    }

    public void setEinnahmenList(List<Einnahmen> einnahmenList) {
        this.einnahmenList = einnahmenList;
    }

    public List<Einnahmen> getFilteredEinnahmenList() {
        return filteredEinnahmenList;
    }

    public void setFilteredEinnahmenList(List<Einnahmen> filteredEinnahmenList) {
        this.filteredEinnahmenList = filteredEinnahmenList;
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
    }

    public Integer getInsert_rownumber() {
        return insert_rownumber;
    }

    public void setInsert_rownumber(Integer insert_rownumber) {
        this.insert_rownumber = insert_rownumber;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAnhangname() {
        return anhangname;
    }

    public void setAnhangname(String anhangname) {
        this.anhangname = anhangname;
    }

    public String getAnhangtype() {
        return anhangtype;
    }

    public void setAnhangtype(String anhangtype) {
        this.anhangtype = anhangtype;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
    }

    public String getNeuEinnahmenkategorie() {
        return neuEinnahmenkategorie;
    }

    public void setNeuEinnahmenkategorie(String neuEinnahmenkategorie) {
        this.neuEinnahmenkategorie = neuEinnahmenkategorie;
    }

    public String getChange_einnahmenkategorie() {
        return change_einnahmenkategorie;
    }

    public void setChange_einnahmenkategorie(String change_einnahmenkategorie) {
        this.change_einnahmenkategorie = change_einnahmenkategorie;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public List<Ausgabenausgabezeitraum> getAusgabenausgabezeitraumList() {
        return ausgabenausgabezeitraumList;
    }

    public void setAusgabenausgabezeitraumList(List<Ausgabenausgabezeitraum> ausgabenausgabezeitraumList) {
        this.ausgabenausgabezeitraumList = ausgabenausgabezeitraumList;
    }

    public EinnahmenKategorie getDeleteEinnahmenkategorie() {
        return deleteEinnahmenkategorie;
    }

    public void setDeleteEinnahmenkategorie(EinnahmenKategorie deleteEinnahmenkategorie) {
        this.deleteEinnahmenkategorie = deleteEinnahmenkategorie;
    }

    public Ausgabenausgabezeitraum getHaeufigkeit() {
        return haeufigkeit;
    }

    public void setHaeufigkeit(Ausgabenausgabezeitraum haeufigkeit) {
        this.haeufigkeit = haeufigkeit;
    }

    public Date getEingangsdatum() {
        return eingangsdatum;
    }

    public void setEingangsdatum(Date eingangsdatum) {
        this.eingangsdatum = eingangsdatum;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public Double getEinnahmenMonatSumme() {
        return einnahmenMonatSumme;
    }

    public void setEinnahmenMonatSumme(Double einnahmenMonatSumme) {
        this.einnahmenMonatSumme = einnahmenMonatSumme;
    }

    public Double getEinnahmenJaehrlichSumme() {
        return einnahmenJaehrlichSumme;
    }

    public void setEinnahmenJaehrlichSumme(Double einnahmenJaehrlichSumme) {
        this.einnahmenJaehrlichSumme = einnahmenJaehrlichSumme;
    }

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public PieChartModel getChartBarEinnahmenMonatlich() {
        return chartBarEinnahmenMonatlich;
    }

    public void setChartBarEinnahmenMonatlich(PieChartModel chartBarEinnahmenMonatlich) {
        this.chartBarEinnahmenMonatlich = chartBarEinnahmenMonatlich;
    }

    public Double getAusgabenMonatSumme() {
        return ausgabenMonatSumme;
    }

    public void setAusgabenMonatSumme(Double ausgabenMonatSumme) {
        this.ausgabenMonatSumme = ausgabenMonatSumme;
    }

    public Double getAusgabenJaehrlichSumme() {
        return ausgabenJaehrlichSumme;
    }

    public void setAusgabenJaehrlichSumme(Double ausgabenJaehrlichSumme) {
        this.ausgabenJaehrlichSumme = ausgabenJaehrlichSumme;
    }

    public Double getGewinnMonatlich() {
        return gewinnMonatlich;
    }

    public void setGewinnMonatlich(Double gewinnMonatlich) {
        this.gewinnMonatlich = gewinnMonatlich;
    }

    public Double getGewinnJaehrlich() {
        return gewinnJaehrlich;
    }

    public void setGewinnJaehrlich(Double gewinnJaehrlich) {
        this.gewinnJaehrlich = gewinnJaehrlich;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    public Double getDurchschnittlicheMonatlicheEinnahmen() {
        return durchschnittlicheMonatlicheEinnahmen;
    }

    public void setDurchschnittlicheMonatlicheEinnahmen(Double durchschnittlicheMonatlicheEinnahmen) {
        this.durchschnittlicheMonatlicheEinnahmen = durchschnittlicheMonatlicheEinnahmen;
    }

    public String getEinnahmenBestimmtenJahres() {
        return einnahmenBestimmtenJahres;
    }

    public void setEinnahmenBestimmtenJahres(String einnahmenBestimmtenJahres) {
        this.einnahmenBestimmtenJahres = einnahmenBestimmtenJahres;
    }

    public String getJahrmonat() {
        return jahrmonat;
    }

    public void setJahrmonat(String jahrmonat) {
        this.jahrmonat = jahrmonat;
    }

    public Date getSelectMonatJahr() {
        return selectMonatJahr;
    }

    public void setSelectMonatJahr(Date selectMonatJahr) {
        this.selectMonatJahr = selectMonatJahr;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.einnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.einnahmenkategorieList);
        hash = 29 * hash + Objects.hashCode(this.ausgabenausgabezeitraumList);
        hash = 29 * hash + Objects.hashCode(this.einnahmenList);
        hash = 29 * hash + Objects.hashCode(this.filteredEinnahmenList);
        hash = 29 * hash + Objects.hashCode(this.rownumbers);
        hash = 29 * hash + Objects.hashCode(this.insert_rownumber);
        hash = 29 * hash + Objects.hashCode(this.baseUrl);
        hash = 29 * hash + Objects.hashCode(this.downloadUrl);
        hash = 29 * hash + Objects.hashCode(this.anhangname);
        hash = 29 * hash + Objects.hashCode(this.anhangtype);
        hash = 29 * hash + Objects.hashCode(this.dao);
        hash = 29 * hash + Objects.hashCode(this.deleteID);
        hash = 29 * hash + Objects.hashCode(this.anhangID);
        hash = 29 * hash + Arrays.hashCode(this.anhang);
        hash = 29 * hash + Objects.hashCode(this.neuEinnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.change_einnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.deleteEinnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.bezeichnung);
        hash = 29 * hash + Objects.hashCode(this.betrag);
        hash = 29 * hash + Objects.hashCode(this.haeufigkeit);
        hash = 29 * hash + Objects.hashCode(this.eingangsdatum);
        hash = 29 * hash + Objects.hashCode(this.informationen);
        hash = 29 * hash + Objects.hashCode(this.einnahmenMonatSumme);
        hash = 29 * hash + Objects.hashCode(this.einnahmenJaehrlichSumme);
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
        final EinnahmenController other = (EinnahmenController) obj;
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.neuEinnahmenkategorie, other.neuEinnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.change_einnahmenkategorie, other.change_einnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenkategorie, other.einnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenkategorieList, other.einnahmenkategorieList)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenausgabezeitraumList, other.ausgabenausgabezeitraumList)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenList, other.einnahmenList)) {
            return false;
        }
        if (!Objects.equals(this.filteredEinnahmenList, other.filteredEinnahmenList)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.deleteEinnahmenkategorie, other.deleteEinnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.haeufigkeit, other.haeufigkeit)) {
            return false;
        }
        if (!Objects.equals(this.eingangsdatum, other.eingangsdatum)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenMonatSumme, other.einnahmenMonatSumme)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenJaehrlichSumme, other.einnahmenJaehrlichSumme)) {
            return false;
        }
        return true;
    }

}
