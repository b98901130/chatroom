import javax.swing.JPanel;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class ChatTabClient extends JPanel {
	public ChatWindowClient cwc; 
	public JPanel tabPanel;
	public JTextField textUsername = new JTextField();;
	public JTextField textChat = new JTextField();
	public JList userList = new JList();
	public Listener listener;
	public ChatTabClient myself;
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
/*	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatTabClient tab = new ChatTabClient();
					tab.tabPanel.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
*/	

	/**
	 * Create the application.
	 */
	public ChatTabClient(ChatWindowClient _cwc) {
		cwc = _cwc;
		myself = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		tabPanel = new JPanel();		
		tabPanel.setBounds(100, 100, 931, 633);

		tabPanel.setLayout(null);
		emoticonScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		emoticonScroll.setBounds(450, 410, 100, 100);
		
		tabPanel.add(emoticonScroll);
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
		tabPanel.add(label);
		btnConnect.setBounds(10, 525, 87, 23);
		
		
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				username = textUsername.getText();
				textUsername.setEditable(false);
				new Thread(listener = new Listener(myself)).start();
			    textChat.requestFocus();
			}
		});
		
		tabPanel.add(btnConnect);
		btnDisconnect.setBounds(107, 525, 104, 23);
		tabPanel.add(btnDisconnect);
		btnWhisper.setBounds(221, 525, 104, 23);
		tabPanel.add(btnWhisper);
		btnChatroom.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				cwc.createNewRoom();
			}
		});
		btnChatroom.setBounds(335, 525, 104, 23);
		tabPanel.add(btnChatroom);
		btnEmoticon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {				
				emoticonScroll.setVisible(!emoticonScroll.isVisible());
			}
		});
		btnEmoticon.setBounds(449, 525, 104, 23);
		tabPanel.add(btnEmoticon);						
		btnCustom.setBounds(563, 525, 104, 23);
		tabPanel.add(btnCustom);				
		btnTransfer.setBounds(677, 525, 104, 23);
		tabPanel.add(btnTransfer);		
		btnVoice.setBounds(791, 525, 116, 23);
		tabPanel.add(btnVoice);		
		
		textUsername = new JTextField();
		textUsername.setBounds(10, 558, 152, 26);
		tabPanel.add(textUsername);
		textUsername.setColumns(10);
		textChat.setBounds(172, 560, 731, 24);				
		textChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				try
				{
					listener.out.writeUTF(listener.user + "> " + textChat.getText());
				}
				catch(Exception ex)
				{
					System.out.println(ex);
				}
				textChat.setText(null);				
			}
		});
		tabPanel.add(textChat);		
		userList.setBounds(10, 45, 152, 470);
		tabPanel.add(userList);
		textPane.setBounds(172, 10, 731, 505);
	    textScroll.setBounds(172, 10, 731, 505);
	    tabPanel.add(textScroll);
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

