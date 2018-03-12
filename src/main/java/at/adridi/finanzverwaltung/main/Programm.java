/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.main;

import at.adridi.finanzverwaltung.db.DAO;
import at.adridi.finanzverwaltung.model.Ausgabenausgabezeitraum;

/**
 *
 * @author adridi
 */
public class Programm {

    /**
     * Erstelllen und Speichern von Ausgabenkategorien und Testdatenin die DB
     *
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DAO dao = new DAO();

        //Ausgabenkategorien: Folgende Ausgabenzeitraeume müssen verwendet werden.
        Ausgabenausgabezeitraum kat = new Ausgabenausgabezeitraum();
        kat.setAusgabezeitraumbezeichnung("einmalig");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("14-taegig");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("monatlich");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("alle 2 Monate");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("vierteljaehrlich");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("alle 6 Monate");
        dao.insertAusgabenausgabezeitraum(kat);

        kat.setAusgabezeitraumbezeichnung("jaehrlich");
        dao.insertAusgabenausgabezeitraum(kat);

        //Hier kann man Testdaten hinzufügen:
    }

}
