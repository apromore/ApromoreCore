/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.service.logimporter.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apromore.service.logimporter.exception.InvalidLogMetadataException;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogProcessorImplTest {

    private LogProcessor logProcessor;

    @BeforeEach
    void setup() {
        logProcessor = new LogProcessorImpl();
    }

    @Test
    void should_process_log_with_valid_information() throws InvalidLogMetadataException {
        // given
        List<String> line =
            List.of("case_1", "Activity_1", "2011/02/16 14:31:00.000", "2011/02/16 15:23:00.000", "Frodo Baggins",
                "Ring bearer", "case_attr_1", "case_attr_2", "case_attr_3", "event_attr_1", "event_attr_2");
        List<String> header =
            List.of("CaseId", "Activity", "StartTimestamp", "EndTimestamp", "Resource", "Role", "CaseAttrHeader1",
                "CaseAttrHeader2", "CaseAttrHeader3", "EventAttrHeader1", "EventAttrHeader2");

        LogMetaData logMetaData = getLogMetadata(header);

        int lineIndex = 0;
        List<LogErrorReport> logErrorReport = new ArrayList<>();

        // when
        LogEventModel logEventModel = logProcessor.processLog(line, header, logMetaData, lineIndex, logErrorReport);

        // then
        assertEquals("case_1", logEventModel.getCaseID());
        assertEquals("Activity_1", logEventModel.getActivity());
        assertEquals("Frodo Baggins", logEventModel.getResource());
        assertEquals("Ring bearer", logEventModel.getRole());
        assertEquals(Timestamp.valueOf("2011-02-16 14:31:00.0"), logEventModel.getStartTimestamp());
        assertEquals(Timestamp.valueOf("2011-02-16 15:23:00.0"), logEventModel.getEndTimestamp());

        Map<String, String> expectedCaseAttributes =
            Map.of("CaseAttrHeader1", "case_attr_1",
                "CaseAttrHeader2", "case_attr_2",
                "CaseAttrHeader3", "case_attr_3");
        assertEquals(expectedCaseAttributes.size(), logEventModel.getCaseAttributes().size());

        assertTrue(logEventModel.getCaseAttributes().entrySet().stream()
            .allMatch(actualCaseAttributeMapEntry -> actualCaseAttributeMapEntry.getValue()
                .equals(expectedCaseAttributes.get(actualCaseAttributeMapEntry.getKey()))));

        Map<String, String> expectedEventAttributes =
            Map.of("EventAttrHeader1", "event_attr_1",
                "EventAttrHeader2", "event_attr_2");
        assertEquals(expectedEventAttributes.size(), logEventModel.getEventAttributes().size());

        assertTrue(logEventModel.getEventAttributes().entrySet().stream()
            .allMatch(actualEventAttributeMapEntry -> actualEventAttributeMapEntry.getValue()
                .equals(expectedEventAttributes.get(actualEventAttributeMapEntry.getKey()))));
    }

    @Test
    void should_return_error_report_for_invalid_information() throws InvalidLogMetadataException {
        // given
        List<String> line =
            List.of("", "", "", "", "", "");
        List<String> header =
            List.of("CaseId", "Activity", "StartTimestamp", "EndTimestamp", "Resource", "Role");

        LogMetaData logMetaData = getLogMetadata(header);

        int lineIndex = 0;
        List<LogErrorReport> logErrorReport = new ArrayList<>();

        // when
        LogEventModel logEventModel = logProcessor.processLog(line, header, logMetaData, lineIndex, logErrorReport);

        // then
        assertEquals("", logEventModel.getCaseID());
        assertEquals("", logEventModel.getActivity());
        assertEquals("", logEventModel.getResource());
        assertEquals("", logEventModel.getRole());
        assertNull(logEventModel.getStartTimestamp());
        assertNull(logEventModel.getEndTimestamp());

        assertEquals(5, logErrorReport.size());
        assertEquals("Case id is empty or has a null value!", logErrorReport.get(0).getError());
        assertEquals("Activity is empty or has a null value!", logErrorReport.get(1).getError());
        assertEquals("Invalid end timestamp due to wrong format or daylight saving!", logErrorReport.get(2).getError());
        assertEquals("Resource is empty or has a null value!", logErrorReport.get(3).getError());
        assertEquals("Role is empty or has a null value!", logErrorReport.get(4).getError());
    }

    @Test()
    void should_throw_exception_when_case_id_header_is_not_available() {
        // given
        List<String> line =
            List.of("", "", "", "", "", "");
        List<String> header =
            List.of("CaseId", "Activity", "StartTimestamp", "EndTimestamp", "Resource", "Role");

        LogMetaData logMetaData = getLogMetadata(header);
        logMetaData.setCaseIdPos(LogMetaData.HEADER_ABSENT);

        int lineIndex = 0;
        List<LogErrorReport> logErrorReport = new ArrayList<>();

        // when, then
        assertThrows(InvalidLogMetadataException.class, () -> {
            logProcessor.processLog(line, header, logMetaData, lineIndex, logErrorReport);
        });
    }

    private LogMetaData getLogMetadata(List<String> header) {
        LogMetaData logMetaData = new LogMetaData(header);
        logMetaData.setCaseIdPos(0);
        logMetaData.setActivityPos(1);
        logMetaData.setStartTimestampPos(2);
        logMetaData.setEndTimestampPos(3);
        logMetaData.setResourcePos(4);
        logMetaData.setRolePos(5);
        logMetaData.setCaseAttributesPos(List.of(6, 7, 8));
        logMetaData.setEventAttributesPos(List.of(9, 10));
        logMetaData.setStartTimestampFormat("yyyy/MM/dd HH:mm:ss.SSS");
        logMetaData.setEndTimestampFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return logMetaData;
    }
}