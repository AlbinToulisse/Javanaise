package irc;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import jvn.JvnObject;
import jvn.JvnServerImpl;


public class Rush2 {
	public static void main(String argv[]) {
		System.out.println("debut Rush");
		try {
			Calendar cal;
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			JvnObject object = js.jvnLookupObject("IRC1");
			if (object == null) {
				object = js.jvnCreateObject((Serializable) new Sentence());
				object.jvnUnLock();
				js.jvnRegisterObject("IRC1", object);
			}
			
			while (true) {
				object.jvnLockRead();
				String s = ((Sentence)(object.jvnGetObjectState())).read();
		        cal = Calendar.getInstance();
				System.out.println(sdf.format(cal.getTime()) + " read " + s);
				object.jvnUnLock();
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			System.out.println("Rush2 problem : " + e.getMessage() + " / " + e.getClass());
		}
	}
}