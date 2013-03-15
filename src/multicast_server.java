import java.net.*;
import java.io.*;
import java.util.*;

public class multicast_server
{
	private static ServerSocket serverSocket;
	private Hashtable ht = new Hashtable(); //蝞∠��lient�喃�����	
	Socket socket;

	public multicast_server() throws IOException
	{
		try
		{
			//�私ort2525撱箇�銝��ServerSocket
			serverSocket = new ServerSocket(2525);
			System.out.println("Waiting for client to connect...");

			//銝���,�舫��client撱箇����
			while(true)
			{
				socket = serverSocket.accept();
				System.out.println("Connected from client " + socket.getInetAddress().getHostAddress());
				
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				ht.put(socket, out);
				Thread thread = new Thread(new ServerThread(socket, ht));
				thread.start();
			}
		}
		catch(IOException ex)
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
	private Hashtable ht;

	public ServerThread(Socket s, Hashtable h)
	{
		this.socket = s;
		this.ht = h;
	}

	public void run()
	{
		DataInputStream in;

		try
		{
			in = new DataInputStream(socket.getInputStream());
			
			//�芾��lient�單���舐策server,撠勗��策�嗡�client
			while(true)
			{
				String message = in.readUTF();

				synchronized(ht) {
					for(Enumeration e = ht.elements(); e.hasMoreElements();) {
						DataOutputStream out = (DataOutputStream)e.nextElement();

						try {
							out.writeUTF(message);
						}
						catch(IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
		catch(IOException ex) {}
		//憒��lient蝯����撠望��瑁�甇文�憛�撘Ⅳ
		finally {
				synchronized(ht) {
				System.out.println("Remove connection" + socket);

				ht.remove(socket);
				try{
				socket.close();
				}
				catch(IOException ex) {}
			}
		}
	}
}
