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
	private ArrayList<String> chatroomList = new ArrayList<String>();
	
	public void receive(String msg) {
		/*
		System.out.println(msg);
		if( msg.indexOf(';') == -1) {
			onlineUsers.add(msg);
			System.out.println("adding user: " + msg);
		} */
		if( msg.indexOf(';') == -1 ) {
			System.out.println("ODD!");
			return;
		}
		
    	String firstword = msg.substring(0, msg.indexOf(';'));
    	String secondword = msg.substring(msg.indexOf(';') + 1, msg.length());		
    	
		if(firstword.equals("addUser"))
		{
			System.out.println("maybe add user");
			onlineUsers.add(secondword);
			for( String user: onlineUsers) {
				System.out.println("online user list: " + onlineUsers.toString());
				sendObjectMessage(user, onlineUsers);
			}
			System.out.println("added user: " + secondword);
		}
		
		else if(firstword.equals("addChat"))
		{
			chatroomList.add(secondword);
			System.out.println("added chatroom");
		}
		
		else if(firstword.equals("getChat"))
		{
			System.out.println("returning chatroom list");
			sendObjectMessage(secondword, chatroomList);
		}
		
		else if(firstword.equals("getList")){
			System.out.println("received a call to retrieve list");
	    	sendObjectMessage(secondword, onlineUsers);
		}
	}

	private void sendObjectMessage(String destination, ArrayList<String> array) {
		context.getBean(JmsTemplate.class).send(destination, 
				context.getBean(SquirtChatServerApplication.class).getObjectMessageCreator(array) );
	}
	

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;	
	}	
}
