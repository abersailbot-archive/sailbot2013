import java.io.*;
import java.util.*;
import java.rmi.RemoteException;

/**
 * Class <code>ServerCommandHandler</code> handles the server commands.
 * <p>
 * The user of the server can give it commands in the console.
 * <code>ServerCommandHandler</code> takes care of receiving and handling these
 * commands.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Server
 */
public class ServerCommandHandler
{
	private Server s;

	/**
	 * Constructor.
	 *
	 * @param srv <code>Server</code> whose commands we are handling
	 */
	public ServerCommandHandler (Server srv)
	{
		s = srv;
	}

	/**
	 * A loop that waits for commands from the console and when they arrive
	 * calls the correct method for them.
	 * <p>
	 * To exit the loop and close the server, command &quot;exit&quot; should be used.
	 */
	public void run ()
	{
		BufferedReader keyb = new BufferedReader (new InputStreamReader (System.in));
		boolean running = true;
		StringTokenizer st = new StringTokenizer ("");
		String cmd;

		while(running)
		{
			try
			{
				st = new StringTokenizer ( (String)keyb.readLine(), " ");
			} catch (Exception e) {}

			while ( st.hasMoreTokens ())
			{
				cmd = st.nextToken ();

				if( cmd.equals("exit") )
				{
					running = false;
					continue;
				}
				else if ( cmd.equals ("help"))
				{
					printHelp ();
					continue;
				}
				else if ( cmd.equals ("players"))
				{
					printPlayers ();
					continue;
				}
				else if ( cmd.equals ("tracks"))
				{
					printTracks ();
					continue;
				}
				else if ( cmd.equals ("kick"))
				{
					if ( st.hasMoreTokens ()) { readParams (cmd, st); }
					else { System.out.println ("\"kick\" who?\n"); }
					continue;
				}
				else if ( cmd.equals ("update"))
				{
					s.updateTrackList();
				}
				else if ( cmd.equals (" "))
				{
					continue;
				}
				else if ( cmd.equals (" "))
				{
					continue;
				}
				else if ( cmd.equals ("set"))
				{
					if ( st.hasMoreTokens ()) { readParams (cmd, st); }
					else { System.out.println ("\"set\" what?"); }
					continue;
				} else {
					System.out.println ("Invalid command. Type help for list of commands.\n");
					continue;
				}
			}
		}
	}

	/**
	 * Handles parameters belonging to some commands.
	 *
	 * @param command the given command
	 * @param st <code>StringTokenizer</code> to read the parameters
	 */
	public void readParams (String command, StringTokenizer st)
	{
		String param = "";
		boolean bad_param = false;

		while ( st.hasMoreTokens ())
		{
			param = st.nextToken ();

			if ( command.equals ("kick")) 
			{
				if ( st.countTokens () > 0)
				{
					System.out.println ("Wrong number of parameters for \"kick\"\n");
					break;
				}
				if ( param.equals ("all"))
				{
					kickAll ();
					break;
				}
				try
				{
					kickPlayer (Integer.parseInt (param));
					break;
				}
				catch (Exception e)
				{
					System.out.println ( e.toString ()); // debug
					bad_param = true;
					break;
				}
			} else {
				bad_param = true;
				break;
			}
		}

		if ( bad_param)
		{
			System.out.println ("Bad parameter: " + param + "\nTry help for list of valid commands.\n");
		}
	}

	/**
	 * Prints out helpful text.
	 */
	protected void printHelp ()
	{
		System.out.println ("\n" + Server.version);
		System.out.println ("----------------------------------------------------------");
		System.out.println ("players: print players currently connected");
		System.out.println ("tracks: print tracks currently played in games");
		System.out.println ("update: updates tracklist");
		System.out.println ("kick [player_id or 'all']: kick player out from the server");
		System.out.println ("exit: stop and exit server");
		System.out.println ("help: list valid commands");
		System.out.println ("----------------------------------------------------------\n");
	}

	/**
	 * Prints out the players currently connected to the server.
	 */
	protected void printPlayers ()
	{
		System.out.println ("Current players:");
		System.out.println ("id Name");

		synchronized(s)
		{
			Iterator it = s.getPlayersIterator();
			while(it.hasNext())
			{
				Player p = (Player)it.next();
				System.out.println ( p.getId() + ": " + p.getName ());
			}
		}
		System.out.println ("\n");
	}

	/**
	 * Prints out the tracks that are currently played.
	 */
	protected synchronized void printTracks ()
	{
		System.out.println ("\nTracks being played:");
		System.out.println ("Id, name, track");

		Iterator it = s.getGamesIterator();
		while(it.hasNext())
		{
			Game gr = (Game)it.next();
			if(gr!=null)
				System.out.println (gr.getId()+", "+gr.getName()+", " + gr.getTrackName());
		}
		System.out.println ("\n");
	}

	/**
	 * Kicks all the players out from the server.
	 */
	protected synchronized void kickAll ()
	{
		Iterator it = s.getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			
			try
			{
				// Call Tracksail.kick().
				p.getClient().kick("Everyone was kicked.");
			}
			catch(RemoteException re) {}
		}
	}

	/**
	 * Kicks the given player out from the server.
	 *
	 * @param id id of the player to kick out
	 */
	protected void kickPlayer (int id)
	{
		Player p = s.getPlayerById (id);
		try
		{
			// Call Tracksail.kick().
			if(p!=null) p.getClient().kick("Well done! You were kicked.");
		}
		catch(RemoteException re) {}
	}
}

