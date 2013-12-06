package squirt.client;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;


public class SquirtChatClient implements MessageListener {

	private Session session;
	private ActiveMQConnection connection;
	public ArrayList<String> userList = new ArrayList<String>();
	private String user; //Using a string for user
	
	private TopicSession topicSession;
	
	private TopicSubscriber chatSubscriber;
	private TopicPublisher chatPublisher;
	private TopicSubscriber broadcastSubscriber;
	private TopicPublisher broadcastPublisher;
	private TopicSubscriber loginSubscriber; // receives a list of people currently logged in from the server
	private MessageProducer producer;
	private MessageConsumer consumer;
	private Destination destQueue;
	public String buf;

	
	public SquirtChatClient(String user, ActiveMQConnection connection ) {
		
		super();
		this.connection = connection;
		this.user = user;
		
		// create a topic session, session, and destination queue
		try {
			this.topicSession = connection.createTopicSession( false, 
					Session.AUTO_ACKNOWLEDGE);
			this.session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			this.destQueue = session.createQueue(user);
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// creating a broadcast subscriber / publisher
		try {
			Topic broadcastTopic = topicSession.createTopic("BROADCAST");
			this.broadcastSubscriber = topicSession.createSubscriber(broadcastTopic);
			this.broadcastPublisher = topicSession.createPublisher(broadcastTopic);
			this.broadcastSubscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// creating a chatroom subscriber / publisher
		try {
			Topic chatTopic = topicSession.createTopic("DEFAULTCHATROOM");
			this.chatSubscriber = topicSession.createSubscriber(chatTopic);
			this.chatPublisher = topicSession.createPublisher(chatTopic);
			this.chatSubscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create message consumer / producer
		try {
			this.consumer = session.createConsumer(destQueue);
			this.producer = session.createProducer(destQueue);
			this.consumer.setMessageListener(this);
		} catch (JMSException e ) {
			e.printStackTrace();
		}
		
		// tell server I wish to log in
		try {
			sendUser(user);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	} 
	
	// LIST OF COMMANDS
	
	public void send(String msg) throws JMSException {
		producer.send(session.createTextMessage(msg));
	}
	
	public void broadcast(String msg) throws JMSException{
		broadcastPublisher.send(session.createTextMessage(msg));
	}
	public void groupChatSend(String msg) throws JMSException
	{
		chatPublisher.send(session.createTextMessage(msg));
	}
	public void sendUser(String msg) throws JMSException{
		Destination oldDest = getProducer();
		setProducer("server");
		producer.send(session.createTextMessage("addUser;"+msg));
		setProducer(((Queue) oldDest).getQueueName());
	}
	public void sendChatroom(String msg) throws JMSException
	{
		Destination oldDest = getProducer();
		setProducer("server");
		producer.send(session.createTextMessage("addChat;"+msg));
		setProducer(((Queue) oldDest).getQueueName());		
	}
	public void getUserList() throws JMSException{
		Destination oldDest = getProducer();
		setProducer("server");
		send("getList;"+getName());
		setProducer(((Queue) oldDest).getQueueName());
	}
	public void getChatList() throws JMSException 
	{
		Destination oldDest = getProducer();
		setProducer("server");
		send("getChat;"+getName());
		setProducer(((Queue) oldDest).getQueueName());
	}
	
	// Listener method
	
	@SuppressWarnings("unchecked")
	public void onMessage( Message input ) {
		if( input instanceof ObjectMessage ) {
			try {
				System.out.println(((ObjectMessage) input).getObject().toString());
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<String> a = new ArrayList<String>();
			
			try {
				this.userList = ( ArrayList<String> ) ((ObjectMessage) input).getObject();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( input instanceof TextMessage )
			try {
				// save this to buffer
				buf = ((TextMessage)input).getText();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	// setting names
	public void setName(String usr){
		this.user = usr;
	}
	public String getName()
	{
		return this.user;
	}
	
	/*
	 * Sets the producer to send to the proper different user queue
	 */
	public void setProducer(String user) throws JMSException
	{
		Queue destQueue = session.createQueue(user);
		MessageProducer producer = session.createProducer(destQueue);
		this.producer = producer;
	}
	
	public Destination getProducer() throws JMSException {
		return producer.getDestination();
	}
	
	public void setChatPublisher(String chatRoom) throws JMSException
	{
		Topic destQueue = topicSession.createTopic(chatRoom);
		TopicPublisher publisher = topicSession.createPublisher(destQueue);
		this.chatPublisher = publisher;
	}
	
	public void setChatSubscriber(String chatRoom) throws JMSException
	{
		Topic destQueue = topicSession.createTopic(chatRoom);		
		TopicSubscriber subscriber = topicSession.createSubscriber(destQueue);
		this.chatSubscriber = subscriber;
		this.chatSubscriber.setMessageListener(this);
	}
}
