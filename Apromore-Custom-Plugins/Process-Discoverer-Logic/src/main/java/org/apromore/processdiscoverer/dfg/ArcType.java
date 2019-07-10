package org.apromore.processdiscoverer.dfg;

public enum ArcType {
	SS, //StartToStart 
	SC, //StartToComplete
	CS, //CompleteToStart
	CC, //CompleteToComplete
	START, //Outgoing arcs from the start event
	END //Incoming arcs to the end event
}
