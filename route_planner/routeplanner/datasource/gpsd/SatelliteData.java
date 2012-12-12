package routeplanner.datasource.gpsd;

public class SatelliteData implements Cloneable
{
    int PRN;
    int elevation;
    int azimuth;
    int snr;
    boolean usedInFix=false;

    public boolean getUsedInFix()
    {
    
        return usedInFix;
    }

    public void setUsedInFix(boolean usedInFix)
    {
    
        this.usedInFix = usedInFix;
    }

    public SatelliteData(int PRN,int elevation,int azimuth,int snr) 
    {
        this.PRN = PRN;
        this.elevation = elevation;
        this.azimuth = azimuth;
        this.snr = snr;
    }
    
    public String toString()
    {
        String s = "Satellite: " + PRN + " elevation: " + elevation + " azimuth: " + azimuth + " SNR: " + snr + " Used in fix: " + usedInFix + "\n";
        return s;
    }

    public int getAzimuth()
    {
    
        return azimuth;
    }

    public void setAzimuth(int azimuth)
    {
    
        this.azimuth = azimuth;
    }

    public int getElevation()
    {
    
        return elevation;
    }

    public void setElevation(int elevation)
    {
    
        this.elevation = elevation;
    }

    public int getPRN()
    {
    
        return PRN;
    }

    public void setPRN(int prn)
    {
    
        PRN = prn;
    }

    public int getSnr()
    {
    
        return snr;
    }

    public void setSnr(int snr)
    {
    
        this.snr = snr;
    }
    
    public Object clone()
    {
        return new SatelliteData(PRN,elevation,azimuth,snr);
    }
}