import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;


public class ChatWindowClient {

	public JFrame frmLabChatroom;
	public JTextField textUsername = new JTextField();;
	public JTextField textChat = new JTextField();
	public JList userList = new JList();
	public Listener listener;
	public ChatWindowClient myCWC;
	public JTextPane textPane= new JTextPane();
	public JScrollPane textScroll = new JScrollPane();	
	public String username;
	public JButton btnConnect = new JButton("\u9023\u7DDA");
	public JButton btnDisconnect = new JButton("\u96E2\u7DDA");
	public JButton btnWhisper = new JButton("\u6084\u6084\u8A71");
	public JButton btnChatroom = new JButton("\u6703\u5BA2\u5BA4");
	public JButton btnEmoticon = new JButton("\u8868\u60C5\u7B26\u865F");
	public JButton btnCustom = new JButton("\u6A5F\u5668\u4EBA");
	public JButton btnTransfer = new JButton("\u50B3\u9001\u6A94\u6848");
	public JButton btnVoice = new JButton("\u8A9E\u97F3\u901A\u8A71");
	private final JPanel emoticonTable = new JPanel();
	private final JButton emo1 = new JButton("");
	private final JButton emo2 = new JButton("");
	private final JButton emo3 = new JButton("");
	private final JButton emo4 = new JButton("");
	private final JScrollPane emoticonScroll = new JScrollPane();;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindowClient window = new ChatWindowClient();
					window.frmLabChatroom.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatWindowClient() {
		myCWC = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLabChatroom = new JFrame();
		frmLabChatroom.setTitle("Lab1 Chatroom");
		frmLabChatroom.setBounds(100, 100, 929, 632);
		frmLabChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLabChatroom.getContentPane().setLayout(null);
		emoticonScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		emoticonScroll.setBounds(450, 410, 100, 100);
		
		frmLabChatroom.getContentPane().add(emoticonScroll);
		emoticonScroll.setViewportView(emoticonTable);
		emoticonTable.setLayout(null);
		
		emo1.addMouseListener(emoMouseListener("{:D}"));
		emo2.addMouseListener(emoMouseListener("{:)}"));
		emo3.addMouseListener(emoMouseListener("{:(}"));
		emo4.addMouseListener(emoMouseListener("{:D}"));
				
		emo1.setBounds(0, 0, 25, 25);
		emo2.setBounds(25, 0, 25, 25);
		emo3.setBounds(50, 0, 25, 25);
		emo4.setBounds(75, 0, 25, 25);
		
		emoticonTable.add(emo1);
		emoticonTable.add(emo2);				
		emoticonTable.add(emo3);				
		emoticonTable.add(emo4);
		
		emoticonScroll.setVisible(false);
		
		JLabel label = new JLabel("\u4F7F\u7528\u8005\u5217\u8868");
		label.setBounds(10, 10, 152, 25);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		frmLabChatroom.getContentPane().add(label);
		btnConnect.setBounds(10, 525, 87, 23);
		
		
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				username = textUsername.getText();
				textUsername.setEditable(false);
				new Thread(listener = new Listener(myCWC)).start();
			    textChat.requestFocus();
			}
		});
		
		frmLabChatroom.getContentPane().add(btnConnect);
		btnDisconnect.setBounds(107, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnDisconnect);
		btnWhisper.setBounds(221, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnWhisper);
		btnChatroom.setBounds(335, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnChatroom);
		btnEmoticon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {				
				emoticonScroll.setVisible(!emoticonScroll.isVisible());
			}
		});
		btnEmoticon.setBounds(449, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnEmoticon);						
		btnCustom.setBounds(563, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnCustom);				
		btnTransfer.setBounds(677, 525, 104, 23);
		frmLabChatroom.getContentPane().add(btnTransfer);		
		btnVoice.setBounds(791, 525, 116, 23);
		frmLabChatroom.getContentPane().add(btnVoice);		
		
		textUsername = new JTextField();
		textUsername.setBounds(10, 558, 152, 26);
		frmLabChatroom.getContentPane().add(textUsername);
		textUsername.setColumns(10);
		textChat.setBounds(172, 560, 731, 24);				
		textChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				try
				{
					listener.out.writeUTF(Listener.user + "> " + textChat.getText());
				}
				catch(Exception ex)
				{
					System.out.println(ex);
				}
				textChat.setText(null);				
			}
		});
		frmLabChatroom.getContentPane().add(textChat);		
		userList.setBounds(10, 45, 152, 470);
		frmLabChatroom.getContentPane().add(userList);
		textPane.setBounds(172, 10, 731, 505);
	    textScroll.setBounds(172, 10, 731, 505);
	    frmLabChatroom.getContentPane().add(textScroll);
	    textScroll.setViewportView(textPane);	    	    
	}
	
	public MouseAdapter emoMouseListener(final String s){
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				textChat.setText(textChat.getText()+s);
				emoticonScroll.setVisible(false);
				textChat.requestFocus();
			};
		}; 
		return ma;
	}
}
