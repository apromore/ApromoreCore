/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Data
public class LogMetaData {

    /**
     * Magic value for header index to indicate the header is absent.
     */
    public static final int HEADER_ABSENT = -1;

    private List<String> header;
    private int caseIdPos = HEADER_ABSENT;
    private int activityPos = HEADER_ABSENT;
    private int endTimestampPos = HEADER_ABSENT;
    private int startTimestampPos = HEADER_ABSENT;
    private int resourcePos = HEADER_ABSENT;
    private List<Integer> caseAttributesPos;
    private List<Integer> eventAttributesPos;
    private HashMap<Integer, String> otherTimestamps; // store position as key and format as value
    private List<Integer> ignoredPos;
    private String endTimestampFormat;
    private String startTimestampFormat;
    private String timeZone;

    public LogMetaData(List<String> header) {
        this.header = header;
        caseAttributesPos = new ArrayList<>();
        eventAttributesPos = new ArrayList<>();
        ignoredPos = new ArrayList<>();
        otherTimestamps = new HashMap<>();
        endTimestampFormat = null;
        startTimestampFormat = null;
    }

    public void validateSample() throws Exception {
        int count = 0;
        if (caseIdPos != HEADER_ABSENT) count++;
        if (activityPos != HEADER_ABSENT) count++;
        if (endTimestampPos != HEADER_ABSENT) count++;
        if (startTimestampPos != HEADER_ABSENT) count++;
        if (resourcePos != HEADER_ABSENT) count++;

        count += otherTimestamps.size();
        count += eventAttributesPos.size();
        count += caseAttributesPos.size();
        count += ignoredPos.size();

        if (header.size() != count) {
            throw new Exception("Failed to construct valid log sample!  Only specified " + count + " of " +
                header.size() + " headers: " + header);
        }
    }
}
