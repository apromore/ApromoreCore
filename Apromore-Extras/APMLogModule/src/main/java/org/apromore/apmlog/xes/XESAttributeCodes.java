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
package org.apromore.apmlog.xes;

public class XESAttributeCodes {
    public static final String CONCEPT_NAME = "concept:name";
    public static final String ORG_RESOURCE = "org:resource";
    public static final String ORG_GROUP = "org:group";
    public static final String TIME_TIMESTAMP = "time:timestamp";
    public static final String LIFECYCLE_TRANSITION = "lifecycle:transition";

    public static String getDisplayLabelForMultiple(String xesAttributeCode) {
        switch (xesAttributeCode) {
            case CONCEPT_NAME: return "Activities";
            case ORG_RESOURCE: return "Resources";
            case ORG_GROUP: return "Groups";
            default: return xesAttributeCode;
        }
    }

    public static String getDisplayLabelForSingle(String xesAttributeCode) {
        switch (xesAttributeCode) {
            case CONCEPT_NAME: return "Activity";
            case ORG_RESOURCE: return "Resource";
            case ORG_GROUP: return "Group";
            default: return xesAttributeCode;
        }
    }
}
