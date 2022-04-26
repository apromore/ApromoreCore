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

package org.apromore.plugin.portal;

import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;

@Slf4j
@UtilityClass
public class EventUtils {
    public void broadcastMessage(Map args, Desktop desktop, String queueId, String command) {
        try {
            Executions.activate(desktop);
            EventQueues.lookup(queueId, EventQueues.APPLICATION, true)
                .publish(new Event(command, null, args));
            Executions.deactivate(desktop);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    public EventListener<Event> registerViewModelToEventListener(String queueName) {
        EventListener<Event> eventListener = event -> BindUtils
            .postGlobalCommand(null, null, event.getName(), (Map) event.getData());

        // Register ZK event handler
        EventQueues.lookup(queueName, EventQueues.APPLICATION, true).subscribe(eventListener);

        log.info("Event registered with queueId = " + queueName);
        return eventListener;
    }
}
