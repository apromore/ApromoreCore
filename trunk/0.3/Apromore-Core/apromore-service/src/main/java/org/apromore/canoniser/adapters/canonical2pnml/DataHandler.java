package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PnmlType;
import org.apromore.pnml.TransitionResourceType;
import org.apromore.pnml.TransitionType;
import org.apromore.pnml.TriggerType;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataHandler {
    Map<String, String> id_map = new HashMap<String, String>();
    Map<BigInteger, String> originalid_map = new HashMap<BigInteger, String>();
    Map<String, TriggerType> triggermap = new HashMap<String, TriggerType>();
    Map<String, ResourceTypeType> resourcemap = new HashMap<String, ResourceTypeType>();
    Map<String, TransitionResourceType> resourcepositionmap = new HashMap<String, TransitionResourceType>();
    Map<String, NodeType> nodeRefMap = new HashMap<String, NodeType>();
    Map<String, TransitionType> dupjoinMap = new HashMap<String, TransitionType>();
    Map<String, TransitionType> dupsplitMap = new HashMap<String, TransitionType>();
    Map<BigInteger, Integer> xorremoveMap = new HashMap<BigInteger, Integer>();
    Map<BigInteger, TransitionType> xorrefMap = new HashMap<BigInteger, TransitionType>();
    Map<String, EdgeType> edgeRefMap = new HashMap<String, EdgeType>();
    Map<String, ArcType> duparcjoinMap = new HashMap<String, ArcType>();
    Map<String, ArcType> duparcsplitMap = new HashMap<String, ArcType>();
    Map<String, Object> pnmlRefMap = new HashMap<String, Object>();
    Map<BigInteger, Object> duppnmlRefMap = new HashMap<BigInteger, Object>();
    Map<String, String> anno_string = new HashMap<String, String>();
    Map<String, Object> temp_map = new HashMap<String, Object>();
    Map<String, Integer> operatortype = new HashMap<String, Integer>();
    Map<String, NodeType> specialoperators = new HashMap<String, NodeType>();
    Map<String, Integer> specialoperatorscount = new HashMap<String, Integer>();
    Map<String, ANDSplitType> andsplits = new HashMap<String, ANDSplitType>();
    Map<String, ANDJoinType> andjoins = new HashMap<String, ANDJoinType>();
    Map<String, XORSplitType> xorsplits = new HashMap<String, XORSplitType>();
    Map<String, XORJoinType> xorjoins = new HashMap<String, XORJoinType>();

    List<String> targetvalues = new LinkedList<String>();
    List<String> sourcevalues = new LinkedList<String>();
    List<String> output = new LinkedList<String>();
    List<String> input = new LinkedList<String>();
    List<TransitionType> subnets = new LinkedList<TransitionType>();
    List<String> units = new LinkedList<String>();
    List<String> roles = new LinkedList<String>();
    List<NodeType> xorconnectors = new LinkedList<NodeType>();
    org.apromore.pnml.NetType subnet = new org.apromore.pnml.NetType();
    String inputnode = "start";
    String outputnode = "end";
    PnmlType pnml = new PnmlType();
    org.apromore.pnml.NetType net = new org.apromore.pnml.NetType();
    long ids = System.currentTimeMillis();
    String filename;
    AnnotationsType anno = new AnnotationsType();
    List<org.apromore.pnml.NodeType> xors = new LinkedList<org.apromore.pnml.NodeType>();
    List<ArcType> xorarcs = new LinkedList<ArcType>();
    String initialType;

    public void put_id_map(String key, String value) {
        id_map.put(key, value);
    }

    public String get_id_map_value(String key) {
        return (id_map.get(key));
    }

    public Map<String, String> get_id_map() {

        return id_map;
    }

    public void put_originalid_map(BigInteger key, String value) {
        originalid_map.put(key, value);
    }

    public String get_originalid_map_value(BigInteger key) {

        return (originalid_map.get(key));
    }

    public Map<BigInteger, String> get_originalid_map() {

        return originalid_map;
    }

    public void put_triggermap(String key, TriggerType value) {
        triggermap.put(key, value);
    }

    public TriggerType get_triggermap_value(String key) {

        return (triggermap.get(key));
    }

    public Map<String, TriggerType> get_triggermap() {

        return triggermap;
    }

    public void put_resourcemap(String key, ResourceTypeType value) {
        resourcemap.put(key, value);
    }

    public ResourceTypeType get_resourcemap_value(String key) {

        return (resourcemap.get(key));
    }

    public Map<String, ResourceTypeType> get_resourcemap() {

        return resourcemap;
    }

    public void put_resourcepositionmap(String key, TransitionResourceType value) {
        resourcepositionmap.put(key, value);
    }

    public TransitionResourceType get_resourcepositionmap_value(String key) {

        return (resourcepositionmap.get(key));
    }

    public Map<String, TransitionResourceType> get_resourcepositionmap() {

        return resourcepositionmap;
    }

    public void put_nodeRefMap(String key, NodeType value) {
        nodeRefMap.put(key, value);
    }

    public NodeType get_nodeRefMap_value(String key) {
        return (nodeRefMap.get(key));
    }

    public Map<String, NodeType> get_nodeRefMap() {
        return nodeRefMap;
    }

    public void put_edgeRefMap(String key, EdgeType value) {
        edgeRefMap.put(key, value);
    }

    public EdgeType get_edgeRefMap_value(BigInteger key) {
        return (edgeRefMap.get(key));
    }

    public Map<String, EdgeType> get_edgeRefMap() {
        return edgeRefMap;
    }

    public void put_pnmlRefMap(String key, Object value) {
        pnmlRefMap.put(key, value);
    }

    public Object get_pnmlRefMap_value(String key) {
        return (pnmlRefMap.get(key));
    }

    public Map<String, Object> get_pnmlRefMap() {
        return pnmlRefMap;
    }

    public void put_annostring(String key, String value) {
        anno_string.put(key, value);
    }

    public String get_annostring_value(String key) {

        return (anno_string.get(key));
    }

    public Map<String, String> get_annostring() {

        return anno_string;
    }

    public void put_operatortype(String key, Integer value) {
        operatortype.put(key, value);
    }

    public Integer get_operatortype_value(String key) {

        return (operatortype.get(key));
    }

    public Map<String, Integer> get_operatortype() {

        return operatortype;
    }

    public void put_tempmap(String key, Object value) {
        temp_map.put(key, value);
    }

    public Object get_tempmap_value(String key) {

        return (temp_map.get(key));
    }

    public Map<String, Object> get_tempmap() {

        return temp_map;
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

    public void addunit(String value) {
        units.add(value);
    }

    public List<String> getunit() {

        return units;
    }

    public void addroles(String value) {
        roles.add(value);
    }

    public List<String> getroles() {

        return roles;
    }

    public void setPnml(PnmlType pnmlt) {
        pnml = pnmlt;
    }

    public PnmlType getPnml() {
        return pnml;
    }

    public void setNet(org.apromore.pnml.NetType nett) {
        net = nett;
    }

    public org.apromore.pnml.NetType getNet() {
        return net;
    }

    public void addsubnet(org.apromore.pnml.NetType value) {
        subnet = value;
    }

    public org.apromore.pnml.NetType getsubnet() {

        return subnet;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void addxors(org.apromore.pnml.NodeType value) {
        xors.add(value);
    }

    public List<org.apromore.pnml.NodeType> getxors() {

        return xors;
    }

    public void addxorarcs(ArcType value) {
        xorarcs.add(value);
    }

    public List<ArcType> getxorarcs() {

        return xorarcs;
    }

    public void addxorconnectors(NodeType value) {
        xorconnectors.add(value);
    }

    public List<NodeType> getxorconnectors() {

        return xorconnectors;
    }

    public void setAnno(AnnotationsType anno) {
        this.anno = anno;
    }

    public AnnotationsType getAnno() {
        return anno;
    }

    public void put_dupjoinMap(String key, TransitionType value) {
        dupjoinMap.put(key, value);
    }

    public TransitionType get_dupjoinMap_value(String key) {

        return (dupjoinMap.get(key));
    }

    public Map<String, TransitionType> get_dupjoinMap() {

        return dupjoinMap;
    }

    public void put_dupsplitMap(String key, TransitionType value) {
        dupsplitMap.put(key, value);
    }

    public TransitionType get_dupsplitMap_value(String key) {

        return (dupsplitMap.get(key));
    }

    public Map<String, TransitionType> get_dupsplitMap() {

        return dupsplitMap;
    }

    public void put_duparcjoinMap(String key, ArcType value) {
        duparcjoinMap.put(key, value);
    }

    public ArcType get_duparcjoinMap_value(String key) {

        return (duparcjoinMap.get(key));
    }

    public Map<String, ArcType> get_duparcjoinMap() {

        return duparcjoinMap;
    }

    public void put_duparcsplitMap(String key, ArcType value) {
        duparcsplitMap.put(key, value);
    }

    public ArcType get_duparcsplitMap_value(String key) {

        return (duparcsplitMap.get(key));
    }

    public Map<String, ArcType> get_duparcsplitMap() {

        return duparcsplitMap;
    }

    public void put_specialoperators(String key, NodeType value) {
        specialoperators.put(key, value);
    }

    public NodeType get_specialoperators_value(String key) {

        return (specialoperators.get(key));
    }

    public Map<String, NodeType> get_specialoperators() {

        return specialoperators;
    }

    public void put_specialoperatorscount(String key, Integer value) {
        specialoperatorscount.put(key, value);
    }

    public Integer get_specialoperatorscount_value(String key) {

        return (specialoperatorscount.get(key));
    }

    public Map<String, Integer> get_specialoperatorscount() {

        return specialoperatorscount;
    }

    public void put_duppnmlRefMap(BigInteger key, Object value) {
        duppnmlRefMap.put(key, value);
    }

    public Object get_duppnmlRefMap_value(BigInteger key) {

        return (duppnmlRefMap.get(key));
    }

    public Map<BigInteger, Object> get_duppnmlRefMap() {

        return duppnmlRefMap;
    }

    public void put_xorremoveMap(BigInteger key, Integer value) {
        xorremoveMap.put(key, value);
    }

    public Integer get_xorremoveMap_value(BigInteger key) {

        return (xorremoveMap.get(key));
    }

    public Map<BigInteger, Integer> get_xorremoveMap() {

        return xorremoveMap;
    }

    public void put_xorrefMap(BigInteger key, TransitionType value) {
        xorrefMap.put(key, value);
    }

    public TransitionType get_xorrefMap_value(BigInteger key) {

        return (xorrefMap.get(key));
    }

    public Map<BigInteger, TransitionType> get_xorrefMap() {

        return xorrefMap;
    }

    public void put_andjoinmap(String key, ANDJoinType value) {
        andjoins.put(key, value);
    }

    public ANDJoinType get_andjoinmap_value(String key) {

        return (andjoins.get(key));
    }

    public Map<String, ANDJoinType> get_andjoinmap() {

        return andjoins;
    }

    public void put_andsplitmap(String key, ANDSplitType value) {
        andsplits.put(key, value);
    }

    public ANDSplitType get_andsplitmap_value(String key) {

        return (andsplits.get(key));
    }

    public Map<String, ANDSplitType> get_andsplitmap() {

        return andsplits;
    }

    public void put_xorjoinmap(String key, XORJoinType value) {
        xorjoins.put(key, value);
    }

    public XORJoinType get_xorjoinmap_value(String key) {

        return (xorjoins.get(key));
    }

    public Map<String, XORJoinType> get_xorjoinmap() {

        return xorjoins;
    }

    public void put_xorsplitmap(String key, XORSplitType value) {
        xorsplits.put(key, value);
    }

    public XORSplitType get_xorsplitmap_value(String key) {

        return (xorsplits.get(key));
    }

    public Map<String, XORSplitType> get_xorsplitmap() {

        return xorsplits;
    }

    public void setSubnet(TransitionType tran) {
        subnets.add(tran);
    }

    public List<TransitionType> getSubnet() {
        return subnets;
    }

    public void setInitialType(String initialType) {
        this.initialType = initialType;
    }

    public String getInitialType() {
        return initialType;
    }
}