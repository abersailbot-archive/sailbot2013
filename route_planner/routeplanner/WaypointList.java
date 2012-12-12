package routeplanner;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.util.Vector;
import routeplanner.uk.me.jstott.jcoord.*;
import java.io.*;
import java.net.Socket;
import java.net.ConnectException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;

public class WaypointList extends JFrame
{
    Vector <MapPoint>points;
    Map map;
    JTextArea positions;
    JLabel totalDistBox;
    LatLng oldLatLng;
    int totalDist=0;
    double latCoords=0.0,lonCoords=0.0;


    public WaypointList(Map map)
    {
        points = new Vector<MapPoint>();
        this.map = map;

        positions = new JTextArea(25,20);
        positions.setEditable(false);
        positions.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(positions,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        totalDistBox = new JLabel("Total Distance: 0m");
       // totalDistBox.setEditable(false);
        totalDistBox.setBackground(Color.WHITE);
        
        JPanel textBoxes = new JPanel();
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        add(totalDistBox);
        add(scrollPane);

        totalDistBox.setSize(250,20);
        add(textBoxes);
        
        this.setBounds(0,0,250,480);
        this.setTitle("Waypoint List");
        
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });
        
        this.setVisible(true);
        
        
    }

    /**
    adds a new waypoint by its pixel co-ordinate
    */
    public void addWaypoint(int x,int y)
    {
       LatLng latlng;
       double easting = (double)map.xCoordToEasting(x);
       double northing = (double)map.yCoordToNorthing(y);
       System.out.println("adding waypoint");

       if(map.isOSMap())
       {
           OSRef os1 = new OSRef(easting,northing);
           latlng = os1.toLatLng();
           latlng.toWGS84();
       }
       else
       {
           latlng = new LatLng(northing,easting);
       }
       
       MapPoint p = new MapPoint(latlng.getLat(),latlng.getLng(),easting,northing,x,y);
       points.add(p);

       //append to our text in the textarea
       if(points.size()>1)
       {
            double distance = latlng.distance(oldLatLng) * 1000;
            int dist = (int)distance;
            totalDist = totalDist + dist;
            positions.append(latlng.toString() + dist + "m\n");
            totalDistBox.setText("Total Distance: " + new Integer(totalDist).toString() + "m\n");
       }
       else
       {
           positions.append(latlng.toString() + "\n");
       }
       oldLatLng = latlng;
    }
    
    /**
     * adds a waypoint by lat/lon
     * @return
     */
    public void addWaypoint(double lat,double lon)
    {
        LatLng latlng;
        int x,y;

        latlng = new LatLng(lat,lon);
        
        
       
        if(map.isOSMap())
        {
            latlng.toOSGB36();
            OSRef os1 = latlng.toOSRef();
            
            x = map.eastingToXCoord((int)os1.getEasting());
            y = map.northingToYCoord((int)os1.getNorthing());
        }
        else
        {
            x = map.eastingToXCoord(lon);
            y = map.northingToYCoord(lat);
        }
        
        MapPoint p = new MapPoint(latlng.getLat(),latlng.getLng(),lat,lon,x,y);
        points.add(p);

        //append to our text in the textarea
        if(points.size()>1)
        {
             double distance = latlng.distance(oldLatLng) * 1000;
             int dist = (int)distance;
             totalDist = totalDist + dist;
             positions.append(latlng.toString() + dist + "m\n");
             totalDistBox.setText(new Integer(totalDist).toString() + "m\n");
        }
        else
        {
            positions.append(latlng.toString() + "\n");
        }
        oldLatLng = latlng;
    }

    public MapPoint[] getPoints()
    {

        MapPoint[] p = points.toArray(new MapPoint[points.size()]);
        return p;
    }
    
    public void clearWaypointList()
    {
        points = new Vector<MapPoint>();
        totalDist=0;
        positions.setText("");
        totalDistBox.setText("");
        repaint();
    }
    
    public void loadData(String filename) throws IOException
    {
        System.out.println("file name " + filename);
        File f = new File(filename);

        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        
        while((line = in.readLine()) != null)
        {

          System.out.println("read line " + line);
          StringTokenizer st1 = new StringTokenizer(line," ");
          String lat = st1.nextToken();
          String lon = st1.nextToken();
           boolean failure=false;
           if(lat.startsWith("lat="))
           {
             latCoords = Double.parseDouble(lat.substring(4));
           }
           else
           {
             failure=true;
           }
           if(lon.startsWith("lon="))
           {
             lonCoords = Double.parseDouble(lon.substring(4));
           }
           else
           {
             failure=true;
           }
                   
           if(!failure)
           {
             System.out.println("Loaded data " + latCoords + " " + lonCoords);
             addWaypoint(latCoords,lonCoords);
           }
           else
           {
             System.out.println("error loading waypoints\n");
           }
            System.out.println("Loaded data " + lat + " " + lon);
            addWaypoint(Double.parseDouble(lat),Double.parseDouble(lon));

        }
        in.close();
   
    }
    
    public void saveData(String filename) throws IOException
    {
        System.out.println("file name " + filename);
        File f = new File(filename);

        PrintWriter pr = new PrintWriter(new FileWriter(f));
        for(MapPoint point : points)
        {
            pr.println("lat=" + point.getLat() + " lon=" + point.getLng() + " linger=0 dist=0.015");
            System.out.println(point);
        }
        System.out.println("done writing");
        pr.close();
        System.out.println("closed file");

    }

    /**
       uploads waypoints to the boat, clearing existing waypoints first
    */
    public void uploadWaypoints(String hostname,int port) throws ConnectException, IOException, WaypointException
    {
        Socket remote = new Socket(hostname,port);
        BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream(), true);
        String response;

        //clear all waypoints
        out.println("CLEARWP");
        response=in.readLine();
        if(response.equals("OK"))
        {
            //upload new waypoints
            for(MapPoint point : points)
            {
                out.println("ADDWP " + point.getLat()+","+point.getLng());
                response = in.readLine();
                if(response.equals("ERROR"))
                {
                    throw new WaypointException("Error adding waypoint");
                }
            }
        }
        remote.close();
    }

    /**
        downloads waypoint currently stored in the boat
        overwrites any waypoints we currently have in memory
    */
    public void downloadWaypoints(String hostname,int port) throws ConnectException, IOException
    {
        points = new Vector<MapPoint>();
        Socket remote = new Socket(hostname,port);
        BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream(), true);
        String response;
        double lat,lon;

        //get all waypoints
        out.println("GETWP");
        response=in.readLine();
        while(!response.equals("DONE"))
        {
            response=in.readLine();
            lat=Double.parseDouble(response.substring(response.indexOf(',')));
            lon=Double.parseDouble(response.substring(response.indexOf(',')+1,response.length()));
            addWaypoint(lat,lon);
        }

        remote.close();
    }
}
