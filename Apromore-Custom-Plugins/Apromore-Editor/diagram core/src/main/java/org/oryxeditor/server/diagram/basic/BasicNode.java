
package org.oryxeditor.server.diagram.basic;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 The University of Melbourne.
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
 * #L%
 */

import org.oryxeditor.server.diagram.generic.GenericNode;

/**
 * Simple extension of {@link GenericNode} to allow for easier usage without having to use generics.
 * Does not add or change any functionality
 *
 * @author Philipp Maschke
 */
public class BasicNode extends GenericNode<BasicShape, BasicDiagram> implements BasicShape {

    public BasicNode(String resourceId) {
        super(resourceId);
    }


    public BasicNode(String resourceId, String stencilId) {
        super(resourceId, stencilId);
    }

}
