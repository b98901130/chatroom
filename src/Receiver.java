import java.awt.FileDialog;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextPane;

public class Receiver extends Thread implements Runnable {
	private ServerSocket servsock;
	private FileDialog fileDialog;
	private JTextPane textPane;
	
	public Receiver(FileDialog fd, JTextPane t) throws IOException {
		/* [File transfer protocol]
		 * 1. transmitter->server: (IPRequest)username
		 * 2. server->transmitter: (IPReply)IpOfReceiver
		 * 3. transmitter->server: (FileRequest)username
		 * 4. server->receiver: (FileRequest)
		 * 5. transmitter->receiver: (FileInfo)filename%fileSize%
		 * 6. transmitter->receiver: file content
		 */
		servsock = new ServerSocket(25535);
		fileDialog = fd;
		textPane = t;
	}
	
	public void run() {
		String filePath = "", fileName = "";
		
		try {
			// 4. after server->receiver: (FileRequest)
			Socket socket = servsock.accept();
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			
			// 5. after connection is opened, transmitter should then send "(FileInfo)filename%fileSize%" to receiver
			String fileInfo = inStream.readUTF(), extension = "";
			int fileSize;
			if (fileInfo.startsWith("(FileInfo)")) {
				fileName = fileInfo.substring(fileInfo.indexOf(')') + 1, fileInfo.indexOf('%'));
				if (fileName.lastIndexOf('.') >= 0)
					extension = fileName.substring(fileName.lastIndexOf('.'));
				fileSize = Integer.parseInt(fileInfo.substring(fileInfo.indexOf('%') + 1, fileInfo.lastIndexOf('%')));
			}
			else {
				servsock.close();
				throw new IOException("FileInfo error!");
			}
			
			// choose file saving location from fileDialog
            class ExtFilter implements FilenameFilter {
            	String extension;
            	ExtFilter(String ext) {
            		extension = ext;
            	}
                public boolean accept(File dir, String name) {
                    return (name.endsWith(extension));
                }
            };
            
			fileDialog.setAutoRequestFocus(true);
			fileDialog.setLocationByPlatform(true);
			fileDialog.setLocationRelativeTo(textPane);
			fileDialog.setFilenameFilter(new ExtFilter(extension));
            fileDialog.setVisible(true);
			filePath = fileDialog.getDirectory();
			fileName = fileDialog.getFile();

			// 6. after file information is received, start listening for file content
			receiveFile(filePath + fileName, fileSize, inStream);
			socket.close();
			servsock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String finishMsg = "System Message> file [" + filePath + fileName + "] received!\n";
		textPane.setSelectionStart(textPane.getText().length());
		textPane.setSelectionEnd(textPane.getText().length());				
		textPane.replaceSelection(finishMsg);
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