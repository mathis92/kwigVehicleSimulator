/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import stuba.bpbphibernatemapper.GtfsRoutes;
import stuba.bpbphibernatemapper.GtfsStopTimes;
import stuba.bpbphibernatemapper.GtfsStops;
import stuba.bpbphibernatemapper.GtfsTrips;

/**
 *
 * @author martinhudec
 */

public class RouteItem {
    public GtfsTrips trip;
    public GtfsRoutes route; 
    public GtfsStopTimes stopTime;
    public GtfsStops stop;

    public RouteItem(GtfsTrips trip, GtfsRoutes route, GtfsStopTimes stopTime, GtfsStops stop) {
        this.trip = trip;
        this.route = route;
        this.stopTime = stopTime;
        this.stop = stop;
    }

    public GtfsTrips getTrip() {
        return trip;
    }

    public GtfsRoutes getRoute() {
        return route;
    }

    public GtfsStopTimes getStopTime() {
        return stopTime;
    }

    public GtfsStops getStop() {
        return stop;
    }
    
}
