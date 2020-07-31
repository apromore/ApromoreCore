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

package org.apromore.service.csvimporter.model;

import java.util.List;
import java.util.Map;

public interface LogSample {

    List<String> getHeader();

    List<List<String>> getLines();

    int getCaseIdPos();

    void setCaseIdPos(int pos);

    int getActivityPos();

    void setActivityPos(int pos);

    int getEndTimestampPos();

    void setEndTimestampPos(int pos);

    int getStartTimestampPos();

    void setStartTimestampPos(int pos);

    int getResourcePos();

    void setResourcePos(int pos);

    List<Integer> getCaseAttributesPos();

    List<Integer> getEventAttributesPos();

    Map<Integer, String> getOtherTimestamps();

    List<Integer> getIgnoredPos();

    String getEndTimestampFormat();

    void setEndTimestampFormat(String format);

    String getStartTimestampFormat();

    void setStartTimestampFormat(String format);

    boolean isParsable(int colPos);

    boolean isParsableWithFormat(int colPos, String format);

    void validateSample() throws Exception;
}
