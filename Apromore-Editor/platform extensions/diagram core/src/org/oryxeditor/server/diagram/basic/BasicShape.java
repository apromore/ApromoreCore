package org.oryxeditor.server.diagram.basic;

import org.oryxeditor.server.diagram.generic.GenericShape;

/**
 * Simple extension of {@link GenericShape} to allow for easier usage without having to use generics.
 * Does not add or change any functionality
 * 
 * @author Philipp Maschke
 *
 */
public interface BasicShape extends GenericShape<BasicShape, BasicDiagram> {

}
