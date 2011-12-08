package org.oryxeditor.server.diagram.basic;

import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.oryxeditor.server.diagram.generic.GenericEdge;

/**
 * Simple extension of {@link GenericDiagram} to allow for easier usage without having to use generics.
 * Does not add or change any functionality
 * 
 * @author Philipp Maschke
 *
 */
public class BasicEdge extends GenericEdge<BasicShape, BasicDiagram> implements BasicShape {

	public BasicEdge(String resourceId) {
		super(resourceId);
	}


	public BasicEdge(String resourceId, String stencilId) {
		super(resourceId, stencilId);
	}

}
