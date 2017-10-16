package irc;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import jvn.JvnException;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

public class Irc2 {
	public TextArea	text;
	public TextField data;
	Frame frame;
	Interface_Sentence sentence;

	public static void main(String argv[]) {
		try {
			String title = "IRC";
			Interface_Sentence s = (Interface_Sentence) JvnProxy.newInstance(title, Sentence.class);
			new Irc2(s, title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public Irc2(Interface_Sentence s, String title) {
		sentence = s;
		frame=new Frame(title);
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		frame.add(write_button);
		frame.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				try {
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (JvnException e1) {
					e1.printStackTrace();
				}
				frame.dispose();
				System.exit(0);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		frame.setSize(545,201);
		text.setBackground(Color.black);
		frame.setVisible(true);
	}
	
	class readListener implements ActionListener {
		Irc2 irc;
	  
		public readListener (Irc2 i) {
			irc = i;
		}
		
		public void actionPerformed (ActionEvent e) {
			try {
				String s = irc.sentence.read();
				irc.data.setText(s);
				irc.text.append(s+"\n");
			} catch (JvnException je) {
				System.out.println("IRC problem readListener : " + je.getMessage());
			}
		}
	}
	
	class writeListener implements ActionListener {
		Irc2 irc;
	  
		public writeListener (Irc2 i) {
			irc = i;
		}
		
		public void actionPerformed (ActionEvent e) {
			try {
			    String s = irc.data.getText();
				irc.sentence.write(s);
			} catch (JvnException je) {
				System.out.println("IRC problem writeListener : " + je.getMessage());
			}
		}
	}
}
