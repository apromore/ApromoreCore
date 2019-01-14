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

package org.oryxeditor.server.diagram.basic;

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
