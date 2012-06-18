package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsSimpleType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.TransitionResourceType;
import org.apromore.pnml.TriggerType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RemoveConnectorTasks {
    DataHandler data;
    CanonicalProcessType cproc = new CanonicalProcessType();
    RemoveDuplicateListItems tl = new RemoveDuplicateListItems();

    public void setValue(DataHandler data, CanonicalProcessType cproc) {
        this.data = data;
        this.cproc = cproc;
    }

    public void remove() {

        Map<String, NodeType> nodemap = new HashMap<String, NodeType>();
        Map<String, EdgeType> joinmap = new HashMap<String, EdgeType>();
        Map<String, EdgeType> splitmap = new HashMap<String, EdgeType>();
        List<NodeType> removenodes = new LinkedList<NodeType>();
        List<EdgeType> removeedges = new LinkedList<EdgeType>();

        for (NetType net : cproc.getNet()) {
            for (EdgeType edge : net.getEdge()) {
                if (edge instanceof EdgeType) {
                    joinmap.put(edge.getSourceId(), edge);
                    splitmap.put(edge.getTargetId(), edge);
                    if (edge.getOriginalID() == null) {
                        removeedges.add(edge);
                    }

                }
            }

            for (NodeType node : net.getNode()) {
                if (node instanceof TaskType) {
                    if (node.getOriginalID() == null) {
                        nodemap.put(node.getName(), node);
                        if (((TaskType) node).getResourceTypeRef().size() > 0) {
                            TriggerType tt = new TriggerType();
                            GraphicsSimpleType gt = new GraphicsSimpleType();
                            DimensionType dt = new DimensionType();
                            dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
                            dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
                            gt.setDimension(dt);
                            tt.setId("");
                            tt.setType(200);
                            tt.setGraphics(gt);
                            data.put_triggermap(node.getName(), tt);
                            for (Object resource : ((WorkType) node)
                                    .getResourceTypeRef()) {
                                if (resource instanceof ResourceTypeRefType) {
                                    if (((ResourceTypeRefType) resource)
                                            .getResourceTypeId() != null) {
                                        if (data.get_resourcemap()
                                                .containsKey(
                                                        String.valueOf(((ResourceTypeRefType) resource)
                                                                .getResourceTypeId()))) {
                                            ResourceTypeType res = data
                                                    .get_resourcemap_value(String
                                                            .valueOf(((ResourceTypeRefType) resource)
                                                                    .getResourceTypeId()));
                                            String[] split = res.getName()
                                                    .split("-");
                                            split[0] = split[0].substring(0);
                                            split[1] = split[1].substring(0);
                                            OrganizationUnitType ui = new OrganizationUnitType();
                                            ui.setName(split[0]);
                                            RoleType ro = new RoleType();
                                            ro.setName(split[1]);
                                            TransitionResourceType tres = new TransitionResourceType();
                                            tres.setOrganizationalUnitName(ui);
                                            tres.setRoleName(ro);
                                            GraphicsSimpleType gtres = new GraphicsSimpleType();
                                            DimensionType dtres = new DimensionType();
                                            dtres.setX(BigDecimal.valueOf(Long
                                                    .valueOf(60)));
                                            dtres.setY(BigDecimal.valueOf(Long
                                                    .valueOf(22)));
                                            gtres.setDimension(dt);
                                            tres.setGraphics(gtres);
                                            data.put_resourcepositionmap(
                                                    node.getName(), tres);
                                            if (!data.getunit().contains(
                                                    split[0])) {
                                                data.addunit(split[0]);
                                            }
                                            if (!data.getroles().contains(
                                                    split[1])) {
                                                data.addroles(split[1]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (node instanceof MessageType) {
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
                        EdgeType upedge = joinmap.get(remove.getId());
                        upedge.setSourceId(node.getId());
                        removenodes.add(remove);
                        // net.getNode().remove(remove);
                        if (node instanceof ANDJoinType) {
                            data.put_andjoinmap(node.getName(),
                                    (ANDJoinType) node);
                        } else if (node instanceof XORJoinType) {
                            data.put_xorjoinmap(node.getName(),
                                    (XORJoinType) node);
                        }

                    }
                } else if (node instanceof SplitType) {
                    if (nodemap.containsKey(node.getName())) {
                        NodeType remove = nodemap.get(node.getName());
                        EdgeType upedge = splitmap.get(remove.getId());
                        upedge.setTargetId(node.getId());
                        removenodes.add(remove);
                        if (node instanceof ANDSplitType) {
                            data.put_andsplitmap(node.getName(),
                                    (ANDSplitType) node);
                        } else if (node instanceof XORSplitType) {
                            data.put_xorsplitmap(node.getName(),
                                    (XORSplitType) node);
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
            if (removeedges.size() > 0) {
                for (Object obj : removeedges) {
                    net.getEdge().remove(obj);
                }
            }
        }
    }

    public CanonicalProcessType getCanonicalProcess() {
        return cproc;
    }
}
