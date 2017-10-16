package irc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jvn.JvnObject;
import jvn.JvnServerImpl;


public class Rush {
	public static void main(String argv[]) {
		try {
			Random rand = new Random();
			
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			JvnObject object = js.jvnLookupObject("IRC1");
			if (object == null) {
				object = js.jvnCreateObject((Serializable) new Sentence());
				object.jvnUnLock();
				js.jvnRegisterObject("IRC1", object);
			}
			
			while (true) {
				if (rand.nextInt(2) == 0) {
					object.jvnLockRead();
					((Sentence)(object.jvnGetObjectState())).read();
					object.jvnUnLock();
				} else {
					object.jvnLockWrite();
					((Sentence)(object.jvnGetObjectState())).write(String.valueOf(rand.nextInt(100)));
					object.jvnUnLock();
				}
			}
		} catch (Exception e) {
			System.out.println("Rush problem : " + e.getMessage() + " / " + e.getClass());
		}
	}
}