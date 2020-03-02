/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.predictivemonitor.impl;

// Java 2 Standard Edition
import java.util.List;
import java.util.Set;

// Third party packages
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictiveMonitorEventRepository extends JpaRepository<PredictiveMonitorEventImpl, Integer> {

    public PredictiveMonitorEventImpl findById(Integer id);
    public Set<PredictiveMonitorEventImpl> findByEventNr(Integer eventNr);
    public List<PredictiveMonitorEventImpl> findByPredictiveMonitor(PredictiveMonitorImpl predictiveMonitor);
    public PredictiveMonitorEventImpl findByPredictiveMonitorAndCaseIdAndEventNr(PredictiveMonitorImpl predictiveMonitor, String caseId, Integer eventNr);
}
