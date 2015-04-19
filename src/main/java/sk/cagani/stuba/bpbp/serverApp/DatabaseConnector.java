/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.JodaTimePermission;

import org.slf4j.LoggerFactory;
import stuba.bpbpdatabasemapper.GtfsRoutes;
import stuba.bpbpdatabasemapper.GtfsStopTimes;
import stuba.bpbpdatabasemapper.GtfsStops;
import stuba.bpbpdatabasemapper.GtfsTrips;

/**
 *
 * @author martinhudec
 */
public class DatabaseConnector {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private static SessionFactory sessionFactory;

    public DatabaseConnector() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addJar(new File("/home/debian/BPbp/target/lib/BPbpDatabaseMapper-1.0.jar"));
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }

    public static Session getSession() {

        Session session = sessionFactory.openSession();
        session.beginTransaction(); //open the transaction
        return session;
    }

    public void createRouteList(String serviceId) throws Exception {
        System.out.println("IDEM TESTUVAC");
        Session session = getSession();

        Long secondsSinceMidnight = Vehicle.getSecondsFromMidnight().longValue();
        System.out.println(secondsSinceMidnight.intValue() + " since midnight ");

        Date date1 = new Date();

        List<RoutesDetails> routesList = new ArrayList<>();
        List<GtfsRoutes> routeList = session.createCriteria(GtfsRoutes.class).list();
        for (GtfsRoutes route : routeList) {
            List<GtfsTrips> tripList = session.createCriteria(GtfsTrips.class).add(Restrictions.eq("gtfsRoutes", route)).add(Restrictions.eq("serviceIdId", serviceId)).addOrder(Order.asc("id")).list();
            for (GtfsTrips trip : tripList) {
                List<GtfsStopTimes> stopTimesList = session.createCriteria(GtfsStopTimes.class).add(Restrictions.eq("gtfsTrips", trip)).addOrder(Order.asc("arrivalTime")).list();
                List<TripStop> routeItemList = new ArrayList<>();
                for (GtfsStopTimes stopTime : stopTimesList) {
                    routeItemList.add(new TripStop(trip, route, stopTime, stopTime.getGtfsStops()));
                }
                routesList.add(new RoutesDetails(routeItemList.get(0).getStopTime().getArrivalTime(), trip.getTripHeadsign(), routeItemList));
            }
        }
        System.out.println((new Date().getTime() - date1.getTime()));
        Collections.sort(routesList, new CustomComparator());
        for (RoutesDetails rd : routesList) {
            System.out.println("rd -> " + rd.headingTo + " " + secsToHMS(rd.startTime) + " trip_id " + rd.getStopsList().get(0).getTrip().getId().getId());
        }
        new Thread(new VehicleScheduler(routesList)).start();

    }

    public class CustomComparator implements Comparator<RoutesDetails> {

        @Override
        public int compare(RoutesDetails o1, RoutesDetails o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    }

    public String secsToHMS(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
