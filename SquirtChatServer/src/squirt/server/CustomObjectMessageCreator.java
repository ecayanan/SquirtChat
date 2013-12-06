package squirt.server;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class CustomObjectMessageCreator implements MessageCreator {
	
	private Object objectMessage;

	@Override
	public Message createMessage(Session session) throws JMSException {
		return session.createObjectMessage((Serializable) objectMessage);
	}

	public Object getObjectMessage() {
		return objectMessage;
	}

	public void setObjectMessage(Object objectMessage) {
		this.objectMessage = objectMessage;
	}
}