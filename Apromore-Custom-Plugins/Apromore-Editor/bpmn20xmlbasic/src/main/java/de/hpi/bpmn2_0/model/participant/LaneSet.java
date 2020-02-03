/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.model.participant;

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tLaneSet complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tLaneSet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}lane" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLaneSet", propOrder = {
        "name",
        "lanes"//,
        //"parentLane"
})
public class LaneSet
        extends BaseElement {

    @XmlElementRef(type = Lane.class)
    protected List<Lane> lanes;

    //	@XmlIDREF
//	@XmlAttribute
    @XmlTransient
    protected Lane parentLane;

    //	@XmlIDREF
//	@XmlAttribute
    @XmlTransient
    protected Process process;

    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;

    @XmlTransient
    public String _processType;
    @XmlTransient
    public String _isClosed;

    public void addChild(BaseElement child) {
        if (child instanceof Lane) {
            Lane lane = (Lane) child;
            this.getLanes().add(lane);
            lane.setLaneSet(this);
        }
    }

//	/**
//	 * Creates the lane compartment including all sub lane compartment for this
//	 * lane set.
//	 */
//	public LaneCompartment createLaneCompartment() {
//		LaneCompartment laneComp = new LaneCompartment();
//		laneComp.setId(Lane)
//	}
//	

    /**
     * @return All {@link FlowElement} that are contained in the {@link LaneSet}
     */
    public List<FlowElement> getChildFlowElements() {
        ArrayList<FlowElement> deepestFlowElements = new ArrayList<FlowElement>();
        List<Lane> lanes = this.getDeepestLanes(this.getLanes());

        for (Lane lane : lanes) {
            deepestFlowElements.addAll(lane.getFlowNodeRef());
        }

        return deepestFlowElements;
    }

    /**
     * Retrieve the deepest child lanes in a lane set
     *
     * @param lanes
     * @return
     */
    private List<Lane> getDeepestLanes(List<Lane> lanes) {
        ArrayList<Lane> laneList = new ArrayList<Lane>();
        if (lanes == null)
            return laneList;
        for (Lane lane : lanes) {
            if (lane.childLaneSet == null)
                /* Deepest lane in lane tree */
                laneList.add(lane);
            else if (lane.getChildLaneSet(false).lanes != null && lane.getChildLaneSet(false).getLanes().size() > 0) {
                laneList.addAll(this.getDeepestLanes(lane.getChildLaneSet(false).getLanes()));
            } else {
//				laneList.add(lane);
            }
        }
        return laneList;
    }

    /**
     * Returns all contained child lane and their children.
     *
     * @return
     */
    public List<Lane> getAllLanes() {
        List<Lane> laneList = new ArrayList<Lane>();
        for (Lane lane : this.getLanes()) {
            laneList.add(lane);
            laneList.addAll(lane.getLaneList());
        }

        return laneList;
    }

    /**
     * Removes the child element from the underling lanes and child lane sets.
     *
     * @param child
     */
    public void removeChild(BaseElement child) {
        for (Lane lane : this.getLanes()) {
            lane.getFlowNodeRef().remove(child);
            if (lane.childLaneSet != null) {
                lane.getChildLaneSet(false).removeChild(child);
            }
        }
    }

//	/**
//	 * Basic method for the conversion of BPMN2.0 to the editor's internal format. 
//	 * {@see BaseElement#toShape(BPMN2DiagramConverter)}
//	 * @param converterForShapeCoordinateLookup an instance of {@link BPMN2DiagramConverter}, offering several lookup methods needed for the conversion.
//	 */
//	  public Shape toShape(BPMN2DiagramConverter converterForShapeCoordinateLookup) {
//	    	Shape shape = super.toShape(converterForShapeCoordinateLookup);
//	    	
//	    	// This should not work...? according to the standard, a laneset contains lanes, it is just a container and no graphical element.
//	    	// > Well, thus it never shows up as a BPMNShape anyway... :D
//	    	shape.setStencil(new StencilType("Pool"));	   
//	    	
//	    	List<FlowElement> x = this.getChildFlowElements();
//	    	ArrayList<Shape> children = new ArrayList<Shape>();
//	    	for(FlowElement f : x){
//	    		children.add(new Shape(f.getId()));
//	    	}
//	    	shape.setChildShapes(children);
//	    	
//	    	this.getParentLane().addChild(this);
//	    	//this.getPool();
//	    	
//	    	return shape;
//	  }

    /* Getter & Setter */

    /**
     * Gets the value of the lane property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lane property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLane().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Lane }
     */
    @ChildElements
    public List<Lane> getLanes() {
        if (this.lanes == null) {
            this.lanes = new ArrayList<Lane>();
        }
        return this.lanes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parentLane
     */
    public Lane getParentLane() {
        return parentLane;
    }

    /**
     * @param parentLane the parentLane to set
     */
    public void setParentLane(Lane parentLane) {
        this.parentLane = parentLane;
    }

    /**
     * @return the process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(Process process) {
        this.process = process;
    }

}
