package irc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import jvn.JvnCoordImpl;
import jvn.JvnRemoteCoord;

public class Coord {

	public static void main(String[] args) throws Exception {
		Registry r = LocateRegistry.createRegistry(1099);
		JvnRemoteCoord coord = new JvnCoordImpl();
		r.rebind("coord", coord);
	}
}