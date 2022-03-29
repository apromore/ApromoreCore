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
package org.apromore.dao.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.PROPERTY)
public class AccessRights {

    private boolean readOnly = false;
    private boolean writeOnly = false;
    private boolean ownerShip = false;

    public AccessRights() {
        super();
    }

    public AccessRights(boolean readOnly, boolean writeOnly, boolean ownerShip) {
        super();
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
        this.ownerShip = ownerShip;
    }

    @Column(name = "has_read")
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Column(name = "has_write")
    public boolean isWriteOnly() {
        return writeOnly;
    }

    public void setWriteOnly(boolean writeOnly) {
        this.writeOnly = writeOnly;
    }

    @Column(name = "has_ownership")
    public boolean isOwnerShip() {
        return ownerShip;
    }

    public void setOwnerShip(boolean ownerShip) {
        this.ownerShip = ownerShip;
    }

    public boolean hasAll() {
        return isOwnerShip() && hasReadWrite();
    }

    public boolean hasReadWrite() {
        return isReadOnly() && isWriteOnly();
    }

}
