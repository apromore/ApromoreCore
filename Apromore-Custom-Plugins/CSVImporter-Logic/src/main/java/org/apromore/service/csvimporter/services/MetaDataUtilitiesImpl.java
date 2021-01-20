/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.service.csvimporter.services;

import static org.apromore.service.csvimporter.constants.Constants.possibleActivity;
import static org.apromore.service.csvimporter.constants.Constants.possibleCaseId;
import static org.apromore.service.csvimporter.constants.Constants.possibleEndTimestamp;
import static org.apromore.service.csvimporter.constants.Constants.possibleOtherTimestamp;
import static org.apromore.service.csvimporter.constants.Constants.possibleResource;
import static org.apromore.service.csvimporter.constants.Constants.possibleStartTimestamp;
import static org.apromore.service.csvimporter.constants.Constants.timestampPattern;
import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;
import static org.apromore.service.csvimporter.dateparser.DateUtil.parseToTimestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apromore.service.csvimporter.model.CaseAttributesDiscovery;
import org.apromore.service.csvimporter.model.LogMetaData;
import org.apromore.service.csvimporter.utilities.NameComparator;

public class MetaDataUtilitiesImpl implements MetaDataUtilities {
    private List<List<String>> lines;
    private LogMetaData logMetaData;

    @Override
    public LogMetaData processMetaData(LogMetaData logMetaData, List<List<String>> lines) {
        this.logMetaData = logMetaData;
        this.lines = lines;

        setUniqueAttributes();
        setOtherTimestamps();
        onlyOnetimestampFound();
        setEventAttributesPos();
        setCaseAttributesPos();

        return this.logMetaData;
    }

    private void setUniqueAttributes() {
        //Reset schema
        this.logMetaData.setCaseIdPos(-1);
        this.logMetaData.setActivityPos(-1);
        this.logMetaData.setEndTimestampPos(-1);
        this.logMetaData.setStartTimestampPos(-1);
        this.logMetaData.setResourcePos(-1);

        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            String value = logMetaData.getHeader().get(pos);
	    if (this.logMetaData.getCaseIdPos() == -1 && match(possibleCaseId, value)) {
                this.logMetaData.setCaseIdPos(pos);
            } else if (this.logMetaData.getActivityPos() == -1 && match(possibleActivity, value)) {
                this.logMetaData.setActivityPos(pos);
            } else if (this.logMetaData.getEndTimestampPos() == -1 && match(possibleEndTimestamp, value) && isTimestamp(pos, this.lines)) {
                this.logMetaData.setEndTimestampPos(pos);
                this.logMetaData.setEndTimestampFormat(detectDateTimeFormat(pos));
            } else if (this.logMetaData.getStartTimestampPos() == -1 && match(possibleStartTimestamp, value) && isTimestamp(pos, this.lines)) {
                this.logMetaData.setStartTimestampPos(pos);
                this.logMetaData.setStartTimestampFormat(detectDateTimeFormat(pos));
            } else if (this.logMetaData.getResourcePos() == -1 && match(possibleResource, value)) {
                this.logMetaData.setResourcePos(pos);
            }
        }
    }

    @Override
    public boolean isTimestamp(int pos, List<List<String>> lines) {
        int emptyCount = 0;
        for (List<String> myLine : lines) {
            if (myLine.get(pos).isEmpty() || determineDateFormat(myLine.get(pos)) == null) {
                emptyCount++;
            }
        }
        return emptyCount < lines.size();
    }

    @Override
    public boolean isTimestamp(int pos, String format, List<List<String>> lines) {
        int emptyCount = 0;
        for (List<String> myLine : lines) {
            if (myLine.get(pos).isEmpty() || parseToTimestamp(myLine.get(pos), format, null) == null)
                emptyCount++;
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
	    return dateTimeFormatCollections.parallelStream()
                    .collect(Collectors.groupingBy(java.util.function.Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(
                        //Map.Entry::getValue  // Java 8
                        new java.util.function.Function<Map.Entry<String, Long>, Long>() {
                            public Long apply(Map.Entry<String, Long> entry) {
                                return entry.getValue();
                            }
                        }
                    ))
                    .get()
                    .getKey();
        } else {
            return null;
        }
    }

    private void setOtherTimestamps() {
        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            if (isNOTUniqueAttribute(pos) && couldBeTimestamp(pos) && isTimestamp(pos, this.lines)) {
                logMetaData.getOtherTimestamps().put(pos, detectDateTimeFormat(pos));
            }
        }
    }

    private boolean couldBeTimestamp(int pos) {
        if (match(possibleOtherTimestamp, logMetaData.getHeader().get(pos))) {
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
        for (int pos = 0; pos < logMetaData.getHeader().size(); pos++) {
            if (isNOTUniqueAttribute(pos) && !logMetaData.getOtherTimestamps().containsKey(pos)) {
                logMetaData.getEventAttributesPos().add(pos);
            }
        }
    }

    // If only one timestamp found then set it as endTimestamp
    private void onlyOnetimestampFound() {
        if (logMetaData.getEndTimestampPos() == -1 && logMetaData.getStartTimestampPos() == -1 && logMetaData.getOtherTimestamps().size() > 0) {
            logMetaData.setEndTimestampPos(logMetaData.getOtherTimestamps().keySet().stream().findFirst().get());
            logMetaData.getOtherTimestamps().remove(logMetaData.getEndTimestampPos());
            logMetaData.setEndTimestampFormat(detectDateTimeFormat(logMetaData.getEndTimestampPos()));

        } else if (logMetaData.getEndTimestampPos() == -1 &&
                (logMetaData.getOtherTimestamps() == null || logMetaData.getOtherTimestamps().isEmpty())
                && logMetaData.getStartTimestampPos() != -1) {
            logMetaData.setEndTimestampPos(logMetaData.getStartTimestampPos());
            logMetaData.setStartTimestampPos(-1);
            logMetaData.setEndTimestampFormat(detectDateTimeFormat(logMetaData.getEndTimestampPos()));
        }
    }

    private void setCaseAttributesPos() {
        if (logMetaData.getCaseIdPos() != -1 && logMetaData.getEventAttributesPos() != null && !logMetaData.getEventAttributesPos().isEmpty()) {
            // sort by case id
            List<List<String>> myLines = new ArrayList<>(lines);
            Comparator<String> nameOrder = new NameComparator();
            myLines.sort(
                //(o1, o2) -> nameOrder.compare(o1.get(logMetaData.getCaseIdPos()), o2.get(logMetaData.getCaseIdPos()))  // Java 8
                new java.util.Comparator<List<String>>() {
                    public int compare(List<String> o1, List<String> o2) {
                        return nameOrder.compare(o1.get(logMetaData.getCaseIdPos()), o2.get(logMetaData.getCaseIdPos()));
                    }
                }
            );

            List<CaseAttributesDiscovery> discoverList;
            Iterator<Integer> iterator = logMetaData.getEventAttributesPos().iterator();
            while (iterator.hasNext()) {
                discoverList = new ArrayList<>();
                boolean caseAttribute = true;
                int pos = (int) iterator.next();
                for (List<String> myLine : myLines) {
                    if (discoverList.isEmpty() || discoverList.stream().noneMatch(
                        //p -> p.getCaseId().equals(myLine.get(logMetaData.getCaseIdPos()))  // Java 8
                        new java.util.function.Predicate<CaseAttributesDiscovery>() {
                            public boolean test(CaseAttributesDiscovery p) {
                                return p.getCaseId().equals(myLine.get(logMetaData.getCaseIdPos()));
                            }
                        }
                    )) { // new case id
                        discoverList = new ArrayList<>();
                        discoverList.add(new CaseAttributesDiscovery(myLine.get(logMetaData.getCaseIdPos()), pos, myLine.get(pos)));
                    } else if (!discoverList.stream().filter(
                        //p -> p.getPosition() == pos  // Java 8
                        new java.util.function.Predicate<CaseAttributesDiscovery>() {
                            public boolean test(CaseAttributesDiscovery p) {
                                return p.getPosition() == pos;
                            }
                        }
                    ).collect(Collectors.toList()).get(0).getValue().equals(myLine.get(pos))) {
                        caseAttribute = false;
                        break;
                    }
                }
                if (caseAttribute) {
                    logMetaData.getCaseAttributesPos().add(pos);
                    iterator.remove();
                }
            }
        }
    }

    private boolean isNOTUniqueAttribute(int pos) {
        return (pos != logMetaData.getCaseIdPos() &&
                pos != logMetaData.getActivityPos() &&
                pos != logMetaData.getEndTimestampPos() &&
                pos != logMetaData.getStartTimestampPos() &&
                pos != logMetaData.getResourcePos());
    }
}
