#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>
#include "simulator_interface.h"

#define MAXMSG 255

#define PORT 5555
#define SERVER "127.0.0.1"

/*Simulator interface code for tracksail-AI
(C)Copyright Colin Sauze 2005-2009
*/

char msgbuf[255];

int isSetup=0;
int sock;

void set_sail(int value)
{
    char *response;
    sprintf(msgbuf,"set sail %d",value);
    //printf("%s ",msgbuf);
    response=write_to_server(msgbuf);
    if(response!=NULL)
    {
        free(response);
    }
    else
    {
        stop_running=1;
    }
}

void set_rudder(int value)
{
    char *response;
    //fix invalid values
    if(value>90&&value<180)
    {
        value=90;
    }
    else if(value<270&&value>179)
    {
        value=270;
    }
    sprintf(msgbuf,"set rudder %d",value);
    //printf("%s ",msgbuf);
    response=write_to_server(msgbuf);
    if(response!=NULL)
    {
        free(response);
    }
    else
    {
        stop_running=1;
    }
}

void set_next_wp()
{
    char *response;

    sprintf(msgbuf,"set waypoint 0");
    //printf("%s ",msgbuf);
    response=write_to_server(msgbuf);
    if(response!=NULL)
    {
        free(response);
    }
    else
    {
        stop_running=1;
    }
}


int get_sail()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get sail");
    response = write_to_server(msgbuf);
    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

int get_wind()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get wind_dir");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

int get_compass()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get compass");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

int get_desired_heading()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get waypointdir");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

int get_wp_distance()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get waypointdist");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

int get_wp_num()
{
    char *response;
    int return_data;
    sprintf(msgbuf,"get waypointnum");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0;
    }
}

double get_easting()
{
    char *response;
    double return_data;
    sprintf(msgbuf,"get easting");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atof(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0.0;
    }
}


double get_northing()
{
    char *response;
    double return_data;
    sprintf(msgbuf,"get northing");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atof(response);
        free(response);
        return return_data;
    }
    else
    {
        stop_running=1;
        return 0.0;
    }
}

int get_rudder()
{
    char *response;
    int return_data;   
    sprintf(msgbuf,"get rudder");
    response = write_to_server(msgbuf);

    if(response!=NULL)
    {
        return_data = atoi(response);
        free(response);  
        return return_data;   
    }
    else
    {
        stop_running=1;
        return 0.0;
    }
}

void setup ()
{
     int sockfd;
     struct sockaddr_in serv_addr;
     stop_running=0;

     //set the sigpipe handler so we don't exit upon a sigpipe
     //this happens when we complete the course in tracksail and the tcp connection to it goes dead
     signal (SIGPIPE, catch_sigpipe);

     sockfd = socket(AF_INET, SOCK_STREAM, 0);

     if (sockfd < 0)
     {
        perror("ERROR opening socket");
     }


     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = inet_addr(SERVER);
     serv_addr.sin_port = htons(PORT);

     // extern void init_sockaddr (struct sockaddr_in *name, const char *hostname, uint16_t port);

    struct sockaddr_in servername;

    /* Create the socket. */
    sock = socket (PF_INET, SOCK_STREAM, 0);
    if (sock < 0)
    {
        perror ("socket (client)");
        exit (EXIT_FAILURE);
    }

    /* Connect to the server. */
    init_sockaddr (&servername, SERVER, PORT);
    if (0 > connect (sock, (struct sockaddr *) &servername,sizeof (servername)))
    {
        perror ("connect (client)");
        exit (EXIT_FAILURE);
    }

    isSetup=TRUE;
}

char * write_to_server (char *message)
{
    int nbytes;
    char *response;
    response = (char*) malloc(MAXMSG);
    if(!isSetup)
    {
        setup();
    }
    //printf("client sending %s\n",message);
    /*send request to server*/
    nbytes = write (sock, message, strlen (message) + 1);
    if (nbytes < 0)
    {
        perror ("write");
        //exit (EXIT_FAILURE);
        return 0;
    }
    /*read response*/
    nbytes = recv(sock, response, MAXMSG,0);
    //printf("client got %s\n",response);
    return response;

}

void init_sockaddr (struct sockaddr_in *name, const char *hostname,uint16_t port)
{
    struct hostent *hostinfo;

    name->sin_family = AF_INET;
    name->sin_port = htons (port);
    hostinfo = gethostbyname (hostname);
    if (hostinfo == NULL)
    {
        fprintf (stderr, "Unknown host %s.\n", hostname);
        exit (EXIT_FAILURE);
    }
    name->sin_addr = *(struct in_addr *) hostinfo->h_addr;
}

void cleanup()
{
    close (sock);
}

void stop_simulator()
{
    char *response;
    sprintf(msgbuf,"set unwind");
    response=write_to_server(msgbuf);
    if(response!=NULL)
    {
        free(response);
        stop_running=1;
    }
}

//signal handler for SIGPIPE, stops the program exiting upon a sig pipe
void catch_sigpipe (int sig)
{
    fprintf(stderr,"in sig handler, server disconnected");
    signal (sig, catch_sigpipe);
    stop_running=1;
}
