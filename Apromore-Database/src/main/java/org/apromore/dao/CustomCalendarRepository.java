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

package org.apromore.dao;

import java.util.Optional;

import java.util.Set;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCalendarRepository extends JpaRepository<CustomCalendar, Long>{

	CustomCalendar findByName(String name);

    Optional<CustomCalendar> findById(Long id);

    /**
     * @param user arbitrary, but non-null
     * @return all custom calendars owned by the specified <var>user</var>
     */
    @Query("SELECT c FROM User u JOIN u.calendars c WHERE u = ?1")
    Set<CustomCalendar> findByUser(User user);

}
