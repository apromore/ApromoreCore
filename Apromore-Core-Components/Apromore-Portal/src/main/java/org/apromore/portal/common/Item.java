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

package org.apromore.portal.common;

import java.util.Date;
import org.apromore.portal.common.ItemType;
import lombok.Getter;
import lombok.Setter;

public class Item {

    @Getter @Setter private Integer id;
    @Getter @Setter private String name;
    @Getter @Setter private ItemType type;
    @Getter @Setter private String iconClass;
    @Getter @Setter private String modifiedDate;

    public Item(Integer id, String name, ItemType type, String modifiedDate) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.modifiedDate = modifiedDate;
        this.iconClass = type.getIconClass();
    }

    @Override
    public int hashCode() { return id == null ? 0 : (type.getHashCode() + id.hashCode()); }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Item.class.equals(obj.getClass())) { return false; }
        return (obj instanceof Item) && id.equals(((Item) obj).id) && type.equals(((Item) obj).type);
    }
}
