import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Model for <code>JList</code>.
 * <p>
 * Implements interface <code>ListModel</code>.
 * <p>
 * This model contains a list of the race information. All information 
 * (<code>GameInfo</code>) is given at the same time with the 
 * <code>setElements(GameInfo [] gis)</code> method.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Tracksail
 */
 public class GameListModel implements ListModel
{
	/** list of list data listeners */
	private ArrayList listeners = new ArrayList();

	/**
	 * Adds a list data listener.
	 *
	 * @param l <code>ListDataListener</code> object
	 */
	public synchronized void addListDataListener(ListDataListener l) { listeners.add(l); }

	/**
	 * Removes a list data listener.
	 * 
	 * @param l <code>ListDataListener</code> object.
	 */
	public synchronized void removeListDataListener(ListDataListener l) { listeners.remove(l); }

	/** race information as an array */
	private GameInfo [] elements = null;

	/**
	 * Returns the requested <code>GameInfo</code> object.
	 * 
	 * @param index index of the requested location
	 * @return The <code>GameInfo</code> object of the requested index.
	 */
	public synchronized Object getElementAt(int index) { return elements[index]; }

	/**
	 * Returns the number of race information objects.
	 * 
	 * @return Size of the model, which is the number of <code>GameInfo</code>
	 * objects.
	 */
	public synchronized int getSize()
	{
		if( elements==null) return 0;
		return elements.length;
	}

	/**
	 * Sets the given race information as the content of the model.
	 * 
	 * @param gis <code>GameInfo</code> objects as an array.
	 */
	public synchronized void setElements(GameInfo [] gis)
	{
		elements = gis;

		/* Since the content has changed, we need to raise an event to
		 * all registered list data listener.
		 */
		Iterator it = listeners.iterator();
		while(it.hasNext())
		{
			((ListDataListener)it.next()).contentsChanged( new ListDataEvent(
					this,ListDataEvent.CONTENTS_CHANGED,0,elements.length-1 ) );
		}
	}
}
