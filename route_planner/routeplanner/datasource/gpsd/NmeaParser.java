package routeplanner.datasource.gpsd;

import routeplanner.util.GreatCircle;

public class NmeaParser 
{
    GPSData currentState;
    double previousLat,previousLon;
    
    public NmeaParser()
    {
        currentState = new GPSData();
    }
    
    /**
    Processes an NMEA scentence 
    */
    public void processScentence(String scentence) throws InvalidFormatException
    {
        //string should start with a $
        if(scentence.charAt(0)=='$')
        {
            //then contain GP
            if(scentence.substring(1,3).equals("GP"))
            {
                //then a 3 letter string
                String type=scentence.substring(3,6);

                if(type.equals("VTG"))
                {
                    updateVelocity(scentence);
                }
                if(type.equals("GGA"))
                {
                    updatePosition(scentence);
                }
                if(type.equals("GSV"))
                {
                    updateFix(scentence);
                }
                if(type.equals("GSA"))
                {
                    updateSatellites(scentence);
                }
                //just ignore unrecognised scentences, we do expect to see them
            }
            else
            {
                System.err.println("No GP in string" + scentence.substring(1,3));
            }
        }
        else
        {
            System.err.println("doesn't start with a $ " + scentence.charAt(0));
        }
    }
    
    /**
    called by processScentence when it encounters a GPVTG string
    */
    private void updateVelocity(String scentence)
    {
        /*
        eg1. $GPVTG,360.0,T,348.7,M,000.0,N,000.0,K*43
        eg2. $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*41

           054.7,T      True course made good over ground, degrees
           034.4,M      Magnetic course made good over ground, degrees
           005.5,N      Ground speed, N=Knots
           010.2,K      Ground speed, K=Kilometers per hour
        */
        GPSTokenizer gt = new GPSTokenizer(scentence);
        gt.getNextToken(); //grab GPVTG
        String course = gt.getNextToken();
        if(course!=null)
        {
            currentState.setHeading(Double.parseDouble(course));
        }
        //grab T
        gt.getNextToken();
        //grab magnetic course
        gt.getNextToken();
        gt.getNextToken();
        //grab sped in knots
        gt.getNextToken();
        gt.getNextToken();
        //speed in km/h
        String speed = gt.getNextToken();
        if(speed!=null)
        {
            currentState.setSpeed(Double.parseDouble(speed));
        }
    }

    /**
    called by processScentence when it encounters a GPGGA string
    */
    private void updatePosition(String scentence)
    {
        /*$GPGGA,hhmmss.ss,llll.ll,a,yyyyy.yy,a,x,xx,x.x,x.x,M,x.x,M,x.x,xxxx*hh
        GGA  = Global Positioning System Fix Data
        1    = UTC of Position
        2    = Latitude
        3    = N or S
        4    = Longitude
        5    = E or W
        6    = GPS quality indicator (0=invalid; 1=GPS fix; 2=Diff. GPS fix)
        7    = Number of satellites in use [not those in view]
        8    = Horizontal dilution of position
        9    = Antenna altitude above/below mean sea level (geoid)
        10   = Meters  (Antenna height unit)
        11   = Geoidal separation (Diff. between WGS-84 earth ellipsoid and
            mean sea level.  -=geoid is below WGS-84 ellipsoid)
        12   = Meters  (Units of geoidal separation)
        13   = Age in seconds since last update from diff. reference station
        14   = Diff. reference station ID#
        15   = Checksum
        
        using comma separation (counting from 0) we want fields 2,3,4,5 and 10
    
        */
        previousLat = currentState.getLat();
        previousLon = currentState.getLon();
        
        GPSTokenizer gt = new GPSTokenizer(scentence);
        gt.getNextToken(); //grab the GPGGA
        gt.getNextToken(); //grab the time
        String lat = gt.getNextToken();
        String northSouth = gt.getNextToken();
        String lon = gt.getNextToken();
        String eastWest = gt.getNextToken();
        String fixValid=gt.getNextToken(); //grab the quality
        String numOfSatellites=gt.getNextToken(); //grab the number of satellites
        gt.getNextToken(); //horizontal position
        String altitude=gt.getNextToken(); //altitude
        
        //process fix validity
        if(fixValid.equals("0"))
        {
            currentState.setFixValid(false);
            return;
        }
        else
        {
            currentState.setFixValid(true);
        }


        //convert lat/lon to decimal degrees
        String degrees = lat.substring(0,2);
        //System.out.println("degrees = " + degrees);
        String minutes = lat.substring(2,9);
        //System.out.println("minutes = " + minutes);
        currentState.setLat(GreatCircle.latitude(Double.parseDouble(degrees),Double.parseDouble(minutes),northSouth.charAt(0)));
        
        degrees = lon.substring(0,3);
        //System.out.println("degrees = " + degrees);
        minutes = lon.substring(3,10);
        //System.out.println("minutes = " + minutes);
        currentState.setLon(GreatCircle.longitude(Double.parseDouble(degrees),Double.parseDouble(minutes),eastWest.charAt(0)));
        
        if(currentState.getFixValid()&&previousLat!=0&&previousLon!=0)
        {
            //measure distance to previous point
            double distance = GreatCircle.getDistance(previousLat, previousLon, currentState.getLat(), currentState.getLon());
            currentState.incDistanceTravelled(distance);
            //System.out.println(distance);
        }
        
        //process altitude
        currentState.setAltitude( Double.parseDouble(altitude));

        //process fix info
        currentState.setSatellitesInView( Integer.parseInt(numOfSatellites));
        
    }

    /**
    called by processScentence when it encounters a GPGSV string
    */
    private void updateFix(String scentence)
    {
        GPSTokenizer gt = new GPSTokenizer(scentence);
        gt.getNextToken(); //grab the GPGSV
        int numOfMsgs = Integer.parseInt(gt.getNextToken());
        int msgNum = Integer.parseInt(gt.getNextToken()); //message number

        if(msgNum==1)
        {
            //flag that we are updating so other processes delay reads
            currentState.setUpdatingFix(true);
        }
        
        gt.getNextToken(); //number of satellites in view

        for(int i=0;i<4;i++)
        {
            int prn=0,elevation=0,azimuth=0,snr=0;
            try
            {
                prn = Integer.parseInt(gt.getNextToken());
                elevation = Integer.parseInt(gt.getNextToken());
                azimuth = Integer.parseInt(gt.getNextToken());
                snr = Integer.parseInt(gt.getNextToken());
            }
            catch(NumberFormatException e)
            {
            }
            
            currentState.setFixInfo(new SatelliteData(prn,elevation,azimuth,snr),prn);
        }
        if(msgNum==numOfMsgs)
        {
            currentState.setUpdatingFix(false);
        }
    }
    /**
     * called when we get a GPGSA string which details which satellites make up our fix
     */
    private void updateSatellites(String scentence)
    {
        GPSTokenizer gt = new GPSTokenizer(scentence);
        gt.getNextToken(); //grab the GPGSA
        gt.getNextToken(); //fix type automatic or manual
        gt.getNextToken(); //fix quality 2d or 3d
        for(int i=0;i<12;i++)
        {
            String prn = gt.getNextToken();
            if(prn!=null)
            {
                currentState.setUsedInFix(Integer.parseInt(prn));
            }
        }
        currentState.setWaitingForGSA(false);
    }

    /**
    gets the current state information
    */
    public GPSData getCurrentState()
    {
        return currentState;
    }

}