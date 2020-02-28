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

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.impl.XAttributeImpl;

public class XAttributeDiscreteSingletonImpl extends XAttributeImpl implements XAttributeDiscrete {
    private static final long serialVersionUID = -1789813595800348876L;
    private Long value;

    public XAttributeDiscreteSingletonImpl(String key, Long value) {
        this(key, value, (XExtension) null);
    }

    public XAttributeDiscreteSingletonImpl(String key, Long value, XExtension extension) {
        super(key, extension);
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String toString() {
        return Long.toString(this.value);
    }

    public Object clone() {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XAttributeDiscrete)) {
            return false;
        } else {
            XAttributeDiscrete other = (XAttributeDiscrete)obj;
            return super.equals(other) && this.value == other.getValue();
        }
    }

    public int compareTo(XAttribute other) {
        if (!(other instanceof XAttributeDiscrete)) {
            throw new ClassCastException();
        } else {
            int result = super.compareTo(other);
            return result != 0 ? result : Long.valueOf(this.value).compareTo(((XAttributeDiscrete)other).getValue());
        }
    }
}
