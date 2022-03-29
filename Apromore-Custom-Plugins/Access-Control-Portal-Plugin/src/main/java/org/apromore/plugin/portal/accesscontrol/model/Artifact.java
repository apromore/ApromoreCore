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

package org.apromore.plugin.portal.accesscontrol.model;

import org.apromore.dao.model.UsermetadataType;

public class Artifact {

    private String name;
    private Integer id;
    private String type;
    private String updatedTime;

    public Artifact(Integer id, String name, String updatedTime, UsermetadataType usermetadataType) {
        this.name = name;
        this.id = id;
        this.updatedTime = updatedTime;
        this.type = usermetadataType.getType();
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public int hashCode() { return id == null ? 0 : id.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Artifact.class.equals(obj.getClass())) { return false; }
        return (obj instanceof Artifact) && id.equals(((Artifact) obj).id);
    }
}
