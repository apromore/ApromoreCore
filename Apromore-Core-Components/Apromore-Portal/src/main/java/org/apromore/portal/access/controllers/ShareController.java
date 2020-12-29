/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
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

package org.apromore.portal.access.controllers;

import java.util.Map;
import java.util.HashMap;

import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;
import org.apromore.portal.model.UserType;

import org.apromore.portal.access.controllers.AccessController;

public class ShareController extends SelectorComposer<Window> {

    private Object selectedItem;
    private Boolean autoInherit;
    private Boolean showRelatedArtifacts;
    private Window win;

    public ShareController() throws Exception {
        Map<String, Object> argMap = (Map<String, Object>) Executions.getCurrent().getArg();

        selectedItem = argMap.get("selectedItem");
        autoInherit = (Boolean)argMap.get("autoInherit");
        showRelatedArtifacts = (Boolean)argMap.get("showRelatedArtifacts");
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        this.win = win;
        super.doAfterCompose(win);
        if (selectedItem.getClass().equals(LogSummaryType.class) || showRelatedArtifacts) {
            win.setWidth("1000px");
        } else {
            win.setWidth("500px");
        }
        EventQueues.lookup("accessControl", EventQueues.DESKTOP, true).subscribe(
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onClose".equals(evt.getName())) {
                            win.detach();
                        }
                    }
                });
    }
}
