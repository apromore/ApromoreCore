/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2019 The University of Tartu.
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
 * #L%
 */

package org.apromore.service.csvimporter;

import java.sql.Timestamp;
import java.util.Map;

public interface LogEventModel {

    String getCaseID();

    String getConcept();

    Timestamp getTimestamp();

    Timestamp getStartTimestamp();

    String getResource();

    void setOtherTimestamps(Map<String, Timestamp> otherTimestamps);
    Map<String, Timestamp> getOtherTimestamps();

    Map<String, String> getOthers();
    Map<String, String> getCaseAttributes();
}
