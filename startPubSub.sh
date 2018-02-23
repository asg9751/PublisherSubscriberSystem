#!/bin/sh

LOGFILE="log/PubSub.log"
touch $LOGFILE
echo Starting PubSubSystem..
wait 100
echo Creating images..This may take a few seconds..
./create.sh >> $LOGFILE 2>&1
if [ $? -eq 0 ]
then
	echo Created images!
	wait 100
	echo Starting Docker..
	echo Building network..
	echo Starting DB..
	echo Taking a coffee break..
	echo Ready!
	./run.sh >> $LOGFILE 2>&1
else
	echo Image Creation Failed
fi
