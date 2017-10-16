package irc;

import annotations.Read;
import annotations.Write;
import jvn.JvnException;

public interface Interface_Sentence {
	@Read
	public String read() throws JvnException;
	
	@Write
	public void write(String text) throws JvnException;
}