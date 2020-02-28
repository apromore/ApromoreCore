/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.xlog.singletonlog;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;

public class XAttributeMapSingletonImpl extends UnifiedMap<String, XAttribute> implements XAttributeMap {
    private static final long serialVersionUID = 2701256420845748051L;

    public XAttributeMapSingletonImpl() {
        this(3);
    }

    public XAttributeMapSingletonImpl(int size) {
        super(size);
    }

    public XAttributeMapSingletonImpl(Map<String, XAttribute> template) {
        this.putAll(template);
    }

    public UnifiedMap<String, XAttribute> clone() {
        XAttributeMapSingletonImpl clone = new XAttributeMapSingletonImpl(this.size());

        for(String key : keySet()) {
            clone.put(key, (XAttribute)(this.get(key)).clone());
        }

        return clone;
    }
}
