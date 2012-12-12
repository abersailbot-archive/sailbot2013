package routeplanner;

import java.io.*;
import java.awt.*;
import java.util.*;

public class Map
{
    protected Image image;
    protected double topNorthing,leftEasting,bottomNorthing,rightEasting;
    protected int width,height;
    protected double xMetrePerPix,yMetrePerPix;
    protected double xPixPerMetre,yPixPerMetre;
    //indicates this is an OS map and that we should convert coords to lat/lons and to WGS84
    protected boolean isOsMap=false;
    
    /**
    figures out the information regarding a map from its descriptor file.
    this should have two lines of the format:
    
    x,y = northing,easting
    
    where x and y are pixels in the image
    and easting and northing as OS coordinates such as 282000 

    */
    public Map(Image map,String imageDescriptorName,int h,int w)
    {
        /*width = map.getWidth();
        height = map.getHeight();*/
        image = map;
        width = w;
        height = h;
        parseFile(imageDescriptorName);
    }


    public Image getImage()
    {
        return image;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }
    
    public boolean isOSMap()
    {
	return isOsMap;
    }

    public double xCoordToEasting(int x)
    {
        double x_location = (xMetrePerPix * x) + leftEasting;
        System.out.println("converting x coord " + x + " to easting " + x_location);
        return x_location;
    }

    public double yCoordToNorthing(int y)
    {
        //invert the coordinate to be like a map
        y = (height - y);

        double y_location = (yMetrePerPix * y) + bottomNorthing;
        System.out.println("converting y coord " + y + " to northing " + y_location);

        return y_location;
    }

    public int eastingToXCoord(double easting)
    {
        int x = (int)((easting - leftEasting)/xMetrePerPix);

        return x;
    }

    public int northingToYCoord(double northing)
    {
        int y = (int)((topNorthing - northing)/yMetrePerPix);
        return y;
    }

    protected void parseFile(String filename)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            //read first line
            line = in.readLine();
	    
	    //if first line is OS then its an OS Map, if its notOS then its not
	    if(line.equals("OS"))
	    {
		isOsMap=true;
	    }
	    else
	    {
		isOsMap=false;
	    }
	    
	    line = in.readLine();
	    
            //parse it
            StringTokenizer st1 = new StringTokenizer(line);
            String pixelCoord = st1.nextToken();
            System.out.println("first token " + pixelCoord);
            String equals = st1.nextToken();
            System.out.println("second token " + equals);
            if(!equals.equals("="))
            {
                throw new NoSuchElementException("equals no found");
            }
            String mapCoord = st1.nextToken();

            //get northing/easting
            StringTokenizer st2 = new StringTokenizer(mapCoord,",");
    	    double easting1 = Double.parseDouble(st2.nextToken());
            double northing1 = Double.parseDouble(st2.nextToken());

            //get x/y
            st2 = new StringTokenizer(pixelCoord,",");
            int x1 = Integer.parseInt(st2.nextToken());
            int y1 = Integer.parseInt(st2.nextToken());

            //read second line
            line = in.readLine();
            //parse it
            st1 = new StringTokenizer(line);
            pixelCoord = st1.nextToken();
            if(!st1.nextToken().equals("="))
            {
                throw new NoSuchElementException("equals no found");
            }
            mapCoord = st1.nextToken();

            //get northing/easting
            st2 = new StringTokenizer(mapCoord,",");
            double easting2 = Double.parseDouble(st2.nextToken());
            double northing2 = Double.parseDouble(st2.nextToken());

            //get x/y
            st2 = new StringTokenizer(pixelCoord,",");
            int x2 = Integer.parseInt(st2.nextToken());
            int y2 = Integer.parseInt(st2.nextToken());


            System.out.println("x1 = " + x1);
            System.out.println("y1 = " + y1);
            System.out.println("x2 = " + x2);
            System.out.println("y2 = " + y2);

            System.out.println("easting1 = " + easting1);
            System.out.println("northing1 = " + northing1);
            System.out.println("easting2 = " + easting2);
            System.out.println("northing2 = " + northing2);

            
            //figure out the map scale
            double xdiff = x2-x1;
            double ydiff = y2-y1;
            double eastingdiff = easting2 - easting1;
            double northingdiff = northing2 - northing1;

            xMetrePerPix = Math.abs((double) eastingdiff / (double) xdiff);

            yMetrePerPix = Math.abs((double) northingdiff / (double) ydiff);

            xPixPerMetre = 1/xMetrePerPix;
            yPixPerMetre = 1/yMetrePerPix;

            //height/width of the map in map units

            double mapHeight = yMetrePerPix * height;
            double mapWidth = xMetrePerPix * width;


            //how far is it from one of our points to the bottom right edge?
            double edgeDistX = width - x2;
            double edgeDistY = height - y2;
            System.out.println("edgeDistX = " + edgeDistX);
            System.out.println("edgeDistY = " + edgeDistY);

            //convert this distance
            double edgeDistX2 = xMetrePerPix * edgeDistX;
            double edgeDistY2 = yMetrePerPix * edgeDistY;
            System.out.println("edgeDistX2 = " + edgeDistX2);
            System.out.println("edgeDistY2 = " + edgeDistY2);

            //now figure out where the edge is by adding the point to this
            rightEasting = edgeDistX2 + easting2;
            bottomNorthing = northing2 - edgeDistY2;

            topNorthing = bottomNorthing + mapHeight;
            leftEasting = rightEasting - mapWidth;
            
            System.out.println("mapHeight = " + mapHeight);
            System.out.println("mapWidth = " + mapWidth);
            System.out.println("topNorthing = " + topNorthing);
            System.out.println("bottomNorthing = " + bottomNorthing);
            System.out.println("leftEasting = " + leftEasting);
            System.out.println("rightEasting = " + rightEasting);
            System.out.println("xMetrePerPix = " + xMetrePerPix);
            System.out.println("yMetrePerPix = " + yMetrePerPix);
	    

            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(NoSuchElementException e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.err.println("Invalid data file format");
            System.exit(1);
        }
    }
}