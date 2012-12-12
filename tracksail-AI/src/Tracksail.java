import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;



/**
 * Class <code>Tracksail</code> implements the client program of Tracksail.
 * <p>
 * When the program is started, the user is asked for the player name and host
 * machine. A connection to the host is attempted an if the attempt is
 * successful, we get a server object that implements the 
 * <code>ServerInterface</code>. Any calls to the server are done via this
 * interface.
 * <p>
 * If the connection and permission are received, the client program is
 * initialised and a Swing-based GUI is constructed with a <code>JFrame</code>
 * frame as it's root.
 * <p>
 * After the program has been started, there are two main states: lobby and
 * race. Depending on the current state, the frame shows either a 
 * <b>lobby room</b> or a <b>race room</b>.
 * <p>
 * - In <b>lobby room</b> we have a largish chat-box in the middle. At the
 *   bottom, we have a text field where a message can be written. The message
 *   can be sent either by pressing return or by clicking on the 'Say'-button.
 *   On the right-hand side there is a race panel, which has a list of the 
 *   current races, buttons to join an existing race or create a new race and a
 *   text field where a name for the race to create is written.
 *<p>
 * - In <b>race room</b> we have a large sea-view (<code>render_view</code>),
 *   where all the race graphics are drawn. At the bottom, there are '&lt;-' and 
 *   '-&gt;' buttons, with which the player can turn his/her boat, and a slider
 *   that controls the sail. Under these we have a text field and a 'Say'
 *   button, which work as they do in the lobby. In the race room, chat messages
 *   are shown in the <code>render_view</code> area, instead of a separate chat
 *   area, and only a few of the latest messages are shown at a time.
 * <p>
 * <code>Tracksail</code> implements the <code>ClientInterface</code>, which is
 * an interface for making method calls from the server to the clients. This
 * client interface object is sent to the server during the intialisation of the
 * connection as one parameter in the server call (see <code>login()</code>).
 * <p>
 *
 * @author Stefan Brockmann, Tero Kuusela
 * @version 1.0-rc2
 *
 * @see ClientInterface
 * @see Player
 * @see Game
 * @see Server
 * @see Track
 */
public class Tracksail extends UnicastRemoteObject implements ClientInterface
{
	public static final String version = "Tracksail client version 1.0-rc2";

	/** Name of the host */
	private String host_name = "localhost";

	/** Player name */
	private String player_name = "Dooku";

	/** Player's unique id */
	private int player_id;

	/** Unique id of the race the player is in (if we are in one) */
	private int game_id = 0;

	/** Current state of the race (if we are in one) */
	Game game = null;

	/** Is this player the master of the current race? */
	private boolean is_master = false;

	/** Main window */
	protected JFrame frame;

	/** Are we in a lobby room or in a race room?
	 * True means we're in a lobby, false means we're in a race */
	boolean is_lobby = true;

	/** Tells if we're still executing the program.
	 * We exit the game loop when this variable is set to false.
	 */
	boolean active = false;

	/** The latest chat messages.
	 * These are for the race room, only the latest messages are shown.
	 */
	private ArrayList chat_text = new ArrayList();


	/** An interface used to call the remote methods of the server */
	ServerInterface ts_server;





	/* ****************************************************************************
	 * GUI COMPONENTS
	 *************************************************************************** */

	/** Panel that's visible when in lobby room */
	JPanel lobby_panel;

	/** Uneditable text field for the program to write information to */
	JTextField info_text;
	/** Multiline text area where chat messages are output to */
	JTextArea chat_messages;
	/** Text field for the player to write chat messages to */
	JTextField say_text;
	/** Button that sends a chat message from this player */
	JButton say_it;
	/** List of the races available */
	JList game_list;
	/** Button to join a race */
	JButton join_button;
	/** Button to create a new race */
	JButton create_game_button;
	/** Text field for the name of a new race */
	JTextField game_name_text;
	/** Model for the race list */
	GameListModel game_list_model;
	/** Button to leave the program if we're at the lobby room.
	 * If we're racing, this button exits to the lobby room.
	 */
	JButton exit_button;
	/** This makes the text area to scroll up so only the latest messages are seen at the bottom */
	JScrollPane chat_scroll;

	/** This is the panel that's visible when we're racing */
	JPanel race_panel;

	/** Racing view.
	 * This is where we draw the race graphics. */
	JRenderView render_view;
	/** Button to start the race */
	JButton start_button;
	/** List to change the track */
	JComboBox track_list;
	/** Model for the track list */
	TrackListModel track_list_model;
	/** Button to turn the boat portboard */
	JButton turn_left_button;
	/** Button to turn the boat starboard */
	JButton turn_right_button;
	/** Slider to set the sail control rope tightness */
	JSlider sail_slider;

	/** Main panel where all other content is placed */
	JPanel main_panel;




	/* *****************************************************************************
	 * MAIN
	 **************************************************************************** */

 	/**
	 * This is where the execution begins.
	 *
	 * @param args command line parameters
	 */
	public static void main(String[] args)
	{
		Tracksail game=null;

		try
		{
			game = new Tracksail();

			game.init();

			// execute game loop
			game.game_loop(); // exit when active == false
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		finally
		{
			if(game!=null) game.uninit();
			System.exit(0);
		}
	}


	/**
	 * Constructor.
	 */
	public Tracksail() throws RemoteException
	{
		super(); // UnicastRemoteObject builder

		// create the program frame
		frame = new JFrame(version);
	}

	/**
	 * Initialize program.
	 * <p>
	 * Show startup dialog, construct the GUI and connect to a server.
	 */
	public void init() throws Exception
	{
		// Show a dialog where the player's name and server address are asked
		/*StartupDialog d = new StartupDialog(frame);

		if( d.doModal()==false ) throw new Exception("Startup Dialog cancelled or failed.");

		player_name = d.getPlayerName();
		host_name = d.getHostName();*/

		// construct the GUI
		createGUI();

		// connect to a server
		connect();
                
                			try {
				String game_name = "My game room";
				// Get the race name from the text field
				Document doc = game_name_text.getDocument();
				if( doc.getLength()>1 )
					game_name = doc.getText(0,doc.getLength());

				// Creates a new race and joins it.
				// TODO: Should we allow races with same names? I would say no.
				game_id = ts_server.createGame(player_id,game_name);

				// Returns the id of the created race
				if( game_id>=0 )
				{
					is_master=true;

					// The default track is loaded by
					// default
					track_list_model.setSelectedItem("default");

					switchToGame();

					// Tell the audience that we have
					// created a new race
					ts_server.sayToLobby(player_name+" created game "+game_name+".");
				}
			}
			catch(BadLocationException l) {}
			catch(RemoteException re) { error(re.getMessage()); }
                                        ts_server.startGame(game_id);
	}


	/**
	 * Establishes a RMI connection to the server.
	 *
	 * @return true if connection was successfully established, 
	 *         false if the connection could not be established
	 */
	public void connect() throws Exception
	{
		ts_server = (ServerInterface)Naming.lookup("rmi://"+host_name+"/TS_Server");

		// Call our own login method, which returns to us our id
		player_id = ts_server.loginPlayer(player_name,this);

		// Tell the players connected to the server that we arrived
		ts_server.sayToLobby(player_name+" joined lobby.");

		// throws MalformedURLException, RemoteException or NotBoundException
	}

	/**
	 * Logs a player out from the server.
	 */
	public void uninit()
	{
		try {
			if(ts_server!=null) {
				ts_server.sayToLobby(player_name+" left lobby.");
				ts_server.logoutPlayer(player_id);
			}
		}
		catch(RemoteException re) {}
	}

	/**
	 * Common error handler.
	 * <p>
	 * Prints out the error message and exits the program (force exit).
	 *
	 * @param str error message
	 */
	public void error(String str)
	{
		uninit();

		System.out.println("Error: "+str);
		System.exit(0);
	}





	/* *****************************************************************************
	 * GUI CONSTRUCTION
	 **************************************************************************** */

	/**
	 * This method constructs the client GUI.
	 */
	public void createGUI()
	{
		/* big text area in the lobby room */
		chat_messages = new JTextArea(20,40);
		chat_messages.setEditable(false);
		chat_scroll = new JScrollPane(chat_messages);
		chat_messages.setToolTipText("Read what others have said :-p");


		/* games_panel is a panel on the left that shows a list of the 
		 * available races, button to join a race or create one and a
		 * text field for the name of the race to create.
		 */
		JPanel games_panel = new JPanel();
		games_panel.setLayout(new BorderLayout());

		/* Button to join a race. */
		join_button = new JButton("Join Game");
		join_button.addActionListener(new JoinGameActionListener());
		join_button.setToolTipText("Select game & click Join");

		/* Button to create a new race. */
		create_game_button = new JButton("Create Game");
		create_game_button.addActionListener(new CreateGameActionListener());
		create_game_button.setToolTipText("Click to create new game");

		game_name_text = new JTextField("My game room", 10);
		game_name_text.setToolTipText("Game name if you create new game");
		
		// This panel is used to lay out the "create" and "join" buttons
		JPanel button_panel = new JPanel();
		button_panel.setLayout(new GridLayout(3,1));
		button_panel.add(game_name_text);
		button_panel.add(create_game_button);
		button_panel.add(join_button);
		games_panel.add(button_panel,"North");

		/* List that shows all available races */
		game_list_model = new GameListModel();
		game_list = new JList(game_list_model);
		game_list.setFixedCellWidth(200);
		JScrollPane game_list_scroll = new JScrollPane(game_list);
		games_panel.add(game_list_scroll,"Center");


		/* lobby_panel is visible when we are in the lobby room */
		lobby_panel = new JPanel();
		lobby_panel.setLayout(new BorderLayout());
		lobby_panel.add(chat_scroll,"Center");
		lobby_panel.add(games_panel,"East");


		/* Sea window. */
		render_view = new JRenderView();

		/* Button to start a race */
		start_button = new JButton("Start game");
		start_button.addActionListener(new StartGameActionListener());
		/* List to pick a track. */
		String [] trks = { "default" };
		track_list_model = new TrackListModel();
		track_list_model.setElements(trks);
		track_list = new JComboBox(track_list_model);
		track_list.addItemListener(new ChangeTrackItemListener());

		JPanel game_panel = new JPanel();
		game_panel.add(new JLabel("Select track"));
		game_panel.add(track_list);
		game_panel.add(new JSeparator());
		game_panel.add(start_button);

		/* control_panel has all the controls for the boat */
		JPanel control_panel = new JPanel();
		
		Action turn_left_action = new TurnLeftAction ("<");
		turn_left_button = new JButton (turn_left_action);
		turn_left_button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put 
			(KeyStroke.getKeyStroke (KeyEvent.VK_Z, 0), "goleft");
		turn_left_button.getActionMap().put( "goleft", turn_left_action);

		Action turn_right_action = new TurnRightAction (">");
		turn_right_button = new JButton (turn_right_action);
		turn_right_button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put 
			(KeyStroke.getKeyStroke (KeyEvent.VK_X, 0), "goright");
		turn_right_button.getActionMap().put( "goright", turn_right_action);

		sail_slider = new JSlider(0, 100, 50);
		sail_slider.addChangeListener(new SailChangeListener());

		control_panel.add(new JLabel("Turn:"));
		control_panel.add(turn_left_button);
		control_panel.add(turn_right_button);
		control_panel.add(new JLabel("Sail:"));
		control_panel.add(sail_slider);

		/* race_panel is the panel visible during a race */
		race_panel = new JPanel();
		race_panel.setLayout(new BorderLayout());
		race_panel.add(game_panel,"North");
		race_panel.add(render_view,"Center");
		race_panel.add(control_panel,"South");


		/* at the top of the screen, we always show an info_text label
		 * and an exit button */
		info_text = new JTextField("Player: "+player_name,40);
		info_text.setEditable(false);
		exit_button= new JButton("Exit");
		exit_button.addActionListener(new ExitActionListener());
		exit_button.setToolTipText("Click here to exit");
		JPanel top_panel = new JPanel(); // info_text and exit button to the same row
		top_panel.add(info_text); top_panel.add(exit_button);

		/* at the bottom of the screen, we always have a text field for writing
		 * messages and a button to send them */
		say_text = new JTextField("Hi all!",40);
		say_text.setToolTipText("Write your message here and hit Enter.");
		say_it = new JButton("Say");
		say_it.setToolTipText("Click here to say.");
		SayItActionListener say_action = new SayItActionListener();
		say_text.addActionListener(say_action);
		say_it.addActionListener(say_action);
		JPanel say_panel = new JPanel(); // say_text and say_it to the same row
		say_panel.add(say_text); say_panel.add(say_it);



		main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());
		main_panel.add(top_panel,"North");
		main_panel.add(say_panel,"South");

		/* at the center of the main panel, we'll first have the 
		 * lobby_panel. During the game we then switch between
		 * race_panel and lobby_panel depending on the situation. */

		main_panel.add(lobby_panel,"Center");

		/* add main_panel to our JFrame */
		frame.getContentPane().add(main_panel);

		frame.addWindowListener(new WindowCloser());
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Changes from lobby state to racing state.
	 */
	public void switchToGame()
	{
		/* change lobby_panel to race_panel */
		main_panel.remove(lobby_panel);
		main_panel.add(race_panel,"Center");
		frame.pack();
		is_lobby = false; /* exit lobby state */

		// Only the master player can choose a track and start the race.
		// These components are disabled from others.
		track_list.setEnabled(is_master);
		start_button.setEnabled(is_master);

	}

	/**
	 * Changes from racing state to lobby state.
	 */
	public void switchToLobby()
	{
                System.exit(0);
		/* change race_panel to lobby_panel */
		main_panel.remove(race_panel);
		main_panel.add(lobby_panel,"Center");
		frame.pack();

		is_lobby = true; /* enter lobby state */
		is_master = false;
		game = null;

		try{ ts_server.sayToLobby(player_name+" back in lobby."); }
		catch(RemoteException re) { error(re.getMessage()); }
	}







	/* *****************************************************************************
	 * GAME LOOP
	 **************************************************************************** */

	/**
	 * Game loop.
	 */
	public void game_loop()
	{

		active = true;

		try
		{

			// We execute the loop as long as (active == true)
			while(active)
			{

				if( is_lobby )
				{
					// In lobby, the game loop is useless,
					// so we take things slow.
					try { Thread.sleep(1000); }
					catch(InterruptedException e) {}
				}
				else // we're racing
				{

					// Each frame, we ask an updated game
					// object (current race state).
					game = ts_server.getGame(game_id);

					// If game == null, the race doesn't
					// exist anymore (e.g. master has ended
					// the race). Move to lobby...
					if( game==null )
					{
						switchToLobby();
					}
					else
					{
						// render the race
						render_view.repaint();
					}

					// Just a small pause
					try { Thread.sleep(100); }
					catch(InterruptedException e) {}

				}

			}

		}
		catch(RemoteException re)
		{
			error(re.getMessage());
		}

	}



	/* *****************************************************************************
	 * EVENT HANDLERS
	 **************************************************************************** */

	/**
	 * Handles events that are generated when the player clicks the 'say'
	 * button or hits enter with focus in the chat input text area.
	 */
	class SayItActionListener implements ActionListener {
		/**
		 * Reads the message written in the text field and sends it to
		 * other players through the server.
		 * <p>
		 * If we're in the lobby room, the messages go to the lobby room.
		 * If we are in a race, the texts go to that particular race.
		 *
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(ActionEvent e) {
			Document doc = say_text.getDocument();
			if( doc.getLength() <1 ) return; /* don't allow empty messages */
			try {
				String str = player_name + ": " + doc.getText(0,doc.getLength());

				if( is_lobby ) ts_server.sayToLobby(str);
				else ts_server.sayToGame(game_id,str);

				doc.remove(0,doc.getLength()); // clear the text field
			}
			catch(BadLocationException l) {}
			catch(RemoteException re) { error(re.getMessage()); }

			// give the focus back to the text field so the player
			// can write directly to it again
			say_text.grabFocus();
		}
	}
	/**
	 * Handles events that are generated when the player closes the window.
	 */
	public class WindowCloser extends WindowAdapter {
		/**
		 * Release the system resources reserved by the window and give
		 * permission to leave the program.
		 * 
		 * @param e <code>WindowEvent</code> event
		 */
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
			active = false;
		}
	}
	/**
	 * Handles clicking the 'Exit' button.
	 */
	class ExitActionListener implements ActionListener {
		/**
		 * If we're in the lobby room, exit the program.
		 * <p>
		 * If we're in a race, exit from the race to the lobby room.
		 * 
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(ActionEvent e) {
			if( is_lobby )
			{ // we're in lobby room => exit program
				active = false;
			}
			else
			{ // we're in race => go to lobby room
				try { ts_server.leaveGame(player_id,game_id); }
				catch(RemoteException re) { error(re.getMessage()); }

				switchToLobby();
			}
		}
	}
	/**
	 * Handles clicking the 'join game' button.
	 * <p>
	 * If some race is selected in the race list, attempt to join that race.
	 */
	class JoinGameActionListener implements ActionListener {
		/**
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(ActionEvent e) {
			GameInfo info = (GameInfo)game_list.getSelectedValue();
			if( info!=null ) {
				try {
					// Call the method to join a race
					if( ts_server.joinGame(player_id,info.id) )
					{
						is_master = false;
						game_id = info.id;
						switchToGame();
						// Tell the audience that we
						// joined the race
						ts_server.sayToLobby(player_name+" joined game "+info.name+".");
					}
				}
				catch(RemoteException re) { error(re.getMessage()); }
			}
		}
	}

	/**
	 * Handles clicking the 'create game' button.
	 */
	class CreateGameActionListener implements ActionListener {
		/**
		 * Attempt to create a race in the server for this player.
		 * <p>
		 * We read from the text field the requested name for the race
		 * and create a race with that name to the server. If the server
		 * returns ok, we move to the racing state and the player is set
		 * as the master player for the created race.
		 * 
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				String game_name = "My game room";
				// Get the race name from the text field
				Document doc = game_name_text.getDocument();
				if( doc.getLength()>1 )
					game_name = doc.getText(0,doc.getLength());

				// Creates a new race and joins it.
				// TODO: Should we allow races with same names? I would say no.
				game_id = ts_server.createGame(player_id,game_name);

				// Returns the id of the created race
				if( game_id>=0 )
				{
					is_master=true;

					// The default track is loaded by
					// default
					track_list_model.setSelectedItem("default");

					switchToGame();

					// Tell the audience that we have
					// created a new race
					ts_server.sayToLobby(player_name+" created game "+game_name+".");
				}
			}
			catch(BadLocationException l) {}
			catch(RemoteException re) { error(re.getMessage()); }
		}
	}

	/**
	 * Handles clicking the 'start game' in a race.
	 */
	class StartGameActionListener implements ActionListener {
		/**
		 * If we are the master player, begin racing.
		 * <p>
		 * The button is disabled for everyone but the master player, so
		 * this event should never occur with others.
		 * 
		 * @param e <code>ActionEvent</code> tapahtuma
		 */
		public void actionPerformed(ActionEvent e) {
		    try { if(is_master) ts_server.startGame(game_id); }
		    catch(RemoteException re) { error(re.getMessage()); }
		}
	}

	/**
	 * Handles clicking the '&lt;' button.
	 */
	class TurnLeftAction extends AbstractAction {
		/**
		 * Constructor.
		 *
		 * @param text description string
		 */
		public TurnLeftAction (String text) {
			super (text);
		}
		
		/**
		 * Send a navigation command to the server to turn the player's
		 * boat portboard.
		 * 
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(ActionEvent e) {
		    try {
		        System.err.println("Turning Left");
		        ts_server.turnLeft(player_id); 
		     }
		    catch(RemoteException re) { error(re.getMessage()); }
		}
	}
	/**
	 * Handles clicking the '&gt;' button.
	 */
	class TurnRightAction extends AbstractAction {
		/**
		 * Constructor.
		 *
		 * @param text description string
		 */
		public TurnRightAction (String text) {
			super (text);
		}
		
		/**
		 * Send a navigation command to the server to turn the player's
		 * boat starboard.
		 *
		 * @param e <code>ActionEvent</code> event
		 */
		public void actionPerformed(ActionEvent e) {
		    try { 
                System.err.println("Turning Right");
		        ts_server.turnRight(player_id); 
		     }
		    catch(RemoteException re) { error(re.getMessage()); }
		}
	}

	/**
	 * Handles changes in the slider controlling the sail.
	 */
	class SailChangeListener implements ChangeListener {
		/**
		 * Sends the new sail value of the player to the server.
		 * 
		 * @param e <code>ItemEvent</code> event
		 */
		public void stateChanged(ChangeEvent e) {
		    try
		    {
	            ts_server.setSail(player_id,sail_slider.getValue()); 
	            System.out.println("Setting sail to " + sail_slider.getValue() + " for player " + player_id);
	            Player player = ts_server.getPlayer(player_id);
	            System.out.println("got handle on player");
	            int wind = ts_server.getGame(game_id).getWindDirection();
	            int heading = player.getDirection();
	            System.out.println("absolute wind direction " + wind);
	            System.out.println("relative wind direction " + (wind - heading));
	            System.out.println("absolute sail direction " + player.calcSailDirection(wind));
		    }
		    catch(RemoteException re) { error(re.getMessage()); }
		}
	}
	/**
	 * Handles the changes of the choice in the track list.
	 */
	class ChangeTrackItemListener implements ItemListener {
		/**
		 * The master player has chosen a new track from the track list.
		 * <p>
		 * Sends a request to the server to change to the track selected
		 * by the master player.
		 * 
		 * @param e <code>ItemEvent</code> tapahtuma
		 */
		public void itemStateChanged(ItemEvent e) {
			if(is_master && e.getStateChange()==ItemEvent.SELECTED)
			{
				String track_name = (String)e.getItem();
				try { ts_server.changeTrack(game_id,track_name); }
				catch(RemoteException re) { error(re.getMessage()); }
			}
		}
	}


	/* *****************************************************************************
	 * IMAGE PROCESSING HELPERS
	 **************************************************************************** */

	/**
	 * Loads an image from a file.
	 * <p>
	 * Waits until the image is fully loaded before returning it as an image
	 * object.
	 *
	 * @param filename name of the image file to load
	 * @return Image object.
	 */
	public BufferedImage getBufferedImage(String filename) {
		Image image = Toolkit.getDefaultToolkit().getImage(filename);

		MediaTracker tr = new MediaTracker(new JComponent() {});
		tr.addImage(image,0);
		try { tr.waitForID(0); }
		catch( InterruptedException e) {}

		// Convert to BufferedImage
		BufferedImage b_image = new BufferedImage(image.getWidth(frame),image.getHeight(frame),
			BufferedImage.TYPE_INT_ARGB);
		Graphics g = b_image.createGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();

		return b_image;
	}

	/**
	 * Creates a rotated image series.
	 * <p>
	 * E.g. if an image series contains 36 images, we create 36 images out
	 * of the original image, of which the first one is rotated by 0
	 * degrees, next by 10, then by 20 and so on. When done, the series has
	 * images to rotate the original by full 360 degrees.
	 *
	 * @param filename name of the image file to generate the series from
	 * @param num_images how many images the series is to contain
	 * @return An array reference to the image series. The array contains
	 *         num_images of BufferedImage objects.
	 */
	public BufferedImage [] createRotatedImages(String filename,int num_images) {

		BufferedImage image = getBufferedImage(filename);
		BufferedImage [] images = new BufferedImage[num_images];

		double w = image.getWidth(frame);
		double h = image.getHeight(frame);

		for(int i=0; i<num_images; i++)
		{
			// '-' to get the 'correct' rotation direction
			double rad = -Math.PI*2.0*(double)i/(double)num_images;
			// rotation transformation
			AffineTransform rot = AffineTransform.getRotateInstance(rad);
			// rotate in respect to the center
			rot.concatenate( AffineTransform.getTranslateInstance(-w/2.0,-h/2.0) );
			// translate back to the image
			rot.preConcatenate( AffineTransform.getTranslateInstance(w/2.0,h/2.0) );

			BufferedImageOp rotOp = new AffineTransformOp(rot,AffineTransformOp.TYPE_BILINEAR);
			images[i] = new BufferedImage(image.getWidth(frame),image.getHeight(frame),image.getType());
			rotOp.filter(image,images[i]);
		}

		return images;
	}


	/* ****************************************************************************
	 * DRAWING COMPONENT OF THE GAME
	 * *************************************************************************** */

	/**
	 * Custom GUI component, a drawing area on which all graphics of the
	 * game are drawn.
	 */
	class JRenderView extends JComponent
	{
		/** size of the window in pixels */
		private Dimension size = new Dimension(500,250);

		/** image series for the boat */
		private BufferedImage [] ship_images; // boat
		/** image series for the sail */
		private BufferedImage [] sail_images; // sail
		/** image series for the wind sock */
		private BufferedImage [] wind_images; // wind sock that shows the direction of the wind
		/** image series for the guiding arrow */
		private BufferedImage [] direction_images; // an arrow to show the direction of the next gate
		/** image of sea waves */
		private BufferedImage seatile_image; // sea tile
		/** image of red buoy */
		private BufferedImage red_image; // red buoy
		/** image of green buoy */
		private BufferedImage green_image; // green buoy

		/**
		 * Constructor.
		 * <p>
		 * Initialises the drawing component.
		 */
		public JRenderView()
		{
                	ship_images = createRotatedImages("../images/ship32.png",36);
                	sail_images = createRotatedImages("../images/sail32.png",36);
                	wind_images = createRotatedImages("../images/wind.png",36);
                	direction_images = createRotatedImages("../images/direction.png",36);
                	seatile_image = getBufferedImage("../images/seatile.png");
                	red_image = getBufferedImage("../images/red.png");
                	green_image = getBufferedImage("../images/green.png");
        }

		/**
		 * Returns the minimum size of this component.
		 *
		 * @return Minimum size of this component.
		 */
		public Dimension getMinimumSize() { return size; }

		/**
		 * Returns the ideal size of this component.
		 *
		 * @return Ideal size of this component.
		 */
		public Dimension getPreferredSize() { return size; }


		/**
		 * Tells how many pixels on the screen represent one metre.
		 * <p>
		 * If e.g. the boat is 5m long and it's length in the image is
		 * 64 pixels, we get a multiplier of about
		 * 64 px / 5 m = 12.8 px/m.
		 */
		private float pix_per_m = 32.0f/5.0f;

		/**
		 * The point in the world where we direct our view to.
		 * <p>
		 * This is the point in the world that we see in the center of
		 * the screen. The idea is to stay centered on one's own boat.
		 */
		private Vector2 look_at = new Vector2(0,0);

		/**
		 * Set the center of our view.
		 *
		 * @param at the target location of our view
		 */
		public void lookAt(Vector2 at) { look_at.set(at); }

		/**
		 * Calculate the x coordinate of a point of the world on screen
		 * (pixel position).
		 *
		 * @param x x coordinate in the world
		 */
		public int worldToScreenX(float x) {
			float f = (float)this.getWidth()/2.0f  + (x-look_at.getX()) * pix_per_m;
			return (int)f;
		}
		/**
		 * Calculate the y coordinate of a point of the world on screen
		 * (pixel position).
		 *
		 * @param y y coordinate in the world
		 */
		public int worldToScreenY(float y) {
			float f = (float)this.getHeight()/2.0f - (y-look_at.getY()) * pix_per_m;
			return (int)f;
			// Note: on screen, y grows downwards, but we do our
			// calculations using a plane where y grows upwards, so
			// before we draw we change the sign if needed.
		}

		/**
		 * Draw the image so that the center of the image is at (x, y).
		 *
		 * @param g Graphics
		 * @param img the image to draw
		 * @param x x coordinate of the image center on screen
		 * @param y y coordinate of the image center on screen
		 */
		public void drawImage(Graphics g,BufferedImage img,int x,int y)
		{
			g.drawImage(img,x-img.getWidth(this)/2,y-img.getHeight(this)/2,this);
		}

		/**
		 * Draw one image from a rotated image series.
		 *
		 * @param g Graphics
		 * @param images an image series of 36 rotated images
		 * @param x x coordinate of the image center on screen
		 * @param y y coordinate of the image center on screen
		 * @param direction the direction of the image
		 */
		public void drawRotatedImage(Graphics g,BufferedImage [] images,int x,int y,int direction)
		{
                	int rot = ((direction+5)/10)%36;
                	if(rot<0) rot+=36; // d'oh!
                	BufferedImage img = images[rot];
                	g.drawImage(img,x-img.getWidth(this)/2,y-img.getHeight(this)/2,this);
		}

		/**
		 * Draw the sea.
		 * <p>
		 * The sea is drawn with 64x64 pixel tiles that scroll on the
		 * background.
		 *
		 * @param g Graphics
		 */
		public void drawSea(Graphics g)
		{
			int p_x = (worldToScreenX(0.0f)%64)-64;
			int p_y = (worldToScreenY(0.0f)%64)-64;

			Rectangle bounds = g.getClipBounds();

			BufferedImage img = seatile_image;
			for(int x=p_x; x<bounds.width; x+=64)
				for(int y=p_y; y<bounds.height; y+=64)
					g.drawImage(img,x,y,this);
		}

		/**
		 * Draw the track with buoys.
		 *
		 * @param g Graphics
		 */
		public void drawTrack(Graphics g)
		{
			Track data = null;
			if( game != null ) data = game.getTrack(); // save the pointer
			if(data==null) {
			    g.setColor(Color.red);
			    g.drawString("Track not available!",100,100);
				return;
			}

			// draw all buoys
			for(int i=0; i<data.getPortCount(); i++)
			{
			    Vector2 l =  data.getPortPB(i); // left = portboard
			    Vector2 r = data.getPortSB(i); // right = starboard

			    drawImage(g,red_image,worldToScreenX(l.getX()),worldToScreenY(l.getY()));
			    drawImage(g,green_image,worldToScreenX(r.getX()),worldToScreenY(r.getY()));
			}

		}

		/**
		 * Draw a component.
		 *
		 * @param g Graphics
		 */
		public void paintComponent(Graphics g)
		{
			drawSea(g);
			drawTrack(g);

			// height of the text
			int text_h = g.getFontMetrics().getHeight();

			// If a race is available, draw the boats and other
			// objects from it.
			Game gd = game;
			if( gd!=null )
			{
				// iterator for the players
				Iterator pit = gd.getPlayersIterator();

				// Find our own boat. This is needed, because
				// our view follows it. Note: if for some reason
				// we can't find our own boat, we follow the
				// boat 0.
				Player oma = (Player)pit.next(); // There should always be at least one!
				while(pit.hasNext())
				{
					Player p = (Player)pit.next();
					if( p.getId()==player_id ) oma = p;
				}

				// look at our own boat
				lookAt( oma.getPosition() );

				// iterate through all the players
				pit = gd.getPlayersIterator();
				while(pit.hasNext())
				{
					Player p = (Player)pit.next();

					// calculate the position of the boat on
					// screen
					int x = worldToScreenX( p.getPosition().getX() );
					int y = worldToScreenY( p.getPosition().getY() );

					// draw the boat
					drawRotatedImage(g,ship_images,x,y,p.getDirection());

					// draw the sail
					drawRotatedImage(g,sail_images,x,y,
					p.calcSailDirection(gd.getWindDirection()) );
                                        



					// write the player name above the boat
					int s_w = g.getFontMetrics().stringWidth( p.getName() );
					g.setColor(Color.white);
					g.drawString(p.getName(),x-(s_w/2),y-28);
                                        
                                        float vel = p.getVelocity();
                                        int   ivel = (int)vel;
                                        int   dvel = (int)(vel*10.0f) % 10;
                                        g.drawString("" + ivel + "." + dvel + "m/s", 200,text_h*3 );

					Track t = gd.getTrack();
					if( p == oma && t != null )
					{

						// if we're racing, show some
						// information
						if( !p.isFinished() )
						{
							// show how many laps we
							// have completed
							if( p.getCurLap() <= t.getNumberOfLaps() )
								g.drawString("Lap "+p.getCurLap() + "/"
										+ t.getNumberOfLaps(),200,text_h*2 );

							// Calculate the position of the next gate (the middle
							// point between the buoys) and draw and arrow to point
							// towards it.
							Vector2 tgt = Vector2.average(
									t.getPortPB(p.getNextPort()),
									t.getPortSB(p.getNextPort()) );
							// subtract the position
							// of our own boat
							tgt.sub(p.getPosition());
							// set length as 1.0f
							tgt.normalize();

							 // Draw an arrow next to the boat to
							 // point to the current direction.
							drawRotatedImage(g,direction_images,
									x + (int)(tgt.x*50.0f),
									y - (int)(tgt.y*50.0f),(int)Math.toDegrees( 
										tgt.getDirection() ));
							// show the distance to
							// the gate
							g.drawString((int)p.getDistToNextPort()+"m",
									x + (int)(tgt.x*50.0f),
									y - (int)(tgt.y*50.0f));
						}
					}
				}

				String [] race_result = gd.getRaceResult();

				// Don't show the results list until the first
				// player has reached the goal.
				if(race_result!=null && race_result.length>0 && race_result[0] != null && 
						race_result[0].length()>0)
				{
					g.drawString("Race Results:",0,(getHeight()/2)-text_h);
					for(int i=0; i<race_result.length; i++)
					{
					 	if( race_result[i] != null && race_result[i].length()>0 )
							g.drawString((i+1)+". "+race_result[i],0,(getHeight()/2)+
									(i*text_h));
					}
				}


				// Show the wind bearing and velocity.
				drawRotatedImage(g,wind_images,50,50,gd.getWindDirection());
				g.drawString(gd.getWindVelocity()+"m/s",20,20);

				// Write a chat message to the bottom left area
				// of the screen.
				g.setColor(Color.white);
				for(int i=0; i<chat_text.size(); i++ )
				{
					g.drawString((String)chat_text.get(i),0,getHeight()-(chat_text.size()-i)*text_h);
				}

				// show the race status
				g.drawString(gd.getGameStatus(),200,text_h);


			} else {
				g.setColor(Color.red);
				g.drawString("Game object not yet available!",10,50);
			}
		}
	}


	/************************************************************************************
	 * IMPLEMENTATION OF ClientInterface
	 ************************************************************************************/

	/**
	 * Sends a message to the client.
	 *
	 * @param str message
	 */
	public void sendText(String str) throws RemoteException
	{
			if( is_lobby )
			{
				Document doc = chat_messages.getDocument();
				try { doc.insertString(doc.getLength(), str + "\n", null); }
				catch(BadLocationException e) {}

				// scrolls down text automatically
				JScrollBar sb = chat_scroll.getVerticalScrollBar();
				if( sb!=null ) sb.setValue(sb.getMaximum());
			}
			else
			{
				// if we're racing, also show the messages in
				// the racing area
				chat_text.add(str);
				// max of 5 messages on screen at once, older
				// ones are removed
				if( chat_text.size()>5 ) chat_text.remove(0);
			}
	}

	/**
	 * Kicks out a player.
	 * 
	 * @param message informational message printed to the console
	 */
	public void kick(String message) throws RemoteException
	{
		System.out.println (message);
		active = false;
	}

	/**
	 * Send an updated race list from the server to the client.
	 * <p>
	 * This is called for all players if a new race is created or an
	 * existing race is closed. This is also always called for a player when
	 * (s)he signs in to the server.
	 */
	public void updateGameList(GameInfo [] ri) throws RemoteException
	{
		game_list_model.setElements(ri);
	}

	/**
	 * Send an updated track list from the server to the client.
	 * <p>
	 * This is called for all players always when the track list changes.
	 * This is also always called for a player when (s)he signs in to the
	 * server.
	 */
	public void updateTrackList(String [] tracks) throws RemoteException
	{
		track_list_model.setElements(tracks);
	}

	/**
	 * The server tells the player the state of the race when it changes
	 * (racing/paused).
	 * <p>
	 * So the server calls this e.g. when a race begins or ends.
	 *
	 * @param racing true if the race begun, false if it ended.
	 */
	public void gameStateChange(boolean racing) throws RemoteException
	{
		// If we're the master player, enable the "start" button and
		// track chooser when the race begins/ends.
		if( is_master )
		{
			track_list.setEnabled(!racing);
			start_button.setEnabled(!racing);
		}
	}
}
