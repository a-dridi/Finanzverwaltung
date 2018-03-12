/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.finanzverwaltung.db;

import at.adridi.finanzverwaltung.model.Ausgaben;
import at.adridi.finanzverwaltung.model.Ausgabenausgabezeitraum;
import at.adridi.finanzverwaltung.model.Ausgabenkategorie;
import at.adridi.finanzverwaltung.model.Benutzer;
import at.adridi.finanzverwaltung.model.DatenbankNotizen;
import at.adridi.finanzverwaltung.model.Einnahmen;
import at.adridi.finanzverwaltung.model.EinnahmenKategorie;
import at.adridi.finanzverwaltung.model.Vermoegen;
import at.adridi.finanzverwaltung.model.VermoegenJaehrlich;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 *
 * @author adridi
 */
public class DAO implements AutoCloseable {

    public DAO() {

    }


    public boolean insertEinnahmen(Einnahmen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertEinnahmenKategorie(EinnahmenKategorie v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }


    public boolean insertDatenbankNotizen(DatenbankNotizen v) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(v);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Notiz wurde gespeichert.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertKryptowaehrungenVorgang: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Notiz SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }



    public boolean deleteDatenbankNotizen(DatenbankNotizen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Die Notiz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteDatenbankNotiz: " + ex);
            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Notiz SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }
        return ret;
    }

    public boolean deleteEinnahmen(Einnahmen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Einnahmen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteEinnahmenKategorie(EinnahmenKategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * Löschen eines Benutzers in der DB
     *
     * @param b das zu löschende Objekt vom Typ Benutzer
     * @return true, wenn erfolgreicher Löschvorgang
     */
    public boolean deleteBenutzer(Benutzer b) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean ret = false;
        try {
            tx = s.beginTransaction();
            s.delete(b);
            tx.commit();
            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteBenutzer: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertAusgaben(Ausgaben a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(a);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgaben(Ausgaben b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Ausgaben bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertAusgabenkategorie(Ausgabenkategorie o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Ausgabenkategorie wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgabenkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgabenkategorie(Ausgabenkategorie b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenkategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }


    public boolean insertVermoegen(Vermoegen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertVermoegenJaehrlich(VermoegenJaehrlich o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Neues jährliches Vermögen wurde hinzugefügt.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteVermoegen(Vermoegen b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            Vermoegen bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteVermoegenJaehrlich(VermoegenJaehrlich b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            VermoegenJaehrlich bGewaehlt = b;
            bGewaehlt.setDeleted(true);
            s.update(bGewaehlt);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean insertAusgabenausgabezeitraum(Ausgabenausgabezeitraum o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(o);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Eintrag für die Kategorie Ausgabezeitraum wurde erfolgreich gespeichert", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in insertAusgabenausgabezeitraum: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean deleteAusgabenausgabezeitraum(Ausgabenausgabezeitraum b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.delete(b);

            tx.commit();
            ret = true;
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Kategorie wurde gelöscht.", ""));

        } catch (HibernateException ex) {
            System.out.println("Fehler in deleteAusgabenausgabezeitraum: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateEinnahmen(Einnahmen a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateEinnahmenKategorie(EinnahmenKategorie a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateEinnahmenKategorie: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateAusgaben(Ausgaben a) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(a);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateAusgaben: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }


    public boolean updateDatenbankNotizen(DatenbankNotizen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateDatenbankNotizen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateVermoegen(Vermoegen o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateVermoegen: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public boolean updateVermoegenJaehrlich(VermoegenJaehrlich o) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(o);

            tx.commit();
            ret = true;

        } catch (HibernateException ex) {
            System.out.println("Fehler in updateVermoegenJaehrlich: " + ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL-Fehler! ", ex.toString()));

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    public List<Vermoegen> getAllVermoegen() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Vermoegen where deleted=false order by vermoegen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Vermoegen> getSingleVermoegen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Vermoegen where deleted=false and vermoegen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleVermoegen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<VermoegenJaehrlich> getAllVermoegenJaehrlich() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM VermoegenJaehrlich where deleted=false order by vermoegenjaehrlich_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllVermoegenJaehrlich: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<VermoegenJaehrlich> getSingleVermoegenJaehrlich(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM VermoegenJaehrlich where deleted=false and vermoegenjaehrlich_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleVermoegenJaehrlich: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgabenkategorie> getAllAusgabenkategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgabenkategorie order by ausgabenkategorieid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenkategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgabenausgabezeitraum> getAllAusgabenausgabezeitraum() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgabenausgabezeitraum order by zeitraumid asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenausgabezeitraum: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<DatenbankNotizen> getDatenbankNotiz(String tabelle) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM DatenbankNotizen where tabelle = :tabellenname");
            qu.setString("tabellenname", tabelle);
            return qu.list();

        } catch (Exception e) {
            //System.out.println("Fehler in getAllKryptowaehrungenNotiz: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Regelmäßige Einnahmen und einmalige Einnahmen aus diesem Jahr
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmen() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        List<Einnahmen> einnahmenListe;

        try {
            //regelmäßige Ausgaben:
            Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
            einnahmenListe = qu.list();

            qu = s.createQuery("FROM Einnahmen where deleted=false and (EXTRACT(year FROM eingangsdatum) = :jahrWert and haeufigkeit='einmalig') order by einnahmen_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            einnahmenListe.addAll(qu.list());
            return einnahmenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Alle in der DB Einnahmen gespeicherten Datensätz
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmenAlle() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false order by einnahmen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Einnahmen> getSingleEinnahmen(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false and einnahmen_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleEinnahmen: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Nur regelmäßige Einnahmen abrufen
     *
     * @return
     */
    public List<Einnahmen> getAllEinnahmenRegelmaessig() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenRegelmaessig: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Wenn Monat 0 oder null ist dann werden die Einnahmen für das ganze Jahr
     * geliefert. MONAT IST IN STRING, WEGEN DER FÜHRENDEN NULL
     *
     * @param monat
     * @param jahr
     * @return
     */
    public List<Einnahmen> getAllEinnahmenCustom(String monat, Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Einnahmen> einnahmenListe;

        try {

            if (jahr != null && jahr != 0) {
                if (monat != null && (!monat.isEmpty())) {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
                    einnahmenListe = qu.list();

                    //Monat und Jahr übergeben - Ausgaben für einen bestimmten Monat
                    Date d = new Date();
                    qu = s.createQuery("FROM Einnahmen where deleted=false and (EXTRACT(year FROM eingangsdatum)  = :jahrWert and EXTRACT(month FROM eingangsdatum)  = :monatWert and haeufigkeit='einmalig') order by einnahmen_id asc");
                    qu.setInteger("jahrWert", jahr);

                    if (Integer.parseInt(monat) < 1) {
                        qu.setInteger("monatWert", 12);
                    } else if (Integer.parseInt(monat) > 12) {
                        qu.setInteger("monatWert", 1);
                    } else {
                        qu.setInteger("monatWert", Integer.parseInt(monat));
                    }
                    qu.setInteger("monatWert", Integer.parseInt(monat));
                    einnahmenListe.addAll(qu.list());
                    return einnahmenListe;
                } else {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Einnahmen where deleted=false and haeufigkeit!='einmalig' order by einnahmen_id asc");
                    einnahmenListe = qu.list();

                    //Jahr übergeben - Ausgaben für ein bestimmtes Jahr
                    Date d = new Date();
                    qu = s.createQuery("FROM Einnahmen where deleted=false and (EXTRACT(year FROM eingangsdatum)  = :jahrWert and haeufigkeit='einmalig') order by einnahmen_id asc");
                    qu.setInteger("jahrWert", jahr);

                    einnahmenListe.addAll(qu.list());
                    return einnahmenListe;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Bitte ein Jahr auswählen!!", "Um Einnahmen für ein Jahr anzuzeigen bitte nur Jahr auswählen."));
                return null;
            }
        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenCustom: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<EinnahmenKategorie> getAllEinnahmenKategorie() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM EinnahmenKategorie order by id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllEinnahmenKategorie: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Liefert eine Liste aller Benutzer als List
     *
     *
     */
    public List<Benutzer> getAllBenutzer() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Benutzer");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllBenutzer: " + e);

            return null;
        } finally {
            s.close();
        }

    }

    /**
     * Regelmäßige Ausgaben und einmalige Ausgaben aus diesem Jahr
     *
     * @return
     */
    public List<Ausgaben> getAllAusgaben() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        List<Ausgaben> ausgabenListe;

        try {
            //regelmäßige Ausgaben:
            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
            ausgabenListe = qu.list();

            qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum) = :jahrWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            ausgabenListe.addAll(qu.list());
            return ausgabenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Nur regelmäßige Einnahmen abrufen
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenRegelmaessig() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenRegelmaessig: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    public List<Ausgaben> getSingleAusgaben(Integer id) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgaben_id=:idWert");
            qu.setInteger("idWert", id);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getSingleAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Liefert alle ausgaben, auch alte einmalige Ausgaben von vorherigen Jahren
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenAlle() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createQuery("FROM Ausgaben where deleted=false order by ausgaben_id asc");
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgaben: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Liefer Ausgaben des aktuellen Monats (inkl. allen regelmäßigen Ausgaben)
     *
     * @return
     */
    public List<Ausgaben> getAllAusgabenMonat() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Ausgaben> ausgabenListe;

        try {
            //regelmäßige Ausgaben:
            Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
            ausgabenListe = qu.list();

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
            qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and EXTRACT(month FROM zahlungsdatum)  = :monatWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
            qu.setInteger("jahrWert", Integer.parseInt(sdf.format(d)));
            qu.setInteger("monatWert", Integer.parseInt(sdf2.format(d)));

            ausgabenListe.addAll(qu.list());
            return ausgabenListe;

        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenMonat: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Wenn Monat 0 oder null ist dann werden die Ausgaben für das ganze Jahr
     * geliefert. MONAT IST IN STRING, WEGEN DER FÜHRENDEN NULL
     *
     * @param monat
     * @param jahr
     * @return
     */
    public List<Ausgaben> getAllAusgabenCustom(String monat, Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Ausgaben> ausgabenListe;

        try {

            if (jahr != null && jahr != 0) {
                if (monat != null && (!monat.isEmpty())) {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
                    ausgabenListe = qu.list();

                    //Monat und Jahr übergeben - Ausgaben für einen bestimmten Monat
                    Date d = new Date();
                    qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and EXTRACT(month FROM zahlungsdatum)  = :monatWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
                    qu.setInteger("jahrWert", jahr);

                    if (Integer.parseInt(monat) < 1) {
                        qu.setInteger("monatWert", 12);
                    } else if (Integer.parseInt(monat) > 12) {
                        qu.setInteger("monatWert", 1);
                    } else {
                        qu.setInteger("monatWert", Integer.parseInt(monat));
                    }
                    qu.setInteger("monatWert", Integer.parseInt(monat));
                    ausgabenListe.addAll(qu.list());
                    return ausgabenListe;
                } else {
                    //regelmäßige Ausgaben:
                    Query qu = s.createQuery("FROM Ausgaben where deleted=false and ausgabezeitraum!='einmalig' order by ausgaben_id asc");
                    ausgabenListe = qu.list();

                    //Jahr übergeben - Ausgaben für ein bestimmtes Jahr
                    Date d = new Date();
                    qu = s.createQuery("FROM Ausgaben where deleted=false and (EXTRACT(year FROM zahlungsdatum)  = :jahrWert and ausgabezeitraum='einmalig') order by ausgaben_id asc");
                    qu.setInteger("jahrWert", jahr);

                    ausgabenListe.addAll(qu.list());
                    return ausgabenListe;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Bitte ein Jahr auswählen!!", "Um Ausgaben für ein Jahr anzuzeigen bitte nur Jahr auswählen."));
                return null;
            }
        } catch (Exception e) {
            System.out.println("Fehler in getAllAusgabenCustom: " + e);

            return null;
        } finally {
            s.close();
        }
    }

    //Benutzerdefinierte SQL.Anfragen:
    public List<String> customGetAll(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            SQLQuery qu = s.createSQLQuery(sqlbefehl);

            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Benutzerdefinierte (Custom SQL-Query) für Abfragen von Listen/Datenreihen
     *
     * @param sqlbefehl
     * @return
     */
    public List<Ausgaben> customGetAllAusgaben(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);
            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage("fehlerAnsichtListen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Benutzerdefinierte (Custom SQL-Query) für Abfragen, die Werte liefern
     *
     * @param sqlbefehl
     * @return
     */
    public List<String> customGetValue(String sqlbefehl) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Query qu = s.createSQLQuery(sqlbefehl);

            return qu.list();

        } catch (Exception e) {
            System.out.println("Fehler im SQL-Befehl:  " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler im SQL-Befehl! ", e.toString()));
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Speichern eines Benutzers in die DB
     *
     * @param b zum Speichern fertiges Benutzer-Objekt
     * @return true, wenn erfolgreiche Speicherung
     */
    public boolean insertBenutzer(Benutzer b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.save(b);

            tx.commit();
            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in insertBenutzer: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    /**
     * Benutzers in der DB aktualisieren Benutzer loeschen und neuanlegen
     *
     * @param b zum Speichern fertiges Benutzer-Objekt
     * @return true, wenn erfolgreiche Speicherung
     */
    public boolean updateBenutzer(Benutzer b) {

        Session s = HibernateUtil.getSessionFactory().openSession();

        DAO dao = new DAO();
        Transaction tx = null;
        boolean ret = false;
        try {

            tx = s.beginTransaction();
            s.update(b);
            tx.commit();

            ret = true;
        } catch (HibernateException ex) {
            System.out.println("Fehler in updateBenutzer: " + ex);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            s.close();
        }

        return ret;
    }

    @Override
    public void close() {
        HibernateUtil.close();
    }

}
