import java.awt.FileDialog;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {
	private ServerSocket servsock;
	private FileDialog fileDialog;
	private Listener listener;
	
	public Receiver(FileDialog fd, Listener l) throws IOException {
		/* [File transfer protocol]
		 * 1. transmitter->server: (IPRequest)username
		 * 2. server->transmitter: (IPReply)IpOfReceiver
		 * 3. transmitter->server: (FileRequest)username
		 * 4. server->receiver: (FileRequest)
		 * 5. transmitter->receiver: (FileInfo)filename%fileSize
		 * 6. transmitter->receiver: file content
		 */
		servsock = new ServerSocket(25535);
		fileDialog = fd;
		listener = l;
	}
	
	public void run() {
		String filePath = "", fileName = "";
		
		try {
			// after server->receiver: (FileRequest)
			Socket socket = servsock.accept();
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			
			// after connection is opened, transmitter should then send "(FileInfo)filename%fileSize" to receiver
			String fileInfo = inStream.readUTF();
			int fileSize;
			if (fileInfo.startsWith("(FileInfo)")) {
				fileName = fileInfo.substring(fileInfo.indexOf(')') + 1, fileInfo.indexOf('%'));
				fileSize = Integer.parseInt(fileInfo.substring(fileInfo.indexOf('%') + 1));
			}
			else {
				servsock.close();
				throw new IOException("FileInfo error!");
			}
			
			// choose file saving location from fileDialog
		    fileDialog.setVisible(true);
			filePath = fileDialog.getDirectory();
			fileName = fileDialog.getFile();
			
			if (fileName == null) {
				listener.printText(0, "<System Message> file rejected.\n", "SystemMessage");
			}
			else {
				// after file information is received, start listening for file content
				receiveFile(filePath + fileName, fileSize, inStream);
				listener.printText(0, "<System Message> file [" + filePath + fileName + "] received!\n", "SystemMessage");
			}
			
			socket.close();
			servsock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveFile(String fileName, int fileSize, DataInputStream inputStream) throws IOException {
		byte[] fileContent = new byte[200000];
	    FileOutputStream fos = new FileOutputStream(fileName);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
		int bytesRead = inputStream.read(fileContent);
		
		while (bytesRead >= 0) {
			bos.write(fileContent, 0, bytesRead);
			if (bytesRead < 1024) {
				bos.flush();
				break;
			}
			bytesRead = inputStream.read(fileContent);
		}
	    
	    bos.close();
	}
}