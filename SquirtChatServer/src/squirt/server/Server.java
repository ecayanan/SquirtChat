package squirt.server;

import java.util.ArrayList;

import javax.jms.MessageProducer;
import javax.jms.Queue;

public class Server {
	
	private ArrayList<String> onlineUsers = new ArrayList<String>();
	
	public void receive(String msg) {
		
		if( msg.indexOf(';') == -1)
			onlineUsers.add(msg);
		else {
			String firstword = msg.substring(0, msg.indexOf(';'));
			if( firstword.equals("getList") ) {
		    	String user = msg.substring(msg.indexOf(';') + 1, msg.length());
		    	// send the list to the user TODO
			}
		}
	}
}
