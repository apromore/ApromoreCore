package org.apromore.canoniser.adapters;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.exception.CanoniserException;
import org.wfmc._2008.xpdl2.Activities;
import org.wfmc._2008.xpdl2.Activity;
import org.wfmc._2008.xpdl2.Artifact;
import org.wfmc._2008.xpdl2.Artifacts;
import org.wfmc._2008.xpdl2.Association;
import org.wfmc._2008.xpdl2.Associations;
import org.wfmc._2008.xpdl2.Condition;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfo;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfos;
import org.wfmc._2008.xpdl2.Coordinates;
import org.wfmc._2008.xpdl2.DataObject;
import org.wfmc._2008.xpdl2.EndEvent;
import org.wfmc._2008.xpdl2.Event;
import org.wfmc._2008.xpdl2.Implementation;
import org.wfmc._2008.xpdl2.IntermediateEvent;
import org.wfmc._2008.xpdl2.Join;
import org.wfmc._2008.xpdl2.Lane;
import org.wfmc._2008.xpdl2.Lanes;
import org.wfmc._2008.xpdl2.NodeGraphicsInfo;
import org.wfmc._2008.xpdl2.NodeGraphicsInfos;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.Pool;
import org.wfmc._2008.xpdl2.Pools;
import org.wfmc._2008.xpdl2.ProcessType;
import org.wfmc._2008.xpdl2.Route;
import org.wfmc._2008.xpdl2.Split;
import org.wfmc._2008.xpdl2.StartEvent;
import org.wfmc._2008.xpdl2.Task;
import org.wfmc._2008.xpdl2.Transition;
import org.wfmc._2008.xpdl2.TransitionRef;
import org.wfmc._2008.xpdl2.TransitionRefs;
import org.wfmc._2008.xpdl2.TransitionRestriction;
import org.wfmc._2008.xpdl2.TransitionRestrictions;
import org.wfmc._2008.xpdl2.Transitions;
import org.wfmc._2008.xpdl2.WorkflowProcesses;

import javax.xml.namespace.QName;
import java.lang.Object;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Canonical2XPDL {
    Map<NodeType, Activity> canon2xpdl = new HashMap<NodeType, Activity>();
    Map<EdgeType, Transition> edge2flow = new HashMap<EdgeType, Transition>();
    Map<ObjectType, Artifact> object2xpdl = new HashMap<ObjectType, Artifact>();
    Map<ObjectRefType, Association> ref2assoc = new HashMap<ObjectRefType, Association>();
    //Map<ResourceTypeType, Object> resource2xpdl = new HashMap<ResourceTypeType, Object>();
    Map<String, NodeType> nodeRefMap = new HashMap<String, NodeType>();
    Map<String, ObjectType> objectMap = new HashMap<String, ObjectType>();
    Map<BigInteger, ObjectRefType> objectRefMap = new HashMap<BigInteger, ObjectRefType>();
    Map<String, Object> resourceRefMap = new HashMap<String, Object>();
    Map<String, EdgeType> edgeRefMap = new HashMap<String, EdgeType>();
    //Map<BigInteger, EdgeType> assocRefMap = new HashMap<BigInteger, EdgeType>();
    Map<String, Activity> xpdlRefMap = new HashMap<String, Activity>();
    Map<String, String> cid2xid = new HashMap<String, String>();

    Map<String, List<Transition>> outgoingFlows = new HashMap<String, List<Transition>>();
    Map<String, List<Transition>> incomingFlows = new HashMap<String, List<Transition>>();

    Map<EventType, Event> events = new HashMap<EventType, Event>();
    Map<RoutingType, TransitionRestrictions> gateways = new HashMap<RoutingType, TransitionRestrictions>();
    List<String> resource_ref_list = new LinkedList<String>();
    //List<BigInteger> object_ref_list = new LinkedList<BigInteger>();
    Map<String, String> task_resource_map = new HashMap<String, String>();
    List<AnnData> mani_trans = new LinkedList<AnnData>();
    List<String> mani_trans_list = new LinkedList<String>();
    int object_ids;
    boolean split_process, EPML_flag;

    private PackageType xpdl;

    /**
     * de-canonize data (canonical) into xpdl
     *
     * @throws
     */
    @SuppressWarnings("unchecked")
    public Canonical2XPDL(CanonicalProcessType cpf) {
        this.xpdl = new PackageType();
        this.xpdl.setWorkflowProcesses(new WorkflowProcesses());
        this.xpdl.setPools(new Pools());
        object_ids = 1;
        split_process = false;
        translateObjects(cpf);

        for (NetType net : cpf.getNet()) {
            ProcessType bpmnproc = new ProcessType();
            bpmnproc.setId(net.getId().toString());
            translateNet(bpmnproc, net);
            translateResources(bpmnproc, cpf);
            if (split_process) {
                split(bpmnproc);
            }
            this.xpdl.getWorkflowProcesses().getWorkflowProcess().add(bpmnproc);
        }

    }


    /**
     * de-canonize data (canonical) into xpdl using anf data
     *
     * @param anf
     * @throws org.apromore.exception.ExceptionAdapters
     *
     */
    @SuppressWarnings("unchecked")
    public Canonical2XPDL(CanonicalProcessType cpf, AnnotationsType anf) throws CanoniserException {
        this.EPML_flag = false;
        for (TypeAttribute att : cpf.getAttribute()) {
            if (att.getTypeRef().equals("IntialFormat")) {
                if (att.getValue().equals("EPML")) {
                    this.EPML_flag = true;
                }
            }
        }

        this.xpdl = new PackageType();
        this.xpdl.setWorkflowProcesses(new WorkflowProcesses());
        this.xpdl.setPools(new Pools());
        object_ids = 1;
        split_process = false;
        translateObjects(cpf);

        for (NetType net : cpf.getNet()) {
            ProcessType bpmnproc = new ProcessType();
            bpmnproc.setId(net.getId().toString());
            translateNet(bpmnproc, net, anf);
            translateResources(bpmnproc, cpf);
            if (split_process) {
                split(bpmnproc);
            }
            this.xpdl.getWorkflowProcesses().getWorkflowProcess().add(bpmnproc);
        }

        if (EPML_flag) {
            enhance_annotation();
            resize_lanes();
        }
    }

    private void resize_lanes() {
    }


    /**
     * This method is for enhancing the annotation in case that
     * initial format is other than XPDL
     */
    private void enhance_annotation() {
        List<Activity> activities = null;
        List<Transition> transitions = null;
        if (xpdl.getWorkflowProcesses() != null) {
            int size, count = 1;
            size = xpdl.getWorkflowProcesses().getWorkflowProcess().size();
            for (ProcessType bpmnproc : xpdl.getWorkflowProcesses().getWorkflowProcess()) {
                // Add the ProcessHeader (to the anytype)
                bpmnproc.getOtherAttributes().put(new QName("ProcessHeader"), "");
                for (Object obj : bpmnproc.getContent()) {
                    if (obj instanceof Activities)
                        activities = ((Activities) obj).getActivity();
                    else if (obj instanceof Transitions)
                        transitions = ((Transitions) obj).getTransition();
                }
                if (activities != null)
                    for (Activity act : activities) {
                        TransitionRefs refs = null;
                        NodeGraphicsInfos infos = null;
                        for (Object obj : act.getContent()) {
                            if (obj instanceof TransitionRestrictions) {
                                for (TransitionRestriction trest : ((TransitionRestrictions) obj).getTransitionRestriction()) {
                                    if (trest.getSplit() != null &&
                                            (trest.getSplit().getType().equals("Exclusive") || trest.getSplit().getType().equals("Inclusive")))
                                        refs = trest.getSplit().getTransitionRefs();
                                }
                            } else if (obj instanceof NodeGraphicsInfos) {
                                infos = (NodeGraphicsInfos) obj;
                            }
                        }
                        if (refs != null && infos != null && infos.getNodeGraphicsInfo() != null) {
                            double x, y, h, w, newX = 0, newY = 0;
                            h = infos.getNodeGraphicsInfo().get(0).getHeight();
                            w = infos.getNodeGraphicsInfo().get(0).getWidth();
                            x = infos.getNodeGraphicsInfo().get(0).getCoordinates().getXCoordinate();
                            y = infos.getNodeGraphicsInfo().get(0).getCoordinates().getYCoordinate();
                            for (TransitionRef ref : refs.getTransitionRef())
                                for (Transition flow : transitions)
                                    if (ref.getId().equals(flow.getId())) {
                                        for (Activity act2 : activities)
                                            if (flow.getTo() != null && flow.getTo().equals(act2.getId())) {
                                                for (Object obj : act2.getContent())
                                                    if (obj instanceof NodeGraphicsInfos) {
                                                        NodeGraphicsInfos infos2 = (NodeGraphicsInfos) obj;
                                                        if (infos2 != null && infos2.getNodeGraphicsInfo() != null) {
                                                            newX = infos2.getNodeGraphicsInfo().get(0).getCoordinates().getXCoordinate() +
                                                                    infos2.getNodeGraphicsInfo().get(0).getWidth() / 2;
                                                            newY = infos2.getNodeGraphicsInfo().get(0).getCoordinates().getYCoordinate();
                                                        }

                                                    }
                                            }
                                        if (flow.getConnectorGraphicsInfos() != null &&
                                                flow.getConnectorGraphicsInfos().getConnectorGraphicsInfo() != null)
                                            for (ConnectorGraphicsInfo info : flow.getConnectorGraphicsInfos().getConnectorGraphicsInfo())
                                                for (Coordinates coord : info.getCoordinates())
                                                    if (!(coord.getXCoordinate() > x && coord.getXCoordinate() < x + w &&
                                                            coord.getYCoordinate() > y && coord.getYCoordinate() < y + h)) {
                                                        coord.setXCoordinate(newX);
                                                        coord.setYCoordinate(newY);
                                                    }
                                    }
                        }
                    }
            }
        }

    }


    private void split(ProcessType bpmnproc) {
        bpmnproc.setAdHocOrdering("Parallel");
        bpmnproc.setProcessType("None");
        bpmnproc.setStatus("None");

        ProcessType transproc = new ProcessType();
        transproc.setAdHocOrdering("Sequential");
        transproc.setProcessType("None");
        transproc.setStatus("None");
        transproc.setSuppressJoinFailure(true);
        transproc.setId("MainPool-process");
        transproc.setName("MainProcess");

        for (Object obj : bpmnproc.getContent())
            if (obj instanceof Transitions) {
                transproc.getContent().add(((Transitions) obj));
            }

        for (Object obj : transproc.getContent())
            if (obj instanceof Transitions) {
                bpmnproc.getContent().remove(((Transitions) obj));
            }

        this.xpdl.getWorkflowProcesses().getWorkflowProcess().add(transproc);

    }

    private void translateObjects(CanonicalProcessType cpf) {

        this.xpdl.setArtifacts(new Artifacts());
        this.xpdl.setAssociations(new Associations());
        for (ObjectType obj : cpf.getObject()) {
            Artifact a = new Artifact();
            a.setArtifactType("DataObject");
            a.setName(obj.getName());
            DataObject o = new DataObject();
            o.setName(obj.getName());
            a.setId(obj.getId().toString());
            a.setDataObject(o);
            this.xpdl.getArtifacts().getArtifactAndAny().add(a);
            objectMap.put(obj.getId(), obj);
            object2xpdl.put(obj, a);
        }

    }

    private void translateResources(ProcessType bpmnproc, CanonicalProcessType cpf) {
        boolean flag = true;
        Pool parent = new Pool();

        if (resource_ref_list.size() == 1) {
            Pool p = new Pool();
            p.setName(cpf.getResourceType().get(0).getName());
            p.setId(resource_ref_list.get(0).toString());
            p.setProcess(bpmnproc.getId());
            resourceRefMap.put(resource_ref_list.get(0), p);
            this.xpdl.getPools().getPool().add(p);
        } else if (resource_ref_list.size() > 1) {

            for (ResourceTypeType res : cpf.getResourceType()) {
                if (resource_ref_list.contains(res.getId()) && res.getSpecializationIds().size() > 0) {
                    Pool p = new Pool();
                    p.setName(res.getName());
                    p.setId(res.getId().toString());
                    p.setProcess(bpmnproc.getId());
                    p.setLanes(new Lanes());
                    for (String id : res.getSpecializationIds()) {
                        Lane lane = new Lane();
                        lane.setId(id.toString());
                        //lane.setName(value);
                        p.getLanes().getLane().add(lane);
                        resourceRefMap.put(id, lane);
                    }
                    resourceRefMap.put(res.getId(), p);
                    this.xpdl.getPools().getPool().add(p);
                } else if (resource_ref_list.contains(res.getId())) { // when all of them should be lanes into a pool

                    if (flag) {
                        parent.setId(res.getId().toString() + "-Pool");
                        parent.setProcess(bpmnproc.getId());
                        parent.setLanes(new Lanes());
                        parent.setMainPool(false);
                        parent.setBoundaryVisible(true);
                        parent.setOrientation("HORIZONTAL");
                        resourceRefMap.put(res.getId(), parent);
                        this.xpdl.getPools().getPool().add(parent);
                        flag = false;

                        Pool p = new Pool();
                        p.setId("MainPool");
                        p.setName("Main Pool");
                        p.setOrientation("HORIZONTAL");
                        p.setBoundaryVisible(false);
                        p.setMainPool(true);
                        p.setProcess("MainPool-process");
                        this.xpdl.getPools().getPool().add(p);

                        split_process = true;
                    }

                    Lane lane = new Lane();
                    lane.setParentPool(parent.getId());
                    lane.setId(res.getId().toString());
                    lane.setName(res.getName());
                    parent.getLanes().getLane().add(lane);
                    resourceRefMap.put(res.getId(), lane);

                }

            }
        } else if (resource_ref_list.size() == 0) {
            Pool p = new Pool();
            p.setId("MainPool");
            p.setName("Main Pool");
            p.setOrientation("HORIZONTAL");
            p.setBoundaryVisible(false);
            p.setMainPool(true);
            p.setProcess(bpmnproc.getId());
            this.xpdl.getPools().getPool().add(p);
        }

        resource_ref_list.clear();
    }

    private void translateNet(ProcessType bpmnproc, NetType net) {
        Activities acts = new Activities();
        Transitions trans = new Transitions();

        bpmnproc.getContent().add(acts);
        bpmnproc.getContent().add(trans);

        for (NodeType node : net.getNode()) {
            Activity act = translateNode(bpmnproc, node);
            acts.getActivity().add(act);
        }
        setActivitiesId(bpmnproc);

        for (EdgeType edge : net.getEdge()) {
            Transition flow = translateEdge(bpmnproc, edge);
            trans.getTransition().add(flow);
        }
        setTransitionsId(bpmnproc);

        completeMapping(bpmnproc, net);
    }

    private void translateNet(ProcessType bpmnproc, NetType net, AnnotationsType annotations) throws CanoniserException {
        Activities acts = new Activities();
        Transitions trans = new Transitions();

        bpmnproc.getContent().add(acts);
        bpmnproc.getContent().add(trans);

        for (NodeType node : net.getNode()) {
            Activity act = translateNode(bpmnproc, node);
            acts.getActivity().add(act);
        }

        setActivitiesId(bpmnproc);
        mapNodeAnnotations(bpmnproc, annotations);

        for (EdgeType edge : net.getEdge()) {
            Transition flow = translateEdge(bpmnproc, edge);
            trans.getTransition().add(flow);
        }

        setTransitionsId(bpmnproc);
        mapEdgeAnnotations(bpmnproc, annotations);

        mapResourceAnnotations(bpmnproc, annotations);
        mapObjectAnnotations(bpmnproc, annotations);
        mapAssociationAnnotations(bpmnproc, annotations);

        completeMapping(bpmnproc, net);
    }

    private void completeMapping(ProcessType bpmnproc, NetType net) {
        for (EventType event : events.keySet()) {
            Activity act = canon2xpdl.get(event);
            act.setName(event.getName());
            Event xevent = events.get(event);
            if (incomingFlows.get(act.getId()) == null) {
                StartEvent startEvent = new StartEvent();
                if (event instanceof MessageType)
                    startEvent.setTrigger("Message");
                else
                    startEvent.setTrigger("None");
                xevent.setStartEvent(startEvent);
            } else if (outgoingFlows.get(act.getId()) == null) {
                EndEvent endEvent = new EndEvent();
                if (event instanceof MessageType)
                    endEvent.setResult("Message");
                else
                    endEvent.setResult("None");
                xevent.setEndEvent(endEvent);
            } else {
                IntermediateEvent endEvent = new IntermediateEvent();
                if (event instanceof MessageType)
                    endEvent.setTrigger("Message");
                else if (event instanceof TimerType)
                    endEvent.setTrigger("Timer");
                else
                    endEvent.setTrigger("None");
                xevent.setIntermediateEvent(endEvent);
            }
        }
        for (RoutingType croute : gateways.keySet()) {
            Activity act = canon2xpdl.get(croute);
            TransitionRestrictions trests = gateways.get(croute);

            if (outgoingFlows.get(act.getId()) != null) {
                if (outgoingFlows.get(act.getId()).size() > 1) {
                    for (TransitionRestriction trest : trests
                            .getTransitionRestriction()) {
                        Split split = trest.getSplit();
                        if (split != null) {
                            TransitionRefs refs = split.getTransitionRefs();
                            for (Transition trans : outgoingFlows.get(act
                                    .getId())) {
                                TransitionRef ref = new TransitionRef();
                                ref.setId(trans.getId());
                                refs.getTransitionRef().add(ref);
                            }
                        }
                    }
                }
            }
        }
    }

    private void mapObjectAnnotations(ProcessType bpmnproc, AnnotationsType annotations) throws CanoniserException {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (objectMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                ObjectType obj = objectMap.get(cid);
                Artifact arti = object2xpdl.get(obj);

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;
                    NodeGraphicsInfos infos = new NodeGraphicsInfos();
                    NodeGraphicsInfo info = new NodeGraphicsInfo();

                    if (cGraphInfo.getFill() != null)
                        // TODO: Parse color format
                        info.setFillColor(cGraphInfo.getFill().getColor());

                    if (cGraphInfo.getSize() != null && cGraphInfo.getSize().getHeight() != null && cGraphInfo.getSize().getWidth() != null) {
                        info.setHeight(cGraphInfo.getSize().getHeight().doubleValue());
                        info.setWidth(cGraphInfo.getSize().getWidth().doubleValue());
                    }
                    Coordinates coords = new Coordinates();
                    if (cGraphInfo.getPosition() != null) {
                        try {
                            coords.setXCoordinate(cGraphInfo.getPosition().get(0).getX().doubleValue());
                            coords.setYCoordinate(cGraphInfo.getPosition().get(0).getY().doubleValue());
                            info.setCoordinates(coords);
                        } catch (IndexOutOfBoundsException e) {
                            String msg = "Failed to get coordinates of the Object, Index out.";
                            throw new CanoniserException(msg, e);
                        } catch (NullPointerException e) {
                            String msg = "Failed to get some attributes when getting coordinates of Object, Null.";
                            throw new CanoniserException(msg, e);
                        }
                    }

                    infos.getNodeGraphicsInfo().add(info);
                    arti.setNodeGraphicsInfos(infos);
                }
            }
        }
    }

    private void mapResourceAnnotations(ProcessType bpmnproc, AnnotationsType annotations) throws CanoniserException {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (resourceRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                Object obj = resourceRefMap.get(cid);

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;
                    NodeGraphicsInfos infos = new NodeGraphicsInfos();
                    NodeGraphicsInfo info = new NodeGraphicsInfo();

                    if (cGraphInfo.getFill() != null)
                        // TODO: Parse color format
                        info.setFillColor(cGraphInfo.getFill().getColor());

                    if (cGraphInfo.getSize() != null && cGraphInfo.getSize().getHeight() != null && cGraphInfo.getSize().getWidth() != null) {
                        info.setHeight(cGraphInfo.getSize().getHeight().doubleValue());
                        info.setWidth(cGraphInfo.getSize().getWidth().doubleValue());
                    }
                    Coordinates coords = new Coordinates();
                    if (cGraphInfo.getPosition() != null) {
                        try {
                            coords.setXCoordinate(cGraphInfo.getPosition().get(0).getX().doubleValue());
                            coords.setYCoordinate(cGraphInfo.getPosition().get(0).getY().doubleValue());
                            info.setCoordinates(coords);
                        } catch (IndexOutOfBoundsException e) {
                            String msg = "Failed to get coordinates of the Resource Type, Index out.";
                            throw new CanoniserException(msg, e);
                        } catch (NullPointerException e) {
                            String msg = "Failed to get some attributes when getting coordinates of the Resource Type, Null.";
                            throw new CanoniserException(msg, e);
                        }
                    }

                    infos.getNodeGraphicsInfo().add(info);

                    if (obj instanceof Pool)
                        ((Pool) obj).setNodeGraphicsInfos(infos);
                    else if (obj instanceof Lane)
                        ((Lane) obj).setNodeGraphicsInfos(infos);
                }
            }
        }
    }

    private void mapNodeAnnotations(ProcessType bpmnproc, AnnotationsType annotations) throws CanoniserException {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (nodeRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                NodeType node = nodeRefMap.get(cid);
                Activity act = canon2xpdl.get(node);

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;
                    NodeGraphicsInfos infos = new NodeGraphicsInfos();
                    NodeGraphicsInfo info = new NodeGraphicsInfo();

                    if (cGraphInfo.getFill() != null)
                        // TODO: Parse color format
                        info.setFillColor(cGraphInfo.getFill().getColor());

                    if (cGraphInfo.getSize() != null && cGraphInfo.getSize().getHeight() != null && cGraphInfo.getSize().getWidth() != null) {
                        info.setHeight(cGraphInfo.getSize().getHeight().doubleValue());
                        info.setWidth(cGraphInfo.getSize().getWidth().doubleValue());
                    }
                    Coordinates coords = new Coordinates();
                    if (cGraphInfo.getPosition() != null) {
                        try {
                            coords.setXCoordinate(cGraphInfo.getPosition().get(0).getX().doubleValue());
                            coords.setYCoordinate(cGraphInfo.getPosition().get(0).getY().doubleValue());
                            info.setCoordinates(coords);
                        } catch (IndexOutOfBoundsException e) {
                            String msg = "Failed to get coordinates of the Node, Index out.";
                            throw new CanoniserException(msg, e);
                        } catch (NullPointerException e) {
                            String msg = "Failed to get some attributes when getting coordinates of the Node, Null.";
                            throw new CanoniserException(msg, e);
                        }
                    }

                    if (EPML_flag) {
                        for (Object obj : act.getContent())
                            if (obj instanceof Event)
                                info = manipulateEPML(info, 'e', act.getId());
                            else if (obj instanceof Route)
                                info = manipulateEPML(info, 'r', act.getId());
                    }

                    infos.getNodeGraphicsInfo().add(info);
                    act.getContent().add(infos);
                }
            }
        }
    }

    private NodeGraphicsInfo manipulateEPML(NodeGraphicsInfo info, char c, String id) {
        double wo, wn, ho, hn, xo, yo, xn, yn;
        wo = wn = ho = hn = xo = yo = xn = yn = 0;

        if (info != null && info.getCoordinates() != null) {
            wo = info.getWidth();
            ho = info.getHeight();
            xo = info.getCoordinates().getXCoordinate();
            yo = info.getCoordinates().getYCoordinate();
        }

        if (c == 'e') {
            wn = hn = 30.0;
            info.setHeight(30.0);
            info.setWidth(30.0);
        } else if (c == 'r') {
            wn = hn = 40.0;
            info.setHeight(40.0);
            info.setWidth(40.0);
        }

        xn = xo + (wo - wn) / 2;
        yn = yo + (ho - hn) / 2;
        info.getCoordinates().setXCoordinate(xn);
        info.getCoordinates().setYCoordinate(yn);
        mani_trans_list.add(id);
        mani_trans.add(new AnnData(id, xo, yo, xn, yn, ho, wo, hn, wn));
        return info;
    }

    private void mapEdgeAnnotations(ProcessType bpmnproc,
                                    AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (edgeRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                EdgeType edge = edgeRefMap.get(cid);
                Transition flow = edge2flow.get(edge);

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;

                    ConnectorGraphicsInfos infos = new ConnectorGraphicsInfos();
                    ConnectorGraphicsInfo info = new ConnectorGraphicsInfo();

                    for (PositionType pos : cGraphInfo.getPosition()) {
                        Coordinates coords = new Coordinates();
                        coords.setXCoordinate(pos.getX().doubleValue());
                        coords.setYCoordinate(pos.getY().doubleValue());
                        info.getCoordinates().add(coords);
                    }

                    if (EPML_flag) {
                        if (mani_trans_list.contains(flow.getFrom()) || mani_trans_list.contains(flow.getTo())) {
                            AnnData annData = null;
                            for (AnnData ad : mani_trans) {
                                if (ad.elementID.equals(flow.getFrom()) || ad.elementID.equals(flow.getTo())) {
                                    annData = ad;
                                    for (Coordinates coor : info.getCoordinates()) {
                                        if (coor.getXCoordinate() >= annData.oldX
                                                && coor.getXCoordinate() <= annData.oldX + annData.oldW
                                                && coor.getYCoordinate() >= annData.oldY
                                                && coor.getYCoordinate() <= annData.oldY + annData.oldH) {
                                            coor.setXCoordinate(annData.newX + annData.newH / 2);
                                            coor.setYCoordinate(annData.newY + annData.newH / 2);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    infos.getConnectorGraphicsInfo().add(info);
                    flow.setConnectorGraphicsInfos(infos);
                }
            }
        }
    }


    private void mapAssociationAnnotations(ProcessType bpmnproc,
                                           AnnotationsType annotations) {
        for (AnnotationType annotation : annotations.getAnnotation()) {
            if (objectRefMap.containsKey(annotation.getCpfId())) {
                // TODO: Handle 1-N mappings
                String cid = annotation.getCpfId();
                ObjectRefType ref = objectRefMap.get(cid);
                Association assoc = ref2assoc.get(ref);

                if (annotation instanceof GraphicsType) {
                    GraphicsType cGraphInfo = (GraphicsType) annotation;

                    ConnectorGraphicsInfos infos = new ConnectorGraphicsInfos();
                    ConnectorGraphicsInfo info = new ConnectorGraphicsInfo();

                    for (PositionType pos : cGraphInfo.getPosition()) {
                        Coordinates coords = new Coordinates();
                        coords.setXCoordinate(pos.getX().doubleValue());
                        coords.setYCoordinate(pos.getY().doubleValue());
                        info.getCoordinates().add(coords);
                    }

                    infos.getConnectorGraphicsInfo().add(info);
                    assoc.setConnectorGraphicsInfos(infos);
                }
            }
        }
    }

    private void setActivitiesId(ProcessType bpmnproc) {
        for (NodeType node : nodeRefMap.values()) {
            Activity act = canon2xpdl.get(node);
            if (act != null) {
                act.setId(node.getId());
                xpdlRefMap.put(node.getId(), act);
                cid2xid.put(node.getId(), act.getId());
            }
        }
    }

    private void setTransitionsId(ProcessType bpmnproc) {
        for (EdgeType edge : edgeRefMap.values()) {
            Transition flow = edge2flow.get(edge);
            flow.setId(edge.getId().toString());
            cid2xid.put(edge.getId(), flow.getId());
        }
    }

    private Transition translateEdge(ProcessType bpmnproc, EdgeType edge) {
        Transition flow = new Transition();
        flow.setFrom(cid2xid.get(edge.getSourceId()));
        flow.setTo(cid2xid.get(edge.getTargetId()));
        if (edge.getCondition() != null) {
            Condition cond = new Condition();
            cond.setExpression(edge.getCondition());
            flow.setCondition(cond);
        }
        edge2flow.put(edge, flow);
        edgeRefMap.put(edge.getId(), edge);

        List<Transition> list = outgoingFlows.get(flow.getFrom());
        if (list == null) {
            list = new LinkedList<Transition>();
            outgoingFlows.put(flow.getFrom(), list);
        }
        list.add(flow);

        list = incomingFlows.get(flow.getTo());
        if (list == null) {
            list = new LinkedList<Transition>();
            incomingFlows.put(flow.getTo(), list);
        }
        list.add(flow);

        return flow;
    }

    private Activity translateNode(ProcessType bpmnproc, NodeType node) {
        Activity act = null;
        if (node instanceof TaskType) {
            act = translateTask(bpmnproc, node);

            for (ResourceTypeRefType ref : ((TaskType) node).getResourceTypeRef()) {
                if (!resource_ref_list.contains(ref.getResourceTypeId()))
                    resource_ref_list.add(ref.getResourceTypeId());
                task_resource_map.put(node.getId(), ref.getResourceTypeId());
            }

            for (ObjectRefType ref : ((TaskType) node).getObjectRef()) {
                Association a = new Association();
                a.setId("xpdl_objects_" + object_ids++);
                a.setAssociationDirection("To");
                // output
                if (ref.getType().equals(InputOutputType.OUTPUT)) {
                    a.setSource(((TaskType) node).getId().toString());
                    a.setTarget(ref.getObjectId().toString());
                } else {
                    a.setSource(ref.getObjectId().toString());
                    a.setTarget(((TaskType) node).getId().toString());
                }
                for (TypeAttribute att : ref.getAttribute())
                    if (att.getTypeRef().equals("RefID"))
                        objectRefMap.put(BigInteger.valueOf(Long.parseLong(att.getValue())), ref);
                ref2assoc.put(ref, a);
                this.xpdl.getAssociations().getAssociationAndAny().add(a);
            }
        } else if (node instanceof RoutingType) {
            act = translateGateway(bpmnproc, node);
        } else if (node instanceof EventType)
            act = translateEvent(bpmnproc, node);

        canon2xpdl.put(node, act);
        nodeRefMap.put(node.getId(), node);

        return act;
    }

    private Activity translateTask(ProcessType bpmnproc, NodeType node) {
        Activity act = new Activity();
        act.setName(node.getName());
        Implementation impl = new Implementation();
        act.getContent().add(impl);
        Task task = new Task();
        impl.setTask(task);
        return act;
    }

    private Activity translateGateway(ProcessType bpmnproc, NodeType node) {
        Activity act = new Activity();
        Route route = new Route();
        TransitionRestrictions trests = new TransitionRestrictions();

        if (node instanceof ANDSplitType) {
            TransitionRestriction trest = createSplitGateway(route, "Parallel", null);
            trests.getTransitionRestriction().add(trest);
        }

        if (node instanceof ANDJoinType) {
            TransitionRestriction trest = createJoinGateway(route, "Parallel");
            trests.getTransitionRestriction().add(trest);
        }

        if (node instanceof XORSplitType) {
            TransitionRestriction trest = createSplitGateway(route, "Exclusive", "Data");
            trests.getTransitionRestriction().add(trest);
        }

        if (node instanceof XORJoinType) {
            TransitionRestriction trest = createJoinGateway(route, "Exclusive");
            trests.getTransitionRestriction().add(trest);
        }

        if (node instanceof ORSplitType) {
            TransitionRestriction trest = createSplitGateway(route, "Inclusive", null);
            trests.getTransitionRestriction().add(trest);
        }

        if (node instanceof ORJoinType) {
            TransitionRestriction trest = createJoinGateway(route, "Inclusive");
            trests.getTransitionRestriction().add(trest);
        }

        act.getContent().add(route);
        act.getContent().add(trests);

        gateways.put((RoutingType) node, trests);
        return act;
    }

    private TransitionRestriction createSplitGateway(Route route, String type, String extra) {
        route.setGatewayType(type);
        TransitionRestriction trest = new TransitionRestriction();
        Split split = new Split();
        TransitionRefs refs = new TransitionRefs();
        split.setTransitionRefs(refs);
        split.setType(type);
        if (extra != null)
            split.setExclusiveType(extra);
        trest.setSplit(split);
        return trest;
    }

    private TransitionRestriction createJoinGateway(Route route, String type) {
        route.setGatewayType(type);
        TransitionRestriction trest = new TransitionRestriction();
        Join join = new Join();
        join.setType(type);
        trest.setJoin(join);
        return trest;
    }

    private Activity translateEvent(ProcessType bpmnproc, NodeType node) {
        Activity act = new Activity();
        Event event = new Event();

        act.getContent().add(event);

        events.put((EventType) node, event);
        return act;
    }

    public PackageType getXpdl() {
        return xpdl;
    }

}