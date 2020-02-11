
package org.oryxeditor.server.diagram.generic;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import org.oryxeditor.server.diagram.Point;

import java.util.List;


public interface ShapeFactory<S extends GenericShape<S, D>, D extends GenericDiagram<S, D>, E extends GenericEdge<S, D>, N extends GenericNode<S, D>> {

    /**
     * Creates a new diagram
     *
     * @param resourceId
     * @return
     */
    public D createNewDiagram(String resourceId);

    /**
     * Creates a new edge
     *
     * @param resourceId
     * @return
     */
    public E createNewEdge(String resourceId);

    /**
     * Creates a new node
     *
     * @param resourceId
     * @return
     */
    public N createNewNode(String resourceId);

    /**
     * Creates either a new edge or a new node, depending on the given list of dockers. Sets the given dockers as the shape's dockers.
     * <br/>
     * If {@link GenericShapeImpl#isEdge(List)} returns true, then a new edge will be created. Otherwise a new node will be returned.
     *
     * @param resourceId
     * @param dockers    the list of dockers for the new shape
     * @return either a new node or a new edge
     */
    public S createNewShapeOfCorrectType(String resourceId, List<Point> dockers);
}
