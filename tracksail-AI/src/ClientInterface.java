
import java.rmi.Remote;
import java.rmi.RemoteException;

import java.io.*;
import java.util.*;

/**
 * Interface <code>ClientInterface</code> is an interface through which the
 * server executes remote calls to the client.
 * <p>
 * Inherits the <code>Remote</code> interface. Class <code>Tracksail</code>
 * implements the <code>ClientInterface</code> interface.
 * Thus, <code>ClientInterface</code> provides the RMI functionality of making
 * remote calls directly from the server to the client.
 * <p>
 * The server receives an object that implements <code>ClientInterface</code> as
 * a remote call from the client. The client first gets a remote object of the
 * server from RMI and then first calls the <code>loginPlayer</code> method,
 * which delivers client's own remote object to the server.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Tracksail
 * @see ServerInterface
 */
public interface ClientInterface extends Remote
{
	/**
	 * Sends a text message to the client.
	 *
	 * @param str the message to send
	 */
	public void sendText(String str) throws RemoteException;

	/**
	 * Kicks a player out.
	 * 
	 * @param message an informational message to print on the console
	 */
	public void kick(String message) throws RemoteException;

	/**
	 * Gives the client an updated race list.
	 * <p>
	 * This is called for all players if a new race is created or an
	 * existing race is closed. This is also always called for a player when
	 * (s)he signs in to the server.
	 *
	 * @param ri the race list to give
	 */
	public void updateGameList(GameInfo [] ri) throws RemoteException;

	/**
	 * Gives the client an updated track list.
	 * <p>
	 * This is called for all players always when the track list changes.
	 * This is also always called for a player when (s)he signs in to the
	 * server.
	 *
	 * @param tracks the track list to give
	 */
	public void updateTrackList(String [] tracks) throws RemoteException;

	/**
	 * Gives a player the race state when it changes (racing/paused).
	 * <p>
	 * The server calls this e.g. when a race begins or ends. Game Master
	 * can e.g. disable the "start" button when a race begins/ends.
	 *
	 * @param racing true if the race begun, false if it ended
	 */
	public void gameStateChange(boolean racing) throws RemoteException;
}
