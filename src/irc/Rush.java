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
			List<JvnObject> objects = new ArrayList<JvnObject>();
			Random rand = new Random();
			int nb_object = 3;
			
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			for (int i = 0; i < nb_object; i++) {
				JvnObject object = js.jvnLookupObject("object" + i);
				if (object == null) {
					object = js.jvnCreateObject((Serializable) new Sentence());
					object.jvnUnLock();
					js.jvnRegisterObject("object" + i, object);
				}
				objects.add(object);
			}
			
			while (true) {
				int id = rand.nextInt(nb_object);
				JvnObject object = objects.get(id);
				if (rand.nextInt(2) == 0) {
					object.jvnLockRead();
					String s = ((Sentence)(object.jvnGetObjectState())).read();
					System.out.println("lecture: " + s + ", object: " + id);
					object.jvnUnLock();
				} else {
					object.jvnLockWrite();
					String s = String.valueOf(rand.nextInt(100));
					((Sentence)(object.jvnGetObjectState())).write(s);
					System.out.println("ecriture: " + s + ", object: " + id);
					object.jvnUnLock();
				}
			}
		} catch (Exception e) {
			System.out.println("Rush problem : " + e.getMessage() + " / " + e.getClass());
		}
	}
}