#!/bin/sh

DIR=`xpath Apromore-FileStore/src/main/webapp/WEB-INF/web.xml "/web-app/servlet/init-param[param-name='rootpath']/param-value/text()"`

if [ -d "$DIR" ]
then
	echo Installing CMAP and QML files in DAV repository at "$DIR".
	cd Apromore-Extras/bpmncmap/src/test/resources/
	cp 1\ Terminal\ entry.cmap \
	   1\ Terminal\ entry.qml \
	   Airport.qml \
	   International\ departure.cmap \
	   International\ departure.qml \
	   Post_Production_BPMN.cmap \
	   Post_Production.qml \
	   "$DIR"
else
	echo DAV repository "$DIR" does not exist.
fi
