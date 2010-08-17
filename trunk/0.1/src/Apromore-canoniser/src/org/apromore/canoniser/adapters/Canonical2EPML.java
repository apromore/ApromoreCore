/**
 * Canonical2EPML is a class for converting an CanonicalProcessType
 *  object into a TypeEPML object.
 * A Canonical2EPML object encapsulates the state of the main
 * component resulted from the canonization process.  This
 * state information includes the TypeEpml object which hold a header
 * for the rest of the EPML elements.
 * <p>
 * 
 *  
                    
@author      Abdul
 *  
                    
@version     %I%, %G%
 *  
                    
@since       1.0
 */

package org.apromore.canoniser.adapters;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

import de.epml.TEpcElement;
import de.epml.TExtensibleElements;
import de.epml.TypeAND;
import de.epml.TypeArc;
import de.epml.TypeAttrTypes;
import de.epml.TypeCFunction;
import de.epml.TypeDefinition;
import de.epml.TypeDefinitions;
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
import de.epml.TypeObject;
import de.epml.TypePosition;
import de.epml.TypeProcessInterface;
import de.epml.TypeRelation;
import de.epml.TypeRole;
import de.epml.TypeToProcess;
import de.epml.TypeXOR;

public class Canonical2EPML {
	Map<BigInteger, BigInteger> id_map = new HashMap<BigInteger, BigInteger>();
	List<BigInteger> event_list = new LinkedList<BigInteger>();
	Map<BigInteger, NodeType> nodeRefMap = new HashMap<BigInteger, NodeType>();
	Map<BigInteger, EdgeType> edgeRefMap = new HashMap<BigInteger, EdgeType>();
	Map<BigInteger, Object> epcRefMap = new HashMap<BigInteger, Object>();
	List<BigInteger> object_res_list = new LinkedList<BigInteger>();
	Map<BigInteger, List<BigInteger>> role_map = new HashMap<BigInteger, List<BigInteger>>();
	List<TypeFunction> subnet_list = new LinkedList<TypeFunction>();
	List<TypeProcessInterface> pi_list = new LinkedList<TypeProcessInterface>();
	
	List<TypeFlow> flow_list = new LinkedList<TypeFlow>();
	
	private TypeEPML epml = new TypeEPML();
	private TypeDirectory dir = new TypeDirectory();
	private long ids = System.currentTimeMillis();;
	private long defIds = 1;
	
	public TypeEPML getEPML()
	{
		return epml;
	}
	
	public Canonical2EPML(CanonicalProcessType cproc, AnnotationsType annotations) throws JAXBException {
		main(cproc);
		mapNodeAnnotations(annotations);
		mapEdgeAnnotations(annotations);
	}
	
	public Canonical2EPML(CanonicalProcessType cproc) throws JAXBException {
		main(cproc);
	}
	
	private void main(CanonicalProcessType cproc)
	{
		epml.getDirectory().add(dir);
		epml.setDefinitions(new TypeDefinitions());
	
		for (NetType net: cproc.getNet()) {
			// To do
			TypeEPC epc = new TypeEPC();
			epc.setEpcId(BigInteger.valueOf(ids++));
			translateNet(epc,net);
			for (ObjectType obj: cproc.getObject()){
				if(object_res_list.contains(obj.getId()))
					translateObject(obj,epc);
			}
			for (ResourceTypeType resT: cproc.getResourceType()){
				if(object_res_list.contains(resT.getId()))
					translateResource(resT,epc);
			}
			createRelationArc(epc,net);
			object_res_list.clear();
			validate_event_sequence(epc);
			validate_function_sequence(epc);
			epml.getDirectory().get(0).getEpcOrDirectory().add(epc);
		}
		
		for(TypeFunction func: subnet_list)
			func.getToProcess().setLinkToEpcId(id_map.get(func.getToProcess().getLinkToEpcId()));
		for(TypeProcessInterface pi: pi_list)
			pi.getToProcess().setLinkToEpcId(id_map.get(pi.getToProcess().getLinkToEpcId()));
	}
	
	private void translateNet(TypeEPC epc, NetType net)
	{
		
		for (NodeType node: net.getNode()) {
			if(node instanceof TaskType || node instanceof EventType)
			{
				if (node instanceof TaskType) {
					translateTask(epc, (TaskType) node);
				} else if (node instanceof EventType) {
					translateEvent(epc, node);
				}
				for(ObjectRefType ref : ((WorkType)node).getObjectRef())
				{
					object_res_list.add(ref.getObjectId());
				}
				List<BigInteger> ll = new LinkedList<BigInteger>();
				for(ResourceTypeRefType ref : ((WorkType)node).getResourceTypeRef())
				{
					object_res_list.add(ref.getResourceTypeId());
					ll.add(ref.getResourceTypeId());
				}
				role_map.put(epc.getEpcId(),ll);
				
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
				{		
				if(id_map.get(edge.getTargetId())!= null) {
					TypeArc arc = new TypeArc();
					TypeFlow flow = new TypeFlow();
					id_map.put(edge.getId(), BigInteger.valueOf(ids));
					arc.setId(BigInteger.valueOf(ids++));
					flow.setSource(id_map.get(edge.getSourceId()));
					flow.setTarget(id_map.get(edge.getTargetId()));
					flow_list.add(flow);
					arc.setFlow(flow);
					epc.getEventOrFunctionOrRole().add(arc);
					epcRefMap.put(arc.getId(), arc);
					}
				else {
					id_map.put(edge.getTargetId(),id_map.get(edge.getSourceId()));
					}
				}
			}
		}
		
		
	}
	
	private void createRelationArc(TypeEPC epc,  NetType net)
	{
		for(NodeType node: net.getNode())
		{
			if(node instanceof WorkType)
			{
				for(ObjectRefType ref:((WorkType)node).getObjectRef())
				{
					if(ref.getObjectId() != null)
					{
						TypeArc arc = new TypeArc();
						TypeRelation rel =  new TypeRelation();
						arc.setId(BigInteger.valueOf(ids++));
						if(ref.getType().equals(InputOutputType.OUTPUT)){
							rel.setSource(id_map.get(node.getId()));
							rel.setTarget(id_map.get(ref.getObjectId()));
						}
						else{
							rel.setTarget(id_map.get(node.getId()));
							rel.setSource(id_map.get(ref.getObjectId()));
						}					
						arc.setRelation(rel);
						epc.getEventOrFunctionOrRole().add(arc);
					}
				}
				
				for(ResourceTypeRefType ref:((WorkType)node).getResourceTypeRef())
				{
					if(ref.getResourceTypeId() != null)
					{
						TypeArc arc = new TypeArc();
						TypeRelation rel =  new TypeRelation();
						arc.setId(BigInteger.valueOf(ids++));
						rel.setSource(id_map.get(node.getId()));
						rel.setTarget(id_map.get(ref.getResourceTypeId()));
						rel.setType("role");
						arc.setRelation(rel);
						epc.getEventOrFunctionOrRole().add(arc);
					}
				}
			}
		}
	}
	
	private void translateTask(TypeEPC epc, TaskType task)
	{
		if(task.getName() == null && task.getSubnetId() != null)
		{
			TypeProcessInterface pi = new TypeProcessInterface();
			pi.setToProcess(new TypeToProcess());
			pi.getToProcess().setLinkToEpcId(task.getSubnetId());
			pi_list.add(pi);
		}
		else {
			TypeFunction func = new TypeFunction();
			id_map.put(task.getId(), BigInteger.valueOf(ids));
			func.setId(BigInteger.valueOf(ids++));
			func.setName(task.getName());
			func.setDefRef(find_def_id("function",func.getName()));
			if(task.getSubnetId() != null)
			{
				func.getToProcess().setLinkToEpcId(task.getSubnetId());
				subnet_list.add(func);
			}
			epc.getEventOrFunctionOrRole().add(func);	
			epcRefMap.put(func.getId(), func);
		}
	}
	
	private void translateEvent(TypeEPC epc, NodeType node)
	{
		TypeEvent event = new TypeEvent();
		id_map.put(node.getId(), BigInteger.valueOf(ids));
		event.setId(BigInteger.valueOf(ids++));
		event.setName(node.getName());
		event.setDefRef(find_def_id("event",event.getName()));
		epc.getEventOrFunctionOrRole().add(event);	
		epcRefMap.put(event.getId(), event);
		
	}
	
	private void translateObject(ObjectType obj, TypeEPC epc)
	{
		TypeObject object = new TypeObject();
		id_map.put(obj.getId(), BigInteger.valueOf(ids));
		object.setId(BigInteger.valueOf(ids++));
		object.setName(obj.getName());
		object.setDefRef(find_def_id("object",object.getName()));
		object.setFinal(obj.isConfigurable());
		epc.getEventOrFunctionOrRole().add(object);
	}
	
	private void translateResource(ResourceTypeType resT, TypeEPC epc)
	{
		TypeRole role = new TypeRole();
		id_map.put(resT.getId(), BigInteger.valueOf(ids));
		role.setId(BigInteger.valueOf(ids++));
		role.setName(resT.getName());
		role.setDefRef(find_def_id("role",role.getName()));
		epc.getEventOrFunctionOrRole().add(role);
		
		// Linking the related element
		
		List<TypeArc> arcs_list = new LinkedList<TypeArc>();
		
		for(Object obj: epc.getEventOrFunctionOrRole())
		{
			List<BigInteger> ll = new LinkedList<BigInteger>();
			if(obj instanceof TypeArc)
				ll = role_map.get(((TypeArc)obj).getId());
			else
				ll = role_map.get(((TEpcElement)obj).getId());
			
			if(ll != null)
			{
				
				if(obj instanceof TypeFunction)
				{
					if(ll.contains(resT.getId()))
					{
						TypeArc arc1 = new TypeArc();
						TypeRelation rel = new TypeRelation();
						rel.setSource(role.getId());
						rel.setTarget(((TypeFunction)obj).getId());
						arc1.setRelation(rel);
						arcs_list.add(arc1);
					}
				}
				/*else if(obj instanceof TypeEvent)
				{
					if(ll.contains(resT.getId()))
					{
						TypeArc arc2 = new TypeArc();
						TypeRelation rel = new TypeRelation();
						rel.setSource(role.getId());
						rel.setTarget(((TypeEvent)obj).getId());
						arc2.setRelation(rel);
						arcs_list.add(arc2);
					}
				}*/
			}
		}
		
		for(TypeArc arc: arcs_list)
			epc.getEventOrFunctionOrRole().add(arc);
		
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
					flow_list.add(flow);
					arc.setFlow(flow);
					
					TypeArc arc2 = new TypeArc();
					TypeFlow flow2 = new TypeFlow();
					arc2.setId(BigInteger.valueOf(ids++));
					flow2.setSource(id_map.get(edge.getSourceId()));
					flow2.setTarget(n);
					flow_list.add(flow2);
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
		
		event_list.clear();
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
					if(cGraphInfo.getPosition() != null && cGraphInfo.getPosition().size() > 0)
					{
						pos.setX(cGraphInfo.getPosition().get(0).getX());
						pos.setY(cGraphInfo.getPosition().get(0).getY());
					}
					graphics.setPosition(pos);
					
					Object obj = epcRefMap.get(id_map.get(cid));
					if(obj != null)
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
	
	private void validate_event_sequence(TypeEPC epc)
	{
		for(Object obj : epc.getEventOrFunctionOrRole())
		{
			if(obj instanceof TypeEvent)
			{
				if(event_validate((TEpcElement) obj))
				{
					TypeFunction func = new TypeFunction();
					func.setName("");
					func.setId(BigInteger.valueOf(ids++));
					TypeArc arc = new TypeArc();
					TypeFlow flow = new TypeFlow();
					arc.setFlow(flow);
					flow.setSource(func.getId());
					arc.setId(BigInteger.valueOf(ids++));
					
					for(TypeFlow f: flow_list)
					{
						if(f.getSource() == ((TypeEvent)obj).getId())
						{
							flow.setTarget(f.getTarget());
							f.setTarget(flow.getSource());
						}
					}
					
					epcRefMap.put(func.getId(), func);
					flow_list.add(flow);
				}
			}
		}
	}
	
	private boolean event_validate(TEpcElement obj)
	{
		boolean flag = true;
		List<BigInteger> target_list = new LinkedList<BigInteger>();
		
		for(TypeFlow flow : flow_list)
		{
			if(flow.getSource() == obj.getId())
			{
				flag = false;
				target_list.add(flow.getTarget());
			}
		}
		
		if(flag)
			return false;
		else if(obj instanceof TypeFunction)
			return false;
		else if(obj instanceof TypeEvent)
			return true;
		else
		{
			for(BigInteger id: target_list)
				return event_validate((TEpcElement) epcRefMap.get(id));
		}
		
		return false;
	}
	
	// validating functions sequence
	private void validate_function_sequence(TypeEPC epc)
	{
		for(Object obj : epc.getEventOrFunctionOrRole())
		{
			if(obj instanceof TypeFunction)
			{
				if(function_validate((TEpcElement) obj))
				{
					TypeEvent event = new TypeEvent();
					event.setName("");
					event.setId(BigInteger.valueOf(ids++));
					TypeArc arc = new TypeArc();
					TypeFlow flow = new TypeFlow();
					arc.setFlow(flow);
					flow.setSource(event.getId());
					arc.setId(BigInteger.valueOf(ids++));
					
					for(TypeFlow f: flow_list)
					{
						if(f.getSource() == ((TypeFunction)obj).getId())
						{
							flow.setTarget(f.getTarget());
							f.setTarget(flow.getSource());
						}
					}
					
					epcRefMap.put(event.getId(), event);
					flow_list.add(flow);
				}
			}
		}
	}
	
	private boolean function_validate(TEpcElement obj)
	{
		boolean flag = true;
		List<BigInteger> target_list = new LinkedList<BigInteger>();
		
		for(TypeFlow flow : flow_list)
		{
			if(flow.getSource() == obj.getId())
			{
				flag = false;
				target_list.add(flow.getTarget());
			}
		}
		
		if(flag)
			return false;
		else if(obj instanceof TypeFunction)
			return true;
		else if(obj instanceof TypeEvent)
			return false;
		else
		{
			for(BigInteger id: target_list)
				return function_validate((TEpcElement) epcRefMap.get(id));
		}
		
		return false;
	}
	
	private BigInteger find_def_id(String type, String name)
	{
		
		for(TExtensibleElements def: epml.getDefinitions().getDefinitionOrSpecialization())
		{
			if(def instanceof TypeDefinition)
			{
				if(((TypeDefinition) def).getType() == type && ((TypeDefinition) def).getName() == name)
				{
					return ((TypeDefinition) def).getDefId();
				}
			}
		}
			
		TypeDefinition def = new TypeDefinition();
		def.setDefId(BigInteger.valueOf(defIds++));
		def.setType(type);
		def.setName(name);
		epml.getDefinitions().getDefinitionOrSpecialization().add(def);
		
		return def.getDefId();
		
	}
}
