/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.portal.dialogController;

import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.UserType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class KeepAliveController extends SelectorComposer<Component> {

    ManagerService managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
    @Wire
    private Div divKeepAlive;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        String id = Executions.getCurrent().getParameter("id");
        if (id == null) {
            throw new AssertionError("No id parameter in URL");
        }
        ApromoreSession session = UserSessionManager.getEditSession(id);
        EditSessionType editSession = session.getEditSession();

        divKeepAlive.addEventListener("onKeepAlive", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                UserType user = (UserType) Sessions.getCurrent().getAttributes().get("USER");
                Sessions.getCurrent().getAttribute("MAIN_CONTROLLER");
                Map<String, Object> arg = (Map<String, Object>) event.getData();
                String bpmnXML = arg.get("bpmnXML").toString();
                Integer processId = editSession.getProcessId();
                String currentVersion = editSession.getCurrentVersionNumber();
                String nativeType = arg.get("nativeType").toString();

//                System.out.println(bpmnXML);
                System.out.println(processId);
                System.out.println(currentVersion);

                managerService.updateDraft(processId, currentVersion, nativeType,
                        new ByteArrayInputStream(bpmnXML.getBytes()), user.getUsername());

                PortalLoggerFactory.getLogger(this.getClass()).debug("Keep BPMN Editor alive for user: " + user.getUsername());

//                divKeepAlive.setVisible(true);
                divKeepAlive.setStyle(divKeepAlive.getStyle().replace("hidden", "visible"));

//                divKeepAlive.appendChild(new Label("Saving..."));

            }
        });
    }


}
