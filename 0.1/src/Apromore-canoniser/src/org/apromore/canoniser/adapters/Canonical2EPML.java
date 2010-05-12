package org.apromore.canoniser.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

import de.epml.TEpcElement;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeAttrTypes;
import de.epml.TypeCFunction;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeEvent;
import de.epml.TypeFill;
import de.epml.TypeFlow;
import de.epml.TypeFont;
import de.epml.TypeFunction;
import de.epml.TypeGraphics;
import de.epml.TypeLine;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeOR;
import de.epml.TypePosition;
import de.epml.TypeRole;
import de.epml.TypeXOR;

public class Canonical2EPML {
	Map<BigInteger, BigInteger> id_map = new HashMap<BigInteger, BigInteger>();
	List<BigInteger> event_list = new LinkedList<BigInteger>();
	Map<BigInteger, NodeType> nodeRefMap = new HashMap<BigInteger, NodeType>();
	Map<BigInteger, EdgeType> edgeRefMap = new HashMap<BigInteger, EdgeType>();
	Map<BigInteger, Object> epcRefMap = new HashMap<BigInteger, Object>();
	
	private CanonicalProcessType cproc = null;
	private JAXBContext jaxbContext = null;
	private JAXBContext jaxbContext2 = null;
	private Unmarshaller unmarshaller = null;
	private Marshaller marshaller = null;
	private TypeEPML pkg = new TypeEPML();
	private TypeDirectory dir = new TypeDirectory();
	private long ids = 1;
	FileInputStream fis;
	//
	
	public Canonical2EPML(File file) throws JAXBException {
		jaxbContext = JAXBContext.newInstance("org.apromore.cpf");
		unmarshaller = jaxbContext.createUnmarshaller();
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object o = unmarshaller.unmarshal(fis);
		cproc = (CanonicalProcessType) (((JAXBElement)o).getValue());

		jaxbContext = JAXBContext.newInstance("org.apromore.anf");
		unmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) unmarshaller.unmarshal(new File(file.getPath().replaceAll(".cpf", ".anf")));
		AnnotationsType annotations = anfRootElement.getValue();
		
		pkg.getDirectory().add(dir);
	
		for (NetType net: cproc.getNet()) {
			// To do
			TypeEPC epc = new TypeEPC();
			translateNet(epc,net);
			epc.setEpcId(BigInteger.valueOf(ids++));
			epc.setName("EPC Name");
			pkg.getDirectory().get(0).getEpcOrDirectory().add(epc);
		}
		
		for (ObjectType obj: cproc.getObject()){
			// to do
		}
		
		for (ResourceTypeType resT: cproc.getResourceType()){
			//TO DO 
		}
		
		mapNodeAnnotations(annotations);
		mapEdgeAnnotations(annotations);

		jaxbContext2 = JAXBContext.newInstance("de.epml");
		marshaller = jaxbContext2.createMarshaller();		
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		JAXBElement<TypeEPML> cprocRootElem2 = new de.epml.ObjectFactory().createEpml(pkg);

		
		try {
			marshaller.marshal(cprocRootElem2, new FileOutputStream(new File("models/example222.epml")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void translateNet(TypeEPC epc, NetType net)
	{
		
		for (NodeType node: net.getNode()) {
			if (node instanceof TaskType) {
				translateTask(epc, node);
			} else if (node instanceof EventType) {
				translateEvent(epc, node);
			} else if (node instanceof RoutingType) {
				translateGateway(epc, node);
			} 
			nodeRefMap.put(node.getId(), node);
		}
		
		createEvent(epc,net);
				
		for (EdgeType edge: net.getEdge()) {
			boolean flag = true;
			if(edge.getCondition()==null)
				flag = true;
			else if(edge.getCondition().equals("EPMLEPML"))
				flag = false;
			
			edgeRefMap.put(edge.getId(), edge);			
			if(flag)
			{
				TypeArc arc = new TypeArc();
				TypeFlow flow = new TypeFlow();
				id_map.put(edge.getId(), BigInteger.valueOf(ids));
				arc.setId(BigInteger.valueOf(ids++));
				flow.setSource(id_map.get(edge.getSourceId()));
				flow.setTarget(id_map.get(edge.getTargetId()));
				arc.setFlow(flow);
				epc.getEventOrFunctionOrRole().add(arc);
				epcRefMap.put(arc.getId(), arc);
			}
		}
	
	}
	
	private void translateTask(TypeEPC epc, NodeType node)
	{
		TypeFunction func = new TypeFunction();
		id_map.put(node.getId(), BigInteger.valueOf(ids));
		func.setId(BigInteger.valueOf(ids++));
		func.setName(node.getName());
		epc.getEventOrFunctionOrRole().add(func);	
		epcRefMap.put(func.getId(), func);
	}
	
	private void translateEvent(TypeEPC epc, NodeType node)
	{
		TypeEvent event = new TypeEvent();
		id_map.put(node.getId(), BigInteger.valueOf(ids));
		event.setId(BigInteger.valueOf(ids++));
		event.setName(node.getName());
		epc.getEventOrFunctionOrRole().add(event);	
		epcRefMap.put(event.getId(), event);
	}
	
	private void translateGateway(TypeEPC epc, NodeType node)
	{
		if (node instanceof ANDSplitType) {
			TypeAND and = new TypeAND();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			and.setId(BigInteger.valueOf(ids++));
			and.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(and);
			epcRefMap.put(and.getId(), and);
			//event_list.add(node.getId());
		} else if (node instanceof ANDJoinType) {
			TypeAND and = new TypeAND();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			and.setId(BigInteger.valueOf(ids++));
			and.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(and);
			epcRefMap.put(and.getId(), and);
		} else if (node instanceof XORSplitType) {
			TypeXOR xor = new TypeXOR();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			xor.setId(BigInteger.valueOf(ids++));
			xor.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(xor);
			epcRefMap.put(xor.getId(), xor);
			event_list.add(node.getId());
		} else if (node instanceof XORJoinType) {
			TypeXOR xor = new TypeXOR();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			xor.setId(BigInteger.valueOf(ids++));
			xor.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(xor);	
			epcRefMap.put(xor.getId(), xor);
		} else if (node instanceof ORSplitType) {
			TypeOR or = new TypeOR();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			or.setId(BigInteger.valueOf(ids++));
			or.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(or);	
			epcRefMap.put(or.getId(), or);
			event_list.add(node.getId());
		} else if (node instanceof ORJoinType) {
			TypeOR or = new TypeOR();
			id_map.put(node.getId(), BigInteger.valueOf(ids));
			or.setId(BigInteger.valueOf(ids++));
			or.setName(node.getName());
			epc.getEventOrFunctionOrRole().add(or);	
			epcRefMap.put(or.getId(), or);
		}
	}
	
	private void createEvent(TypeEPC epc,  NetType net)
	{
		BigInteger n;
		

		for(BigInteger id: event_list)
			for (EdgeType edge: net.getEdge()) {
				if(edge.getSourceId().equals(id))
				{
					// 
					n = BigInteger.valueOf(ids++);
					TypeEvent event = new TypeEvent();
					event.setName(edge.getCondition());
					event.setId(n);
					//edge.setTargetId(n);
					
					TypeArc arc = new TypeArc();
					TypeFlow flow = new TypeFlow();
					arc.setId(BigInteger.valueOf(ids++));
					flow.setSource(n);
					flow.setTarget(id_map.get(edge.getTargetId()));
					arc.setFlow(flow);
					
					TypeArc arc2 = new TypeArc();
					TypeFlow flow2 = new TypeFlow();
					arc2.setId(BigInteger.valueOf(ids++));
					flow2.setSource(id_map.get(edge.getSourceId()));
					flow2.setTarget(n);
					arc2.setFlow(flow2);
					
					edge.setCondition("EPMLEPML");
					
					epc.getEventOrFunctionOrRole().add(arc);
					epc.getEventOrFunctionOrRole().add(event);
					epc.getEventOrFunctionOrRole().add(arc2);
					epcRefMap.put(arc.getId(), arc);
					epcRefMap.put(arc2.getId(), arc2);
					epcRefMap.put(event.getId(), event);
				}
			}
	}
	
	/// translate the annotations 
	private void mapNodeAnnotations(AnnotationsType annotations) {
		for (AnnotationType annotation: annotations.getAnnotation()) {
			if (nodeRefMap.containsKey(annotation.getCpfId())) {
				// TODO: Handle 1-N mappings
				BigInteger cid = annotation.getCpfId();
				
				if (annotation instanceof GraphicsType) {
					GraphicsType cGraphInfo = (GraphicsType)annotation;
					TypeGraphics graphics = new TypeGraphics();
					
					if (cGraphInfo.getFill() != null)
					{
						TypeFill fill = new TypeFill();
						fill.setColor(cGraphInfo.getFill().getColor());
						fill.setGradientColor(cGraphInfo.getFill().getGradientColor());
						fill.setGradientRotation(cGraphInfo.getFill().getGradientRotation());
						fill.setImage(cGraphInfo.getFill().getImage());
						graphics.setFill(fill);
					}
					if(cGraphInfo.getFont() != null)
					{
						TypeFont font = new TypeFont();
						font.setColor(cGraphInfo.getFont().getColor());
						font.setDecoration(cGraphInfo.getFont().getDecoration());
						font.setFamily(cGraphInfo.getFont().getFamily());
						font.setHorizontalAlign(cGraphInfo.getFont().getHorizontalAlign());
						font.setRotation(cGraphInfo.getFont().getRotation());
						font.setSize(cGraphInfo.getFont().getSize());
						font.setStyle(cGraphInfo.getFont().getStyle());
						font.setVerticalAlign(cGraphInfo.getFont().getVerticalAlign());
						font.setWeight(cGraphInfo.getFont().getWeight());
						graphics.setFont(font);
					}
					if(cGraphInfo.getLine() != null)
					{
						TypeLine line = new TypeLine();
						line.setColor(cGraphInfo.getLine().getColor());
						line.setShape(cGraphInfo.getLine().getShape());
						line.setStyle(cGraphInfo.getLine().getStyle());
						line.setWidth(cGraphInfo.getLine().getWidth());
						graphics.setLine(line);
					}
					TypePosition pos = new TypePosition();
					if(cGraphInfo.getSize() != null)
					{	
						pos.setHeight(cGraphInfo.getSize().getHeight());
						pos.setWidth(cGraphInfo.getSize().getWidth());
					}
					if(cGraphInfo.getPosition().get(0) != null)
					{
						pos.setX(cGraphInfo.getPosition().get(0).getX());
						pos.setY(cGraphInfo.getPosition().get(0).getY());
					}
					graphics.setPosition(pos);
					
					Object obj = epcRefMap.get(id_map.get(cid));
					((TEpcElement)obj).setGraphics(graphics);
				}
			}			
		}
	}

	private void mapEdgeAnnotations(AnnotationsType annotations) {
		for (AnnotationType annotation: annotations.getAnnotation()) {
			if (edgeRefMap.containsKey(annotation.getCpfId())) {
				// TODO: Handle 1-N mappings
				BigInteger cid = annotation.getCpfId();
				TypeLine line = new TypeLine();
				TypeFont font = new TypeFont();
				
				if (annotation instanceof GraphicsType) {
					GraphicsType cGraphInfo = (GraphicsType)annotation;
					TypeMove move = new TypeMove();
					
					if(cGraphInfo.getFont() != null)
					{
						font.setColor(cGraphInfo.getFont().getColor());
						font.setDecoration(cGraphInfo.getFont().getDecoration());
						font.setFamily(cGraphInfo.getFont().getFamily());
						font.setHorizontalAlign(cGraphInfo.getFont().getHorizontalAlign());
						font.setRotation(cGraphInfo.getFont().getRotation());
						font.setSize(cGraphInfo.getFont().getSize());
						font.setStyle(cGraphInfo.getFont().getStyle());
						font.setVerticalAlign(cGraphInfo.getFont().getVerticalAlign());
						font.setWeight(cGraphInfo.getFont().getWeight());
						move.setFont(font);
					}
					if(cGraphInfo.getLine() != null)
					{
						line.setColor(cGraphInfo.getLine().getColor());
						line.setShape(cGraphInfo.getLine().getShape());
						line.setStyle(cGraphInfo.getLine().getStyle());
						line.setWidth(cGraphInfo.getLine().getWidth());
						move.setLine(line);
					}
					
					for (PositionType pos: cGraphInfo.getPosition()) {
						TypeMove2 m = new TypeMove2();
						m.setX(pos.getX());
						m.setY(pos.getY());
						move.getPosition().add(m);
					}
					
					Object obj = epcRefMap.get(id_map.get(cid));
					if(obj instanceof TypeArc)
						((TypeArc) obj).getGraphics().add(move);
				}
			}			
		}
	}
	
}

