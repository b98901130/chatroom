import java.awt.BorderLayout;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.*;
import org.json.simple.*;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.*;

public class FBChatClient {
	private final static String appId = "284623318334487";
	private final static String appSecret = "ae277cca7a2381861fbba37640c34652";
	private final static String server = "chat.facebook.com";
	private FBChatTab tab;

	public FBChatClient(FBChatTab t) {
		tab = t;
		
		// login FB to get the OAuth token
		String token = getAccessToken();
		if (token.length() == 0) {
			tab.cwc.tabbedPane.remove(tab);
			return;
		}
		
		String url = "https://graph.facebook.com/me/friends?fields=name,picture,id&access_token=" + token;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
			JSONObject data = (JSONObject)JSONValue.parse(in.readLine());
			in.close();

			JSONArray friendList = (JSONArray)data.get("data");
			JSONObject friend = (JSONObject)friendList.get(0);
			System.out.println(friend.get("name"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getAccessToken() {
		final String url = "https://www.facebook.com/dialog/oauth?scope=xmpp_login&redirect_uri=https://www.facebook.com/connect/login_success.html&display=popup&response_type=token&client_id=" + appId;

	    NativeInterface.open();
	    BrowserThread thread = new BrowserThread(url);
	    SwingUtilities.invokeLater(thread);
	    NativeInterface.runEventPump();
	    
		return thread.getToken();
	}

}

class BrowserThread implements Runnable {
	private String url;
	private StringWrapper token = new StringWrapper();

	class StringWrapper {
		String value = "";
		boolean done = false;
	}
	
	public BrowserThread(String u) { url = u; }
	
	public void run() {
		JPanel webBrowserPanel = new JPanel(new BorderLayout());
		
		final JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(webBrowserPanel, BorderLayout.CENTER);
		frame.setSize(420, 310);
		frame.setLocationByPlatform(true);
		frame.setResizable(false);
		
		final JWebBrowser webBrowser = new JWebBrowser();
		webBrowser.navigate(url);
		webBrowser.setBarsVisible(false);
		webBrowser.addWebBrowserListener(new WebBrowserListener() {
			public void locationChanged(WebBrowserNavigationEvent event) {
				String pageUrl = event.getWebBrowser().getResourceLocation();
				if (pageUrl.startsWith("https://www.facebook.com/connect/login_success.html")) {
					token.value = pageUrl.substring(pageUrl.indexOf("access_token=") + 13, pageUrl.indexOf("&expires_in"));
					token.done = true;
					frame.setVisible(false);
					frame.dispose();
				}
			}
			public void commandReceived(WebBrowserCommandEvent event) {}
			public void locationChangeCanceled(WebBrowserNavigationEvent event) {}
			public void locationChanging(WebBrowserNavigationEvent event) {}
			public void windowClosing(WebBrowserEvent event) {}
			public void statusChanged(WebBrowserEvent event) {}
			public void titleChanged(WebBrowserEvent event) {}
			public void windowOpening(WebBrowserWindowOpeningEvent event) {}
			public void windowWillOpen(WebBrowserWindowWillOpenEvent event) {}
			public void loadingProgressChanged(WebBrowserEvent event) {}
		});
		webBrowserPanel.add(webBrowser, BorderLayout.CENTER);

		frame.setVisible(true);
	}
	
	public String getToken() { return token.value; }
	public boolean isDone() { return token.done; }
}

class FBUser {
	private String id;
	private String name;
	private String profileLink;
	private String pictureLink;
	
	public String getId() { return id; }
	public String getName() { return name; }
	public String getProfile_link() { return profileLink; }
	public String getPicture_link() { return pictureLink; }
	
	public void setId(String id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public void setProfileLink(String profileLink) { this.profileLink = profileLink; }
	public void setPictureLink(String pictureLink) { this.pictureLink = pictureLink; }
}