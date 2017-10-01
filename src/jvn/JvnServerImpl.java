/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;



public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	
  // A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	private HashMap<Integer, JvnObject> objects;
	private JvnRemoteCoord coord;
	

  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		super();
		objects = new HashMap<Integer, JvnObject>();
		coord = (JvnRemoteCoord) LocateRegistry.getRegistry().lookup("coord");
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate() throws jvn.JvnException {
		try {
			coord.jvnTerminate(this);
		} catch (Exception e) {
			throw new JvnException(e.getMessage());
		}
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException { 
		try {
			int id = coord.jvnGetObjectId();
			JvnObject object = new JvnObjectImpl(id, o);
			objects.put(id, object);
			return object;
		} catch (Exception e) {
			throw new JvnException("erreur creation object");
		}
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		try {
			coord.jvnRegisterObject(jon, jo, this);
		} catch (Exception e) {
			throw new JvnException(e.getMessage());
		}
	}
	
	/**
	* Provide the reference of a JVN object being given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
		try {
			JvnObject object = coord.jvnLookupObject(jon, this);
			if (object != null) objects.put(object.jvnGetObjectId(), object);
			return object;
		} catch (Exception e) {
			throw new JvnException(e.getMessage());
		}
	}	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi) throws JvnException {
	   try {
		   return coord.jvnLockRead(joi, this);
	   } catch (Exception e) {
		   throw new JvnException(e.getMessage());
	   }	   
	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi) throws JvnException {
	   try {
		   return coord.jvnLockWrite(joi, this);
	   } catch (Exception e) {
		   throw new JvnException(e.getMessage());
	   }
	}	

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		JvnObject object = objects.get(joi);
		object.jvnInvalidateReader();
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		JvnObject object = objects.get(joi);
		return object.jvnInvalidateWriter();
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException { 
		JvnObject object = objects.get(joi);
		return object.jvnInvalidateWriterForReader();
	 }
}

 
