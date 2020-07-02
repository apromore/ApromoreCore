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
package org.apromore.dao;

import org.apromore.dao.model.DashboardLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardLayoutRepository extends JpaRepository<DashboardLayout, Integer>,
        DashboardLayoutRepositoryCustom{

    @Query("SELECT d.layout FROM DashboardLayout d WHERE d.userId = ?1 AND d.logId = ?2")
    String findByUserIdAndLogId(String userId, Integer logId);

    //TODO fix how to get latest one?
    @Query("SELECT DISTINCT d.layout FROM DashboardLayout d WHERE d.userId = ?1")
    String findByUserId(String userId);

}
