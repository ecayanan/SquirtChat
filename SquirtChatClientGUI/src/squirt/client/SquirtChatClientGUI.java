package squirt.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;

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
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.activemq.ActiveMQConnection;

//import squirt.client.SquirtChatClientApplication.CloseHook;

public class SquirtChatClientGUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField tfSend;
	private boolean singleMode;
	private boolean broadcastMode;
	private boolean groupMode;
	private JTextArea textArea;
	private JTextField tfSignIn;
	private JScrollPane textAreaScroll;
	private JButton btnSend;
	private JButton btnSignIn;
	private JLabel lblSignedIn;
	private SquirtChatClient client;
	private DefaultListModel listModel;
	private JList lstLog;
	private JMenu menu; 
	
	//TODO menu from above

	// ADDED FROM SQUIRTCHATCLIENT APPLICATION
	
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
	
	private static SquirtChatClient wireClient(String user) throws JMSException, URISyntaxException {
		ActiveMQConnection connection = ActiveMQConnection.makeConnection(
				/* Constants.USERNAME, Constants.PASSWORD, */
				Constants.ACTIVEMQ_URL);
		connection.start();
		CloseHook.registerCloseHook(connection);
	
		// for communication via queues (ie one-on-one communication)
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
	
		// for communication via topics (broadcasting and subset broadcasting ie chatrooms)
		TopicSession topicSession = connection.createTopicSession( false, 
				Session.AUTO_ACKNOWLEDGE);
		Topic topic = topicSession.createTopic("TESTNAME");
		TopicSubscriber subscriber = topicSession.createSubscriber(topic);
		TopicPublisher publisher = topicSession.createPublisher(topic);
	
		Queue destQueue = session.createQueue(user);
		MessageProducer producer = session.createProducer(destQueue);
		MessageConsumer consumer = session.createConsumer(destQueue);
		return new SquirtChatClient(producer, session, subscriber, publisher, connection, user,consumer,topicSession);
	}

	
	// end of stuff that was added from blah
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SquirtChatClientGUI frame = new SquirtChatClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SquirtChatClientGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("SquirtChat");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setSize(new Dimension( 800, 600));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		//SEND BUTTON////////////////////////////////////////////////////////////////////////////
		btnSend = new JButton("Send");//TODO
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(singleMode) {
					String payload = tfSend.getText();
					textArea.append(payload + "//single msg\n");
					//tfSend.setText(null);  //TODO put in deliverable
				} else if(groupMode){
					String payload = tfSend.getText();
					textArea.append(payload + "//group msg\n");
					//tfSend.setText(null);  //TODO put in deliverable
				} else if(broadcastMode){
					String payload = tfSend.getText();
					textArea.append(payload + "//broadcast\n");
					//tfSend.setText(null);  //TODO put in deliverable
				} else;
					
			}
		});
		
		tfSend = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tfSend, 18, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tfSend, -1, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tfSend, -6, SpringLayout.WEST, btnSend);
		contentPane.add(tfSend);
		tfSend.setColumns(10);
		
		tfSend.addActionListener(new ActionListener(){ // TODO allows for enter to be used for button
															//refactor / re-place code
			public void actionPerformed(ActionEvent e){
				btnSend.doClick();
			}
		});
		
		///TEXT AREA////////////////////////////////////////////////////////////////
		
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textAreaScroll = new JScrollPane(textArea);
		textAreaScroll.setPreferredSize(new Dimension(300,300));
		sl_contentPane.putConstraint(SpringLayout.NORTH, textAreaScroll, 23, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textAreaScroll, -18, SpringLayout.NORTH, tfSend);
		sl_contentPane.putConstraint(SpringLayout.EAST, textAreaScroll, -138, SpringLayout.EAST, contentPane);
		contentPane.add(textAreaScroll);
		
		

		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSend, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSend, 0, SpringLayout.EAST, contentPane);
		contentPane.add(btnSend);
		
		///RADIO BUTTONS/////////////////////////////////////////
		
		JRadioButton rdbtnGroupMsg = new JRadioButton("-gm");
		sl_contentPane.putConstraint(SpringLayout.WEST, textAreaScroll, 14, SpringLayout.EAST, rdbtnGroupMsg);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGroupMsg, 72, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGroupMsg, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupMsg.setMnemonic(KeyEvent.VK_G);
		contentPane.add(rdbtnGroupMsg);
		rdbtnGroupMsg.addActionListener(this);
		
		JRadioButton rdbtnGroupChat = new JRadioButton("-gc");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGroupChat, 6, SpringLayout.SOUTH, rdbtnGroupMsg);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGroupChat, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupChat.setMnemonic(KeyEvent.VK_C);
		contentPane.add(rdbtnGroupChat);
		rdbtnGroupChat.addActionListener(this);
		
		JRadioButton rdbtnSingle = new JRadioButton("-m");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSingle, 6, SpringLayout.SOUTH, rdbtnGroupChat);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSingle, 10, SpringLayout.WEST, contentPane);
		rdbtnSingle.setMnemonic(KeyEvent.VK_M);
		contentPane.add(rdbtnSingle);
		rdbtnSingle.addActionListener(this);
		
		JRadioButton rdbtnBroadcast = new JRadioButton("-b");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnBroadcast, 7, SpringLayout.SOUTH, rdbtnSingle);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnBroadcast, 10, SpringLayout.WEST, contentPane);
		rdbtnBroadcast.setMnemonic(KeyEvent.VK_B);
		contentPane.add(rdbtnBroadcast);
		rdbtnBroadcast.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnGroupMsg);
		group.add(rdbtnGroupChat);
		group.add(rdbtnSingle);
		group.add(rdbtnBroadcast);
		
		/////////SIGN IN BUTTONS /////////////////////////////////////////////////////////////////////
		
		tfSignIn = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfSignIn, 0, SpringLayout.NORTH, contentPane);
		contentPane.add(tfSignIn);
		tfSignIn.setColumns(10);
		
		btnSignIn = new JButton("Squirt In");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSignIn, 11, SpringLayout.SOUTH, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfSignIn, 0, SpringLayout.WEST, btnSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSignIn, 17, SpringLayout.EAST, textAreaScroll);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSignIn, 0, SpringLayout.EAST, btnSend);
		contentPane.add(btnSignIn);
		
		tfSignIn.addActionListener(new ActionListener(){ // TODO allows for enter to be used for button
														//refactor / re-place code
			public void actionPerformed(ActionEvent e){
				btnSignIn.doClick();
			}
		});
		
		//////SIGN IN STUFF/////////////////////////////////////////////////////////////////////////////
		
		JLabel lblSignedInAs = new JLabel("Squirted In As: ");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSignedInAs, 2, SpringLayout.NORTH, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSignedInAs, 0, SpringLayout.WEST, rdbtnGroupMsg);
		contentPane.add(lblSignedInAs);
				
		listModel = new DefaultListModel();
		lstLog = new JList(listModel);
		sl_contentPane.putConstraint(SpringLayout.WEST, lstLog, 0, SpringLayout.WEST, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lstLog, 0, SpringLayout.SOUTH, textAreaScroll);
		sl_contentPane.putConstraint(SpringLayout.EAST, lstLog, -2, SpringLayout.EAST, contentPane);
		lstLog.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(lstLog);
		
		JLabel lblLog = new JLabel("Users Logged In:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lstLog, 6, SpringLayout.SOUTH, lblLog);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblLog, 0, SpringLayout.NORTH, rdbtnGroupMsg);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblLog, 0, SpringLayout.EAST, btnSend);
		contentPane.add(lblLog);
		
		JButton btnListMode = new JButton("Show Chatrooms");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnListMode, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnListMode, 0, SpringLayout.SOUTH, textAreaScroll);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnListMode, 0, SpringLayout.EAST, rdbtnGroupMsg);
		contentPane.add(btnListMode);
		lblSignedInAs.setVisible(false);
		//createComponents();
		//setListeners();
	}

	
	//////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////DO NOT CROSS////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////////////
	private void createComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setSize(new Dimension( 800, 600));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
/*
		final JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(singleMode) {
					String payload = tfSend.getText();
					textArea.append(payload);
				} else if(groupMode) {
					
				} else if(broadcastMode){
					
				}
				else;
					
			}
		});
*/
		
		/////////////////////////////////////////////////
		tfSend = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tfSend, 18, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tfSend, -1, SpringLayout.SOUTH, contentPane);
		//TODO sl_contentPane.putConstraint(SpringLayout.EAST, tfSend, -6, SpringLayout.WEST, btnSend);
		contentPane.add(tfSend);
		tfSend.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, textArea, 23, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textArea, -18, SpringLayout.NORTH, tfSend);
		sl_contentPane.putConstraint(SpringLayout.EAST, textArea, -138, SpringLayout.EAST, contentPane);
		contentPane.add(textArea);
		

		//TODO sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSend, 0, SpringLayout.SOUTH, contentPane);
		//sl_contentPane.putConstraint(SpringLayout.EAST, btnSend, 0, SpringLayout.EAST, contentPane);
		//contentPane.add(btnSend);
		
		///BUTTONS/////////////////////////////////////////
		
		JRadioButton rdbtnGroupMsg = new JRadioButton("-gm");
		sl_contentPane.putConstraint(SpringLayout.WEST, textArea, 14, SpringLayout.EAST, rdbtnGroupMsg);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGroupMsg, 72, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGroupMsg, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupMsg.setMnemonic(KeyEvent.VK_G);
		contentPane.add(rdbtnGroupMsg);
		rdbtnGroupMsg.addActionListener(this);
		
		JRadioButton rdbtnGroupChat = new JRadioButton("-gc");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGroupChat, 6, SpringLayout.SOUTH, rdbtnGroupMsg);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGroupChat, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupMsg.setMnemonic(KeyEvent.VK_C);
		contentPane.add(rdbtnGroupChat);
		rdbtnGroupChat.addActionListener(this);
		
		JRadioButton rdbtnSingle = new JRadioButton("-m");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSingle, 6, SpringLayout.SOUTH, rdbtnGroupChat);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSingle, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupMsg.setMnemonic(KeyEvent.VK_M);
		contentPane.add(rdbtnSingle);
		rdbtnSingle.addActionListener(this);
		
		JRadioButton rdbtnBroadcast = new JRadioButton("-b");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnBroadcast, 7, SpringLayout.SOUTH, rdbtnSingle);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnBroadcast, 10, SpringLayout.WEST, contentPane);
		rdbtnGroupMsg.setMnemonic(KeyEvent.VK_B);
		contentPane.add(rdbtnBroadcast);
		rdbtnBroadcast.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnGroupMsg);
		group.add(rdbtnGroupChat);
		group.add(rdbtnSingle);
		group.add(rdbtnBroadcast);
		
		tfSignIn = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfSignIn, 0, SpringLayout.NORTH, contentPane);
		contentPane.add(tfSignIn);
		tfSignIn.setColumns(10);
		
		btnSignIn = new JButton("Squirt In");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSignIn, 11, SpringLayout.SOUTH, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfSignIn, 0, SpringLayout.WEST, btnSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSignIn, 17, SpringLayout.EAST, textArea);
		//TODO sl_contentPane.putConstraint(SpringLayout.EAST, btnSignIn, 0, SpringLayout.EAST, btnSend);
		contentPane.add(btnSignIn);
		
		JLabel lblSignedInAs = new JLabel("Squirted In As: ");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSignedInAs, 2, SpringLayout.NORTH, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSignedInAs, 0, SpringLayout.WEST, rdbtnGroupMsg);
		contentPane.add(lblSignedInAs);
		
		listModel = new DefaultListModel();
		lstLog = new JList(listModel);
		sl_contentPane.putConstraint(SpringLayout.WEST, lstLog, 0, SpringLayout.WEST, tfSignIn);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lstLog, 0, SpringLayout.SOUTH, textArea);
		sl_contentPane.putConstraint(SpringLayout.EAST, lstLog, -2, SpringLayout.EAST, contentPane);
		lstLog.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(lstLog);
		
		JLabel lblLog = new JLabel("Users Logged In:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lstLog, 6, SpringLayout.SOUTH, lblLog);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblLog, 0, SpringLayout.NORTH, rdbtnGroupMsg);
		//TODO sl_contentPane.putConstraint(SpringLayout.EAST, lblLog, 0, SpringLayout.EAST, btnSend);
		contentPane.add(lblLog);
		
		JButton btnListMode = new JButton("Show Chatrooms");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnListMode, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnListMode, 0, SpringLayout.SOUTH, textArea);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnListMode, 0, SpringLayout.EAST, rdbtnGroupMsg);
		contentPane.add(btnListMode);
		lblSignedInAs.setVisible(false);
		
		btnSignIn.addActionListener(this);
		
	}
	
	private void setListeners() {
		btnSignIn.addActionListener(this);
	}
	
	private void updateContents() {
		// update list
		repaint();
	}


	private void signIn() throws JMSException, URISyntaxException {
		String username = tfSignIn.getText();
		tfSignIn.setVisible(false);
		lblSignedIn = new JLabel("Squirted in as: " + username);
		contentPane.add(lblSignedIn);
		lblSignedIn.setLocation(tfSignIn.getLocation());
		btnSignIn.setText("Squirt Out");
		client = wireClient(username);
		updateContents();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getActionCommand() == "-m") {
			singleMode = true;
			broadcastMode = false;
			groupMode = false;
			// and everything else is false
			
			// set list selectability to 1
			lstLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		
		else if( e.getActionCommand() == "-b") {
			singleMode = false;
			broadcastMode = true;
			groupMode = false;
			
		}
	
		else if( e.getActionCommand() == "-gc" ) {
			// not sure what to do with group chat
			singleMode = false;
			broadcastMode = false;
			groupMode = false;
		}
		
		else if( e.getActionCommand() == "-gm" ) {
			groupMode = true;
			singleMode = false;
			broadcastMode = false;
			
			// set list selectability to multiple
			lstLog.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		
		else if( e.getActionCommand() == "Squirt In" && !tfSignIn.getText().equals("")) {
			try {
				signIn();
			} catch (JMSException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		else if( e.getActionCommand() == "Squirt Out") {
			// tell server we're logging off
			// register close hook
			// exit everything out
		}
		
		
		// and so on
	}
}
