/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.cagani.stuba.bpbp.serverApp;

import java.io.DataOutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import static org.apache.http.HttpHeaders.USER_AGENT;
import stuba.bpbphibernatemapper.GtfsTrips;
import stuba.bpbphibernatemapper.GtfsTripsId;

/**
 *
 * @author martinhudec
 */
public class SendPost {
    
    private void sendPost(GtfsTripsId tripId, Long lat, Long lon) throws Exception {
 
		String url = "http://bpbp.ctrgn.net/api/vehicle/updateLocation";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "gtfs_trip_id="+tripId+"&lat="+lat+"&lon="+lon+"&spd=0&acc=1";
                
		// Send post request
		con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }
	}
    
}
