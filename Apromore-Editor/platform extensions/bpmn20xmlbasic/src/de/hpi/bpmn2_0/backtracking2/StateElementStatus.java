package de.hpi.bpmn2_0.backtracking2;

public enum StateElementStatus {
    STARTEVENT,
    ACTIVITY_MATCHED, 
    ACTIVITY_SKIPPED, 
    EVENT_SKIPPED, 
    ANDSPLIT, 
    XORSPLIT, 
    ORSPLIT, 
    XORJOIN, 
    ANDJOIN, 
    ORJOIN,
    ENDEVENT
}