#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <time.h>
#include <ncurses.h>
#include <stdint.h>
#include "simulator_interface.h"


/*
Example client for tracksail-AI
(C)Copyright Colin Sauze 2005-2009
*/


//how close to the wind do we want to sail when the course isn't directly sailable
#define HOW_CLOSE 45

uint8_t SAILABLE=1,PORT_TACK=0,STBD_TACK=0;
uint8_t DOWNWIND_SAILABLE=1,DOWNWIND_PORT_TACK=0,DOWNWIND_STBD_TACK=0;

//calculates difference between two headings taking wrap around into account
int get_hdg_diff(int heading1,int heading2)
{
    int result;

    result = heading1-heading2;

    if(result<-180)
    {
        result = 360 + result;
        return result;
    } 
    
    if(result>180)
    {
        result = 0 - (360-result);
    }

    return result;
}

/*
works out if we should be tacking or not
returns a new heading that reflects this
uses the #define HOW_CLOSE to decide how many degrees from the wind we should be
45 degrees is usually quoted to most people learning sailing
but many boats (especially wing sailed robots) can sail closer and I suspect tracksail works a bit closer too.
*/
int check_tacking(int relwind,int heading,int desired_heading)
{
        int truewind,tempwpthdg,temptruewind;

        truewind = relwind + heading;       // Calculate true wind direction
        if (truewind > 360) truewind -= 360;
        //handle tacking
        //handle when differenve over 180
        if((abs(truewind-desired_heading))>180)
        {
            if((360-(abs(truewind-desired_heading)))<HOW_CLOSE)
            {
                SAILABLE=0;
            }
            else
            {
                SAILABLE=1;
            }
        }
        //when difference less than 180
        else if (abs(truewind-desired_heading) < HOW_CLOSE)    // Only try to sail to within HOW_CLOSE degrees of the wind
        {
            SAILABLE = 0;
        }
        else
        {
            SAILABLE = 1;
            PORT_TACK = 0;
            STBD_TACK = 0;
        }

        if ((SAILABLE == 0) && (PORT_TACK ==0) && (STBD_TACK == 0)) // If we can't lay the course to the waypoint then...
        {
            temptruewind = truewind;
            tempwpthdg = desired_heading;
            if (desired_heading < HOW_CLOSE)
            {
                tempwpthdg += 180;
                temptruewind += 180;
                if (temptruewind > 360)
                {
                    temptruewind -= 360;
                }
            }
            if (desired_heading > (360 - HOW_CLOSE))
            {
                tempwpthdg -= 180;
                temptruewind -= 180;
                if (temptruewind < 0)
                {
                    temptruewind += 360;
                }
            }
            if (tempwpthdg > temptruewind)
            {
                PORT_TACK = 1;          // Set flag to stop boat "short tacking" to waypoint
            }
            else 
            {
                STBD_TACK = 1;          // Set flag to stop boat "short tacking" to waypoint
            }

        } // otherwise just sail directly to the waypoint

        if (SAILABLE == 0)
        {
             // Keep boat hard on wind on same tack until we can lay course for waypoint (enforce single tack)
            if (PORT_TACK == 1) 
            {
                desired_heading = truewind + HOW_CLOSE; // Sail HOW_CLOSE degrees off the wind on port tack 
            }

            if (STBD_TACK == 1) 
            {
                desired_heading = truewind - HOW_CLOSE; // Sail HOW_CLOSE degrees off the wind on stbd tack 
            }
        }

        if (desired_heading > 359)
        {
            desired_heading -= 360;
        }

        if (desired_heading < 0)
        {
            desired_heading += 360;
        }
	
        return desired_heading;
}


int main(int argc,char **argv)
{
    int wind_dir=0,heading=0,desired_heading=0,heading_error=0,new_sail_pos,new_rudder_pos;
    double pgain=1.7;
    setup();

    while(stop_running!=1)
    {

	heading=get_compass();
        wind_dir=get_wind();

	//the heading to the next waypoint
        desired_heading=get_desired_heading();
	//see if we need to tack
        desired_heading=check_tacking(wind_dir,heading,desired_heading);
	//work out how many degrees difference between our heading and desired heading
        heading_error=get_hdg_diff(heading,desired_heading);
        
	//set a 10 degree wide deadband
        if(abs(heading_error)<5)
        {
            new_rudder_pos=0;
	}
	else
	{
	    //calculate rudder position
	    new_rudder_pos = (int)(heading_error * pgain);
	}

	//limit us between -90 and +90														
	if(new_rudder_pos<-90)
	{
	    new_rudder_pos=-90;
	}
	else if(new_rudder_pos>90)
	{
	    new_rudder_pos=90;
	}
																				
	
	/*translate rudder
        currently -90 to 90 we want 270 to 90
	and it needs to be flipped
        90 = 270
        -90 = 90
        */

        if(new_rudder_pos<0)
        {
            set_rudder(-1*new_rudder_pos);
        }
        else
        {
            set_rudder(360-new_rudder_pos);
        }


	//calculate the sail position	
        if(wind_dir<180)
        {
            if (wind_dir < 70)
                new_sail_pos = 0;
            else if (wind_dir < 80)
                new_sail_pos = 18;
            else if (wind_dir < 90)
                new_sail_pos = 36;
            else if (wind_dir < 110)
                new_sail_pos = 54;
            else
                new_sail_pos = 72;
        }
        else
        {
            if (wind_dir >= 290)
                new_sail_pos = 0;
            else if (wind_dir >= 280)
                new_sail_pos = 342;
            else if (wind_dir >= 270)
                new_sail_pos = 324;
            else if (wind_dir >= 250)
                new_sail_pos = 306;
            else
                new_sail_pos = 288;
        }
	
	set_sail(new_sail_pos);
	
	printf("Heading: %d Wind: %d Heading Error: %d Rudder position: %d Sail position: %d Sailable: %d\n",heading_error,heading,wind_dir,new_rudder_pos,new_sail_pos,SAILABLE);

    }
    return 0;
}
