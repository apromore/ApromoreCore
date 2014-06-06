#!/bin/sh

DIR=`xpath Apromore-FileStore/src/main/webapp/WEB-INF/web.xml "/web-app/servlet/init-param[param-name='rootpath']/param-value/text()"`

if [ -d "$DIR" ]
then
	echo Installing CMAP and QML files in DAV repository at "$DIR".
	cd Apromore-Extras/bpmncmap/src/test/resources/
	cp Airport.qml \
	   International\ departure.cmap \
	   International\ departure.qml \
	   1\ Terminal\ entry.cmap \
	   1\ Terminal\ entry.qml \
	   2\ Check-in.cmap \
	   2\ Check-in.qml \
	   2x\ Finalise\ Check-in.cmap \
	   2x\ Finalise\ Check-in.qml \
	   2x\ Perform\ preliminary\ check\ in.cmap \
	   2x\ Perform\ preliminary\ check\ in.qml \
	   3\ Security.cmap \
	   3\ Security.qml \
	   3x\ Undergo\ Secondary\ screening.cmap \
	   3x\ Undergo\ Secondary\ screening.qml \
	   3x\ Undergo\ preparation.cmap \
	   3x\ Undergo\ preparation.qml \
	   3x\ Undergo\ security\ checks.cmap \
	   3x\ Undergo\ security\ checks.qml \
	   4x\ Undergo\ customs\ and\ immigration\ checks.cmap \
	   4x\ Undergo\ customs\ and\ immigration\ checks.qml \
	   5\ Boarding.cmap \
	   5\ Boarding.qml \
	   91\ Discretionary\ experience\ 1.cmap \
	   91\ Discretionary\ experience\ 1.qml \
	   92\ Discretionary\ experience\ 2.cmap \
	   92\ Discretionary\ experience\ 2.qml \
	   93\ Discretionary\ experience\ 3.cmap \
	   93\ Discretionary\ experience\ 3.qml \
	   94\ Discretionary\ experience\ 4.cmap \
	   94\ Discretionary\ experience\ 4.qml \
	   Post_Production_BPMN.cmap \
	   Post_Production.qml \
	   "$DIR"
else
	echo DAV repository "$DIR" does not exist.
fi
