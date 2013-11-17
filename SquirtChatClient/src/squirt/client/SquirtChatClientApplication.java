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
		return new SquirtChatClient(producer, session, subscriber, publisher, connection, user,consumer);
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
			/* 
			 * We have some other function wire the ChatClient 
			 * to the communication platform
			 */
			
			SquirtChatClient client = wireClient(user);
	        System.out.println("ChatClient wired.");
	        boolean quit = false;
	        while(quit == false)
	        {
		        System.out.println("Enter a message to send: ");
	        	String message;
	        	message = scanIn.nextLine();
	        	
	        	if (message.equals("quit"))
		        {
		        	quit = true;
		        	break;
		        }
	        	System.out.println("Who to send to?");
	        	String receiver;
	        	receiver = scanIn.nextLine();
	        	
		        
	
		        	//scanIn.close();
		        	
		        	/* 
		        	 * Now we can happily send messages around
		        	 */
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
