package squirt.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JList;

public class SquirtChatClientGUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField tfSend;
	private boolean singleMode;
	private boolean broadcastMode;
	private JTextArea textArea;

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(singleMode) {
					String payload = tfSend.getText();
					textArea.append(payload);
				}
					
			}
		});
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		tfSend = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tfSend, 18, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tfSend, -1, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tfSend, -6, SpringLayout.WEST, btnSend);
		contentPane.add(tfSend);
		tfSend.setColumns(10);
		
		textArea = new JTextArea();
		sl_contentPane.putConstraint(SpringLayout.NORTH, textArea, 23, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textArea, -18, SpringLayout.NORTH, tfSend);
		sl_contentPane.putConstraint(SpringLayout.EAST, textArea, -138, SpringLayout.EAST, contentPane);
		textArea.setEnabled(false);
		contentPane.add(textArea);
		

		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSend, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSend, 0, SpringLayout.EAST, contentPane);
		contentPane.add(btnSend);
		
		///BUTTONS//////
		
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
		
		// list of online usrs
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getActionCommand() == "-m") {
			singleMode = true;
			// and everything else is false
			// set list selectability to 1
		}
		
		// and so on
	}
}
