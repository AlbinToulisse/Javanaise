package irc;

import java.util.Random;

import jvn.JvnProxy;


public class Rush {
	public static void main(String argv[]) {
		try {
			Random rand = new Random();
			String title = "IRC";
			Interface_Sentence s = (Interface_Sentence) JvnProxy.newInstance(title, Sentence.class);
			while (true) {
				if (rand.nextInt(2) == 0) {
					s.read();
				} else {
					s.write(String.valueOf(rand.nextInt(100)));
				}
			}
		} catch (Exception e) {
			System.out.println("Rush2 problem : " + e.getMessage() + " / " + e.getClass());
		}
	}
}