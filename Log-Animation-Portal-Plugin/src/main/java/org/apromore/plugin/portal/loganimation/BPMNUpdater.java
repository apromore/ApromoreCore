package org.apromore.plugin.portal.loganimation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/11/17.
 */
public class BPMNUpdater {

    private Set<String> splitGatewayIDs = new HashSet<>();
    private Set<String> joinGatewayIDs = new HashSet<>();
    private Set<String> removedFlowIDs = new HashSet<>();

    public Set<String> getRemovedFlowIDs() {
        return removedFlowIDs;
    }

    public String getUpdatedBPMN(String bpmn, String layout) {
        Map<String, Set<String>> mapSource = new HashMap<>();
        Map<String, Set<String>> mapTarget = new HashMap<>();

        Map<String, ElementLayout> layoutMap = LayoutGenerator.generateLayout(layout);
        bpmn = bpmn.substring(0, bpmn.indexOf("<bpmndi:BPMNShape"));
        String jsonEnding = "</bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>";

        while (bpmn.contains("<exclusiveGateway id=")) {
            String pre = bpmn.substring(0, bpmn.indexOf("<exclusiveGateway id="));
            String mid = bpmn.substring(bpmn.indexOf("<exclusiveGateway id=") + 22, bpmn.indexOf("</exclusiveGateway>") + 19);
            String id = mid.substring(0, mid.indexOf("\""));
            if(mid.contains("Diverging")) splitGatewayIDs.add(id);
            else joinGatewayIDs.add(id);
            String post = bpmn.substring(bpmn.indexOf("</exclusiveGateway>") + 19);
            bpmn = pre + post;
        }

        for(String split : splitGatewayIDs) {
            String target = "targetRef=\"" + split + "\"/>";
            String pre = bpmn.substring(0, bpmn.indexOf(target) + target.length());
            String flow = pre.substring(pre.lastIndexOf("<sequenceFlow"));
            String source_node = flow.substring(flow.indexOf("sourceRef=\"") + 11, flow.indexOf("targetRef") - 2);

            String flowId = getFlowID(flow);
            removedFlowIDs.add(flowId);
            bpmn = bpmn.replace(flow, "");

            String ref = "sourceRef=\"" + split;
            String tmp = bpmn;
            Set<String> sources = new HashSet<>();
            while(tmp.contains(ref)) {
                tmp = tmp.substring(tmp.indexOf(ref) - 69);
                sources.add(getFlowID(tmp));
                tmp = tmp.substring(tmp.indexOf("/>"));
            }
            mapSource.put(flowId, sources);
            bpmn = bpmn.replaceAll(ref, "sourceRef=\"" + source_node);
        }

        for(String join : joinGatewayIDs) {
            String source = "sourceRef=\"" + join + "\"";
            String pre = bpmn.substring(0, bpmn.indexOf(source));
            pre = pre.substring(pre.lastIndexOf("<sequenceFlow"));
            String post = bpmn.substring(bpmn.indexOf(source));
            post = post.substring(0, post.indexOf("/>") + 2);
            String flow = pre + post;
            String target_node = flow.substring(flow.indexOf("targetRef=\"") + 11, flow.indexOf("/>") - 1);

            String flowId = getFlowID(flow);
            removedFlowIDs.add(flowId);
            bpmn = bpmn.replace(flow, "");

            String ref = "targetRef=\"" + join;
            String tmp = bpmn;
            Set<String> targets = new HashSet<>();
            while(tmp.contains(ref)) {
                tmp = tmp.substring(tmp.indexOf(ref) - 123);
                targets.add(getFlowID(tmp));
                tmp = tmp.substring(tmp.indexOf("/>"));
            }
            mapTarget.put(flowId, targets);
            bpmn = bpmn.replaceAll("targetRef=\"" + join, "targetRef=\"" + target_node);
        }

        String startId = null;
        String endId = null;
        for(String elementID : getBPMNElementIDs(bpmn)) {
            String taskName = getBPMNElementName(bpmn, elementID);

            if(taskName.equals("|&gt;")) {
                startId = elementID;
            }else if(taskName.equals("[]")) {
                endId = elementID;
            }

            ElementLayout elementLayout = layoutMap.get(taskName);

            String shape = createBPMNShape(elementID, elementLayout.getWidth(), elementLayout.getHeight(), elementLayout.getX(), elementLayout.getY());
            bpmn += shape;
        }

        for(String flowID : getFlowIDs(bpmn)) {
            String sourceID = getSourceID(bpmn, flowID);
            String targetID = getTargetID(bpmn, flowID);

            String sourceName = getBPMNElementName(bpmn, sourceID);
            String targetName = getBPMNElementName(bpmn, targetID);

            ElementLayout sourceLayout = layoutMap.get(sourceName);
            ElementLayout targetLayout = layoutMap.get(targetName);

            double source_x = sourceLayout.getX() + (sourceLayout.getWidth() / 2);
            double source_y = sourceLayout.getY() + (sourceLayout.getHeight() / 2);
            double target_x = targetLayout.getX() + (targetLayout.getWidth() / 2);
            double target_y = targetLayout.getY() + (targetLayout.getHeight() / 2);

            boolean bend_point = false;
            if(sourceLayout.getX() == targetLayout.getX() && sourceLayout.getY() == targetLayout.getY()) {
                bend_point = true;
            }else {
                for(String flowID2 : getFlowIDs(bpmn)) {
                    if(sourceID.equals(getTargetID(bpmn, flowID2)) && targetID.equals(getSourceID(bpmn, flowID2))) {
                        bend_point = true;
                        break;
                    }
                }
            }

            String edge = createBPMNEdge(flowID, source_x, source_y, target_x, target_y, bend_point);
            bpmn += edge;
        }

        for(String elementID : getBPMNElementIDs(bpmn)) {
            if(!elementID.equals(startId) && !elementID.equals(endId)) {
                String element = getBPMNElement(bpmn, elementID);
                String taskName = getBPMNElementName(bpmn, elementID);

                String extensionElements = "<extensionElements>" +
                        "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + layoutMap.get(taskName).getElementColor() + "\"/>" +
                        "</extensionElements></task>";
                String element2 = element.replace("/>", ">");
                element2 += extensionElements;
                bpmn = bpmn.replace(element, element2);
            }
        }

        String element = getBPMNElement(bpmn, startId);
        String extensionElements = "<extensionElements>" +
                "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, startId)).getElementColor() + "\"/>" +
                "<signavio:signavioMetaData metaKey=\"bordercolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, startId)).getElementColor() + "\"/>" +
                "</extensionElements></startEvent>";
        String element2 = element.replace("|&gt;", "");
        element2 = element2.replace("/>", ">");
        element2 += extensionElements;
        bpmn = bpmn.replace(element, element2);
        Set<String> outgoingArcs = getOutgoingEdgesIDs(bpmn, startId);
        if(outgoingArcs.size() == 1) {
            String edge = getBPMNEdge(bpmn, (String) outgoingArcs.toArray()[0]);
            String wayPoint = getLastBPMNWayPoint(edge);
            String shape = getBPMNShape(bpmn, startId);

            Double y = (Double.parseDouble(wayPoint.substring(wayPoint.indexOf(" y=\"") + 4, wayPoint.indexOf("\"/>"))) - 15);

            String shape2 = shape.substring(0, shape.indexOf(" y=\"") + 4) + y + shape.substring(shape.indexOf("\" width"));
            bpmn = bpmn.replaceAll(shape, shape2);

            wayPoint = getFirstBPMNWayPoint(edge);
            String wayPoint2 = wayPoint.substring(0, wayPoint.indexOf(" y=\"") + 4) + (y + 15) + "\"/>";
            String edge2 = edge.replaceAll(wayPoint, wayPoint2);
            bpmn = bpmn.replaceAll(edge, edge2);

        }

        element = getBPMNElement(bpmn, endId);
        extensionElements = "<extensionElements>" +
                "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, endId)).getElementColor() + "\"/>" +
                "<signavio:signavioMetaData metaKey=\"bordercolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, endId)).getElementColor() + "\"/>" +
                "</extensionElements></endEvent>";
        element2 = element.replace("[]", "");
        element2 = element2.replace("/>", ">");
        element2 += extensionElements;
        bpmn = bpmn.replace(element, element2);
        Set<String> incomingArcs = getIncomingEdgesIDs(bpmn, endId);
        if(incomingArcs.size() == 1) {
            String edge = getBPMNEdge(bpmn, (String) incomingArcs.toArray()[0]);

            String wayPoint = getFirstBPMNWayPoint(edge);
            String shape = getBPMNShape(bpmn, endId);

            Double x = (Double.parseDouble(wayPoint.substring(wayPoint.indexOf(" x=\"") + 4, wayPoint.indexOf("\" y=\""))));
            Double y = (Double.parseDouble(wayPoint.substring(wayPoint.indexOf(" y=\"") + 4, wayPoint.indexOf("\"/>"))) - 15);

            String sourceID = getSourceID(bpmn, (String) incomingArcs.toArray()[0]);
            Double min_offset = getMinXOffsetBPMNShape(bpmn, sourceID);

            String shape2;
            if(x < min_offset) {
                shape2 = shape.substring(0, shape.indexOf(" x=\"") + 4) + (min_offset + 100) + "\" y=\"" + y + shape.substring(shape.indexOf("\" width"));
            }else {
                shape2 = shape.substring(0, shape.indexOf(" y=\"") + 4) + y + shape.substring(shape.indexOf("\" width"));
            }
            bpmn = bpmn.replaceAll(shape, shape2);

            wayPoint = getLastBPMNWayPoint(edge);
            String wayPoint2;
            if(x < min_offset) {
                wayPoint2 = wayPoint.substring(0, wayPoint.indexOf(" x=\"") + 4) + (min_offset + 115) + "\" y=\"" + (y + 15) + "\"/>";
            }else {
                wayPoint2 = wayPoint.substring(0, wayPoint.indexOf(" y=\"") + 4) + (y + 15) + "\"/>";
            }
            String edge2 = edge.replaceAll(wayPoint, wayPoint2);
            bpmn = bpmn.replaceAll(edge, edge2);
        }

        bpmn += jsonEnding;
        bpmn = bpmn.replace("xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"", "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:signavio=\"http://www.signavio.com\"");
        return bpmn;
    }

    private String getFlowID(String flow) {
        return flow.substring(flow.indexOf("id=") + 4, flow.indexOf("\" name"));
    }

    private Set<String> getBPMNElementIDs(String bpmn) {
        Set<String> ids = new HashSet<>();
        String tmp = bpmn;
        String startEvent = "<startEvent id=\"";
        String endEvent = "<endEvent id=\"";
        String task = "<task id=\"";
        String post = "/>";
        String name = "\" name";
        while (tmp.contains(startEvent) || tmp.contains(endEvent) || tmp.contains(task)) {
            String intro;
            if(tmp.contains(startEvent)) {
                intro = startEvent;
            }else if(tmp.contains(endEvent)) {
                intro = endEvent;
            }else {
                intro = task;
            }
            String pre = tmp.substring(tmp.indexOf(intro));
            String element = pre.substring(0, pre.indexOf(post) + post.length());
            String id = element.substring(element.indexOf(intro) + intro.length(), element.indexOf(name));
            ids.add(id);
            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }
        return ids;
    }

    private String getBPMNElementName(String bpmn, String elementID) {
        String pre = "<task id=\"" + elementID;
        String task = null;
        if(bpmn.contains(pre)) {
            pre = bpmn.substring(bpmn.indexOf(pre));
            String post = "/>";
            task = pre.substring(pre.indexOf("name") + 6, pre.indexOf(post) - 1);
        }else {
            pre = "<startEvent id=\"" + elementID;
            if(bpmn.contains(pre)) {
                pre = bpmn.substring(bpmn.indexOf(pre));
                String post = "isInterrupting=\"false\"/>";
                task = pre.substring(pre.indexOf("name") + 6, pre.indexOf(post) - 2);
            }else {
                pre = "<endEvent id=\"" + elementID;
                pre = bpmn.substring(bpmn.indexOf(pre));
                String post = "isInterrupting=\"false\"/>";
                task = pre.substring(pre.indexOf("name") + 6, pre.indexOf(post) - 2);
            }
        }
        task = task.replace("\\u0027", "");
        return task;
    }

    private String createBPMNShape(String shapeId, double width, double height, double x, double y) {
        return "<bpmndi:BPMNShape bpmnElement=\"" +
                shapeId +
                "\"><dc:Bounds x=\"" +
                x +
                "\" y=\"" +
                y +
                "\" width=\"" +
                width +
                "\" height=\"" +
                height +
                "\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape>";
    }

    private Set<String> getFlowIDs(String bpmn) {
        Set<String> ids = new HashSet<>();
        String tmp = bpmn;
        while (tmp.contains("<sequenceFlow")) {
            String intro = "<sequenceFlow";
            String pre = tmp.substring(tmp.indexOf(intro));
            String post = "/>";
            String edge = pre.substring(0, pre.indexOf(post) + post.length());
            String id = edge.substring(edge.indexOf(intro) + intro.length() + 5, edge.indexOf("\" name"));
            ids.add(id);
            tmp = tmp.substring(tmp.indexOf(edge) + edge.length());
        }
        return ids;
    }

    private String getSourceID(String bpmn, String flowID) {
        String intro = "<sequenceFlow id=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "\"/>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        String sourceRef = "sourceRef=\"";
        String targetRef = "\" targetRef=\"";
        String id = edge.substring(edge.indexOf(sourceRef) + sourceRef.length(), edge.indexOf(targetRef));
        return id;
    }

    private String getTargetID(String bpmn, String flowID) {
        String intro = "<sequenceFlow id=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "\"/>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        String targetRef = "targetRef=\"";
        String id = edge.substring(edge.indexOf(targetRef) + targetRef.length(), edge.indexOf(post));
        return id;
    }

    private String createBPMNEdge(String flowID, double source_x, double source_y, double target_x, double target_y, boolean bent_point) {
        String edge = "<bpmndi:BPMNEdge bpmnElement=\"" +
                flowID +
                "\"><di:waypoint x=\"" +
                source_x +
                "\" y=\"" +
                source_y +
                "\"/>";

        if(bent_point) {
            double x = ((source_x + target_x) / 2);
            double y = ((source_y + target_y) / 2);

            if(source_x < target_x) {
                if(source_y < target_y) {
                    x += 30;
                    y -= 30;
                }else if(source_y > target_y) {
                    x -= 30;
                    y -= 30;
                }else {
                    y -= 30;
                }
            }else if(source_x > target_x) {
                if(source_y < target_y) {
                    x += 30;
                    y += 30;
                }else if(source_y > target_y) {
                    x -= 30;
                    y += 30;
                }else {
                    y += 30;
                }
            }else {
                if(source_y < target_y) {
                    x += 30;
                }else if(source_y > target_y) {
                    x -= 30;
                }else {
                    y -= 50;
                    x += 30;
                    edge += "<di:waypoint x=\"" +
                            x +
                            "\" y=\"" +
                            y +
                            "\"/>";
                    x -= 60;
                }
            }

            edge += "<di:waypoint x=\"" +
                    x +
                    "\" y=\"" +
                    y +
                    "\"/>";
        }

        edge += "<di:waypoint x=\"" +
                target_x +
                "\" y=\"" +
                target_y +
                "\"/></bpmndi:BPMNEdge>";

        return edge;
    }

    private String getBPMNElement(String bpmn, String elementID) {
        String pre = "<task id=\"" + elementID;
        String post = "/>";
        if(bpmn.contains(pre)) {
            pre = bpmn.substring(bpmn.indexOf(pre));
        }else {
            pre = "<startEvent id=\"" + elementID;
            if(bpmn.contains(pre)) {
                pre = bpmn.substring(bpmn.indexOf(pre));
            }else {
                pre = "<endEvent id=\"" + elementID;
                pre = bpmn.substring(bpmn.indexOf(pre));
            }
        }
        return pre.substring(0, pre.indexOf(post) + post.length());
    }

    private Set<String> getOutgoingEdgesIDs(String bpmn, String elementID) {
        Set<String> flowIDs = getFlowIDs(bpmn);
        Set<String> outgoingEdgesIDs = new HashSet<>();
        for(String flowID : flowIDs) {
            if(getSourceID(bpmn, flowID).equals(elementID)) {
                outgoingEdgesIDs.add(flowID);
            }
        }
        return outgoingEdgesIDs;
    }

    private String getBPMNEdge(String bpmn, String flowID) {
        String intro = "<bpmndi:BPMNEdge bpmnElement=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "</bpmndi:BPMNEdge>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        return edge;
    }

    private String getLastBPMNWayPoint(String edge) {
        String intro = "<di:waypoint x=\"";
        String pre = edge.substring(edge.lastIndexOf(intro));
        String post = "/>";
        String waypoint = pre.substring(0, pre.indexOf(post) + post.length());
        return waypoint;
    }

    private String getBPMNShape(String bpmn, String elementID) {
        String pre = "<bpmndi:BPMNShape bpmnElement=\"" + elementID;
        String post = "</bpmndi:BPMNShape>";
        pre = bpmn.substring(bpmn.indexOf(pre));
        return pre.substring(0, pre.indexOf(post) + post.length());
    }

    private String getFirstBPMNWayPoint(String edge) {
        String intro = "<di:waypoint x=\"";
        String pre = edge.substring(edge.indexOf(intro));
        String post = "/>";
        String waypoint = pre.substring(0, pre.indexOf(post) + post.length());
        return waypoint;
    }

    private Set<String> getIncomingEdgesIDs(String bpmn, String elementID) {
        Set<String> flowIDs = getFlowIDs(bpmn);
        Set<String> incomingEdgesIDs = new HashSet<>();
        for(String flowID : flowIDs) {
            if(getTargetID(bpmn, flowID).equals(elementID)) {
                incomingEdgesIDs.add(flowID);
            }
        }
        return incomingEdgesIDs;
    }

    private Double getMinXOffsetBPMNShape(String bpmn, String elementID) {
        String pre = "<bpmndi:BPMNShape bpmnElement=\"" + elementID;
        String post = "</bpmndi:BPMNShape>";
        pre = bpmn.substring(bpmn.indexOf(pre));
        String shape = pre.substring(0, pre.indexOf(post) + post.length());
        Double x = Double.parseDouble(shape.substring(shape.indexOf("x=\"") + 3, shape.indexOf("\" y=\"")));
        Double width = Double.parseDouble(shape.substring(shape.indexOf("width=\"") + 7, shape.indexOf("\" height=\"")));
        return x + width;
    }
}
