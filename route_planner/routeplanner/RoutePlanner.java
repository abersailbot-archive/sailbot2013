package routeplanner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import routeplanner.datasource.*;
import routeplanner.datasource.gpsd.*;
import routeplanner.datasource.udp.*;
import routeplanner.uk.me.jstott.jcoord.LatLng;
import routeplanner.uk.me.jstott.jcoord.OSRef;

import java.util.Vector;
/*

 each square should contain

 its coords (OS format)
 its image


 current view 

 collection of square
 list of points
 */

public class RoutePlanner extends JFrame implements ActionListener, ComponentListener
{
    //imagecanvas for map
    ImageCanvas ic;
    //scroll pane for map
    JScrollPane scroller;
    //waypoint list window
    WaypointList wl;
    //the map
    Map map;
    //The remote control
    Remote remote;
    
    //dialog box for entering waypoints
    JDialog enterWP;
    JTextField lat;
    JTextField lon;
   
    
    //dialog for saving waypoints
    JDialog saveDialog;
    JTextField filename;

    //dialog for uploading waypoints
    JDialog uploadWP;
    JTextField hostname;
    JTextField port;
    
    GpsdDataSource gps;
    UdpDataSource udp;
    Vector <AbstractDataSource>dataSources;


    double currentEasting, currentNorthing;
    int currx,curry;

    boolean standAlone = false;
    public static final int CENTRE_NONE = 0;
    public static final int CENTRE_UDP = 1;
    public static final int CENTRE_GPS = 2;
    int centreOn=0; //0 = don't, 1= udp, 2= gps
    
    //used to create a dialog
    JFrame me = this;

    public RoutePlanner(String mapFileName)
    {
        dataSources = new Vector<AbstractDataSource>();

        this.setTitle("Route Planner");

        loadMap(mapFileName);

        addComponentListener(this);

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        
        if(gps!=null)
        {
            gps.setDataSourceName("GPS");
            gps.startReading();
	    centreOn=CENTRE_GPS;
        }
        if(udp!=null)
        {
            udp.setDataSourceName("Boat");
            udp.startReading();
	    centreOn=CENTRE_UDP;
        }
    }
    
    public void loadMap(String mapFileName)
    {
        this.setLayout(new BorderLayout());
        Image img = new ImageIcon(mapFileName).getImage();
        map = new Map(img, mapFileName + ".desc", img.getHeight(null), img.getWidth(null));
        
        try
        {
            gps = new GpsdDataSource(map);
            dataSources.add(gps);
            gps.setColor(Color.MAGENTA);
        }
        catch (java.net.ConnectException e)
        {
            // can't connect to GPSd continue in standalone mode
            standAlone = true;
            System.out.println("connection failed, standalone mode");
            e.printStackTrace();
        }
        
  
            udp = new UdpDataSource(map);
            dataSources.add(udp);
            udp.setColor(Color.RED);




        if(wl!=null)
        {
            wl.setVisible(false);
        }
        if(ic!=null)
        {
            ic.setVisible(false);
        }
        wl = new WaypointList(map);
        AbstractDataSource sources[] = new AbstractDataSource[2];
        ic = new ImageCanvas(map, img, wl,dataSources.toArray(sources));
        
        //create a scaoller with our image canvas in
        scroller = new JScrollPane(ic,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        this.getContentPane().add(scroller,BorderLayout.CENTER);

        //setup the menus        
        JMenuBar mb = new JMenuBar();
        setJMenuBar(mb);
        JMenu m;
        mb.add(m = new JMenu("File"));

        JMenuItem mi;
        m.add(mi = new JMenuItem("New Route"));
        mi.addActionListener(this); 
        m.addSeparator();
        m.add(mi = new JMenuItem("Change Map"));
        mi.addActionListener(this);         
        m.addSeparator();
        m.add(mi = new JMenuItem("Open Route"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Save Route"));
        mi.addActionListener(this); 
        m.addSeparator();
        m.add(mi = new JMenuItem("Exit"));
        mi.addActionListener(this); 
        
        mb.add(m = new JMenu("Edit"));
        
        m.add(mi = new JMenuItem("Enter Waypoint"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Display Waypoint List"));
        mi.addActionListener(this); 

        mb.add(m = new JMenu("Network"));
        
        m.add(mi = new JMenuItem("Upload Waypoints"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Download Waypoints"));
        mi.addActionListener(this); 
	
	mb.add(m = new JMenu("Telemetry"));
	
        m.add(mi = new JMenuItem("Stop Telemetry"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Centre on UDP"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Centre on GPS"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Don't auto centre"));
        mi.addActionListener(this); 
        m.add(mi = new JMenuItem("Clear Path History"));
        mi.addActionListener(this); 
	


        pack();
        this.setBounds(250,0,815,760);
        this.setVisible(true);
    }

    //menu handler
    public void actionPerformed(ActionEvent e)
    {

        String item = e.getActionCommand();
        if (item.equals("Exit"))
        {
            System.exit(0);
        }
        if(item.equals("New Route"))
        {
            //clear the route
            wl.clearWaypointList();
	    ic.setRightClick(false);
            ic.repaint();
        }
        if(item.equals("Open Route"))
        {
            //create a popup asking us for the route name
            JFrame f = new JFrame();
            FileDialog d = new FileDialog(f, "Open");
            d.setVisible(true);
            String selectedItem = d.getFile();
            if (selectedItem != null)
            {
                    wl.clearWaypointList();
                    try
                    {
                        wl.loadData(selectedItem);
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
            } 
            f.dispose();
        }
        if(item.equals("Save Route"))
        {
            saveDialog = new JDialog(this,"Enter Filename");
            saveDialog.setLayout(new BorderLayout());
            
            filename = new JTextField();
            JLabel filenameLabel = new JLabel("Filename:");
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2,1));
            p.add(filenameLabel);
            p.add(filename);
            
            saveDialog.add(BorderLayout.NORTH,p);
            
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() { 
                    public void actionPerformed(ActionEvent e) { 
                       String name = filename.getText(); 
                       try
                       {
                           wl.saveData(name);
                       }
                       catch(IOException e1)
                       {
                           JDialog err = new JDialog(me,"Error");
                           err.add(new JLabel(e1.toString()));
                       }
                       saveDialog.setVisible(false); 
                       ic.repaint();
                    } 
                  }); 
            saveDialog.add(BorderLayout.SOUTH,saveButton);
            saveDialog.pack();
            saveDialog.setSize(300,100);
            saveDialog.setVisible(true);
            //create a popup asking us for the route name
        }
        if(item.equals("Change Map"))
        {
            //create a popup asking us for the map name
            JFrame f = new JFrame();
            FileDialog d = new FileDialog(f, "Open");
            d.setVisible(true);
            String selectedItem = d.getFile();
            if (selectedItem != null)
            {
                loadMap(selectedItem);
            } 
            f.dispose();
        }
        if(item.equals("Enter Waypoint"))
        {
            enterWP = new JDialog(this,"Enter Waypoint");
            enterWP.setLayout(new BorderLayout());
            
            lat = new JTextField();
            JLabel latLabel = new JLabel("Latitude");
            
            lon = new JTextField();
            JLabel lonLabel = new JLabel("Longitude");

            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2,2));
            p.add(latLabel);
            p.add(lat);
            p.add(lonLabel);
            p.add(lon);
            
            enterWP.add(BorderLayout.NORTH,p);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() { 
                    public void actionPerformed(ActionEvent e) { 
                       double latitude = Double.parseDouble(lat.getText()); 
                       double longitude = Double.parseDouble(lon.getText());
                       wl.addWaypoint(latitude, longitude);
                       enterWP.setVisible(false); 
                       ic.repaint();
                    } 
                  }); 
            enterWP.add(BorderLayout.SOUTH,okButton);
            enterWP.pack();
            enterWP.setVisible(true);
            
        }
        if(item.equals("Display Waypoint List"))
        {
            wl.setVisible(true);
        }
        if(item.equals("Upload Waypoints"))
        {
            uploadWP = new JDialog(this,"Enter Hostname");
            uploadWP.setLayout(new BorderLayout());
            
            hostname = new JTextField();
            JLabel hostnameLabel = new JLabel("Hostname");
            
            port = new JTextField();
            JLabel portLabel = new JLabel("Port");

            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2,2));
            p.add(hostnameLabel);
            p.add(hostname);
            p.add(portLabel);
            p.add(port);
            
            uploadWP.add(BorderLayout.NORTH,p);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() { 
                    public void actionPerformed(ActionEvent e) { 
                        try
                        {
                            wl.uploadWaypoints(hostname.getText(),Integer.parseInt(port.getText()));
                        }
                        catch(WaypointException e2)
                        {
                            JOptionPane.showMessageDialog(me, "Error reading waypoint","Error uploading waypoints",JOptionPane.ERROR_MESSAGE);
                            e2.printStackTrace();
                        }
                        catch(ConnectException e2)
                        {
                            JOptionPane.showMessageDialog(me, "Error Connecting to host.","Error uploading waypoints",JOptionPane.ERROR_MESSAGE);
                            e2.printStackTrace();
                        }
                        catch(IOException e2)
                        {
                            JOptionPane.showMessageDialog(me, "IOException","Error uploading waypoints",JOptionPane.ERROR_MESSAGE);
                            e2.printStackTrace();
                        }
                        uploadWP.setVisible(false); 
                        ic.repaint();
                    } 
                  }); 
            uploadWP.add(BorderLayout.SOUTH,okButton);
            uploadWP.pack();
            uploadWP.setVisible(true);
        }
	if(item.equals("Stop Telemetry"))
        {
	    udp.stopReading();    
	    System.out.println("Stopping Telemetry");
	}

	if(item.equals("Centre on GPS"))
        {
	    centreOn=CENTRE_GPS;    
	    System.out.println("Centre on GPS");
	}
	
	if(item.equals("Centre on UDP"))
        {
	    centreOn=CENTRE_UDP;    
	    System.out.println("Centreing on UDP");
	}

        
	if(item.equals("Don't auto centre"))
        {
	    centreOn=CENTRE_NONE;    
	    System.out.println("no autocentre");
	}
	
	if(item.equals("Clear Path History"))
	{
	    udp.clearPoints();
	    System.out.println("clearing history");
	}

    }
    
    public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        scroller.setSize(c.getSize().width-20, c.getSize().height-60);
    }
    
    public void componentHidden(ComponentEvent e)
    {
    }
    
    public void componentShown(ComponentEvent e)
    {
    }
    
    public void componentMoved(ComponentEvent e)
    {
    }
    
    public void run()
    {

        String scentence;
        LatLng oldPosition=null;
        LatLng ll=null;
	remote = new Remote(udp);
	//remote.SetCurrentWp(udp.getWpnum());
        try
        {
            while (true)
            {
                if (!standAlone)
                {
                    GPSData state = gps.getGPSData();
                    //System.out.println("GPS State " + state);
                    if (state.isFixValid())
                    {
                        //create a new point from latest gps reading and convert to radians
                        ll = new LatLng( gps.getLat() * (180 / Math.PI),gps.getLon() * (180 / Math.PI));
                        System.out.println("latlon = " + ll);
                        if (map.isOSMap())
                        {
                            ll.toOSGB36();
                            OSRef or = ll.toOSRef();
                            currentEasting = (int) or.getEasting();
                            currentNorthing = (int) or.getNorthing();
                        }
                        else
                        {
                            currentEasting = ll.getLat();
                            currentNorthing = ll.getLng();
                        }

                        //System.out.println("GPS reads " + currentEasting + "," + currentNorthing);

                        ic.setCurrX(map.eastingToXCoord(currentEasting));
                        ic.setCurrY(map.northingToYCoord(currentNorthing));

                        /*if(oldPosition!=null&&!oldPosition.equals(ll))
                        {
                            if(ll.distance(oldPosition)>0.015)
                            {
                                        wl.addWaypoint(ic.getCurrX(),ic.getCurrY());
                            }
                        }*/


                        //scroller.s
                        scroller.getViewport().setViewPosition(new Point(ic.getCurrX()-(scroller.getWidth()/2),ic.currY-(scroller.getHeight()/2)));
                       //System.out.println("current pixel position " + ic.getCurrX() + "," + ic.getCurrY());

                    }
                }
                else
                {
                    //System.out.println("in main");
			if(centreOn==CENTRE_UDP)
			{
                    	   // System.out.println("centering on " + udp.getCurrX() + "," + udp.getCurrY());
                    	    ic.setCurrX(udp.getCurrX());
                    	    ic.setCurrY(udp.getCurrY());
			}
			else if(centreOn==CENTRE_GPS)
			{
                    	 //   System.out.println("centering on " + gps.getCurrX() + "," + gps.getCurrY());
                    	    ic.setCurrX(gps.getCurrX());
                    	    ic.setCurrY(gps.getCurrY());
			}
			if(centreOn==CENTRE_GPS||centreOn==CENTRE_UDP)
			{    
                    	    scroller.getViewport().setViewPosition(new Point(ic.getCurrX()-(scroller.getWidth()/2),ic.currY-(scroller.getHeight()/2)));
			}
                    
                }
                Thread.sleep(1000);
                repaint();
                oldPosition = ll;
              //  System.out.println("standalone = " + standAlone);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String args[])
    {

        String filename;
        if (args.length == 0)
        {
            filename = "maps/aber_os.png";
        }
        else
        {
            filename = args[0];
        }
        RoutePlanner rp = new RoutePlanner(filename);
        rp.run();
    }

}
