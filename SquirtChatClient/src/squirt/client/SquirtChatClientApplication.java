package squirt.client;

// import
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.jms.JMSException;
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
	private static SquirtChatClient wireClient() throws JMSException,
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
		
		Queue destQueue = session.createQueue(Constants.QUEUENAME);
		MessageProducer producer = session.createProducer(destQueue);
		return new SquirtChatClient(producer, session, subscriber, publisher, connection);
	}

	public static void main(String[] args) {
		try {
			
			/* 
			 * We have some other function wire the ChatClient 
			 * to the communication platform
			 */
			SquirtChatClient client = wireClient();
	        System.out.println("ChatClient wired.");
	        System.out.println("Enter a message to send: ");
        	String message;
        	Scanner scanIn = new Scanner(System.in);
        	message = scanIn.nextLine();
	        while(!(message.equals("quit"))){

	        	//scanIn.close();
	        	
	        	/* 
	        	 * Now we can happily send messages around
	        	 */
	        	client.send(message);
	        	//client.send("Hello World!");
	        	System.out.println("Message Sent!");
	        	message = scanIn.nextLine();
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
