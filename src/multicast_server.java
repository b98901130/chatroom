import java.net.*;
import java.io.*;
import java.util.*;

public class multicast_server
{
	private static ServerSocket serverSocket;
	private Hashtable<String, Hashtable<Socket, DataOutputStream>> ht_rooms = new Hashtable<String, Hashtable<Socket, DataOutputStream>>();
	private Hashtable<String, UserData> ht_user = new Hashtable<String, UserData>();

	public multicast_server() throws IOException
	{
		try
		{
			serverSocket = new ServerSocket(2525);
			ht_rooms.put("Room0", new Hashtable<Socket, DataOutputStream>());
			System.out.println("Waiting for client to connect...");

			while (true)
			{
				Socket socket = serverSocket.accept();
				String ip = socket.getInetAddress().getHostAddress();
				System.out.println("Connected from client " + ip);
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				
				// send userList to the new user
				String userStr = "(UserList_Room0)";
				for (Enumeration<String> e = ht_user.keys(); e.hasMoreElements();)
					userStr += e.nextElement() + "%";
				out.writeUTF(userStr);
				
				// update userinfo
				String username = in.readUTF();
				ht_user.put(username, new UserData(username, ip, socket));

				// put new user into lobby(=room0)
				Hashtable<Socket, DataOutputStream> lobby = ht_rooms.get("Room0");
				lobby.put(socket, out);
				Thread thread = new Thread(new ServerThread(socket, 0, ht_user.get(username), lobby, ht_user));
				thread.start();
				
				// broadcast user connect message
				synchronized (lobby) {
					for (Enumeration<DataOutputStream> e = lobby.elements(); e.hasMoreElements();) {
						out = e.nextElement();
						try {
							out.writeUTF("(UserConnected_Room0)" + username);
						}
						catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		multicast_server s = new multicast_server();
	}
}

class ServerThread extends Thread implements Runnable
{
	private Socket socket;
	private int roomIndex;
	private UserData userdata;
	private Hashtable<Socket, DataOutputStream> ht_room;
	private Hashtable<String, UserData> ht_user;

	public ServerThread(Socket s, int r, UserData u, Hashtable<Socket, DataOutputStream> h, Hashtable<String, UserData> hu)
	{
		this.socket = s;
		this.userdata = u;
		this.roomIndex = r;
		this.ht_room = h;
		this.ht_user = hu;
	}

	public void run()
	{
		DataInputStream in;

		try
		{
			in = new DataInputStream(socket.getInputStream());
			
			while (true)
			{
				String message = in.readUTF();

				// for normal Text, broadcast it to everyone in this room
				if (!isSpecialMsg(message, socket)) {
					synchronized (ht_room) {
						for(Enumeration<DataOutputStream> e = ht_room.elements(); e.hasMoreElements();) {
							DataOutputStream out = (DataOutputStream)e.nextElement();
							out.writeUTF(message);
						}
					}
				}
			}
		}
		catch (IOException e) {}
		finally {
			synchronized (ht_room) {
				System.out.println("Remove connection " + socket);
				
				// broadcast user disconnect message
				for(Enumeration<DataOutputStream> e = ht_room.elements(); e.hasMoreElements();) {
					DataOutputStream out = e.nextElement();
					try {
						out.writeUTF("(UserDisconnected_Room" + roomIndex + ")" + userdata.getName());
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				
				// close connection
				ht_room.remove(socket);
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			synchronized (ht_user) {
				ht_user.remove(userdata.getName());
			}
		}
	}
	
	private boolean isSpecialMsg(String msg, Socket socket) throws IOException {
		String header = msg.substring(0, msg.indexOf(")") + 1), username, ip;
		DataOutputStream out;
		
		switch (header) {
		case "(IPRequest)":
			username = msg.substring(msg.indexOf(")") + 1);
			ip = ht_user.get(username).getIp();
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("(IPReply)" + username + '%' + ip);
			return true;
		case "(FileRequest)":
			// 4. server->receiver: (FileRequest)
			username = msg.substring(msg.indexOf(")") + 1);
			out = new DataOutputStream(ht_user.get(username).getSocket().getOutputStream());
			out.writeUTF("(FileRequest)");
			return true;
		}
		
		return false;
	}
	
}

class UserData {
	private String name;
	private String ip;
	private Socket socket;
	
	public UserData(String n, String i, Socket s)
	{
		name = n;
		ip = i;
		socket = s;
	}
	
	public String getName()	{ return name; }
	public String getIp() {	return ip; }
	public Socket getSocket() {	return socket; }
}