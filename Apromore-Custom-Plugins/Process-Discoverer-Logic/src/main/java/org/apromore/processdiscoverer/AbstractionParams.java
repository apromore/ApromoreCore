/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.processdiscoverer;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;

/**
 * Parameters used to generate different abstractions for a log.
 * 
 * @author Bruce Nguyen
 *
 */
public class AbstractionParams {
	private IndexableAttribute IndexableAttribute;
	private double nodeSelectThreshold;
	private double arcSelectThreshold;
	private double parallelism;
	private boolean prioritizeParallelism;
	private boolean preserve_connectivity;
	private boolean inverted_nodes;
	private boolean inverted_arcs;
	boolean secondary;
	
	private MeasureType fixedType;
	private MeasureAggregation fixedAggregation;
	private MeasureRelation fixedRelation;
	
	private MeasureType primaryType;
	private MeasureAggregation primaryAggregation;
	private MeasureRelation primaryRelation;
	
	private MeasureType secondaryType;
	private MeasureAggregation secondaryAggregation;
	private MeasureRelation secondaryRelation;

	private int maxNumberOfNodes;
	private int maxNumberOfArcs;
	
	private Abstraction correspondingDFG;
	
	public AbstractionParams(IndexableAttribute IndexableAttribute, double nodeSelectThreshold, double arcSelectThreshold, double parallelism,
							boolean prioritizeParallelism, boolean preserve_connectivity,
							boolean inverted_nodes, boolean inverted_arcs, boolean secondary,
							MeasureType fixedType, MeasureAggregation fixedAggregation, MeasureRelation fixedRelation,
							MeasureType primaryType, MeasureAggregation primaryAggregation, MeasureRelation primaryRelation,
							MeasureType secondaryType, MeasureAggregation secondaryAggregation, MeasureRelation secondaryRelation,
							int maxNumberOfNodes, int maxNumberOfArcs,
							Abstraction correspondingDFG) {
		this.IndexableAttribute = IndexableAttribute;
		this.nodeSelectThreshold = nodeSelectThreshold;
		this.arcSelectThreshold = arcSelectThreshold;
		this.parallelism = parallelism;
		this.prioritizeParallelism = prioritizeParallelism;
		this.preserve_connectivity = preserve_connectivity;
		this.inverted_nodes = inverted_nodes;
		this.inverted_arcs = inverted_arcs;
		this.secondary = secondary;
		
		this.fixedType = fixedType;
		this.fixedAggregation = fixedAggregation;
		this.fixedRelation = fixedRelation;
		
		this.primaryType = primaryType;
		this.primaryAggregation = primaryAggregation;
		this.primaryRelation = primaryRelation;
		
		this.secondaryType = secondaryType;
		this.secondaryAggregation= secondaryAggregation;
		this.secondaryRelation = secondaryRelation;

		this.maxNumberOfNodes = maxNumberOfNodes;
		this.maxNumberOfArcs = maxNumberOfArcs;
		
		this.correspondingDFG = correspondingDFG;
	}
	
	public IndexableAttribute getAttribute() {
		return this.IndexableAttribute;
	}
	
	public double getNodeSelectThreshold() {
		return this.nodeSelectThreshold;
	}
	
	public void setNodeSelectThreshold(double nodes) {
	    this.nodeSelectThreshold = nodes;
	}
	
	public double getArcSelectThreshold() {
		return this.arcSelectThreshold;
	}
	
	public void setArcSelectThreshold(double arcs) {
        this.arcSelectThreshold = arcs;
    }
	
	public double getParallelismLevel() {
		return this.parallelism;
	}
	
	public boolean prioritizeParallelism() {
		return this.prioritizeParallelism;
	}
	
	public boolean preserveConnectivity() {
		return this.preserve_connectivity;
	}
	
	public boolean invertedNodes() {
		return this.inverted_nodes;
	}
	
	public boolean invertedArcs() {
		return this.inverted_arcs;
	}
	
	public MeasureType getFixedType() {
		return this.fixedType;
	}
	
	public MeasureAggregation getFixedAggregation() {
		return this.fixedAggregation;
	}
	
    public MeasureRelation getFixedRelation() {
        return this.fixedRelation;
    }
	
	public MeasureType getPrimaryType() {
		return this.primaryType;
	}
	
	public MeasureAggregation getPrimaryAggregation() {
		return this.primaryAggregation;
	}
	
    public MeasureRelation getPrimaryRelation() {
        return this.primaryRelation;
    }
    
    public void setPrimaryMeasure(MeasureType type, MeasureAggregation aggregation, MeasureRelation relation) {
    	primaryType = type;
    	primaryAggregation = aggregation;
    	primaryRelation = relation;
    }
	
	public MeasureType getSecondaryType() {
		return this.secondaryType;
	}
	
	public MeasureAggregation getSecondaryAggregation() {
		return this.secondaryAggregation;
	}

    public MeasureRelation getSecondaryRelation() {
        return this.secondaryRelation;
    }
	
	public boolean getSecondary() {
		return this.secondary;
	}
	
	public void setSecondary(boolean hasSecondary) {
		this.secondary = hasSecondary;
	}

	public int getMaxNumberOfNodes() {
		return this.maxNumberOfNodes;
	}

	public void setMaxNumberOfNodes(int maxNumberOfNodes) {
		this.maxNumberOfNodes = maxNumberOfNodes;
	}

	public int getMaxNumberOfArcs() {
		return this.maxNumberOfArcs;
	}

	public void setMaxNumberOfArcs(int maxNumberOfArcs) {
		this.maxNumberOfArcs = maxNumberOfArcs;
	}
	
	// Corresponding DFG is the related one of the to-be DFG
	// For example: BPMN diagram will be created from a graph, or
	// a graph will be created based on another graph with weight type difference only
	public Abstraction getCorrepondingDFG() {
		return this.correspondingDFG;
	}
	
	public void setCorrespondingDFG(Abstraction correspondingDFG) {
	    this.correspondingDFG = correspondingDFG;
	}
	
	@Override
    public AbstractionParams clone() {
	    return new AbstractionParams(
	            this.getAttribute(),
	            this.getNodeSelectThreshold(),
	            this.getArcSelectThreshold(),
	            this.getParallelismLevel(),
	            this.prioritizeParallelism(),
	            this.preserveConnectivity(),
	            this.invertedNodes(),
	            this.invertedArcs(),
	            this.getSecondary(),
	            
	            this.getFixedType(),
	            this.getFixedAggregation(),
	            this.getFixedRelation(),
	            
	            this.getPrimaryType(),
	            this.getPrimaryAggregation(),
	            this.getPrimaryRelation(),
	            
	            this.getSecondaryType(),
	            this.getSecondaryAggregation(),
	            this.getSecondaryRelation(),

				this.getMaxNumberOfNodes(),
				this.getMaxNumberOfArcs(),
	            
	            this.getCorrepondingDFG());
	}
}
