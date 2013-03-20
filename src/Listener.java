import java.awt.FileDialog;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

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
			socket = new Socket(cwc.server_ip, 2525);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			if (cwc.username.length() == 0) {
				cwc.username = "user_" + socket.getInetAddress().getHostAddress();
				cwc.tabs.get(0).textUsername.setText(cwc.username);
			}
			out.writeUTF(cwc.username);
			
			String message = in.readUTF();
			if (message.startsWith("(UserList)"))
				parseUserList(message);
		} catch (ConnectException e) {
			disconnect();
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "Server connection error!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		while(true)
		{				
			String receivedLine = "";
			try {
				if (in == null) return;
				receivedLine = in.readUTF();
				System.out.println("Message received: " + receivedLine);
				if (!isSpecialMsg(receivedLine))
					parseAll(receivedLine);
			} catch (SocketException e) {
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatTabClient ctc = cwc.tabs.get(0);
		ctc.userList.removeAllElements();
		ctc.textUsername.setEditable(true);
		ctc.btnConnect.setEnabled(true);
		ctc.btnDisconnect.setEnabled(false);
		ctc.btnConnect.setVisible(true);
		ctc.btnDisconnect.setVisible(false);
		ctc.textChat.setEnabled(false);
		ctc.textPane.setEditable(true);
		ctc.textPane.setText("");
		ctc.textPane.setEditable(false);
		cwc.username = "";
	    cwc.removeAllTabs();
	}
	
	public void printText(int r, String s) {
		printText(r, s, "NormalMessage");
	}
	
	public void printText(int r, String s, String style) {
		if (cwc.tabs.get(r) != null) {
			JTextPane textPane = cwc.tabs.get(r).textPane;
			textPane.setEditable(true);
			textPane.setSelectionStart(textPane.getText().length());
			textPane.setSelectionEnd(textPane.getText().length());
		    textPane.setCharacterAttributes(textPane.getStyle(style), true);
			textPane.replaceSelection(s);
			textPane.setEditable(false);
		}
	}
	
	public void printIcon(int r, String s) {
		JTextPane textPane = cwc.tabs.get(r).textPane;
		textPane.setSelectionStart(textPane.getText().length());
		textPane.setSelectionEnd(textPane.getText().length());		
		textPane.insertIcon(new ImageIcon(s));
	}
	
	private boolean isSpecialMsg(String message) throws IOException {
		String header = message.substring(0, message.indexOf(")") + 1), username, ip;
		int room_id;
		FileDialog fd;
		
		switch (header) {
		case "(UserConnected)":
			room_id = Integer.parseInt(message.substring(message.indexOf(')') + 1, message.indexOf('%')));
			username = message.substring(message.indexOf("%") + 1);
			cwc.tabs.get(room_id).userList.addElement(username);
			printText(room_id, "<System Message> user [" + username + "] has joined room " + room_id + "!\n", "SystemMessage");
			return true;
		case "(UserDisconnected)":
			room_id = Integer.parseInt(message.substring(message.indexOf(')') + 1, message.indexOf('%')));
			username = message.substring(message.indexOf("%") + 1);
			cwc.tabs.get(room_id).userList.removeElement(username);
			return true;
		case "(UserList)":
			parseUserList(message);
			return true;
		case "(IPReply)":
			username = message.substring(message.indexOf(")") + 1, message.indexOf("%"));
			ip = message.substring(message.indexOf("%") + 1);
            fd = new FileDialog(cwc.dialogFrame, "Load file..", FileDialog.LOAD); // use FileDialog to get filename
			fd.setVisible(true);			
			if (fd.getFile() == null)
				printText(0, "<System Message> transmission cancelled.\n", "SystemMessage");
			else {
				out.writeUTF("(FileRequest)" + username); // transmitter->server: (FileRequest)username
				new Thread(new Transmitter(ip, fd, this)).start();
			}
			return true;
		case "(FileRequest)":
			fd = new FileDialog(cwc.dialogFrame, "Save file..", FileDialog.SAVE); // use FileDialog to get filename
            new Thread(new Receiver(fd, this)).start();
			return true;
		case "(Opened_Room)":
			room_id = Integer.parseInt(message.substring(message.indexOf(")") + 1));
			cwc.createNewRoom(room_id);
			return true;
		case "(UserNameConflict)":
			disconnect();
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "Username has been used!", "Error", JOptionPane.ERROR_MESSAGE);
			return true;
		case "(Invite_Room)":
			room_id = Integer.parseInt(message.substring(message.indexOf(')') + 1, message.indexOf('%')));
			username = message.substring(message.indexOf("%") + 1);
			int decision = JOptionPane.showConfirmDialog(cwc.frmLabChatroom,
					                                     username + " has invited you to join room " + room_id + ",\n" + "是否接受祝福?(y/n)",
					                                     "Invitation", JOptionPane.YES_NO_OPTION);
			if (decision == JOptionPane.YES_OPTION) {
				out.writeUTF("(ReceiveInvitation)" + room_id + "%" + cwc.username);
				cwc.createNewRoom(room_id);
			}
			else
				out.writeUTF("(RejectInvitation)" + username);
			return true;
		case "(RejectInvitation)":
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "ㄏㄏ 被打槍惹", "lol", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		
		return false;
	}
	
	private void parseAll(String s) throws IOException {
		if (!s.startsWith("(text"))
			throw new IOException("Invalid message: " + s);
		
		int offset1 = s.indexOf("%");
		int offset2 = s.indexOf("%", offset1+1);
		int offset3 = s.indexOf(")", offset2+1);
		String name = s.substring(offset1+1, offset2);
		String room_str = s.substring(offset2+1, offset3);
		int r = Integer.parseInt(room_str);
		
		if (cwc.tabs.get(r) != null){
			JTextPane textPane = cwc.tabs.get(r).textPane;
			textPane.setEditable(true);	
			textPane.setSelectionStart(textPane.getText().length());
			textPane.setSelectionEnd(textPane.getText().length());
		    textPane.setCharacterAttributes(textPane.getStyle("UserName"), true);
			textPane.replaceSelection(name + ": ");
			textPane.setEditable(false);
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
	
	private void parseUserList(String userList) {
		int room_id = Integer.parseInt(userList.substring(userList.indexOf(')') + 1, userList.indexOf('%'))),
			  begin = userList.indexOf('%') + 1,
				end = userList.indexOf('%', begin);
		while (end > 0) {
			cwc.tabs.get(room_id).userList.addElement(userList.substring(begin, end));
			begin = end + 1;
			end = userList.indexOf('%', begin);
		}
	}
	
	public boolean isConnected() { return socket != null; }
	
	public void sendInvitation(int room_id, String username) {
		try {
			out.writeUTF("(AddPeopleRequest)" + room_id + "%" + username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



