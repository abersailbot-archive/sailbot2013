package routeplanner.datasource.gpsd;

public class GPSData
{
    private double altitude;
    private double distanceTravelled;
    private SatelliteData [] fixInfo = new SatelliteData[33];
    private float fixPrecision;
    private boolean fixValid;
    private double heading;

    private double lat;
    private double lon;
    private SatelliteData [] oldFixInfo = new SatelliteData[33];
    
    private int satellitesInView;
    private double speed;
    private boolean updatingFix=false;
    //we don't know which will arrive first the GSA or the GSV strings. Need both for a fix.
    private boolean waitingForGSA=true;
    private boolean waitingForGSV=true;
    
    private void copyFixData()
    {
        for (int i=0;i<fixInfo.length;i++)
        {
            oldFixInfo[i]=(SatelliteData) fixInfo[i].clone();
        }

    }
    
    public double getAltitude()
    {
    
        return altitude;
    }
       

    public double getDistanceTravelled()
    {
    
        return distanceTravelled;
    }

    public SatelliteData getFixInfo(int prn)
    {
        //while fixinfo is changing it will be incomplete so return the older copy
       if(updatingFix)
       {
           return oldFixInfo[prn];
       }
        return fixInfo[prn];
    }

    public float getFixPrecision()
    {
    
        return fixPrecision;
    }

    public double getHeading()
    {
    
        return heading;
    }

    public double getLat()
    {
    
        return lat;
    }

    public double getLon()
    {
    
        return lon;
    }

    public int getSatellitesInView()
    {
    
        return satellitesInView;
    }

    public double getSpeed()
    {
    
        return speed;
    }
    
    public boolean getFixValid()
    {
        return fixValid;
    }

    public void incDistanceTravelled(double distanceTravelled)
    {
    
        this.distanceTravelled = this.distanceTravelled + distanceTravelled;
    }

    /**
     * only complete the fix when we've had the full set of GSV strings and the GSA string
     *
     */
    private void isFixDone()
    {
        if(!waitingForGSA&&!waitingForGSV)
        {
            waitingForGSA=true;
            waitingForGSV=true;
            if(updatingFix)
            {
                copyFixData();
                updatingFix = false;
            }
        }
    }

    public boolean isFixValid()
    {
    
        return fixValid;
    }

    public boolean isUpdatingFix()
    {
    
        return updatingFix;
    }

    public void setAltitude(double altitude)
    {
    
        this.altitude = altitude;
    }

    public void setFixInfo(SatelliteData fixInfo,int prn)
    {
        this.fixInfo[prn] = fixInfo;
    }

    public void setFixPrecision(float fixPrecision)
    {
    
        this.fixPrecision = fixPrecision;
    }

    public void setFixValid(boolean fixValid)
    {
    
        this.fixValid = fixValid;
    }

    public void setHeading(double heading)
    {
    
        this.heading = heading;
    }

    public void setLat(double lat)
    {
    
        this.lat = lat;
    }

    public void setLon(double lon)
    {
    
        this.lon = lon;
    }

    public void setSatellitesInView(int satellitesInView)
    {
    
        this.satellitesInView = satellitesInView;
    }

    public void setSpeed(double speed)
    {
    
        this.speed = speed;
    }

    public void setUpdatingFix(boolean updatingFix)
    {
        waitingForGSV=false;
        isFixDone();
    }
    
    public void setUsedInFix(int prn)
    {
        if(fixInfo[prn]==null)
        {
            fixInfo[prn] = new SatelliteData(prn,0,0,0);
        }
        fixInfo[prn].setUsedInFix(true);
    }

    public void setWaitingForGSA(boolean waitingForGSA)
    {    
        this.waitingForGSA = waitingForGSA;
        //see if we're done with the fix
        isFixDone();
    }
    
    public String toString()
    {
        String s = "lat: " + lat + " lon: " + lon + " speed:"  + speed + "km/h altitude: " + altitude + "m heading: " + heading + "\n";
        s = s + "fixValid: " + fixValid + ", " + satellitesInView + " satellites in view" + "distance travelled " + distanceTravelled + "\n";
        
        //if in the process of updating show us the old data
        if(updatingFix)
        {
            for (int i=0;i<oldFixInfo.length;i++)
            {
                if(oldFixInfo[i]!=null)
                {
                    s= s + oldFixInfo[i];
                }
            }           
        }
        else
        {
            for (int i=0;i<fixInfo.length;i++)
            {
                if(fixInfo[i]!=null)
                {
                    s = s + fixInfo[i];
                }
            }
        }
        return s;
    }
    

}