import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;

public class FBChatTab extends JPanel {

	private static final long serialVersionUID = 1L;
	public final ChatWindowClient cwc;
	
	public JPanel tabPanel;
	public JTextField textChat = new JTextField();
	public DefaultListModel<String> userList = new DefaultListModel<String>();
	public JList<String> userListUI = new JList<String>(userList);
	public JLabel label = new JLabel("\u597d\u53cb\u5217\u8868");

	public StyleContext sc = new StyleContext();
	Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
	final Style mainStyle = sc.addStyle(null, defaultStyle);
	final Style boldStyle = sc.addStyle(null, defaultStyle);
	final Style grayStyle = sc.addStyle(null, defaultStyle);
	final Style boldGrayStyle = sc.addStyle(null, defaultStyle);
	
	public JTextPane textPane = new JTextPane(new DefaultStyledDocument(sc));
	public JScrollPane textScroll = new JScrollPane();
	public JButton btnLeavePage = new JButton("\u96E2\u958B");

	public FBChatTab(ChatWindowClient c) {
		this.cwc = c;
		
		tabPanel = new JPanel();		
		tabPanel.setBounds(100, 100, 903, 604);
		tabPanel.setLayout(null);
		
		StyleConstants.setBold(boldStyle, true);
		StyleConstants.setBold(boldGrayStyle, true);
		StyleConstants.setForeground(mainStyle, Color.BLACK);
		StyleConstants.setForeground(boldStyle, Color.BLACK);
		StyleConstants.setForeground(grayStyle, Color.GRAY);
		StyleConstants.setForeground(boldGrayStyle, Color.DARK_GRAY);
		textPane.addStyle("NormalMessage", mainStyle);
		textPane.addStyle("UserName", boldStyle);
		textPane.addStyle("SystemMessage", grayStyle);
		textPane.addStyle("FriendName", boldGrayStyle);
		textPane.setBorder(null);
		
		label.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		label.setBounds(11, 10, 150, 25);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		tabPanel.add(label);
		
		btnLeavePage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				cwc.tabbedPane.remove(tabPanel);
			}
		});
		btnLeavePage.setBounds(74, 546, 87, 25);
		tabPanel.add(btnLeavePage);
		
		textChat.setBounds(172, 546, 724, 25);				
		textChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				textChat.setText(null);				
			}
		});		
		tabPanel.add(textChat);
	    textChat.requestFocus();
		
		userListUI.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		userListUI.setBounds(11, 44, 150, 492);
		tabPanel.add(userListUI);
		
		textPane.setBounds(172, 10, 731, 505);
		textPane.setEditable(false);
	    textScroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	    textScroll.setBounds(172, 10, 724, 526);
	    tabPanel.add(textScroll);
	    textScroll.setViewportView(textPane);	    	
	    
	    JLabel fb_label = new JLabel("");
	    fb_label.setBounds(11, 544, 50, 50);
		BufferedImage img_scaled = null, img = null;
		try {
			img_scaled = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
			img = ImageIO.read(new URL("http://profile.ak.fbcdn.net/hprofile-ak-snc6/c13.12.160.160/281468_259870444030331_5568518_n.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		img_scaled.createGraphics().drawImage(img, 0, 0, 50, 50, null);
		fb_label.setIcon(new ImageIcon(img_scaled));
	    tabPanel.add(fb_label);
	}
	
}
