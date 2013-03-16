import java.awt.Frame;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;


class Listener extends Frame implements Runnable
{
	TextField chatInput = new TextField();
	Socket socket;
	String user;
	DataOutputStream out;
	DataInputStream in;
	ChatTabClient ctc;

	public Listener(ChatTabClient c)
	{		
		ctc = c; 
		user = ctc.username;
		try
		{
			//��server
			socket = new Socket("140.112.18.199", 2525);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void run()
	{
		try
		{
			while(true)
			{				
				String ReceivedLine = in.readUTF();	
				parseAll(ReceivedLine);
			}			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void printText(String s){
		ctc.textPane.setSelectionStart(ctc.textPane.getText().length());
		ctc.textPane.setSelectionEnd(ctc.textPane.getText().length());				
		ctc.textPane.replaceSelection(s);
	}
	
	public void printIcon(String s){
		ctc.textPane.setSelectionStart(ctc.textPane.getText().length());
		ctc.textPane.setSelectionEnd(ctc.textPane.getText().length());		
		ctc.textPane.insertIcon(new ImageIcon(s));		
	}
	
	public void parseAll(String s){
		int begin = 0;
		int end = 0;		
		IconInfo getIcon = getIconPos(s, begin);
		while (getIcon != null){					
			end = getIcon.pos;
			String cut = "";
			cut = s.substring(begin, end);
			printText(cut);
			printIcon(getIcon.name);
			begin = end+4;
			if (begin >= s.length())
				break;
			getIcon = getIconPos(s, begin);
		}
		String last = s.substring(begin)+"\n";
		printText(last);
	}
	
	public IconInfo getIconPos(String s, int b){
		Vector<Integer> find = new Vector<Integer>();
		find.add(s.indexOf("{:)}", b));
		find.add(s.indexOf("{:(}", b));
		find.add(s.indexOf("{:D}", b));
		int min = 10000;
		int min_index = 0;
		for (int i = 0; i < find.size(); i++){
			if (find.get(i) <= min && find.get(i) != -1){
				min = find.get(i);			
				min_index = i;
			}			
		}
		IconInfo ret = null;
		if (min != 10000){
			if (find.get(min_index) != -1){
				switch (min_index){
					case 0:
						ret = new IconInfo("1.png", find.get(min_index));
						break;
					case 1:
						ret = new IconInfo("2.png", find.get(min_index));
						break;
					case 2:
						ret = new IconInfo("3.png", find.get(min_index));
						break;
				}
			}
		}
		return ret;
	}
}



