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

package org.apromore.plugin.portal.calendar.controllers;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.zk.label.LabelSupplier;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

public class EditRange extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(EditRange.class);
    private Map argMap = Executions.getCurrent().getArg();
    private CalendarController parentController = (CalendarController) argMap.get("parentController");
    private int dowIndex = (Integer) argMap.get("dowIndex");
    private int index = (Integer) argMap.get("index");
    private Date start = (Date) argMap.get("start");
    private Date end = (Date) argMap.get("end");

    //private int startHour = (Integer)argMap.get("startHour");
    //private int startMin = (Integer)argMap.get("startMin");
    //private int endHour = (Integer)argMap.get("endHour");
    //private int endMin = (Integer)argMap.get("endMin");

    @Wire("#startTimebox")
    Timebox startTimebox;
    @Wire("#endTimebox")
    Timebox endTimebox;
    @Wire("#deleteBtn")
    Button deleteBtn;
    @Wire("#saveBtn")
    Button saveBtn;
    @Wire("#cancelBtn")
    Button cancelBtn;

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        TimeZone tz = TimeZone.getTimeZone("UTC");

        startTimebox.setTimeZone(tz);
        endTimebox.setTimeZone(tz);
        startTimebox.setValue(start);
        endTimebox.setValue(end);
    }

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Listen("onClick = #deleteBtn")
    public void onClickDeleteButton() throws Exception {
        try {
            parentController.deleteRange(dowIndex, index);
            getSelf().detach();
        } catch (Exception e) {
            String msg = getLabels().getString("failed_delete_range_message");
            LOGGER.error(msg, e);
            Messagebox.show(msg);
        }
    }

    @Listen("onClick = #saveBtn")
    public void onClickSaveButton() throws Exception {

        try {
            LocalDateTime startTime = TimeUtils.dateToLocalDateTime(startTimebox.getValue());
            LocalDateTime endTime = TimeUtils.dateToLocalDateTime(endTimebox.getValue());
            int startHour = startTime.getHour();
            int startMin = startTime.getMinute();
            int endHour = endTime.getHour();
            int endMin = endTime.getMinute();
            parentController.updateRange(dowIndex, index, startHour, startMin, endHour, endMin);
        } catch (Exception e) {
            String msg = getLabels().getString("failed_save_range_message");
            LOGGER.error(msg, e);
            Messagebox.show(msg);
        }
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }
}
