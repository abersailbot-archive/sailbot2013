import java.awt.*;
import java.io.*;


/**
 * Class <code>Track</code> represents a track/course in the game.
 * <p>
 * A track contains a group of gates (which the player must pass through in
 * correct order), track name, number of laps, maximum duration of a race and 
 * knowledge of wind velocity, direction and variation frequency. The track
 * is read from a file using <code>TrackFileParser</code>.
 * <p>
 * Format of a track file:<br/><br/>
 * #This is a comment<br/>
 * TRACK;Default # track name (here "Default")<br/>
 * LAPS;1 # how many laps to sail in a race<br/>
 * MAXTIME;300 # maximum duration of a race in seconds<br/>
 * CHANGEWIND;10 # change frequency of the wind velocity and direction in
 *               # seconds<br/>
 * MINWINDD;10 # minimum wind direction in degrees<br/>
 * MAXWINDD;40 # maximum wind direction in degrees<br/>
 * MINWINDV;10 # minimum wind velocity in m/s<br/>
 * MAXWINDV;20 # maximum wind velocity in m/s<br/>
 * NUMPORTS;5 # number of gates<br/>
 * # NUMPORTS gates defined like this:<br/>
 * # PORT;left_x;left_y;right_x;right_y<br/>
 * PORT;10;40;30;-10<br/>
 * PORT;160;50;170;10<br/>
 * PORT;150;100;170;130<br/>
 * PORT;50;80;30;100<br/>
 * PORT;-100;30;-140;30<br/>
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see TrackFileParser
 * @see Game
 * @see Vector2
 * @see Port
 */
public class Track implements Serializable
{
	/** track name */
	private String name;

	/** how many laps a race sailed on this track lasts */
	private int laps;

	/** maximum duration of a race sailed on this track in seconds */
	private int maxDuration;

	/** gates of this track in an array */
	private Port [] ports;

	/** how often to change the wind direction and velocity in seconds */
	private int windChangeInterval;

	/** minimum direction of the wind in degrees */
	private int minWindDirection;
	/** maximum direction of the wind in degrees */
	private int maxWindDirection;
	/** minimum velocity of the wind in m/s */
	private int minWindVelocity;
	/** maximum velocity of the wind in m/s */
	private int maxWindVelocity;

	/** Default constructor. */
	public Track () { }

	/** Sets the name attribute. */
	public void setName (String n) { name = n; }
	/** Sets the laps attribute. */
	public void setNumberOfLaps (int l) { laps = l; }
	/** Sets the maxDuration attribute. */
	public void setMaxGameDuration (int md) { maxDuration = md; }
	/** Sets the minWindDirection attribute. */
	public void setMinWindDirection (int mwd) { minWindDirection = mwd; }
	/** Sets the minWindVelocity attribute. */
	public void setMinWindVelocity (int mwv) { minWindVelocity = mwv; }
	/** Sets the maxWindDirection attribute. */
	public void setMaxWindDirection (int mwd) { maxWindDirection = mwd; }
	/** Sets the maxWindVelocity attribute. */
	public void setMaxWindVelocity (int mwv) { maxWindVelocity = mwv; }
	/** Sets the windChangeInterval attribute. */
	public void setWindChangeInterval (int wci) { windChangeInterval = wci; }

	/**
	 * Initialises the gates by reserving memory for them.
	 *
	 * @param num how many gates to reserve memory for
	 */
	public void initPorts (int num) { ports = new Port[num]; }

	/**
	 * Adds a gate.
	 * <p>
	 * Receives as a parameter the number of the gate and the x and y
	 * coordinates of both the starboard and portboard buoys as a
	 * <code>Vector2</code>.
	 *
	 * @param i gate number
	 * @param pb portboard buoy
	 * @param sb starboard buoy
	 */
	public void addPort (int i, Vector2 pb, Vector2 sb)
	{
		ports[i] = new Port(pb,sb);
	}

	/** Returns the name attribute */
	public String getName () { return name; }
	/** Returns the laps attribute */
	public int getNumberOfLaps () { return laps; }
	/** Returns the maxDuration attribute */
	public int getMaxGameDuration () { return maxDuration; }
	/** Returns the minWindDirection attribute */
	public int getMinWindDirection () { return minWindDirection; }
	/** Returns the minWindVelocity attribute */
	public int getMinWindVelocity () { return minWindVelocity; }
	/** Returns the maxWindDirection attribute */
	public int getMaxWindDirection () { return maxWindDirection; }
	/** Returns the maxWindVelocity attribute */
	public int getMaxWindVelocity () { return maxWindVelocity; }
	/** Returns the windChangeInterval attribute */
	public int getWindChangeInterval () { return windChangeInterval; }

	/**
	 * Returns the location of the portboard buoy of the given gate.
	 * 
	 * @param i gate number
	 * @return Location of the portboard buoy.
	 */
	public Vector2 getPortPB (int i) { return ports[i].left; }
	/**
	 * Returns the location of the starboard buoy of the given gate.
	 * 
	 * @param i gate number
	 * @return Location of the starboard buoy.
	 */
	public Vector2 getPortSB (int i) { return ports[i].right; }

	/**
	 * Returns the number of gates in this track.
	 * 
	 * @return Number of gates in this track.
	 */
	public int getPortCount ()
	{
		try { return ports.length; }
		catch( NullPointerException e) { return 0; }
	}

	/**
	 * Writes an object in the given stream.
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(name);
		out.writeInt(laps);
		out.writeInt(maxDuration);

		// number of gates
		out.writeInt(getPortCount());
		// the gates themselves
		for(int i=0; i<getPortCount(); i++ )
		{
			out.writeFloat(getPortPB(i).getX());
			out.writeFloat(getPortPB(i).getY());

			out.writeFloat(getPortSB(i).getX());
			out.writeFloat(getPortSB(i).getY());
		}

		out.writeInt(getWindChangeInterval());

		out.writeInt(getMinWindDirection());
		out.writeInt(getMaxWindDirection());

		out.writeInt(getMinWindVelocity());
		out.writeInt(getMaxWindVelocity());
	}

	/**
	 * Reads an object from the given stream.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		setName( (String)in.readObject() );
		setNumberOfLaps( in.readInt() );
		setMaxGameDuration( in.readInt() );

		// number of gates
		int n_ports = in.readInt();
		initPorts( n_ports );
		// the gates themselves
		for(int i=0; i<n_ports; i++ )
		{
			addPort(i,
				new Vector2(in.readFloat(),in.readFloat()),
				new Vector2(in.readFloat(),in.readFloat()) );
		}

		setWindChangeInterval( in.readInt() );

		setMinWindDirection( in.readInt() );
		setMaxWindDirection( in.readInt() );

		setMinWindVelocity( in.readInt() );
		setMaxWindVelocity( in.readInt() );
	}

	/**
	 * Check if the line segment (p1-&lt;p2) passed the port_id:th gate.
	 * <p>
	 * The direction does matter!
	 * 
	 * @param port_id number of the gate to check against
	 * @param p1 startpoint of the line segment
	 * @param p2 endpoint of the line segment
	 * @return true if the line segment passed the gate, false if it didn't.
	 */
	public boolean passedPort(int port_id,Vector2 p1,Vector2 p2)
	{
		if( port_id<0 || port_id>=getPortCount() ) return false;
		return ports[port_id].passed(p1,p2);
	}

	/**
	 * Moves all the gates as needed.
	 * 
	 * @param seconds time interval to move for in seconds
	 */
	public void move(float seconds)
	{
		for(int i=0; i<ports.length; i++) ports[i].move(seconds);
	}

	/**
	 * Checks and handles the player collisions to the gate buoys.
	 * 
	 * @param p player whose collisions to check
	 */
	public void crashWithPlayer(Player p)
	{
		for(int i=0; i<ports.length; i++) ports[i].crashWithPlayer(p);
	}

}
