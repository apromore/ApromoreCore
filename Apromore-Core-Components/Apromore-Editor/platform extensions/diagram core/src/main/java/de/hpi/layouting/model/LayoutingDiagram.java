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

package de.hpi.layouting.model;

import java.util.List;
import java.util.Map;

public interface LayoutingDiagram {

    public abstract Map<String, LayoutingElement> getElements();

    public abstract List<LayoutingElement> getChildElementsOf(LayoutingElement parent);

    public abstract List<LayoutingElement> getChildElementsOf(
            List<LayoutingElement> parents);

    public abstract List<LayoutingElement> getElementsOfType(String type);

    public abstract List<LayoutingElement> getElementsWithoutType(String type);

    /**
     * Liefert das bereits bekannte Element oder legt ein neues mit der id an
     *
     * @param id die ID des Elements
     * @return ein LayoutingElement mit der id
     */
    public abstract LayoutingElement getElement(String id);

    public abstract List<LayoutingElement> getStartEvents();

    public abstract List<LayoutingElement> getConnectingElements();

    public abstract List<LayoutingElement> getGateways();

}
