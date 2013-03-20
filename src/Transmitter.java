import java.awt.FileDialog;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Transmitter implements Runnable {
	private Socket socket;
	private FileDialog fileDialog;
	private Listener listener;
	
	public Transmitter(String ip, FileDialog fd, Listener l) throws IOException {
		/* [File transfer protocol]
		 * 1. transmitter->server: (IPRequest)username
		 * 2. server->transmitter: (IPReply)IpOfReceiver
		 * 3. transmitter->server: (FileRequest)username
		 * 4. server->receiver: (FileRequest)
		 * 5. transmitter->receiver: (FileInfo)filename%fileSize
		 * 6. transmitter->receiver: file content
		 */
		socket = new Socket(ip, 25535);
		fileDialog = fd;
		listener = l;
	}
	
	public void run() {
		String filePath = "", fileName = "";
		
		try {
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			outStream.flush();

			// transmitter->receiver: (FileInfo)filename%fileSize
			filePath = fileDialog.getDirectory();
			fileName = fileDialog.getFile();
			
			if (fileName.length() == 0) {
				listener.printText(0, "<System Message> transmission cancelled.\n", "SystemMessage");
			}
			else {
				long fileSize = new File(filePath + fileName).length();
				String fileInfo = "(FileInfo)" + fileName + "%" + fileSize;
				outStream.writeUTF(fileInfo);
				outStream.flush();

				// transmitter->receiver: file content
				transmitFile(filePath + fileName, fileSize, outStream);
				listener.printText(0, "<System Message> file [" + filePath + fileName + "] transmission OK!\n", "SystemMessage");
			}
			
			socket.close();
		} catch (IOException e) {
			listener.printText(0, "<System Message> transmission has been rejected.\n", "SystemMessage");
		}
	}
	
	private void transmitFile(String fileName, long fileSize, DataOutputStream outStream) throws IOException {
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