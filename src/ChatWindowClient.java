import java.awt.EventQueue;
import javax.swing.JFrame;
import java.util.Vector;
import javax.swing.JTabbedPane;

public class ChatWindowClient {

	public JFrame frmLabChatroom;
	public JTabbedPane tabbedPane;
	public Vector<ChatTabClient> tabs = new Vector<ChatTabClient>();
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
		frmLabChatroom.setBounds(100, 100, 1028, 668);
		frmLabChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLabChatroom.getContentPane().setLayout(null);
		
		tabs.add(new ChatTabClient(this));		
		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(0, 0, 1012, 630);
		frmLabChatroom.getContentPane().add(tabbedPane);		
		tabbedPane.addTab("Lobby", null, tabs.get(0).tabPanel, null);				

		//btnClose.addActionListener(myCloseActionHandler);
		
		
	}
	
	public void createNewRoom() {
		tabs.add(new ChatTabClient(this));
		tabbedPane.addTab("Room", null, tabs.get(tabs.size()-1).tabPanel, null);
	}
}
