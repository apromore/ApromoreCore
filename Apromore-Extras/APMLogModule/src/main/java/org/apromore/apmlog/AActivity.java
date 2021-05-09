/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.apmlog;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.List;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (07/10/2020) - "schedule" event included; added start time method.
 * Modified: Chii Chang (27/10/2020)
 * Modified: Chii Chang (26/01/2021)
 * Modified: Chii Chang (05/05/2021)
 */
public interface AActivity  {

     int getImmutableIndex();

     void setMutableIndex(int mutableIndex);
     void setMutableTraceIndex(int mutableTraceIndex);

     int getMutableIndex();

     String getName();

     String getResource();

     UnifiedMap<String, String> getAttributeMap();

     List<AEvent> getImmutableEventList();

     long getStartTimeMilli();

     long getEndTimeMilli();

     double getDuration();

     UnifiedMap<String, String> getAttributes();

     UnifiedMap<String, String> getAllAttributes();

     void setAttributes(UnifiedMap<String, String> allAttributes);

     IntArrayList getEventIndexes();

     String getAttributeValue(String key);

     AActivity clone(ATrace parentTrace);

     AActivity clone();

     int getMutableTraceIndex();

     int getImmutableTraceIndex();

     void setParentTrace(ATrace parentTrace);

     long getEventSize();

     ATrace getParentTrace();
}
