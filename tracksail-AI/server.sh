#!/bin/sh
cd bin
killall rmiregistry
rmiregistry&
java -Djava.security.policy=../policy Server
