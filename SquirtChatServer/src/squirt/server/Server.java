package squirt.server;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server implements ApplicationContextAware {
	
	private ApplicationContext context;
	private ArrayList<String> onlineUsers = new ArrayList<String>();
	
	public void receive(String msg) {
		System.out.println(msg);
		if( msg.indexOf(';') == -1) {
			onlineUsers.add(msg);
			System.out.println("adding user");
		}
		else {
			System.out.println("received a call to retrieve list");
			String firstword = msg.substring(0, msg.indexOf(';'));
			if( firstword.equals("getList") ) {
		    	String destination = msg.substring(msg.indexOf(';') + 1, msg.length());
		    	System.out.println("destination: " +destination);
		    	
		    	// send object list also
		    	sendObjectMessage(destination);
			}
		}
	}

	private void sendObjectMessage(String destination) {
		context.getBean(JmsTemplate.class).send(destination, 
				context.getBean(SquirtChatServerApplication.class).getObjectMessageCreator(onlineUsers) );
		
	}
	

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;	
	}	
}
