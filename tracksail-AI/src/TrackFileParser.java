import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Class <code>TrackFileParser</code> reads and parses a track file
 * (../tracks/*.track) from the disk and creates a new <code>Track</code> object
 * based on it.
 * <p>
 * This class is used by calling the <code>parseTrack (String filename)</code>
 * method, which returns a <code>Track</code> object if successful.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Track
 */
public class TrackFileParser
{
	/** Default constructor. */
	public TrackFileParser() {	}

	/**
	 * The track file is first read in a vector.
	 * <p>
	 * In this phase, the comment lines are removed.
	 *
	 * @param file track file
	 */
	private Vector readFile (File file)
	{
		Vector lines = new Vector();

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String ln;
			while ( ( ln = in.readLine ()) != null)
			{
				if ( !ln.startsWith ("#")) { lines.addElement(ln); }
			}
		}
		catch ( Exception e)
		{
			System.out.println ("Could not open file: " + e.toString ());
			return null;
		}

		return lines;
	}

	/**
	 * The actual creation of a <code>Track</code> object.
	 *
	 * @param filename Name of the track file in relation to the working
	 * 		   directory (e.g. "../tracks/default.track").
	 */
	public Track parseTrack (String filename)
	{
		File trackfile = new File (filename);
		Vector pl = readFile (trackfile);
		if ( pl == null) { return null; }
		
		Track track = new Track();

		int port_no = 0, num_ports = 0;
		int pb_x, pb_y, sb_x, sb_y;
		String tok;

		for ( int i = 0; i < pl.size (); i++)
		{
			StringTokenizer st = new StringTokenizer ( (String)pl.elementAt (i), "; ");

			while ( st.hasMoreTokens ())
			{
				tok = st.nextToken ();

				if ( tok.equals ("#"))
				{
					while ( st.hasMoreTokens ())
					{
						st.nextToken ();
					}
					continue;
				}
					
				if ( tok.equals ("TRACK"))
				{
					track.setName ( st.nextToken ());
					continue;
				}
				else if ( tok.equals ("LAPS"))
				{
					track.setNumberOfLaps ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("MAXTIME"))
				{
					track.setMaxGameDuration ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("CHANGEWIND"))
				{
					track.setWindChangeInterval ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("MINWINDD"))
				{
					track.setMinWindDirection ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("MAXWINDD"))
				{
					track.setMaxWindDirection ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("MINWINDV"))
				{
					track.setMinWindVelocity ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("MAXWINDV"))
				{
					track.setMaxWindVelocity ( Integer.parseInt ( st.nextToken ()));
					continue;
				}
				else if ( tok.equals ("NUMPORTS"))
				{
					num_ports = Integer.parseInt ( st.nextToken ());
					track.initPorts (num_ports);
					continue;
				}
				else if ( tok.equals ("PORT"))
				{
					if ( port_no < num_ports)
					{
						try
						{
							pb_x = Integer.parseInt ( st.nextToken ());
							pb_y = Integer.parseInt ( st.nextToken ());
							sb_x = Integer.parseInt ( st.nextToken ());
							sb_y = Integer.parseInt ( st.nextToken ());
							track.addPort ( port_no, new Vector2(pb_x,pb_y),
									new Vector2(sb_x,sb_y) );
							port_no++ ;
							continue;
						}
						catch (Exception e) {}
					} else {
						System.out.println ("Too many ports in trackfile!");
						return null;
					}
				} else {
					// There's something wrong with the
					// track file.
					System.out.println ("Error in trackfile: \"" + tok + "\" is not a valid entry!");
					return null;
				}
			}
		}

		return track;
	}
}
