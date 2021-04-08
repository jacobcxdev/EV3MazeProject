package networking;

import org.json.JSONException;
import org.json.JSONObject;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

import lejos.hardware.BrickFinder;

public class ServerThread extends Thread {

    public ServerThread() {
    	setDaemon(true);
    }
    
    @Override
    public void run() {
    	Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
			@Override
			public void onConnect(SocketIOClient client) {
				JSONObject json = new JSONObject();
				try {
					json.put("battery_level", BrickFinder.getDefault().getPower().getVoltage());
				} catch (JSONException e) {
					System.err.printf("Error encoding JSON: %s%n", e.getLocalizedMessage());
				}
				client.sendEvent("ev3", json.toString());
			}
        });
        
        server.start();
    }

}
