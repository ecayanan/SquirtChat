package squirt.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
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
	
	public SquirtChatClient(MessageProducer producer, MessageConsumer consumer, Session session, 
			TopicSubscriber subscriber, TopicPublisher publisher, 
			ActiveMQConnection connection ) {
		super();
		this.producer = producer;
		this.consumer = consumer;
		this.session = session;
		// this.subscriber = subscriber;
		this.publisher = publisher;
		this.connection = connection;
		try {
			subscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			consumer.setMessageListener(this);
		} catch (JMSException e ) {
			e.printStackTrace();
		}
	} 
	
	public void send(String msg) throws JMSException {
		producer.send(session.createTextMessage(msg));
		//publisher.send(session.createTextMessage(msg));
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
}
