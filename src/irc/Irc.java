/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;


import jvn.*;
import java.io.*;


public class Irc {
	public TextArea		text;
	public TextField	data;
	Frame 			frame;
	JvnObject       sentence;


  /**
  * main method
  * create a JVN object named IRC for representing the Chat application
  **/
	public static void main(String argv[]) {
	   try {
		   
		// initialize JVN
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		
		// look up the IRC object in the JVN server
		// if not found, create it, and register it in the JVN server
		JvnObject jo1 = js.jvnLookupObject("IRC1");
		if (jo1 == null) {
			jo1 = js.jvnCreateObject((Serializable) new Sentence());
			// after creation, I have a write lock on the object
			jo1.jvnUnLock();
			js.jvnRegisterObject("IRC1", jo1);
		}
		new Irc(jo1, "IRC1");
		
		//Ouverture second IRC
		JvnObject jo2 = js.jvnLookupObject("IRC2");
		if (jo2 == null) {
			jo2 = js.jvnCreateObject((Serializable) new Sentence());
			jo2.jvnUnLock();
			js.jvnRegisterObject("IRC2", jo2);
		}
		new Irc(jo2, "IRC2");
	   
	   } catch (Exception e) {
		   System.out.println("IRC problem main : " + e.getMessage() + " / " + e.getClass());
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public Irc(JvnObject jo, String title) {
		sentence = jo;
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
}

 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListener implements ActionListener {
	Irc irc;
  
	public readListener (Irc i) {
		irc = i;
	}
   
 /**
  * Management of user events
  **/
	public void actionPerformed (ActionEvent e) {
	 try {
		// lock the object in read mode
		irc.sentence.jvnLockRead();

		// invoke the method
		String s = ((Sentence)(irc.sentence.jvnGetObjectState())).read();

		// unlock the object
		irc.sentence.jvnUnLock();
		
		// display the read value
		irc.data.setText(s);
		irc.text.append(s+"\n");
	   } catch (JvnException je) {
		   System.out.println("IRC problem readListener : " + je.getMessage());
	   }
	}
}

 /**
  * Internal class to manage user events (write) on the CHAT application
  **/
 class writeListener implements ActionListener {
	Irc irc;
  
	public writeListener (Irc i) {
        	irc = i;
	}
  
  /**
    * Management of user events
   **/
	public void actionPerformed (ActionEvent e) {
	   try {	
		// get the value to be written from the buffer
    String s = irc.data.getText();
        	
    // lock the object in write mode
		irc.sentence.jvnLockWrite();
		
		// invoke the method
		((Sentence)(irc.sentence.jvnGetObjectState())).write(s);
		
		// unlock the object
		irc.sentence.jvnUnLock();
	 } catch (JvnException je) {
		   System.out.println("IRC problem writeListener : " + je.getMessage());
	 }
	}
}



