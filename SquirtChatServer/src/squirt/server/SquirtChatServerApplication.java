package squirt.server;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;


public class SquirtChatServerApplication {
	
	static CustomObjectMessageCreator objectMessageCreator;
	static CustomStringMessageCreator stringMessageCreator;

	@Bean
	Server makeServer() {
		return new Server();
	}
	
    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(
                new ActiveMQConnectionFactory(Constants.ACTIVEMQ_URL));
    }
    
    /*
    
    @Bean 
    MessageListenerAdapter loginReceiver(Server myServer) {
        return new MessageListenerAdapter(myServer)
        {{
            setDefaultListenerMethod("loginReceive");
        }};
    }
    */
    
    /*
    @Bean
    SimpleMessageListenerContainer loginContainer(final MessageListenerAdapter messageListener, 
    		final ConnectionFactory connectionFactory) {
        return new SimpleMessageListenerContainer() {{
            setMessageListener(messageListener);
            setConnectionFactory(connectionFactory);
            setDestinationName(Constants.LOGINQUEUE); // name of new login queue
        }};
    } */
    
    @Bean
    MessageListenerAdapter receiver(Server myServer) {
        return new MessageListenerAdapter(myServer)
        {{
            setDefaultListenerMethod("receive");
        }};
    }
    
    @Bean
    SimpleMessageListenerContainer container(final MessageListenerAdapter messageListener,
            final ConnectionFactory connectionFactory) {
        return new SimpleMessageListenerContainer() {{
            setMessageListener(messageListener);
            setConnectionFactory(connectionFactory);
            setDestinationName(Constants.SERVERNAME);
        }};
    }

    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
    
	public static void main(String[] args) throws Throwable {
		BrokerService broker = new BrokerService();
		broker.addConnector(Constants.ACTIVEMQ_URL);
		broker.setPersistent(false);
		broker.start();
		
		AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(SquirtChatServerApplication.class);
        
    	objectMessageCreator = new CustomObjectMessageCreator();
    	stringMessageCreator = new CustomStringMessageCreator();
		
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

	}
    
    @SuppressWarnings("resource")
	public CustomStringMessageCreator getStringMessageCreator(final String toSend) {
    	stringMessageCreator.setStringMessage(toSend);
    	return stringMessageCreator;
    }
    
    public CustomObjectMessageCreator getObjectMessageCreator(final ArrayList<String> toSend) {
    	objectMessageCreator.setObjectMessage(toSend);
    	return objectMessageCreator;
    }
}
