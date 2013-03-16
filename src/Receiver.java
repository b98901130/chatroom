import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {
	public Receiver(String[] argv) throws IOException {
		/* [File transfer protocol]
		 * 1. transmitter->server: (IPRequest)username
		 * 2. server->transmitter: (IPReply)IpOfReceiver
		 * 3. transmitter->server: (FileRequest)username
		 * 4. server->receiver: (FileRequest)
		 * 5. transmitter->receiver: (FileInfo)filename%fileSize%
		 * 6. transmitter->receiver: file content
		 */
		ServerSocket servsock = new ServerSocket(25535);
	    
		// 4. after server->receiver: (FileRequest)
		Socket socket = servsock.accept();
		
		// 5. after connection is opened, transmitter should then send "(FileInfo)filename%fileSize%" to receiver
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());
		String fileInfo = inputStream.readUTF(), fileName;
		int fileSize;
		if (fileInfo.startsWith("(FileInfo)")) {
			fileName = fileInfo.substring(fileInfo.indexOf(')') + 1, fileInfo.indexOf('%'));
			fileSize = Integer.parseInt(fileInfo.substring(fileInfo.indexOf('%') + 1, fileInfo.lastIndexOf('%')));
		}
		else {
			servsock.close();
			throw new IOException("FileInfo error!");
		}
		
		// 6. after file information is received, start listening for file content
		receiveFile(fileName + "_recv", fileSize, inputStream);
		socket.close();
		servsock.close();
	}
	
	private static void receiveFile(String fileName, int fileSize, DataInputStream inputStream) throws IOException {
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