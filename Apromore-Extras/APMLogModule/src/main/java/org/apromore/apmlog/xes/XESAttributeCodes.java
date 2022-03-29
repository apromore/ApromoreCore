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
package org.apromore.apmlog.xes;

import org.zkoss.util.resource.Labels;

/**
 * Modified: Jane Hoh (23/02/2022) - i18n support. Get display labels from property file.
 */
public class XESAttributeCodes {
    public static final String CONCEPT_NAME = "concept:name";
    public static final String ORG_RESOURCE = "org:resource";
    public static final String ORG_ROLE = "org:role";
    public static final String ORG_GROUP = "org:group";
    public static final String TIME_TIMESTAMP = "time:timestamp";
    public static final String LIFECYCLE_TRANSITION = "lifecycle:transition";

    public static String getDisplayLabelForMultiple(String xesAttributeCode) {
        switch (xesAttributeCode) {
            case CONCEPT_NAME: return Labels.getLabel("att_multiple_activity_text", "Activities");
            case ORG_RESOURCE: return Labels.getLabel("att_multiple_resource_text", "Resources");
            case ORG_ROLE: return Labels.getLabel("att_multiple_role_text", "Roles");
            case ORG_GROUP: return Labels.getLabel("att_multiple_group_text", "Groups");
            default: return xesAttributeCode;
        }
    }

    public static String getDisplayLabelForSingle(String xesAttributeCode) {
        switch (xesAttributeCode) {
            case CONCEPT_NAME: return Labels.getLabel("att_single_activity_text", "Activity");
            case ORG_RESOURCE: return Labels.getLabel("att_single_resource_text", "Resource");
            case ORG_ROLE: return Labels.getLabel("att_single_role_text", "Role");
            case ORG_GROUP: return Labels.getLabel("att_single_group_text", "Group");
            default: return xesAttributeCode;
        }
    }
}
