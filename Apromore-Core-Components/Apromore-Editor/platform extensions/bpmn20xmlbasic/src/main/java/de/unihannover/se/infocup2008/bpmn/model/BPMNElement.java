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
package de.unihannover.se.infocup2008.bpmn.model;

import de.hpi.layouting.model.LayoutingElement;

/**
 * Represents an element of the bpmn diagram. Each task, sequenceflow, etc. is
 * an element.
 *
 * @author Team Royal Fawn
 *         <p/>
 *         FIXME make a Class of it (BPMNElementImpl)
 */
public interface BPMNElement extends LayoutingElement {

    /**
     * Updates the underlying DataModel e.g. JSONObject or XMLNode
     */
    public abstract void updateDataModel();

    public abstract boolean isADockedIntermediateEvent();
}