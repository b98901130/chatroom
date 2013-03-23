import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import org.json.simple.*;

public class FBChatClient implements Runnable {

	private final static String appId = "284623318334487";
	private final static String appSecret = "ae277cca7a2381861fbba37640c34652";
	private final static String server = "chat.facebook.com";
	private FBChatTab tab;
	private Hashtable<String, FBUser> friendList = new Hashtable<String, FBUser>();
	private String accessToken = "";
	
	public FBChatClient(FBChatTab t) {
		tab = t;
	}
	
	public void run() {
		tab.cwc.browserFrame.setVisible(true); // invoke FB login dialog to get the OAuth token
	}
	
	public void setAccessToken(String t) { 
		accessToken = t;
		getFriendList();
	}
	
	@SuppressWarnings("unchecked")
	public void getFriendList() {
		tab.logged_in = true;
		
		String url = "https://graph.facebook.com/me/friends?fields=name,picture,id&access_token=" + accessToken;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
			JSONObject data = (JSONObject)JSONValue.parse(in.readLine());
			in.close();

			JSONArray friendArray = (JSONArray)data.get("data");
			for (ListIterator<JSONObject> e = friendArray.listIterator(); e.hasNext();) {
				FBUser friend = new FBUser(e.next());
				friendList.put(friend.getName(), friend);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> list = Collections.list(friendList.keys());
        Collections.sort(list);
        for (String name : list)
        	tab.userList.addElement(name);
	}
}


class FBUser {
	private String id;
	private String name;
	private String picture;
	
	@Override
	public String toString() {
		return "FBUser [id=" + id + ", name=" + name + ", picture=" + picture + "]";
	}

	public FBUser(JSONObject obj) {
		id = (String)obj.get("id");
		name = (String)obj.get("name");
		obj = (JSONObject)obj.get("picture");
		obj = (JSONObject)obj.get("data");
		picture = (String)obj.get("url");
	}
	
	public String getId() { return id; }
	public String getName() { return name; }
	public String getPicture() { return picture; }
}