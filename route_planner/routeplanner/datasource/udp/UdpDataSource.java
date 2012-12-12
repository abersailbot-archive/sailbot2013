package routeplanner.datasource.udp;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;
import java.awt.Graphics;
import routeplanner.Map;
import routeplanner.datasource.AbstractDataSource;
import java.util.regex.*;
import java.util.StringTokenizer;


public class UdpDataSource extends AbstractDataSource
{
    long sequenceNumber;
    long time; //time message was sent
    short automode;
    double lat,lon;
    int wpnum;
    double distance; //distnace to next wp
    int wpHeading;
    double wpLat,wpLon;
    double speed; //GPS speed
    int heading; //compass heading
    short leftSpeed,rightSpeed; //motor speeds
    double current[] = new double[5]; //current sensor readings
    double xte; //cross track error
    double voltage;
    short warnings;
    InetAddress remoteAddress;

    /**
    @return the timestamp of the last received message, in seconds since 00:00:00 UTC on jan 1st 1970
    */
    public long getTime()
    {
        return time;
    }

    /**
    @return if the boat is in autononous mode or not.
    */
    public short getAutomode()
    {
        return automode;
    }

    /**
    @return the remote device's last reported latitude, in degrees 
    */
    public double getLat()
    {
        return lat;
    }

    /**
    @return the remote device's last reported longitude, in degrees 
    */
    public double getLon()
    {
        return lon;
    }


    /**
    @return the remote device's current waypoint number
    */
    public int getWpnum()
    {
        return wpnum;
    }

    /**
    @return the distance in kilometres from the next waypoint, as reported by the remote device in its last message
    */
    public double getDistance()
    {
        return distance;
    }


    /**
    @return the heading to the current waypoint, as reported by the remote device in its last message
    */
    public int getWpHeading()
    {
        return wpHeading;
    }

    /**
    @return the longitude of the next waypoint in degrees, as reported by the remote device in its last message
    */
    public double getWpLon()
    {
        return wpLon;
    }

    /**
    @return the latitude of the next waypoint in degrees, as reported by the remote device in its last message
    */
    public double getWpLat()
    {
        return wpLat;
    }


    /**
    @return the GPS speed in knots??, as reported by the remote device in its last message
    */
    public double getSpeed()
   {
        return speed;
    }

    
   /*
    public int getHeading()
    {
        return heading;
    }*/

  
    /**
    @return the cross track error in metres between the last waypoint and next waypoint, as reported by the remote device in its last message
    */
    public double getXte()
    {
        return xte;
    }

    /**
    @return the speed (0-255, approx 122 = stop) of the left motor, as reported by the remote device in its last message
    */
    public short getLeftSpeed()
    {
      return leftSpeed;
    }
    
    /**
    @return the speed (0-255, approx 122 = stop) of the right motor,as reported by the remote device in its last message
    */
    public short getRightSpeed()
    {
      return rightSpeed;
    }


    /**
    @return an array containing the readings from the current sensors, as reported by the remote device in its last message
    */
    public double[] getCurrent()
    {
      return current;
    }


    public long getSequenceNumber()
    {
      return sequenceNumber;
    }

    public short getWarnings()
    {
      return warnings;
    }

    public double getVoltage()
    {
      return voltage;
    }


    /**
    @return the address of the remote device, which is sending us its telemetry data
    */
    public InetAddress getRemoteAddress()
    {
      System.out.println("Remote address is " + remoteAddress);
      return remoteAddress;
    }
    
    
    /**
    Receives data from a remote source via broadcasts on UDP port 4321, data is sent in key=value format. All fields are optional, so packets redundant, unchanging data need not be resent. 
    Valid field names are time (time since epoch in seconds), lat (latitude in degrees), lon (longitude in degrees), wpnum (waypoint number), wpdist (waypoint distance in km), wphdg (waypoint heading in degrees), xte (cross track error in km), wplat (waypoint latitude in degrees), wplon (waypoint longitude in degrees), speed (in knots?), hdg (heading in degrees), lspd (left motor speed 0-255), rspd (right motor speed 0-255), c1 (current sensor 1 in amps), c2 (current sensor 2 in amps), c3 (current sensor 3 in amps), c4 (current sensor 3 in amps)
   example packet: time=1336476465 lat=52.4064 lon=-4.0764 wpnum=6 wpdist=1.100 wphdg=90 xte=1.000 wplat=52.4064 wplon=-4.0779 speed=1.2000 hdg=6 lspd=128 rspd=128 c1=1.000000 c2=2.000000 c3=3.000000 c4=4.000000 
    */
    public void receiveData() throws Exception
    {
        Pattern regexPattern = Pattern.compile("^[[A-Za-z0-9]]*=[0-9.-]*$");

        byte buffer[] = new byte[4096];
        try
        {
            DatagramSocket ds = new DatagramSocket(4321);
            ds.setBroadcast(true);
            DatagramPacket p = new DatagramPacket(buffer,buffer.length);
	    
            ds.receive(p);
	    sequenceNumber++;
	    remoteAddress = p.getAddress();
	    //System.out.println("Got new packet Remote address is " + remoteAddress);

            String data = new String(buffer,0,p.getLength());

            StringTokenizer t = new StringTokenizer(data," ");

            //System.out.println("got message: " + data);
            while(t.hasMoreTokens())
            {
              String token = t.nextToken();
              Matcher matchPattern = regexPattern.matcher(token);
              if(matchPattern.matches())
              {
                //each token should be of the form name=value, where name is alphanumeric and value is numeric
                String splitData[] = token.split("=");
               // System.out.println("Got submessage " + token);
                    
                if(splitData[0].equalsIgnoreCase("time"))
                {                 
                  time=Long.parseLong(splitData[1]);
                  //System.out.println("new time " + time);
                }
                else if(splitData[0].equalsIgnoreCase("lat"))
                {
                  lat = Double.parseDouble(splitData[1]);
                //  System.out.println("new lat " + lat);
                }
                else if(splitData[0].equalsIgnoreCase("lon"))
                {
                  lon = Double.parseDouble(splitData[1]);
                //  System.out.println("new lon " + lon);
                }
                else if(splitData[0].equalsIgnoreCase("wpnum"))
                {
                  wpnum = Integer.parseInt(splitData[1]);
                //  System.out.println("new wpnum " + wpnum);
                }
                else if(splitData[0].equalsIgnoreCase("wpdist"))
                {
                  distance = Double.parseDouble(splitData[1]);
                //  System.out.println("new distance " + distance);
                }
                else if(splitData[0].equalsIgnoreCase("wphdg"))
                {
                  wpHeading = Integer.parseInt(splitData[1]);
                 // System.out.println("new wpheading " + wpHeading);
                }
                else if(splitData[0].equalsIgnoreCase("wplat"))
                {
                  wpLat = Double.parseDouble(splitData[1]);
                //  System.out.println("new wplat " + wpLat);
                }
                else if(splitData[0].equalsIgnoreCase("wplon"))
                {
                  wpLon = Double.parseDouble(splitData[1]);
                 // System.out.println("new lon " + wpLon);
                }
                else if(splitData[0].equalsIgnoreCase("speed"))
                {
                  speed = Double.parseDouble(splitData[1]);
                //  System.out.println("new speed " + speed);
                }
                else if(splitData[0].equalsIgnoreCase("hdg"))
                {
                  heading=Integer.parseInt(splitData[1]);
                 // System.out.println("new heading " + heading);
                }
                else if(splitData[0].equalsIgnoreCase("lspd"))
                {
                  leftSpeed=Short.parseShort(splitData[1]);
               //   System.out.println("new leftspeed " + leftSpeed);
                }
                else if(splitData[0].equalsIgnoreCase("rspd"))
                {
                  rightSpeed=Short.parseShort(splitData[1]);
                //  System.out.println("new rightspeed " + rightSpeed);
                }
                else if(splitData[0].equalsIgnoreCase("xte"))
                {
                  xte=Double.parseDouble(splitData[1]);
               //   System.out.println("new xte " + xte);
                }       
                else if(splitData[0].equalsIgnoreCase("auto"))
                {
                  automode=Short.parseShort(splitData[1]);
                }
                else if(splitData[0].equalsIgnoreCase("c0"))
                {
                  current[0]=Double.parseDouble(splitData[1]);
               //   System.out.println("new c1 " + current[0]);
                }            
                else if(splitData[0].equalsIgnoreCase("c1"))
                {
                  current[1]=Double.parseDouble(splitData[1]);
               //   System.out.println("new c2 " + current[1]);
                }            
                else if(splitData[0].equalsIgnoreCase("c2"))
                {
                  current[2]=Double.parseDouble(splitData[1]);
              //    System.out.println("new c3 " + current[2]);
                }            
                else if(splitData[0].equalsIgnoreCase("c3"))
                {
                  current[3]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
                else if(splitData[0].equalsIgnoreCase("c4"))
                {
                  current[4]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
               /* else if(splitData[0].equalsIgnoreCase("c5"))
                {
                  current[5]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
                else if(splitData[0].equalsIgnoreCase("c6"))
                {
                  current[6]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
                else if(splitData[0].equalsIgnoreCase("c7"))
                {
                  current[7]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
                else if(splitData[0].equalsIgnoreCase("c8"))
                {
                  current[8]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }            
                else if(splitData[0].equalsIgnoreCase("c9"))
                {
                  current[9]=Double.parseDouble(splitData[1]);
                //  System.out.println("new c4 " + current[3]);
                }    */
                else if(splitData[0].equalsIgnoreCase("volt"))
                {
                  voltage=Double.parseDouble(splitData[1]);
                //  System.out.println("new voltage " + voltage);
                }           
                else if(splitData[0].equalsIgnoreCase("warn"))
                {
                  warnings=Short.parseShort(splitData[1]);
                //  System.out.println("new warnings " + warnings);
                }   
              }
              else
              {
                System.err.println("Failed to match regex");
                throw new Exception("corrupt data " + token); //make this a more specific exception
              }
            }
          
          //  System.out.println();
            ds.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

 
    /**
     * @param map - the map we are currently using
     * @param updateInterval - how long to sleep between readings
     */
    public UdpDataSource(Map map,int updateInterval) 
    {
        super(map);
        super.setUpdateInterval(updateInterval);
    }
    
    /**
     * constructor with default options, update every second, 9 lines of GPS data and connect to gpsd on 127.0.0.1:2497
     * @param map - the map we are working with
     * @throws ConnectException
     */
    public UdpDataSource(Map map)
    {
        super(map);
        super.setUpdateInterval(100);
    }

    public void render(Graphics g,boolean drawArrow)
    {
        new UdpDataSourceRenderer().render(this,g,drawArrow);
    }

    protected void updateState()
    {
            try
            {
              receiveData();
              super.curr_heading=heading;
              super.curr_lat=lat*(Math.PI/180);
              super.curr_lon=lon*(Math.PI/180);
              super.curr_speed=speed;
              super.fixValid=true;
              //System.out.println("new udp point " + lat + "," + lon);
              super.addPoint(lat,lon);
            }
            catch(Exception e)
            {
              e.printStackTrace();
            }
    }
}
