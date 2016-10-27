package au.edu.qut.processmining.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adriano on 27/10/2016.
 */
public class SimpleLog {
    private Map<String, Integer> traces;
    private Map<Integer, String> events;
    private int size;

    private int startcode;
    private int endcode;

    public SimpleLog(Map<String, Integer> traces, Map<Integer, String> events) {
        this.traces = traces;
        this.events = events;
        this.size = 0;

        for( int traceFrequency : traces.values() ) this.size += traceFrequency;
    }

    public Map<String, Integer> getTraces() { return traces; }
    public Map<Integer, String> getEvents() { return events; }
    public int size() { return size; }

    public void setStartcode(int startcode){ this.startcode = startcode; }
    public int getStartcode(){ return startcode; }

    public void setEndcode(int endcode){ this.endcode = endcode; }
    public int getEndcode(){ return endcode; }
}
