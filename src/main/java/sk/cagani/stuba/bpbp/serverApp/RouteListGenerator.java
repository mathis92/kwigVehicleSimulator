/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import stuba.bpbpdatabasemapper.GtfsCalendarDates;
import stuba.bpbpdatabasemapper.GtfsCalendars;

/**
 *
 * @author martinhudec
 */
public class RouteListGenerator implements Runnable {

    private final DatabaseConnector dc;
    public static Boolean routeListDone = true;
    public static Boolean lastRouteDone = true;
private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public RouteListGenerator(DatabaseConnector dc) {
        this.dc = dc;
    }

    public void startGenerator() {

        new Thread(this).start();

    }

    @Override
    public void run() {
        while (true) {
          //  System.out.println("last route " + lastRouteDone + " route list done " + routeListDone);

            if (routeListDone && lastRouteDone) {
                System.out.println("GENERUJEM NOVU ROUTE LIST");
                routeListDone = false;
                lastRouteDone = false;
                DateTime currentDate = new org.joda.time.DateTime();
                Session session = DatabaseConnector.getSession();
                List<GtfsCalendarDates> calendarDatesList = session.createCriteria(GtfsCalendarDates.class).addOrder(Order.asc("date")).list();
                List<GtfsCalendars> calendarList = session.createCriteria(GtfsCalendars.class).list();
                String foundServiceId = null;
                for (GtfsCalendarDates date : calendarDatesList) {
                if (date.getDate().equals(sdf.format(new Date()))) {
                    foundServiceId = date.getServiceIdId();
                    break;
                }
            }
                session.getTransaction().commit();
                session.close();
                if (foundServiceId == null) {
                    if (currentDate.getDayOfWeek() == DateTimeConstants.MONDAY
                            || currentDate.getDayOfWeek() == DateTimeConstants.TUESDAY
                            || currentDate.getDayOfWeek() == DateTimeConstants.WEDNESDAY
                            || currentDate.getDayOfWeek() == DateTimeConstants.THURSDAY
                            || currentDate.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                        foundServiceId = "Prac.dny_0";
                    } else if (currentDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                        foundServiceId = "Soboty_1";
                    } else {
                        foundServiceId = "Neděle+Sv_2";
                    }
                }
                try {
                    System.out.println("spustam generovanie RouteListu na den : " + foundServiceId);
                    dc.createRouteList(foundServiceId);
                } catch (Exception ex) {
                    Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void CurrentRouteListDone() {
        routeListDone = true;
    }

    public static void LastRouteDone() {
        lastRouteDone = true;
    }
}
