/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.adridi.finanzverwaltung.listener;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Sessionphasen Listener
 * Session überprüfen
 * @author adridi
 */
public class SessionPhaseListener implements PhaseListener {

   

 
  /**
   * Wenn die Session ausgelaufen ist (nach 10 min.) wird man auto. auf
   * die Loginseite (Startseite) umgeleitet. Zudem wird darüber eine Meldung 
   * ausgegeben (in einer Growl)
   * @param event 
   */
    @Override
    public void beforePhase(PhaseEvent event) {
        
        FacesContext context = event.getFacesContext();
        ExternalContext ext = context.getExternalContext();
        
        HttpServletRequest rq = (HttpServletRequest) ext.getRequest();
        String ref = rq.getHeader("referer"); //Link von der zuvor aufgerufenen Seite holen
        
        //Session holen und überprüfen ob sie abgelaufen ist.
        HttpSession session = (HttpSession) ext.getSession(false);
       
        boolean sessionAbgelaufen = (session == null) || (session.isNew());
        if (sessionAbgelaufen && ref != null) {
            //Session abgelaufen
            Application app = context.getApplication();
            ViewHandler viewHandler = app.getViewHandler();
           
            //Loginseite erstellen und diese rendern
            UIViewRoot view = viewHandler.createView(context, "/index.xhtml");
            FacesMessage m = new FacesMessage("Ihre Session ist abgelaufen! Bitte nochmal einloggen. Siehe Menüpunkt \"Startseite\" ");
            m.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, m);
            context.setViewRoot(view);
            context.renderResponse();       
            System.out.println("*** Ihre Session ist abgelaufen! Bitte nochmal einloggen. ***");
        } 
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        //To change body of generated methods, choose Tools | Templates.
    }

}
