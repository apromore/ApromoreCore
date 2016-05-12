package au.edu.qut.structuring;

import au.edu.qut.structuring.core.StructuringCore;
import au.edu.qut.structuring.wrapper.BPStructWrapper;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import au.edu.qut.structuring.ui.iBPStructUI;
import au.edu.qut.structuring.ui.iBPStructUIResult;
import org.processmining.plugins.bpmn.BpmnAssociation;

import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */
public class StructuringService {

    private static final int MAX_DEPTH = 100;
    private static final int MAX_CHILDREN = 10;
    private static final int MAX_SOL = 500;
    private static final int MAX_STATES = 100;
    private static final int MAX_MINUTES = 2;

    private String  policy;
    private int     maxDepth;
    private int     maxSolutions;
    private int     maxChildren;
    private int     maxStates;
    private int     maxMinutes;
    private boolean timeBounded;
    private boolean keepBisimulation;
    private boolean forceStructuring;

    private BPMNDiagram diagram;		//initial diagram

    private Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> unmappableEdges;
    private Set<BPMNNode> unmappableNodes;
    private Map<String, BPMNNode> originalNodes;

    private Set<SubProcess> subProcessesToParse;
    private LinkedList<String> rebuildOrder;

    private Set<Event> startEvents;
    private Set<Event> fakeStartEvents;
    private Set<Event> endEvents;
    private Set<Event> fakeEndEvents;

    private Map<Event, Activity> boundToFix;
    private Map<Event, BPMNNode> compensationActivities;

    private Map<String, Set<BPMNNode>> blackList;
    private Map<String, Set<BPMNNode>> whiteList;
    private Map<BPMNNode, BPMNNode> matrices;

    /**** mapping between processes' IDs and their structured diagram version ****/
    private Map<String, BPMNDiagram> idToDiagram;

    public StructuringService(){}

    public BPMNDiagram structureDiagram(BPMNDiagram diagram) {
        this.diagram = diagram;
        return structureDiagram(diagram, "ASTAR", MAX_DEPTH, MAX_SOL, MAX_CHILDREN, MAX_STATES, MAX_MINUTES, true, true, false);
    }

    public BPMNDiagram structureDiagram(BPMNDiagram diagram,
                                        String  policy,
                                        int     maxDepth,
                                        int     maxSolutions,
                                        int     maxChildren,
                                        int     maxStates,
                                        int     maxMinutes,
                                        boolean timeBounded,
                                        boolean keepBisimulation,
                                        boolean forceStructuring) {
        this.diagram = diagram;
        this.policy = policy;
        this.maxDepth = maxDepth;
        this.maxSolutions = maxSolutions;
        this.maxChildren = maxChildren;
        this.maxStates = maxStates;
        this.maxMinutes = maxMinutes;
        this.timeBounded = timeBounded;
        this.keepBisimulation = keepBisimulation;
        this.forceStructuring = forceStructuring;

        long start, end;

        start = System.currentTimeMillis();

        try {
            this.structureDiagram();
        } catch (Exception e) {
            System.out.println("ERROR - impossible structure diagram");
        }

        end = System.currentTimeMillis() - start;
        System.out.println("TEST - total structuring time: " + end + " ms");

        return this.diagram;
    }


    @Plugin(
            name = "Structure Diagram",
            parameterLabels = { "BPMNDiagram" },
            returnLabels = { "Structured Diagram" },
            returnTypes = { BPMNDiagram.class },
            userAccessible = true,
            help = "Structure a BPMNDiagram"
    )
    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "adriano.augusto@qut.edu.au"
    )
    public static BPMNDiagram structureDiagram(UIPluginContext context, BPMNDiagram diagram) {
        BPMNDiagram structuredDiagram;
        iBPStructUI gui = new iBPStructUI();
        iBPStructUIResult result = gui.showGUI(context);
        StructuringService ss = new StructuringService();
        try {
            structuredDiagram = ss.structureDiagram(diagram,
                                                    result.getPolicy().toString(),
                                                    result.getMaxDepth(),
                                                    result.getMaxSol(),
                                                    result.getMaxChildren(),
                                                    result.getMaxStates(),
                                                    result.getMaxMinutes(),
                                                    result.isTimeBounded(),
                                                    result.isKeepBisimulation(),
                                                    result.isForceStructuring());
        } catch(Exception e) {
            context.log(e);
            System.err.print(e);
            return diagram;
        }
        return structuredDiagram;
    }


    private void structureDiagram() throws Exception {
        unmappableEdges = new HashSet<>();
        unmappableNodes = new HashSet<>();
        originalNodes = new HashMap<>();

        subProcessesToParse = new HashSet<>();
        rebuildOrder = new LinkedList<>();

        startEvents = new HashSet<>();
        fakeStartEvents = new HashSet<>();
        endEvents = new HashSet<>();
        fakeEndEvents = new HashSet<>();

        boundToFix = new HashMap<>();
        compensationActivities = new HashMap<>();

        blackList = new HashMap<>();
        whiteList = new HashMap<>();
        matrices = new HashMap<>();

        idToDiagram = new HashMap<>();

        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeToRemove = new HashSet<>();
        Set<BPMNNode> nodeToRemove = new HashSet<>();

        /**** STEP0: back up of all not mappable edges and nodes on the .json file ****/
        unmappableEdges.addAll(diagram.getAssociations());
        unmappableEdges.addAll(diagram.getDataAssociations());
        unmappableEdges.addAll(diagram.getMessageFlows());

        unmappableNodes.addAll(diagram.getTextAnnotations());
        unmappableNodes.addAll(diagram.getDataObjects());

        /** STEP1: restructure the diagram in order to remove
         * - double edges
         * - fakeGates (those gate with only 1 entering and 1 exiting flow)
         * - multiple start and end events
         * - boundary events
         **/
        fixImplicitGateways();
        removeDoubleEdges();

        collapseSplitGateways();
        removeDoubleEdges();

        removeMultipleStartEvents();
        removeMultipleEndEvents();
        handleBoundaryEvents();

        /**** STEP2: get all the structured version for each subProcess ****/
        for( SubProcess sp : diagram.getSubProcesses() ) {
            subProcessesToParse.add(sp);
            originalNodes.put(sp.getId().toString(), sp);
            System.out.println("SubProcess: " + sp.getId());
        }

        for( Swimlane pool : diagram.getPools() ) parsePool(pool);
        parsePool(null);
        parseSubProcesses(null);

        /**** STEP3: remove all the edges and not mappable nodes from the diagram ****/
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getEdges() ) edgeToRemove.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : edgeToRemove ) diagram.removeEdge(e);

        for( Gateway g : diagram.getGateways() ) nodeToRemove.add(g);
        for( BPMNNode g : nodeToRemove ) removeNode(g);

        /**** STEP4: reconnect all the elements inside the diagram exploiting .json files and support maps ****/
        while( !rebuildOrder.isEmpty() ) {
            String idp = rebuildOrder.removeLast();
            boolean err = rebuildSubProcess(idp);
            if( err ) throw new Exception("Unable to rebuild the subProcess: " + idp);
        }

//        System.out.println("Total gateways after structuring: " + xorGates + "(xor) + " + andGates + "(and) + " + orGates + "(or)." );
        System.out.println("Total duplicates: " + matrices.size());

        /**** STEP5: restore edges and boundary, start, end events where feasible ****/
        restoreEdges();
        restoreEvents();

        /**** STEP6: fixing possible not valid configurations ****/
        removeDoubleEdges();
        for( Gateway g : new HashSet<>(diagram.getGateways()) ) checkFakeGateway(g);

        collapseJoinGateways();
    }

    private void parsePool(Swimlane pool) {
        String id = (pool == null ? "null" : pool.getId().toString());
        System.out.println("Analyzing Pool: " + id );
        structureSubProcess(id, diagram.getFlows(pool));
    }

    private void parseSubProcesses(SubProcess parent) {

        HashSet<SubProcess> analyzed = new HashSet<>();

        for( SubProcess sp : subProcessesToParse )
            if( sp.getParentSubProcess() == parent ) {
                structureSubProcess(sp.getId().toString(), diagram.getFlows(sp));
                analyzed.add(sp);
            }

        for( SubProcess spa : analyzed ) subProcessesToParse.remove(spa);
        for( SubProcess spa : analyzed ) parseSubProcesses(spa);
    }

    private void removeDoubleEdges() {
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> towAway = new HashSet<>();
        HashMap<BPMNNode, HashSet<BPMNNode>> flows = new HashMap<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getEdges() ) {
            if( !flows.containsKey(f.getSource()) ) {
                flows.put(f.getSource(), new HashSet<BPMNNode>());
                flows.get(f.getSource()).add(f.getTarget());
            } else {
                if( flows.get(f.getSource()).contains(f.getTarget()) ) towAway.add(f);
                else flows.get(f.getSource()).add(f.getTarget());
            }
        }

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : towAway ) {
            //System.out.println("DoubleFlow removed: " + ff.getSource().getId() + " > " + ff.getTarget().getId());
            diagram.removeEdge(ff);
            if( ff.getSource() instanceof Gateway )	checkFakeGateway((Gateway) ff.getSource());
            if( ff.getTarget() instanceof Gateway )	checkFakeGateway((Gateway) ff.getTarget());
        }
    }

    private void fixImplicitGateways() {
        Gateway g;
        HashSet<Flow> inFlows;
        HashSet<Flow> outFlows;

        for (BPMNNode n : diagram.getNodes()) {
            if( n instanceof Activity || n instanceof CallActivity ) {
                inFlows = new HashSet<>();
                outFlows = new HashSet<>();

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(n) )
                    if(e instanceof Flow) inFlows.add((Flow)e);

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(n) )
                    if(e instanceof Flow) outFlows.add((Flow)e);

                if( inFlows.size() > 1 ) {
                    g = diagram.addGateway("exGate", Gateway.GatewayType.DATABASED, n.getParentSubProcess());
                    g.setParentSwimlane(n.getParentSwimlane());
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : inFlows ) {
                        diagram.addFlow(e.getSource(), g, "");
                        diagram.removeEdge(e);
                    }
                    diagram.addFlow(g, n, "");
                }

                if( outFlows.size() > 1 ) {
                    g = diagram.addGateway("exGate", Gateway.GatewayType.PARALLEL, n.getParentSubProcess());
                    g.setParentSwimlane(n.getParentSwimlane());
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : outFlows ) {
                        diagram.addFlow(g, e.getTarget(), "");
                        diagram.removeEdge(e);
                    }
                    diagram.addFlow(n, g, "");
                }
            }
        }
    }

    private void checkFakeGateway(Gateway g) {
        //System.out.println("Checking fake gateways: " + g.getId());
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in = null;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out = null;
        int incoming = 0;
        int outgoing = 0;

        for( Flow f : diagram.getFlows() ) {
            if( f.getSource() == g ) {
                out = f;
                outgoing++;
            }
            if( f.getTarget() == g ) {
                in = f;
                incoming++;
            }
        }

        if( (outgoing == 1) && (incoming == 1) ) {
            diagram.addFlow(in.getSource(), out.getTarget(), "");
            diagram.removeEdge(in);
            diagram.removeEdge(out);
            removeNode(g);
            System.out.println("Found and removed a fake gate: " + g.getId());
        }
    }

    private void collapseSplitGateways() {
        LinkedList<Gateway> gates = new LinkedList<>(diagram.getGateways());
        Set<Gateway> eaten = new HashSet<>();
        Gateway eater;
        Gateway meal;
        boolean unhealthy;

        do {
            eater = gates.pollFirst();
            while( !eaten.contains(eater) ) {
                meal = null;
                unhealthy = true;

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(eater) )
                    if( (e.getTarget() instanceof Gateway) && ((meal = (Gateway) e.getTarget()).getGatewayType() == eater.getGatewayType()) ) {
                        unhealthy = false;
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getInEdges(meal) )
                            if( ee.getSource() != eater ) {
                                unhealthy = true;
                                break;
                            }

                        if( unhealthy ) continue;
                        break;
                    }

                if( unhealthy ) break;
                else {
                    eatSplit(meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );
        System.out.println("Collapsed gateways [split]: " + eaten.size());
    }

    private void collapseJoinGateways() {
        LinkedList<Gateway> gates = new LinkedList<>(diagram.getGateways());
        Set<Gateway> eaten = new HashSet<>();
        Gateway eater;
        Gateway meal;
        boolean unhealthy;

        do {
            eater = gates.pollFirst();
            while( !eaten.contains(eater) ) {
                meal = null;
                unhealthy = true;

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(eater) )
                    if( (e.getSource() instanceof Gateway) && ((meal = (Gateway) e.getSource()).getGatewayType() == eater.getGatewayType()) ) {
                        unhealthy = false;
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getOutEdges(meal) )
                            if( ee.getTarget() != eater ) {
                                unhealthy = true;
                                break;
                            }

                        if( unhealthy ) continue;
                        break;
                    }

                if( unhealthy ) break;
                else {
                    eatJoin(meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );
        System.out.println("Collapsed gateways [join]: " + eaten.size());
    }

    private void eatSplit(Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(eater, e.getTarget(), "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(meal);
    }

    private void eatJoin(Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(e.getSource(), eater, "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(meal);
    }

    private void removeMultipleStartEvents() {
        HashMap<SubProcess, HashSet<Event>> sp2se = new HashMap<>(); //subprocesses
        HashMap<Swimlane, HashSet<Event>> pl2se = new HashMap<>();	//pools
        Gateway g;
        Event fakeStart;

        for( SubProcess sp : diagram.getSubProcesses() ) sp2se.put(sp, new HashSet<Event>());
        for( Swimlane pl : diagram.getPools() ) pl2se.put(pl, new HashSet<Event>());
        pl2se.put(null, new HashSet<Event>());

		/* adding each startEvent to the right set */
        for( Event e : diagram.getEvents() )
            if( e.getEventType() == Event.EventType.START ) {
                if( e.getParentSubProcess() == null ) pl2se.get(e.getParentPool()).add(e);
                else sp2se.get(e.getParentSubProcess()).add(e);
            }

		/* for all those processes with multiple endEvents it is created a fakeEndPoint */
        for( SubProcess spe : sp2se.keySet() )
            if( sp2se.get(spe).size() > 1 ) {
                g = diagram.addGateway("fakeStartXOR", Gateway.GatewayType.DATABASED, spe);
                fakeStart = diagram.addEvent("fakeStart", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, spe, true, null);
                fakeStartEvents.add(fakeStart);

                for( Event e : sp2se.get(spe) ) {
                    diagram.addFlow(g, e, "fakeMergingFlow");
                    startEvents.add(e);
                    matrices.put(e, fakeStart);
                }

                diagram.addFlow(fakeStart, g, "fakeStartFlow");
                System.out.println("Added fakeStart (" + fakeStart.getId() + ") for subProcess: " + (spe == null ? "null" : spe.getId()));
            }

        for( Swimlane ple : pl2se.keySet() )
            if( pl2se.get(ple).size() > 1 ) {
                g = diagram.addGateway("fakeStartXOR", Gateway.GatewayType.DATABASED, ple);
                fakeStart = diagram.addEvent("fakeStart", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, ple, true, null);
                fakeStartEvents.add(fakeStart);

                for( Event e : pl2se.get(ple) ) {
                    diagram.addFlow(g, e, "fakeMergingFlow");
                    startEvents.add(e);
                    matrices.put(e, fakeStart);
                }

                diagram.addFlow(fakeStart, g, "fakeStartFlow");
                System.out.println("Added fakeStart (" + fakeStart.getId() + ") for pool: " + (ple == null ? "null" : ple.getId()));
            }
    }

    private void removeMultipleEndEvents() {
        HashMap<SubProcess, HashSet<Event>> sp2ee = new HashMap<>(); //subprocesses
        HashMap<Swimlane, HashSet<Event>> pl2ee = new HashMap<>();	//pools
        Gateway g;
        Event fakeEnd;

        for( SubProcess sp : diagram.getSubProcesses() ) sp2ee.put(sp, new HashSet<Event>());
        for( Swimlane pl : diagram.getPools() ) pl2ee.put(pl, new HashSet<Event>());
        pl2ee.put(null, new HashSet<Event>());

		/* adding each endEvent to the right set */
        for( Event e : diagram.getEvents() )
            if( e.getEventType() == Event.EventType.END ) {
                if( e.getParentSubProcess() == null ) pl2ee.get(e.getParentPool()).add(e);
                else sp2ee.get(e.getParentSubProcess()).add(e);
            }

		/* for all those processes with multiple endEvents it is created a fakeEndPoint */
        for( SubProcess spe : sp2ee.keySet() )
            if( sp2ee.get(spe).size() > 1 ) {
                g = diagram.addGateway("fakeEndXOR", Gateway.GatewayType.DATABASED, spe);
                fakeEnd = diagram.addEvent("fakeEnd", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, spe, true, null);
                fakeEndEvents.add(fakeEnd);

                for( Event e : sp2ee.get(spe) ) {
                    diagram.addFlow(e, g, "fakeMergingFlow");
                    endEvents.add(e);
                    matrices.put(e, fakeEnd);
                }

                diagram.addFlow(g, fakeEnd, "toFakeEnd");
                System.out.println("Added fakeEnd (" + fakeEnd.getId() + ") for subProcess: " + (spe == null ? "null" : spe.getId()));
            }

        for( Swimlane ple : pl2ee.keySet() )
            if( pl2ee.get(ple).size() > 1 ) {
                g = diagram.addGateway("fakeEndXOR", Gateway.GatewayType.DATABASED, ple);
                fakeEnd = diagram.addEvent("fakeEnd", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, ple, true, null);
                fakeEndEvents.add(fakeEnd);

                for( Event e : pl2ee.get(ple) ) {
                    diagram.addFlow(e, g, "fakeMergingFlow");
                    endEvents.add(e);
                    matrices.put(e, fakeEnd);
                }

                diagram.addFlow(g, fakeEnd, "toFakeEnd");
                System.out.println("Added fakeEnd (" + fakeEnd.getId() + ") for pool: " + (ple == null ? "null" : ple.getId()));
            }
    }

    private void handleBoundaryEvents() {
        Activity a;
        Gateway g;
        BPMNNode tgt;

        for( Event e : diagram.getEvents() )
            if( (a = e.getBoundingNode()) != null ) {
                if( e.getEventTrigger() == Event.EventTrigger.COMPENSATION ) {
                    for( Association association : diagram.getAssociations() )
                        if( association.getSource() == e )
                            if( ((association.getTarget() instanceof Activity) && ((Activity) association.getTarget()).isBCompensation()) ||
                                    ((association.getTarget() instanceof CallActivity) && ((CallActivity) association.getTarget()).isBCompensation()) )
                            {
                                System.out.println("Detaching compensation event and activity: " + e.getId() + " > " + association.getTarget().getId());
                                boundToFix.put(e, a);
                                compensationActivities.put(e, association.getTarget());
                                break;
                            }
                } else {
                    for (Flow f : diagram.getFlows())
                        if (f.getSource().equals(a)) {
                            tgt = f.getTarget();
                            if ((tgt instanceof Gateway) && ((((Gateway) tgt).getGatewayType() == Gateway.GatewayType.EVENTBASED) ||
                                    (((Gateway) tgt).getGatewayType() == Gateway.GatewayType.DATABASED))) {
                                g = (Gateway) tgt;
                            } else {
                                g = diagram.addGateway("boundaryXOR", Gateway.GatewayType.DATABASED, a.getParentSubProcess());
                                g.setParentSwimlane(a.getParentSwimlane());
                                diagram.addFlow(a, g, "");
                                diagram.addFlow(g, tgt, "");
                                diagram.removeEdge(f);
                            }
                            diagram.addFlow(g, e, "");
                            e.setParentSubprocess(a.getParentSubProcess());
                            boundToFix.put(e, a);
                            System.out.println("Boundary event found: " + a.getId() + " > " + e.getId());
                            break;
                        }
                }
            }
    }

    private void structureSubProcess(String processID, Collection<Flow> edges) {
        if( edges.size() == 0 ) return;

        Collection<BPMNNode> nodes = new HashSet<>();
        Collection<Flow> flows = new HashSet<>();

        BPMNNode src, tgt;
        BPMNDiagram structuredDiagram = null;

        boolean tryClassicBPStruct = false;

        for( Flow flow : edges ) {

            src = flow.getSource();
            tgt = flow.getTarget();

            /**** we cannot map these elements within the json scheme and so neither the current flow, this SHOULD NOT HAPPEN ****/
            if (src instanceof DataObject || src instanceof Swimlane || src instanceof TextAnnotation ||
                    tgt instanceof DataObject || tgt instanceof Swimlane || tgt instanceof TextAnnotation) {
                System.out.println("WARNING - unmappable flow: " + src.getId() + " > " + tgt.getId());
                unmappableEdges.add(flow);
                continue;
            }

            nodes.add(src);
            nodes.add(tgt);
            flows.add(flow);

            if( (!tryClassicBPStruct) && (src instanceof Gateway) && (((Gateway) src).getGatewayType() == Gateway.GatewayType.PARALLEL) ) {
                System.out.println("DEBUG - BPStruct enabled.");
                tryClassicBPStruct = true;
            }

            if (!originalNodes.containsKey(src.getId().toString())) originalNodes.put(src.getId().toString(), src);
            if (!originalNodes.containsKey(tgt.getId().toString())) originalNodes.put(tgt.getId().toString(), tgt);
        }

        try {
            if( tryClassicBPStruct && !forceStructuring ) {
                BPStructWrapper bpsw = new BPStructWrapper();
                structuredDiagram = bpsw.getStructured(flows);
            }

            if( structuredDiagram == null ) {
                iBPStruct spi = new iBPStruct(  StructuringCore.Policy.valueOf(policy),
                        maxDepth, maxSolutions, maxChildren, maxStates,
                        maxMinutes, timeBounded, keepBisimulation, forceStructuring);
                spi.setProcess(nodes, flows);
                spi.structure();
                structuredDiagram = spi.getDiagram();
            } else {
                matchGateways(structuredDiagram);
            }

        } catch(Exception e) {
            System.err.print(e);
            structuredDiagram = null;
        }

        idToDiagram.put(processID, structuredDiagram);
        rebuildOrder.addLast(processID);
    }

    private boolean rebuildSubProcess(String processID) {
        SubProcess parentProcess;

        Set<String> greyList = new HashSet<>();
        Map<String, BPMNNode> processedNodes = new HashMap<>();

        BPMNDiagram structuredProcess = idToDiagram.get(processID);

        Collection<BPMNNode> nodes = structuredProcess.getNodes();
        Collection<Flow> flows = structuredProcess.getFlows();
        System.out.println("Rebuilding: Process_" + processID + " with nodes(" + nodes.size() + ") flows(" + flows.size() + ")");

        BPMNNode node, src, tgt;
        String srcID;
        String tgtID;

        boolean error = false;

        try {
            if( originalNodes.get(processID) instanceof SubProcess ) parentProcess = (SubProcess) originalNodes.get(processID);
            else parentProcess = null;


            /** populating the blackList with duplicated activities **/
            for( BPMNNode n : nodes ) {
                String id = n.getLabel();
                if( n instanceof Gateway ) {
                    node = diagram.addGateway("", ((Gateway) n).getGatewayType(), parentProcess);
                } else if( originalNodes.containsKey(id) ) {
                    if (greyList.contains(id)) {
                        // this node has been duplicated by iBPStruct tool so we do the same
                        node = duplicateNode(originalNodes.get(id), parentProcess, true);
                        if (node == null)
                            throw new Exception("Error parsing tasks: " + n.getLabel() + " cannot be duplicated!");
                    } else {
    					/* the first time we encounter a node we retrieve the original one and mark it in the greyList */
                        node = originalNodes.get(id);
                        greyList.add(id);
                    }
                } else {
                    node = null;
                    System.out.println("ERROR - found new node that is not a gateway.");
                }
                processedNodes.put(n.getId().toString(), node);
            }

            /** applying mapped flows **/
            for( Flow f : flows ) {

                src = f.getSource();
                tgt = f.getTarget();

                srcID = src.getId().toString();
                tgtID = tgt.getId().toString();

                if( processedNodes.containsKey(srcID) ) src = processedNodes.get(srcID);
                else throw new Exception("ERROR - parsing flows source not found: " + srcID);

                if( processedNodes.containsKey(tgtID) ) tgt = processedNodes.get(tgtID);
                else throw new Exception("ERROR - parsing flows target not found: " + tgtID);

                diagram.addFlow(src, tgt, "");

                System.out.println("diagram- added flow: " + src.getId() +  " -> " + tgt.getId());
            }
        } catch(Exception e) {
            System.out.println("Error rebuilding subProcess: " + processID);
            error = true;
        }

        return error;
    }

    private BPMNNode duplicateNode(BPMNNode node, SubProcess parentProcess, boolean blackMark) {
        BPMNNode duplicate = null;
        String id = node.getId().toString();

        if( blackMark ) {
            if( !blackList.containsKey(id) ) blackList.put(id, new HashSet<BPMNNode>());
        } else {
            //System.out.println("Duplicating (" + blackMark + "): " + node.getClass().getSimpleName() + " : " + node.getId() + " with Parent: " + (parentProcess == null ? "top-Level" : parentProcess.getId()));
            if( !whiteList.containsKey(id) ) whiteList.put(id, new HashSet<BPMNNode>());
        }

        if( node instanceof SubProcess ) {
            //System.out.println("Duplicating subProcess: " + node.getId());
            BPMNNode src, tgt, osrc, otgt;
            boolean mark;
            HashMap<BPMNNode, BPMNNode> mapping = new HashMap<>();

            duplicate = diagram.addSubProcess( node.getLabel(),
                    ((SubProcess) node).isBLooped(),
                    ((SubProcess) node).isBAdhoc(),
                    ((SubProcess) node).isBCompensation(),
                    ((SubProcess) node).isBMultiinstance(),
                    ((SubProcess) node).isBCollapsed(),
                    ((SubProcess) node).getTriggeredByEvent(),
                    parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((SubProcess) duplicate).setDecorator(((SubProcess) node).getDecorator());

            //System.out.println("("+ diagram.getFlows((SubProcess) node).size() +")STARTING>>> flows duplication for: " + duplicate.getId() + " : " + node.getId());
            for( Flow flow : diagram.getFlows((SubProcess) node) ) {
                src = osrc = flow.getSource();
                tgt = otgt = flow.getTarget();

                if( !mapping.containsKey(osrc) ) {
					/* checking if these nodes have been duplicated or not */
                    mark = false;
                    if( matrices.containsKey(osrc) ) {
                        src = matrices.get(osrc);
                        mark = blackList.containsKey(src.getId());
                    }
                    src = duplicateNode(src, (SubProcess) duplicate, mark);
                    mapping.put(osrc, src);
                } else src = mapping.get(osrc);

                if( !mapping.containsKey(otgt) ) {
                    mark = false;
                    if (matrices.containsKey(otgt)) {
                        tgt = matrices.get(otgt);
                        mark = blackList.containsKey(tgt.getId());
                    }
                    tgt = duplicateNode(tgt, (SubProcess) duplicate, mark);
                    mapping.put(otgt, tgt);
                } else tgt = mapping.get(otgt);

                if( src == null || tgt == null ) return null;
                diagram.addFlow(src, tgt, flow.getLabel());
                //System.out.println("Added Flow: " + src.getId() + " > " + tgt.getId());
            }
        } else if( node instanceof Activity ) {
            duplicate = diagram.addActivity( node.getLabel(),
                    ((Activity) node).isBLooped(),
                    ((Activity) node).isBAdhoc(),
                    ((Activity) node).isBCompensation(),
                    ((Activity) node).isBMultiinstance(),
                    ((Activity) node).isBCollapsed(),
                    parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Activity) duplicate).setDecorator(((Activity) node).getDecorator());

        } else if( node instanceof CallActivity ) {
            duplicate = diagram.addCallActivity( node.getLabel(),
                    ((CallActivity) node).isBLooped(),
                    ((CallActivity) node).isBAdhoc(),
                    ((CallActivity) node).isBCompensation(),
                    ((CallActivity) node).isBMultiinstance(),
                    ((CallActivity) node).isBCollapsed(),
                    parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((CallActivity) duplicate).setDecorator(((CallActivity) node).getDecorator());

        } else if( node instanceof Event ) {
            duplicate = diagram.addEvent( node.getLabel(),
                    ((Event) node).getEventType(),
                    ((Event) node).getEventTrigger(),
                    ((Event) node).getEventUse(),
                    parentProcess,
                    true,
                    null);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Event) duplicate).setDecorator(((Event) node).getDecorator());

            if( endEvents.contains(node) ) endEvents.add((Event) duplicate);
            if( startEvents.contains(node) ) startEvents.add((Event) duplicate);
            if( matrices.containsKey(node) ) matrices.put(duplicate, matrices.get(node));

			/* this should happen only during a subProcess duplication, because BPstruct is supposed to do not duplicate end Events */
            if( fakeEndEvents.contains(node) ) fakeEndEvents.add((Event) duplicate);
            if( fakeStartEvents.contains(node) ) fakeStartEvents.add((Event) duplicate);

        } else if( node instanceof Gateway ) {
            duplicate = diagram.addGateway( node.getLabel(),
                    ((Gateway) node).getGatewayType(),
                    parentProcess);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Gateway) duplicate).setMarkerVisible(((Gateway) node).isMarkerVisible());
            ((Gateway) duplicate).setDecorator(((Gateway) node).getDecorator());
        }

        if( duplicate != null ) {
            if( blackMark ) blackList.get(id).add(duplicate);
            else whiteList.get(id).add(duplicate);
            matrices.put(duplicate, node);
        }

        //System.out.println("Added new node (" + duplicate.getClass().getSimpleName() + "): " + duplicate.getId());
        return duplicate;
    }

    private void restoreEdges() {
        BPMNNode src, tgt;
        String srcID, tgtID;

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : unmappableEdges ) {
            src = e.getSource();
            tgt = e.getTarget();

            srcID = src.getId().toString();
            tgtID = tgt.getId().toString();

            if( blackList.containsKey(srcID) || blackList.containsKey(tgtID) ||
                    whiteList.containsKey(srcID) || whiteList.containsKey(tgtID) ) continue;

            if( unmappableNodes.contains(src) ) unmappableNodes.remove(src);
            if( unmappableNodes.contains(tgt) ) unmappableNodes.remove(tgt);

            if( e instanceof MessageFlow) diagram.addMessageFlow(src, tgt, (Swimlane) null, e.getLabel());
            else if( e instanceof Flow ) diagram.addFlow(src, tgt, e.getLabel());
            else if( e instanceof Association) diagram.addAssociation(src, tgt, ((Association) e).getDirection());
            else if( e instanceof DataAssociation ) diagram.addDataAssociation(src, tgt, e.getLabel());

            System.out.println(e.getClass().getSimpleName() + " restored: " + src.getId() + " > " + tgt.getId());
        }

        for( BPMNNode n : unmappableNodes ) {
            System.out.println("Removing unmappable node: " + n.getClass().getSimpleName() + " : " + n.getId());
            removeNode(n);
        }
    }

    private void restoreEvents() {
        Event de;
        Activity a;
        BPMNNode ca;
        Gateway g;
        boolean error;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> startFlow;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> endFlow;
        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> fakeFlows;
        Set<Event> removableFakeStarts = new HashSet<>();
        Set<Event> removableFakeEnds = new HashSet<>();

        String aID, eID;

		/* Restoring other boundary events */
        for( Event e : boundToFix.keySet() ) {
            a = boundToFix.get(e);

            aID = a.getId().toString();
            eID = e.getId().toString();

            if( compensationActivities.containsKey(e) ) {
                if( blackList.containsKey(aID) )
                    for( BPMNNode bn : blackList.get(aID) ) {
                        de = (Event) duplicateNode(e, bn.getParentSubProcess(), false);
                        ca = duplicateNode(compensationActivities.get(e), bn.getParentSubProcess(), false);
                        de.setExceptionFor((Activity)bn);
                        diagram.addAssociation(de, ca, BpmnAssociation.AssociationDirection.ONE);
                    }

                if( whiteList.containsKey(aID) )
                    for( BPMNNode wn : whiteList.get(aID) ) {
                        de = (Event) duplicateNode(e, wn.getParentSubProcess(), false);
                        ca = duplicateNode(compensationActivities.get(e), wn.getParentSubProcess(), false);
                        de.setExceptionFor((Activity)wn);
                        diagram.addAssociation(de, ca, BpmnAssociation.AssociationDirection.ONE);
                    }
            } else {
                if( blackList.containsKey(aID) || blackList.containsKey(eID) ) {
                    e.setExceptionFor(null);
                    System.out.println("[" + blackList.containsKey(aID) + ":" + blackList.containsKey(eID)  + "] unfixable bound between: " + aID + " > " + eID);
                } else if( !whiteList.containsKey(aID) && !whiteList.containsKey(eID) ) {
                    tryToFixBound(e, a, false);
                } else if( whiteList.containsKey(aID) && whiteList.containsKey(eID) ) {
                    tryToFixBound(e, a, false);
                    for( BPMNNode en : whiteList.get(eID) )
                        for( BPMNNode an : whiteList.get(aID) ) tryToFixBound((Event) en, (Activity) an, true);
                } else e.setExceptionFor(null);
            }
        }

		/* Restoring multiple start Events */
        for( Event fse : fakeStartEvents ) {
            //System.out.println("FakeStart : " + fse.getId());
            g = null;
            error = false;
            startFlow = null;
            fakeFlows = new HashSet<>();

            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) //loop on flows to find the fakeGateway of this fakeStartEvent
                if( (f.getSource() == fse) && (f.getTarget() instanceof Gateway) ) {
                    startFlow = f;
                    g = (Gateway) f.getTarget();
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getFlows() ) //loop on flows to find all the fakeStartFlows
                        if( ff.getSource() == g ) {
                            if( startEvents.contains(ff.getTarget()) ) fakeFlows.add(ff);
                            else {
                                error = true;
                                System.out.println("Fixing FSE: " + fse.getId());
                                fixStartEvent(fse);
                                break;
                            }
                        }

                    if( error ) break;
                }

            if( g != null && !error ) {
                System.out.println("Removing FSE: " + fse.getId());
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> fff : fakeFlows ) diagram.removeEdge(fff);
                diagram.removeEdge(startFlow);
                removeNode(g);
                removableFakeStarts.add(fse);
            }
        }
        for( Event rfs : removableFakeStarts ) removeNode(rfs);

		/* Restoring multiple end Events */
        for( Event fee : fakeEndEvents ) {
            //System.out.println("FakeEnd : " + fee.getId());
            g = null;
            error = false;
            endFlow = null;
            fakeFlows = new HashSet<>();

            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getFlows() ) //loop on flows to find the fakeGateway of this fakeEndEvent
                if( (f.getTarget() == fee) && (f.getSource() instanceof Gateway) ) {
                    endFlow = f;
                    g = (Gateway) f.getSource();
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getFlows() ) //loop on flows to find all the fakeEndFlows
                        if( ff.getTarget() == g ) {
                            if( endEvents.contains(ff.getSource()) ) fakeFlows.add(ff);
                            else {
                                error = true;
                                System.out.println("Fixing FEE: " + fee.getId());
                                fixEndEvent(fee);
                                break;
                            }
                        }

                    if( error ) break;
                }

            if( g != null && !error ) {
                System.out.println("Removing FEE: " + fee.getId());
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> fff : fakeFlows) diagram.removeEdge(fff);
                diagram.removeEdge(endFlow);
                removeNode(g);
                removableFakeEnds.add(fee);
            }
        }
        for( Event rfe : removableFakeEnds ) removeNode(rfe);
    }

    private void tryToFixBound(Event e, Activity a, boolean increment) {

        if( !(e.getParentSubProcess() == a.getParentSubProcess()) ) return;
        for( Flow f : diagram.getFlows() )
            if( f.getTarget().equals(e) && (f.getSource() instanceof Gateway) ) {
                diagram.removeEdge(f);
                e.setExceptionFor(a);
                if( increment ) a.incNumOfBoundaryEvents();
                System.out.println("Boundary event restored: " + a.getId() + " > " + e.getId());
                return;
            }
    }

    private void fixStartEvent( Event fakeStart ) {
        Event e;
        String eID;

        for( BPMNNode n : matrices.keySet() )
            if( matrices.get(n) == fakeStart ) {
                e = (Event) n;
                e.setEventType(Event.EventType.INTERMEDIATE);
                eID = e.getId().toString();

                if( blackList.containsKey(eID) )
                    for( BPMNNode bde : blackList.get(eID) )
                        if( bde instanceof Event ) ((Event)bde).setEventType(Event.EventType.INTERMEDIATE);

                if( whiteList.containsKey(eID) )
                    for( BPMNNode wde : whiteList.get(eID) )
                        if( wde instanceof Event ) ((Event)wde).setEventType(Event.EventType.INTERMEDIATE);
            }
    }

    private void fixEndEvent( Event fakeEnd ) {
        Event e;
        String eID;

        for( BPMNNode n : matrices.keySet() )
            if( matrices.get(n) == fakeEnd ) {
                e = (Event) n;
                e.setEventType(Event.EventType.INTERMEDIATE);
                eID = e.getId().toString();

                if( blackList.containsKey(eID) )
                    for( BPMNNode bde : blackList.get(eID) )
                        if( bde instanceof Event ) ((Event)bde).setEventType(Event.EventType.INTERMEDIATE);

                if( whiteList.containsKey(eID) )
                    for( BPMNNode wde : whiteList.get(eID) )
                        if( wde instanceof Event ) ((Event)wde).setEventType(Event.EventType.INTERMEDIATE);
            }
    }

    private void matchGateways(BPMNDiagram diagram) {
            if(diagram == null) return;

            try {
                HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
                HashMap<String, Gateway> gates = new HashMap<String, Gateway>();
                HashSet<String> removed = new HashSet<String>();

                IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
                Vertex src;
                Vertex tgt;

                for (Flow f : diagram.getFlows((Swimlane) null)) {
                    if (!mapping.containsKey(f.getSource())) {
                        src = new Vertex(f.getSource().getId().toString());
                        if (f.getSource() instanceof Gateway) gates.put(f.getSource().getId().toString(), (Gateway) f.getSource());
                        mapping.put(f.getSource(), src);
                    } else src = mapping.get(f.getSource());

                    if (!mapping.containsKey(f.getTarget())) {
                        tgt = new Vertex(f.getTarget().getId().toString());
                        if (f.getTarget() instanceof Gateway) gates.put(f.getTarget().getId().toString(), (Gateway) f.getTarget());
                        mapping.put(f.getTarget(), tgt);
                    } else tgt = mapping.get(f.getTarget());

                    graph.addEdge(src, tgt);
                }

                RPST rpst = new RPST(graph);

                RPSTNode root = rpst.getRoot();
                LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
                toAnalize.add(root);

                while (toAnalize.size() != 0) {
                    root = toAnalize.pollFirst();

                    for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                        switch (n.getType()) {
                            case R:
                                toAnalize.add(n);
                                break;
                            case T:
                                break;
                            case P:
                                toAnalize.add(n);
                                break;
                            case B:
                                Gateway entry = gates.get(n.getEntry().getName());
                                Gateway exit = gates.get(n.getExit().getName());
                                exit.setGatewayType(entry.getGatewayType());
                                toAnalize.add(n);
                                break;
                            default:
                        }
                    }
                    toAnalize.remove(root);
                }

            } catch (Exception e) {
                System.out.println("WARNING = impossible match gateways.");
            }
    }

    private void removeNode(BPMNNode n) {
        diagram.removeNode(n);
        if( n.getParentSubProcess() != null ) n.getParentSubProcess().getChildren().remove(n);
        if( n.getParentSwimlane() != null ) n.getParentSwimlane().getChildren().remove(n);
    }

}
