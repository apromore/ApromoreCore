package ee.ut.bpmn.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import ee.ut.bpmn.BPMNProcess;

public class BPMN2Reader {
        public static BPMNProcess<Element> parse(File file) throws JDOMException, IOException {
                Namespace BPMN2NS = Namespace.getNamespace("http://schema.omg.org/spec/BPMN/2.0");
                Document doc = new SAXBuilder().build(file);

                BPMNProcess<Element> proc = new BPMNProcess<Element>();
                Element procElem = doc.getRootElement().getChild("process", BPMN2NS);
                if (procElem == null) {
                        BPMN2NS = Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");
                        procElem = doc.getRootElement().getChild("process", BPMN2NS);
                }

                initProcess(proc, procElem, BPMN2NS);
                return proc;
        }

        protected static void initProcess(BPMNProcess<Element> proc, Element procElem, Namespace BPMN2NS) {
                Map<String, Integer> nodes = new HashMap<String, Integer>();
                List<Element> edges = new LinkedList<Element>();
                
                for (Object obj : procElem.getChildren())
                        if (obj instanceof Element) {
                                Element elem = (Element) obj;
                                String id = elem.getAttributeValue("id");
                                if (id == null || id.isEmpty())
                                        System.out.println("oops");
                                String name = elem.getAttributeValue("name");
                                if (elem.getName().endsWith("ask") || elem.getName().endsWith("vent")) {
//                                	if (elem.getName().equals("startEvent"))
//                                		name = "_input_";
//                                	if (elem.getName().equals("endEvent") && (name == null || name.isEmpty()))
//                                		name = "_output_";
                                	
                                	nodes.put(id, proc.addTask(name, id, elem));
                                } else if (elem.getName().equals("exclusiveGateway") || elem.getName().equals("eventBasedGateway")) {
                                    nodes.put(id, proc.addXORGateway(name, id, elem));
                                } else if (elem.getName().equals("parallelGateway")) {
                                    nodes.put(id, proc.addANDGateway(name, id, elem));
                                } else if (elem.getName().equals("inclusiveGateway")) {
                                    nodes.put(id, proc.addORGateway(name, id, elem));
                                } else if (elem.getName().equals("sequenceFlow"))
                                        edges.add(elem);
                        }

                for (Element edge : edges) {
                        Integer src = nodes.get(edge.getAttributeValue("sourceRef"));
                        Integer tgt = nodes.get(edge.getAttributeValue("targetRef"));
                        if (src != null && tgt != null) {
                                proc.addEdge(src, tgt, edge);
                        } else {
                        	System.out.println(edge.getAttributeValue("sourceRef"));
                        	System.out.println(edge.getAttributeValue("targetRef"));
                            throw new RuntimeException("Malformed graph");
                        }
                }
        }

}
