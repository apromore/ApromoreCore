package org.oryxeditor.server.diagram.basic;

import org.oryxeditor.server.diagram.generic.GenericNode;

/**
 * Simple extension of {@link GenericNode} to allow for easier usage without having to use generics.
 * Does not add or change any functionality
 * 
 * @author Philipp Maschke
 *
 */
public class BasicNode extends GenericNode<BasicShape, BasicDiagram> implements BasicShape{

	public BasicNode(String resourceId) {
		super(resourceId);
	}


	public BasicNode(String resourceId, String stencilId) {
		super(resourceId, stencilId);
	}

}
