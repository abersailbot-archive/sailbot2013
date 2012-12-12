
import java.io.*;

/**
 * Class <code>Vector2</code> represents a two-dimensional vector.
 * <p>
 * <code>Vector2</code> includes all of the most important vector operations and
 * some more specific ones that were needed in the game. We implemented the
 * <code>Vector2</code> class, because there wasn't one provided by Java SDK and
 * the class was already almost completely implemented for a previous project.
 * <p>
 * The vector components, x and y coordinates, are of type float and public
 * variables. They were made public for convenience and this also results in a
 * lot clearer and shorter code :)
 * <p>
 * TODO: I don't see any real reason to have them public. (Tero)
 * 
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 */
public class Vector2 implements Serializable
{
	/** the value of the vector's x coordinate */
	public float x;

	/** the value of the vector's y coordinate */
	public float y;

	/** Default constructor creates a zero vector. */
	public Vector2() { set(0,0); }

	/**
	 * Constructor.
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Vector2(float x,float y) { set(x,y); }

	/**
	 * Constructor.
	 *
	 * @param v initial values as a vector
	 */
	public Vector2(Vector2 v) { set(v); }

	/**
	 * Sets the values of vector components.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void set(float x,float y) { this.x=x; this.y=y; }

	/**
	 * Sets the values of vector components.
	 * 
	 * @param v vector that gives us the component values
	 */
	public void set(Vector2 v) { set(v.getX(),v.getY()); }

	/**
	 * Gives a value to the vector's x component.
	 * 
	 * @param x x coordinate
	 */
	public void setX(float x) { this.x=x; }

	/**
	 * Gives a value to the vector's y component.
	 * 
	 * @param y y coordinate
	 */
	public void setY(float y) { this.y=y; }

	/**
	 * Returns the value of the vector's x component.
	 * 
	 * @return Vector's x component.
	 */
	public float getX() { return this.x; }

	/**
	 * Returns the value of the vector's y component.
	 * 
	 * @return Vector's y component.
	 */
	public float getY() { return this.y; }

	/**
	 * Returns a copy of this vector.
	 * 
	 * @return Copy of this vector.
	 */
	public Vector2 duplicate() { return new Vector2(this); }

	/**
	 * Adds another vector to this vector.
	 * 
	 * @param dx x component of the vector to add
	 * @param dy y component of the vector to add
	 */
	public void add(float dx,float dy) { this.x+=dx; this.y+=dy; }

	/**
	 * Adds another vector to this vector.
	 * 
	 * @param v vector to add
	 */
	public void add(Vector2 v) { this.x+=v.x; this.y+=v.y; }

	/**
	 * Returns the sum of two vectors as a new vector.
	 * 
	 * @param v1 first vector to add
	 * @param v2 second vector to add
	 * @return Sum of vectors v1 and v2 v1+v2.
	 */
	public static Vector2 add(Vector2 v1,Vector2 v2) { return new Vector2(v1.x+v2.x,v1.y+v2.y); }

	/**
	 * Subtracts another vector from this vector.
	 * 
	 * @param dx x component of the vector to subtract
	 * @param dy y component of the vector to subtract
	 */
	public void sub(float dx,float dy) { this.x-=dx; this.y-=dy; }

	/**
	 * Subtracts another vector from this vector.
	 * 
	 * @param v vector to subtract
	 */
	public void sub(Vector2 v) { this.x-=v.x; this.y-=v.y; }

	/**
	 * Returns the difference of two vectors as a new vector.
	 * 
	 * @param v1 vector to subtract
	 * @param v2 vector to subtract from
	 * @return Difference of vectors v1 and v2 v1-v2.
	 */
	public static Vector2 sub(Vector2 v1,Vector2 v2) { return new Vector2(v1.x-v2.x,v1.y-v2.y); }

	/**
	 * Multiplies a vector with a scalar.
	 * 
	 * @param t scalar multiplier
	 */
	public void mul(float t) { this.x*=t; this.y*=t; }

	/**
	 * Divides a vector with a scalar.
	 * 
	 * @param t scalar divisor
	 */
	public void div(float t) { mul(1.0f/t); }

	/**
	 * Returns the dot product of this vector with another vector.
	 * 
	 * @param v the other factor of the dot product
	 * @return Dot product = this.x*v.x + this.y*v.y
	 */
	public float dotProduct(Vector2 v) { return this.x*v.x + this.y*v.y; }

	/**
	 * Returns the quadratic length of this vector.
	 * 
	 * @return Quadratic length of this vector = x*x + y*y.
	 */
	public float lengthSq() { return x*x + y*y; }

	/**
	 * Returns the length of this vector.
	 * 
	 * @return Length of this vector = sqrt(x*x + y*y).
	 */
	public float length() { return (float)Math.sqrt(lengthSq()); }

	/**
	 * Normalises this vector, meaning it's length should be 1.0.
	 */
	public void normalize() { div(length()); } // length becomes 1

	/**
	 * Returns the mean of two vectors as a new vector.
	 * 
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return Mean of vectors v1 and v2 (v1+v2)/2.0
	 */
	public static Vector2 average(Vector2 v1,Vector2 v2) { return new Vector2((v1.x+v2.x)*0.5f,(v1.y+v2.y)*0.5f); }

	/**
	 * Returns the direction angle of this vector in radians.
	 * 
	 * @return Angle of this vector in a two-dimensional plane.
	 */
	public float getDirection() {
		Vector2 v = this.duplicate();
		v.normalize();
		// calculate the angle from a unit vector
		if( v.x<0 ) return (float)Math.PI - (float)Math.asin(v.y);
		return (float)Math.asin(v.y);
	}

	/**
	 * Interpolate linearly between two vectors.
	 * 
	 * @param v1 first vector
	 * @param v2 second vector
	 * @param t multiplier
	 * @return Vector interpolated linearly between vectors v1 and v2. <br>
	 *					If t=0 returns vector v1<br>
	 *					If t=1 returns vector v2<br>
	 *					If 0&lt;t&lt;1 returns vector between v1 and v2<br>
	 *					If t&lt;0 or t&gt;1 vector outside v1 and v2<br>
	 */
	public static Vector2 linearInterpolate(Vector2 v1, Vector2 v2,float t) {
		Vector2 v = Vector2.sub(v2,v1); v.mul(t); v.add(v1);
		return v;
	}

	/**
	 * Writes an object to the given stream.
	 *
	 * @param out <code>ObjectOutputStream</code> to write into
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeFloat( x );
		out.writeFloat( y );
	}

	/**
	 * Reads an object from the given stream.
	 *
	 * @param in <code>ObjectInputStream</code> to read from
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		x = in.readFloat();
		y = in.readFloat();
	}
}
