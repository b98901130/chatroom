import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;


public class ChatTabClient extends JPanel {
	public ChatWindowClient cwc; 
	public int room_id;
	public JPanel tabPanel;
	public JTextField textUsername = new JTextField();
	public JTextField textChat = new JTextField();
	public DefaultListModel<String> userList = new DefaultListModel<String>();
	public JList userListUI = new JList(userList);

	public ChatTabClient myself;
	public JTextPane textPane = new JTextPane();
	public JScrollPane textScroll = new JScrollPane();
	
	public JButton btnConnect = new JButton("\u9023\u7DDA");
	public JButton btnDisconnect = new JButton("\u96E2\u7DDA");
	public JButton btnWhisper = new JButton("\u6084\u6084\u8A71");
	public JButton btnChatroom = new JButton("\u6703\u5BA2\u5BA4");
	public JButton btnEmoticon = new JButton("\u8868\u60C5\u7B26\u865F");
	public JButton btnCustom = new JButton("\u6A5F\u5668\u4EBA");
	public JButton btnTransfer = new JButton("\u50B3\u9001\u6A94\u6848");
	public JButton btnVoice = new JButton("\u8A9E\u97F3\u901A\u8A71");
	public JButton btnLeaveRoom = new JButton("\u96E2\u958b");
	private final JPanel emoticonPane = new JPanel();
	private final JPanel emoticonTable = new JPanel();
	private final JButton emo1 = new JButton("");
	private final JButton emo2 = new JButton("");
	private final JButton emo3 = new JButton("");
	private final JButton emo4 = new JButton("");
	private final JButton emo5 = new JButton("");
	private final JButton emo6 = new JButton("");
	private final JButton emo7 = new JButton("");
	private final JButton emo8 = new JButton("");
	private final JButton emo9 = new JButton("");
	private final JButton emo10 = new JButton("");
	private final JButton emo11 = new JButton("");
	private final JButton emo12 = new JButton("");
	private final JButton emo13 = new JButton("");
	private final JButton emo14 = new JButton("");
	private final JButton emo15 = new JButton("");
	private final JButton emo16 = new JButton("");
	
	private final JScrollPane emoticonScroll = new JScrollPane();
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
	public ChatTabClient(ChatWindowClient _cwc, int rmid) {
		cwc = _cwc;
		room_id = rmid;
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
		
		emoticonPane.setBounds(450, 420, 100, 100);
		emoticonTable.setBounds(0, 0, 450, 420);
		tabPanel.add(emoticonPane);
		emoticonPane.setLayout(null);
		emoticonScroll.setBounds(0, 0, 100, 100);
		emoticonPane.add(emoticonScroll);
		emoticonScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		emoticonScroll.setViewportView(emoticonTable);
		emoticonTable.setLayout(null);
		emoticonPane.setVisible(false);
		
		emo1.addMouseListener(emoMouseListener("{:D}"));
		emo2.addMouseListener(emoMouseListener("{:)}"));
		emo3.addMouseListener(emoMouseListener("{:(}"));
		emo4.addMouseListener(emoMouseListener("{:D}"));
				
		emo1.setBounds(0, 0, 25, 25);
		emo2.setBounds(25, 0, 25, 25);
		emo3.setBounds(50, 0, 25, 25);
		emo4.setBounds(75, 0, 25, 25);
		emo5.setBounds(0, 25, 25, 25);
		emo6.setBounds(25, 25, 25, 25);
		emo7.setBounds(50, 25, 25, 25);
		emo8.setBounds(75, 25, 25, 25);
		emo9.setBounds(0, 49, 25, 25);		
		emo10.setBounds(25, 49, 25, 25);		
		emo11.setBounds(50, 49, 25, 25);		
		emo12.setBounds(75, 49, 25, 25);		
		emo13.setBounds(0, 73, 25, 25);	
		emo14.setBounds(25, 73, 25, 25);		
		emo15.setBounds(50, 73, 25, 25);		
		emo16.setBounds(75, 73, 25, 25);
		
		emoticonTable.add(emo1);
		emoticonTable.add(emo2);				
		emoticonTable.add(emo3);				
		emoticonTable.add(emo4);						
		emoticonTable.add(emo5);					
		emoticonTable.add(emo6);
		emoticonTable.add(emo7);						
		emoticonTable.add(emo8);		
		emoticonTable.add(emo9);	
		emoticonTable.add(emo10);		
		emoticonTable.add(emo11);		
		emoticonTable.add(emo12);		
		emoticonTable.add(emo13);		
		emoticonTable.add(emo14);		
		emoticonTable.add(emo15);		
		emoticonTable.add(emo16);
		
		JLabel label = new JLabel("\u4F7F\u7528\u8005\u5217\u8868");
		label.setBounds(10, 10, 152, 25);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		tabPanel.add(label);
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cwc.username = textUsername.getText();
				textUsername.setEditable(false);
				new Thread(cwc.listener = new Listener(cwc)).start();
			    textChat.requestFocus();
			    btnConnect.setEnabled(false);
			    btnDisconnect.setEnabled(true);
			    btnConnect.setVisible(false);
			    btnDisconnect.setVisible(true);
			    textChat.setEnabled(true);
			}
		});
		btnConnect.setBounds(10, 525, 87, 23);
		tabPanel.add(btnConnect);
		
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					cwc.listener.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				textUsername.setEditable(true);
				btnConnect.setEnabled(true);
				btnDisconnect.setEnabled(false);
			    btnConnect.setVisible(true);
			    btnDisconnect.setVisible(false);
			    textChat.setEnabled(false);
			    textPane.setEditable(true);
			    textPane.setText("");
			    textPane.setEditable(false);
			    cwc.removeAllTabs();
			}
		});
		btnDisconnect.setBounds(10, 525, 87, 23);
		tabPanel.add(btnDisconnect);
		
		btnWhisper.setBounds(221, 525, 104, 23);
		tabPanel.add(btnWhisper);
		
		btnChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cwc.sentNewRoomReq();				
			}
		});
		btnChatroom.setBounds(335, 525, 104, 23);
		tabPanel.add(btnChatroom);
		
		btnEmoticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				emoticonPane.setVisible(!emoticonPane.isVisible());
			}
		});
		btnEmoticon.setBounds(449, 525, 104, 23);
		tabPanel.add(btnEmoticon);						
		
		btnCustom.setBounds(563, 525, 104, 23);
		tabPanel.add(btnCustom);				
		
		btnTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (userListUI.isSelectionEmpty()) return;
				
				// 1. transmitter->server: (IPRequest)username
				String receiver = userListUI.getSelectedValue().toString();
				if (cwc.username.equals(receiver)) return;
				
				try {
					cwc.listener.out.writeUTF("(IPRequest)" + receiver);
					cwc.listener.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		});
		btnTransfer.setBounds(677, 525, 104, 23);
		tabPanel.add(btnTransfer);
		
		btnVoice.setBounds(791, 525, 116, 23);
		tabPanel.add(btnVoice);		
		
		btnLeaveRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				cwc.removeTab(room_id);
				try
				{
					cwc.listener.out.writeUTF("(LeaveRoomRequest)");
					cwc.listener.out.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		btnLeaveRoom.setBounds(10, 525, 87, 23);
		btnLeaveRoom.setVisible(false);
		tabPanel.add(btnLeaveRoom);
		
		textUsername = new JTextField();
		textUsername.setToolTipText("pleas enter username");
		textUsername.setBounds(10, 558, 152, 26);
		tabPanel.add(textUsername);
		textUsername.setColumns(10);
		textChat.setBounds(172, 560, 731, 24);				
		textChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				try
				{
					cwc.listener.out.writeUTF("(text%"+cwc.username+"%"+room_id+")" + textChat.getText());
					cwc.listener.out.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				textChat.setText(null);				
			}
		});		
		tabPanel.add(textChat);
		if (room_id == 0)
			textChat.setEnabled(false);
		
		userListUI.setBounds(10, 45, 152, 470);
		tabPanel.add(userListUI);
		textPane.setBounds(172, 10, 731, 505);
		textPane.setEditable(false);
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
	
	public void autoConnect(int r_id){
		room_id = r_id;
		textUsername.setText(cwc.username);
		textUsername.setEditable(false);		
	    textChat.requestFocus();
	    btnConnect.setEnabled(false);
	    btnDisconnect.setEnabled(false);
	    btnConnect.setVisible(false);
	    btnDisconnect.setVisible(false);
		btnLeaveRoom.setVisible(true);
	    textChat.setEnabled(true);		
	}
}

