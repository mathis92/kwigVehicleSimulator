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
    public Boolean operating;
    public List<RouteItem> routeList;

    public RoutesDetails(Integer startTime, String headingTo, List<RouteItem> routeList) {
        this.startTime = startTime;
        this.headingTo = headingTo;
        this.routeList = routeList;
        this.operating = false;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setOperating(Boolean Started) {
        this.operating = Started;
    }

    public Boolean getOperating() {
        return operating;
    }

    public List<RouteItem> getRouteList() {
        return routeList;
    }
    
    
    
}
