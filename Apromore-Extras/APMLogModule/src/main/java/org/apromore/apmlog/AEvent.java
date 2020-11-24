/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Set;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (13/10/2020)
 * Modified: Chii Chang (27/10/2020)
 */
public interface AEvent {

     UnifiedMap<String, String> getAllAttributes();

     void setName(String name);

     void setResource(String resource);

     void setLifecycle(String lifecycle);

     void setTimestampMilli(long timestampMilli);

     String getName();

     String getResource();

     String getLifecycle();

     long getTimestampMilli();

     String getAttributeValue(String attributeKey);

     UnifiedMap<String, String> getAttributeMap();

     Set<String> getAttributeNameSet();

     String getTimeZone();
     int getIndex();

     int getParentActivityIndex();

     void setParentActivityIndex(int immutableActivityIndex);

     void setParentTrace(ATrace parentTrace);

     ATrace getParentTrace();

     AEvent clone(ATrace parentTrace, AActivity parentActivity);

     AEvent clone();
}
