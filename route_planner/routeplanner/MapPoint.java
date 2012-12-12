package routeplanner;

public class MapPoint
{
    
    double lat,lng;
    double easting,northing;
    int x,y;
    long time;
    
    public double getEasting()
    {
        return easting;
    }
    
    public void setEasting(double easting)
    {
        this.easting = easting;
    }
    
    public double getLat()
    {
        return lat;
    }
    
    public void setLat(double lat)
    {
        this.lat = lat;
    }
    
    public double getLng()
    {
        return lng;
    }
    
    public void setLng(double lng)
    {
        this.lng = lng;
    }
    
    public double getNorthing()
    {
        return northing;
    }
    
    public void setNorthing(double northing)
    {
        this.northing = northing;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    /**
     * @return gets the time this point was created
     */
    public long getTime()
    {
        return time;
    }

    public MapPoint(double lat, double lng, double easting, double northing, int x, int y)
    {
        this.lat = lat;
        this.lng = lng;
        this.easting = easting;
        this.northing = northing;
        this.x = x;
        this.y = y;
        time=System.currentTimeMillis()/1000;
    }
    
    public String toString()
    {
        String returnString = lat + "," + lng;
        return returnString;
    }
}
