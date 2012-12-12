import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Class <code>StartupDialog</code> represents the dialog where we ask for the
 * player and host names.
 * <p>
 * The dialog is shown with the <code>boolean doModal()</code> method. There are
 * two text fields, of which the other asks for the player name and the other
 * for the host name. The dialog is shown as modal, so the execution of the rest
 * of the program won't continue until this dialog is closed. When the dialog is
 * closed, it has a state of either ok or not ok.
 * <p>
 * If, for some reason, the player pressed the "Cancel" button or the dialog
 * otherwise failed, the state is not ok and <code>doModal()</code> returns false.
 * <p>
 * If the player pressed "Start" and the text fields have content,
 * <code>doModal()</code> returns true.
 * <p>
 * The default name for the player is "Dooku" and the default host is
 * "localhost".
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see Tracksail
 */
public class StartupDialog extends JDialog
{
	/** cancel button */
	private JButton button_cancel = new JButton("Cancel");
	/** start button */
	private JButton button_start = new JButton("Start");

	/** text field for the player name */
	private JTextField text_name = new JTextField("Dooku",20);
	/** text field for the host name */
	private JTextField text_host = new JTextField("localhost",20);

	/**
	 * Reads the player name.
	 *
	 * @return Player name.
	 */
	public String getPlayerName() {
			// read the name from the text field
			Document doc = text_name.getDocument();
			try { return doc.getText(0,doc.getLength()); }
			catch(BadLocationException l) {}
			return null;
	}
	/**
	 * Reads the host name.
	 *
	 * @return Host name.
	 */
	public String getHostName() {
			// read the name from the text field
			Document doc = text_host.getDocument();
			try { return doc.getText(0,doc.getLength()); }
			catch(BadLocationException l) {}
			return null;
	}

	/**
	 * Stores the state of the dialog.
	 * <p>
	 * Ok = false if "Cancel" was pressed, true if "Start" was pressed
	 */
	private boolean ok = false;

	/**
	 * Constructor.
	 *
	 * @param parent <code>JFrame</code>
	 */
	public StartupDialog(JFrame parent)
	{
		super(parent,Tracksail.version+" - Startup");

		Container c = getContentPane();
		c.setLayout(new GridLayout(3,2));
		c.add(new JLabel("Your name:"));
		c.add(text_name);
		c.add(new JLabel("Host machine:"));
		c.add(text_host);

		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok = false; StartupDialog.this.setVisible(false);
			}});
		button_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok = true; StartupDialog.this.setVisible(false);
			}});

		c.add(button_cancel);
		c.add(button_start);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ok = false; StartupDialog.this.setVisible(false);
			}});

		pack();
	}

	/**
	 * Show the dialog.
	 * <p>
	 * The dialog is shown modal, so we don't return until it's closed.
	 *
	 * @return true if the player pressed the "Start" button, false if "Cancel".
	 */
	public boolean doModal()
	{
	 	ok = false;
		setModal(true);
		this.setVisible(true);
                this.setVisible(false);

		// Check if the text fields have proper content.
		// If they don't, set ok to false
		if(ok)
		{
			if( getPlayerName()==null || getPlayerName().length()<1 ) ok = false;
			if( getHostName()==null || getHostName().length()<1 ) ok = false;
		}
		return ok;
	}
}
