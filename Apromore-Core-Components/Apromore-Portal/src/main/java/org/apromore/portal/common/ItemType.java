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
package org.apromore.portal.common;

/**
 * Item types
 */
public enum ItemType {
    LOG("log", "ap-icon-log"),
    MODEL("model", "ap-icon-bpmn-model"),
    FOLDER("folder", "ap-icon-folder");

    private final String label;
    private final String iconClass;
    private final int hash;

    /**
     * @param label
     */
    ItemType(String label, String iconClass) {
        this.label = label;
        this.iconClass = iconClass;
        this.hash = label.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() { return label; }

    public int getHashCode() {
        return this.hash;
    }

    public String getIconClass() {
        return this.iconClass;
    }
}
