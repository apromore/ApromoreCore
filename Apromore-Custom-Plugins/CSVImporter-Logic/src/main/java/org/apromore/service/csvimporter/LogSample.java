/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.csvimporter;

import java.util.List;
import java.util.Map;

public interface LogSample {

    String getCaseIdLabel();
    String getActivityLabel();
    String getTimestampLabel();
    String getStartTimestampLabel();
    String getOtherTimestampLabel();
    String getResourceLabel();

    boolean isParsable(int colPos);
    boolean isParsableWithFormat(int colPos, String format);

    List<String> getHeader();
    List<List<String>> getLines();

    Map<String, Integer> getMainAttributes();
    List<Integer> getIgnoredPos();
    List<Integer> getCaseAttributesPos();
    List<Integer> getEventAttributesPos();
    Map<Integer, String> getOtherTimeStampsPos();

    String getTimestampFormat();
    void setTimestampFormat(String s);
    String getStartTsFormat();
    void setStartTsFormat(String s);
}
