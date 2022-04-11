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

package org.apromore.calendar.model;

import java.time.LocalDate;

public class ImmutableHolidayModel extends HolidayModel {

    protected ImmutableHolidayModel(HolidayModel model) {
        super();
        this.id = model.getId();
        this.holidayType = model.getHolidayType();
        this.name = model.getName();
        this.description = model.getDescription();
        this.holidayDate = LocalDate.of(model.getHolidayDate().getYear(),
            model.getHolidayDate().getMonth(), model.getHolidayDate().getDayOfMonth());
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
    }

    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHolidayType(HolidayType holidayType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String desc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHolidayDate(LocalDate date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCreatedBy(String createdBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        throw new UnsupportedOperationException();
    }
}
