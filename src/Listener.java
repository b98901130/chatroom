import java.awt.FileDialog;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;
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
				cwc.username = generateUsername();
				cwc.tabs.get(0).textUsername.setText(cwc.username);
			}
			out.writeUTF(cwc.username);
			
			String message = in.readUTF();
			if (message.startsWith("(UserList)"))
				parseUserList(message);
		} catch (ConnectException e) {
			disconnect();
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "\u4f3a\u670d\u5668\u9023\u7dda\u5931\u6557\uff01", "Error", JOptionPane.ERROR_MESSAGE);
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
			printText(room_id, "<\u7cfb\u7d71\u8a0a\u606f> \u4f7f\u7528\u8005 " + username + " \u52a0\u5165\u4e86\u623f\u9593 " + room_id + "\u3002\n", "SystemMessage");
			return true;
		case "(UserDisconnected)":
			room_id = Integer.parseInt(message.substring(message.indexOf(')') + 1, message.indexOf('%')));
			username = message.substring(message.indexOf("%") + 1);
			cwc.tabs.get(room_id).userList.removeElement(username);
			printText(room_id, "<\u7cfb\u7d71\u8a0a\u606f> \u4f7f\u7528\u8005 " + username + " \u96e2\u958b\u4e86\u623f\u9593 " + room_id + "\u3002\n", "SystemMessage");
			return true;
		case "(UserList)":
			parseUserList(message);
			return true;
		case "(IPReply)":
			username = message.substring(message.indexOf(')') + 1, message.indexOf('%'));
			ip = message.substring(message.indexOf('%') + 1);
			fd = new FileDialog(cwc.dialogFrame, "Load file..", FileDialog.LOAD); // use FileDialog to get filename
			fd.setVisible(true);			
			if (fd.getFile() == null)
				printText(cwc.getRoomIdOnFocus(), "<\u7cfb\u7d71\u8a0a\u606f> \u53d6\u6d88\u50b3\u6a94\u3002\n", "SystemMessage");
			else {
				out.writeUTF("(FileRequest)" + username); // transmitter->server: (FileRequest)username
				new Thread(new Transmitter(ip, fd, this)).start();
			}
			return true;
		case "(WhisperRequest)":
			username = message.substring(message.indexOf(')') + 1);
			if (JOptionPane.showConfirmDialog(cwc.frmLabChatroom, username + " \u60f3\u8ddf\u4f60\u8b1b\u500b\u6084\u6084\u8a71\u5152", "Whisper", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				out.writeUTF("(OpenRoomRequest)" + username);
			else
				out.writeUTF("(RejectInvitation)" + username);
			return true;
		case "(FileRequest)":
			username = message.substring(message.indexOf(')') + 1);
			fd = new FileDialog(cwc.dialogFrame, "Save file..", FileDialog.SAVE); // use FileDialog to get filename
            new Thread(new Receiver(username, fd, this)).start();
			return true;
		case "(Opened_Room)":
			room_id = Integer.parseInt(message.substring(message.indexOf(")") + 1));
			cwc.createNewRoom(room_id);
			return true;
		case "(Opened_Whisper)":
			room_id = Integer.parseInt(message.substring(message.indexOf(")") + 1, message.indexOf('%')));
			cwc.createNewRoom(room_id);
			parseUserList(message);
			if (cwc.tabs.get(room_id).userList.firstElement().equals(cwc.username))
				cwc.tabbedPane.setTitleAt(cwc.tabbedPane.getSelectedIndex(), "with " + cwc.tabs.get(room_id).userList.lastElement());
			else
				cwc.tabbedPane.setTitleAt(cwc.tabbedPane.getSelectedIndex(), "with " + cwc.tabs.get(room_id).userList.firstElement());
			cwc.tabs.get(room_id).btnLeaveRoom.setVisible(false);
			cwc.tabs.get(room_id).btnInvitation.setVisible(false);
			cwc.tabs.get(room_id).btnWhisper.setVisible(false);
			cwc.tabs.get(room_id).btnLeaveWhisper.setVisible(true);
			return true;
		case "(UserNameConflict)":
			disconnect();
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "\u5df2\u6709\u76f8\u540c\u540d\u7a31\u4f7f\u7528\u8005\u767b\u5165\uff01", "Error", JOptionPane.ERROR_MESSAGE);
			return true;
		case "(Invite_Room)":
			room_id = Integer.parseInt(message.substring(message.indexOf(')') + 1, message.indexOf('%')));
			username = message.substring(message.indexOf("%") + 1);
			if (JOptionPane.showConfirmDialog(cwc.frmLabChatroom, username + " \u9080\u8acb\u4f60\u52a0\u5165\u623f\u9593 " + room_id + "\n\u662f\u5426\u63a5\u53d7\u795d\u798f\uff1f(y/n)", "Invitation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				out.writeUTF("(ReceiveInvitation)" + room_id + "%" + cwc.username);
				cwc.createNewRoom(room_id);
			}
			else
				out.writeUTF("(RejectInvitation)" + username);
			return true;
		case "(RejectInvitation)":
			JOptionPane.showMessageDialog(cwc.frmLabChatroom, "\u88ab\u6253\u69cd\u60f9 \u310f\u310f", "lol", JOptionPane.INFORMATION_MESSAGE);
			return true;
		case "(Close_Room)":
			room_id = Integer.parseInt(message.substring(message.indexOf(")") + 1));
			cwc.removeTab(room_id);
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
			begin = end+7;
			if (begin >= s.length())
				break;
			getIcon = getIconPos(s, begin);
		}
		String last = s.substring(begin)+"\n";
		printText(r, last);
	}
	
	private IconInfo getIconPos(String s, int b) {
		Vector<Integer> find = new Vector<Integer>();
		find.add(s.indexOf("{emo01}", b));
		find.add(s.indexOf("{emo02}", b));
		find.add(s.indexOf("{emo03}", b));
		find.add(s.indexOf("{emo04}", b));
		find.add(s.indexOf("{emo05}", b));
		find.add(s.indexOf("{emo06}", b));
		find.add(s.indexOf("{emo07}", b));
		find.add(s.indexOf("{emo08}", b));
		find.add(s.indexOf("{emo09}", b));
		find.add(s.indexOf("{emo10}", b));
		find.add(s.indexOf("{emo11}", b));
		find.add(s.indexOf("{emo12}", b));
		find.add(s.indexOf("{emo13}", b));
		find.add(s.indexOf("{emo14}", b));
		find.add(s.indexOf("{emo15}", b));
		find.add(s.indexOf("{emo16}", b));		
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
						ret = new IconInfo("images/1.png", find.get(min_index));
						break;
					case 1:
						ret = new IconInfo("images/2.png", find.get(min_index));
						break;
					case 2:
						ret = new IconInfo("images/3.png", find.get(min_index));
						break;
					case 3:
						ret = new IconInfo("images/4.png", find.get(min_index));
						break;
					case 4:
						ret = new IconInfo("images/5.png", find.get(min_index));
						break;
					case 5:
						ret = new IconInfo("images/6.png", find.get(min_index));
						break;
					case 6:
						ret = new IconInfo("images/7.png", find.get(min_index));
						break;
					case 7:
						ret = new IconInfo("images/8.png", find.get(min_index));
						break;
					case 8:
						ret = new IconInfo("images/9.png", find.get(min_index));
						break;
					case 9:
						ret = new IconInfo("images/10.png", find.get(min_index));
						break;
					case 10:
						ret = new IconInfo("images/11.png", find.get(min_index));
						break;
					case 11:
						ret = new IconInfo("images/12.png", find.get(min_index));
						break;
					case 12:
						ret = new IconInfo("images/13.png", find.get(min_index));
						break;
					case 13:
						ret = new IconInfo("images/14.png", find.get(min_index));
						break;
					case 14:
						ret = new IconInfo("images/15.png", find.get(min_index));
						break;
					case 15:
						ret = new IconInfo("images/16.png", find.get(min_index));
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
	
	private String generateUsername() {
		Random rng = new Random();
		Scanner sc = null;
		try {
			sc = new Scanner(new FileInputStream("username.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String ret = sc.nextLine();
		for (int i = 0, limit = rng.nextInt(20000); i < limit; ++i)
			ret = sc.nextLine();
		sc.close();
		return ret;
	}
}

