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

import org.apromore.dao.model.Statistic;
import org.apromore.util.StatType;

import java.util.List;

public interface StatisticRepositoryCustom {

    /**
     * Store all statistics into DB. Use custom JPA instead of sava() for better performance.
     * @param stats
     */
    void storeAllStats(List<Statistic> stats);

    /**
     * check if these type of stat exist in database.
     * @param logid
     * @param statType
     * @return
     */
    boolean existsByLogidAndStatType(Integer logid, StatType statType);
}
