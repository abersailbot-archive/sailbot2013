#include <stdint.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>

#ifndef simulator_interface_h
#define simulator_interface_h

#define TRUE 1
#define FALSE 0

int stop_running;

int get_hdg_diff(int heading1,int heading2);
void set_sail(int value);
void set_rudder(int value);
void set_next_wp();
int get_sail();
int get_wind();
int get_compass();
int get_desired_heading();
int get_wp_distance();
int get_wp_num();
double get_easting();
double get_northing();
int get_rudder();
void setup ();
char * write_to_server (char *message);
void init_sockaddr (struct sockaddr_in *name, const char *hostname,uint16_t port);
void cleanup();
void stop_simulator();
void catch_sigpipe (int sig);

#endif
