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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.hibernate.Session;
import stuba.bpbpdatabasemapper.GtfsTrips;
import stuba.bpbpdatabasemapper.GtfsTripsId;
import stuba.bpbpdatabasemapper.TripPositions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 *
 * @author martinhudec
 */
public class Vehicle implements Runnable {

    private final String USER_AGENT = "Mozilla/5.0";
    public RoutesDetails trip;

    public Vehicle(RoutesDetails routeDetail) {
        this.trip = routeDetail;
    }

    @Override
    public void run() {
        boolean run = true;
        Double lat = null;
        Double lon = null;
        Double nextLat;
        Double nextLon;
        Double difflat = null;
        Double difflon = null;
        Integer delay = 0;

        while (run) {
            int i = 1;
            for (TripStop r : trip.getStopsList()) {
               // System.out.println("iteration routeItem " + i);
                // System.out.println("[Current Stop] " + r.getStop().getName()); 
                if (i < trip.getStopsList().size()) {
                    
                    Long secondsSinceMidnight = getSecondsFromMidnight().longValue();
                   
                    TripStop rs = trip.getStopsList().get(i);
                    Double seconds = null;
                    lat = r.getStop().getLat();
                    lon = r.getStop().getLon();
                    nextLat = rs.getStop().getLat();
                    nextLon = rs.getStop().getLon();

                    //System.out.println("START lat " + lat + " -> " + " lon " + lon + " stop " + r.getStop().getName() + " next stop lat lon  " + nextLat + " " + nextLon);
                    seconds = rs.getStopTime().getArrivalTime().doubleValue() - r.getStopTime().getArrivalTime().doubleValue();
                    if (seconds == 0) {
                        seconds = (double) 30;
                    }
                    seconds += randInt(-10, 40);
                    //System.out.println(seconds + " seconds ");
                    difflat = nextLat - lat;
                    difflon = nextLon - lon;
                    //System.out.println("difflat " + difflat + " difflon " + difflon);
                    difflat = difflat / (seconds / 3);
                    difflon = difflon / (seconds / 3);
                    delay = secondsSinceMidnight.intValue() - r.getStopTime().getArrivalTime();
                    while (!(lat - 0.0002 <= nextLat && nextLat <= lat + 0.0002) || !(lon - 0.0002 <= nextLon && nextLon <= lon + 0.0002)) {

                        if (lat > 90 || lat < -90 || lon > 90 || lon < -90 || lat > 49.0 || lat < 46 || lon > 18 || lon < 15.0) {
                            lat = 0.0;
                            lon = 0.0;
                            i = trip.getStopsList().size();
                             System.out.println("[Vehicle STOP FAULT] " + r.getRoute().getShortName() + " -> " + r.getTrip().getTripHeadsign());
                            break;
                        } else {
                            try {
                                sendPost(r.getTrip().getId().getId(), lat, lon, delay, "a");
                            } catch (Exception ex) {
                                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            lat += difflat;
                            lon += difflon;

                            //System.out.println("VOZIDLO c. " + r.getRoute().getShortName() + " lat " + lat + " -> " + " lon " + lon + " last stop " + r.getStop().getName() + " next stop " + rs.getStop().getName() + " lat lon  " + nextLat + " " + nextLon + " HEADING TO " + r.trip.getTripHeadsign());
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                } else {
                    run = false;

                    try {
                        sendPost(r.getTrip().getId().getId(), lat, lon, delay, "n");
                        System.out.println("[Vehicle STOP] " + r.getRoute().getShortName() + " -> " + r.getTrip().getTripHeadsign());
                    } catch (Exception ex) {
                        Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    RouteListGenerator.LastRouteDone();
                    break;
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

    private void sendPost(String tripId, Double lat, Double lon, Integer delay, String state) throws Exception {

        String url = "http://bpbp.ctrgn.net/api/vehicle/updateLocation";
        //  System.out.println("sending POST");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("tripId", tripId));
        urlParameters.add(new BasicNameValuePair("lat", lat.toString()));
        urlParameters.add(new BasicNameValuePair("lon", lon.toString()));
        urlParameters.add(new BasicNameValuePair("delay", delay.toString()));
        urlParameters.add(new BasicNameValuePair("spd", "0"));
        urlParameters.add(new BasicNameValuePair("acc", "0"));
        urlParameters.add(new BasicNameValuePair("state", state));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
    }
        public static Integer getSecondsFromMidnight() {
        DateTime now = new DateTime();
        DateTime midnight = now.withTimeAtStartOfDay();       
        Duration duration = new Duration(midnight, now);
        int secs = duration.toStandardSeconds().getSeconds();
        return secs < 12600 ? secs + 86400 : secs;
    }
}
