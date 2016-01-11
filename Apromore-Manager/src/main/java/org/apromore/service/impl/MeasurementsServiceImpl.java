package org.apromore.service.impl;

import org.apromore.service.BPMNDiagramImporter;
import org.apromore.service.MeasurementsService;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.springframework.stereotype.Service;

/**
 * Created by Adriano on 08/01/2016.
 */

@Service
public class MeasurementsServiceImpl implements MeasurementsService {
    private BPMNDiagram diagram;
    private BPMNDiagramImporter diagramImporter;
    private JSONObject result;


    public MeasurementsServiceImpl() {
        diagram = null;
        diagramImporter = new BPMNDiagramImporterImpl();
    }

    public MeasurementsServiceImpl(String process) {
        diagramImporter = new BPMNDiagramImporterImpl();
        try {
            diagram = diagramImporter.importBPMNDiagram(process);
        } catch(Exception e) {
            diagram = null;
        }
    }

    public boolean setProcess(String process) {
        try {
            diagram = diagramImporter.importBPMNDiagram(process);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public String computeSimplicity() {
        if( diagram == null ) return null;
        result = new JSONObject();

        try {
            result.put("size", computeSize());
            result.put("CFC", computeCFC());
            result.put("CNC", computeCNC());
            result.put("ACD", computeACD());

            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public String computeSimplicity(String process) {
        result = new JSONObject();

        try {
            diagram = diagramImporter.importBPMNDiagram(process);

            result.put("size", computeSize());
            result.put("density", computeDensity());
            result.put("CFC", computeCFC());
            result.put("CNC", computeCNC());
            result.put("ACD", computeACD());
            result.put("MCD", computeMCD());

            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }

    public int computeSize() {
        int size = 0;
        if(diagram == null) return -1;

        size += diagram.getGateways().size();
        size += diagram.getActivities().size();
        size += diagram.getCallActivities().size();
        size += diagram.getSubProcesses().size();
        size += diagram.getEvents().size();

        return size;
    }

    public int computeCFC() {
        int cfc = 0;
        int outgoingEdges;
        if(diagram == null) return -1;

        for(Gateway g : diagram.getGateways()) {
            if( (outgoingEdges = diagram.getOutEdges(g).size()) > 1 )
                switch( g.getGatewayType() ) {
                    case DATABASED:
                    case EVENTBASED:
                        //case XOR
                        cfc += outgoingEdges;
                        break;
                    case INCLUSIVE:
                    case COMPLEX:
                        //case OR
                        cfc += (Math.pow(2.0, outgoingEdges) - 1);
                        break;
                    case PARALLEL:
                        //case AND
                        cfc += 1;
                        break;
                }
        }

        return cfc;
    }

    public double computeACD() {
        double acd = 0;
        if(diagram == null) return -1;

        for(Gateway g : diagram.getGateways()) acd += (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size());
        acd = acd / (double)diagram.getGateways().size();

        return acd;
    }

    public int computeMCD() {
        int mcd = 0;
        int tmp;
        if(diagram == null) return -1;

        for(Gateway g : diagram.getGateways())
            if( mcd < (tmp = (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size())) ) mcd = tmp;

        return mcd;
    }

    public double computeCNC() {
        int nodes = 0;
        double cnc;
        if(diagram == null) return -1;

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        cnc = (double)diagram.getFlows().size() / (double)nodes;

        return cnc;
    }

    public double computeDensity() {
        return -1;
        /*
        int nodes = 0;
        double density;
        if(diagram == null)

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        for(Event e : diagram.getEvents())
            if((e.getEventType() != Event.EventType.END) && (e.getEventType() != Event.EventType.START)) nodes++;

        density = (double)diagram.getFlows().size() / (double)(diagram.getGateways().size() * nodes);

        return density;
        */
    }
}
