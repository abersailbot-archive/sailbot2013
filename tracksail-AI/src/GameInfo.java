import java.io.*;

/**
 * Class <code>GameInfo</code> contains basic information about a race.
 * <p>
 * <code>GameInfo</code> is used when the necessary information about the race
 * is sent to the client programs. Based on this information, the client program
 * shows a list of races available.
 * <p>
 * All attributes are <code>public</code>, since this is basically a collection 
 * of temporary information attributes.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Game
 * @see Tracksail
 */
class GameInfo implements Serializable
{
	/** unique id of this race */
	public int id;
	/** name of this race */
	public String name;
	/** id of the player who owns this race */
	public int master_id;
	/** number of players in this race */
	public int num_players;
	/** is the race currently in racing state */
	public boolean is_racing;
	
	/**
	 * Constructor.
	 *
	 * @param id the unique id of this race
	 * @param name the name of this race
	 * @param master_id the id of the player who owns this race
	 * @param num_players the number of players in this race
	 * @param is_racing true if the race is currently in racing state,
	 *                  false otherwise
	 */
	public GameInfo(int id, String name, int master_id, int num_players, boolean is_racing)
	{
		this.id = id;
		this.name = name;
		this.master_id = master_id;
		this.num_players = num_players;
		this.is_racing = is_racing;
	}

	/**
	 * Writes the game information to an <code>Object</code> stream.
	 *
	 * @param out <code>ObjectOutputStream</code> to write to
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(id);
		out.writeObject(name);
		out.writeInt(master_id);
		out.writeInt(num_players);
		out.writeBoolean(is_racing);
	}
	
	/**
	 * Reads an <code>Object</code> from a stream.
	 *
	 * @param in <code>ObjectInputStream</code> to read from
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		id = in.readInt();
		name = (String)in.readObject();
		master_id = in.readInt();
		num_players = in.readInt();
		is_racing = in.readBoolean();
	}
	
	/**
	 * Returns an image of this race.
	 * <p>
	 * The image contains the name, number of players and state of the
	 * race.
	 *
	 * @return Image of this race.
	 */
	public String toString()
	{
		if( is_racing ) return name+" "+num_players+"/"+Game.MaxPlayers+" (racing)";
		else return name+" "+num_players+"/"+Game.MaxPlayers+" (waiting)";
	}
}
