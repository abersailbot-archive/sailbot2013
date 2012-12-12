package routeplanner.datasource.gpsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Graphics;

import routeplanner.Map;
import routeplanner.datasource.AbstractDataSource;

public class GpsdDataSource extends AbstractDataSource 
{
    private Socket gpsdSocket = null;
    private BufferedReader in = null;
    private NmeaParser n = new NmeaParser();
    // the number of lines which the GPS sends every time it updates
    int numOfLines=9;
    
    /**
     * @param map - the map we are currently using
     * @param server - the server running GPSd
     * @param port - the port GPSd is running on 
     * @param updateInterval - how often to query the GPS
     * @param numOfLines - how many lines of text the GPS sends on each update (most send 9 - GGA,VTG,RMC,GSA,GSV,GSV,GSV some just send GGA,VTG)
     * @throws ConnectException - thrown if we can't connect to the server
     */
    public GpsdDataSource(Map map,String server,int port,int updateInterval,int numOfLines) throws ConnectException
    {
        super(map);
        super.setUpdateInterval(updateInterval);
        this.numOfLines=numOfLines;
        try {
            gpsdSocket = new Socket(server,port);
            in = new BufferedReader(new InputStreamReader(gpsdSocket.getInputStream()));
            PrintWriter out = new PrintWriter(gpsdSocket.getOutputStream(), true);
            //set raw mode
            out.println("R=1");
        }
        catch(ConnectException e)
        {
            throw(e);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * constructor with default options, update every second, 9 lines of GPS data and connect to gpsd on 127.0.0.1:2497
     * @param map - the map we are working with
     * @throws ConnectException
     */
    public GpsdDataSource(Map map) throws ConnectException
    {
        super(map);
        super.setUpdateInterval(50);
        this.numOfLines=9;
        try {
            gpsdSocket = new Socket("localhost",2947);
            in = new BufferedReader(new InputStreamReader(gpsdSocket.getInputStream()));
            PrintWriter out = new PrintWriter(gpsdSocket.getOutputStream(), true);
            //set raw mode
            out.println("R=1");
        }
        catch(ConnectException e)
        {
            throw(e);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    protected void updateState()
    {

            try
            {
                for(int i=0;i<numOfLines;i++)
                {
                    n.processScentence(in.readLine());    
                }
		//gpsdSocket.close();
                super.curr_heading=n.getCurrentState().getHeading();
                super.curr_lat=n.getCurrentState().getLat();
                super.curr_lon=n.getCurrentState().getLon();
                super.curr_speed=n.getCurrentState().getSpeed();
                super.fixValid=n.getCurrentState().getFixValid();
                //add the new point to our 
                System.out.println("new gps point " + curr_lat + "," + curr_lon); 
                super.addPoint(curr_lat*(180/Math.PI),curr_lon*(180/Math.PI));
            }
            catch (InvalidFormatException e)
            {

                e.printStackTrace();
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }   
    }

    public void render(Graphics g,boolean drawArrow)
    {
        new GpsdDataSourceRenderer().render(this,g,drawArrow);
    }
    
    /**
     * allows the user to get at the GPS data should they want to do something else with it
     * e.g. draw the sky map.
     * @return
     */
    public GPSData getGPSData()
    {
        return n.getCurrentState();
    }

}
