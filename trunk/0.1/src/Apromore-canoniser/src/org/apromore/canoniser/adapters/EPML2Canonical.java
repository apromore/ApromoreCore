package org.apromore.canoniser.adapters;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HumanType;
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
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeEvent;
import de.epml.TypeFlow;
import de.epml.TypeFont;
import de.epml.TypeFunction;
import de.epml.TypeLine;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeOR;
import de.epml.TypeObject;
import de.epml.TypeProcessInterface;
import de.epml.TypeRANGE;
import de.epml.TypeRole;
import de.epml.TypeXOR;

public class EPML2Canonical{
	Map<BigInteger, BigInteger> id_map = new HashMap<BigInteger, BigInteger>();
	List<BigInteger> flow_source_id_list = new LinkedList<BigInteger>();
	List<TypeAND> and_list = new LinkedList<TypeAND>();
	List<TypeOR> or_list = new LinkedList<TypeOR>();
	List<TypeXOR> xor_list = new LinkedList<TypeXOR>();
	Map<BigInteger, BigInteger> def_ref = new HashMap<BigInteger, BigInteger>();
	Map<BigInteger, TypeRole> role_ref = new HashMap<BigInteger, TypeRole>();
	Map<BigInteger, TypeObject> obj_ref = new HashMap<BigInteger, TypeObject>();
	List<TaskType> subnet_list = new LinkedList<TaskType>();
	List<BigInteger> range_ids = new LinkedList<BigInteger>();
	
	private CanonicalProcessType cproc = new CanonicalProcessType();
	private AnnotationsType annotations = new AnnotationsType();
	private long ids = 1;
	//
	
	public CanonicalProcessType getCPF()
	{
		return cproc;
	}
	
	public AnnotationsType getANF()
	{
		return annotations;
	}
	
	public EPML2Canonical(TypeEPML epml) throws JAXBException {

	
		if(epml.getDirectory() != null)
		{
			for(int i = 0; i < epml.getDirectory().size(); i++)
			{
				for (TExtensibleElements epc: epml.getDirectory().get(i).getEpcOrDirectory()) {
					if(epc instanceof TypeEPC)
					{
						NetType net = new NetType();
						translateEpc(net,(TypeEPC)epc);
						id_map.put(((TypeEPC) epc).getEpcId(), BigInteger.valueOf(ids));
						net.setId(BigInteger.valueOf(ids++));
						cproc.getNet().add(net);
					}
				}
				for(TaskType task: subnet_list)
					task.setSubnetId(id_map.get(task.getSubnetId()));
			}
		} else {
			// the epml element doesn't have any directory
		}

	}

	private void translateEpc(NetType net, TypeEPC epc)
	{
		for (Object obj: epc.getEventOrFunctionOrRole()) {
			if (obj instanceof TypeEvent) {
				translateEvent(net, (TypeEvent) obj);
				addNodeAnnotations(obj);
			} else if (obj instanceof TypeFunction) {
				translateFunction(net, (TypeFunction) obj);
				addNodeAnnotations(obj);
			} else if(obj instanceof TypeAND || obj instanceof TypeOR || obj instanceof TypeXOR)
			{
				translateGateway(net, obj);
				addNodeAnnotations(obj);
			}
			else if(obj instanceof TypeRole)
			{
				translateRole((TypeRole)obj);
				addNodeAnnotations(obj);
			}
			else if(obj instanceof TypeObject)
			{
				translateObject((TypeObject)obj);
				addNodeAnnotations(obj);
			} 
			else if(obj instanceof TypeRANGE)
			{
				range_ids.add(((TypeRANGE)obj).getId());
				//translateRANGE((TypeRANGE)obj);
				//addNodeAnnotations(obj);
			}
			else if(obj instanceof TypeProcessInterface)
			{
				translatePI(net, (TypeProcessInterface)obj);
				addNodeAnnotations(obj);
			}
		}
		
		
		for (Object obj: epc.getEventOrFunctionOrRole()) {
			if (obj instanceof TypeArc) {
				TypeArc arc = (TypeArc) obj;
				if(arc.getFlow() != null) {
					if(range_ids.contains(arc.getFlow().getSource()) || range_ids.contains(arc.getFlow().getTarget()) )
						System.out.println();
					else
						translateArc(net, arc);
						addEdgeAnnotation(arc);
				}
				else if(arc.getRelation() != null) {
					if(range_ids.contains(arc.getRelation().getSource()) || range_ids.contains(arc.getRelation().getTarget()) )
						System.out.println();
					else
						translateArc(net, arc);
						addEdgeAnnotation(arc);
				}
			}
		}
		
		//process the gateway lists
		int counter;
		for(TypeAND and: and_list)
		{
			counter = 0;
			BigInteger n = and.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				//the and is joint
			{
				ANDJoinType andJ = new ANDJoinType();
				andJ.setId(and.getId());
				andJ.setName(and.getName());
				net.getNode().add(andJ);
			}
			else
				//TODO
				//the and is split, create it
			{
				ANDSplitType andS = new ANDSplitType();
				andS.setId(and.getId());
				andS.setName(and.getName());
				net.getNode().add(andS);
			}
		}
		
		/// make the same for or 
		for(TypeOR or: or_list)
		{
			counter = 0;
			BigInteger n = or.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				//the or is joint
			{
				ORJoinType orJ = new ORJoinType();
				orJ.setId(or.getId());
				orJ.setName(or.getName());
				net.getNode().add(orJ);
			}
			else
				//TODO
				//or is split, create it then remove the events after
			{
				ORSplitType orS = new ORSplitType();
				orS.setId(or.getId());
				orS.setName(or.getName());
				net.getNode().add(orS);
				processUnrequiredEvents(net,or.getId()); // after creating the split node ,, delete the event
			}
		}
		
		// make the same for xor
		for(TypeXOR xor: xor_list)
		{
			counter = 0;
			BigInteger n = xor.getId();
			for(BigInteger s: flow_source_id_list)
				if(n.equals(s))
					counter++;
			if(counter == 1)
				//TODO
				// xor is joint
			{
				XORJoinType xorJ = new XORJoinType();
				xorJ.setId(xor.getId());
				xorJ.setName(xor.getName());
				net.getNode().add(xorJ);
			}
			else
				//TODO
				//xor is split, create it
			{
				XORSplitType xorS = new XORSplitType();
				xorS.setId(xor.getId());
				xorS.setName(xor.getName());
				net.getNode().add(xorS);
				processUnrequiredEvents(net,xor.getId()); // after creating the split node ,, delete the event
			}
		}
		
		// find the edge after the split
		// and remove the event
		//TODO

		
	}


	private void addEdgeAnnotation(TypeArc arc) {
		
		LineType line = new LineType();
		GraphicsType graph = new GraphicsType();
		FontType font = new FontType();
		
		if(arc.getGraphics() != null)
		{
			graph.setCpfId(arc.getId());
			try {
				if(arc.getGraphics().get(0) != null)
				{
					if(arc.getGraphics().get(0).getFont() != null)
					{
						font.setColor(arc.getGraphics().get(0).getFont().getColor());
						font.setDecoration(arc.getGraphics().get(0).getFont().getDecoration());
						font.setFamily(arc.getGraphics().get(0).getFont().getFamily());
						font.setHorizontalAlign(arc.getGraphics().get(0).getFont().getHorizontalAlign());
						font.setRotation(arc.getGraphics().get(0).getFont().getRotation());
						font.setSize(arc.getGraphics().get(0).getFont().getSize());
						font.setStyle(arc.getGraphics().get(0).getFont().getStyle());
						font.setVerticalAlign(arc.getGraphics().get(0).getFont().getVerticalAlign());
						font.setWeight(arc.getGraphics().get(0).getFont().getWeight());
						graph.setFont(font);
					}
					if(arc.getGraphics().get(0).getLine() != null)
					{
						line.setColor(arc.getGraphics().get(0).getLine().getColor());
						line.setShape(arc.getGraphics().get(0).getLine().getShape());
						line.setStyle(arc.getGraphics().get(0).getLine().getStyle());
						line.setWidth(arc.getGraphics().get(0).getLine().getWidth());
						graph.setLine(line);
					}
					
					for(TypeMove2 mov2: arc.getGraphics().get(0).getPosition())
					{
						PositionType pos = new PositionType();
						pos.setX(mov2.getX());
						pos.setY(mov2.getY());
						graph.getPosition().add(pos);
					}
					annotations.getAnnotation().add(graph);
				}
			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println("Index out");
			}
		}
	}

	private void addNodeAnnotations(Object obj) {
		GraphicsType graphT = new GraphicsType();
		LineType line = new LineType();
		FillType fill = new FillType();
		PositionType pos = new PositionType();
		SizeType size = new SizeType();
		FontType font = new FontType();
		BigInteger cpfId = null;
		
		// 
		
		TEpcElement element = (TEpcElement)obj;
		cpfId = id_map.get(element.getId());
		
		if(element.getGraphics() != null)
		{
			if(element.getGraphics().getFill() != null) {
				fill.setColor(element.getGraphics().getFill().getColor());
				fill.setGradientColor(element.getGraphics().getFill().getGradientColor());
				fill.setGradientRotation(element.getGraphics().getFill().getGradientRotation());
				fill.setImage(element.getGraphics().getFill().getImage());
				graphT.setFill(fill);
	
			} 
			
			if(element.getGraphics().getPosition() != null) {
				size.setHeight(element.getGraphics().getPosition().getHeight());
				size.setWidth(element.getGraphics().getPosition().getWidth());
				graphT.setSize(size);
				
				pos.setX(element.getGraphics().getPosition().getX());
				pos.setY(element.getGraphics().getPosition().getY());
				graphT.getPosition().add(pos);
			} 
			
			if(element.getGraphics().getLine() != null) {
				line.setColor(element.getGraphics().getLine().getColor());
				line.setShape(element.getGraphics().getLine().getShape());
				line.setStyle(element.getGraphics().getLine().getStyle());
				line.setWidth(element.getGraphics().getLine().getWidth());
				graphT.setLine(line);
			} 
			
			if(element.getGraphics().getFont() != null) {
				font.setColor(element.getGraphics().getFont().getColor());
				font.setDecoration(element.getGraphics().getFont().getDecoration());
				font.setFamily(element.getGraphics().getFont().getFamily());
				font.setHorizontalAlign(element.getGraphics().getFont().getHorizontalAlign());
				font.setRotation(element.getGraphics().getFont().getRotation());
				font.setSize(element.getGraphics().getFont().getSize());
				font.setStyle(element.getGraphics().getFont().getStyle());
				font.setVerticalAlign(element.getGraphics().getFont().getVerticalAlign());
				font.setWeight(element.getGraphics().getFont().getWeight());
				graphT.setFont(font);
			}

			graphT.setCpfId(cpfId);
			annotations.getAnnotation().add(graphT);
		}
	}

	// should be in the end
	
	private void processUnrequiredEvents(NetType net, BigInteger id)
	{
		List<EdgeType> edge_remove_list = new LinkedList<EdgeType>();
		List<NodeType> node_remove_list = new LinkedList<NodeType>();
		BigInteger event_id;
		for(EdgeType edge: net.getEdge())
			if(edge.getSourceId().equals(id))
			{
				event_id = edge.getTargetId();
				for(EdgeType edge2: net.getEdge())
					if(edge2.getSourceId().equals(edge.getTargetId()))
					{
						edge.setTargetId(edge2.getTargetId());
						edge_remove_list.add(edge2);
						//net.getEdge().remove(edge2);
					}
				
				// delete the unrequired event and set its name as a condition for the edge
				for(NodeType node: net.getNode())
					if(node.getId().equals(event_id))
					{
						edge.setCondition(node.getName());
						node_remove_list.add(node);
						//net.getNode().remove(node);
					}
			}
		
		for(EdgeType edge: edge_remove_list)
			net.getEdge().remove(edge);
		edge_remove_list.clear();
		for(NodeType node: node_remove_list)
			net.getNode().remove(node);
		node_remove_list.clear();
						
	}
	
	private void translateEvent(NetType net, TypeEvent event)
	{
		EventType node = new EventType();
		id_map.put(event.getId(), BigInteger.valueOf(ids));
		node.setId(BigInteger.valueOf(ids++));
		node.setName(event.getName());
		net.getNode().add(node);	
	}
	
	private void translateFunction(NetType net, TypeFunction func)
	{
		TaskType task = new TaskType();
		id_map.put(func.getId(), BigInteger.valueOf(ids));
		task.setId(BigInteger.valueOf(ids++));
		task.setName(func.getName());
		if (func.getToProcess() != null) {
			if (func.getToProcess().getLinkToEpcId() != null) {
				task.setSubnetId(func.getToProcess().getLinkToEpcId());
				subnet_list.add(task);
			}
		}
		net.getNode().add(task);	
	}
	
	private void translatePI(NetType net, TypeProcessInterface pi) {
		TaskType task = new TaskType();
		id_map.put(pi.getId(), BigInteger.valueOf(ids));
		task.setId(BigInteger.valueOf(ids++));
		task.setSubnetId(pi.getToProcess().getLinkToEpcId()); // Will be modified later to the ID for Net
		subnet_list.add(task);
		net.getNode().add(task);
	}
	
	private void translateArc(NetType net, TypeArc arc)
	{
		if(arc.getFlow() != null) // if it is null, that's mean the arc is relation
		{
			EdgeType edge = new EdgeType();
			id_map.put(arc.getId(), BigInteger.valueOf(ids));
			edge.setId(BigInteger.valueOf(ids++));	
			edge.setSourceId(id_map.get(arc.getFlow().getSource()));
			edge.setTargetId(id_map.get(arc.getFlow().getTarget()));
			net.getEdge().add(edge);
			flow_source_id_list.add(edge.getSourceId());
		}
		else if(arc.getRelation() != null)
		{
			for(NodeType node: net.getNode())
			{
				if(node.getId().equals(id_map.get(arc.getRelation().getSource())))
				{
					ObjectRefType ref = new ObjectRefType();
					ref.setObjectId(id_map.get(arc.getRelation().getTarget()));
					ref.setType(InputOutputType.OUTPUT);
					if (obj_ref.get(arc.getRelation().getTarget()) != null) {
						ref.setOptional(obj_ref.get(arc.getRelation().getTarget()).isOptional());
						ref.setConsumed(obj_ref.get(arc.getRelation().getTarget()).isConsumed());
					}
					((WorkType)node).getObjectRef().add(ref);
				} 
				else if(node.getId().equals(id_map.get(arc.getRelation().getTarget()))){
					if(arc.getRelation().getType().equals("role"))
					{
						ResourceTypeRefType ref = new ResourceTypeRefType();
						ref.setResourceTypeId(id_map.get(arc.getRelation().getSource()));
						if (role_ref.get(arc.getRelation().getSource()) != null) {
							ref.setOptional(role_ref.get(arc.getRelation().getSource()).isOptional());
							ref.setQualifier(role_ref.get(arc.getRelation().getSource()).getDescription()); /// update
						}
						((WorkType)node).getResourceTypeRef().add(ref);
					}
					else
					{
						ObjectRefType ref = new ObjectRefType();
						ref.setObjectId(id_map.get(arc.getRelation().getSource()));
						ref.setType(InputOutputType.INPUT);
						ref.setOptional(obj_ref.get(arc.getRelation().getSource()).isOptional());
						ref.setConsumed(obj_ref.get(arc.getRelation().getSource()).isConsumed());
						((WorkType)node).getObjectRef().add(ref);
					}
				}
			}
		}
	}
	
	private void translateGateway(NetType net, Object object)
	{
		id_map.put(((TEpcElement) object).getId(), BigInteger.valueOf(ids));
		((TEpcElement) object).setId(BigInteger.valueOf(ids++));
	
		if (object instanceof TypeAND) {
			and_list.add((TypeAND) object);
		} else if (object instanceof TypeOR) {
			or_list.add((TypeOR) object);
		} else if (object instanceof TypeXOR) {
			xor_list.add((TypeXOR) object);
		}
	}
	
	private void translateObject(TypeObject obj) {
		if(obj.getDefRef() != null && def_ref.get(obj.getDefRef()) != null)
		{
			id_map.put(obj.getId(), def_ref.get(obj.getDefRef()));
		}
		else {	
			ObjectType object = new ObjectType();
			id_map.put(obj.getId(), BigInteger.valueOf(ids));
			object.setId(BigInteger.valueOf(ids));
			object.setName(obj.getName());
			//object.setConfigurable(!obj.getConfigurableObject().equals(null));
			cproc.getObject().add(object);
			def_ref.put(obj.getDefRef(),BigInteger.valueOf(ids++));
		}
		obj_ref.put(obj.getId(), obj);
	}

	private void translateRole(TypeRole role) {
		if(role.getDefRef() != null && def_ref.get(role.getDefRef()) != null)
		{
			id_map.put(role.getId(), def_ref.get(role.getDefRef()));
		}
		else {		
			HumanType obj = new HumanType();
			id_map.put(role.getId(), BigInteger.valueOf(ids));
			obj.setId(BigInteger.valueOf(ids));
			obj.setName(role.getName());
			cproc.getResourceType().add(obj);
			def_ref.put(role.getDefRef(),BigInteger.valueOf(ids++));
		}
		role_ref.put(role.getId(), role);
	}
	
	private void translateRANGE(TypeRANGE obj) {
		ObjectType object = new ObjectType();
		id_map.put(obj.getId(), BigInteger.valueOf(ids));
		object.setId(BigInteger.valueOf(ids++));
		object.setName(obj.getName());
		cproc.getObject().add(object);
		
		// temporary to deal with range elements problem
		TypeObject o = new TypeObject();
		o.setOptional(obj.isOptional());
		obj_ref.put(obj.getId(), o);
	}
}
