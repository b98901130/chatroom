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
	
	// 視訊處理相關變數
	private MediaPlayerFactory mediaPlayerFactory;
	private EmbeddedMediaPlayer localMediaPlayer;
	private EmbeddedMediaPlayer remoteMediaPlayer;
	private String mrl = "dshow://";
	// 視窗相關變數
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

		contentPane = new JPanel();

		videoPanel = new JPanel();
		videoPanel.setLayout(new GridLayout(1, 2));
		
		//local端視窗
		localCanvas = new Canvas();
		localCanvas.setBackground(Color.black);
		localCanvas.setSize(320, 180);
		localVideoSurface = mediaPlayerFactory.newVideoSurface(localCanvas);
	    localMediaPlayer.setVideoSurface(localVideoSurface);
	    localPanel = new JPanel();
	    localPanel.setBorder(new TitledBorder("Local"));
	    localPanel.setLayout(new BorderLayout(0, 8));
	    localPanel.add(localCanvas, BorderLayout.CENTER);
	    
	    //對方視窗
	    remoteCanvas = new Canvas();
	    remoteCanvas.setBackground(Color.black);
	    remoteCanvas.setSize(320, 180);
	    remoteVideoSurface = mediaPlayerFactory.newVideoSurface(remoteCanvas);
	    remoteMediaPlayer.setVideoSurface(remoteVideoSurface);
	    remotePanel = new JPanel();
	    remotePanel.setBorder(new TitledBorder("Remote"));
	    remotePanel.setLayout(new BorderLayout(0, 8));
	    remotePanel.add(remoteCanvas, BorderLayout.CENTER);
	    
	    videoPanel.add(localPanel);
	    videoPanel.add(remotePanel);
	    
	    contentPane.add(videoPanel, BorderLayout.CENTER);
	    
	    //新增視窗
	    frame = new JFrame();
	    frame.setContentPane(contentPane);
	    frame.setSize(700, 260);
	    frame.setVisible(true);
	}
	
	public void setTitle(String s)
	{
		frame.setTitle(s);
	}
	
	public void close()
	{
		System.exit(0);
	}
	
	//送出（VideoChatRequest）receiver_transmitter
	public void askForVideoChat(String receiverName, String transmitterName)
	{
		try {		
			cwc.listener.out.writeUTF("(VideoChatRequest)" + receiverName + "_" + transmitterName);
			JOptionPane.showMessageDialog(frame, "等待 "+receiverName+" 接受邀約...", "SkypeLog",JOptionPane.PLAIN_MESSAGE);
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
	
	private static String formatRtpStream(String serverAddress, int serverPort) {
	    StringBuilder sb = new StringBuilder(60);
	    sb.append(":sout=#transcode{vcodec=mp4v,vb=2048,scale=1,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display,dst=rtp{dst=");
	    sb.append(serverAddress);
	    sb.append(",port=");
	    sb.append(serverPort);
	    sb.append(",mux=ts, ttl=10}}");
	    return sb.toString();
	  }
}
