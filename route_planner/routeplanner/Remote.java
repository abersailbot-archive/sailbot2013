package routeplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import routeplanner.gui.*;
import routeplanner.datasource.udp.*;
import java.net.InetAddress;


public class Remote {
	JFrame frame;
	JButton button_gohome;
	JButton button_goway;
	JButton button_skipway;
	RoundButton button_start;
	RoundButton button_stop;
	RoundButton button_startgen;
	RoundButton button_stopgen;
	JButton button_forward;
	JButton button_back;
	JButton button_left;
	JButton button_right;
	JButton button_nudgeright;
	JButton button_nudgeleft;
	JButton button_hold;

	SendCommand sd;
	InetAddress idrobot;
	UdpDataSource udp;
	Icon ImgBoat = new ImageIcon("buttons/boat.gif");
	byte currentWp;
	
	public Remote(UdpDataSource Udp)//InetAddress idminty)
	{
		udp=Udp;
		
		double grid_size = 9.0;
		int frame_size_x = 525;
		int frame_size_y = 565;
		
		frame = new JFrame("Command Minty2");
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c;
		
		Container interieur = frame.getContentPane();
		interieur.setLayout(g);
		
		//Icon basics
		Icon imgGoHome = new ImageIcon("buttons/Gohome_vert.png");
		Icon imgGoWay = new ImageIcon("buttons/Goway_vert.png");
		Icon imgSkipWay = new ImageIcon("buttons/Skipway_vert.png");
		Icon imgStop = new ImageIcon("buttons/Stop_clair.png");
		Icon imgStart = new ImageIcon("buttons/Start_clair.png");
		Icon imgStopgen = new ImageIcon("buttons/Stopgen.png");
		Icon imgStartgen = new ImageIcon("buttons/Startgen.png");
		Icon imgLeft = new ImageIcon("buttons/Left_vert.png");
		Icon imgRight = new ImageIcon("buttons/Right_vert.png");
		Icon imgNudgeleft = new ImageIcon("buttons/Nudgeleft_vert.png");
		Icon imgNudgeright = new ImageIcon("buttons/Nudgeright_vert.png");
		Icon imgForward = new ImageIcon("buttons/Forward_vert.png");
		Icon imgBack = new ImageIcon("buttons/Backward_vert.png");
		Icon imgHold = new ImageIcon("buttons/Headinghold_vert.png");
		
		//Icon on rollover
		Icon imgGoHomeOver = new ImageIcon("buttons/Gohome_rouge.png");
		Icon imgGoWayOver = new ImageIcon("buttons/Goway_rouge.png");
		Icon imgSkipWayOver = new ImageIcon("buttons/Skipway_rouge.png");
		Icon imgStopOver = new ImageIcon("buttons/Stop_dark.png");
		Icon imgStartOver = new ImageIcon("buttons/Start_dark.png");
		Icon imgStopgenOver = new ImageIcon("buttons/Stopgen_dark.png");
		Icon imgStartgenOver = new ImageIcon("buttons/Startgen_dark.png");
		Icon imgLeftOver = new ImageIcon("buttons/Left_rouge.png");
		Icon imgRightOver = new ImageIcon("buttons/Right_rouge.png");
		Icon imgNudgeleftOver = new ImageIcon("buttons/Nudgeleft_rouge.png");
		Icon imgNudgerightOver = new ImageIcon("buttons/Nudgeright_rouge.png");
		Icon imgForwardOver = new ImageIcon("buttons/Forward_rouge.png");
		Icon imgBackOver = new ImageIcon("buttons/Backward_rouge.png");
		Icon imgHoldOver = new ImageIcon("buttons/Headinghold_rouge.png");
		
		//Icon onclick
		Icon imgGoHomeClick = new ImageIcon("buttons/Gohome_click.png");
		Icon imgGoWayClick = new ImageIcon("buttons/Goway_click.png");
		Icon imgSkipWayClick = new ImageIcon("buttons/Skipway_click.png");
		Icon imgStopClick = new ImageIcon("buttons/Stop_click.png");
		Icon imgStartClick = new ImageIcon("buttons/Start_click.png");
		Icon imgStopgenClick = new ImageIcon("buttons/Stopgen_click.png");
		Icon imgStartgenClick = new ImageIcon("buttons/Startgen_click.png");
		Icon imgLeftClick = new ImageIcon("buttons/Left_click.png");
		Icon imgRightClick = new ImageIcon("buttons/Right_click.png");
		Icon imgNudgeleftClick = new ImageIcon("buttons/Nudgeleft_click.png");
		Icon imgNudgerightClick = new ImageIcon("buttons/Nudgeright_click.png");
		Icon imgForwardClick = new ImageIcon("buttons/Forward_click.png");
		Icon imgBackClick = new ImageIcon("buttons/Backward_click.png");
		Icon imgHoldClick = new ImageIcon("buttons/Headinghold_click.png");
			
		//HOME
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridy=0;
		c.gridx=0;
		c.gridheight=2;
		c.gridwidth=3;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(10, 10, 15, 5);
		c.ipady=15;
		button_gohome = new JButton(imgGoHome);
		button_gohome.addActionListener(new MyAction());
		button_gohome.setBorderPainted(false);
		button_gohome.setFocusPainted(false);
		button_gohome.setContentAreaFilled(false);
		button_gohome.setRolloverIcon(imgGoHomeOver);
		button_gohome.setPressedIcon(imgGoHomeClick);
		interieur.add(button_gohome);
		g.setConstraints(button_gohome,c);
	
		
		//GOWAY
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridy=0;
		c.gridx=3;
		c.gridheight=2;
		c.gridwidth=3;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(10, 5, 15, 5);
		button_goway = new JButton(imgGoWay);
		button_goway.addActionListener(new MyAction());
		button_goway.setBorderPainted(false);
		button_goway.setFocusPainted(false);
		button_goway.setContentAreaFilled(false);
		button_goway.setRolloverIcon(imgGoWayOver);
		button_goway.setPressedIcon(imgGoWayClick);
		interieur.add(button_goway);
		g.setConstraints(button_goway,c);
		
		//SKIPWAY
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=6;
		c.gridy=0;
		c.gridheight=2;
		c.gridwidth=3;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(10, 5, 15, 10);
		button_skipway = new JButton(imgSkipWay);
		button_skipway.addActionListener(new MyAction());
		button_skipway.setBorderPainted(false);
		button_skipway.setFocusPainted(false);
		button_skipway.setContentAreaFilled(false);
		button_skipway.setRolloverIcon(imgSkipWayOver);
		button_skipway.setPressedIcon(imgSkipWayClick);
		interieur.add(button_skipway);
		g.setConstraints(button_skipway,c);
		
		
		//Fiche technique START STOP
		//START
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=3;
		c.gridheight=4;
		c.gridwidth=3;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(40, 10, 0, 5);
		button_start = new RoundButton(imgStart);
		button_start.addActionListener(new MyAction());
		button_start.setRolloverIcon(imgStartOver);
		button_start.setPressedIcon(imgStartClick);
		interieur.add(button_start);
		g.setConstraints(button_start,c);
		//Keyboard shortcut ENTER
		Action action_enter = new KeyAction(button_start);
		KeyStroke stroke_enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		button_start.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_enter,"Enter");
		button_start.getActionMap().put("Enter",action_enter);
		
		//START GENERATOR
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=2;
		c.gridheight=1;
		c.gridwidth=1;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(0, 90, 0, 0);
		button_startgen = new RoundButton(imgStartgen);
		button_startgen.addActionListener(new MyAction());
		button_startgen.setRolloverIcon(imgStartgenOver);
		button_startgen.setPressedIcon(imgStartgenClick);
		interieur.add(button_startgen);
		g.setConstraints(button_startgen,c);
		
		//STOP GENERATOR
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=3;
		c.gridy=2;
		c.gridheight=1;
		c.gridwidth=2;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.ipady=5;
		c.insets = new Insets(0, 0, 0, 90);
		button_stopgen = new RoundButton(imgStopgen);
		button_stopgen.addActionListener(new MyAction());
		button_stopgen.setRolloverIcon(imgStopgenOver);
		button_stopgen.setPressedIcon(imgStopgenClick);
		interieur.add(button_stopgen);
		g.setConstraints(button_stopgen,c);
		
		//STOP
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=7;
		c.gridheight=3;
		c.gridwidth=3;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.insets = new Insets(0, 10, 15, 5);
		c.ipady = 15;
		button_stop = new RoundButton(imgStop);
		button_stop.addActionListener(new MyAction());
		button_stop.setRolloverIcon(imgStopOver);
		button_stop.setPressedIcon(imgStopClick);
		interieur.add(button_stop);
		g.setConstraints(button_stop,c);
		
	
		//DIRECTIONS
		//FORWARD
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=3;
		c.gridy=3;
		c.gridwidth=6;
		c.gridheight=2;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		double inset_side = c.weightx*frame_size_x/3;
		c.ipady=10;
		c.insets = new Insets(0, (int)inset_side, 0, (int)inset_side);
		button_forward = new JButton(imgForward);
		button_forward.addActionListener(new MyAction());
		button_forward.setBorderPainted(false);
		button_forward.setFocusPainted(false);
		button_forward.setContentAreaFilled(false);
		button_forward.setRolloverIcon(imgForwardOver);
		button_forward.setPressedIcon(imgForwardClick);
		interieur.add(button_forward);
		g.setConstraints(button_forward,c);
		//Keyboard shortcut UP
		Action action_up = new KeyAction(button_forward);
		KeyStroke stroke_up = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
		button_forward.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_up,"up");
		button_forward.getActionMap().put("up",action_up);
		
		// LEFT
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=2;
		c.gridy=5;
		c.gridwidth=3;
		c.gridheight=2;
		c.weightx=0;
		c.weighty=(double)c.gridheight/grid_size;
		c.ipady=10;
		double inset_right = c.weightx*frame_size_x;
		c.insets = new Insets(0, 10, 0, (int)inset_right+20);
		button_left = new JButton(imgLeft);
		button_left.addActionListener(new MyAction());
		button_left.setBorderPainted(false);
		button_left.setFocusPainted(false);
		button_left.setContentAreaFilled(false);
		button_left.setRolloverIcon(imgLeftOver);
		button_left.setPressedIcon(imgLeftClick);
		interieur.add(button_left);
		g.setConstraints(button_left,c);
		//Keyboard shortcut LEFT ARROW
		Action action_left = new KeyAction(button_left);
		KeyStroke stroke_left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0);
		button_left.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_left,"LEFT");
		button_left.getActionMap().put("LEFT",action_left);
		
		// HEADING HOLD
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=3;
		c.gridy=5;
		c.gridwidth=6;
		c.gridheight=2;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		c.ipadx=20;
		c.insets = new Insets(0, (int)inset_side, 0, (int)inset_side);
		button_hold = new JButton(imgHold);
		button_hold.addActionListener(new MyAction());
		button_hold.setBorderPainted(false);
		button_hold.setFocusPainted(false);
		button_hold.setContentAreaFilled(false);
		button_hold.setRolloverIcon(imgHoldOver);
		button_hold.setPressedIcon(imgHoldClick);
		interieur.add(button_hold);
		g.setConstraints(button_hold,c);
		//Keyboard shortcut H
		Action action_hold = new KeyAction(button_hold);
		KeyStroke stroke_hold = KeyStroke.getKeyStroke(KeyEvent.VK_H,0);
		button_hold.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_hold,"HOLD");
		button_hold.getActionMap().put("HOLD",action_hold);

		//RIGHT
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=6;
		c.gridy=5;
		c.gridwidth=3;
		c.gridheight=2;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		double inset_left = c.weightx*frame_size_x/3;
		c.insets = new Insets(0, (int)inset_left-25, 0, 15);
		button_right = new JButton(imgRight);
		button_right.addActionListener(new MyAction());
		button_right.setBorderPainted(false);
		button_right.setFocusPainted(false);
		button_right.setContentAreaFilled(false);
		button_right.setRolloverIcon(imgRightOver);
		button_right.setPressedIcon(imgRightClick);
		interieur.add(button_right);
		g.setConstraints(button_right,c);
		//Keyboard shortcut RIGHT ARROW
		Action action_right = new KeyAction(button_right);
		KeyStroke stroke_right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0);
		button_right.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_right,"RIGHT");
		button_right.getActionMap().put("RIGHT",action_right);
		
		//BACK
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=3;
		c.gridy=8;
		c.gridwidth=6;
		c.gridheight=2;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		inset_side = c.weightx*frame_size_x/3;
		c.insets = new Insets(0, (int)inset_side, 10, (int)inset_side);
		c.ipady = 10;
		button_back = new JButton(imgBack);
		button_back.addActionListener(new MyAction());
		button_back.setBorderPainted(false);
		button_back.setFocusPainted(false);
		button_back.setContentAreaFilled(false);
		button_back.setRolloverIcon(imgBackOver);
		button_back.setPressedIcon(imgBackClick);
		interieur.add(button_back);
		g.setConstraints(button_back,c);
		//Keyboard shortcut DOWN ARROW
		Action action_down = new KeyAction(button_back);
		KeyStroke stroke_down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
		button_back.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_down,"DOWN");
		button_back.getActionMap().put("DOWN",action_down);
		
		//NUDGE LEFT
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=3;
		c.gridy=7;
		c.gridwidth=3;
		c.gridheight=1;
		c.ipady=10;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		inset_left = c.weightx*frame_size_x/5;
		c.insets = new Insets(0, (int)inset_left, 0, 3);
		button_nudgeleft = new JButton(imgNudgeleft);
		button_nudgeleft.addActionListener(new MyAction());
		button_nudgeleft.setBorderPainted(false);
		button_nudgeleft.setFocusPainted(false);
		button_nudgeleft.setContentAreaFilled(false);
		button_nudgeleft.setRolloverIcon(imgNudgeleftOver);
		button_nudgeleft.setPressedIcon(imgNudgeleftClick);
		interieur.add(button_nudgeleft);
		g.setConstraints(button_nudgeleft,c);
		//Keyboard shortcut Q
		Action action_q = new KeyAction(button_nudgeleft);
		KeyStroke stroke_q = KeyStroke.getKeyStroke(KeyEvent.VK_Q,0);
		button_nudgeleft.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_q,"Q");
		button_nudgeleft.getActionMap().put("Q",action_q);

		//NUDGE RIGHT
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=6;
		c.gridy=7;
		c.gridwidth=3;
		c.gridheight=1;
		c.weightx=(double)c.gridwidth/grid_size;
		c.weighty=(double)c.gridheight/grid_size;
		inset_right = c.weightx*frame_size_x/5;
		c.insets = new Insets(0, 3, 0, (int)inset_right);
		button_nudgeright = new JButton(imgNudgeright);
		button_nudgeright.addActionListener(new MyAction());
		button_nudgeright.setBorderPainted(false);
		button_nudgeright.setFocusPainted(false);
		button_nudgeright.setContentAreaFilled(false);
		button_nudgeright.setRolloverIcon(imgNudgerightOver);
		button_nudgeright.setPressedIcon(imgNudgerightClick);
		interieur.add(button_nudgeright);
		g.setConstraints(button_nudgeright,c);
		//Keyboard shortcut E
		Action action_e = new KeyAction(button_nudgeright);
		KeyStroke stroke_e = KeyStroke.getKeyStroke(KeyEvent.VK_E,0);
		button_nudgeright.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke_e,"E");
		button_nudgeright.getActionMap().put("E",action_e);
		
		//frame.setResizable(false);
		frame.setSize(frame_size_x, frame_size_y);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("boat.gif"));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		button_stop.requestFocusInWindow(); //Keyboard shortcut space

		//SendCommand
		try {
			sd = new SendCommand();
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
	}
	
	public class MyAction implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			Object source = ae.getSource();
			idrobot=udp.getRemoteAddress();
			try {
				if(source == button_gohome)
				{
					sd.goHome(idrobot);
				}
				else if(source == button_goway)
				{
					udp.receiveData();
					byte currentWp = (byte)udp.getWpnum();
					String s;
					s = (String)JOptionPane.showInputDialog(frame,"Choose the Waypoint number:\n"
				    			+ "The current Waypoint is "+currentWp,
							    "Waypoint Number",
							    JOptionPane.PLAIN_MESSAGE,
							    ImgBoat, null, null);

					//If a string was returned, say so.
					if (s != null && s.length()!=0) {
						byte wp;
						try{
							wp = Byte.parseByte(s);
							//JOptionPane.showMessageDialog(frame, wp);
							sd.gotoWaypoint(wp, idrobot);
						}
						catch(NumberFormatException nfe) {
							JOptionPane.showMessageDialog(frame, "Error your number don't contain only digits!");	
						}
						
					}
					//The return value was null/empty.
					else JOptionPane.showMessageDialog(frame, "No waypoint has been chosen");
				}
				else if(source == button_skipway)
				{
					sd.skipWp(idrobot);
				}
				else if(source == button_start)
				{
					sd.start(idrobot);
				}
				else if(source == button_stop)
				{
					sd.stop(idrobot);
				}
				else if(source == button_forward)
				{
					sd.forward(idrobot);
				}
				else if(source == button_back)
				{
					sd.back(idrobot);
				}
				else if(source == button_left)
				{
					sd.left(idrobot);
				}
				else if(source == button_right)
				{
					sd.right(idrobot);
				}
				else if(source == button_nudgeright)
				{
					sd.nudgeRight(idrobot);
				}
				else if(source == button_nudgeleft)
				{
					sd.nudgeLeft(idrobot);
				}
				else if(source == button_stopgen)
				{
					sd.stopGenerator(idrobot);
				}
				else if(source == button_startgen)
				{
					sd.startGenerator(idrobot);
				}
				else if(source == button_hold)
				{
					sd.headingHold(idrobot);
				}
			}
			catch (Exception e)
			{
		   		e.printStackTrace();
			}
			button_stop.requestFocusInWindow();
		}
		
		  
	}

	public void SetCurrentWp(byte wp)
	{
		currentWp = wp;
	}
}




