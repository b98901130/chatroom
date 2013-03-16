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
	String user;
	int room_id;
	DataOutputStream out; // client->server
	DataInputStream in;   // server->client
	ChatTabClient ctc;

	public Listener(ChatTabClient c, int r_id)
	{		
		ctc = c; 
		user = ctc.username;
		room_id = r_id;
		try
		{
			socket = new Socket("127.0.0.1", 2525);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			String userStr = in.readUTF();
			if (userStr.startsWith("(UserList_Room0)")) {
				int begin = userStr.indexOf(')') + 1;
				while (userStr.indexOf('%', begin) > 0) {
					ctc.userList.addElement(userStr.substring(begin, userStr.indexOf('%', begin)));
					System.out.println(userStr.substring(begin, userStr.indexOf('%', begin)));
					begin = userStr.indexOf('%', begin) + 1;
				}
			}
			
			if (user.length() == 0)
				user = "null";
			out.writeUTF(user);
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
					ctc.userList.removeAllElements();
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
	
	public void printText(String s){
		ctc.textPane.setEditable(true);
		ctc.textPane.setSelectionStart(ctc.textPane.getText().length());
		ctc.textPane.setSelectionEnd(ctc.textPane.getText().length());				
		ctc.textPane.replaceSelection(s);
		ctc.textPane.setEditable(false);
	}
	
	public void printIcon(String s){
		ctc.textPane.setSelectionStart(ctc.textPane.getText().length());
		ctc.textPane.setSelectionEnd(ctc.textPane.getText().length());		
		ctc.textPane.insertIcon(new ImageIcon(s));		
	}
	
	private boolean isSpecialMsg(String msg) throws IOException {
		String header = msg.substring(0, msg.indexOf(")") + 1), username, ip, fileName, filePath;
		FileDialog fd;
		
		switch (header) {
		case "(UserConnected_Room0)":
			username = msg.substring(msg.indexOf(")") + 1);
			ctc.userList.addElement(username);
			return true;
		case "(UserDisconnected_Room0)":
			username = msg.substring(msg.indexOf(")") + 1);
			ctc.userList.removeElement(username);
			return true;
		case "(IPReply)":
			// 2. server->transmitter: (IPReply)IpOfReceiver
			// 3. transmitter->server: (FileRequest)username
			username = msg.substring(msg.indexOf(")") + 1, msg.indexOf("%"));
			ip = msg.substring(msg.indexOf("%") + 1);
			out.writeUTF("(FileRequest)" + username);
			
			// use FileDialog to get filename
            fd = new FileDialog(ctc.cwc.frmLabChatroom, "Load file..", FileDialog.LOAD);
            fd.setLocationRelativeTo(ctc.tabPanel);
            
            // 5. transmitter->receiver: (FileInfo)filename%fileSize%
            Thread transThread = new Thread(new Transmitter(ip, fd, ctc.textPane));
            transThread.start();
			return true;
		case "(FileRequest)":
			// use FileDialog to get filename
            fd = new FileDialog(ctc.cwc.frmLabChatroom, "Save file..", FileDialog.SAVE);
            fd.setLocationRelativeTo(ctc.tabPanel);
            Thread recvThread = new Thread(new Receiver(fd, ctc.textPane));
            recvThread.start();
			return true;
		}
		
		return false;
	}
	
	public void parseAll(String s){
		int begin = 0;
		int end = 0;		
		IconInfo getIcon = getIconPos(s, begin);
		while (getIcon != null){					
			end = getIcon.pos;
			String cut = "";
			cut = s.substring(begin, end);
			printText(cut);
			printIcon(getIcon.name);
			begin = end+4;
			if (begin >= s.length())
				break;
			getIcon = getIconPos(s, begin);
		}
		String last = s.substring(begin)+"\n";
		printText(last);
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



