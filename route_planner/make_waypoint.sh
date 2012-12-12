#!/bin/sh

if [ "$#" != "2" ] ; then
    echo "usage:"
    echo "make_waypoint.sh inputfile outputfile"
    exit 1
fi

name=`echo $1 | awk -F. '{print $1}'`

echo "type=\"track\" name=\"$name\"" > $2

cat $1 | awk -F, '{print "type=\"trackpoint\" altitude=\"0\" latitude=\""$1"\" longitude=\""$2"\" unixtime=\"0\"" }' >> $2

echo "type=\"trackend\"" >> $2