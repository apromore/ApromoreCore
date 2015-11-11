package org.apromore.service.impl;

import org.apromore.service.BPMNDiagramImporter;
import org.processmining.models.graphbased.directed.bpmn.*;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import org.omg.spec.bpmn._20100524.model.*;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringReader;
import java.util.*;

import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.bpmn.BpmnAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Adriano on 29/10/2015.
 */
public class BPMNDiagramImporterImpl implements BPMNDiagramImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNDiagramImporterImpl.class);

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;

    private BPMNDiagram diagram;

    private Map<String, Swimlane> idToPool;
    private Map<String, BPMNNode> idToNode;
    private Map<Event, String> boundToFix;

    private Set<TDataAssociation> dataAssociations;
    private Set<TAssociation> associations;
    private Set<TSequenceFlow> flows;
    private Set<TMessageFlow> messageFlows;
    private List<JAXBElement<? extends TRootElement>> processes;

    public BPMNDiagramImporterImpl() {}

    public BPMNDiagram importBPMNDiagram(String xmlProcess) {

        ClassLoader classLoader = getClass().getClassLoader();
        TDefinitions definitions;

        diagram = new BPMNDiagramImpl("");
        idToPool = new HashMap<>();
        idToNode = new HashMap<>();
        boundToFix = new HashMap<>();
        dataAssociations = new HashSet<>();
        associations = new HashSet<>();
        flows = new HashSet();
        messageFlows = new HashSet<>();
        processes = new LinkedList<>();

        try {

			/* Creating the JAXBContext from the xsd file */
            //LOGGER.info("importBPMNDiagram: Creating JAXBcontext...");
            jaxbContext = JAXBContext.newInstance( "org.omg.spec.bpmn._20100524.model" );
            //LOGGER.info("Created JAXBcontext!");

            //LOGGER.info("importBPMNDiagram: Creating Unmarshaller...");
            unmarshaller = jaxbContext.createUnmarshaller();
            //LOGGER.info("Created Unmarshaller");

            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

            File schemaFile = new File(classLoader.getResource("./xsd/BPMN20.xsd").getFile());
            Schema schema = sf.newSchema(schemaFile);
            unmarshaller.setSchema(schema);

            //LOGGER.info("importBPMNDiagram: Unmarshalling...");
            Object o = unmarshaller.unmarshal(new StringReader(xmlProcess));
            //LOGGER.info("importBPMNDiagram: Unmarshalling... DONE!");

            if( o instanceof JAXBElement ) o = ((JAXBElement) o).getValue();
            if( !(o instanceof TDefinitions) ) throw new Exception("TDefinition element NOT found!");

            LOGGER.info("Definitions element got!");
            definitions = (TDefinitions) o;

            for( JAXBElement<? extends TRootElement> rootElement : definitions.getRootElement() ) {

                if( rootElement.getValue() instanceof TProcess ) {
                    LOGGER.info("TProcess found: " + rootElement.getValue().getId());
                    processes.add(rootElement);
                } else if( rootElement.getValue() instanceof TCallableElement ) {
                    LOGGER.info("TCallableElement found: " + rootElement.getValue().getId() );
                    //TODO nothing
                } else if( rootElement.getValue() instanceof TCollaboration ) {
                    LOGGER.info("TCollaboration found: " + rootElement.getValue().getId());
                    handleCollaboration((TCollaboration) rootElement.getValue());
                }
            }

            for( JAXBElement<? extends TRootElement> process : processes ) {
                if (process.getValue() instanceof TProcess) unfoldProcess(process.getValue(), null);
            }

            buildEdges();
            fixBounds();

            LOGGER.info("Parsing DONE!");
            return diagram;

        } catch( UnmarshalException ue ) {
            LOGGER.error("importBPMNDiagram: unable to unmarshall the xml file.", ue);
        } catch( JAXBException je ) {
            LOGGER.error("importBPMNDiagram: unable to create the JAXBContext.", je);
        } catch(Exception e) {
            LOGGER.error("importBPMNDiagram: error creating the importBPMNDiagram.", e);
        }

        return null;
    }

    private void handleCollaboration(TCollaboration collaboration) {

        for( TParticipant participant : collaboration.getParticipant() ) {
            Swimlane s;
            String procRef;

            if( participant.getProcessRef() != null ) {
                LOGGER.info("Found useful participant[POOL]: " + participant.getName() + "[" + participant.getId() + "]");
                procRef = participant.getProcessRef().getLocalPart();
                s = diagram.addSwimlane(participant.getName(), null, SwimlaneType.POOL);
                idToPool.put(procRef, s);
            }
        }

        for( TMessageFlow messageFlow : collaboration.getMessageFlow() )
            messageFlows.add(messageFlow);
    }


    /**** adding method for BPMNEdge objects ****/
    private void buildEdges() {
        String src;
        String tgt;
        String direction;

        for( TSequenceFlow flow : flows ) {
            src = flow.getSourceRef().getId();
            tgt = flow.getTargetRef().getId();
            if( idToNode.containsKey(src) && idToNode.containsKey(tgt) ) {
                diagram.addFlow(idToNode.get(src), idToNode.get(tgt), flow.getName());
                LOGGER.info("Adding Flow: " + src + " > " + tgt);
            } else LOGGER.info("Unfixable Flow: " + src + " > " + tgt);
        }

        for( TAssociation association : associations ) {
            src = association.getSourceRef().getLocalPart();
            tgt = association.getTargetRef().getLocalPart();
            if( idToNode.containsKey(src) && idToNode.containsKey(tgt) ) {
                    direction = association.getAssociationDirection().value();
                    diagram.addAssociation(idToNode.get(src), idToNode.get(tgt), BpmnAssociation.AssociationDirection.valueOf(direction));
                    LOGGER.info("Adding Association: " + src + " > " + tgt);
            } else LOGGER.info("Unfixable Association: " + src + " > " + tgt);
        }

        for( TDataAssociation dataAssociation: dataAssociations ) {
            tgt = dataAssociation.getTargetRef().getId();

            for( JAXBElement je : dataAssociation.getSourceRef() )
                if( je.getValue() instanceof TBaseElement ) {
                    src = ((TBaseElement) je.getValue()).getId();
                    if( idToNode.containsKey(src) && idToNode.containsKey(tgt) ) {
                        diagram.addDataAssociation(idToNode.get(src), idToNode.get(tgt), "");
                        LOGGER.info("Adding DataAssociation: " + src + " > " + tgt);
                    } else { LOGGER.info("Unfixable DataAssociation: " + src + " > " + tgt); }
                }
        }

        for( TMessageFlow messageFlow : messageFlows ) {
            src = messageFlow.getSourceRef().getLocalPart();
            tgt = messageFlow.getTargetRef().getLocalPart();
            if( idToNode.containsKey(src) && idToNode.containsKey(tgt) ) {
                diagram.addMessageFlow(idToNode.get(src), idToNode.get(tgt), "");
                LOGGER.info("Adding MessageFlow: " + src + " > " + tgt);
            } else LOGGER.info("Unfixable MessageFlow: " + src + " > " + tgt);
        }

    }

    private void fixBounds() {
        for( Event e : boundToFix.keySet() )
            if( idToNode.get(boundToFix.get(e)) instanceof Activity ) {
                e.setExceptionFor((Activity) idToNode.get(boundToFix.get(e)));
                ((Activity) idToNode.get(boundToFix.get(e))).incNumOfBoundaryEvents();
            } else LOGGER.info("Unfixable boundaryEvent: " + e.getLabel() + " > " + boundToFix.get(e));
    }


    /**** adding methods for BPMNNode objects ****/
    private void addDataObject(TDataObject dataObject, SubProcess parentProcess, Swimlane pool) {
        LOGGER.info("Adding dataObject: " + dataObject.getName() + "(" + dataObject.getId() + ")");
        DataObject d = diagram.addDataObject(dataObject.getName());
        d.setParentSubprocess(parentProcess);
        d.setParentSwimlane(pool);
        idToNode.put(dataObject.getId(), d);
    }

    private void addTextAnnotation(TTextAnnotation textAnnotation, SubProcess parentProcess, Swimlane pool) {
        String text = textAnnotation.getText().getContent().toString();
        LOGGER.info("Adding textAnnotation: " + text + ":" + textAnnotation.getId());
        TextAnnotation ta = diagram.addTextAnnotation(text);
        ta.setParentSubprocess(parentProcess);
        ta.setParentSwimlane(pool);
        idToNode.put(textAnnotation.getId(), ta);
    }

    private void addEvent(TEvent event, SubProcess parentProcess, Swimlane pool) {
        Event e;
        String label = event.getName();
        LOGGER.info("Adding Event: " + label + ":" + event.getId());

        Event.EventUse use = Event.EventUse.CATCH;
        Event.EventType type = Event.EventType.INTERMEDIATE;
        Event.EventTrigger trigger = Event.EventTrigger.NONE;
        Activity activity = null;
        String activityID = null;
        boolean toFix = false;
        boolean isInterrupting = true;

        if( event instanceof TCatchEvent ) {
            use = Event.EventUse.CATCH;
            if( event instanceof TStartEvent ) type = Event.EventType.START;
            else if( event instanceof TIntermediateCatchEvent ) type = Event.EventType.INTERMEDIATE;
            else if( event instanceof TBoundaryEvent ) {
                type = Event.EventType.INTERMEDIATE;
                toFix = true;
                activityID = ((TBoundaryEvent) event).getAttachedToRef().getLocalPart();
                LOGGER.info("Boundary event for > " + activityID);
            }

            if( ((TCatchEvent)event).getEventDefinition().size() > 1 ) trigger = Event.EventTrigger.MULTIPLE;
            else if( (((TCatchEvent)event).getEventDefinition().size() != 0) && (((TCatchEvent)event).getEventDefinition().get(0) != null) ) {
                TEventDefinition definition = ((TCatchEvent)event).getEventDefinition().get(0).getValue();
                if( definition instanceof TTimerEventDefinition ) trigger = Event.EventTrigger.TIMER;
                else if( definition instanceof TCancelEventDefinition ) trigger = Event.EventTrigger.CANCEL;
                else if( definition instanceof TTerminateEventDefinition ) trigger = Event.EventTrigger.TERMINATE;
                else if( definition instanceof TConditionalEventDefinition ) trigger = Event.EventTrigger.CONDITIONAL;
                else if( definition instanceof TCompensateEventDefinition ) trigger = Event.EventTrigger.COMPENSATION;
                else if( definition instanceof TErrorEventDefinition ) trigger = Event.EventTrigger.ERROR;
                else if( definition instanceof TLinkEventDefinition ) trigger = Event.EventTrigger.LINK;
                else if( definition instanceof TMessageEventDefinition ) trigger = Event.EventTrigger.MESSAGE;
                else if( definition instanceof TSignalEventDefinition ) trigger = Event.EventTrigger.SIGNAL;
                else if( definition instanceof TEscalationEventDefinition ) trigger = Event.EventTrigger.NONE; //missing escalation trigger in BPMNDiagram!
            }

            for( TDataAssociation dataAssociation : ((TCatchEvent) event).getDataOutputAssociation() )
                dataAssociations.add(dataAssociation);

        } else if( event instanceof TThrowEvent ) {
            use = Event.EventUse.THROW;
            if( event instanceof TEndEvent ) type = Event.EventType.END;
            else if( event instanceof TIntermediateThrowEvent ) type = Event.EventType.INTERMEDIATE;
            else if( event instanceof TImplicitThrowEvent ) type = Event.EventType.INTERMEDIATE;

            if( ((TThrowEvent)event).getEventDefinition().size() > 1 ) trigger = Event.EventTrigger.MULTIPLE;
            else if( (((TThrowEvent)event).getEventDefinition().size() != 0) && (((TThrowEvent)event).getEventDefinition().get(0)) != null ) {
                TEventDefinition definition = ((TThrowEvent)event).getEventDefinition().get(0).getValue();
                if( definition instanceof TTimerEventDefinition ) trigger = Event.EventTrigger.TIMER;
                else if( definition instanceof TCancelEventDefinition ) trigger = Event.EventTrigger.CANCEL;
                else if( definition instanceof TTerminateEventDefinition ) trigger = Event.EventTrigger.TERMINATE;
                else if( definition instanceof TConditionalEventDefinition ) trigger = Event.EventTrigger.CONDITIONAL;
                else if( definition instanceof TCompensateEventDefinition ) trigger = Event.EventTrigger.COMPENSATION;
                else if( definition instanceof TErrorEventDefinition ) trigger = Event.EventTrigger.ERROR;
                else if( definition instanceof TLinkEventDefinition ) trigger = Event.EventTrigger.LINK;
                else if( definition instanceof TMessageEventDefinition ) trigger = Event.EventTrigger.MESSAGE;
                else if( definition instanceof TSignalEventDefinition ) trigger = Event.EventTrigger.SIGNAL;
                else if( definition instanceof TEscalationEventDefinition ) trigger = Event.EventTrigger.NONE; //missing escalation trigger in BPMNDiagram!
            }

            for( TDataAssociation dataAssociation : ((TThrowEvent) event).getDataInputAssociation() )
                dataAssociations.add(dataAssociation);
        }

        e = diagram.addEvent(label, type, trigger, use, parentProcess, isInterrupting, activity);
        e.setParentSwimlane(pool);
        if( toFix ) boundToFix.put(e, activityID);
        idToNode.put(event.getId(), e);
    }

    private void addTask(TTask task, SubProcess parentProcess, Swimlane pool) {
        Activity a;
        String label = task.getName();
        LOGGER.info("Adding Activity: " + label + ":" + task.getId());

        boolean isBCompensation = task.isIsForCompensation();
        boolean isBMultiinstance = false;
        boolean isBLooped = false;

        /* not found within the schema: always false */
        boolean isBAdhoc = false;
        boolean isBCollapsed = false;

        JAXBElement loopCharacteristics = task.getLoopCharacteristics();

        if( loopCharacteristics != null ) {
            isBLooped = true;
            if( loopCharacteristics.getValue() instanceof TMultiInstanceLoopCharacteristics ) isBMultiinstance = true;
        }

        for( TDataAssociation dataAssociation : task.getDataOutputAssociation() )
            dataAssociations.add(dataAssociation);

        for( TDataAssociation dataAssociation : task.getDataInputAssociation() )
            dataAssociations.add(dataAssociation);

        a = diagram.addActivity(label, isBLooped, isBAdhoc, isBCompensation, isBMultiinstance, isBCollapsed, parentProcess);
        a.setParentSwimlane(pool);
        idToNode.put(task.getId(), a);
    }

    private void addGateway(TGateway gateway, SubProcess parentProcess, Swimlane pool) {
        Gateway g;
        String label = gateway.getName();
        LOGGER.info("Adding gateway: " + label + ":" + gateway.getId());
        Gateway.GatewayType type = Gateway.GatewayType.DATABASED;

        if(gateway instanceof TComplexGateway) type = Gateway.GatewayType.COMPLEX;
        else if(gateway instanceof TParallelGateway) type = Gateway.GatewayType.PARALLEL;
        else if(gateway instanceof TEventBasedGateway) type = Gateway.GatewayType.EVENTBASED;
        else if(gateway instanceof TExclusiveGateway)  type = Gateway.GatewayType.DATABASED;
        else if(gateway instanceof TInclusiveGateway)  type = Gateway.GatewayType.INCLUSIVE;

        g = diagram.addGateway(label, type, parentProcess);
        g.setParentSwimlane(pool);
        idToNode.put(gateway.getId(), g);
    }

    private void addCallActivity(TCallActivity callActivity, SubProcess parentProcess, Swimlane pool) {
        CallActivity ca;
        String label = callActivity.getName();
        LOGGER.info("Adding CallActivity: " + label + ":" + callActivity.getId());

        boolean isBCompensation = callActivity.isIsForCompensation();
        boolean isBMultiinstance = false;
        boolean isBLooped = false;

        /* not found within the schema: always false */
        boolean isBAdhoc = false;
        boolean isBCollapsed = false;

        JAXBElement loopCharacteristics = callActivity.getLoopCharacteristics();

        if( loopCharacteristics != null ) {
            isBLooped = true;
            if (loopCharacteristics.getValue() instanceof TMultiInstanceLoopCharacteristics) isBMultiinstance = true;
        }

        for( TDataAssociation dataAssociation : callActivity.getDataOutputAssociation() )
            dataAssociations.add(dataAssociation);

        for( TDataAssociation dataAssociation : callActivity.getDataInputAssociation() )
            dataAssociations.add(dataAssociation);

        ca = diagram.addCallActivity(label, isBLooped, isBAdhoc, isBCompensation, isBMultiinstance, isBCollapsed, parentProcess);
        ca.setParentSwimlane(pool);
        idToNode.put(callActivity.getId(), ca);

    }

    private void addSubProcess(TSubProcess subProcess, SubProcess parentProcess, Swimlane pool) {
        SubProcess sp;
        String label = subProcess.getName();
        LOGGER.info("Adding SubProcess: " + label + ":" + subProcess.getId());

        boolean isBCompensation = subProcess.isIsForCompensation();
        boolean isTriggeredByEvent = subProcess.isTriggeredByEvent();
        boolean isBMultiinstance = false;
        boolean isBLooped = false;

        /* not found within the schema: always false */
        boolean isBAdhoc = false;
        boolean isBCollapsed = false;

        JAXBElement loopCharacteristics = subProcess.getLoopCharacteristics();

        if( loopCharacteristics != null ) {
            isBLooped = true;
            if( loopCharacteristics.getValue() instanceof TMultiInstanceLoopCharacteristics ) isBMultiinstance = true;
        }

        for( TDataAssociation dataAssociation : subProcess.getDataOutputAssociation() )
            dataAssociations.add(dataAssociation);

        for( TDataAssociation dataAssociation : subProcess.getDataInputAssociation() )
            dataAssociations.add(dataAssociation);

        sp = diagram.addSubProcess(label, isBLooped, isBAdhoc, isBCompensation, isBMultiinstance, isBCollapsed, isTriggeredByEvent, parentProcess);
        sp.setParentSwimlane(pool);
        idToNode.put(subProcess.getId(), sp);

        unfoldProcess(subProcess, sp);
    }

    private void unfoldProcess(TBaseElement root, SubProcess parentProcess) {

        if( root instanceof TProcess ) {
            Swimlane pool = idToPool.get(root.getId());
            if( (pool == null) && (processes.size() > 1) ) {
                //LOGGER.info("number of processes: " + processes.size() );
                pool = diagram.addSwimlane("unknown", null, SwimlaneType.POOL);
                LOGGER.info("Added Pool unknown: " + pool.getId());
            }

            /**** Artifact contains TextAnnotation and Association objects ****/
            for( JAXBElement artifact : ((TProcess) root).getArtifact() ) {

                if( artifact.getValue() instanceof TAssociation )
                    this.associations.add((TAssociation) artifact.getValue());

                if( artifact.getValue() instanceof TTextAnnotation )
                    this.addTextAnnotation((TTextAnnotation) artifact.getValue(), null,  pool);
            }

            /**** FlowElement contains Flow, Event, DataObject, Activity, Gateway, CallActivity and SubProcess objects ****/
            for( JAXBElement flowElement : ((TProcess) root).getFlowElement() ) {

                if( flowElement.getValue() instanceof TSubProcess )
                    this.addSubProcess((TSubProcess) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TEvent )
                    this.addEvent((TEvent) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TTask )
                    this.addTask((TTask) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TGateway )
                    this.addGateway((TGateway) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TCallActivity )
                    this.addCallActivity((TCallActivity) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TDataObject )
                    this.addDataObject((TDataObject) flowElement.getValue(), null, pool);

                else if( flowElement.getValue() instanceof TSequenceFlow )
                    this.flows.add((TSequenceFlow) flowElement.getValue());
            }

            for( TLaneSet laneSet : ((TProcess) root).getLaneSet() ) addLanes(laneSet, pool);

        } else if( root instanceof TSubProcess ) {
            /**** Artifact contains TextAnnotation and Association objects ****/
            for( JAXBElement artifact : ((TSubProcess) root).getArtifact() ) {

                if( artifact.getValue() instanceof TAssociation )
                    this.associations.add((TAssociation) artifact.getValue());

                if( artifact.getValue() instanceof TTextAnnotation )
                    this.addTextAnnotation((TTextAnnotation) artifact.getValue(), parentProcess, null);
            }

            /**** FlowElement contains Flow, Event, DataObject, Activity, Gateway, CallActivity and SubProcess objects ****/
            for( JAXBElement flowElement : ((TSubProcess) root).getFlowElement() ) {

                if( flowElement.getValue() instanceof TSubProcess )
                    this.addSubProcess((TSubProcess) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TEvent )
                    this.addEvent((TEvent) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TTask )
                    this.addTask((TTask) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TGateway )
                    this.addGateway((TGateway) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TCallActivity )
                    this.addCallActivity((TCallActivity) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TDataObject )
                    this.addDataObject((TDataObject) flowElement.getValue(), parentProcess, null);

                else if( flowElement.getValue() instanceof TSequenceFlow )
                    this.flows.add((TSequenceFlow) flowElement.getValue());
            }

            for( TLaneSet laneSet : ((TSubProcess) root).getLaneSet() ) addLanes(laneSet, null);
        }
    }

    private void addLanes(TLaneSet laneSet, Swimlane parent) {
        TLaneSet childSet;
        Swimlane s;
        String id;

        for( TLane lane : laneSet.getLane() ) {
            s = diagram.addSwimlane(lane.getName(), parent, SwimlaneType.LANE);

            for( JAXBElement node : lane.getFlowNodeRef() ) {
                id = node.getValue().toString();
                LOGGER.info("Trying to set Lane > " + lane.getId() + " for:  " + id);
                if( idToNode.containsKey(id) ) {
                    idToNode.get(id).setParentSwimlane(s);
                    LOGGER.info("Lane set.");
                } else LOGGER.info("Unsettable Lane.");
            }

            childSet = lane.getChildLaneSet();
            if( childSet != null ) addLanes(childSet, s);
        }
    }

}
