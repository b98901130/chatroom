import java.awt.EventQueue;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

public class ChatWindowClient {

	public JFrame frmLabChatroom;
	public JFrame dialogFrame;
	public JTabbedPane tabbedPane;
	public ChatTabClient tabOnFocus;
	public Hashtable<Integer, ChatTabClient> tabs = new Hashtable<Integer, ChatTabClient>();
	public Listener listener;
	public String username;
	public String server_ip;
	
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
		frmLabChatroom = new JFrame();
		frmLabChatroom.setResizable(false);
		frmLabChatroom.setTitle("Lab1 Chatroom");
		frmLabChatroom.setBounds(100, 100, 929, 658);
		frmLabChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabs.put(0, new ChatTabClient(this, 0));		
		frmLabChatroom.getContentPane().setLayout(new BoxLayout(frmLabChatroom.getContentPane(), BoxLayout.X_AXIS));
		tabbedPane = new JTabbedPane();
		frmLabChatroom.getContentPane().add(tabbedPane);	
		tabbedPane.addTab("Lobby", null, tabs.get(0).tabPanel, null);				
		tabs.get(0).room_id = 0;
	    
		while (server_ip == null) 
			server_ip = JOptionPane.showInputDialog(frmLabChatroom, "Server IP:", "127.0.0.1");
	}

	public void sentNewRoomReq() {	
		try {
			listener.out.writeUTF("(OpenRoomRequest)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createNewRoom(int r) {
		System.out.println("start to open tab" + r);
		tabs.put(r, new ChatTabClient(this, r));
		tabs.get(r).autoConnect(r);		
		tabbedPane.addTab("Room" + r, null, tabs.get(r).tabPanel, null);
		tabbedPane.setSelectedComponent(tabs.get(r).tabPanel);
		tabbedPane.getSelectedComponent().setName(Integer.toString(r));
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
	
	public int getRoomIdOnFocus() {
		return Integer.parseInt(tabbedPane.getSelectedComponent().getName());
	}
	
}
