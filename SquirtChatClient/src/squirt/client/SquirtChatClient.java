package squirt.client;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;


public class SquirtChatClient {
	private MessageProducer producer;
	private Session session;
	
	public SquirtChatClient(MessageProducer producer, Session session) {
		super();
		this.producer = producer;
		this.session = session;
	} 
	
	public void send(String msg) throws JMSException {
		producer.send(session.createTextMessage(msg));
	}
}
