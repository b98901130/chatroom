

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Transmitter {
	public Transmitter(String ip, String fileName) throws IOException {
		/* [File transfer protocol]
		 * 1. transmitter->server: (IPRequest)username
		 * 2. server->transmitter: (IPReply)IpOfReceiver
		 * 3. transmitter->server: (FileRequest)username
		 * 4. server->receiver: (FileRequest)
		 * 5. transmitter->receiver: (FileInfo)filename%fileSize%
		 * 6. transmitter->receiver: file content
		 */
		Socket socket = new Socket(ip, 25535);
		DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		outStream.flush();
		
		// 5. transmitter->receiver: (FileInfo)filename%fileSize%
		long fileSize = new File(fileName).length();
		String fileInfo = "(FileInfo)" + fileName + "%" + fileSize + "%";
		outStream.writeUTF(fileInfo);
		
		// 6. transmitter->receiver: file content
	    transmitFile(fileName, fileSize, outStream);
	    socket.close();
	}
	
	private static void transmitFile(String fileName, long fileSize, DataOutputStream outStream) throws IOException {
	    File inFile = new File(fileName);
	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
	    
		byte[] fileContent = new byte[150000];
		int bytesRead = bis.read(fileContent, 0, fileContent.length);
		
		while (bytesRead >= 0) {
			outStream.write(fileContent, 0, bytesRead);
			bytesRead = bis.read(fileContent, 0, fileContent.length);
		}
		
		bis.close();
	}
}