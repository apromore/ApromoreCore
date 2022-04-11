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

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ImmutableWorkDayModel extends WorkDayModel {

    protected ImmutableWorkDayModel(WorkDayModel model) {
        super();
        this.id = model.getId();
        this.dayOfWeek = model.getDayOfWeek();
        this.startTime = LocalTime.from(model.getStartTime());
        this.endTime = LocalTime.from(model.getEndTime());
        this.workingDay = model.isWorkingDay();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
    }

    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDayOfWeek(DayOfWeek doW) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStartTime(LocalTime startTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEndTime(LocalTime endTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWorkingDay(boolean isWorkingDay) {
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
