package squirt.client;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;


public class SquirtChatClient implements MessageListener {
	private MessageProducer producer;
	private MessageConsumer consumer;
	private Session session;
	private TopicSubscriber subscriber;
	private TopicPublisher publisher;
	private ActiveMQConnection connection;
	private List<String> userList;
	private String user; //Using a string for user
	
	public SquirtChatClient(MessageProducer producer, Session session, 
			TopicSubscriber subscriber, TopicPublisher publisher, 
			ActiveMQConnection connection, String user, MessageConsumer consumer ) {
		super();
		this.producer = producer;
		this.consumer = consumer;
		this.session = session;
		this.subscriber = subscriber;
		this.publisher = publisher;
		this.connection = connection;
		this.user = user;
		/*try {
			subscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			consumer.setMessageListener(this);
			subscriber.setMessageListener(this);

		} catch (JMSException e ) {
			e.printStackTrace();
		}
	} 
	
	public void send(String msg) throws JMSException {
		//producer.send(session.createTextMessage(msg));
		publisher.send(session.createTextMessage(msg));
	}
	
	public void sendUser(String msg) throws JMSException{
		producer.send(session.createTextMessage(msg));
	}
	
	public void onMessage( Message input ) {
		TextMessage retval = (TextMessage) input;
		try {
			System.out.println("client received message: " + retval.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
}
