package org.oryxeditor.server.diagram.basic;

import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.generic.GenericDiagram;

/**
 * Simple extension of {@link GenericDiagram} to allow for easier usage without having to use generics.
 * Does not add or change any functionality
 * 
 * @author Philipp Maschke
 *
 */
public class BasicDiagram extends GenericDiagram<BasicShape, BasicDiagram> implements BasicShape {

	public BasicDiagram(String resourceId, String stencilId, StencilSetReference stencilsetRef) {
		super(resourceId, stencilId, stencilsetRef);
	}


	public BasicDiagram(String resourceId) {
		super(resourceId);
	}


	public BasicDiagram(String resourceId, String stencilId) {
		super(resourceId, stencilId);
	}

}
