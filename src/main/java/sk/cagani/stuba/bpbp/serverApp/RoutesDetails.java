/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.util.List;

/**
 *
 * @author martinhudec
 */

public class RoutesDetails {
    public Integer startTime; 
    public String headingTo; 
    public List<RouteItem> routeList;

    public RoutesDetails(Integer startTime, String headingTo, List<RouteItem> routeList) {
        this.startTime = startTime;
        this.headingTo = headingTo;
        this.routeList = routeList;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public List<RouteItem> getRouteList() {
        return routeList;
    }
    
    
    
}