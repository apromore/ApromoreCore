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
package org.apromore.util;

import lombok.Getter;

import java.util.Arrays;

/**
 * Access types in ACL, which can map to flags in Group_[artifact] POJOs.
 *
 |        | has_read | has_write | has_ownership |
 |--------|----------|-----------|---------------|
 | Viewer | 1        | 0         | 0             |
 | Editor | 1        | 1         | 0             |
 | Owner  | 1        | 1         | 1             |
 *
 */
@Getter
public enum AccessType {

    NONE(false, false, false),
    VIEWER(true, false, false),
    EDITOR(true, true, false),
    OWNER(true, true, true);

    boolean isRead;
    boolean isWrite;
    boolean isOwner;

    AccessType(boolean isRead, boolean isWrite, boolean isOwner) {
        this.isRead = isRead;
        this.isWrite = isWrite;
        this.isOwner = isOwner;
    }

    public static AccessType getAccessType(boolean isRead, boolean isWrite, boolean isOwner) {

        return Arrays.asList(AccessType.values()).stream().filter(accessType -> accessType.isRead == isRead &&
                accessType.isWrite == isWrite &&
                accessType.isOwner == isOwner).findFirst().orElse(AccessType.NONE);
    }


}
