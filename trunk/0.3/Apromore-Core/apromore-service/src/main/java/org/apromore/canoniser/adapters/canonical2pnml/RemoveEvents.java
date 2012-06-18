package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsSimpleType;
import org.apromore.pnml.TriggerType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RemoveEvents {
    DataHandler data;
    CanonicalProcessType cproc = new CanonicalProcessType();
    RemoveDuplicateListItems tl = new RemoveDuplicateListItems();
    AnnotationsType annotations = new AnnotationsType();

    public void setValue(AnnotationsType annotations, DataHandler data, CanonicalProcessType cproc) {
        this.data = data;
        this.cproc = cproc;
        this.annotations = annotations;
    }

    public void remove() {
        Map<String, NodeType> nodemap = new HashMap<String, NodeType>();
        List<String> joineventlist = new LinkedList<String>();
        List<String> spliteventlist = new LinkedList<String>();
        Map<String, EdgeType> joinmap = new HashMap<String, EdgeType>();
        Map<String, EdgeType> splitmap = new HashMap<String, EdgeType>();
        List<NodeType> removenodes = new LinkedList<NodeType>();

        for (NetType net : cproc.getNet()) {
            for (EdgeType edge : net.getEdge()) {
                if (edge instanceof EdgeType) {
                    joinmap.put(edge.getSourceId(), edge);
                    splitmap.put(edge.getTargetId(), edge);

                }
            }

            for (NodeType node : net.getNode()) {
                if (node instanceof MessageType) {
                    if (node.getOriginalID() == null) {
                        nodemap.put(node.getName(), node);
                        TriggerType tt = new TriggerType();
                        GraphicsSimpleType gt = new GraphicsSimpleType();
                        DimensionType dt = new DimensionType();
                        dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
                        dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
                        gt.setDimension(dt);
                        tt.setId("");
                        tt.setType(201);
                        tt.setGraphics(gt);
                        data.put_triggermap(node.getName(), tt);
                    }
                } else if (node instanceof TimerType) {
                    if (node.getOriginalID() == null) {
                        nodemap.put(node.getName(), node);
                        TriggerType tt = new TriggerType();
                        GraphicsSimpleType gt = new GraphicsSimpleType();
                        DimensionType dt = new DimensionType();
                        dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
                        dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
                        gt.setDimension(dt);
                        tt.setId("");
                        tt.setType(202);
                        tt.setGraphics(gt);
                        data.put_triggermap(node.getName(), tt);
                    }
                }
            }
            for (NodeType node : net.getNode()) {
                if (node instanceof JoinType) {
                    if (nodemap.containsKey(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        removenodes.add(remove);
                        joineventlist.add(node.getName());
                    }
                } else if (node instanceof SplitType) {
                    if (nodemap.containsKey(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        removenodes.add(remove);
                        spliteventlist.add(node.getName());
                    }
                } else if (node instanceof EventType) {
                    if (nodemap.containsKey(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        removenodes.add(remove);
                        spliteventlist.add(node.getName());
                    }
                }
            }
            for (NodeType node : net.getNode()) {
                if (node instanceof TaskType) {
                    if (joineventlist.contains(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        EdgeType upedge = joinmap.get(remove.getId());
                        upedge.setSourceId(node.getId());
                    } else if (spliteventlist.contains(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        EdgeType upedge = splitmap.get(remove.getId());
                        upedge.setTargetId(node.getId());

                        for (AnnotationType annotation : annotations
                                .getAnnotation()) {
                            if (String.valueOf(annotation.getCpfId()).equals(
                                    String.valueOf(remove.getId()))) {
                                annotation.setCpfId(node.getId());
                            }
                        }
                    }
                }
            }

        }
        for (NetType net : cproc.getNet()) {
            if (removenodes.size() > 0) {
                for (Object obj : removenodes) {
                    net.getNode().remove(obj);
                }
            }
        }
    }

    public CanonicalProcessType getCanonicalProcess() {
        return cproc;
    }

    public AnnotationsType getAnnotations() {
        return annotations;
    }
}
