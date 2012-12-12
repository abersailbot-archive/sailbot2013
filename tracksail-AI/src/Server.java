import java.io.*;
import java.net.*;
import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;


/**
 * Class <code>Server</code> implements the server-side of RMI.
 * <p>
 * This includes all the players, races and tracks. Their creation, deletion,
 * joining, loading and changing.
 * <p>
 * Implements <code>ServerInterface</code> RMI interface. When the client
 * program is run and it joins to the server, <code>ServerInterface</code> is
 * the interface through which the client can communicate with the server. The
 * client receives an object that implements the <code>ServerInterface</code>
 * and can make direct method invocations to the server through the methods in
 * this interface.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see ServerInterface
 * @see Player
 * @see Game
 * @see ServerCommandHandler
 */
public class Server extends UnicastRemoteObject implements ServerInterface
{

	public static final String version = "Tracksail server version 1.0-rc2";

	/** Registry */
	private static Registry registry;






	/* *****************************************************************************
	 * PLAYERS
	 **************************************************************************** */


	/** A list of the players that are on the server */
	private ArrayList players;

	/**
	 * Adds a player to the list on the server.
	 *
	 * @param p the handler object of the player to add
	 */
	public synchronized void addPlayer(Player p) { players.add(p); }

	/**
	 * Removes a player from the list on the server.
	 *
	 * @param p the handler object of the player to remove
	 */
	public synchronized void removePlayer(Player p) { players.remove(p); }

	/**
	 * Returns the number of players on the server.
	 *
	 * @return The number of players on the server.
	 */
	public int getNumPlayers() { return players.size(); }

	/**
	 * Returns an iterator for the players.
	 *
	 * @return Player iterator.
	 */
	public Iterator getPlayersIterator() { return players.iterator(); }

	/**
	 * Returns the player with the given id.
	 *
	 * @param player_id the id of the player to return
	 * @return The player object with the given id or null if it doesn't
	 *         exist.
	 */
	public synchronized Player getPlayerById(int player_id)
	{
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			if( p.getId() == player_id ) return p;
		}
		return null;
	}


	/* *****************************************************************************
	 * RACES
	 **************************************************************************** */

	// FIXME: We should probably use the word "race" instead of "game".
	// (post-1.0)

	/** List of the races on the server. */
	private ArrayList games;

	/**
	 * Adds a race to the list on the server.
	 * 
	 * @param p reference to the race to add
	 */
	public synchronized void addGame(Game g) { games.add(g); gameListChanged(); }

	/**
	 * Removes a race from the list on the server.
	 * 
	 * @param p reference to the race to remove
	 */
	public synchronized void removeGame(Game g) { games.remove(g); gameListChanged(); }

	/**
	 * Returns the number of races.
	 * 
	 * @return The number of races.
	 */
	public int getNumGames() { return games.size(); }

	/**
	 * Returns an iterator for the races.
	 * 
	 * @return Race iterator.
	 */
	public Iterator getGamesIterator() { return games.iterator(); }

	/**
	 * Returns the race with the given id.
	 *
	 * @param id the id of the race to return
	 * @return Reference to the player to return.
	 */
	public synchronized Game getGameById(int id)
	{
		Iterator it = getGamesIterator();
		while(it.hasNext())
		{
			Game r = (Game)it.next();
			if( r.getId()==id ) return r;
		}
		return null;
	}

	/**
	 * Sends an updated race list to all players.
	 * <p>
	 * This is executed when the race list has changed, i.e. a new race is
	 * created or an existing race is closed. Also executed when the state
	 * of a race is changed!
	 *
	 * @see Game.java
	 */
	public synchronized void gameListChanged()
	{
		GameInfo [] ri = getGameList();

		// Send the list to all players.
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			try{ p.getClient().updateGameList(ri); }
			catch(RemoteException re) {}
		}
	}

	/**
	 * Collects a race list.
	 * 
	 * @return An array with information of each race.
	 */
	public synchronized GameInfo [] getGameList()
	{
		GameInfo [] ri = new GameInfo[getNumGames()];
		Iterator it = getGamesIterator();
		int i=0;
		while(it.hasNext() && i<ri.length)
		{
			ri[i++] = ((Game)it.next()).getInfo();
		}
		return ri;
	}


	/* *****************************************************************************
	 * UNIQUE ID
	 **************************************************************************** */


	/**
	 * Counter for the unique id.
	 * <p>
	 * Each new player and race receives a new, unused
	 * <code>unique_id</code> value.
	 */
	private static int next_unique_id = 100;

	/**
	 * Returns the next unused unique_id.
	 *
	 * @return next unused unique_id
	 */
	public int getNextUniqueId() { return ++next_unique_id; }


	/* ****************************************************************************
	 * TRACKS
	 *************************************************************************** */

	/** A list of the names of the tracks that can be used. */
	private String [] track_list;

	/**
	 * Updates the track list by reading the names of all tracks from the 
	 * <code>tracklist</code> file in the <code>tracks</code> directory.
	 */
	public void updateTrackList()
	{
		Vector lines = new Vector();

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("../tracks/tracklist")));
			String ln;
			while ( ( ln = in.readLine ()) != null)
			{
				if ( ln.length()>1 ) { lines.addElement(ln); }
			}
		}
		catch ( Exception e)
		{
			System.out.println ("Could not open file: " + e.toString ());
			track_list = new String[1];
			track_list[0] = "default";
			return;
		}

		track_list = new String[lines.size()];

		System.out.println("Updating track list...");
		for(int i=0; i<lines.size(); i++)
		{
			track_list[i] = (String)lines.elementAt(i);
			System.out.println("Track: " + track_list[i]);
		}
		System.out.println("");

		// Send the list to all players.
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			try{ p.getClient().updateTrackList(track_list); }
			catch(RemoteException re) {}
		}

	}


	/* ***********************************************************************************
	 * MAIN
	 *********************************************************************************** */

	/**
	 * Main.
	 *
	 * @param args command line parameters given
	 */
	public static void main(String [] args)
	{

        // Creates and sets a security manager.
        if (System.getSecurityManager() == null)
        {
			System.setSecurityManager(new RMISecurityManager());
        }

        try
        {
			Server obj = new Server();

			// The server wants to be enabled.
			registry = LocateRegistry.getRegistry();
			registry.bind("TS_Server", obj);


			System.out.println("TS_Server bound in registry");

			ServerCommandHandler ch = new ServerCommandHandler (obj);
			ch.printHelp ();

			ch.run(); // execute the loop until the server is closed with the "exit" command

			obj.cleanup();

        }
        catch (Exception e)
        {
			System.out.println("Server error: " + e.getMessage());
			e.printStackTrace();
        }
		finally
		{
			// If this isn't done, the program can't be rebound to the registry
			// without closing and rerunning the rmiregistry.
			try
			{
				if( registry!=null ) registry.unbind("TS_Server");
				System.out.println("Server unbound from registry.");
			}
			catch(Exception re) {}

			System.out.println("Server closed.");
		}

		System.exit(0);
	}


	/**
	 * The constructor for the server.
	 */
	public Server() throws RemoteException
	{
		super(); // Call the UnicastRemoteObject constructor

		// Create an empty player list.
		players = new ArrayList();

		// Create an empty race list.
		games = new ArrayList();

		updateTrackList();
	}


	/**
	 * Kicks the players from the server and ends all races.
	 */
	public synchronized void cleanup()
	{
		// Kick all the players from the server.
		// If this isn't done, all the players in the lobby will stay in
		// the program until they do something that tries to use the
		// connection.
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			try { p.getClient().kick("Server closed."); }
			catch(RemoteException re) {}
		}

		// End all races.
		it = getGamesIterator();
		while(it.hasNext())
		{
			Game g = (Game)it.next();
			g.quit();
		}
	}


	/* ************************************************************************************
	 * REMOTE METHODS: ServerInterface
	 *********************************************************************************** */

	/**
	 * Logs the player in to the server.
	 *
	 * @param name player name
	 * @param cobj client's player object
	 * @return The unique id for this player.
	 */
	public int loginPlayer(String name,ClientInterface cobj) throws RemoteException
	{
		int id = this.getNextUniqueId();

		Player p = new Player(id,name,cobj);

		this.addPlayer(p);

		try {
			// Tell the player how many players are currently on the server
			p.getClient().sendText("Welcome! There are currently "+getNumPlayers()+" players logged.");
			p.getClient().updateGameList(this.getGameList()); // Give the player the list of available races.
			p.getClient().updateTrackList(this.track_list); // Give the player the list of available tracks
		}
		catch(RemoteException re) {}

		return id;
	}

	/**
	 * Logs the player out from the server.
	 *
	 * @param player_id player id
	 */
	public void logoutPlayer(int player_id) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			this.removePlayer(p);
		}
	}

	/**
	 * Places the player in a race.
	 *
	 * @param player_id player id
	 * @param game_id race id
	 * @return true if the player successfully joined the race, false otherwise.
	 */
	public boolean joinGame(int player_id,int game_id) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			Game r = this.getGameById(game_id);
			if( r!=null )
			{
				if( r.isRacing() )
				{
					// If the race is in racing state, we
					// can't join it.
					p.getClient().sendText("Game still racing!");
					return false;
				}

				if( r.addPlayer(p) )
				{
					p.setGame(r);
					gameListChanged(); // The number of players changed, update.
					return true; // ok!
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new race and places the player in it.
	 *
	 * @param player_id player id
	 * @param game_name name of the race to create
	 * @return -1 if failed, game_id(&lt;=0) if a new race was created and
	 *         the player placed in it.
	 */
	public int createGame(int player_id,String game_name) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			int id = getNextUniqueId();

			Game game = new Game( id, game_name, player_id, this );

			// Start the race thread.
			Thread th = new Thread(game);
			th.setDaemon(true);
			th.start();

			if( game.addPlayer(p) )
			{
                                SimulatorServer st = new SimulatorServer(this,player_id);
                                st.start();
				p.setGame(game);
				// Insert the race to the server's race list
				addGame(game);
				// now the race has been created and the player
				// placed in it
				return id;
			}
		}
		return -1;
	}

	/**
	 * Exits a race.
	 *
	 * @param player_id player id
	 * @param game_id race id
	 */
	public void leaveGame(int player_id,int game_id) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			p.setGame(null);

			Game r = this.getGameById(game_id);
			if( r!=null )
			{
				r.removePlayer(p); // remove player from the race

				// If the player_id is the race master's id,
				// close the race. It will stay on and close
				// down when the race is empty of players. New
				// players can't join the race since it's not
				// shown in the race list any longer.
				if( player_id == r.getMasterId() )
				{
					removeGame(r);
					r.quit(); // end the race
				}
				else { // Note: above removeGame does this!
					gameListChanged(); // number of the players has changed, update
				}
			}
		}
	}

	/**
	 * Starts racing in a race.
	 * <p>
	 * Only race master calls this.
	 *
	 * @param game_id race id
	 */
	public void startGame(int game_id) throws RemoteException
	{
		Game r = this.getGameById(game_id);
		if( r!=null )
		{
			// start race only if we're not currently racing
			if( !r.isRacing() )
			{
				r.startGame();
			}
		}
	}

	/**
	 * Turns a boat portboard.
	 *
	 * @param player_id player id
	 */
	public void turnLeft(int player_id) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			p.turnLeft();
		}
	}
	/**
	 * Turns a boat starboard.
	 *
	 * @param player_id player id
	 */
	public void turnRight(int player_id) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			p.turnRight();
		}
	}
	/**
	 * Adjusts a sail.
	 *
	 * @param player_id player id
	 * @param sail new sail value
	 */
	public void setSail(int player_id,int sail) throws RemoteException
	{
		Player p = this.getPlayerById(player_id);
		if( p != null )
		{
			p.setSailValue(sail);
		}
	}

	/**
	 * Returns the specified <code>Game</code> object.
	 *
	 * @param game_id race id
	 * @return <code>Game</code> object.
	 */
	public Game getGame(int game_id) throws RemoteException
	{
		return getGameById(game_id);
	}

	/**
	 * Returns the specified <code>Player</code> object.
	 *
	 * @param player_id player id
	 * @return <code>Player</code> object.
	 */
	public Player getPlayer(int player_id) throws RemoteException
	{
		return getPlayerById(player_id);
	}


	/**
	 * Sends a chat message to everybody.
	 *
	 * @param message chat message
	 */
	public synchronized void sayToAll(String message) throws RemoteException
	{
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			p.getClient().sendText(message);
		}
	}

	/**
	 * Sends a chat message to everybody in the lobby room.
	 *
	 * @param message chat message
	 */
	public void sayToLobby(String message) throws RemoteException
	{
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			// only send a message to players who are not in a race
			if( p.getGame()==null ) p.getClient().sendText(message);
		}
	}


	/**
	 * Sends a chat message to the specified race.
	 *
	 * @param game_id the race to send the message to
	 * @param message chat message
	 */
	public void sayToGame(int game_id,String message) throws RemoteException
	{
		Game r = getGameById(game_id);
		if( r!=null ) r.say(message);
	}

	/**
	 * Loads a new track to a race (if it's not in a racing state).
	 *
	 * @param game_id race id
	 * @param track_name the name of the track to load
	 */
	public void changeTrack(int game_id,String track_name) throws RemoteException
	{
		Game r = getGameById(game_id);
		if( r!=null )
		{
			if( !r.isRacing() )
				r.loadTrack("../tracks/"+track_name+".track");
		}
	}

}
