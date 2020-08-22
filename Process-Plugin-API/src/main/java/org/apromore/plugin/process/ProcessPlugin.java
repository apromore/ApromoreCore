/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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
package org.apromore.plugin.process;

import org.apromore.plugin.Plugin;
import org.apromore.portal.helper.Version;

/**
 * Plug-in interface providing a hook into the process storage service.
 */
public interface ProcessPlugin extends Plugin {

    /**
     * Notifies a client that it has been attached to the process store.
     */
    void bindToProcessService();

    /**
     * Notifies a client that it is about to be detached from the process store.
     */
    void unbindFromProcessService();

    /**
     * Notifies a client that the process store has changed.
     *
     * In most cases a follow-up call to the process service will be needed to determine the nature of the change.
     *
     * TODO: Accomplish the lookup using a passed <var>processService</var> instance, which is read-only
     * and therefore prevents endless cascades of process change notifications.
     *
     * If an exception is thrown by this method, the process store will log that fact but disregard your tears.
     *
     * @param processId  the process identifier of a process model version which is either newly created, modified, or deleted.
     * @param branch  the branch of a process model version which is either newly created, modified, or deleted.
     * @param version  the version of a process model version which is either newly created, modified, or deleted.
     * @throws ProcessPluginException
     */
    void processChanged(int processId, String branch, Version version) throws ProcessChangedException;

    /** Requests rollback by implementations of the {@link #processChanged} method. */
    static class ProcessChangedException extends Exception {}
}
