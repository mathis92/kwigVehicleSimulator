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
    Integer second = 44820;
    Boolean run = true;
    CopyOnWriteArrayList<RoutesDetails> concurrentRouteList;
    public VehicleScheduler(List<RoutesDetails> routeList) {
        this.routeList = routeList;
        concurrentRouteList = new CopyOnWriteArrayList<>(routeList);
    }

    @Override
    public void run() {

        while (run) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            // System.out.println(c.getTimeInMillis());
            Long timeSinceMidnight = new Date().getTime() - (c.getTimeInMillis());
            Long secondsSinceMidnight = timeSinceMidnight / 1000;
         //   second = secondsSinceMidnight.intValue();
            int index = 1;
            for (RoutesDetails rd : concurrentRouteList) {
                if (second <= rd.getStartTime() && rd.getStartTime() <= second+5 && rd.getOperating() == false) {
                    System.out.println("index " + index + " size route listu " + concurrentRouteList.size());
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
            second ++;
            System.out.println(secsToHMS(second));
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
