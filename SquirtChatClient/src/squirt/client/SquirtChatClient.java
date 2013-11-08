package squirt.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;


public class SquirtChatClient implements MessageListener {
	private MessageProducer producer;
	private Session session;
	private TopicSubscriber subscriber;
	private TopicPublisher publisher;
	private ActiveMQConnection connection;
	
	public SquirtChatClient(MessageProducer producer, Session session, 
			TopicSubscriber subscriber, TopicPublisher publisher, 
			ActiveMQConnection connection ) {
		super();
		this.producer = producer;
		this.session = session;
		this.subscriber = subscriber;
		this.publisher = publisher;
		this.connection = connection;
		try {
			subscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void send(String msg) throws JMSException {
		//producer.send(session.createTextMessage(msg));
		publisher.send(session.createTextMessage(msg));
	}
	
	public void onMessage( Message input ) {
		TextMessage retval = (TextMessage) input;
		try {
			System.out.println("client received broadcast: " + retval.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
