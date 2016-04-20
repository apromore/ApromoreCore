package au.edu.qut.structuring;

import au.edu.qut.structuring.core.StructuringCore;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import au.edu.qut.structuring.ui.iBPStructUI;
import au.edu.qut.structuring.ui.iBPStructUIResult;

import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */
public class StructuringService {

    public StructuringService(){}

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
        BPMNDiagram structuredDiagram;
        long start, end;

        start = System.currentTimeMillis();

        while(  removeDoubleEdges(diagram) || removeFakeGateways(diagram) ||
                collapseSplitGateways(diagram) || collapseJoinGateways(diagram) );

        try {
            iBPStruct spi = new iBPStruct(  StructuringCore.Policy.valueOf(policy),
                                            maxDepth,
                                            maxSolutions,
                                            maxChildren,
                                            maxStates,
                                            maxMinutes,
                                            timeBounded,
                                            keepBisimulation,
                                            forceStructuring);

            spi.setProcess(diagram.getNodes(), diagram.getEdges());
            spi.structure();
            structuredDiagram = spi.getDiagram();
            if(structuredDiagram == null) return diagram;
        } catch(Exception e) {
            System.err.print(e);
            return diagram;
        }

        while(  removeDoubleEdges(structuredDiagram) || removeFakeGateways(structuredDiagram) ||
                collapseSplitGateways(structuredDiagram) || collapseJoinGateways(structuredDiagram) );

        end = System.currentTimeMillis() - start;
        System.out.println("TEST - total structuring time: " + end + " ms");

        return structuredDiagram;
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
        long start, end;
        int gates = diagram.getGateways().size();

        start = System.currentTimeMillis();

        while(  removeDoubleEdges(diagram) || removeFakeGateways(diagram) ||
                collapseSplitGateways(diagram) || collapseJoinGateways(diagram) );

        try {
            iBPStruct spi = new iBPStruct(  result.getPolicy(),
                                            result.getMaxDepth(),
                                            result.getMaxSol(),
                                            result.getMaxChildren(),
                                            result.getMaxStates(),
                                            result.getMaxMinutes(),
                                            result.isTimeBounded(),
                                            result.isKeepBisimulation(),
                                            result.isForceStructuring());

            spi.setProcess(diagram.getNodes(), diagram.getEdges());
            spi.structure();
            structuredDiagram = spi.getDiagram();
            if(structuredDiagram == null) return diagram;
        } catch(Exception e) {
            context.log(e);
            System.err.print(e);
            return diagram;
        }

        while(  removeDoubleEdges(structuredDiagram) || removeFakeGateways(structuredDiagram) ||
                collapseSplitGateways(structuredDiagram) || collapseJoinGateways(structuredDiagram) );

        end = System.currentTimeMillis() - start;
        System.out.println("TEST - gateways: " + gates);
        System.out.println("TEST - total time: " + end + " ms");

        return structuredDiagram;
    }

    private static boolean removeDoubleEdges(BPMNDiagram diagram) {
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

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : towAway ) diagram.removeEdge(f);
        return !towAway.isEmpty();
    }

    private static boolean removeFakeGateways(BPMNDiagram diagram) {
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out;
        HashSet<Gateway> towAway = new HashSet<>();

        for( Gateway g : diagram.getGateways() )
            if( (diagram.getInEdges(g).size() == 1) && (diagram.getOutEdges(g).size() == 1) ) {
                in = (new ArrayList<>(diagram.getInEdges(g))).get(0);
                out = (new ArrayList<>(diagram.getOutEdges(g))).get(0);
                diagram.addFlow(in.getSource(), out.getTarget(), "");
                diagram.removeEdge(in);
                diagram.removeEdge(out);
                towAway.add(g);
            }

        for( Gateway g : towAway ) removeNode(diagram, g);
        return !towAway.isEmpty();
    }

    private static boolean collapseSplitGateways(BPMNDiagram diagram) {
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
                    eatSplit(diagram, meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );

        return !eaten.isEmpty();
    }

    private static boolean collapseJoinGateways(BPMNDiagram diagram) {
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
                    eatJoin(diagram, meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );

        return !eaten.isEmpty();
    }

    private static void eatSplit(BPMNDiagram diagram, Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(eater, e.getTarget(), "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(diagram, meal);
    }

    private static void eatJoin(BPMNDiagram diagram, Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(e.getSource(), eater, "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(diagram, meal);
    }

    private static void removeNode(BPMNDiagram diagram, BPMNNode n) {
        diagram.removeNode(n);
        if( n.getParentSubProcess() != null ) n.getParentSubProcess().getChildren().remove(n);
        if( n.getParentSwimlane() != null ) n.getParentSwimlane().getChildren().remove(n);
    }

}
