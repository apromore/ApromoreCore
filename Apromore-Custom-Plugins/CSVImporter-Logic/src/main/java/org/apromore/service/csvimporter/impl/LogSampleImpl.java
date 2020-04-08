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

package org.apromore.service.csvimporter.impl;

import org.apromore.service.csvimporter.LogSample;

import java.util.*;
import java.util.stream.Collectors;


class LogSampleImpl implements LogSample, Constants {

    private final Parse parse = new Parse();

    private List<String> header;
    private List<List<String>> lines;
    private Map<String, Integer> uniqueAttributes;
    private String timestampFormat;
    private String startTsFormat;
    private List<Integer> ignoredPos;
    private HashMap<Integer, String> otherTimestamps;
    private List<Integer> caseAttributesPos;
    private List<Integer> eventAttributesPos;

    LogSampleImpl(List<String> header, List<List<String>> lines) {
        this.header = header;
        this.lines = lines;
        this.ignoredPos = new ArrayList<>();
        this.caseAttributesPos = new ArrayList<>();
        this.eventAttributesPos = new ArrayList<>();
        this.timestampFormat = null;
        this.startTsFormat = null;
        otherTimestamps = new HashMap<>();

        setUniqueAttributes();
        setOtherTimestamps();
        setCaseAndEventAttributesPos();
    }


    private void setUniqueAttributes(){
        uniqueAttributes = new HashMap<>();
        uniqueAttributes.put(caseIdLabel, -1);
        uniqueAttributes.put(activityLabel, -1);
        uniqueAttributes.put(timestampLabel, -1);
        uniqueAttributes.put(startTimestampLabel, -1);
        uniqueAttributes.put(resourceLabel, -1);

        // Populate mainAttributes, timestampFormat, startTsFormat
        for (int pos = 0; pos < header.size(); pos++) {
            if ((uniqueAttributes.get(caseIdLabel) == -1) && getMainAttributePosition(caseIdValues, header.get(pos))) {
                uniqueAttributes.put(caseIdLabel, pos);
            } else if ((uniqueAttributes.get(activityLabel) == -1) && getMainAttributePosition(activityValues, header.get(pos))) {
                uniqueAttributes.put(activityLabel, pos);
            } else if ((uniqueAttributes.get(timestampLabel) == -1) && getMainAttributePosition(timestampValues, header.get(pos).toLowerCase()) && isParsable(pos)) {
                uniqueAttributes.put(timestampLabel, pos);
            } else if ((uniqueAttributes.get(startTimestampLabel) == -1) && getMainAttributePosition(StartTsValues, header.get(pos)) && isParsable(pos)) {
                uniqueAttributes.put(startTimestampLabel, pos);
            } else if ((uniqueAttributes.get(resourceLabel) == -1) && getMainAttributePosition(resourceValues, header.get(pos))) {
                uniqueAttributes.put(resourceLabel, pos);
            }
        }
    }

    private boolean getMainAttributePosition(String[] col, String elem) {
        if (col == timestampValues || col == StartTsValues) {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        } else {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::contains);
        }
    }

    @Override
    public boolean isParsable(int pos) {
        for (List<String> myLine : lines) {
            if (parse.tryParsing(myLine.get(pos)) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isParsableWithFormat(int pos, String format) {
        for (List<String> myLine : lines) {
            if (format == null || format.length() != myLine.get(pos).length() || parse.tryParsingWithFormat(myLine.get(pos), format) == null) {
                return false;
            }
        }
        return true;
    }


    private void setOtherTimestamps() {
        int timeStampPos = uniqueAttributes.get(timestampLabel);
        int StartTimeStampPos = uniqueAttributes.get(startTimestampLabel);

        for (int pos = 0; pos < header.size(); pos++) {
            if ((pos != timeStampPos) && (pos != StartTimeStampPos) && isParsable(pos)) {
                otherTimestamps.put(pos, null);
            }
        }
    }


    private void setCaseAndEventAttributesPos() {
        if (uniqueAttributes.get(caseIdLabel) == -1) return;

        // set all attributes that are not main attributes or timestamps as case attributes
        for (int pos = 0; pos < header.size(); pos++) {
            if (!uniqueAttributes.containsValue(pos) && !otherTimestamps.containsKey(pos)) {
                caseAttributesPos.add(pos);
            }
        }

        // set ones who are not consistent within a case as event attributes instead of case attributes
        List<CaseAttributesDiscovery> myCaseAttributes = new ArrayList<>();
        for (List<String> myLine : lines) {
            if (myCaseAttributes.size() == 0 || myCaseAttributes.stream().noneMatch(p -> p.getCaseId().equals(myLine.get(uniqueAttributes.get(caseIdLabel))))) { // new case id
                myCaseAttributes = new ArrayList<>();
                for (int pos : caseAttributesPos) {
                    myCaseAttributes.add(new CaseAttributesDiscovery(myLine.get(uniqueAttributes.get(caseIdLabel)), pos, myLine.get(pos)));
                }
            } else {
                for (int pos = 0; pos < caseAttributesPos.size(); pos++) {
                    int finalPos = caseAttributesPos.get(pos);
                    if (!myCaseAttributes.stream().filter(p -> p.getPosition() == finalPos).collect(Collectors.toList()).get(0).getValue().equals(myLine.get(finalPos)) && caseAttributesPos.contains(finalPos)) {
                        caseAttributesPos.remove(new Integer(finalPos));
                        eventAttributesPos.add(finalPos);
                    }
                }
            }
        }
    }


    // Accessors
    @Override
    public String getCaseIdLabel() {
        return caseIdLabel;
    }

    @Override
    public String getActivityLabel() {
        return activityLabel;
    }

    @Override
    public String getTimestampLabel() {
        return timestampLabel;
    }

    @Override
    public String getStartTimestampLabel() {
        return startTimestampLabel;
    }

    @Override
    public String getOtherTimestampLabel() {
        return otherTimestampLabel;
    }

    @Override
    public String getResourceLabel() {
        return resourceLabel;
    }

    @Override
    public List<String> getHeader() {
        return header;
    }

    @Override
    public List<List<String>> getLines() {
        return lines;
    }

    @Override
    public Map<String, Integer> getUniqueAttributes() {
        return uniqueAttributes;
    }

    @Override
    public List<Integer> getCaseAttributesPos() {
        return caseAttributesPos;
    }

    @Override
    public List<Integer> getEventAttributesPos() {
        return eventAttributesPos;
    }

    @Override
    public List<Integer> getIgnoredPos() {
        return ignoredPos;
    }

    @Override
    public HashMap<Integer, String> getOtherTimestamps() {
        return otherTimestamps;
    }

    @Override
    public String getTimestampFormat() {
        return timestampFormat;
    }

    @Override
    public void setTimestampFormat(String s) {
        timestampFormat = s;
    }

    @Override
    public String getStartTsFormat() {
        return startTsFormat;
    }

    @Override
    public void setStartTsFormat(String s) {
        startTsFormat = s;
    }

}
