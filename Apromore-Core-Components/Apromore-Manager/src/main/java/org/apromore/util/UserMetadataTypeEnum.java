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
package org.apromore.util;

import java.util.List;

/**
 * Parameter type for the
 *
 * {@link org.apromore.service.impl.UserMetadataServiceImpl#saveUserMetadata(String, String, UserMetadataTypeEnum, String, List)}.
 * These user metadata type must be a subset of the ones in the database.
 * @author frankm
 */

public enum UserMetadataTypeEnum {

    /**
     * Types of user metadata
     */
    FILTER(1),
    DASHBOARD(2),
    CSV_IMPORTER(3),
    LOG_ANIMATION(4),
    DASH_TEMPLATE(5),
    SIMULATOR(6),
    FILTER_TEMPLATE(7),
    PERSPECTIVE_TAG(8),
    COST_TABLE(9);

    private final Integer userMetadataTypeId;

    UserMetadataTypeEnum(Integer newUserMetadataTypeId) {
        this.userMetadataTypeId = newUserMetadataTypeId;
    }

    public Integer getUserMetadataTypeId() {
        return userMetadataTypeId;
    }
}
