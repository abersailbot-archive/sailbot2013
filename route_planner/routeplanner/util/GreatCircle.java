package routeplanner.util;

public class GreatCircle
{
	/**
    gets the distance between lat1,lon1 and lat2,lon2 in km
    @param lat1 - the start latitude (in radians)
    @param lon1 - the start longitude (in radians)
    @param lat2 - the end latitude (in radians)
    @param lon2 - the start longitude (in radians)
    @return the distance in km between the 2 points
	*/
	public static double getDistance(double lat1,double lon1,double lat2,double lon2 )
	{
		double theta = lon2-lon1;
		double distance = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(theta));
		if(distance<0) 
		{
			distance=distance + Math.PI;
		}
		//halfway between equatorial radius (6378km) and polar radius(6357km)
		distance = distance * 6367.0;
		return distance;
	}
	
	/**
	calculates the great circle heading between one point and another
	Does not work if one latitude is polar!!!
    @param lat1 - the start latitude (in radians)
    @param lon1 - the start longitude (in radians)
    @param lat2 - the end latitude (in radians)
    @param lon2 - the start longitude (in radians)
    @return the heading in degrees between the 2 points
	*/
	public static double getCourse(double lat1,double lon1,double lat2,double lon2 )
	{
		double C;
		double L = lon2 - lon1;
		
		double cosD = Math.sin( lat1 )*Math.sin( lat2 ) + Math.cos( lat1 )*Math.cos( lat2 )*Math.cos( L );
		
		double D = Math.acos( cosD );
		
		double cosC = ( Math.sin( lat2 ) - cosD*Math.sin( lat1 ) ) / ( Math.sin( D )*Math.cos( lat1 ) );
		
		// numerical error can result in |cosC| slightly > 1.0 
		if( cosC > 1.0 )
		{
			cosC = 1.0;
		}
		if( cosC < -1.0 )
		{
			cosC = -1.0;
		}
		
		C = 180.0*Math.acos( cosC )/Math.PI;
		
		if( Math.sin( L ) < 0.0 )
		{
			C = 360.0 - C;
		}
		
		return ( 100*C )/100.0;
	}
	
	/**
	converts latitude from ddmm.mm format to dd.dd format co-ordinates and into radians
	@param deg is the degreess
	@param min is the minutes
	@param sgn is the sign (N or S)
	*/
	public static double latitude(double deg, double min,char sgn )
	{
		double lat = 0.0;
		
		if( 0.0 <= min && min < 60.0 )
		{
			lat += ( min / 60.0 );
		}
		/*else
		{
			error( "minutes outside allowed 0-60 range!\n" );
		}*/
		
		if( 0.0 <= deg && deg <= 90.0 )
		{
			lat += ( deg );
		}
		/*else
		{
			error( "degrees outside allowed 0-90 range!\n" );
		}*/
	
		
		if( 0.0 <= lat && lat <= 90.0 )
		{
			lat = Math.PI * lat / 180.0;
		}
		/*else
		{
			error( "latitude range error!\n" );
		}*/
		
		if( sgn == 'S' )
		{
			lat = -lat;
		}
		
		return lat;
	}
	
	/**
	converts longditude from ddmm.mm format to dd.dd format co-ordinates and into radians
    @param deg is the degreess
	@param min is the minutes
	@param sgn is the sign (E or W)
	*/
	public static double longitude(double deg,double min,char sgn )
	{
		double lon = 0.0;
		
		if( 0.0 <= min && min < 60.0 )
		{
			lon += ( min / 60.0 );
		}
		/*else
		{
			error( "check longitude minutes!\n" );
		}*/
		
		if( 0.0 <= deg && deg <= 180.0 )
		{
			lon += ( deg );
		}
		/*else
		{
			error( "check longitude degrees!\n" );
		}*/
		
		if( 0.0 <= lon && lon <= 180.0 )
		{
			lon = Math.PI * lon / 180.0;
		}
		/*else
		{
			error( "longitude range error!\n" );
		}*/
		
		if( sgn == 'W' )
		{
			lon = -lon;
		}
		
		return lon;
	}
}