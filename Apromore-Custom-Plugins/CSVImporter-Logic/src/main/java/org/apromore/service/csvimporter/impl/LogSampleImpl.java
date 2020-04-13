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

import lombok.Data;
import org.apromore.service.csvimporter.LogSample;

import java.util.*;
import java.util.stream.Collectors;


@Data
class LogSampleImpl implements LogSample, Constants {

    private final Parse parse = new Parse();

    private List<String> header;
    private List<List<String>> lines;

    private int caseIdPos;
    private int activityPos;
    private int endTimestampPos;
    private int startTimestampPos;
    private int resourcePos;
    private List<Integer> caseAttributesPos;
    private List<Integer> eventAttributesPos;
    private HashMap<Integer, String> otherTimestamps;
    private List<Integer> ignoredPos;
    private String endTimestampFormat;
    private String startTimestampFormat;

    LogSampleImpl(List<String> header, List<List<String>> lines) {
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
        setCaseAndEventAttributesPos();
    }


    private void setUniqueAttributes(){
        caseIdPos = -1;
        activityPos = -1;
        endTimestampPos = -1;
        startTimestampPos = -1;
        resourcePos = -1;

        for (int pos = 0; pos < header.size(); pos++) {
            if (caseIdPos == -1 && guessUniqueAttributesPos(caseIdValues, header.get(pos))) {
                caseIdPos = pos;
            } else if (activityPos == -1 && guessUniqueAttributesPos(activityValues, header.get(pos))) {
                activityPos = pos;
            } else if (endTimestampPos == -1 && guessUniqueAttributesPos(timestampValues, header.get(pos)) && isParsable(pos)) {
                endTimestampPos =pos;
            } else if (startTimestampPos == -1 && guessUniqueAttributesPos(StartTsValues, header.get(pos)) && isParsable(pos)) {
                startTimestampPos =pos;
            } else if (resourcePos == -1 && guessUniqueAttributesPos(resourceValues, header.get(pos))) {
                resourcePos = pos;
            }
        }
    }

    private boolean guessUniqueAttributesPos(String[] col, String elem) {
        if (col == timestampValues || col == StartTsValues) {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        } else {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::contains);
        }
    }

    @Override
    public boolean isParsable(int pos) {
        for (List<String> myLine : lines) {
            if (parse.tryParsing(myLine.get(pos)) != null) {
                return true;
            }
        }
        return false;
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
        for (int pos = 0; pos < header.size(); pos++) {
            if ((pos != endTimestampPos) && (pos != startTimestampPos) && isParsable(pos)) {
                otherTimestamps.put(pos, null);
            }
        }
    }


    private void setCaseAndEventAttributesPos() {
        if (caseIdPos == -1) return;

        // set all attributes that are not main attributes or timestamps as case attributes
        for (int pos = 0; pos < header.size(); pos++) {
            if (!(pos == caseIdPos || pos == activityPos || pos == endTimestampPos || pos == startTimestampPos || pos == resourcePos) && !otherTimestamps.containsKey(pos)) {
                caseAttributesPos.add(pos);
            }
        }

        // set ones who are not consistent within a case as event attributes instead of case attributes
        List<CaseAttributesDiscovery> myCaseAttributes = new ArrayList<>();
        for (List<String> myLine : lines) {
            if (myCaseAttributes.size() == 0 || myCaseAttributes.stream().noneMatch(p -> p.getCaseId().equals(myLine.get(caseIdPos)))) { // new case id
                myCaseAttributes = new ArrayList<>();
                for (int pos : caseAttributesPos) {
                    myCaseAttributes.add(new CaseAttributesDiscovery(myLine.get(caseIdPos), pos, myLine.get(pos)));
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
}
