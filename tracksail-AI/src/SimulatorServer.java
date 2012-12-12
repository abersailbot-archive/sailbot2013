
import java.net.*;
import java.rmi.RemoteException;
import java.io.*;

public class SimulatorServer extends Thread
{
    static final int TCP_LISTEN_PORT = 5555;
    String remoteAddress;
    //interface to control the boat
    ServerInterface ts_server;
    //interface to the player
    Player player=null;  
    int currentSailAngle;
    //the number of the player in the game
    int playerNumber;
    
    //store the rudder angle as we need to  keep recalculating the turn
    int rudderAngle;

    public SimulatorServer(ServerInterface simIface, int playerNum)
    {
        ts_server = simIface;
        playerNumber = playerNum;
        try
        {
            player = ts_server.getPlayer(playerNum);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket(TCP_LISTEN_PORT);
            while (true)
            {
                new RudderThread().start();
                System.out.println("waiting for new connection\n");
                Socket socket = ss.accept();
                ClientThread tcpHandler = new ClientThread(socket);
                tcpHandler.start();
                //we can only have 1 input at once, so to prevent any further
                // processes opening a tcp connection
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public class ClientThread extends Thread
    {
        DataInputStream fromClient;
        DataOutputStream toClient;

        public ClientThread(Socket socket) 
        {

            try
            {
                fromClient = new DataInputStream(socket.getInputStream());
                toClient = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void run()
        {
            String response;

            
            try
            {
                while (true)
                {
                    //read some data from the client
                    byte buf [] = new byte[255];
                    int nbytes = fromClient.read(buf);
                    String data = new String(buf);
                    data = data.trim();
                    response = processInput(data);
                    //add C style termination to it
                    
                    //terminate the string properly
                    byte sendBuf [] = response.getBytes();
                    byte sendBufTerm [] = new byte[sendBuf.length+1];
                    
                    for (int i=0;i<sendBuf.length;i++)
                    {
                        sendBufTerm[i] = sendBuf[i];    
                    }
                    
                    
                    toClient.write(sendBufTerm,0,sendBufTerm.length);
                    toClient.flush();
                }

                /*
                 * toClient.close(); fromClient.close();
                 */
            }
            catch (Exception e)
            {
                e.printStackTrace();
                try
                {
                    toClient.close();
                    fromClient.close();
                    System.out.println("Connection closed");
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        /**
         * @param input -
         *            a string containing a command
         * @return the result of the executed command, will be blank for set
         *         commands
         */
        public String processInput(String input)
        {

            String type, command, params = "";
            int begin = 0;

            type = splitter(input,' ',1);
            command=splitter(input,' ',2);
                        //only if there's some text left after the type and command do we

            // get the parameters
            if (type.length() + command.length() + 2 < input.length())
            {
                params = splitter(input,' ',3);
            }

            if (type.equalsIgnoreCase("set"))
            {
                if(command.equalsIgnoreCase("waypoint"))
                {
                    int whichWp=Integer.parseInt(params);
                    player.getGame().gotoNextWp(player);
                    return "OK";
                }
                //rudder centres around 0, full left is at 270 and full right is at 90
                else if (command.equalsIgnoreCase("rudder"))
                {

                    int angle=Integer.parseInt(params);
                    if (angle>=0&angle<=360)
                    {
                       rudderAngle=angle;
                    }
                      else
                    {
                        return "ERROR-1:Value out of range";
                    }
                    return "OK";
                }
                else if (command.equalsIgnoreCase("sail"))
                {

                    try
                    {          
                        //get desired sail direction in terms against the boat
                        int desired_direction = Integer.parseInt(params);
                                               
                        //sail cannot be placed between 270 and 90 degrees
                        if (desired_direction>90&&desired_direction<=180)
                        {
                            desired_direction=90;
                        }
                        if (desired_direction>180&&desired_direction<270)
                        {
                            desired_direction=270;
                        }
                        
                        //change coords to normal boat like ones, from robot like ones
                        //on the robot sail angle taken at front of sail, not rear
                        //valid angles are 0-90 and 270-360 not 90-270.
                        //90 means the same, 135 means 45, 180 means 0
                        //270 means the same, 225 means 315, 180 means 360
                        if (desired_direction<180)
                        {
                            desired_direction = 180 - desired_direction;
                        }
                        else
                        {
                            desired_direction = (360 - desired_direction) + 180;
                        }
                        
                        desired_direction = 360 - desired_direction; 
                        
                        //invert angle (180 becomes 0, vice versa) to meet tracksail method
                        desired_direction = convertCoords(desired_direction);
                        
                        //System.out.println("desired_direction = " + desired_direction);
                        
                        desired_direction = ((360 + desired_direction) - 90) % 360;
                        
                        //System.out.println("modified desired_direction = " + desired_direction);
                        

                        
                        int wind_dir = player.getGame().getWindDirection();
                        
                        int boat_dir = player.getDirection();
                        
                        //equivalent to sail_dir code in player.java
                        //this is the maximum angle the sail may reach
                        int sail_dir = (wind_dir-boat_dir+360)%360;
                        
                        // Change the scale 0..100 % => 0...90 degrees.
                        int restrict_angle = 90;
                        int new_sail_dir=sail_dir;
                        // The sail may swing between k1 ... k2 against the direction of
                        // the boat.
                        /*int k1 = (180 - restrict_angle+360)%360;
                        int k2 = (180 + restrict_angle)%360;*/
                        int k1 = 90;
                        int k2 = 270;
                        
                        

                        
                        if( sail_dir < k1 ) 
                        {
                            new_sail_dir = k1;
                        }
                        
                        if( sail_dir > k2 ) 
                        {
                            new_sail_dir = k2;
                        }
                        
                        int max=new_sail_dir;
                        


                        restrict_angle = 0;
                        new_sail_dir = sail_dir;
                        k1 = (180 - restrict_angle+360)%360;
                        k2 = (180 + restrict_angle)%360;
                        if( sail_dir < k1 ) 
                        {
                            new_sail_dir = k1;
                        }
                        
                        if( sail_dir > k2 ) 
                        {
                            new_sail_dir = k2;
                        }
                        
                        int min=new_sail_dir;
                        
          
                        
                        //swap if order is wrong
                        if (min>max)
                        {
                            /*int temp = min;
                            min = max;
                            max = temp;
                            System.out.println("Swapping min and max");*/
                            if (desired_direction<=max)
                            {
                                desired_direction=max;
                            }
                            if (desired_direction>=min)
                            {
                                desired_direction=min;
                            }   
                        }
                        else
                        {        
                            //is our angle outside this range?
                            if (desired_direction<=min)
                            {
                                desired_direction=min;
                            }
                            if (desired_direction>=max)
                            {
                                desired_direction=max;
                            }
                        }
                        
                        //now figure out where in the range it lies
                        //scale to 0-100
                        int range = max-min;

                        
                        float angle = desired_direction - min;

                        
                        angle = (float) angle / (float) range;                      
                        angle = angle * 100;
                        

                        
                        //server uses values between 0 and 100 to represent the sail
                        ts_server.setSail(playerNumber,(int)angle);   
                        return "OK";
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                        return "ERROR-3: Communication Error";
                    } 
                }
                else if(command.equalsIgnoreCase("unwind"))
                {
                    System.out.println("Received unwind, exiting");
                    System.exit(0);
                }
                else
                {
                    System.err.println("Invalid Set command " + command + "_");
                    return "ERROR-4: Missing or invalid command";
                }
            }
            else if (type.equalsIgnoreCase("get"))
            {

                if (command.equalsIgnoreCase("wind_dir"))
                {

                    //needs to be relative to boat
                    int angle = player.getGame().getWindDirection();

                    //make relative to the boat
                    angle = ((player.getDirection() - angle) + 360 ) % 360;
                    
                    //invert the angle by 180 degrees
                    angle = (angle + 180) % 360;

                    
                    return new Integer(angle).toString();
                }
                else if (command.equalsIgnoreCase("compass"))
                {

                    
                    int angle=(player.getDirection()+360)%360;  

                    angle = convertCoords(angle);                 
                    //needs to convert from tracksail angles
                    return new Integer(angle).toString();
                }
                else if (command.equalsIgnoreCase("waypointdir"))
                {
                    Track t = player.getGame().getTrack();
                    Vector2 tgt = Vector2.average(t.getPortPB(player.getNextPort()),
                                  t.getPortSB(player.getNextPort()) );
                    // subtract the position
                    // of our own boat
                    tgt.sub(player.getPosition());
                    // set length as 1.0f
                    tgt.normalize();
                    int angle = (int)Math.toDegrees(tgt.getDirection());
                    angle = convertCoords(angle);
                    return new Integer(angle).toString();
                }
                else if (command.equalsIgnoreCase("waypointnum"))
                {
                    return new Integer(player.getNextPort()).toString();
                }
                else if (command.equalsIgnoreCase("numofwaypoints"))
                {
                    return new Integer(player.getGame().getTrack().getPortCount()).toString();
                }
                else if (command.equalsIgnoreCase("waypointdist"))
                {
                    return new Integer((int)player.getDistToNextPort()).toString();
                }
                else if (command.equalsIgnoreCase("waypointnorthing"))
                {
                    int wpnum=Integer.parseInt(params);
                    Track t = player.getGame().getTrack();
                    int northing = (int)t.getPortPB(wpnum).getY();
                    return new Integer(northing).toString();
                }
                else if (command.equalsIgnoreCase("waypointeasting"))
                {
                    int wpnum=Integer.parseInt(params);
                    Track t = player.getGame().getTrack();
                    int easting = (int)t.getPortPB(wpnum).getX();
                    return new Integer(easting).toString();
                }
                else if (command.equalsIgnoreCase("lastwaypointnorthing"))
                {
                    Track t = player.getGame().getTrack();
                    int northing=0;
                    if(player.getNextPort()>0)
                    {
                        northing = (int)t.getPortPB(player.getNextPort()-1).getY();
                    }
                    return new Integer(northing).toString();
                }
                else if (command.equalsIgnoreCase("lastwaypointeasting"))
                {
                    Track t = player.getGame().getTrack();
                    int easting = 0;
                    if(player.getNextPort()>0)
                    {
                        easting = (int)t.getPortPB(player.getNextPort()-1).getX();
                    }
                    return new Integer(easting).toString();
                }

                else if (command.equalsIgnoreCase("sail"))
                {

                    int wind = player.getGame().getWindDirection();
                    currentSailAngle = player.calcSailDirection(wind) -  player.getDirection();
                    //invert from weird tracksail angles
                    currentSailAngle = convertCoords(currentSailAngle);
                    
                    //convert to just as weird robot angles!
                    currentSailAngle = ((360 + currentSailAngle) - 270) % 360;
                    
                    //remove negative sign if there is one
                    if (currentSailAngle<0)
                    {
                        currentSailAngle = currentSailAngle * -1;
                    }
                    
                    System.out.println("negative sign removed " + currentSailAngle);
                    
                    return new Integer(currentSailAngle).toString();       
                }
                else if (command.equalsIgnoreCase("northing"))
                {

                    float x = player.getPosition().getY();
                    return new Float(x).toString();
                    
                }
                else if (command.equalsIgnoreCase("easting"))
                {

                    float y = player.getPosition().getX();
                    return new Float(y).toString();
                }
                else
                {

                    return "ERROR-4:Missing or invalid command";
                }
            }//end of get

            return "ERROR-2:Invalid Mode";
        }//end of method
        
        /**An awk style string splitter, divides the string into different fields
        separated by character divider, will only return field number asked for.
        e.g. if input is "hello world", divider is a space and fieldnum is 1 then hello is returned
        with a fieldnum of 2 world is returned*/
        protected String splitter(String input,char divider,int fieldnum)
        {
            char temp;
            int fieldstart=0; //position at which this field begins           
            int i,j;
            for (j=0;j<fieldnum;j++)
            {
	            for(i=fieldstart;i<input.length();i++)
	            {
	                temp = input.charAt(i);
	                if (temp==divider||temp==10||temp==13)
	                {
	                    break;
	                }
	            }
	            //j hasn't been incremented yet so look at fieldnum -1
	            if (j==fieldnum-1)
	            {
	                return input.substring(fieldstart,i);
	            }
	            //move fieldstart onto this field
	            fieldstart=i+1;
            }
            return null;           
        }
        
        /**
         * Flips an angle hoziontally
         * for instance 270 degrees becomes 90 and vice versa
         * 1 becomes 359
         * 181 becomes 179
         * @param angle
         * @return an flipped angle
         */
        protected int convertCoords(int angle)
        {
            return ((360 + ((180 - angle) + 180)) + 90) % 360; 
        }
        
    }//end of class ClientThread
    
    /*thread to keep turning rudder*/
    public class RudderThread extends Thread
    {
        public void run()
        {
            int turnAngle=0;
            
            /*
            small rudder angle, higher waits, slow turn
            large rudder angle, lower waits, quick turn
            angle = 1, wait = 5000,  = 5000
            angle = 90, wait = 
            quick turn 360 degrees in 36 seconds
            1 degree per 100 milliseconds
            
            rudder = 90|270 time = 100
            rudder = 1|359 time = 1000
            
            -10*angle + 1010 = time
            
                      
            
            slow turn 360 degrees in 360 seconds
            1 degree per second
            */
            
            while(true)
            {
                if (rudderAngle<=359&rudderAngle>=270)
                {
                    //perform left turns in proportion to rudder
                    //at most we can turn 10 degrees
                    turnAngle = 360-rudderAngle;

                    player.turnLeft(turnAngle/10);
                }
                else if(rudderAngle>0&rudderAngle<=90)
                {
                    //perform right turns in proportion to rudder setting
                    turnAngle = rudderAngle;

                    player.turnRight(turnAngle/10);
                }
                
                try
                {
                    sleep(500);
                }
                catch (InterruptedException e)
                {
                
                }
            }
        }
        public void setRudderAngle(int newAngle)
        {
            rudderAngle = newAngle;
        }
        
    }
    

}//end of class SimulatorServer