import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;

public class ChatTabClient extends JPanel {

	private static final long serialVersionUID = 1L;
	public ChatWindowClient cwc; 
	public int room_id;
	public JPanel tabPanel;
	public JTextField textUsername = new JTextField();
	public JTextField textChat = new JTextField();
	public DefaultListModel<String> userList = new DefaultListModel<String>();
	public JList<String> userListUI = new JList<String>(userList);
	public JLabel profilePicLabel = new JLabel("");

	public ChatTabClient myself;
	public StyleContext sc = new StyleContext();
	public JTextPane textPane = new JTextPane(new DefaultStyledDocument(sc));
	public JScrollPane textScroll = new JScrollPane();
	
	public JButton btnConnect = new JButton("\u9023\u7DDA");
	public JButton btnDisconnect = new JButton("\u96E2\u7DDA");
	public JButton btnWhisper = new JButton("\u6084\u6084\u8A71");
	public JButton btnChatroom = new JButton("\u6703\u5BA2\u5BA4");
	public JButton btnEmoticon = new JButton("\u8868\u60C5\u7B26\u865F");
	public JButton btnRobot = new JButton("\u6A5F\u5668\u4EBA");
	public JButton btnTransfer = new JButton("\u50B3\u9001\u6A94\u6848");
	public JButton btnVoice = new JButton("\u8996\u8A0A\u901A\u8A71");
	public JButton btnLeaveRoom = new JButton("\u96E2\u958B\u623F\u9593");
	public JButton btnLeaveWhisper = new JButton("\u96E2\u958B\u5bc6\u8ac7");
	public JButton btnInvitation = new JButton("\u9080\u8ACB\u4F7F\u7528\u8005");
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
	
	Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
	final Style mainStyle = sc.addStyle(null, defaultStyle);
	final Style boldStyle = sc.addStyle(null, defaultStyle);
	final Style greenStyle = sc.addStyle(null, defaultStyle);
	
	private final JScrollPane emoticonScroll = new JScrollPane();

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
		tabPanel.setBounds(100, 100, 903, 604);
		tabPanel.setLayout(null);
		emoticonPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		emoticonPane.setBackground(Color.WHITE);
		
		emoticonPane.setBounds(449, 160, 360, 360);
		emoticonTable.setBackground(Color.WHITE);
		emoticonTable.setBounds(0, 0, 450, 420);
		tabPanel.add(emoticonPane);
		emoticonPane.setLayout(null);
		emoticonScroll.setBounds(0, 0, 360, 360);
		emoticonPane.add(emoticonScroll);
		emoticonScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		emoticonScroll.setViewportView(emoticonTable);
		emoticonTable.setLayout(null);
		emoticonPane.setVisible(false);
		emo1.setBackground(Color.WHITE);
		emo1.setForeground(Color.WHITE);
		emo1.setIcon(new ImageIcon("images/1.png"));
		emo2.setForeground(Color.WHITE);
		emo2.setBackground(Color.WHITE);
		emo2.setIcon(new ImageIcon("images/2.png"));
		emo3.setForeground(Color.WHITE);
		emo3.setBackground(Color.WHITE);
		emo3.setIcon(new ImageIcon("images/3.png"));
		emo4.setForeground(Color.WHITE);
		emo4.setBackground(Color.WHITE);
		emo4.setIcon(new ImageIcon("images/4.png"));
		emo5.setForeground(Color.WHITE);
		emo5.setBackground(Color.WHITE);
		emo5.setIcon(new ImageIcon("images/5.png"));
		emo6.setForeground(Color.WHITE);
		emo6.setBackground(Color.WHITE);
		emo6.setIcon(new ImageIcon("images/6.png"));
		emo7.setForeground(Color.WHITE);
		emo7.setBackground(Color.WHITE);
		emo7.setIcon(new ImageIcon("images/7.png"));
		emo8.setForeground(Color.WHITE);
		emo8.setBackground(Color.WHITE);
		emo8.setIcon(new ImageIcon("images/8.png"));
		emo9.setForeground(Color.WHITE);
		emo9.setBackground(Color.WHITE);
		emo9.setIcon(new ImageIcon("images/9.png"));
		emo10.setForeground(Color.WHITE);
		emo10.setBackground(Color.WHITE);
		emo10.setIcon(new ImageIcon("images/10.png"));
		emo11.setForeground(Color.WHITE);
		emo11.setBackground(Color.WHITE);
		emo11.setIcon(new ImageIcon("images/11.png"));
		emo12.setForeground(Color.WHITE);
		emo12.setBackground(Color.WHITE);
		emo12.setIcon(new ImageIcon("images/12.png"));
		emo13.setForeground(Color.WHITE);
		emo13.setBackground(Color.WHITE);
		emo13.setIcon(new ImageIcon("images/13.png"));
		emo14.setForeground(Color.WHITE);
		emo14.setBackground(Color.WHITE);
		emo14.setIcon(new ImageIcon("images/14.png"));
		emo15.setForeground(Color.WHITE);
		emo15.setBackground(Color.WHITE);
		emo15.setIcon(new ImageIcon("images/15.png"));
		emo16.setForeground(Color.WHITE);
		emo16.setBackground(Color.WHITE);
		emo16.setIcon(new ImageIcon("images/16.png"));
		
		emo1.addMouseListener(emoMouseListener("{emo01}"));
		emo2.addMouseListener(emoMouseListener("{emo02}"));
		emo3.addMouseListener(emoMouseListener("{emo03}"));
		emo4.addMouseListener(emoMouseListener("{emo04}"));
		emo5.addMouseListener(emoMouseListener("{emo05}"));
		emo6.addMouseListener(emoMouseListener("{emo06}"));
		emo7.addMouseListener(emoMouseListener("{emo07}"));
		emo8.addMouseListener(emoMouseListener("{emo08}"));
		emo9.addMouseListener(emoMouseListener("{emo09}"));
		emo10.addMouseListener(emoMouseListener("{emo10}"));
		emo11.addMouseListener(emoMouseListener("{emo11}"));
		emo12.addMouseListener(emoMouseListener("{emo12}"));
		emo13.addMouseListener(emoMouseListener("{emo13}"));
		emo14.addMouseListener(emoMouseListener("{emo14}"));
		emo15.addMouseListener(emoMouseListener("{emo15}"));
		emo16.addMouseListener(emoMouseListener("{emo16}"));
				
		emo1.setBounds(0, 0, 90, 90);
		emo2.setBounds(90, 0, 90, 90);
		emo3.setBounds(180, 0, 90, 90);
		emo4.setBounds(270, 0, 90, 90);
		emo5.setBounds(0, 90, 90, 90);
		emo6.setBounds(90, 90, 90, 90);
		emo7.setBounds(180, 90, 90, 90);
		emo8.setBounds(270, 90, 90, 90);
		emo9.setBounds(0, 180, 90, 90);		
		emo10.setBounds(90, 180, 90, 90);		
		emo11.setBounds(180, 180, 90, 90);		
		emo12.setBounds(270, 180, 90, 90);		
		emo13.setBounds(0, 270, 90, 90);	
		emo14.setBounds(90, 270, 90, 90);		
		emo15.setBounds(180, 270, 90, 90);		
		emo16.setBounds(270, 270, 90, 90);
		
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
		
		StyleConstants.setBold(boldStyle, true);
		StyleConstants.setForeground(greenStyle, Color.GRAY);
		textPane.setBorder(null);
		textPane.addStyle("NormalMessage", mainStyle);
		textPane.addStyle("UserName", boldStyle);
		textPane.addStyle("SystemMessage", greenStyle);
		
		JLabel label = new JLabel("\u4F7F\u7528\u8005\u5217\u8868");
		label.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		label.setBounds(11, 10, 150, 25);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		tabPanel.add(label);
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cwc.username = textUsername.getText();
				textUsername.setEditable(false);
			    textChat.requestFocus();
			    btnConnect.setEnabled(false);
			    btnDisconnect.setEnabled(true);
			    btnConnect.setVisible(false);
			    btnDisconnect.setVisible(true);
			    textChat.setEnabled(true);
				new Thread(cwc.listener = new Listener(cwc)).start();
			}
		});
		btnConnect.setBounds(10, 525, 87, 23);
		tabPanel.add(btnConnect);
		
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cwc.listener.disconnect();
				cwc.listener = null;
			}
		});
		btnDisconnect.setBounds(10, 525, 87, 23);
		tabPanel.add(btnDisconnect);
		
		btnWhisper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected() || userListUI.isSelectionEmpty()) return;
				String receiver = userListUI.getSelectedValue();
				if (cwc.username.equals(receiver)) return;
				
				try {
					cwc.listener.out.writeUTF("(WhisperRequest)" + receiver);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		});
		btnWhisper.setBounds(221, 525, 104, 23);
		tabPanel.add(btnWhisper);
		
		btnChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected()) return;
				cwc.sentNewRoomReq();				
			}
		});
		btnChatroom.setBounds(335, 525, 104, 23);
		tabPanel.add(btnChatroom);
		
		btnEmoticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected()) return;
				emoticonPane.setVisible(!emoticonPane.isVisible());
			}
		});
		btnEmoticon.setBounds(449, 525, 104, 23);
		tabPanel.add(btnEmoticon);						
		
		btnRobot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected()) return;
				cwc.listener.robotMode = !cwc.listener.robotMode;
				cwc.tabs.get(0).textChat.setEnabled(!cwc.listener.robotMode);
				cwc.listener.printText(room_id, "<\u7cfb\u7d71\u8a0a\u606f> \u81ea\u52d5\u56de\u8a71\u6a21\u5f0f" + (cwc.listener.robotMode? "\u958b\u555f": "\u95dc\u9589") + "\u3002\n", "SystemMessage");
			}
		});
		btnRobot.setBounds(563, 525, 104, 23);
		tabPanel.add(btnRobot);				
		
		btnTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String receiver = null;
				if (btnLeaveWhisper.isVisible()) {
					receiver = cwc.tabbedPane.getTitleAt(cwc.tabbedPane.getSelectedIndex()).substring(5);
				}
				else {
					if (cwc.listener == null || !cwc.listener.isConnected() || userListUI.isSelectionEmpty()) return;
					receiver = userListUI.getSelectedValue();
					if (cwc.username.equals(receiver)) return;
				}
				
				try {
					cwc.listener.out.writeUTF("(IPRequest)" + receiver);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		});
		btnTransfer.setBounds(677, 525, 104, 23);
		tabPanel.add(btnTransfer);
		
		btnVoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected() || userListUI.isSelectionEmpty()) return;
				String receiver = userListUI.getSelectedValue();
				if (cwc.username.equals(receiver)) return;
				
				// TODO
				VideoChat v = new VideoChat(cwc);
				v.setTitle("Video Chat with " + receiver);
				v.askForVideoChat(receiver, cwc.username);
			}			
		});
		btnVoice.setBounds(791, 525, 104, 23);
		tabPanel.add(btnVoice);		
		
		btnLeaveRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				cwc.removeTab(room_id);
				try	{
					cwc.listener.out.writeUTF("(LeaveRoomRequest)" + room_id);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnLeaveRoom.setBounds(10, 525, 87, 23);
		btnLeaveRoom.setVisible(false);
		tabPanel.add(btnLeaveRoom);
		
		btnLeaveWhisper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				try	{
					cwc.listener.out.writeUTF("(LeaveWhisperRequest)" + room_id);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnLeaveWhisper.setBounds(10, 525, 87, 23);
		btnLeaveWhisper.setVisible(false);
		tabPanel.add(btnLeaveWhisper);
		
		btnInvitation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cwc.listener == null || !cwc.listener.isConnected()) return;
				ChatTabClient lobby = cwc.tabs.get(0);
				String receiver = (String)JOptionPane.showInputDialog(cwc.frmLabChatroom,
						                                              "\u8acb\u9078\u64c7\u9080\u8acb\u5c0d\u8c61\uff1a",
						                                              "Invitation",
						                                              JOptionPane.QUESTION_MESSAGE,
						                                              null,
						                                              lobby.userList.toArray(),
						                                              lobby.userList.firstElement());
				if (receiver == null) return;
				if (cwc.username.equals(receiver) || userList.contains(receiver)) {
					JOptionPane.showMessageDialog(cwc.frmLabChatroom, "\u8acb\u9078\u64c7\u5176\u4ed6\u4f7f\u7528\u8005\uff01", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				cwc.listener.sendInvitation(room_id, receiver);
			}			
		});
		btnInvitation.setBounds(107, 525, 104, 23);
		btnInvitation.setVisible(false);
		tabPanel.add(btnInvitation);
		
		textUsername = new JTextField();
		textUsername.setToolTipText("Please enter username");
		textUsername.setBounds(10, 562, 150, 25);
		tabPanel.add(textUsername);
		textUsername.setColumns(10);
		textChat.setBounds(172, 562, 724, 25);				
		textChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				try	{
					cwc.listener.out.writeUTF("(text%"+cwc.username+"%"+room_id+")" + textChat.getText());
				} catch (IOException ex) {
					cwc.listener.disconnect();
					JOptionPane.showMessageDialog(cwc.frmLabChatroom, "\u5931\u53bb\u8207\u4f3a\u670d\u5668\u7684\u9023\u7dda\u3002", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				textChat.setText(null);				
			}
		});		
		tabPanel.add(textChat);
		if (room_id == 0)
			textChat.setEnabled(false);
		userListUI.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		userListUI.setBounds(11, 44, 150, 300);
		tabPanel.add(userListUI);
		textPane.setBounds(172, 10, 731, 505);
		textPane.setEditable(false);
	    textScroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	    textScroll.setBounds(172, 10, 724, 501);
	    tabPanel.add(textScroll);
	    textScroll.setViewportView(textPane);	    	
	    
	    profilePicLabel.setForeground(Color.WHITE);
	    profilePicLabel.setBackground(Color.WHITE);
	    profilePicLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	    profilePicLabel.setBounds(11, 361, 150, 150);
	    tabPanel.add(profilePicLabel);
	}
	
	public MouseAdapter emoMouseListener(final String s) {
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				textChat.setText(textChat.getText()+s);
				emoticonPane.setVisible(false);
				textChat.requestFocus();
			};
		}; 
		return ma;
	}
	
	public void autoConnect(int r_id) {
		room_id = r_id;
		profilePicLabel.setIcon(cwc.userIcon);
		textUsername.setText(cwc.username);
		textUsername.setEditable(false);		
	    textChat.requestFocus();
	    btnConnect.setEnabled(false);
	    btnDisconnect.setEnabled(false);
	    btnConnect.setVisible(false);
	    btnDisconnect.setVisible(false);
		btnInvitation.setVisible(true);
		btnLeaveRoom.setVisible(true);
	    textChat.setEnabled(true);		
	}
}
