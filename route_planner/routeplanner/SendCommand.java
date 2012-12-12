package routeplanner;
import java.net.*;
public class SendCommand {

  public static int clientPort = 1235;
  
  protected DatagramSocket ds;

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
  public static final byte HEADING_HOLD=15;


/**
constructor for the SendCommand class
this creates the datagram socket which is used to send data to the robot
*/
  public SendCommand() throws Exception
  {
    ds = new DatagramSocket();
  }

/**
converts a float to an array of bytes in order for them to be sent over the network
@param data - the float to convert
@returns - an array of bytes representing the float 
*/
  public byte[] floatToByteArray(float data) {
    int rawint = Float.floatToRawIntBits(data);
    return new byte[] {
      (byte)((rawint >> 24) & 0xff),
      (byte)((rawint >> 16) & 0xff),
      (byte)((rawint >> 8) & 0xff),
      (byte)((rawint >> 0) & 0xff),
    };
  }
  
/**
sends a message to go to a certain location specified by the lat and lon parameters.
@param lat - the latitude of the new location
@param lon - the longitude of the new location
@param dest - the destination IP address to send the data to (the robot's IP address)
*/
  public void gotoLocation(float lat,float lon,InetAddress dest) throws Exception 
  {
    byte buffer[] = new byte[11];

    buffer[0]=SendCommand.GOTO_LATLON; 

    byte floatbuf[] = floatToByteArray(lat);
    buffer[1]=floatbuf[3];
    buffer[2]=floatbuf[2];
    buffer[3]=floatbuf[1];
    buffer[4]=floatbuf[0];

    floatbuf = floatToByteArray(lon);
    buffer[5]=floatbuf[3];
    buffer[6]=floatbuf[2];
    buffer[7]=floatbuf[1];
    buffer[8]=floatbuf[0];
    buffer[9]=0;   

    ds.send(new DatagramPacket(buffer,10,InetAddress.getLocalHost(),clientPort));
  }

  /**
  tells the robot to drive to a specfic (pre loaded) waypoint
  @param waypointNumber - the number of the waypoint we want the robot to go to
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void gotoWaypoint(byte waypointNumber,InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[3];

    buffer[0]=SendCommand.GOTO_WP; 
    buffer[1]=waypointNumber;
    buffer[2]=0;

    ds.send(new DatagramPacket(buffer,3,InetAddress.getLocalHost(),clientPort));
  }

  /**
  Tells the robot to turn left for 5 seconds while staying in autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void nudgeLeft(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.NUDGE_LEFT; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to turn right for 5 seconds while staying in autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */

  public void nudgeRight(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.NUDGE_RIGHT; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to stop and exit autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */

  public void stop(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.STOP; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to increase the speed of the left motor and  exit autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void left(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.LEFT; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to increase the speed of the right motor and  exit autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void right(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.RIGHT; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to decrease the speed of both motors and  exit autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void back(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.BACK; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to increase the speed of both motors and  exit autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void forward(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.FORWARD; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to ignore the next waypoint
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void skipWp(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.SKIP_WP; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to go to a pre-defined home location
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void goHome(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.GO_HOME; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }


  /**
  Tells the robot to start autonomous navigation mode
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void start(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.START; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }


  /**
  Tells the robot to start its generator
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void startGenerator(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.START_GENERATOR; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to stop its generator
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void stopGenerator(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.STOP_GENERATOR; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }

  /**
  Tells the robot to Heading hold
  @param dest - the destination IP address to send the data to (the robot's IP address)
  */
  public void headingHold(InetAddress dest) throws Exception
  {
    byte buffer[] = new byte[2];

    buffer[0]=SendCommand.HEADING_HOLD; 
    buffer[1]=0;

    ds.send(new DatagramPacket(buffer,2,dest,clientPort));
  }


 /* public static void main(String args[]) throws Exception 
  {
    InetAddress dest = InetAddress.getLocalHost();
    SendCommand send = new SendCommand();
    System.out.println("Location");
    send.gotoLocation((float)52.4,(float)-4.0,dest);
    Thread.sleep(1000);

    System.out.println("gotowp");
    send.gotoWaypoint((byte)17,dest);
    Thread.sleep(1000);

    System.out.println("gotohome");
    send.goHome(dest);
    Thread.sleep(1000);

    System.out.println("skipwp");
    send.skipWp(dest);
    Thread.sleep(1000);

    System.out.println("forward");
    send.forward(dest);
    Thread.sleep(1000);

    System.out.println("back");
    send.back(dest);
    Thread.sleep(1000);

    System.out.println("left");
    send.left(dest);
    Thread.sleep(1000);

    System.out.println("right");
    send.right(dest);
    Thread.sleep(1000);

    System.out.println("stop");
    send.stop(dest);
    Thread.sleep(1000);


    System.out.println("nudge left");
    send.nudgeLeft(dest);
    Thread.sleep(1000);

    System.out.println("nudge right");
    send.nudgeRight(dest);
  }*/
}
