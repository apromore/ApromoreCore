/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.eventlisteners;

import static org.apromore.commons.item.Constants.HOME_FOLDER_NAME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import org.apromore.dao.model.Log;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.components.AbstractController;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.utils.InputDialog;
import org.apromore.zk.notification.Notification;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.event.Event;

public class LogExportController extends AbstractController {
    private ContextData contextData;
    private UserOptionsData userOptions;
    private PDAnalyst analyst;

    public LogExportController(PDController controller) {
        super(controller);
        contextData = parent.getContextData();
        userOptions = parent.getUserOptions();
        analyst = parent.getProcessAnalyst();
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (!parent.prepareCriticalServices()) {
            return;
        }

        InputDialog.showInputDialog(
            // Labels.getLabel("e.pd.saveLogWin.text"), // "Save filtered log",
            parent.getLabel("saveLogWin_text"), "Enter a log name (no more than 100 characters)",
            contextData.getLogName() + "_filtered", null, null, returnEvent -> {
                if (returnEvent.getName().equals("onOK")) {
                    String logName = (String) returnEvent.getData();
                    // userOptions.setActivityFilterValue(activities.getCurpos());
                    // userOptions.setArcFilterValue(arcs.getCurpos());
                    saveLog(analyst.getXLog(), logName);
                }
            });
    }

    private void saveLog(XLog filteredLog, String logName) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            parent.getEventLogService().exportToStream(outputStream, filteredLog);

            Log log = parent.getEventLogService().importFilteredLog(contextData.getUsername(),
                contextData.getFolderId(), logName, new ByteArrayInputStream(outputStream.toByteArray()),
                "xes.gz", contextData.getDomain(),
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                false, false, contextData.getLogId());
            String folderName = log.getFolder() == null ? HOME_FOLDER_NAME : log.getFolder().getName();
            String notif = MessageFormat.format(parent.getLabel("successSaveLog_message"),
                "<strong>" + logName + "</strong>", "<strong>" + folderName + "</strong>");
            Notification.info(notif);
            parent.refreshPortal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
