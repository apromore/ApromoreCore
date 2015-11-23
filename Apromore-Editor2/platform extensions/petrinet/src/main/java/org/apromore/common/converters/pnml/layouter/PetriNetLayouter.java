package org.apromore.common.converters.pnml.layouter;

import org.apromore.pnml.NodeType;
import org.apromore.pnml.PnmlType;
import org.oryxeditor.server.diagram.Bounds;

import java.util.HashMap;
import java.util.Map;

public class PetriNetLayouter {

    private PnmlType net;
    private PetriNetLayoutMatrix matrix;
    private Map<NodeType, Bounds> boundsMap = new HashMap<>();

    public PetriNetLayouter(PnmlType net) {
        this.net = net;
    }

    public void layout() {
        matrix = new PetriNetLayoutMatrix();
        //buildLayoutMatrix();
       // setBounds();
    }
//
//    private void setBounds() {
//        NodeType node;
//        for (int row = 0; row < matrix.getRows(); row++) {
//            for (int col = 0; col < matrix.getCols(); col++) {
//                node = matrix.get(row, col);
//                if (node != null) {
//                    int height;
//                    int width;
//                    int margin = 80;
//                    int x = margin + 100 * col;
//                    int y = margin + 100 * row;
//                    if (node instanceof PlaceType) {
//                        height = 30;
//                        width = 30;
//                    } else if (node instanceof TransitionType) {
//                        height = 40;
//                        width = 80;
//                    } else {
//                        height = 50;
//                        width = 10;
//                    }
//                    Bounds bounds = new Bounds(new Point(x - width / 2, y - height / 2), new Point(x + width / 2, y + height / 2));
//                    boundsMap.put(node, bounds);
//                }
//            }
//        }
//    }
//
//    public void buildLayoutMatrix() {
//        takeStep(getStartNodes(), 0);
//    }
//
//    public void takeStep(Collection<NodeType> nodes, int column) {
//        if (nodes.size() == 0) {
//            return;
//        }
//
//        int row = 0;
//        Collection<NodeType> nextNodes = new LinkedList<>();
//        for (NodeType node : nodes) {
//            matrix.set(row, column, node);
//            nextNodes = net.getDirectSuccessors(node);
//            row++;
//        }
//
//        takeStep(nextNodes, ++column);
//    }
//
//    public Collection<NodeType> getStartNodes() {
//        return net.getSourceNodes();
//    }
//
//    public Map<NodeType, Bounds> getBounds() {
//        return boundsMap;
//    }
//
//    public Bounds getBounds(String id) {
//        List<NodeType> startNodes = new LinkedList<>();
//
//        for(PlaceType place : net.getNet().get(0).getPlace()){
//            if(place.getIncomingFlowRelationships().size() == 0){
//                startNodes.add(place);
//            }
//        }
//
//        for(TransitionType transition : net.getNet().get(0).getTransition()){
//            if(transition.getIncomingFlowRelationships().size() == 0){
//                startNodes.add(transition);
//            }
//        }
//
//        return startNodes;
//    }

}
