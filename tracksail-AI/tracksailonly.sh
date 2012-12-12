#!/bin/sh

start_tracksail()
{
    killall rmiregistry
    sleep 3
    ./server.sh &
    sleep 10
    ./client.sh 
    sleep 8
}

kill_tracksail()
{
    pid=`ps aux | grep "java -Djava.security.policy=../policy Server" | grep -v "grep" | awk '{print $2}'`
    if [ "$pid" -gt "0" ] ; then
        kill $pid
    fi

}


kill_tracksail
start_tracksail

