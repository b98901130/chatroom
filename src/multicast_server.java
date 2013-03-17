import java.net.*;
import java.io.*;
import java.util.*;


public class multicast_server
{
	private static ServerSocket serverSocket;
	public Hashtable<String, Hashtable<Socket, DataOutputStream>> ht_rooms = new Hashtable<String, Hashtable<Socket, DataOutputStream>>();
	public Hashtable<String, UserData> ht_user = new Hashtable<String, UserData>();
	public int room_index = 0;
	
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
				out.flush();
				
				// update userinfo
				String username = in.readUTF();
				ht_user.put(username, new UserData(username, ip, socket));

				// put new user into lobby(=room0)
				Hashtable<Socket, DataOutputStream> lobby = ht_rooms.get("Room0");
				lobby.put(socket, out);
				Thread thread = new Thread(new ServerThread(this, ht_user.get(username), 0, lobby));
				thread.start();
				
				// broadcast user connect message
				synchronized (lobby) {
					for (Enumeration<DataOutputStream> e = lobby.elements(); e.hasMoreElements();) {
						out = e.nextElement();
						try {
							out.writeUTF("(UserConnected_Room0)" + username);
							out.flush();
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
	private multicast_server master;
	private UserData userdata;
	private int room_id;
	private Hashtable<Socket, DataOutputStream> ht_room;

	public ServerThread(multicast_server ss, UserData u, int r, Hashtable<Socket, DataOutputStream> h)
	{
		this.master = ss;
		this.userdata = u;
		this.room_id = r;
		this.ht_room = h;
	}

	public void run()
	{
		DataInputStream in;

		try
		{
			in = new DataInputStream(userdata.getSocket().getInputStream());
			
			while (true)
			{
				String message = in.readUTF();
				System.out.println("Message received: " + message);

				// for normal Text, broadcast it to everyone in this room
				if (!isSpecialMsg(message)) {
					synchronized (ht_room) {
						for(Enumeration<DataOutputStream> e = ht_room.elements(); e.hasMoreElements();) {
							DataOutputStream out = (DataOutputStream)e.nextElement();
							out.writeUTF(message);
							out.flush();
						}
					}
				}
			}
		}
		catch (EOFException e) {}
		catch (SocketException e) {}
		catch (IOException e) { e.printStackTrace(); }
		finally {
			synchronized (ht_room) {
				ht_room.remove(userdata.getSocket());
				
				// broadcast user disconnect message
				for(Enumeration<DataOutputStream> e = ht_room.elements(); e.hasMoreElements();) {
					DataOutputStream out = e.nextElement();
					try {
						out.writeUTF("(UserDisconnected_Room" + room_id + ")" + userdata.getName());
						out.flush();
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			
			if (room_id == 0) {
				// close connection
				System.out.println("Remove connection " + userdata.getSocket());
				try {
					userdata.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// remove user
				synchronized (master.ht_user) {
					master.ht_user.remove(userdata.getName());
				}
			}
		}
	}
	
	private boolean isSpecialMsg(String msg) throws IOException {
		String header = msg.substring(0, msg.indexOf(")") + 1), username, ip;
		DataOutputStream out;
		
		switch (header) {
		case "(IPRequest)":
			username = msg.substring(msg.indexOf(")") + 1);
			ip = master.ht_user.get(username).getIp();
			out = new DataOutputStream(userdata.getSocket().getOutputStream());
			out.writeUTF("(IPReply)" + username + '%' + ip);
			out.flush();
			return true;
		case "(FileRequest)":
			// 4. server->receiver: (FileRequest)
			username = msg.substring(msg.indexOf(")") + 1);
			out = new DataOutputStream(master.ht_user.get(username).getSocket().getOutputStream());
			out.writeUTF("(FileRequest)");
			out.flush();
			return true;
		case "(OpenRoomRequest)":
			int newRoomId = ++master.room_index;
			username = msg.substring(msg.indexOf(")") + 1);
			out = new DataOutputStream(master.ht_user.get(username).getSocket().getOutputStream());
			out.writeUTF("(Opened_Room)"+Integer.toString(newRoomId));
			out.flush();
			System.out.println("(Opened_Room)"+Integer.toString(newRoomId));

			// create a new hashtable for new room, and insert it into ht_rooms
			Hashtable<Socket, DataOutputStream> newRoom = new Hashtable<Socket, DataOutputStream>();
			synchronized (master.ht_rooms) {
				master.ht_rooms.put("Room" + Integer.toString(newRoomId), newRoom);
			}
			
			// broadcast user connect message
			synchronized (newRoom) {
				newRoom.put(userdata.getSocket(), out);
				Thread thread = new Thread(new ServerThread(master, userdata, newRoomId, newRoom));
				thread.start();
				for (Enumeration<DataOutputStream> e = newRoom.elements(); e.hasMoreElements();) {
					out = e.nextElement();
					try {
						out.writeUTF("(UserConnected_Room" + Integer.toString(newRoomId) + ")" + username);
						out.flush();
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			return true;
		case "(LeaveRoomRequest)":
			synchronized (ht_room) {
				ht_room.remove(userdata.getSocket());
				
				// broadcast user disconnect message
				for(Enumeration<DataOutputStream> e = ht_room.elements(); e.hasMoreElements();) {
					out = e.nextElement();
					try {
						out.writeUTF("(UserDisconnected_Room" + room_id + ")" + userdata.getName());
						out.flush();
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
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