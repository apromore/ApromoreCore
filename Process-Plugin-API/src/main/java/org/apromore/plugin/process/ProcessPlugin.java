/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.plugin.process;

import org.apromore.plugin.Plugin;

/**
 * Plug-in interface providing a hook into the process storage service.
 */
public interface ProcessPlugin extends Plugin {

    /**
     * Notifies a client that the process store has changed.
     *
     * In most cases a follow-up call to the process service will be needed to determine the nature of the change.
     * This should only be accomplished using the passed <var>processService</var> instance, which is read-only
     * and therefore prevents endless cascades of process change notifications.
     *
     * If an exception is thrown by this method, the process store will roll back the current transaction.
     *
     * @param processId  the identifier of a process which is either newly created, modified, or deleted.
     * @param processService  a read-only view of the process store
     * @throws ProcessPluginException
     */
    void processChanged(int processId /*, ProcessService processService*/) throws ProcessChangedException;

    /** Requests rollback by implementations of the {@link #processChanged} method. */
    static class ProcessChangedException extends Exception {}
}
