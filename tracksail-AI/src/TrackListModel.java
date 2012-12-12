import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * <code>TrackListModel</code> is a model for <code>JComboBox</code>.
 * It implements the <code>ComboBoxModel</code> interface. This model contains a
 * list of the names of the tracks available. Track names are all given at the
 * same time with the <code>setElements(String [] trks)</code> method. The
 * selected track can be queried with the <code>Object getSelectedItem()</code>
 * method, which always returns a <code>String</code> object.
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Tracksail
 */
public class TrackListModel implements ComboBoxModel
{
	/** list of registered list data listeners */
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
	 * @param l <code>ListDataListener</code> object
	 */
	public synchronized void removeListDataListener(ListDataListener l) { listeners.remove(l); }

	/** list elements (track names) */
	private String [] elements = null;

	/**
	 * Returns the name of the track with the given index.
	 * 
	 * @param index index of the track to return the name of
	 * @return Name of the track with the given index.
	 */
	public synchronized Object getElementAt(int index) { return elements[index]; }

	/**
	 * Returns the number of the tracks in this list.
	 * 
	 * @return Number of tracks in this list.
	 */
	public synchronized int getSize() {
		if( elements==null) return 0;
		return elements.length;
	}

	/**
	 * Sets the content of the model (track names).
	 * <p>
	 * Everything is done directly with one array.
	 * 
	 * @param trks tracks as a <code>String</code> array
	 */
	public synchronized void setElements(String [] trks)
	{
		// set new content
		elements = trks;

		// Since the content was changed, we need to raise an event for
		// all registered list data listeners.
		Iterator it = listeners.iterator();
		while(it.hasNext())
		{
			((ListDataListener)it.next()).contentsChanged( new ListDataEvent(
					this,ListDataEvent.CONTENTS_CHANGED,0,elements.length-1 ) );
		}
	}

	/** the track that's selected in the <code>ComboBox</code> */
	private Object selected = new String("default");

	/**
	 * Returns the name of the selected track.
	 * 
	 * @return Name of the selected track.
	 */
	public synchronized Object getSelectedItem()
	{
		return selected;
	}
	/**
	 * Sets the selected track.
	 *
	 * @param item Name of the selected track.
	 */
	public synchronized void setSelectedItem(Object item)
	{
		selected = item;
	}

}
