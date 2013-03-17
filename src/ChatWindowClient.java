import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
}
