package squirt.client;

// import
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
//import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;

public class SquirtChatClientApplication {
	/*
	 * This inner class is used to make sure we clean up when the client closes
	 */
	static private class CloseHook extends Thread {
		ActiveMQConnection connection;

		private CloseHook(ActiveMQConnection connection) {
			this.connection = connection;
		}

		public static Thread registerCloseHook(ActiveMQConnection connection) {
			Thread ret = new CloseHook(connection);
			Runtime.getRuntime().addShutdownHook(ret);
			return ret;
		}

		public void run() {
			try {
				System.out.println("Closing ActiveMQ connection");
				connection.close();
			} catch (JMSException e) {
				/*
				 * This means that the connection was already closed or got some
				 * error while closing. Given that we are closing the client we
				 * can safely ignore this.
				 */
			}
		}
	}

	/*
	 * This method wires the client class to the messaging platform Notice that
	 * ChatClient does not depend on ActiveMQ (the concrete communication
	 * platform we use) but just in the standard JMS interface.
	 */
	private static SquirtChatClient wireClient(String user) throws JMSException,
			URISyntaxException {
		ActiveMQConnection connection = ActiveMQConnection.makeConnection(
		/* Constants.USERNAME, Constants.PASSWORD, */
		Constants.ACTIVEMQ_URL);
		connection.start();
		CloseHook.registerCloseHook(connection);
		
		// for communication via queues (ie one-on-one communication)
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);

		//MessageConsumer consumer = session.createConsumer(destQueue); // may not need
		
		// for communication via topics (broadcasting and subset broadcasting ie chatrooms)
		TopicSession topicSession = connection.createTopicSession( false, 
				Session.AUTO_ACKNOWLEDGE);
		Topic topic = topicSession.createTopic("TESTNAME");
		TopicSubscriber subscriber = topicSession.createSubscriber(topic);
		TopicPublisher publisher = topicSession.createPublisher(topic);
		
		Queue destQueue = session.createQueue(user);
		MessageProducer producer = session.createProducer(destQueue);
		MessageConsumer consumer = session.createConsumer(destQueue);
		return new SquirtChatClient(producer, session, subscriber, publisher, connection, user,consumer,topicSession);
	}

	public static void main(String[] args) {
		try {
			
			/*
			 * Ask for username
			 */
			System.out.println("What is your username?");
			String user;
			Scanner scanIn = new Scanner(System.in);
			user = scanIn.nextLine();
	        System.out.println("ChatClient wired.");
	        
	        String mode = "";
			/* 
			 * We have some other function wire the ChatClient 
			 * to the communication platform
			 */
			
			SquirtChatClient client = wireClient(user);
	        System.out.println("ChatClient wired.");
	        boolean quit = false;
	        
	        System.out.println("Enter a message to send: ");
	        System.out.println("Select a mode to use:");
	        System.out.println("-gc to join a group chat");
	        System.out.println("-gm to send a group message");
	        System.out.println("-m to send a message to an individual");
	        System.out.println("-b to broadcast a message");
	        System.out.println("-r to reply to the last message");
	        System.out.println("-h or HALP to see this message again");
	        System.out.println("-q or QUIT  to quit");
	        while(quit == false)
	        {
		        
	        	String message;
	        	message = scanIn.nextLine();
	        	
	        	if (message.equals("quit"))
		        {
		        	quit = true;
		        	break;
		        }
	        	else if(message.equals("-gc"))
	        	{
	        		System.out.println("Would you like to create a chatroom or join an existing chatroom?");
	        		System.out.println("'Create ChatroomName' or 'Join ChatroomName'");
	        		message = scanIn.nextLine();
	        		String firstword = message.substring(0, message.indexOf(' '));
	        		String secondword = message.substring(message.indexOf(' ') + 1, message.length());
	        		System.out.println(firstword);
	        		System.out.println(secondword);
	        		if(firstword.equals("Create"))
	        		{
	        			client.setPublisher(secondword);
	        			client.setSubscriber(secondword);
	        			System.out.println("Who do you want to invite (Separate names with a semicolon, with no "
	        					+ "spaces)?");
	        			String receiver = scanIn.nextLine();
	        			
		        		int position = 0;
			        	while(receiver.length() != 0)
			        	{
			        		if(receiver.indexOf(';') == -1)
			        		{
			        			
			        			client.setProducer(receiver);
					        	client.sendUser("Join" + secondword);
					        	System.out.println(client.getName() + "Message Sent!");
					        	receiver = "";
			        		}
			        		else
			        		{
			        			
			        			String n = receiver.substring(0,receiver.indexOf(';'));
			        			
			        			position = receiver.indexOf(';') + 1;
			        			
			        			client.setProducer(n);
					        	client.sendUser("Join " + secondword);
					        	System.out.println(client.getName() + "Message Sent!");
					        	receiver = receiver.substring(position, receiver.length());
					        	
					        	
			        		}
				        	
				        	//message = scanIn.nextLine();
			        	}
	        		}
	        		else if(firstword.equals("Join"))
	        		{
	        			client.setPublisher(secondword);
	        			client.setSubscriber(secondword);
	        		}
	        		
	        		else
	        		{
	        			System.out.println("Dun fucking goofed");
	        			break;
	        		}
	        		message = scanIn.nextLine();
	        		client.send(message);
	        		
	        	}
	        	else if(message.equals("-m"))
	        	{
	        		System.out.println("What message would you like to send?");
	        		message = scanIn.nextLine();
		        	System.out.println("Who to send to?");
		        	String receiver;
		        	receiver = scanIn.nextLine();
		        	
		        	client.setProducer(receiver);
		        	client.sendUser(message);
		        	System.out.println(client.getName() + "Message Sent!");
		        	
	        	}
	        	
	        	else if(message.equals("-gm"))
	        	{
	        		System.out.println("What message would you like to send?");
	        		message = scanIn.nextLine();
	        		System.out.println("Who to send to?");
		        	String receiver;
		        	receiver = scanIn.nextLine();
		        	
		        	
	        		int position = 0;
		        	while(receiver.length() != 0)
		        	{
		        		if(receiver.indexOf(';') == -1)
		        		{
		        			
		        			client.setProducer(receiver);
				        	client.sendUser(message);
				        	System.out.println(client.getName() + "Message Sent!");
				        	receiver = "";
		        		}
		        		else
		        		{
		        			
		        			String n = receiver.substring(0,receiver.indexOf(';'));
		        			
		        			position = receiver.indexOf(';') + 1;
		        			
		        			client.setProducer(n);
				        	client.sendUser(message);
				        	System.out.println(client.getName() + "Message Sent!");
				        	receiver = receiver.substring(position, receiver.length());
				        	
				        	
		        		}
			        	
			        	//message = scanIn.nextLine();
		        	}
	        	}
	        	
	        	
	        	else if(message.equals("-b"))
	        	{
	        		message = scanIn.nextLine();
	        		client.send(message);
		        	System.out.println(client.getName() + "Message Sent!");		        	
		        	continue;
	        		
	        	}        	
	        }   
	        scanIn.close();
	        System.exit(0);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
