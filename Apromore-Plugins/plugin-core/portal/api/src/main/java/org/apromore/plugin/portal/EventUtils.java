/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal;

import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;

@Slf4j
@UtilityClass
public class EventUtils {
    public void broadcastMessage(Map<String, Object> args, Desktop desktop, String queueId, String command) {
        try {
            Executions.activate(desktop);
            EventQueues.lookup(queueId, EventQueues.APPLICATION, true)
                .publish(new Event(command, null, args));
            Executions.deactivate(desktop);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (DesktopUnavailableException e) {
            broadcastMessage(queueId, args, command);
            log.warn(e.getMessage(), e);
        }
    }

    public void broadcastMessage(String queueId, Map<String, Object> args, String command) {
        EventQueues.lookup(queueId, EventQueues.APPLICATION, true)
            .publish(new Event(command, null, args));
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
