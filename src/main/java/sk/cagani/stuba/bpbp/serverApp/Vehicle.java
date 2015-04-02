/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import stuba.bpbphibernatemapper.GtfsTrips;
import stuba.bpbphibernatemapper.GtfsTripsId;
import stuba.bpbphibernatemapper.TripPositions;

/**
 *
 * @author martinhudec
 */
public class Vehicle implements Runnable {

    public RoutesDetails routeDetail;

    public Vehicle(RoutesDetails routeDetail) {
        this.routeDetail = routeDetail;
    }

    @Override
    public void run() {
        boolean run = true;
        Float lat;
        Float lon;
        Float nextLat;
        Float nextLon;
        Float difflat = null;
        Float difflon = null;
        Integer delay = 0;

        while (run) {
            int i = 1;
            for (RouteItem r : routeDetail.getRouteList()) {
                System.out.println("iteration routeItem " + i);
                if (i != routeDetail.getRouteList().size()) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    // System.out.println(c.getTimeInMillis());
                    Long timeSinceMidnight = new Date().getTime() - (c.getTimeInMillis());
                    Long secondsSinceMidnight = timeSinceMidnight / 1000;
                    RouteItem rs = routeDetail.getRouteList().get(i);
                    Float seconds = null;
                    lat = ((float) Math.round(r.getStop().getLat().floatValue() * 100000) / 100000);
                    lon = ((float) Math.round(r.getStop().getLon().floatValue() * 100000) / 100000);
                    nextLat = ((float) Math.round(rs.getStop().getLat().floatValue() * 100000) / 100000);
                    nextLon = ((float) Math.round(rs.getStop().getLon().floatValue() * 100000) / 100000);
                    System.out.println("START lat " + lat + " -> " + " lon " + lon + " stop " + r.getStop().getName() + " next stop lat lon  " + nextLat + " " + nextLon);
                    seconds = rs.getStopTime().getArrivalTime().floatValue() - r.getStopTime().getArrivalTime().floatValue() + randInt(-10, 40);
                    //System.out.println(seconds + " seconds ");
                    difflat = nextLat - lat;
                    difflon = nextLon - lon;
                    //System.out.println("difflat " + difflat + " difflon " + difflon);
                    difflat = difflat / (seconds / 3);
                    difflon = difflon / (seconds / 3);
                    delay = secondsSinceMidnight.intValue() - r.getStopTime().getArrivalTime(); 
                    while (!(lat - 0.0002 <= nextLat && nextLat <= lat + 0.0002) || !(lon - 0.0002 <= nextLon && nextLon <= lon + 0.0002)) {

                        TripPositions tripPosition = new TripPositions(
                                new GtfsTrips(
                                        new GtfsTripsId("01", r.getTrip().getId().getId())), lat, lon, delay, 0, 1);
                        Session session = DatabaseConnector.getSession();
                        session.save(tripPosition);
                        session.getTransaction().commit();
                        session.close();

                        lat += difflat;
                        lon += difflon;
                        lat = ((float) Math.round(lat * 100000) / 100000);
                        lon = ((float) Math.round(lon * 100000) / 100000);

                        System.out.println("VOZIDLO c. "+ r.getRoute().getShortName() +" lat " + lat + " -> " + " lon " + lon + " last stop " + r.getStop().getName() + " next stop " + rs.getStop().getName() + " lat lon  " + nextLat + " " + nextLon + " HEADING TO " + r.trip.getTripHeadsign());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }else { 
                    run = false;
                }
                i++;
            }
        }
    }

    public static int randInt(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

    // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
