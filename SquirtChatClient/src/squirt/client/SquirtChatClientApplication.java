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
		ActiveMQConnection connection = ActiveMQConnection.makeConnection( Constants.ACTIVEMQ_URL);
		connection.start();
		CloseHook.registerCloseHook(connection);
		
		return new SquirtChatClient( user, connection );
	}

	private void quit() {
		// close hoook 
		// set some boolean
	}
	
	private static void groupChat( String message, Scanner scanIn, SquirtChatClient client ) throws JMSException {
		//if(message.equals("-gc"))
    	//{
    	System.out.println("Would you like to create a chatroom or join an existing chatroom?");
    	System.out.println("'Create ChatroomName' or 'Join ChatroomName'");
    	message = scanIn.nextLine();
    	String firstword = message.substring(0, message.indexOf(' '));
    	String secondword = message.substring(message.indexOf(' ') + 1, message.length());
    	System.out.println(firstword);
    	System.out.println(secondword);
    	
    	if(firstword.equals("Create"))
    	{
    		client.setChatPublisher(secondword);
    		client.setChatSubscriber(secondword);

    		System.out.println("Who do you want to invite (Separate names with a semicolon, with no "
    				+ "spaces)?");
    		String receiver = scanIn.nextLine();
    		client.sendChatroom(secondword);
    		
        	int position = 0;
	       	while(receiver.length() != 0)
	       	{
	       		if(receiver.indexOf(';') == -1)
	       		{
	        			
	       			client.setProducer(receiver);
		        	client.send("Join" + secondword);
		        	System.out.println(client.getName() + ": Message Sent!");
		        	receiver = "";
	       		}
	       		else
	       		{
	       			
	       			String n = receiver.substring(0,receiver.indexOf(';'));
	        			
	       			position = receiver.indexOf(';') + 1;
	        			
	       			client.setProducer(n);
		        	client.send("Join " + secondword);
		        	System.out.println(client.getName() + ": Message Sent!");
		        	receiver = receiver.substring(position, receiver.length());
			        	
	       		}
		        	
		        	//message = scanIn.nextLine();
	       	}
    		}
    		else if(firstword.equals("Join"))
    		{
    			client.setChatPublisher(secondword);
    			client.setChatSubscriber(secondword);
    		}
    		
    		else
    		{
    			System.out.println("Dun goofed");

    		}
    		while(!message.equals("quit"))
    		{
	    		message = scanIn.nextLine();
	    		client.groupChatSend(message);
    		}
    	//}
	}
	
	private static void IMessage(String message, Scanner scanIn, SquirtChatClient client) throws JMSException
	{
		while(!message.equals("quit"))
		{
			System.out.println("What message would you like to send?");
			message = scanIn.nextLine();
	    	System.out.println("Who to send to?");
	    	String receiver;
	    	receiver = scanIn.nextLine();
	    	
	    	client.setProducer(receiver);
	    	client.send(message);
	    	System.out.println(client.getName() + "Message Sent!");	
		}
	}
	private static void GMessage(String message, Scanner scanIn, SquirtChatClient client) throws JMSException
	{
		while(!message.equals("quit"))
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
	}
	
	private static void broadcastMessage(String message, Scanner scanIn, SquirtChatClient client) throws JMSException
	{
		while(!message.equals("quit"))
		{
			//client.setPublisher("TESTNAME");
			//client.setSubscriber("TESTNAME");	        		
			message = scanIn.nextLine();
			client.broadcast(message);
	    	System.out.println(client.getName() + "Message Sent!");		        	
		}
	}
	
	private static void getList(SquirtChatClient client) {
		try {
			client.getUserList();//send("getList;"+client.getName());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	private static void printHelp()
	{
        System.out.println("Select a mode to use:");
        System.out.println("-gc to join a group chat");
        System.out.println("-gm to send a group message");
        System.out.println("-gl to get login list");
        System.out.println("-m to send a message to an individual");
        System.out.println("-b to broadcast a message");
        System.out.println("-r to reply to the last message");
        System.out.println("-h or HALP to see this message again");
        System.out.println("-q or QUIT  to quit");
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
	        System.out.println("-gl to get login list");
	        System.out.println("-gcl to get chatroom list");
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
	        	
	        	
	        	else if(message.equals("-m"))
	        	{
	        		IMessage( message, scanIn, client );
	        	}
	        	
	        	else if( message.equals("-gc")) {
	        		groupChat( message, scanIn, client );
	        	}
	        	else if(message.equals("-gm"))
	        	{
	        		GMessage(message,scanIn,client);
	        	}
	        	else if( message.equals("-gl"))
	        	{
	        		client.getUserList();
	        	}
	        	else if( message.equals("-gcl"))
	        	{
	        		client.getChatList();
	        	}
	        	
	        	
	        	else if(message.equals("-b"))
	        	{

	        		broadcastMessage(message,scanIn,client);
	        	} 
	        	else if(message.equals("-h"))
	        	{
	        		printHelp();
	        	}
	        	else
	        	{
	        		printHelp();
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
