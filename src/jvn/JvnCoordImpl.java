/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;

import irc.Sentence;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	
	private HashMap<Integer, JvnObject> objects;
	private HashMap<String, Integer> names;
	private HashMap<JvnRemoteServer, HashSet<Integer>> remoteObjects;
	private HashMap<Integer, HashSet<JvnRemoteServer>> readServers;
	private HashMap<Integer, JvnRemoteServer> writeServer;
	private int id;
	
  /**
  * Default constructor
  * @throws JvnException
  **/
	public JvnCoordImpl() throws Exception {
		super();
		objects = new HashMap<Integer, JvnObject>();
		names = new HashMap<String, Integer>();
		remoteObjects = new HashMap<JvnRemoteServer, HashSet<Integer>>();
		readServers = new HashMap<Integer, HashSet<JvnRemoteServer>>();
		writeServer = new HashMap<Integer, JvnRemoteServer>();
		id = 0;
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public synchronized int jvnGetObjectId() throws java.rmi.RemoteException,jvn.JvnException {
	  return id++;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException{
	  int id = jo.jvnGetObjectId();
	  objects.put(id, jo);
	  names.put(jon, id);
	  HashSet<Integer> serverObjects = remoteObjects.get(js);
	  if (serverObjects == null) serverObjects = new HashSet<Integer>();
	  serverObjects.add(id);
	  remoteObjects.put(js, serverObjects);
	  readServers.put(id, new HashSet<JvnRemoteServer>());
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException{
	  JvnObject object = objects.get(names.get(jon));
	  if (object == null) return null;
	  if (remoteObjects.get(js) == null) remoteObjects.put(js, new HashSet<Integer>());
	  remoteObjects.get(js).add(object.jvnGetObjectId());
	  return object;
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
	   JvnRemoteServer writeMode = writeServer.get(joi);
	   Serializable data;
	   if (writeMode == null) data = objects.get(joi).jvnGetObjectState();
	   else {
		   try {
			   data = writeMode.jvnInvalidateWriterForReader(joi);
			   readServers.get(joi).add(writeMode);
			   writeServer.remove(joi);
		   } catch (Exception e) {
			   System.out.println("Server " + writeMode + " not responding, terminating");
			   jvnTerminate(writeMode);
			   data = objects.get(joi).jvnGetObjectState();
		   }
		   objects.get(joi).setData(data);
	   }
	   readServers.get(joi).add(js);
	   return data;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
	   JvnRemoteServer writeMode = writeServer.get(joi);
	   Serializable data;
	   if (writeMode == null) {
		   JvnObject object = objects.get(joi);
		   if (object != null) data = object.jvnGetObjectState();
		   else data = null;
	   } else {
		   try {
			   data = writeMode.jvnInvalidateWriter(joi);
			   writeServer.remove(joi);
		   } catch (Exception e) {
			   System.out.println("Server " + writeMode + " not responding, terminating");
			   jvnTerminate(writeMode);
			   data = objects.get(joi).jvnGetObjectState();
		   }
		   objects.get(joi).setData(data);
	   }
	   HashSet<JvnRemoteServer> readMode = readServers.get(joi);
	   if (readMode != null) {
		   for (JvnRemoteServer remoteServer : readMode) {
			   try {
				   remoteServer.jvnInvalidateReader(joi);
			   } catch (Exception e) {
				   System.out.println("Server " + remoteServer + " not responding, terminating");
				   jvnTerminate(remoteServer);
			   }
		   }
	   }
	   readServers.put(joi, new HashSet<JvnRemoteServer>());
	   writeServer.put(joi, js);
	   return data;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
    	for (Integer id : remoteObjects.get(js)) {
    		readServers.get(id).remove(js);
    		if (writeServer.get(id) != null && writeServer.get(id).equals(js)) writeServer.remove(id);
    	}
    	remoteObjects.remove(js);
    }
    
    public synchronized void jvnRemove(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
    	readServers.get(joi).remove(js);
		if (writeServer.get(joi) != null && writeServer.get(joi).equals(js)) writeServer.remove(joi);
		remoteObjects.get(js).remove(joi);
    }
    
    public synchronized void jvnAdd(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
    	remoteObjects.get(js).add(joi);
    }
}

 
