package routeplanner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import routeplanner.gui.GraphicsUtils;
import routeplanner.datasource.*;
import routeplanner.datasource.udp.*;
import routeplanner.uk.me.jstott.jcoord.*;
import java.net.InetAddress;

public class ImageCanvas extends JComponent implements MouseListener
{
    Image img;

    Map map;

    Graphics g;

    WaypointList wl;

    SendCommand sd;
	
    int currX,currY,lastX,lastY;
    boolean showCrossHairs;
    int crossHairsCount;
    AbstractDataSource [] datasources;
    boolean displayArrow;
	
    boolean rightClick;
    int rightClickx, rightClicky;

    public ImageCanvas(Map map, Image img, WaypointList wl, AbstractDataSource [] datasources)
    {
        this.wl = wl;
        this.map = map;
        this.img = img;
        this.datasources = datasources;
        showCrossHairs=true;

        g = this.getGraphics();
        System.out.println("in constructor, g = " + g);

        /*
         * Toolkit toolkit = Toolkit.getDefaultToolkit(); Image image =
         * toolkit.getImage(mapFileName);
         */
        addMouseListener(this);

        System.out.println("wl = " + wl + " map = " + map + " img = " + img);
	
	try 
	{
	    sd = new SendCommand();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public Dimension getPreferredSize()
    {

        return new Dimension(img.getWidth(null), img.getHeight(null));
    }

    public void paint(Graphics graphics)
    {
        g = graphics;
        graphics.drawImage(map.getImage(), 0, 0, null);

        MapPoint points[] = wl.getPoints();
	
	if(rightClick)
	{
	    graphics.setColor(Color.ORANGE);	
	    graphics.fillOval(rightClickx -3, rightClicky - 3, 6, 6);
	}
	
        for (int i = 0; i < points.length; i++)
        {
            if (i >= 1)
            {
                GraphicsUtils.drawThickLine(graphics, points[i].getX(), points[i].getY(),
                        points[i - 1].getX(), points[i - 1].getY(), 2,
                        Color.GREEN);
            }
	}
	for (int i = 0; i < points.length; i++)
        {
            // draw this point
	    graphics.setColor(Color.RED);
            graphics.fillOval(points[i].getX() - 2, points[i].getY() - 2, 4, 4);
        }

        //display the paths from each data source
        for(int i=0;i<datasources.length;i++)
        {
            if(datasources[i]!=null)
            {
                datasources[i].render(g,displayArrow);
            }
            //DataSourceRenderer.render(datasources[i],g,displayArrow);
        }
        displayArrow=!displayArrow;
    }



    public void mouseClicked(MouseEvent e)
    {
	if ( SwingUtilities.isLeftMouseButton (e) ) 
	{
		System.out.println("mouse clicked at " + e.getX() + "," + e.getY());
		wl.addWaypoint(e.getX(), e.getY());
		System.out.println("points = " + wl.getPoints());
		MapPoint points[] = wl.getPoints();
		
		rightClick = false;		
	}

        else if( SwingUtilities.isRightMouseButton (e) )
	{
		UdpDataSource udp=null;
		System.out.println("mouse rightclicked at " + e.getX() + "," + e.getY());
		rightClick = true;
		rightClickx = e.getX();
		rightClicky = e.getY();
		LatLng latlng = convertCoordLatlng(rightClickx, rightClicky);
		System.out.println("latlng rightclicked " + latlng.getLat() + "," + latlng.getLng());
		
        //display the paths from each data source
        for(int i=0;i<datasources.length;i++)
        {
            if(datasources[i] instanceof UdpDataSource)
            {
		udp = (UdpDataSource) datasources[i];                
            }
            //DataSourceRenderer.render(datasources[i],g,displayArrow);
        }
		
		try 
		{
		    sd.gotoLocation((float)latlng.getLat(), (float)latlng.getLng(),udp.getRemoteAddress());
		}
		catch (Exception exc)
		{
		    exc.printStackTrace();
		}
	}
	
	repaint();
    }

    public void mouseEntered(MouseEvent e)
    {

    }

    public void mousePressed(MouseEvent e)
    {

    }

    public void mouseExited(MouseEvent e)
    {

    }

    public void mouseReleased(MouseEvent e)
    {

    }

    public int getCurrX()
    {
    
        return currX;
    }

    public void setCurrX(int newX)
    {
       
        currX = newX;
    }

    public int getCurrY()
    {
       // System.out.println("getting y as " + currY);
        return currY;
    }

    public void setCurrY(int newY)
    {
       // System.out.println("y set to " + newY);
        currY = newY;
    }

    public void setRightClick(boolean rc)
    {
	rightClick=rc;
    }

    public LatLng convertCoordLatlng(int x, int y)
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
	
	return latlng;
    }
}
