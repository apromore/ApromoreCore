/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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

import lombok.Data;
import org.apromore.service.csvimporter.constants.Constants;
import org.apromore.service.csvimporter.dateparser.Parse;
import org.apromore.service.csvimporter.utilities.NameComparator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;
import static org.apromore.service.csvimporter.dateparser.DateUtil.parseToTimestamp;


@Data
public class LogSampleImpl implements LogSample, Constants {

//    private final Parse parse = new Parse();

    private List<String> header;
    private List<List<String>> lines;

    private int caseIdPos;
    private int activityPos;
    private int endTimestampPos;
    private int startTimestampPos;
    private int resourcePos;
    private List<Integer> caseAttributesPos;
    private List<Integer> eventAttributesPos;
    private HashMap<Integer, String> otherTimestamps; // store position as key and format as value
    private List<Integer> ignoredPos;
    private String endTimestampFormat;
    private String startTimestampFormat;

    public LogSampleImpl(List<String> header, List<List<String>> lines) throws Exception {
        this.header = header;
        this.lines = lines;

        caseAttributesPos = new ArrayList<>();
        eventAttributesPos = new ArrayList<>();
        ignoredPos = new ArrayList<>();
        otherTimestamps = new HashMap<>();
        endTimestampFormat = null;
        startTimestampFormat = null;

        setUniqueAttributes();
        setOtherTimestamps();
        onlyOnetimestampFound();
        setEventAttributesPos();
        setCaseAttributesPos();
        validateSample();
    }


    private void setUniqueAttributes() {
        caseIdPos = -1;
        activityPos = -1;
        endTimestampPos = -1;
        startTimestampPos = -1;
        resourcePos = -1;

        for (int pos = 0; pos < header.size(); pos++) {
            if (caseIdPos == -1 && match(possibleCaseId, header.get(pos))) {
                caseIdPos = pos;
            } else if (activityPos == -1 && match(possibleActivity, header.get(pos))) {
                activityPos = pos;
            } else if (endTimestampPos == -1 && match(possibleEndTimestamp, header.get(pos)) && isParsable(pos)) {
                endTimestampPos = pos;
                endTimestampFormat = detectDateTimeFormat(pos);
            } else if (startTimestampPos == -1 && match(possibleStartTimestamp, header.get(pos)) && isParsable(pos)) {
                startTimestampPos = pos;
                startTimestampFormat = detectDateTimeFormat(pos);
            } else if (resourcePos == -1 && match(possibleResource, header.get(pos))) {
                resourcePos = pos;
            }
        }
    }

    @Override
    public boolean isParsable(int pos) {
        int emptyCount = 0;
        for (List<String> myLine : lines) {
            if (myLine.get(pos).isEmpty() || determineDateFormat(myLine.get(pos)) == null) {
                emptyCount++;
            }
        }
        return emptyCount < lines.size();
    }

    private String detectDateTimeFormat(int pos) {

        List<String> dateTimeFormatCollections = new ArrayList<>();
        for (List<String> myLine : lines) {
            if (determineDateFormat(myLine.get(pos)) != null)
                dateTimeFormatCollections.add(determineDateFormat(myLine.get(pos)));
        }
        // Get the most common date format
        if (dateTimeFormatCollections.size() > 0) {
            return dateTimeFormatCollections.stream()
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue))
                    .get()
                    .getKey();
        } else {
            return null;
        }
    }

    @Override
    public boolean isParsableWithFormat(int pos, String format) {
        int emptyCount = 0;
        for (List<String> myLine : lines) {
            if (myLine.get(pos).isEmpty() || parseToTimestamp(myLine.get(pos), format) == null)
                emptyCount++;
        }
        return emptyCount < lines.size();
    }

    private void setOtherTimestamps() {
        for (int pos = 0; pos < header.size(); pos++) {
            if (isNOTUniqueAttribute(pos) && couldBeTimestamp(pos) && isParsable(pos)) {
                otherTimestamps.put(pos, null);
            }
        }
    }

    private boolean couldBeTimestamp(int pos) {
        if (match(possibleOtherTimestamp, header.get(pos))) {
            return true;
        }

        for (List<String> myLine : lines) {
            if (match(timestampPattern, myLine.get(pos))) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String myPattern, String myValue) {
        Pattern pattern = Pattern.compile(myPattern);
        Matcher match = pattern.matcher(myValue.replace("\uFEFF", "").toLowerCase()); //﻿ remove ﻿﻿﻿﻿\uFEFF for UTF-8 With BOM encoding
        return match.find();
    }


    private void setEventAttributesPos() {
        // set all attributes that are not main attributes or timestamps as event attributes
        for (int pos = 0; pos < header.size(); pos++) {
            if (isNOTUniqueAttribute(pos) && !otherTimestamps.containsKey(pos)) {
                eventAttributesPos.add(pos);
            }
        }
    }

    // If only one timestamp found then set it as endTimestamp
    private void onlyOnetimestampFound() {
        if (endTimestampPos == -1 && startTimestampPos == -1 && otherTimestamps.size() == 1) {
            endTimestampPos = otherTimestamps.keySet().stream().findFirst().get();
            otherTimestamps.remove(endTimestampPos);
            endTimestampFormat = detectDateTimeFormat(endTimestampPos);
        } else if (endTimestampPos == -1 && (otherTimestamps == null || otherTimestamps.isEmpty()) && startTimestampPos != -1) {
            endTimestampPos = startTimestampPos;
            startTimestampPos = -1;
            endTimestampFormat = detectDateTimeFormat(endTimestampPos);
        }
    }

    private void setCaseAttributesPos() {
        if (caseIdPos != -1 && eventAttributesPos != null && !eventAttributesPos.isEmpty()) {
            // sort by case id
            List<List<String>> myLines = new ArrayList<>(lines);
            Comparator<String> nameOrder = new NameComparator();
            myLines.sort((o1, o2) -> nameOrder.compare(o1.get(caseIdPos), o2.get(caseIdPos)));

            List<CaseAttributesDiscovery> discoverList;
            Iterator<Integer> iterator = eventAttributesPos.iterator();
            while (iterator.hasNext()) {
                discoverList = new ArrayList<>();
                boolean caseAttribute = true;
                int pos = (int) iterator.next();
                for (List<String> myLine : myLines) {
                    if (discoverList.isEmpty() || discoverList.stream().noneMatch(p -> p.getCaseId().equals(myLine.get(caseIdPos)))) { // new case id
                        discoverList = new ArrayList<>();
                        discoverList.add(new CaseAttributesDiscovery(myLine.get(caseIdPos), pos, myLine.get(pos)));
                    } else if (!discoverList.stream().filter(p -> p.getPosition() == pos).collect(Collectors.toList()).get(0).getValue().equals(myLine.get(pos))) {
                        caseAttribute = false;
                        break;
                    }
                }
                if (caseAttribute) {
                    caseAttributesPos.add(pos);
                    iterator.remove();
                }
            }
        }
    }

    private boolean isNOTUniqueAttribute(int pos) {
        return (pos != caseIdPos && pos != activityPos && pos != endTimestampPos && pos != startTimestampPos && pos != resourcePos);
    }

    @Override
    public void validateSample() throws Exception {
        int count = 0;
        if (caseIdPos != -1) count++;
        if (activityPos != -1) count++;
        if (endTimestampPos != -1) count++;
        if (startTimestampPos != -1) count++;
        if (resourcePos != -1) count++;

        count += otherTimestamps.size();
        count += eventAttributesPos.size();
        count += caseAttributesPos.size();
        count += ignoredPos.size();

        if (header.size() != count) {
            throw new Exception("Failed to construct valid log sample! Contact out support.");
        }
    }
}