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

import java.util.*;

/**
 * This class represents a diagram. It holds the elements an provides some
 * access-methods for them.
 *
 * @author Team Royal Fawn
 */
public abstract class LayoutingAbstractDiagram<T extends LayoutingElement> implements LayoutingDiagram {
    Map<String, T> elements = new HashMap<String, T>();

    @SuppressWarnings("unchecked")
    public Map<String, LayoutingElement> getElements() {
        return Collections.unmodifiableMap((Map<String, LayoutingElement>) elements);
    }

    public List<LayoutingElement> getChildElementsOf(LayoutingElement parent) {
        return getChildElementsOf(Collections.singletonList(parent));
    }

    public List<LayoutingElement> getChildElementsOf(List<LayoutingElement> parents) {
        List<LayoutingElement> result = new LinkedList<LayoutingElement>();
        for (String key : getElements().keySet()) {
            LayoutingElement element = getElements().get(key);
            if (parents.contains(element.getParent())) {
                result.add(element);
            }
        }
        return result;

    }

    public List<LayoutingElement> getElementsOfType(String type) {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (String key : getElements().keySet()) {
            LayoutingElement element = getElements().get(key);
            if (element.getType().equals(type)) {
                resultList.add(element);
            }
        }

        return resultList;
    }

    public List<LayoutingElement> getElementsWithoutType(String type) {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (String key : getElements().keySet()) {
            LayoutingElement element = getElements().get(key);
            if (!element.getType().equals(type)) {
                resultList.add(element);
            }
        }

        return resultList;
    }

    public T getElement(String id) {
        T element = this.elements.get(id);
        if (element == null) {
            element = this.newElement();
            element.setId(id);
            this.elements.put(id, element);
        }
        return element;
    }

    protected abstract T newElement();


    @Override
    public String toString() {
        String out = "Diagramm: \n";
        out += getElements().size() + " Elements:\n";
        for (String key : getElements().keySet()) {
            LayoutingElement element = getElements().get(key);
            out += element.toString() + "\n";
        }
        return out;
    }

}
