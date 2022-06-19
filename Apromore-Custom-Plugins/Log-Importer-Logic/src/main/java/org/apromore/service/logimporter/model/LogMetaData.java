/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * <p>Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */

package org.apromore.service.logimporter.model;

import static org.apromore.dao.model.AttributeType.ACTIVITY;
import static org.apromore.dao.model.AttributeType.CASE_ATTRIBUTE;
import static org.apromore.dao.model.AttributeType.CASE_ID;
import static org.apromore.dao.model.AttributeType.END_TIME;
import static org.apromore.dao.model.AttributeType.EVENT_ATTRIBUTE;
import static org.apromore.dao.model.AttributeType.IGNORED_ATTRIBUTE;
import static org.apromore.dao.model.AttributeType.RESOURCE;
import static org.apromore.dao.model.AttributeType.ROLE;
import static org.apromore.dao.model.AttributeType.START_TIME;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apromore.dao.model.AttributeType;
import org.apromore.dao.model.DataType;
import org.apromore.service.logimporter.exception.InvalidLogMetadataException;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.eclipse.collections.impl.list.Interval;

@Data
@NoArgsConstructor
@Slf4j
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
    private int rolePos = HEADER_ABSENT;
    private List<Integer> caseAttributesPos;
    private List<Integer> eventAttributesPos;
    /**
     * store position as key and format as value.
     */
    private HashMap<Integer, String> otherTimestamps;
    private List<Integer> ignoredPos;
    private String endTimestampFormat;
    private String startTimestampFormat;
    private String timeZone;
    private String encoding;
    private List<Integer> maskPos;
    private List<Integer> perspectivePos;

    private List<Integer> stringAttributesPos;
    private List<Integer> integerAttributesPos;
    private List<Integer> doubleAttributesPos;
    private List<Integer> timestampAttributesPos;

    public LogMetaData(List<String> header) {
        this.header = header;
        caseAttributesPos = new ArrayList<>();
        eventAttributesPos = new ArrayList<>();
        ignoredPos = new ArrayList<>();
        otherTimestamps = new HashMap<>();
        endTimestampFormat = null;
        startTimestampFormat = null;
        encoding = null;
        maskPos = new ArrayList<>();
        perspectivePos = new ArrayList<>();
        stringAttributesPos = new ArrayList<>();
        integerAttributesPos = new ArrayList<>();
        doubleAttributesPos = new ArrayList<>();
        timestampAttributesPos = new ArrayList<>();
    }

    public void validateSample() throws Exception {
        int count = 0;
        if (caseIdPos != HEADER_ABSENT) {
            count++;
        }
        if (activityPos != HEADER_ABSENT) {
            count++;
        }
        if (endTimestampPos != HEADER_ABSENT) {
            count++;
        }
        if (startTimestampPos != HEADER_ABSENT) {
            count++;
        }
        if (resourcePos != HEADER_ABSENT) {
            count++;
        }
        if (rolePos != HEADER_ABSENT) {
            count++;
        }

        count += otherTimestamps.size();
        count += eventAttributesPos.size();
        count += caseAttributesPos.size();
        count += ignoredPos.size();

        if (header.size() != count) {
            throw new InvalidLogMetadataException(
                "Failed to construct valid log sample!  Specified attribute type for " + count + " of "
                    + header.size() + " headers: " + header);
        }

        List<Integer> indexList = new ArrayList<>();
        indexList.addAll(stringAttributesPos);
        indexList.addAll(integerAttributesPos);
        indexList.addAll(doubleAttributesPos);
        indexList.addAll(timestampAttributesPos);

        int dateTypeCount = stringAttributesPos.size() + integerAttributesPos.size() + doubleAttributesPos.size()
                            + timestampAttributesPos.size();

        if (dateTypeCount != count) {
            List<Integer> missingIndexList = findMissingIndex(indexList, dateTypeCount);
            log.info("Only specified data type for {} of {} headers: {}. Add missing index to String data type",
                dateTypeCount, header.size(), header);
            stringAttributesPos.addAll(missingIndexList);
        }
    }

    protected List<Integer> findMissingIndex(List<Integer> indexList, Integer count) {

        List<Integer> fullList = Interval.zeroTo(count);
        Collections.sort(indexList);

        return new ArrayList<>(Sets.difference(Sets.newHashSet(fullList), Sets.newHashSet(indexList)));
    }

    public List<String> getPerspectives() throws InvalidLogMetadataException {

        List<String> result = new ArrayList<>();

        if (activityPos != HEADER_ABSENT) {
            result.add(XConceptExtension.KEY_NAME);
        } else {
            throw new InvalidLogMetadataException("Found invalid Log Metadata that missing activityPos");
        }
        if (resourcePos != HEADER_ABSENT) {
            result.add(XOrganizationalExtension.KEY_RESOURCE);
        }
        if (rolePos != HEADER_ABSENT) {
            result.add(XOrganizationalExtension.KEY_ROLE);
        }
        if (perspectivePos != null && !perspectivePos.isEmpty()) {
            for (Integer i : perspectivePos) {
                if (eventAttributesPos.contains(i)) {
                    result.add(header.get(i));
                } else {
                    throw new InvalidLogMetadataException("Found invalid Log Metadata that eventAttributesPos doesn't"
                                                          + " match with perspectivePos");
                }
            }
        }

        return result;
    }

    public Map<Integer, DataType> getColumnDataTypes() {
        final Map<Integer, DataType> colPositionToDataType = new HashMap<>();
        this.getDoubleAttributesPos().forEach(integer -> colPositionToDataType.put(integer, DataType.REAL));
        this.getStringAttributesPos().forEach(integer -> colPositionToDataType.put(integer, DataType.STRING));
        this.getIntegerAttributesPos().forEach(integer -> colPositionToDataType.put(integer, DataType.INTEGER));
        this.getTimestampAttributesPos()
            .forEach(integer -> colPositionToDataType.put(integer, DataType.TIMESTAMP));

        return colPositionToDataType;
    }

    public Map<Integer, AttributeType> getColumnAttributeTypes() {
        final Map<Integer, AttributeType> colPositionToAttributeType = new HashMap<>();

        colPositionToAttributeType.put(this.getCaseIdPos(), CASE_ID);
        colPositionToAttributeType.put(this.getActivityPos(), ACTIVITY);
        colPositionToAttributeType.put(this.getStartTimestampPos(), START_TIME);
        colPositionToAttributeType.put(this.getEndTimestampPos(), END_TIME);
        colPositionToAttributeType.put(this.getResourcePos(), RESOURCE);
        colPositionToAttributeType.put(this.getRolePos(), ROLE);
        this.getEventAttributesPos()
            .forEach(integer -> colPositionToAttributeType.put(integer, EVENT_ATTRIBUTE));
        this.getCaseAttributesPos()
            .forEach(integer -> colPositionToAttributeType.put(integer, CASE_ATTRIBUTE));
        this.getIgnoredPos().forEach(integer -> colPositionToAttributeType.put(integer, IGNORED_ATTRIBUTE));

        return colPositionToAttributeType;
    }

    public Map<Integer, String> getColumnTimestampFormats() {
        Map<Integer, String> colPositionToTimestampFormat = new HashMap<>();

        if (this.getStartTimestampPos() != HEADER_ABSENT) {
            colPositionToTimestampFormat.put(getStartTimestampPos(), getStartTimestampFormat());
        }

        if (this.getEndTimestampPos() != HEADER_ABSENT) {
            colPositionToTimestampFormat.put(getEndTimestampPos(), getEndTimestampFormat());
        }

        if (otherTimestamps != null && !otherTimestamps.isEmpty()) {
            colPositionToTimestampFormat.putAll(otherTimestamps);
        }

        return colPositionToTimestampFormat;
    }
}
