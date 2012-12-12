import java.net.*;
import java.io.IOException;
import java.util.Random;

public class TestTelemetryHeadless
{
    long time; //time message was sent
    double lat,lon;
    int wpnum;
    double distance; //distnace to next wp
    int wpHeading;
    double wpLat,wpLon;
    double speed; //GPS speed
    int heading; //compass heading
    short leftSpeed,rightSpeed; //motor speeds
    double current[] = new double[4]; //current sensor readings
    double xte; //cross track error
    DatagramSocket ds;
    String lastMsg;


    static long last_full_msg=0;
    static long old_time;
    static double old_lat;
    static double old_lon;
    static int old_wpnum;
    static double old_distance;
    static int old_wp_heading;
    static double old_udp_xte;
    static double old_wp_lat;
    static double old_wp_lon;
    static double old_speed;
    static int old_heading;
    static int old_left_speed;
    static int old_right_speed;
    static double old_current1;
    static double old_current2;
    static double old_current3;
    static double old_current4;


    static final int FULL_MSG_INTERVAL=60;

  public TestTelemetryHeadless() throws Exception
  {

        ReceiveThread r = new ReceiveThread(this);
        r.start();

        ds = new DatagramSocket();
        this.ds = ds;



        /*this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });*/

  }

    
    //calculates difference between two headings taking wrap around into account
    private int getHdgDiff(int heading1,int heading2)
    {
        int result;
    
        result = heading1-heading2;
        
        if(result<-180)
        {
            result = 360 + result;
            return result;
        } 
        if(result>180)
        {
            result = 0 - (360-result);
        }
        return result;
    }

  public void updateData()
  {
      //timeLabel.setText("Time: " + new java.util.Date(old_time*1000.toString()));
  //timeLabel.setText("Time: " + ds.getTime());
      System.out.println("Message Age: " + ((System.currentTimeMillis()/1000)-old_time));
      
      System.out.println("Lat: " + old_lat);
      System.out.println("Lon: " + old_lon);
      System.out.println("Speed: " + old_speed);
      System.out.println("Heading: "+ old_heading);
      System.out.println("Heading Error: "+ getHdgDiff((int)old_heading,old_wp_heading));
      System.out.println("Cross track err: " + old_udp_xte);
      
      System.out.println("Waypoint Number: " + old_wpnum);
      System.out.println("Waypoint Distance: " + old_distance);
      System.out.println("Waypoint Heading: " + old_wp_heading);
      System.out.println("Waypoint Lat: " + old_wp_lat);
      System.out.println("Waypoint Lon: " + old_wp_lon);

      System.out.println("Left Motor: " + old_left_speed);
      System.out.println("Right Motor: " + old_right_speed);

      System.out.println("Last Message: " + lastMsg);

      for(int i=0;i<current.length;i++)
      {         
        System.out.println("Current Sensor " + i + ": " + current[i]);
      }
      
  }

  public void udp_send(long time,double lat,double lon,int wpnum,double distance,int wp_heading,double udp_xte,double wp_lat,double wp_lon,double speed,int heading,int left_speed,int right_speed,double current1,double current2,double current3,double current4) throws Exception
  {
    String databuf="";
    int broadcast=1;
    int param_count=0;
    

         
    if(old_time!=time||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "time="+time + " ";
      this.time=time;
      param_count++;
    }


    if(old_lat!=lat||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "lat="+lat + " ";
      this.lat=lat;
      param_count++;
    }

    
    if(old_lon!=lon||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf=databuf+"lon=" + lon + " ";
      this.lon=lon;
      param_count++;
    }

    
    if(old_wpnum!=wpnum||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wpnum=" + wpnum  + " ";
      this.wpnum=wpnum;
      param_count++;
    }

    
    if(old_distance!=distance||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wpdist="+ distance + " ";
      this.distance = distance;
      param_count++;
  
    }            

    
    if(old_wp_heading!=wp_heading||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wphdg="+wp_heading + " ";
      this.wpHeading = wp_heading;
      param_count++;
    }    

    
    if(old_udp_xte!=udp_xte||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "xte=" + udp_xte + " ";
      this.xte=udp_xte;
      param_count++;
    }    

    
    if(old_wp_lat!=wp_lat||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wplat=" + wp_lat  + " ";
      this.wpLat=wp_lat;
      param_count++;
    }    


    if(old_wp_lon!=wp_lon||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wplon="+wp_lon  + " ";
      this.wpLon=wp_lon;
      param_count++;
    }    

    
    if(old_speed!=speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "speed=" + speed  + " ";
      this.speed=speed;
      param_count++;
    }        

    
    if(old_heading!=heading||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "hdg=" + heading  + " ";
      this.heading=heading;
      param_count++;
    }        

    
    if(old_left_speed!=left_speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "lspd=" + left_speed +  " ";
      this.leftSpeed=(short)left_speed;
      param_count++;
    }        


    if(old_right_speed!=right_speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "rspd=" +  right_speed  + " ";
      this.rightSpeed=(short)right_speed;
      param_count++;
    }        

    
    if(old_current1!=current1||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "c1=" + current1  + " ";
      this.current[0]=current1;
      param_count++;
    }            

      
    if(old_current2!=current2||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "c2=" + current2  + " ";
      this.current[1]=current2;
      param_count++;
    }                

    
    if(old_current3!=current3||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "c3=" + current3  + " ";
      this.current[2]=current3;

      param_count++;
    }                

       
    if(old_current4!=current4||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "c4=" + current4  + " ";      
      this.current[3]=current4;
      param_count++;
    }                


    if(param_count==17)
    {
      last_full_msg=time;
    }
    
    old_time=time;
    old_lat=lat;
    old_lon=lon;
    old_wpnum=wpnum;
    old_distance=distance;
    old_wp_heading=wp_heading;
    old_udp_xte=udp_xte;
    old_wp_lat=wp_lat;
    old_wp_lon=wp_lon;
    old_speed=speed;
    old_heading=heading;
    old_left_speed=left_speed;
    old_right_speed=right_speed;
    old_current1=current1;
    old_current2=current2;
    old_current3=current3;
    old_current4=current4;  
    System.out.println("Sending " + databuf);
    ds.send(new DatagramPacket(databuf.getBytes(),databuf.length(),InetAddress.getByName("192.168.0.255"),4321));
    updateData();
  }

  static double rad2deg(double x)
  {
    return (180/Math.PI) * x;
  }

  static double deg2rad(double x)
  {
    return x * Math.PI/180;
  }

  public void setWaypoint(int newWaypoint)
  {
    wpnum = newWaypoint;
  }

  public int getWpNum()
  {
    return wpnum;
  }

  public void setLastMsg(String msg)
  {
    lastMsg = msg;
  }

  public void gotoLatLon(float newLat,float newLon)
  {
    wpLat=newLat;
    wpLon=newLon;
  }

  public void loop() throws Exception
  {
    double distance=1.0;
    int heading=0;
    int i=0;
    double speed=0.0;
    long time;

    Random rand = new Random();

    while(true)
    {
      time=System.currentTimeMillis()/1000;
        if(time%10==0)
        {
          //randomize some small changes so we aren't always shown in the same place
          double r = rand.nextDouble();
          r=r/500.0;
          r=r-0.0005;
          
          lat=52.415;
          lat=lat+r;
          
           r = rand.nextDouble();
          r=r/500.0;
          r=r-0.0005;
          
          lon=-4.065;
          lon=lon+r;
        }

      
      //int udp_send(long time,double lat,double lon,int wpnum,double distance,int wp_heading,double udp_xte,double wp_lat,double wp_lon,double speed,int heading,int left_speed,int right_speed,double current1,double current2,double current3,double current4);
        udp_send(time,lat,lon,wpnum,distance,90,1.0,wpLat,wpLon,speed,heading,128,128,1.0,2.0,3.0,4.0);
        
        
        if(time%5==0)
        {
          speed=speed+0.1;
        }
        
        distance=distance+0.1;
        if(distance>2.0)
        {
            distance=1.0;
            heading++;
            wpnum++;
            if(heading>=360)
            {
              heading=0;
            }
        }
        try
        {
          Thread.sleep(1000);
        }
        catch(Exception e){}
    }
  }

  public static void main(String args[]) throws Exception
  {
    
    TestTelemetryHeadless test = new TestTelemetryHeadless();
    test.loop();
  }

  public class ReceiveThread extends Thread
  {
  public static final byte NUDGE_LEFT=1;
  public static final byte NUDGE_RIGHT=2;
  public static final byte STOP=3;
  public static final byte SKIP_WP=4;
  public static final byte GO_HOME=5;
  public static final byte GOTO_WP=6;
  public static final byte GOTO_LATLON=7;
  public static final byte LEFT=8;
  public static final byte RIGHT=9;
  public static final byte BACK=10;
  public static final byte FORWARD=11;
  public static final byte START=12;
  public static final byte START_GENERATOR=13;
  public static final byte STOP_GENERATOR=14;


    DatagramSocket serverSocket;
    byte[] buf = new byte[1024];
    TestTelemetryHeadless gui;

    public ReceiveThread(TestTelemetryHeadless gui) throws SocketException
    {
      serverSocket = new DatagramSocket(1235);
      this.gui=gui;
    }

    public void run()
    {
        try {
         recieve();
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
    }

    public void recieve() throws IOException
    {
      while(true)
      {
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
        serverSocket.receive(receivePacket);
        
                
        if(NUDGE_LEFT==buf[0])
          {

            gui.setLastMsg("Nudge Left");
          }
          else if(NUDGE_RIGHT==buf[0])
          {
          gui.setLastMsg("Nudge Right");
            
          }
          else if(STOP==buf[0])
          {
          gui.setLastMsg("STOP");
            

          }
          else if(SKIP_WP==buf[0])
          {
                      gui.setLastMsg("Skip Wp");
            gui.setWaypoint(gui.getWpNum()+1);
            
          }
          else if(GO_HOME==buf[0])
          {
                      gui.setLastMsg("Go Home");

          }
          else if(GOTO_WP==buf[0])
          {   
            int new_wp = (int) buf[1];
            gui.setWaypoint(new_wp);
                      gui.setLastMsg("Go To WP " + new_wp);

          }
          else if(FORWARD==buf[0])
          {
          gui.setLastMsg("Foward");
            
            
          }
          else if(BACK==buf[0])
          {
                      gui.setLastMsg("Back");

            
          }
          else if(LEFT==buf[0])
          {
                      gui.setLastMsg("Left");

          }
          else if(RIGHT==buf[0])
          {
                      gui.setLastMsg("Right");

          } 
          else if(GOTO_LATLON==buf[0])
          {
            //decode lat lon
            int tmp_lat=0,tmp_lon=0;
            
            tmp_lat = (((int)buf[4]) << 24)&0xFF000000;
            tmp_lat = tmp_lat + ((((int)buf[3]) << 16)&0x00FF0000);
            tmp_lat = tmp_lat + ((((int)buf[2]) << 8)&0x0000FF00);
            tmp_lat = tmp_lat + (((int)buf[1])&0x000000FF);

            
            tmp_lon = (((int)buf[8]) << 24)&0xFF000000;
            tmp_lon = tmp_lon + ((((int)buf[7]) << 16)&0x00FF0000);
            tmp_lon = tmp_lon + ((((int)buf[6]) << 8)&0x0000FF00);
            tmp_lon = tmp_lon + ((int)buf[5]&0x000000FF);
          
            float new_lat = Float.intBitsToFloat(tmp_lat);
            float new_lon = Float.intBitsToFloat(tmp_lon);

          gui.setLastMsg("Go to lat lon " + new_lat + ","+new_lon);

            
            gui.gotoLatLon(new_lat,new_lon);
          }
          else if(START==buf[0])
          {        
            gui.setLastMsg("START\n");
          }
          else if(START_GENERATOR==buf[0])
          {        
            gui.setLastMsg("Start Geneartor\n");
          }
          else if(STOP_GENERATOR==buf[0])
          {        
            gui.setLastMsg("Stop Generator\n");
          }

      }
    }
  }
}

