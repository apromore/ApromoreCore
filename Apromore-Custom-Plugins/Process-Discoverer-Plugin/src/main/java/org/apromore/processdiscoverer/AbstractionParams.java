package org.apromore.processdiscoverer;

import java.util.Collections;
import java.util.Set;

import org.apromore.processdiscoverer.dfg.ArcType;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class AbstractionParams {
	private String attribute;
	private EventClassifier classifier;
	private double activities;
	private double arcs;
	private double parallelism;
	private boolean prioritizeParallelism;
	private boolean preserve_connectivity;
	private boolean inverted_nodes;
	private boolean inverted_arcs;
	private VisualizationType fixedType;
	private VisualizationAggregation fixedAggregation;
	private VisualizationType primaryType;
	private VisualizationAggregation primaryAggregation;
	boolean secondary;
	private VisualizationType secondaryType;
	private VisualizationAggregation secondaryAggregation;
	private Set<ArcType> arcTypes;
	
	public AbstractionParams(String attribute, double activities, double arcs, double parallelism, 
							boolean prioritizeParallelism, boolean preserve_connectivity, 
							boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, 
							VisualizationAggregation fixedAggregation, VisualizationType primaryType, 
							VisualizationAggregation primaryAggregation, VisualizationType secondaryType, 
							VisualizationAggregation secondaryAggregation, 
							Set<ArcType> arcTypes) {
		this.attribute = attribute;
		this.classifier = new EventClassifier(attribute);
		this.activities = activities;
		this.arcs = arcs;
		this.parallelism = parallelism;
		this.prioritizeParallelism = prioritizeParallelism;
		this.preserve_connectivity = preserve_connectivity;
		this.inverted_nodes = inverted_nodes;
		this.inverted_arcs = inverted_arcs;
		this.fixedType = fixedType;
		this.fixedAggregation = fixedAggregation;
		this.primaryType = primaryType;
		this.primaryAggregation = primaryAggregation;
		this.secondaryType = secondaryType;
		this.secondaryAggregation= secondaryAggregation;
		this.secondary = secondary;
		this.arcTypes = arcTypes;
	}
	
	public String getAttribute() {
		return this.attribute;
	}
	
	public EventClassifier getClassifier() {
		return classifier;
	}
	
	public double getActivityLevel() {
		return this.activities;
	}
	
	public double getArcLevel() {
		return this.arcs;
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
	
	public VisualizationType getFixedType() {
		return this.fixedType;
	}
	
	public VisualizationAggregation getFixedAggregation() {
		return this.fixedAggregation;
	}
	
	public VisualizationType getPrimaryType() {
		return this.primaryType;
	}
	
	public VisualizationAggregation getPrimaryAggregation() {
		return this.primaryAggregation;
	}
	
	public VisualizationType getSecondaryType() {
		return this.secondaryType;
	}
	
	public VisualizationAggregation getSecondaryAggregation() {
		return this.secondaryAggregation;
	}
	
	public boolean getSecondary() {
		return this.secondary;
	}
	
	public Set<ArcType> getArcTypes() {
		return Collections.unmodifiableSet(this.arcTypes);
	}
}
