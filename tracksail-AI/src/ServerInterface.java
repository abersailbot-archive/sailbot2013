
import java.rmi.Remote;
import java.rmi.RemoteException;

import java.io.*;
import java.util.*;

/**
 * Interface <code>ServerInterface</code> is an interface through which the
 * client (<code>Tracksail</code>) executes remote calls to the server.
 * <p>
 * Inherits the <code>Remote</code> interface. Class <code>Server</code>
 * implements the <code>ServerInterface</code> interface.
 * Thus, <code>ServerInterface</code> provides the RMI functionality of making
 * remote calls directly from the client to the server.
 * <p>
 * The client receives an object that implements <code>ServerInterface</code>
 * from RMI (returned by the <code>Naming.lookup(...)</code> method). After
 * this, the client can directly call the <code>ServerInterface</code> methods
 * of the received object and the calls are transferred to the server which has
 * implemented the methods in question.
 * <p>
 * First we call the <code>loginPlayer</code> method, which receives as it's
 * parameter, among others, a client object that implements the
 * <code>ClientInterface</code> interface. The object is stored on the server.
 * The reason behind this is that the server can use it to call the services of
 * the client directly.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Server
 * @see ClientInterface
 */
public interface ServerInterface extends Remote
{
	/**
	 * Logs the player in to the server.
	 *
	 * @param name player name
	 * @param cobj client's player object
	 * @return The unique id for this player.
	 */
	public int loginPlayer(String name,ClientInterface cobj) throws RemoteException;

	/**
	 * Logs the player out from the server.
	 *
	 * @param player_id player id
	 */
	public void logoutPlayer(int player_id) throws RemoteException;

	/**
	 * Places the player in a race.
	 *
	 * @param player_id player id
	 * @param game_id race id
	 * @return true if the player successfully joined the race, false otherwise
	 */
	public boolean joinGame(int player_id,int game_id) throws RemoteException;

	/**
	 * Creates a new race and places the player in it.
	 *
	 * @param player_id player id
	 * @param game_name name of the race to create
	 * @return -1 if failed, game_id(&lt;=0) if a new race was created and
	 *         the player placed in it.
	 */
	public int createGame(int player_id,String game_name) throws RemoteException;

	/**
	 * Exits a race.
	 *
	 * @param player_id player id
	 * @param game_id race id
	 */
	public void leaveGame(int player_id,int game_id) throws RemoteException;

	/**
	 * Starts racing in a race.
	 * <p>
	 * Only race master calls this.
	 *
	 * @param game_id race id
	 */
	public void startGame(int game_id) throws RemoteException;

	/**
	 * Turns a boat portboard.
	 *
	 * @param player_id player id
	 */
	public void turnLeft(int player_id) throws RemoteException;

	/**
	 * Turns a boat starboard.
	 *
	 * @param player_id player id
	 */
	public void turnRight(int player_id) throws RemoteException;

	/**
	 * Adjusts a sail.
	 *
	 * @param player_id player id
	 * @param sail new sail value
	 */
	public void setSail(int player_id,int sail) throws RemoteException;

	/**
	 * Returns the specified <code>Game</code> object.
	 *
	 * @param game_id race id
	 * @return <code>Game</code> object.
	 */
	public Game getGame(int game_id) throws RemoteException;

	/**
	 * Returns the specified <code>Player</code> object.
	 *
	 * @param player_id player id
	 * @return <code>Player</code> object.
	 */
	public Player getPlayer(int player_id) throws RemoteException;

	/**
	 * Sends a chat message to everybody.
	 *
	 * @param message chat message
	 */
	public void sayToAll(String message) throws RemoteException;

	/**
	 * Sends a chat message to everybody in the lobby room.
	 *
	 * @param message chat message
	 */
	public void sayToLobby(String message) throws RemoteException;

	/**
	 * Sends a chat message to the specified race.
	 *
	 * @param game_id the race to send the message to
	 * @param message chat message
	 */
	public void sayToGame(int game_id,String message) throws RemoteException;

	/**
	 * Loads a new track to a race (if it's not in a racing state).
	 *
	 * @param game_id race id
	 * @param track_name the name of the track to load
	 */
	public void changeTrack(int game_id,String track_name) throws RemoteException;
}
