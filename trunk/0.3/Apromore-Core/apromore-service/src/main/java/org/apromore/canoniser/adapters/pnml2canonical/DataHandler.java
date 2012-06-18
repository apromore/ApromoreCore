package org.apromore.canoniser.adapters.pnml2canonical;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PlaceType;

public class DataHandler {
	Map<String, String> id_map = new HashMap<String, String>();
	Map<String, String> andsplitmap = new HashMap<String, String>();
	Map<String, String> andjoinmap = new HashMap<String, String>();
	Map<String, String> andsplitjoinmap = new HashMap<String, String>();
	Map<String, String> triggermap = new HashMap<String, String>();
	Map<String, ResourceTypeType> resourcemap = new HashMap<String, ResourceTypeType>();
	Map<String, String> xorcounter = new HashMap<String, String>();
	Map<String, Object> objectmap = new HashMap<String, Object>();
	List<ArcType> centerarcs = new LinkedList<ArcType>();
	List<ArcType> updatedarcs = new LinkedList<ArcType>();
	List<NodeType> noderef = new LinkedList<NodeType>();
	List<Object> annotationobjects = new LinkedList<Object>();
	List<PlaceType> center = new LinkedList<PlaceType>();
	List<String> targetvalues = new LinkedList<String>();
	List<String> sourcevalues = new LinkedList<String>();
	List<String> output = new LinkedList<String>();
	List<String> input = new LinkedList<String>();
	AnnotationsType annotations = new AnnotationsType();
    String inputevent;
    String outputstate;
	CanonicalProcessType cproc = new CanonicalProcessType();
	EdgeType inputedge;
	EdgeType outputedge;
	String subnettask;
	long ids = 6121979;
	long resourceid = 10000;
	long rootid;
	File folder;
	NetType net;
	String filename;
	String inputnode;
	String outputnode;

	public void put_id_map(String key, String value) {
		id_map.put(key, value);
	}

	public String get_id_map_value(String key) {

		return (id_map.get(key));
	}

	public Map<String, String> get_id_map() {

		return id_map;
	}

	public void put_objectmap(String key, Object obj) {
		objectmap.put(key, obj);
	}

	public Object get_objectmap_value(String key) {

		return (objectmap.get(key));
	}

	public Map<String, Object> get_objectmap() {

		return objectmap;
	}

	public void put_triggermap(String key, String obj) {
		triggermap.put(key, obj);
	}

	public String get_triggermap_value(String key) {

		return (triggermap.get(key));
	}

	public Map<String, String> get_triggermap() {

		return triggermap;
	}

	public void put_resourcemap(String key, ResourceTypeType obj) {
		resourcemap.put(key, obj);
	}

	public ResourceTypeType get_resourcemap_value(String key) {

		return (resourcemap.get(key));
	}

	public Map<String, ResourceTypeType> get_resourcemap() {

		return resourcemap;
	}

	public void addsourcevalues(String value) {
		sourcevalues.add(value);
	}

	public List<String> getsourcevalues() {

		return sourcevalues;
	}

	public void addtargetvalues(String value) {
		targetvalues.add(value);
	}

	public List<String> gettargetvalues() {

		return targetvalues;
	}

	public void addoutput(String value) {
		output.add(value);
	}

	public List<String> getoutput() {

		return output;
	}

	public void addinput(String value) {
		input.add(value);
	}

	public List<String> getinput() {

		return input;
	}

	public void put_andsplitmap(String key, String value) {
		andsplitmap.put(key, value);
	}

	public String get_andsplitmap_value(String key) {

		return (andsplitmap.get(key));
	}

	public Map<String, String> get_andsplitmap() {

		return andsplitmap;
	}

	public void put_andjoinmap(String key, String value) {
		andjoinmap.put(key, value);
	}

	public String get_andjoinmap_value(String key) {
		return (andjoinmap.get(key));
	}

	public Map<String, String> get_andjoinmap() {
		return andjoinmap;
	}

	public void put_andsplitjoinmap(String key, String value) {
		andsplitjoinmap.put(key, value);
	}

	public String get_andsplitjoinmap_value(String key) {

		return (andsplitjoinmap.get(key));
	}

	public Map<String, String> get_andsplitjoinmap() {

		return andsplitjoinmap;
	}

	public void setInputnode(String node) {
		inputnode = node;
	}

	public String getInputnode() {
		if (inputnode == null) {
			inputnode = "start";
			return inputnode;
		} else {
			return inputnode;
		}
	}

	public void setOutputnode(String node) {
		outputnode = node;
	}

	public String getOutputnode() {
		if (outputnode == null) {
			outputnode = "end";
			return outputnode;
		} else {
			return outputnode;
		}
	}

	public void setOutputEdge(EdgeType edge) {
		outputedge = edge;
	}

	public EdgeType getOutputEdge() {

		return outputedge;

	}

	public void setInputEdge(EdgeType edge) {
		inputedge = edge;
	}

	public EdgeType getInputEdge() {

		return inputedge;

	}

	public void setOutputState(String id) {
		outputstate = id;
	}
	public String getOutputState() {
		return outputstate;

	}

	public void setInputEvent(String id) {
		inputevent = id;
	}

	public String getInputEvent() {
		return inputevent;

	}

	public void setCanonicalProcess(CanonicalProcessType cpt) {
		cproc = cpt;
	}

	public CanonicalProcessType getCanonicalProcess() {

		return cproc;

	}

	public void setAnnotations(AnnotationsType an) {
		annotations = an;
	}

	public AnnotationsType getAnnotations() {

		return annotations;

	}

	public void addNoderef(NodeType an) {
		noderef.add(an);
	}

	public List<NodeType> getNoderef() {

		return noderef;

	}

	public void setIds(long lid) {
		ids = lid;
	}

	public long getIds() {
		return ids;
	}

	public long countIds() {
		ids++;
		return ids;
	}

	public void addAnnotationObject(Object object) {
		annotationobjects.add(object);
	}

	public List<Object> getAnnotationObjects() {

		return annotationobjects;

	}

	public void setRootId(long lid) {
		rootid = lid;
	}

	public long getRootid() {
		return rootid;
	}

	public void setSubnetTask(String tsub) {
		subnettask = tsub;
	}

	public String getSubnetTask() {
		return subnettask;
	}

	public void setFolder(File file) {
		folder = file;
	}

	public File getFolder() {
		return folder;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void addupdatedarc(ArcType value) {
		updatedarcs.add(value);
	}

	public List<ArcType> getupdatedarcs() {
		return updatedarcs;
	}

	public void put_xorcounter(String key, String value) {
		xorcounter.put(key, value);
	}

	public String get_xorcounter_value(String key) {

		return (xorcounter.get(key));
	}

	public Map<String, String> get_xorcounter() {

		return xorcounter;
	}

	public void addCenter(PlaceType place) {
		center.add(place);
	}

	public List<PlaceType> getCenter() {
		return center;
	}

	public void addCenterArc(ArcType arc) {
		centerarcs.add(arc);
	}

	public List<ArcType> getCenterArcs() {
		return centerarcs;
	}

	public void setNet(NetType net) {
		this.net = net;
	}

	public NetType getNet() {
		return net;
	}

	public long getResourceID() {
		return resourceid;
	}

	public void setResourceID(long id) {
		resourceid = id;
	}
}
