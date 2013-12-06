package squirt.server;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class CustomStringMessageCreator implements MessageCreator {
	private String stringMessage;
	
	@Override
	public Message createMessage(Session session) throws JMSException {
		return session.createObjectMessage(stringMessage);
	}

	public String getStringMessage() {
		return stringMessage;
	}

	public void setStringMessage(String stringMessage) {
		this.stringMessage = stringMessage;
	}
}