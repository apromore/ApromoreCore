/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.logman.attribute;

import org.apromore.logman.ATrace;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * The attribute can be indexed, i.e. assigned an integer
 * 
 * @author Bruce Nguyen
 *
 */
public interface IndexableAttribute extends Attribute {
	Object getValue(int index);
	int getValueIndex(Object value);
	int getValueIndex(XAttribute xatt);
	int getValueIndex(XAttributable ele);
	int[] getValueIndexes();
	ImmutableList<Object> getValues();
	int getValueSize();
	AttributeMatrixGraph getMatrixGraph();
    public int getArtificialStartIndex();
    public int getArtificialEndIndex();
    public void updateValueCount(XEvent event, boolean increase);
    public void updateValueCount(ATrace event, boolean increase);
    public double getValueRelativeFrequency(int valueIndex);
}
