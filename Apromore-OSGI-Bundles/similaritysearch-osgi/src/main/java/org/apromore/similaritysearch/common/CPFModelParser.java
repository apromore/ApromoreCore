/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.similaritysearch.common;

import com.processconfiguration.Configurable;
import com.processconfiguration.ConfigurationAnnotation;
import com.processconfiguration.Variants;
import org.apromore.cpf.*;
import org.apromore.similaritysearch.graph.*;
import org.apromore.similaritysearch.graph.Vertex.GWType;
import org.apromore.similaritysearch.graph.Vertex.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CPFModelParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CPFModelParser.class.getName());

    public static Graph readModel(CanonicalProcessType cpf) {
        Graph epcGraph = new Graph();
        for(TypeAttribute attribute : cpf.getAttribute()) {
            if(attribute.getName().equals("ProcessName")) {
                epcGraph.name = attribute.getValue();
                epcGraph.ID = attribute.getValue();
                break;
            }
        }

        NetType mainNet = null;
        for (NetType n : cpf.getNet()) {
            if (n.getNode().size() > 0) {
                mainNet = n;
                break;
            }
        }

        if (mainNet == null) {
            return epcGraph;
        }

        addNodes(mainNet, epcGraph);
        addEdges(mainNet, epcGraph);
        epcGraph.linkVertices();

        // read references and objects
        addObjectsAndResources(cpf, epcGraph);

        return epcGraph;
    }

    public static List<Graph> readModels(CanonicalProcessType cpf) {
        ArrayList<Graph> l = new ArrayList<Graph>();

        for (NetType mainNet : cpf.getNet()) {
            if (mainNet.getNode().size() > 0) {
                Graph epcGraph = new Graph();

                addNodes(mainNet, epcGraph);
                addEdges(mainNet, epcGraph);
                epcGraph.linkVertices();

                // read references and objects
                addObjectsAndResources(cpf, epcGraph);
                l.add(epcGraph);
            }
        }
        return l;
    }

    private static void addObjectsAndResources(CanonicalProcessType cpf, Graph epcGraph) {
        for (NetType net : cpf.getNet()) {
            for (ObjectType o : net.getObject()) {
                epcGraph.addObject(
                        new VertexObject(o.getId(), o.getName(), o.isConfigurable(),
                                o instanceof HardType ? VertexObject.SoftHart.Hard : (o instanceof SoftType ? VertexObject.SoftHart.Soft : VertexObject.SoftHart.Other))
                );
            }
        }

        for (ResourceTypeType r : cpf.getResourceType()) {
            epcGraph.addResource(new VertexResource(r.getId(), r.getName(), r.isConfigurable(),
                    r instanceof HumanType ? VertexResource.Type.Human : (r instanceof NonhumanType ? VertexResource.Type.NonHuman : VertexResource.Type.Other)));
        }
    }

    private static void addNodes(NetType mainNet, Graph epcGraph) {
        for (NodeType n : mainNet.getNode()) {
            HashMap<String, String> annotationMap = parseAnnotationForEventsAndFunctions(getFromAnnotations("configurationAnnotation", n.getAttribute()));
            // gateways
            if (n instanceof RoutingType && !(n instanceof StateType)) {
                boolean initialGW = true;
                String initial = getFromAnnotations("added", n.getAttribute());
                if (initial != null && "true".equals(initial)) {
                    initialGW = false;
                }
                Vertex v = new Vertex(
                        (n instanceof XORJoinType || n instanceof XORSplitType) ? "xor" :
                                ((n instanceof ANDJoinType || n instanceof ANDSplitType) ? "and" :
                                        "or"),
                        n.getId());

                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                // this is initial gateway
                // TODO if we don't have graph that is already merged, then
                // set all gateways to initials
                if (initialGW) {
                    v.setInitialGW();
                }
                if (n.isConfigurable() != null && n.isConfigurable()) {
                    v.setConfigurable(true);
                }

                epcGraph.addVertex(v);
            } else if (n instanceof TaskType) {
                Vertex v = new Vertex(Type.function, n.getName(), n.getId());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof EventType) {
                Vertex v = new Vertex(Type.event, n.getName(), n.getId());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }

                addResourcesAndObjects((WorkType) n, v);

                epcGraph.addVertex(v);
            } else if (n instanceof StateType) {
                Vertex v = new Vertex(Type.state, n.getName(), n.getId());
                // add annotations
                if (annotationMap != null) {
                    v.addAnnotations(annotationMap);
                }
                epcGraph.addVertex(v);
            }
        }
    }

    private static void addResourcesAndObjects(WorkType n, Vertex v) {
        // add resources
        for (ResourceTypeRefType r : n.getResourceTypeRef()) {
            v.resourceRefs.add(new VertexResourceRef(
                    r.getResourceTypeId(),
                    r.getQualifier(),
                    parseModelsFromAnnotations(getFromAnnotations("configurationAnnotation", r.getAttribute()))));
        }

        // add objects
        for (ObjectRefType o : n.getObjectRef()) {
            v.objectRefs.add(new VertexObjectRef(
                    o.isOptional(),
                    o.getObjectId(),
                    o.isConsumed(),
                    (o.getType().equals(InputOutputType.INPUT) ?
                            VertexObjectRef.InputOutput.Input :
                            VertexObjectRef.InputOutput.Output),
                    parseModelsFromAnnotations(getFromAnnotations("configurationAnnotation", o.getAttribute()))));
        }
    }

    private static String getFromAnnotations(String typeRef, List<TypeAttribute> attributes) {
        for (TypeAttribute a : attributes) {
            if (a.getName().equals(typeRef)) {
                return a.getValue();
            }
        }
        return "";
    }

    // the annotation attribute for events and functions are in format
    // model1:name in model1;model2:name in modelass 2;
    // the names must not contain ';', otherwise the name parsing fails
    private static HashMap<String, String> parseAnnotationForEventsAndFunctions(String nodeValue) {
        HashMap<String, String> annotationMap = new HashMap<String, String>();
        if (nodeValue == null) {
            return annotationMap;
        }

        StringTokenizer st = new StringTokenizer(nodeValue, ";");
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            int colon = nextToken.indexOf(':');
            String modelName = nextToken.substring(0, colon);
            String value = nextToken.substring(colon + 1, nextToken.length());
            annotationMap.put(modelName, value);
        }

        return annotationMap;
    }

    private static void addEdges(NetType mainNet, Graph epcGraph) {

        List<EdgeType> edges = mainNet.getEdge();
        HashSet<String> graphLabels = new HashSet<String>();
        HashSet<String> allGraphLabels = new HashSet<String>();

        // add elements
        for (EdgeType e : edges) {
            Edge toAdd = new Edge(e.getSourceId(), e.getTargetId(), e.getId());

            String annotations = getFromAnnotations("configurationAnnotation", e.getAttribute());

            graphLabels = parseModelsFromAnnotations(annotations);
            toAdd.addLabels(graphLabels);
            allGraphLabels.addAll(graphLabels);
            epcGraph.addEdge(toAdd);
        }

        if (allGraphLabels.size() > 0) {
            epcGraph.setGraphConfigurable();
            epcGraph.addGraphLabels(allGraphLabels);
        }
    }

    private static HashSet<String> parseModelsFromAnnotations(String annotations) {
        HashSet<String> graphLabels = new HashSet<String>();
        if (annotations != null && annotations.length() > 0) {
            StringTokenizer st = new StringTokenizer(annotations, ";");
            while (st.hasMoreTokens()) {
                String l = st.nextToken();
                graphLabels.add(l);
            }
        }
        return graphLabels;
    }

    public static CanonicalProcessType writeModel(Graph g, IdGeneratorHelper idGenerator) {
        CanonicalProcessType toReturn = new CanonicalProcessType();
        TypeAttribute nameAttribute = new TypeAttribute();
        nameAttribute.setName("ProcessName");
        nameAttribute.setValue("merged");
        toReturn.getAttribute();

        // objects and resources
        for (VertexObject o : g.getObjects().values()) {
            ObjectType ot = new ObjectType();
            if (o.getSofthard().equals(VertexObject.SoftHart.Soft)) {
                ot = new SoftType();
            } else if (o.getSofthard().equals(VertexObject.SoftHart.Hard)) {
                ot = new HardType();
            }

            ot.setConfigurable(o.isConfigurable());
            ot.setId(o.getId());
            ot.setName(o.getName());
            if(o.isConfigurable()) {
                ot.setConfigurable(true);
//                a.setName("configurationAnnotation");
//                a.setValue(parseAnnotationFromSet(o.getModels()));
//                ot.getAttribute().add(a);
            }
            toReturn.getNet().get(0).getObject().add(ot);
        }

        // objects and resources
        for (VertexResource r : g.getResources().values()) {
            ResourceTypeType rt = new ResourceTypeType();
            if (r.getType().equals(VertexResource.Type.Human)) {
                rt = new HumanType();
            } else if (r.getType().equals(VertexResource.Type.NonHuman)) {
                rt = new NonhumanType();
            }

            rt.setConfigurable(r.isConfigurable());
            rt.setId(r.getId());
            rt.setName(r.getName());

            if(r.isConfigurable()) {
                rt.setConfigurable(true);
            }
//            TypeAttribute a = new TypeAttribute();
//            a.setName("configurationAnnotation");
//            a.setValue(parseAnnotationFromSet(r.getModels()));
//            rt.getAttribute().add(a);
            toReturn.getResourceType().add(rt);
        }

        NetType net = new NetType();
        net.setId(idGenerator.getNextId());
        toReturn.getNet().add(net);

        for (Vertex v : g.getVertices()) {
            NodeType n = new NodeType();
            if (v.getType().equals(Vertex.Type.state)) {
                n = new StateType();
            } else if (v.getType().equals(Vertex.Type.event)) {
                n = new EventType();
            } else if (v.getType().equals(Vertex.Type.function)) {
                n = new TaskType();
            } else if (v.getType().equals(Vertex.Type.gateway)) {
                if (v.getParents().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        n = new ANDJoinType();
                    } else if (v.getGWType().equals(GWType.or)) {
                        n = new ORJoinType();
                    } else if (v.getGWType().equals(GWType.xor)) {
                        n = new XORJoinType();
                    }
                } else if (v.getChildren().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        n = new ANDSplitType();
                    } else if (v.getGWType().equals(GWType.or)) {
                        n = new ORSplitType();
                    } else if (v.getGWType().equals(GWType.xor)) {
                        n = new XORSplitType();
                    }
                }
//                if (v.isAddedGW()) {
//                    TypeAttribute a = new TypeAttribute();
//                    a.setName("added");
//                    a.setValue("true");
//                    n.getAttribute().add(a);
//                }
            }

//            n.setConfigurable(v.isConfigurable());
            n.setId(v.getID());
            n.setName(v.getLabel());

            if(v.isConfigurable()) {
                n.setConfigurable(true);

                TypeAttribute a = new TypeAttribute();
                a.setName("bpmn_cpf/extensions");
                Configurable configurable = new Configurable();
                a.setAny(configurable);
                n.getAttribute().add(a);
            }
//            TypeAttribute a = new TypeAttribute();
//            a.setName("configurationAnnotation");
//            a.setValue(parseAnnotationFromMap(v.getAnnotationMap()));
//            n.getAttribute().add(a);

            if (v.getType().equals(Vertex.Type.event) || v.getType().equals(Vertex.Type.function)) {
                // object ref
                for (VertexObjectRef o : v.objectRefs) {
                    ObjectRefType oRef = new ObjectRefType();
                    if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Input)) {
                        oRef.setType(InputOutputType.INPUT);
                    } else if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Output)) {
                        oRef.setType(InputOutputType.OUTPUT);
                    }
                    oRef.setConsumed(o.getConsumed());
                    oRef.setObjectId(o.getObjectID());
                    oRef.setOptional(o.isOptional());
                    // attributes
//                    TypeAttribute a1 = new TypeAttribute();
//                    a1.setName("configurationAnnotation");
//                    a1.setValue(parseAnnotationFromSet(o.getModels()));
//                    oRef.getAttribute().add(a1);

                    ((WorkType) n).getObjectRef().add(oRef);
                }
                // resource ref
                for (VertexResourceRef r : v.resourceRefs) {
                    ResourceTypeRefType rRef = new ResourceTypeRefType();
                    rRef.setResourceTypeId(r.getResourceID());
                    rRef.setQualifier(r.getQualifier());
                    // attrubutes
//                    TypeAttribute a1 = new TypeAttribute();
//                    a1.setName("configurationAnnotation");
//                    a1.setValue(parseAnnotationFromSet(r.getModels()));
//                    rRef.getAttribute().add(a1);

                    ((WorkType) n).getResourceTypeRef().add(rRef);
                }
            }
            net.getNode().add(n);
        }

        Set<String> setVariants = new HashSet<String>();
        for (Edge e : g.getEdges()) {
            setVariants.addAll(e.getLabels());
        }
        HashMap<String, String> mapVariants = new HashMap<String, String>();
        for(String variant : setVariants) {
            String id = "vid-"+idGenerator.getNextId();
            mapVariants.put(id, variant);
        }

        HashMap<String, Variants.Variant> reverseMapVariants = new HashMap<String, Variants.Variant>();
        TypeAttribute attributeVariants = new TypeAttribute();
        attributeVariants.setName("bpmn_cpf/extensions");
        Variants variants = new Variants();
        for(Map.Entry<String, String> entry : mapVariants.entrySet()) {
            Variants.Variant variant = new Variants.Variant();
            variant.setId(entry.getKey());
            variant.setName(entry.getValue());
            variants.getVariant().add(variant);
            reverseMapVariants.put(entry.getValue(), variant);
        }
        attributeVariants.setAny(variants);
        toReturn.getAttribute().add(attributeVariants);

        for (Edge e : g.getEdges()) {
            EdgeType et = new EdgeType();
            et.setId(e.getId());
            // from and to vertex
            et.setSourceId(e.getFromVertex());
            et.setTargetId(e.getToVertex());
            // attributes
            if(e.getLabels().size() > 0) {
//                boolean afterSplit = false;
                for(NodeType nt : net.getNode()) {
                    if(nt.getId().equals(et.getSourceId()) && nt instanceof RoutingType) {
                        nt.setConfigurable(true);
//                        if(nt instanceof SplitType) {
//                            afterSplit = true;
//                        }
                    }
                    if(nt.getId().equals(et.getTargetId()) && nt instanceof RoutingType) {
                        nt.setConfigurable(true);
                    }
                }
//                if(afterSplit) {
                    TypeAttribute a = new TypeAttribute();
                    a.setName("bpmn_cpf/extensions");
                    ConfigurationAnnotation configurationAnnotation = new ConfigurationAnnotation();
                    for (String variant : e.getLabels()) {
                        ConfigurationAnnotation.Configuration configuration = new ConfigurationAnnotation.Configuration();
                        configuration.setVariantRef(reverseMapVariants.get(variant));
                        configurationAnnotation.getConfiguration().add(configuration);
                    }
                    a.setAny(configurationAnnotation);
                    et.getAttribute().add(a);
//                }
            }

//            TypeAttribute a = new TypeAttribute();
//            a.setName("configurationAnnotation");
//            a.setValue(parseAnnotationFromSet(e.getLabels()));
//            et.getAttribute().add(a);
            net.getEdge().add(et);
        }

        return toReturn;
    }

    private static String parseAnnotationFromMap(
            HashMap<String, String> annotationMap) {
        String toReturn = "";
        for (Map.Entry<String, String> a : annotationMap.entrySet()) {
            toReturn += a.getKey() + ":" + a.getValue() + ";";
        }
        return toReturn;
    }

    private static String parseAnnotationFromSet(HashSet<String> annotationSet) {
        String toReturn = "";
        for (String a : annotationSet) {
            toReturn += a + ";";
        }
        return toReturn;
    }
}
