import java.util.*;
import java.io.*;
import java.rmi.RemoteException;

/**
 * Class <code>Game</code> runs one race.
 * <p>
 * The race can have two states, racing or not racing (attribute
 * <code>is_racing</code>. If the race isn't in a racing state, all player boats
 * wait at the starting line for the master player to begin the race. When this
 * happens, the race switches to the racing state. The race runs in it's own
 * thread where several things, like checking any changes in positions and wind
 * velocity, are done in a race loop.
 * </p>
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see GameInfo
 * @see Track
 * @see TrackFileParser
 */
public class Game implements Runnable, Serializable
{
	/** maximum number of players in one race */
	public static final int MaxPlayers = 6;

	/** race id */
	private int id;

	/** race name */
	private String name;

	/** id of the master player */
	private int master_id;

	/** <code>Server</code> object this race belongs to */
	private Server server;

	/** list of results is a <code>String</code> array where the name of
	 * each player reaching the goal is stored */
	private String [] race_result = new String[MaxPlayers];

	/** number of players who have reached the goal */
	private int num_finished;

	/** track used in this race */
	private Track track = null;

	/** is this race in a racing state or not */
	private boolean is_racing = false;

	/** Is the race being run?
	 * The race loop is executed as long as running == true. */
	private boolean running = true;

	/** starting time of the race */
	private int start_time;

	/** time when the current race state (racing/not racing) ends */
	private int end_time = 0;

	/** A text that tells the race state.
	 * This is sent to the client programs. */
	private String game_status = "Game Not Started";


	/**
	 * Returns the information related to this race.
	 *
	 * @return Race information.
	 */
	public GameInfo getInfo()
	{
		return new GameInfo(getId(),getName(),getMasterId(),getNumPlayers(),isRacing());
	}

	/**
	 * Returns the id of this race.
	 *
	 * @return Race id.
	 */
	public int getId() { return id; }

	/**
	 * Returns the name of this race.
	 *
	 * @return Race name.
	 */
	public String getName() { return name; }

	/**
	 * Returns the id of the master player of this race.
	 *
	 * @return Id of the master player.
	 */
	public int getMasterId() { return master_id; }


	/**
	 * Clears the result array.
	 */
	public void clearRaceResult()
	{
		num_finished=0;
		for(int i=0;i<MaxPlayers;i++) race_result[i]="";
	}

	/**
	 * Returns the result array.
	 *
	 * @return Race results.
	 */
	 public String [] getRaceResult() { return race_result; }

	 /**
	 * Returns the race state.
	 *
	 * @return Race state.
	 */
	public boolean isRacing() { return is_racing; }

	/**
	 * Sets the text that gives the race status.
	 *
	 * @param str Race status text
	 */
	public void setStatusText(String str) { game_status = str; }

	/**
	 * Returns the text that gives the race status.
	 *
	 * @return Race status text.
	 */
	public String getGameStatus() { return game_status; }

	/* ****************************************************************************
	 * TRACK
	 *************************************************************************** */

	/**
	 * Loads a new track to the race.
	 *
	 * @param track_name name of the track to load
	 */
	public synchronized void loadTrack (String track_name)
	{
		Track new_track = null;

		// Load a new track
		try {
			new_track = (new TrackFileParser ()).parseTrack (track_name);
		} catch(Exception e) {
			System.out.println ("Failed to load track.");
		}

		// If the track was loaded correctly, set is as the track of
		// this race.
		if ( new_track != null)
		{
			track = new_track;

			// A new track was loaded so the old race must be stopped.
			say("Server: Track changed to " + track.getName ());
		}
		else
		{
			say("Server: Failed to load track: " + track_name);
			System.out.println ("Failed to load track.");
		}
	}

	/**
	 * Returns a reference to the currect track object.
	 *
	 * @return Reference to the current track object.
	 */
	public synchronized Track getTrack() { return track; }

	/**
	 * Returns the name of the current track.
	 *
	 * @return Name of the current track.
	 */
	public String getTrackName ()
	{
		if( track!=null ) return track.getName ();
		else return "No track loaded";
	}


	/* ***************************************************************************
	 * PLAYERS
	 *************************************************************************** */

	/**
	 * A list of all players in this race.
	 */
	private ArrayList players;

	/**
	 * Adds a player to the race.
	 *
	 * @param p <code>Player</code> object to add
	 * @return Boolean that tells was the player succesfully added.<br>
	 *         true == added, false == not added.
	 */
	public synchronized boolean addPlayer(Player p)
	{
		if(getNumPlayers()<MaxPlayers ) { players.add(p); return true; }
		else return false;
	}

	/**
	 * Returns an iterator for the players.
	 *
	 * @return Player iterator.
	 */
	public Iterator getPlayersIterator() { return players.iterator(); }

	/**
	 * Removes a player from the race.
	 *
	 * @param p player's object
	 */
	public synchronized void removePlayer(Player p) { players.remove(p); }

	/**
	 * Removes all players from the race.
	 */
	public synchronized void removeAllPlayers() { players.clear(); }

	/**
	 * Returns the number of players in this race.
	 *
	 * @return Number of players in this race.
	 */
	public synchronized int getNumPlayers() { return players.size(); }

	/**
	 * Prepares players for the race.
	 */
	public synchronized void preparePlayers()
	{
		int i=0;
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			((Player)it.next()).preparePlayerToRace(this,i++);
		}
	}




	/* ***************************************************************************
	 * WIND
	 *************************************************************************** */

	/** direction of the wind in this race, given in degrees */
	private int wind_direction = 0;

	/**
	 * Returns the direction of the wind in this race.
	 *
	 * @return Direction of the wind in this race.
	 */
	public int getWindDirection() { return wind_direction; }

	/** velocity of the wind in this race, given in m/s */
	private int wind_velocity = 10;

	/**
	 * Returns the wind velocity in this race.
	 *
	 * @return Wind velocity in this race as m/s.
	 */
	public int getWindVelocity() { return wind_velocity; }

	/** time when the direction and strength of the wind will next be changed */
	private int change_wind_time=0;

	/* ***************************************************************************
	 * RANDOM
	 *************************************************************************** */

	/** <code>Random</code> object to generate random numbers */
	private Random rand = new Random(1);

	/**
	 * Returns the next random number from the given range.
	 *
	 * @param min lower limit of the integer to return
	 * @param max the upper limit of the integer to return
	 * @return A random integer in the given range.
	 */
	public int getRandomIntRange(int min,int max)
	{
		if(min==max) return min; // If they are the same, we would divide by zero
		int r = rand.nextInt(); if(r<0) r=-r;
		return min+(r%(max-min));
	}



	/**
	 * Returns the time of the server clock in seconds.
	 * <p>
	 * NOTE: This means all timing is done at a precision of one second.
	 *
	 * @return Server clock time in seconds.
	 */
	public int getTimeS() {
		Date d = new Date();
		return (int)((long)(d.getTime()/1000));
	}

	/**
	 * Constructor.
	 *
	 * @param id Race id
	 * @param name Race name
	 * @param master_id Id of the master player
	 * @param s <code>Server</code> object
	 */
	public Game(int id,String name,int master_id,Server s)
	{
		this.id = id;
		this.name = name;
		this.master_id = master_id;

		players = new ArrayList();

		server = s;

		// We need a track, so load the default one
		loadTrack("../tracks/default.track");

		clearRaceResult();
	}



	/**
	 * Exits the race.
	 */
	public void quit()
	{
		running = false;
		say("Game closed.");
		removeAllPlayers();
	}

	/**
	 * Begins a race.
	 */
	public void startGame()
	{
		clearRaceResult();

		// Put players at the starting line.
		preparePlayers();

		is_racing = true;
		start_time = getTimeS();
		end_time = start_time+track.getMaxGameDuration();

		// Race state has changed!
		// Update the server status texts to the race list
		server.gameListChanged();

		// Tell the changed race state to all client programs.
		synchronized (this)
		{
			Iterator it = getPlayersIterator();
			while(it.hasNext())
			{
				Player p = (Player)it.next();
				try { p.getClient().gameStateChange(is_racing); }
				catch(RemoteException re) {}
			}
		}
	}

	/**
	 * Ends a race.
	 */
	public void stopGame()
	{
		is_racing = false;

		/* FIXME: same code twice */
		// Race state has changed!
		// Update the server status texts to the race list
		server.gameListChanged();

		// Tell the changed race state to all client programs.
		synchronized (this)
		{
			Iterator it = getPlayersIterator();
			while(it.hasNext())
			{
				Player p = (Player)it.next();
				try { p.getClient().gameStateChange(is_racing); }
				catch(RemoteException re) {}
			}
		}
	}

	/**
	 * The race is ran in this run method of the thread.
	 */
	public void run()
	{
	    /** The time that one loop takes */
	    float seconds_per_frame = 0.1f;

		// We begin in the resting room.
		is_racing = false;
		setStatusText("");

		while(running)
		{
			int current_time = getTimeS();


			// Is it time to change the wind direction and velocity?
			if(current_time>change_wind_time)
			{
				wind_direction = getRandomIntRange(track.getMinWindDirection(),track.getMaxWindDirection());
				wind_velocity  = getRandomIntRange(track.getMinWindVelocity(), track.getMaxWindVelocity());
				change_wind_time = current_time + track.getWindChangeInterval();
			}

			// Are we racing?
		    if( is_racing )
			{	// yes

				// If the time has run out, end the race.
				if( current_time >= end_time ) {
					//stopGame();
                    System.out.println("Time's up, exiting\n");
                    System.exit(0);
				}

				// Move the buoys.
				track.move(seconds_per_frame);

				movePlayers(seconds_per_frame);

				// Tell the time that the race has taken.
				// After the race has ended, tell the winner's
				// name and the remaining time.
				if( num_finished>0 ) {
					setStatusText("Race won by "+race_result[0]+"! Time left: "+
							(end_time-current_time));
                    System.out.println("Race won by "+race_result[0]+"! Time left: "+                           (end_time-current_time));
                    //exit when first player finishes
                    System.exit(0);
				} else {
					int s = (current_time - start_time);
					setStatusText("Racing "+(s/60)+":"+(s%60));
				}
			}
			else // We're not racing right now.
			{

				// All the time while in the resting room,
				// prepare the players for a race.
				// This seems to be the easiest solution, since
				// the players can join a race at any time.
				preparePlayers();

				setStatusText("Waiting Game Master to Start Game");
			}

			// A small pause at the end of the loop.
			int delay_msecs = (int)(1000.0f * seconds_per_frame);
			try { Thread.sleep( delay_msecs ); }
			catch(InterruptedException e) {}
		}
	}


	/**
	 * Moves all the players and makes the collision detections.
	 *
	 * @param sedconds How long a time slice will we move, given in seconds.
	 */
	public synchronized void movePlayers(float seconds)
	{
		// Move the players.
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();

			// Move the buoys if they're hit by a boat.
			track.crashWithPlayer(p);

			// Check if this player is colliding with another
			// player.
			crashWithOtherPlayers(p);

			// Move those who are racing.
			Vector2 old_pos = p.getPosition().duplicate(); // !

			// If there's no danger of collision, move the player.
			p.move( seconds, wind_direction, wind_velocity );


			p.setDistToNextPort( Vector2.average(
				track.getPortPB(p.getNextPort()),
				track.getPortSB(p.getNextPort()) ) );

			Vector2 new_pos = p.getPosition();


			// Did the player cross a gate line?
			//if( !p.isFinished() && track.passedPort( p.getNextPort(), old_pos, new_pos ) )
            if( !p.isFinished() && p.getDistToNextPort()<15)
			{
                gotoNextWp(p);
			}

		}

	}
    
    public void gotoNextWp(Player p)
    {
        // Update player information.
        p.passedPort(p.getNextPort(), track.getPortCount());
    
        // Did the player reach the goal?
        if( p.getCurLap() > track.getNumberOfLaps() )
        {
            p.setFinished(true);
    
            int cur_time = getTimeS();
    
            // Insert the name and time of the
            // finished player to the results list.
            int s = (cur_time - start_time);
            setStatusText("Racing ");
            race_result[num_finished] = p.getName()+" "+(s/60)+":"+(s%60);
            num_finished++;
    
            // After the first player reaches the
            // goal, give the others a maximum of
            // one minute more to race.
            if( end_time>getTimeS()+60 ) end_time = cur_time+60;
    
            // If this was the last player to reach
            // the goal, wait for two seconds before
            // a new race can be begun.
            if( num_finished >= getNumPlayers() )
            {
                if( end_time>getTimeS()+2 ) end_time = cur_time+2;
            }
        }
     }

	/**
	 * Makes the collision detections between the given player and other
	 * players.
	 *
	 * @param plr The player whose collisions to check.
	 */
	public synchronized void crashWithOtherPlayers(Player plr)
	{
		Vector2 p1 = plr.getPosition();
		Vector2 d1 = plr.getDirectionVector();

		// Move the players.
		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();

			if( plr != p )
			{
				Vector2 d = Vector2.sub(plr.getPosition(),p.getPosition());

				// We have a collision if the boats are too close to each other.
				if( d.length()<3 )
				{
					d.mul(0.3f);
					d.add(plr.getPosition());
					plr.setPosition(d);
					return;
				}

			}
		}
	}

	/**
	 * Sends a message to all players in this race.
	 *
	 * @param str The message to send.
	 */
	public void say(String str)
	{
  		Iterator it = getPlayersIterator();
		while(it.hasNext())
		{
			Player p = (Player)it.next();
			try { p.getClient().sendText(str); }
			catch(RemoteException re) {}
		}
	}


	/**
	 * Writes the race data to an <code>Object</code> stream.
	 * <p>
	 * NOTE: We only write the necessary attributes and objects to keep the
	 * race and network load as load as possible.
	 *
	 * @param out <code>ObjectOutputStream</code> to write into
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject( players ); // ArrayList is Serializable, this should be ok.

		out.writeInt(wind_direction);
		out.writeInt(wind_velocity);
		out.writeObject(game_status);
		out.writeObject(track);
		out.writeBoolean(is_racing);

		// Write the result table to the stream.
		for(int i=0; i<Game.MaxPlayers; i++ )
			out.writeObject(race_result[i]);
	}

	/**
	 * Reads an object from a stream.
	 *
	 * @param in ObjectInputStream to read from
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		players = (ArrayList)in.readObject();

		wind_direction = in.readInt();
		wind_velocity = in.readInt();
		game_status = (String)in.readObject();
		track = (Track)in.readObject();
		is_racing = in.readBoolean();

		// Read the result table.
		race_result = new String[Game.MaxPlayers];
		for(int i=0; i<Game.MaxPlayers; i++ )
			race_result[i] = (String)in.readObject();

	}

}

