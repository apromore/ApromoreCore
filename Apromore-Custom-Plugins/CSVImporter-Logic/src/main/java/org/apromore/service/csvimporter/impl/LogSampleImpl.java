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

import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogSample;
import org.zkoss.zul.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A sample of a CSV log.
 */
class LogSampleImpl implements LogSample, Constants {

    private final Parse parse = new Parse();

    private List<String> header;
    private List<List<String>> lines;
    private Map<String, Integer> mainAttributes;
    private String timestampFormat;
    private String startTsFormat;
    private List<Integer> ignoredPos;
    private HashMap<Integer, String> otherTimeStampsPos;
    private List<Integer> caseAttributesPos;


    // Constructor

    LogSampleImpl(List<String> header, List<List<String>> lines) throws InvalidCSVException {
        this.header = header;
        this.lines = lines;

        // Empty mainAttributes permutation map (heads)
        mainAttributes = new HashMap<>();
        mainAttributes.put(caseIdLabel, -1);
        mainAttributes.put(activityLabel, -1);
        mainAttributes.put(timestampLabel, -1);
        mainAttributes.put(startTimestampLabel, -1);
        mainAttributes.put(resourceLabel, -1);

        // Populate heads, timestampFormat, startTsFormat
        for (int pos = 0; pos < header.size(); pos++) {
            if ((mainAttributes.get(caseIdLabel) == -1) && getMainAttributePosition(caseIdValues, header.get(pos))) {
                mainAttributes.put(caseIdLabel, pos);
            } else if ((mainAttributes.get(activityLabel) == -1) && getMainAttributePosition(activityValues, header.get(pos))) {
                mainAttributes.put(activityLabel, pos);
            } else if ((mainAttributes.get(timestampLabel) == -1) && getMainAttributePosition(timestampValues, header.get(pos).toLowerCase()) && isParsable(pos)) {
                mainAttributes.put(timestampLabel, pos);
            } else if ((mainAttributes.get(startTimestampLabel) == -1) && getMainAttributePosition(StartTsValues, header.get(pos)) && isParsable(pos)) {
                mainAttributes.put(startTimestampLabel, pos);
            } else if ((mainAttributes.get(resourceLabel) == -1) && getMainAttributePosition(resourceValues, header.get(pos))) {
                mainAttributes.put(resourceLabel, pos);
            }
        }

        this.ignoredPos = new ArrayList<>();
        this.otherTimeStampsPos = new HashMap<>();
        this.caseAttributesPos = new ArrayList<>();
        this.timestampFormat = null;
        this.startTsFormat = null;

        setOtherTimestamps();
        setCaseAttributesPos();
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
        otherTimeStampsPos.clear();
        Integer timeStampPos = mainAttributes.get(timestampLabel);
        Integer StartTimeStampPos = mainAttributes.get(startTimestampLabel);

        for (int pos = 0; pos < header.size(); pos++) {
            if ((pos != timeStampPos) && (pos != StartTimeStampPos) && isParsable(pos)) {
                otherTimeStampsPos.put(pos, null);
            }
        }
    }


    private void setCaseAttributesPos() {
        if (mainAttributes.get(caseIdLabel) == -1) return;

        // set all attributes that are not main attributes or timestamps as case attributes
        for (int pos = 0; pos < header.size(); pos++) {
            if (pos != mainAttributes.get(caseIdLabel) && pos != mainAttributes.get(activityLabel) && pos != mainAttributes.get(timestampLabel) && pos != mainAttributes.get(startTimestampLabel) && pos != mainAttributes.get(resourceLabel) && !otherTimeStampsPos.containsKey(pos)) {
                caseAttributesPos.add(pos);
            }
        }

        // remove ones who fail to satisfy case attribute condition - Being consistent within a case.
        List<CaseAttributesDiscovery> myCaseAttributes = new ArrayList<>();
        for (List<String> myLine : lines) {
            if (myCaseAttributes.size() == 0 || myCaseAttributes.stream().noneMatch(p -> p.getCaseId().equals(myLine.get(mainAttributes.get(caseIdLabel))))) { // new case id
                myCaseAttributes = new ArrayList<>();
                for (Integer pos : caseAttributesPos) {
                    myCaseAttributes.add(new CaseAttributesDiscovery(myLine.get(mainAttributes.get(caseIdLabel)), pos, myLine.get(pos)));
                }
            } else {
                for (int pos = 0; pos < caseAttributesPos.size(); pos++) {
                    int finalPos = caseAttributesPos.get(pos);
                    if (!myCaseAttributes.stream().filter(p -> p.getPosition() == finalPos).collect(Collectors.toList()).get(0).getValue().equals(myLine.get(finalPos)) && caseAttributesPos.contains(finalPos)) {
                        caseAttributesPos.remove(new Integer(finalPos));
                    }
                }
            }
        }
    }


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
    public Map<String, Integer> getMainAttributes() {
        return mainAttributes;
    }

    @Override
    public List<Integer> getCaseAttributesPos() {
        return caseAttributesPos;
    }

    @Override
    public List<Integer> getIgnoredPos() {
        return ignoredPos;
    }

    @Override
    public HashMap<Integer, String> getOtherTimeStampsPos() {
        return otherTimeStampsPos;
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
