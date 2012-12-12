/**
 * Class <code>Port</code> implements a gate.
 * FIXME: (1.1) This should probably be named "Gate".
 * <p>
 * A gate consists of two buoys floating on the water, left (portboard) and right
 * (starboard).
 * <p>
 * Contains the anchored positions for each buoy and the moving positions of
 * each buoy. The buoys can move, e.g. when hit by a boat. A buoy that has moved
 * will float back to its anchored position.
 * <p>
 * Port contains the functionality to test collisions with the players and to
 * test passing the gate. Passing the gate is dependent on the direction.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Vector2
 * @see Track
*/
public class Port
{
	/** portboard buoy of the port */
	public Vector2 left;

	/** starboard buoy of the port */
	public Vector2 right;

	/** anchoring point of the portboard buoy */
	private Vector2 mount_left;

	/** anchoring point of the starboard buoy */
	private Vector2 mount_right;

	/**
	 * Constructor.
	 * FIXME: (1.1) Use p and s (portboard/starboard)
	 *
	 * @param l position of the portboard buoy
	 * @param r position of the starboard buoy
	 */
	public Port(Vector2 l,Vector2 r)
	{
		left = l.duplicate(); right = r.duplicate();
		mount_left = l.duplicate(); mount_right = r.duplicate();
	}

	/**
	 * Moves the buoys towards the anchoring position if they aren't there.
	 * 
	 * @param seconds time slice according to which the buoys should be
	 *                moved
	 */
	public void move(float seconds)
	{
		Vector2 dl = Vector2.sub(left,mount_left); dl.mul(1.0f*seconds); left.sub(dl);
		Vector2 dr = Vector2.sub(right,mount_right); dr.mul(1.0f*seconds); right.sub(dr);
	}

	/**
	 * Checks the collisions between a player and the gate buoys.
	 * <p>
	 * If there is a collision, the buoy should be moved.
	 * 
	 * @param p the player to check the collisions with
	 */
	public void crashWithPlayer(Player p)
	{
		// If the buoy is too close to the boat, move the buoy away from
		// the boat.
		Vector2 dl = Vector2.sub(left,p.getPosition());
		if(dl.length()<2.0f) { dl.mul(0.5f); left.add(dl); }

		Vector2 dr = Vector2.sub(right,p.getPosition());
		if(dr.length()<2.0f) { dr.mul(0.5f); right.add(dr); }
	}

	/**
	 * Checks if the line segment p1-p2 (the interval traveled by
	 * the boat) intersects with this gate while going to the allowed
	 * direction.
	 * 
	 * @return true if intersects correctly, false if doesn't
	 */
	public boolean passed(Vector2 p1,Vector2 p2)
	{

		{ // Make sure the gate is approached from the correct direction.

			Vector2 v1 = Vector2.sub(right,left);
			Vector2 v2 = Vector2.sub(p1,left);

			// We use a part of the 3d cross product formula to
			// get the z component of v1 X v2
			float z = v1.x*v2.y - v1.y*v2.x;
			// The sign of z tells us which side of the
			// gate p1 is on.

			// If p1 is on the wrong side, passing the gate in the
			// correct direction is impossible, so return
			// immediately.
			if( z>0 ) return false;
		}

		Vector2 V = new Vector2(right); V.sub(left); // V = starboard-portboard
		Vector2 P = new Vector2(p2); P.sub(p1); // P = p2-p1

		/* We want to know the intersection point of two line segments,
		 * so we must solve from (portboard + t*V = p1 + s*P) both t and
		 * s (as float). If t and s are in the interval [0,1], the line
		 * segments intersect each other between the endpoints of the line
		 * segments.
 		 *
		 * Here are the equations solved. There are many ways to do
		 * this, but we pick such that we don't get division by zero.
  		 *
		 * s = (p1.y*V.x - left.y*V.x - p1.x*V.y + left.x*V.y) / (P.x*V.y - P.y*V.x);
		 * t = (p1.x + s*P.x - left.x)/V.x;
		 * t = (p1.y + s*P.y - left.y)/V.y;
		 *
		 * t = (left.y*P.x - p1.y*P.x - left.x*P.y + p1.x*P.y)/(V.x*P.y - V.y*P.x);
		 * s = (left.x - p1.x + t*V.x)/P.x;
		 * s = (left.y - p1.y + t*V.y)/P.y;
		 */

		float div,t,s;

		div = (P.x*V.y - P.y*V.x);
		if( div != 0.0f )
		{
			s = (p1.y*V.x - left.y*V.x - p1.x*V.y + left.x*V.y) / div;
			if( s<0.0f || s>=1.0f ) return false;
			if( V.x!=0.0f ) {
				t = (p1.x + s*P.x - left.x)/V.x;
				if( t<0.0f || t>=1.0f ) return false;
				return true;
			}
			else if( V.y!=0.0f ) {
				t = (p1.y + s*P.y - left.y)/V.y;
				if( t<0.0f || t>=1.0f ) return false;
				return true;
			}
			return false;
		}

		div = (V.x*P.y - V.y*P.x);
		if( Math.abs(div) < 0.000001 )
		{
			t = (left.y*P.x - p1.y*P.x - left.x*P.y + p1.x*P.y)/div;
			if( t<0.0f || t>=1.0f ) return false;
			if( P.x!=0.0f ) {
				s = (left.x - p1.x + t*V.x)/P.x;
				if( s<0.0f || s>=1.0f ) return false;
				return true;
			}
			else if( P.y!=0.0f ) {
				s = (left.y - p1.y + t*V.y)/P.y;
				if( s<0.0f || s>=1.0f ) return false;
				return true;
			}
			return false;
		}

		// We should never reach this.
		return false;
	}
}

