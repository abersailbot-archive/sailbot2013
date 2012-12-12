import java.io.*;
import java.util.*;

import java.rmi.RemoteException;

/**
 * Class <code>Player</code> represents one player.
 * <p>
 * Contains the data regarding a player, his/her boat, player's status in the
 * race and the functionality of the player's boat.
 * </p>
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Vector2
 */
public class Player implements Serializable
{
	/** position of the player's boat */
	Vector2 pos;
	/** direction of the player's boat in degrees */
	private int direction=0;
	/** openness of the sail, an integer in range 0..100 */
	private int sail_value;
        
         private Vector2 velocity;

	/** player's unique id */
	private int id;
	/** player's name */
	private String name;

	/** player's current race */
	private Game game;

	/** interface to the client object of this player */
	private ClientInterface client;

	/**
	 * Returns the interface of the player's client object.
	 * 
	 * @return Interface of the player's client object.
	 */
	public ClientInterface getClient() { return client; }

	/**
	 * Joins the player in a race.
	 * 
	 * @param g the race the player joined in.
	 */
	public void setGame(Game g) { game = g; }

	/**
	 * Returns the race the player is in.
	 * 
	 * @return Race the player is in or <code>NULL</code> if the player is
	 *         not in a race.
	 */
	public Game getGame() { return game; }
        
        public float getVelocity() { return velocity.length() * 7.0f; }

	/**
	 * Stores the lap the player is in.
	 * <p>
	 * Lap 1 is the first lap.<br/>
	 * At start, before the player has crossed the starting line, lap == 0.
	 * </p>
	 */
	private int cur_lap;
	/** the next gate to pass */
	private int next_port;
	/** distance to the next gate to pass */
	private float dist_to_next_port;

	/** has the player reached the goal */
	private boolean finished;

	/**
	 * Sets the value of <code>finished</code>.
	 * 
	 * @param flag the new value of <code>finished</code>
	 */
	public void setFinished(boolean flag) {
		finished = flag;
	}

	/**
	 * Returns the value of <code>finished</code>.
	 * 
	 * @return Value of <code>finished</code>.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Constructor.
	 *
	 * @param id the unique id of the player
	 * @param name the name of the player
	 * @param cobj the interface of the player's client object
	 */
	public Player(int id,String name,ClientInterface cobj)
	{
		pos = new Vector2();
		direction = 0;
		sail_value = 0;
                velocity = new Vector2(0,0);
		this.id = id;
		this.name = name;

		cur_lap=0;
		next_port=0;
		dist_to_next_port=0.0f;

		finished = false;
		game = null;

		client = cobj;
	}

	/**
	 * Rotates the player's boat to portboard.
	 */
	public void turnLeft()
	{
		direction += 10;
		if( direction>360 ) direction -= 360;
	}
	
	public void turnLeft(int angle)
	{
	    direction += angle;
		if( direction>360 ) direction -= 360;
	}
	
	/**
	 * Rotates the player's boat to starboard.
	 */
	public void turnRight()
	{
		direction -= 10;
		if( direction<0 ) direction += 360;
	}
	
	public void turnRight(int angle)
	{
	    direction -= angle;
		if( direction<0 ) direction += 360;
	}

	/**
	 * Adjusts the position of the player's sail.
	 *
	 * @param pa An integer that defines the position of the sail.<br/>
	 * 		Value in range [0, 100].<br/>
	 *		pa==0 means that the sail if fully locked (pointing 
	 *		towards the stern).<br/>
	 *		pa==100 means that the sail is completely loose (may
	 *		point directly to the sides).
	 */
	public void setSailValue(int pa)
	{
		sail_value = pa;
		if( sail_value<0 ) sail_value=0;
		if( sail_value>100 ) sail_value=100;
	}

	/**
	 * Sets the coordinates of the player's boat.
	 *
	 * @param x the new x coordinate of the boat
	 * @param y the new y coordinate of the boat
	 */
	public void setPosition(float x,float y) { pos.set(x,y); }

	/**
	 * Sets the position vector of the player's boat.
	 *
	 * @param v the new position vector of the player's boat
	 */
	public void setPosition(Vector2 v) { pos.set(v); }

	/**
	 * Sets the movement direction of the player's boat.
	 *
	 * @param dir The new movement direction of the boat, an angle in
	 *            two-dimensional plane in degrees.<br/>
	 *            The angle grows when rotating counterclockwise.
	 */
	public void setDirection(int dir) { direction = dir; }

	/**
	 * Returns a copy of the position vector of the player's boat.
	 *
	 * @return Copy of the position vector of the boat.
	 */
	public Vector2 getPosition() { return pos.duplicate(); }

	/**
	 * Returns the movement angle of the player's boat.
	 *
	 * @return Movement angle of the player's boat in degrees.
	 */
	public int getDirection() { return direction; }

	/**
	 * Returns the unique id of the player.
	 *
	 * @return Unique id of the player.
	 */
	public int getId() { return id; }

	/**
	 * Returns the name of the player.
	 *
	 * @return Name of the player.
	 */
	public String getName() { return name; }


	/**
	 * Sets the next gate the player must pass through.
	 *
	 * @param p the number of the next gate to pass, in range [0...num_ports-1]
	 */
	public void setNextPort(int p) { next_port=p; }

	/**
	 * Returns the number of the gate the player must pass through next.
	 *
	 * @return Number of the gate to pass through next<br/>
	 *         in range [0...num_ports-1]
	 */
	public int getNextPort() { return next_port; }

	/**
	 * Sets a new lap number to the player.
	 *
	 * @param l the number of the lap the player is moving into
	 */
	public void setCurLap(int l) { cur_lap=l; }

	/**
	 * Returns the lap the player is in.
	 *
	 * @return Number of the lap the player is currently in.
	 */
	public int getCurLap() { return cur_lap; }

	/**
	 * Calculates the disctance between the player's boat and a given
	 * location (the next gate).
	 * <p>
	 * The result is saved in an attribute and can be requested through the
	 * method <code>getDistToNextPort()</code>.
	 * </p>
	 *
	 * @param v the location of the next gate
	 */
	public void setDistToNextPort(Vector2 v)
	{
		Vector2 d = Vector2.sub(v,pos);
		dist_to_next_port = d.length();
	}

	/**
	 * Returns the distance to the next gate.
	 *
	 * @return The distance to the next gate to pass.
	 */
	public float getDistToNextPort() { return dist_to_next_port; }


	/**
	 * Calculates the force pushing the boat forward.
	 * <p>
	 * The force is affected by the direction and velocity of the wind
	 * together with the direction of the boat and the position of the sail.
	 * </p>
	 *
	 * @param wind_direction the direction of the wind in degrees
	 * @param wind_velocity the velocity of the wind in m/s
	 * @return Thrust force that the wind gives to the sail (with respect
	 *         to the direction of the boat).
	 */
	public float calcWindPush(int wind_direction,int wind_velocity) {

		// The direction of the normal line of the sail's surface in
		// degrees.
		int sail_dir = calcSailDirection(wind_direction)+90;

		// The direction of the normal line of the sail's surface in
		// radians.
		float p_dir = 3.14159f * (float)sail_dir / 180.0f;
		// The direction of the wind in radians.
		float w_dir = 3.14159f * (float)wind_direction / 180.0f;
		// The direction of the boat in radians.
		float s_dir = 3.14159f * (float)direction / 180.0f;

		// The normal vector of the sail.
		float px = (float)Math.cos(p_dir);
		float py = (float)Math.sin(p_dir);

		// The direction vector of the wind.
		float wx = (float)Math.cos(w_dir);
		float wy = (float)Math.sin(w_dir);

		// The direction vector of the boat.
		float sx = (float)Math.cos(s_dir);
		float sy = (float)Math.sin(s_dir);

		// Dot product: perpendicular == 0, parallel = 1.
		float pt = px*wx + py*wy;

		// We only move the boat to it's own direction, so we take the
		// lenght of it's component.
		//float pt2 = px*sx + py*sy;

		// So the thrust against the normal line of the sale is
		return (float)wind_velocity * (float)Math.abs(pt);
	}

	/**
	 * Calculates the direction of the sail in relation to the world.
	 * <p>
	 * Needed e.g. to draw the sail correctly.<br>
	 * The direction of the sail is measured outwards from the mast.
	 * </p>
	 *
	 * @param wind_direction The direction of the wind in degrees. This affects to
	 *                       the direction of the sail, since the sail is below
	 *                       the wind when it's loose.
	 * @return New direction of the sail.
	 */
	public int calcSailDirection(int wind_direction) {

		// If the sail is loose, it's flapping to the direction of the
		// wind. The direction of the sail is expressed as the direction
		// outwards from the mast.

		// The direction of the sail when it's loose, expressed against
		// the direction of the boat.
		int sail_dir = (wind_direction-direction+360)%360;

		// The sail is limited by the guiding string (sail_value).
		// If sail_value == 0, the string is tight and the sail is always
		// limited to point towards the stern of the boat, so it can't
		// move.
		// If sail_value == 100, the sail can freely swing 90 degrees to
		// both sides.

		// Change the scale 0..100 % => 0...90 degrees.
		int restrict_angle = 90 * sail_value / 100;

		// The sail may swing between k1 ... k2 against the direction of
		// the boat.
		int k1 = (180 - restrict_angle+360)%360;
		int k2 = (180 + restrict_angle)%360;
		
		
                //k1 and k2 only when heading into wind
		if( sail_dir < k1 ) 
                {
                    sail_dir = k1;
                }
                
		if( sail_dir > k2 ) 
                {
                    sail_dir = k2;
                }
                

		return sail_dir+direction;
	}
        
	/**
	 * Moves the boat for a certain timeslice.
	 *
	 * @param seconds the timeslice to move the boat for (in seconds)
	 * @param wind_direction the direction of the wind in degrees
	 * @param wind_velocity the velocity of the wind in m/s
	 */
	public void move(float seconds,int wind_direction,int wind_velocity) {

		// To test, we only move the boat to the direction it's moving
		// to at a certain speed. A simple modeling of physics that
		// calculates the velocity based on the directions of the wind,
		// boat and sail.

		// Watch out for division by zero!
		float vel = calcWindPush(wind_direction,wind_velocity) * seconds;
		float dir = (float)Math.toRadians(direction);
		pos.add( (float)Math.cos(dir) * vel, (float)Math.sin(dir) * vel );

		// In addition, a small slide to the direction of the wind,
		// which gives us a bit of realism in a simple way.
		// After all, the meaning isn't to make a simulator.
		float wdir = (float)Math.toRadians(wind_direction);
		float f = wind_velocity * 0.05f * seconds;
		pos.add( (float)Math.cos(wdir) * f, (float)Math.sin(wdir) * f );
                
                
                
                /*some random direction changes
	        */
                /*if (Math.random()<0.3)
                {
		    Random myrand = new Random(1);
                    
                    int random2 = myrand.nextInt(10);
                    random2 = 5 - random2;
                    System.out.println("random value of " + random2);
                    direction = ((direction + random2) + 360) % 360;
                    System.out.println("new direction is " + direction);
                }*/
	}

	/**
	 * Tells the player (s)he has just passed a gate.
	 *
	 * @param passed_port the number of the passed gate, in range [0...num_ports-1]
	 * @param ports_in_track the number of gates to pass in one lap
	 */
	public void passedPort(int passed_port,int ports_in_track)
	{
		// If we passed the finish line (gate 0), add the lap counter.
		if( passed_port==0 ) cur_lap++;

		next_port = passed_port+1;
		if( next_port>=ports_in_track )
			next_port=0;
	}


	/**
	 * Prepares the player for a race.
	 *
	 * @param game the object of the race the player is racing in
	 * @param grid_pos the number of the player's starting grid, according to 
	 *                 which the player is placed in the correct position
	 *                 before the gate.
	 */
	public void preparePlayerToRace(Game game,int grid_pos)
	{
		// Place the boat in it's own starting position.
		Vector2 l = game.getTrack().getPortPB(0); // Portboard buoy of gate 0.
		Vector2 r = game.getTrack().getPortSB(0); // Starboard buoy of gate 0.
		Vector2 d = new Vector2(-(r.y-l.y),(r.x-l.x)); d.normalize(); // Movement direction vector.
		int dir = (int)Math.toDegrees( d.getDirection() );

		Vector2 pos = Vector2.linearInterpolate(l,r, (float)(grid_pos+1) / (float)(game.getNumPlayers()+1) );
		// Place the boat a few meters before the gate.
		pos.sub(d); pos.sub(d); pos.sub(d); pos.sub(d); pos.sub(d);

		setPosition(pos);
		setDirection(dir);

		setNextPort(0); // The next gate to pass.
		setCurLap(0); // The lap we're in.

		setFinished(false);
	}

	/**
	 * Returns the movement direction vector of the player's boat.
	 * 
	 * @return Movement direction vector.
	 */
	public Vector2 getDirectionVector()
	{
		return new Vector2( (float)Math.cos(Math.toRadians(direction)), 
				(float)Math.sin(Math.toRadians(direction)) );
	}


	/**
	 * Writes an object to a stream.
	 *
	 * @param out <code>ObjectOutputStream</code> to write to
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(pos);
                out.writeObject(velocity);
		out.writeInt(direction);
		out.writeInt(sail_value);
		out.writeInt(id);
		out.writeObject(name);
		out.writeInt(cur_lap);
		out.writeInt(next_port);
		out.writeFloat(dist_to_next_port);
		out.writeBoolean(finished);
	}

	/**
	 * Reads an object form a stream.
	 *
	 * @param in <code>ObjectInputStream</code> to read from
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		pos = (Vector2)in.readObject();
                velocity = (Vector2)in.readObject();
		direction = in.readInt();
		sail_value = in.readInt();
		id = in.readInt();
		name = (String)in.readObject();
		cur_lap = in.readInt();
		next_port = in.readInt();
		dist_to_next_port = in.readFloat();
		finished = in.readBoolean();
	}

}
