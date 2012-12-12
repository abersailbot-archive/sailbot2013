package routeplanner.datasource;

import routeplanner.MapPoint;
import routeplanner.Map;
import routeplanner.uk.me.jstott.jcoord.*;
import routeplanner.util.GreatCircle;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.util.Vector;



/*
 * an abstract data source
 * each data source somehow gets the current position, heading and speed.
 * 
 */

public abstract class AbstractDataSource
{
    protected int updateInterval;
    protected String dataSourceName;
    protected Color color=Color.GREEN;
    protected Image icon;

    protected double curr_lat,curr_lon;
    protected double curr_heading;
    protected double curr_speed;
    protected boolean fixValid;

    protected int currX,currY;

    protected Vector <MapPoint> points;

    protected Thread t = new updateThread();

    //map object representing the map we are using, this provides methods to convert pixels to coordinates
    protected Map map;
    protected boolean running=false;
    
    public boolean isRunning()
    {
	return running;
    }

    public Color getColor()
    {

        return color;
    }

    public void setColor(Color color)
    {

        this.color = color;
    }

    public String getDataSourceName()
    {

        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName)
    {

        this.dataSourceName = dataSourceName;
    }

    public Image getIcon()
    {

        return icon;
    }
    public void setIcon(Image icon)
    {

        this.icon = icon;
    }

    public int getUpdateInterval()
    {

        return updateInterval;
    }

    public void setUpdateInterval(int updateFrequency)
    {

        this.updateInterval = updateFrequency;
    }

    /**
     * @return the current heading
     */
     public double getHeading()
     {

         return curr_heading;
     }

     /**
      * @return the current latitude
      */
     public double getLat()
     {

         return curr_lat;
     }

     /**
      * @return the current longitude
      */
     public double getLon()
     {

         return curr_lon;
     }

     /**
      * @return the current speed in km/h
      */
     public double getSpeed()
     {

         return curr_speed;
     }


    public int getCurrX()
    {
        return currX;
    }
     
    public int getCurrY()
    {
        return currY;
    }

     public boolean isFixValid()
     {
         return fixValid;
     }

    public Map getMap()
    {
        return map;
    }


    public void clearPoints()
    {
        points=new Vector<MapPoint>();
    }

     /**
      * gets the list of points visited so far
      * @return
      */
     public MapPoint[] getPoints()
     {
         return points.toArray(new MapPoint[points.size()]);
     }

     /**
      * adds a new point to the list of visited points
      */
     protected void addPoint(double lat,double lon)
     {
         int x,y;
        MapPoint previousPoint=null;
         //only add the point if its more than 5m from the previous one
         if(points.size()>0)
         {
             previousPoint = points.lastElement();
         }
         else
        {
            System.out.println("points size is 0");
        }

         if(previousPoint==null||GreatCircle.getDistance(lat,lon,previousPoint.getLat(),previousPoint.getLng())>0.05)
         {

             LatLng latlng = new LatLng(lat,lon);
             //do we need to convert this position to OS grid to calculate its pixel positon?
             if(map.isOSMap())
             {
                 latlng.toOSGB36();
                
                 OSRef os1 = latlng.toOSRef();
                 x = map.eastingToXCoord((int)os1.getEasting());
                 y = map.northingToYCoord((int)os1.getNorthing());
              //  System.out.println("adding point " + lat + "," + lon + " ( " + os1.getEasting() + "," + os1.getNorthing() + " ) " + x + "," + y);
             }
             else
             {
                 x = map.eastingToXCoord(lon);
                 y = map.northingToYCoord(lat);
             }
             currX=x;
             currY=y;
             //add the new point to the list
             MapPoint p = new MapPoint(latlng.getLat(),latlng.getLng(),lat,lon,x,y);
             try
             {
                
                points.add(p);
             }
             catch(java.util.NoSuchElementException e)
            {
                e.printStackTrace();
                System.out.println("p = " + p);
            }

         }
     }

     public void startReading()
     {
	running=true;
         t.start();
     }
     
     public void stopReading()
     {
        running=false;
        t.stop();
     }

     /**
      * this method updates the current position, heading and  speed
      * it also calls addPoint when a new position is read 
      */
     protected abstract void updateState();


    public abstract void render(Graphics g,boolean drawArrow);


     /**
      * thread to update the current state from a data source
      */
     protected class updateThread extends Thread
     {
         public void run()
         {
            while(true)
            {
                    updateState();
                    try
                    {
                        Thread.sleep(updateInterval);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
            }
         }
     }

     public AbstractDataSource(Map map)
     {
         this.map = map;
         //update every second, the user may alter this via the setUpdateFrequency method
         updateInterval=100;
        points=new Vector<MapPoint>();
     }


}
