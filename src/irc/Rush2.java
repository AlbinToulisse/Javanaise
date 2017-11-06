package irc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jvn.JvnProxy;


public class Rush2 {
	public static void main(String argv[]) {
		System.out.println("debut Rush");
		try {
			Calendar cal;
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String title = "IRC";
			Interface_Sentence s = (Interface_Sentence) JvnProxy.newInstance(title, Sentence.class);
			while (true) {
				String read = s.read();
		        cal = Calendar.getInstance();
				System.out.println(sdf.format(cal.getTime()) + " read " + read);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			System.out.println("Rush2 problem : " + e.getMessage() + " / " + e.getClass());
		}
	}
}