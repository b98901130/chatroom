import java.awt.FileDialog;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.ImageIcon;

class Listener extends Frame implements Runnable
{
	
	private static final long serialVersionUID = 1L;
	Socket socket;
	DataOutputStream out; // client->server
	DataInputStream in;   // server->client
	ChatWindowClient cwc;

	public Listener(ChatWindowClient c) {		
		cwc = c; 
		try	{
			socket = new Socket("127.0.0.1", 2525);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			String userStr = in.readUTF();
			if (userStr.startsWith("(UserList_Room0)")) {
				int begin = userStr.indexOf(')') + 1;
				while (userStr.indexOf('%', begin) > 0) {
					cwc.tabs.get(0).userList.addElement(userStr.substring(begin, userStr.indexOf('%', begin)));
					System.out.println(userStr.substring(begin, userStr.indexOf('%', begin)));
					begin = userStr.indexOf('%', begin) + 1;
				}
			}
			
			if (cwc.username.length() == 0)
				cwc.username = "user_" + socket.getInetAddress().getHostAddress();
			out.writeUTF(cwc.username);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		try	{
			listen();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listen() throws IOException {
		while(true)
		{				
			String ReceivedLine = "";
			try {
				ReceivedLine = in.readUTF();
			} catch (SocketException e) {
				cwc.tabs.get(0).userList.removeAllElements();
				return;
			}
			if (!isSpecialMsg(ReceivedLine))
				parseAll(ReceivedLine);
		}
	}
	
	public void printText(int r, String s) {			
		if (cwc.tabs.get(r) != null) {
			cwc.tabs.get(r).textPane.setEditable(true);
			cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
			cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());
			cwc.tabs.get(r).textPane.replaceSelection(s);
			cwc.tabs.get(r).textPane.setEditable(false);
		}
	}
	
	public void printIcon(int r, String s) {
		cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
		cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());		
		cwc.tabs.get(r).textPane.insertIcon(new ImageIcon(s));
	}
	
	private boolean isSpecialMsg(String msg) throws IOException {
		String header = msg.substring(0, msg.indexOf(")") + 1), username, ip;
		int room_id;
		FileDialog fd;
		
		if (header.startsWith("(UserConnected_Room")) {
			System.out.println(msg);
			room_id = Integer.parseInt(header.substring(19, header.indexOf(')')));
			username = msg.substring(msg.indexOf(")") + 1);
			cwc.tabs.get(room_id).userList.addElement(username);
			return true;
		}
		else if (header.startsWith("(UserDisconnected_Room")) {
			room_id = Integer.parseInt(header.substring(22, header.indexOf(')')));
			username = msg.substring(msg.indexOf(")") + 1);
			cwc.tabs.get(room_id).userList.removeElement(username);
			return true;
		}
		else if (header.equals("(IPReply)")) {
			// 2. server->transmitter: (IPReply)IpOfReceiver
			// 3. transmitter->server: (FileRequest)username
			username = msg.substring(msg.indexOf(")") + 1, msg.indexOf("%"));
			ip = msg.substring(msg.indexOf("%") + 1);
			out.writeUTF("(FileRequest)" + username);
			
			// use FileDialog to get filename
            fd = new FileDialog(cwc.frmLabChatroom, "Load file..", FileDialog.LOAD);
            fd.setLocationRelativeTo(cwc.tabs.get(0).tabPanel);
            
            // 5. transmitter->receiver: (FileInfo)filename%fileSize%
            new Thread(new Transmitter(ip, fd, cwc.tabs.get(0).textPane)).start();
			return true;
		}
		else if (header.equals("(FileRequest)")) {
            fd = new FileDialog(cwc.frmLabChatroom, "Save file..", FileDialog.SAVE); // use FileDialog to get filename
            fd.setLocationRelativeTo(cwc.tabs.get(0).tabPanel);
            new Thread(new Receiver(fd, cwc.tabs.get(0).textPane)).start();
			return true;
		}
		else if (header.equals("(Opened_Room)")) {
			room_id = Integer.parseInt(msg.substring(msg.indexOf(")") + 1));
			cwc.createNewRoom(room_id);
			return true;
		}
		
		return false;
	}
	
	private void parseAll(String s) {
		if (!s.startsWith("(text"))
			return;
		
		int offset1 = s.indexOf("%");
		int offset2 = s.indexOf("%", offset1+1);
		int offset3 = s.indexOf(")", offset2+1);
		String name = s.substring(offset1+1, offset2);
		String room_str = s.substring(offset2+1, offset3);
		int r = Integer.parseInt(room_str);
		
		if (cwc.tabs.get(r) != null){
			cwc.tabs.get(r).textPane.setEditable(true);	
			cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
			cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());
			cwc.tabs.get(r).textPane.replaceSelection(name + ": ");
			cwc.tabs.get(r).textPane.setEditable(false);
		}
		
		int begin = offset3+1;
		int end = offset3+1;		
		IconInfo getIcon = getIconPos(s, begin);
		while (getIcon != null){					
			end = getIcon.pos;
			String cut = "";
			cut = s.substring(begin, end);
			printText(r, cut);
			printIcon(r, getIcon.name);
			begin = end+4;
			if (begin >= s.length())
				break;
			getIcon = getIconPos(s, begin);
		}
		String last = s.substring(begin)+"\n";
		printText(r, last);
	}
	
	private IconInfo getIconPos(String s, int b) {
		Vector<Integer> find = new Vector<Integer>();
		find.add(s.indexOf("{:)}", b));
		find.add(s.indexOf("{:(}", b));
		find.add(s.indexOf("{:D}", b));
		int min = 10000;
		int min_index = 0;
		for (int i = 0; i < find.size(); i++){
			if (find.get(i) <= min && find.get(i) != -1){
				min = find.get(i);			
				min_index = i;
			}			
		}
		IconInfo ret = null;
		if (min != 10000){
			if (find.get(min_index) != -1){
				switch (min_index){
					case 0:
						ret = new IconInfo("tusky.gif", find.get(min_index));
						break;
					case 1:
						ret = new IconInfo("2.png", find.get(min_index));
						break;
					case 2:
						ret = new IconInfo("3.png", find.get(min_index));
						break;
				}
			}
		}
		return ret;
	}
	
	public void registerRoom(String u_name) {
		
	}
}



