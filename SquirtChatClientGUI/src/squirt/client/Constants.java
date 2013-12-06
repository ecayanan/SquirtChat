package squirt.client;

public class Constants {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";
	public static String USERNAME = "max";	
	public static String PASSWORD = "pwd";	
	public static String QUEUENAME = "test";
	public static void helpMsG(){
        System.out.println("Enter a message to send: ");
        System.out.println("Select a mode to use:");
        System.out.println("-gc to join a group chat");
        System.out.println("-gm to send a group message");
        System.out.println("-m to send a message to an individual");
        System.out.println("-b to broadcast a message");
        System.out.println("-r to reply to the last message");
        System.out.println("-h or HALP to see this message again");
        System.out.println("-q or QUIT  to quit");
	}
}
