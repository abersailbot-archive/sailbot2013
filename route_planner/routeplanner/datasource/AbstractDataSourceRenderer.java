package routeplanner.datasource;

import routeplanner.MapPoint;
import routeplanner.gui.GraphicsUtils;

import java.awt.Color;
import java.awt.Graphics;

public abstract class AbstractDataSourceRenderer
{
    /**
     * renders the path recorded from a datasource
     * @param ds - a datasource to render 
     * @param g - the display to render to
     */
    public void render(AbstractDataSource ds,Graphics g,boolean displayArrow)
    {
	if(!ds.isRunning())
	{
	    System.out.println("not running");
	    return;
	}
        float red,green,blue;
        red=(float)ds.getColor().getRed();
        green=(float)ds.getColor().getGreen();
        blue=(float)ds.getColor().getBlue();
        red=red/255;
        blue=blue/255;
        green=green/255;
        
        Color temp = new Color(red,green,blue,(float)0.2);
        g.setColor(ds.getColor());

        MapPoint points[] = ds.getPoints();

        for (int i = 0; i < points.length; i++)
        {
            // draw this point
            g.fillOval(points[i].getX() - 1, points[i].getY() - 1, 3, 3);

            if (i >= 1)
            {
                GraphicsUtils.drawThickLine(g, points[i].getX(), points[i].getY(),
                        points[i - 1].getX(), points[i - 1].getY(), 3,
                        temp.brighter());
            }
        }

        //only draw if we have a valid position
        //only draw every other time (when system.currentTimeMillis is divisible by 2)
        int currX=ds.getCurrX();
        int currY=ds.getCurrY();
        if(currX!=0&&currY!=0)
        {

                if(displayArrow)
                {
                    int x1 = currX+(int)(10*Math.sin((((ds.getHeading()+90)%360)/180)*Math.PI));
                    int y1 = currY-(int)(10*Math.cos((((ds.getHeading()+90)%360)/180)*Math.PI));
    
                    int x2 = currX+(int)(20*Math.sin((ds.getHeading()/180)*Math.PI));
                    int y2 = currY-(int)(20*Math.cos((ds.getHeading()/180)*Math.PI));
    
                    int x3 = currX+(int)(10*Math.sin((((ds.getHeading()-90)%360)/180)*Math.PI));
                    int y3 = currY-(int)(10*Math.cos((((ds.getHeading()-90)%360)/180)*Math.PI));
    
                    GraphicsUtils.drawThickLine(g,x1,y1,x2,y2,5,ds.getColor().darker());
                    GraphicsUtils.drawThickLine(g,x2,y2,x3,y3,5,ds.getColor().darker());
                }


                if(ds.getDataSourceName()!=null)
                {
                    g.setColor(Color.WHITE);
                    g.fillRect(currX,currY+20,ds.getDataSourceName().length()*8,11);
                    g.setColor(Color.BLACK);
                    g.drawString(ds.getDataSourceName(),currX,currY+30);
                }

/*
            //g.setColor(Color.GREEN);
            //g.fillOval(currX,currY,10, 10);
            //currx = currx+1;
            System.out.println("currX = " + currX + " currY = " + currY);
            GraphicsUtils.drawThickLine(g,currX - 20, currY, currX - 2, currY,3,ds.getColor());
            g.setColor(Color.BLACK);
            g.drawLine(currX - 20, currY-2, currX - 2, currY-1);
            g.drawLine(currX - 20, currY+2, currX - 2, currY+1);
    
            GraphicsUtils.drawThickLine(g,currX + 20, currY, currX + 2, currY,3,ds.getColor());
            g.setColor(Color.BLACK);
            g.drawLine(currX + 20, currY-2, currX + 2, currY-1);
            g.drawLine(currX + 20, currY+2, currX + 2, currY+1);
    
            GraphicsUtils.drawThickLine(g,currX, currY + 20, currX, currY + 2,3,ds.getColor());    
            g.setColor(Color.BLACK);
            g.drawLine(currX-1, currY + 20, currX-1, currY + 2);
            g.drawLine(currX+1, currY + 20, currX+1, currY + 2);
    
            GraphicsUtils.drawThickLine(g,currX,currY-20,currX,currY-2,3,ds.getColor());
            g.setColor(Color.BLACK);
            g.drawLine(currX-1, currY - 20, currX-1, currY - 2);
            g.drawLine(currX+1, currY - 20, currX+1, currY - 2);*/

        }

    }

}
