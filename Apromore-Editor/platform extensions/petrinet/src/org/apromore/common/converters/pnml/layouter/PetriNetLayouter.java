package org.apromore.common.converters.pnml.layouter;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PetriNetLayouter {

    private NetSystem net;
    private PetriNetLayoutMatrix matrix;
    private Map<Node, Bounds> boundsMap = new HashMap<>();

    public PetriNetLayouter(NetSystem net) {
        this.net = net;
    }

    public void layout() {
        matrix = new PetriNetLayoutMatrix();
        buildLayoutMatrix();
        setBounds();
    }

    private void setBounds() {
        Node node;
        for (int row = 0; row < matrix.sizeRows; row++) {
            for (int col = 0; col < matrix.sizeCols; col++) {
                node = matrix.get(row, col);
                if (node != null) {
                    int height;
                    int width;
                    int margin = 80;
                    int x = margin + 100 * col;
                    int y = margin + 100 * row;
                    if (node instanceof Place) {
                        height = 30;
                        width = 30;
                    } else if (node instanceof Transition) {
                        height = 40;
                        width = 80;
                    } else {
                        height = 50;
                        width = 10;
                    }
                    Bounds bounds = new Bounds(new Point(x - width / 2, y - height / 2), new Point(x + width / 2, y + height / 2));
                    boundsMap.put(node, bounds);
                }
            }
        }
    }

    public void buildLayoutMatrix() {
        takeStep(getStartNodes(), 0);
    }

    public void takeStep(Collection<Node> nodes, int step) {
        if (nodes.size() == 0) {
            return;
        }

        int i = 0;
        Collection<Node> nextNodes = new LinkedList<>();
        for (Node node : nodes) {
            matrix.set(i, step, node);
            nextNodes = net.getDirectSuccessors(node);
            i++;
        }

        step++;
        takeStep(nextNodes, step);
    }

    public Collection<Node> getStartNodes() {
        return net.getSourceNodes();
    }

    public Map<Node, Bounds> getBounds() {
        return boundsMap;
    }

    public Bounds getBounds(String id) {
        for (Map.Entry<Node, Bounds> bounds : boundsMap.entrySet()) {
            if (bounds.getKey().getId().equals(id)) {
                return bounds.getValue();
            }
        }
        return null;
    }

}
