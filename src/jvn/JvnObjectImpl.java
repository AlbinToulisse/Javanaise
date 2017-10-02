package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
	private int id;
	private Serializable data;
	private State state;

	public JvnObjectImpl(int id, Serializable data) {
		this.id = id;
		this.data = data;
		this.state = State.NL;
	}
	
	public void setData(Serializable data) throws JvnException{
		this.data = data;
	}

	public void jvnLockRead() throws JvnException {
		switch(state) {
			case NL:
				data = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			case RC:
				state = State.R;
				break;
			case WC:
				state = State.RWC;
				break;
			case R:
			case W:
			case RWC:
				throw new JvnException("verrou Read deja pris");
		}
	}

	public void jvnLockWrite() throws JvnException {
		switch(state) {
			case NL:
			case RC:
			case R:
				JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			case WC:
			case RWC:
				state = State.W;
				break;
			case W:
				throw new JvnException("verrou Write deja pris");
		}
	}

	public synchronized void jvnUnLock() throws JvnException {
		switch(state) {
			case NL:
				throw new JvnException("aucun verrou pris");
			case RC:
				throw new JvnException("verrou Read deja libere");
			case WC:
				throw new JvnException("verrou Write deja libere");
			case R:
				state = State.RC;
				break;
			case W:
			case RWC:
				state = State.WC;
		}
		notify();
	}

	public int jvnGetObjectId() throws JvnException {
		return id;
	}

	public Serializable jvnGetObjectState() throws JvnException {
		return data;
	}
	
	public State JvnGetState() throws JvnException {
		return state;
	}

	public void jvnInvalidateReader() throws JvnException {
		switch(state) {
			case NL:
			case WC:
			case W:
				throw new JvnException("aucun verrou Read pris");
			case RC:
				state = State.NL;
				break;
			case R:
			case RWC:
				try {
					wait();
					state = State.NL;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		switch(state) {
			case NL:
			case RC:
			case R:
				throw new JvnException("aucun verrou Write pris");
			case WC:
				state = State.NL;
				break;
			case W:
			case RWC:
				try {
					wait();
					state = State.NL;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return jvnGetObjectState();
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		switch(state) {
			case NL:
			case RC:
			case R:
				throw new JvnException("aucun verrou Write pris");
			case WC:
				state = State.RC;
				break;
			case W:
				try {
					wait();
					state = State.RC;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case RWC:
				try {
					wait();
					state = State.R;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return jvnGetObjectState();
	}

	public void setState(State state) throws JvnException {
		this.state = state;
	}
}
