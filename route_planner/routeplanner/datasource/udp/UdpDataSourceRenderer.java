package routeplanner.datasource.udp;

import routeplanner.Map;
import routeplanner.uk.me.jstott.jcoord.*;
import routeplanner.datasource.AbstractDataSourceRenderer;
import routeplanner.gui.GraphicsUtils;

import java.awt.Color;
import java.awt.Graphics;

public class UdpDataSourceRenderer extends AbstractDataSourceRenderer
{
    private static TelemetryWindow tw = null;

    public void render(UdpDataSource ds,Graphics g,boolean drawArrow)
    {
        super.render(ds,g,drawArrow);
	if(!ds.isRunning())
	{
	    return;
	}

        double wplat,wplon;
        int x,y;
        Map map;

        if(tw==null)
        {
            tw=new TelemetryWindow(ds);
        }
        tw.updateData();



        map = ds.getMap();
        wplat=ds.getWpLat();
        wplon=ds.getWpLon();

        LatLng latlng = new LatLng(wplat,wplon);
        //do we need to convert this position to OS grid to calculate its pixel positon?
        if(map.isOSMap())
        {
            latlng.toOSGB36();
            OSRef os1 = latlng.toOSRef();
            x = map.eastingToXCoord((int)os1.getEasting());
            y = map.northingToYCoord((int)os1.getNorthing());
        }
        else
        {
            x = map.eastingToXCoord(wplon);
            y = map.northingToYCoord(wplat);
        }
        
        if(drawArrow) //alternate between a line to the waypoint and a circle around the waypoint 
        {
            g.setColor(Color.GREEN);
            g.drawOval(x-15,y-15,30,30);
            g.drawOval(x-14,y-14,29,29);
            g.drawOval(x-13,y-13,28,28);
        }
        else
        {
            g.setColor(Color.DARK_GRAY);
            GraphicsUtils.drawThickLine(g,x,y, ds.getCurrX(), ds.getCurrY(),2, Color.DARK_GRAY);
        }
        
        g.setColor(Color.WHITE);
        g.fillRect(x-15,y-30,35,11);
        g.setColor(Color.BLACK);
        g.drawString("WP " + ds.getWpnum(),x-15,y-20);
        g.drawLine(x-2,y-2,x+2,y+2);
        g.drawLine(x-2,y+2,x+2,y-2);          

    }
}
