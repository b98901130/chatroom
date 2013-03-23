import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.test.VlcjTest;

public class VideoChat {
	public ChatWindowClient cwc;
	
	// µø°T³B²z¬ÛÃöÅÜ¼Æ
	private MediaPlayerFactory mediaPlayerFactory;
	private EmbeddedMediaPlayer localMediaPlayer;
	private EmbeddedMediaPlayer remoteMediaPlayer;
	private String mrl = "dshow://";
	// µøµ¡¬ÛÃöÅÜ¼Æ
	private JFrame frame;
	private JPanel contentPane;
	private JPanel videoPanel;
	private JPanel localPanel;
	private JPanel remotePanel;
	private Canvas localCanvas;
	private Canvas remoteCanvas;
	private CanvasVideoSurface localVideoSurface;
	private CanvasVideoSurface remoteVideoSurface;

	public VideoChat(ChatWindowClient _cwc) { 
		cwc = _cwc;
		
		mediaPlayerFactory = new MediaPlayerFactory("--no-video-title-show");
		localMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		remoteMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

		//localºÝµøµ¡
		localCanvas = new Canvas();
		localCanvas.setBackground(Color.black);
		localCanvas.setSize(300, 225);
		localVideoSurface = mediaPlayerFactory.newVideoSurface(localCanvas);
	    localMediaPlayer.setVideoSurface(localVideoSurface);
	    localPanel = new JPanel();
	    localPanel.setLayout(null);
	    localPanel.add(localCanvas);
	    localPanel.setBounds(480, 355, 300, 225);
	    
	    //¹ï¤èµøµ¡
	    remoteCanvas = new Canvas();
	    remoteCanvas.setBackground(Color.black);
	    remoteCanvas.setSize(780, 580);
	    remoteVideoSurface = mediaPlayerFactory.newVideoSurface(remoteCanvas);
	    remoteMediaPlayer.setVideoSurface(remoteVideoSurface);
	    remotePanel = new JPanel();
	    remotePanel.setLayout(null);
	    remotePanel.add(remoteCanvas);
	    remotePanel.setBounds(0, 0, 790, 600);
	    
	    contentPane = new JPanel();
	    contentPane.setLayout(null);
	    contentPane.add(localPanel);
	    contentPane.add(remotePanel);
	    
	    //·s¼Wµøµ¡
	    frame = new JFrame();
	    frame.setContentPane(contentPane);
	    frame.setSize(800, 600);
	    frame.setVisible(true);
	}
	
	public void setTitle(String s)
	{
		frame.setTitle(s);
	}
	
	public void close()
	{
		localMediaPlayer.release();
		remoteMediaPlayer.release();
		System.exit(0);
	}
	
	//°e¥X¡]VideoChatRequest¡^receiver_transmitter
	public void askForVideoChat(String receiverName, String transmitterName)
	{
		try {		
			cwc.listener.out.writeUTF("(VideoChatRequest)" + receiverName + "_" + transmitterName);
			JOptionPane.showMessageDialog(frame, "µ¥«Ý "+receiverName+" ±µ¨üÁÜ¬ù...", "SkypeLog",JOptionPane.PLAIN_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendLocalVideo(String localInfo)
	{
		if(localInfo.length() > 0) {
		      String[] parts = localInfo.split(":");
		      if(parts.length == 2) {
		        String host = parts[0];
		        int port = Integer.parseInt(parts[1]);
		        
		        String[] localOptions = {
		        formatRtpStream(host, port),
		          ":no-sout-rtp-sap", 
		          ":no-sout-standard-sap", 
		          ":sout-all", 
		          ":sout-keep",
		        };

		        localMediaPlayer.playMedia(mrl, localOptions);
		      }
		}
	}
	
	public void receiveRemoteVideo(String remoteInfo)
	{
		remoteMediaPlayer.playMedia("rtp://" + remoteInfo);
	}
	
	public static String formatRtpStream(String serverAddress, int serverPort) {
	    StringBuilder sb = new StringBuilder(60);
	    sb.append(":sout=#transcode{vcodec=mp4v,vb=2048,scale=1,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display,dst=rtp{dst=");
	    sb.append(serverAddress);
	    sb.append(",port=");
	    sb.append(serverPort);
	    sb.append(",mux=ts, ttl=10}}");
	    return sb.toString();
	  }

}
