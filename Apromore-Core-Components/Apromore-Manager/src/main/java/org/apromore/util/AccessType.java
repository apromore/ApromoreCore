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

import lombok.Getter;
import org.apromore.dao.model.AccessRights;

import java.util.Arrays;

/**
 * Access types in ACL, which can map to flags in Group_[artifact] POJOs.
 * <p>
 * |            | has_read | has_write | has_ownership |
 * |------------|----------|-----------|---------------|
 * | Restricted | 0        | 0         | 0             |
 * | Viewer     | 1        | 0         | 0             |
 * | Editor     | 1        | 1         | 0             |
 * | Owner      | 1        | 1         | 1             |
 *
 * @author frankma
 */
@Getter
public enum AccessType {

    /**
     * Access types
     */
    RESTRICTED(false, false, false, "Viewer (restricted)"),
    VIEWER(true, false, false, "Viewer (full)"),
    EDITOR(true, true, false, "Editor"),
    OWNER(true, true, true, "Owner");

    boolean isRead;
    boolean isWrite;
    boolean isOwner;
    String label;

    AccessType(boolean isRead, boolean isWrite, boolean isOwner, String label) {
        this.isRead = isRead;
        this.isWrite = isWrite;
        this.isOwner = isOwner;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    /**
     * Get AccessType by label
     *
     * @param label
     * @return the AccessType object
     */
    public static AccessType getAccessType(String label) {
        return Arrays.stream(AccessType.values())
                .filter(accessType -> accessType.label.equals(label))
                .findFirst()
                .orElse(AccessType.RESTRICTED);
    }

    public static AccessType getAccessType(boolean isRead, boolean isWrite, boolean isOwner) {
        return Arrays.stream(AccessType.values()).filter(accessType -> accessType.isRead == isRead &&
                accessType.isWrite == isWrite &&
                accessType.isOwner == isOwner).findFirst().orElse(AccessType.RESTRICTED);
    }

    public static AccessType getAccessType(AccessRights accessRights) {
        return getAccessType(accessRights.isReadOnly(), accessRights.isWriteOnly(), accessRights.isOwnerShip());
    }


}
