package squirt.server;

import java.util.ArrayList;
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
		
		if( msg.indexOf(';') == -1)
			onlineUsers.add(msg);
		else {
			
			String firstword = msg.substring(0, msg.indexOf(';'));
			
			if( firstword.equals("getList") ) {
		    	String user = msg.substring(msg.indexOf(';') + 1, msg.length());
		  
		    	
		    	for(String userInList: onlineUsers) {
		    		context.getBean(JmsTemplate.class).send(user, 
		    				context.getBean(SquirtChatServerApplication.class).getMessageCreator(userInList) );	
		    		System.out.println("");
		    	}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;	
	}
	
	//@Autowired
	
}
