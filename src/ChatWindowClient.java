import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTabbedPane;

public class ChatWindowClient {

	public JFrame frmLabChatroom;
	public JTabbedPane tabbedPane;
	public Hashtable<Integer, ChatTabClient> tabs = new Hashtable<Integer, ChatTabClient>();
	public Listener listener;
	public String username;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ChatWindowClient window = new ChatWindowClient();
				window.frmLabChatroom.setVisible(true);
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatWindowClient() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frmLabChatroom = new JFrame();
		frmLabChatroom.setTitle("Lab1 Chatroom");
		frmLabChatroom.setBounds(100, 100, 981, 668);
		frmLabChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLabChatroom.getContentPane().setLayout(null);
		
		tabs.put(0, new ChatTabClient(this, 0));		
		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(0, 0, 965, 630);
		frmLabChatroom.getContentPane().add(tabbedPane);	
		tabbedPane.addTab("Lobby", null, tabs.get(0).tabPanel, null);				
		tabs.get(0).room_id = 0;
		//btnClose.addActionListener(myCloseActionHandler);
	}
	
	public void sentNewRoomReq() {	
		try {
			listener.out.writeUTF("(OpenRoomRequest)"+username);
			listener.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createNewRoom(int r) {
		System.out.println("start to open tab");
		tabs.put(r, new ChatTabClient(this, r));
		tabs.get(r).autoConnect(r);		
		tabbedPane.addTab("Room" + r, null, tabs.get(r).tabPanel, null);
		tabs.get(r).room_id = r;
	}
	
	public void removeAllTabs() {
		int numTabs = tabbedPane.getTabCount();
		for (int i = 1; i < numTabs; ++i)
			tabbedPane.removeTabAt(1);
		Vector<Integer> keys = new Vector<Integer>();
		for (Enumeration<Integer> e = tabs.keys(); e.hasMoreElements();) {
			int room_id = e.nextElement(); 
			if (room_id != 0)
				keys.add(room_id);
		}
		for (Enumeration<Integer> e = keys.elements(); e.hasMoreElements();)
			tabs.remove(e.nextElement());
	}
	
	public void removeTab(int room_id) {
		tabbedPane.remove(tabs.get(room_id).tabPanel);
		tabs.remove(room_id);
	}
	
}
