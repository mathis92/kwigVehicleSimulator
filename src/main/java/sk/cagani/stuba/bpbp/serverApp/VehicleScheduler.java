/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martinhudec
 */
public class VehicleScheduler implements Runnable {

    List<RoutesDetails> routeList;
    Integer second = 74640;
    Boolean run = true;
    CopyOnWriteArrayList<RoutesDetails> concurrentRouteList;
    public VehicleScheduler(List<RoutesDetails> routeList) {
        this.routeList = routeList;
        concurrentRouteList = new CopyOnWriteArrayList<>(routeList);
    }

    @Override
    public void run() {

        while (run) {
            
// ZROB TU QUEUE 
            
            Long secondsSinceMidnight = Vehicle.getSecondsFromMidnight().longValue();
            second = secondsSinceMidnight.intValue();
            int index = 1;
            for (RoutesDetails rd : concurrentRouteList) {
                if (second <= rd.getStartTime() && rd.getStartTime() <= second+5 && rd.getOperating() == false) {
                    System.out.println("[Vehicle start] " + index + "/" + concurrentRouteList.size() + " " + rd.getStopsList().get(0).getRoute().getShortName() + " -> " + rd.headingTo);
                    new Thread(new Vehicle(rd)).start();
                    rd.setOperating(true);
                    if (index == concurrentRouteList.size()) {
                        run = false;
                        RouteListGenerator.CurrentRouteListDone();
                    }
                    break;
                }
                index++;
            }
           // second ++;
      //      System.out.println(secsToHMS(second));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(VehicleScheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String secsToHMS(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
