import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.ImageIcon;


class Listener extends Frame implements Runnable
{
	TextField chatInput = new TextField();
	Socket socket;
	int room_id;
	DataOutputStream out; // client->server
	DataInputStream in;   // server->client
	ChatWindowClient cwc;

	public Listener(ChatWindowClient c)
	{		
		cwc = c; 
		try
		{
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
				cwc.username = "null";
			out.writeUTF(cwc.username);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void run()
	{
		try
		{
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
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void printText(int r, String s){			
		cwc.tabs.get(0).textPane.setEditable(true);
		cwc.tabs.get(0).textPane.setSelectionStart(cwc.tabs.get(0).textPane.getText().length());
		cwc.tabs.get(0).textPane.setSelectionEnd(cwc.tabs.get(0).textPane.getText().length());
		cwc.tabs.get(0).textPane.replaceSelection(s);
		cwc.tabs.get(0).textPane.setEditable(false);
		if (r != 0){
			if (cwc.tabs.get(r) != null){
				cwc.tabs.get(r).textPane.setEditable(true);
				cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
				cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());
				cwc.tabs.get(r).textPane.replaceSelection(s);
				cwc.tabs.get(r).textPane.setEditable(false);
			}
		}
	}
	
	public void printIcon(int r, String s){
		cwc.tabs.get(0).textPane.setSelectionStart(cwc.tabs.get(0).textPane.getText().length());
		cwc.tabs.get(0).textPane.setSelectionEnd(cwc.tabs.get(0).textPane.getText().length());		
		cwc.tabs.get(0).textPane.insertIcon(new ImageIcon(s));
		if (r != 0){
			cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
			cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());		
			cwc.tabs.get(r).textPane.insertIcon(new ImageIcon(s));
		}
	}
	
	private boolean isSpecialMsg(String msg) throws IOException {
		String header = msg.substring(0, msg.indexOf(")") + 1), username, ip, fileName, filePath, r_id;
		FileDialog fd;
		
		switch (header) {
		case "(UserConnected_Room0)":
			username = msg.substring(msg.indexOf(")") + 1);
			cwc.tabs.get(0).userList.addElement(username);
			return true;
		case "(UserDisconnected_Room0)":
			username = msg.substring(msg.indexOf(")") + 1);
			cwc.tabs.get(0).userList.removeElement(username);
			return true;
		case "(IPReply)":
			// 2. server->transmitter: (IPReply)IpOfReceiver
			// 3. transmitter->server: (FileRequest)username
			username = msg.substring(msg.indexOf(")") + 1, msg.indexOf("%"));
			ip = msg.substring(msg.indexOf("%") + 1);
			out.writeUTF("(FileRequest)" + username);
			
			// use FileDialog to get filename
            fd = new FileDialog(cwc.frmLabChatroom, "Load file..", FileDialog.LOAD);
            fd.setLocationRelativeTo(cwc.tabs.get(0).tabPanel);
            
            // 5. transmitter->receiver: (FileInfo)filename%fileSize%
            Thread transThread = new Thread(new Transmitter(ip, fd, cwc.tabs.get(0).textPane));
            transThread.start();
			return true;
		case "(FileRequest)":
			// use FileDialog to get filename
            fd = new FileDialog(cwc.frmLabChatroom, "Save file..", FileDialog.SAVE);
            fd.setLocationRelativeTo(cwc.tabs.get(0).tabPanel);
            Thread recvThread = new Thread(new Receiver(fd, cwc.tabs.get(0).textPane));
            recvThread.start();
			return true;
		case "(Opened_Room)":
			r_id = msg.substring(msg.indexOf(")") + 1);
			room_id = Integer.parseInt(r_id);
			cwc.createNewRoom(room_id);
			return true;
		}
		
		return false;
	}
	
	public void parseAll(String s){
		int offset1 = s.indexOf("%");
		int offset2 = s.indexOf("%", offset1+1);
		int offset3 = s.indexOf(")", offset2+1);
		String name = s.substring(offset1+1, offset2);
		String room_str = s.substring(offset2+1, offset3);
		int r = Integer.parseInt(room_str);
		
		cwc.tabs.get(0).textPane.setEditable(true);
		cwc.tabs.get(0).textPane.setSelectionStart(cwc.tabs.get(0).textPane.getText().length());
		cwc.tabs.get(0).textPane.setSelectionEnd(cwc.tabs.get(0).textPane.getText().length());
		cwc.tabs.get(0).textPane.replaceSelection(name + ": ");
		cwc.tabs.get(0).textPane.setEditable(false);
		if (r != 0){
			if (cwc.tabs.get(r) != null){
				cwc.tabs.get(r).textPane.setEditable(true);	
				cwc.tabs.get(r).textPane.setSelectionStart(cwc.tabs.get(r).textPane.getText().length());
				cwc.tabs.get(r).textPane.setSelectionEnd(cwc.tabs.get(r).textPane.getText().length());
				cwc.tabs.get(r).textPane.replaceSelection(name + ": ");
				cwc.tabs.get(r).textPane.setEditable(false);
			}
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
	
	public IconInfo getIconPos(String s, int b){
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
	
	public void registerRoom(String u_name){
		
	}
}



