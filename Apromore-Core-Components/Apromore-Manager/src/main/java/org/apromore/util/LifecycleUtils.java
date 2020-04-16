/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.util;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LifecycleUtils {

    private static final Logger log = LoggerFactory.getLogger(LifecycleUtils.class);

    public static void init(Object o) throws Exception {
        if (o instanceof Initializable) {
            init((Initializable) o);
        }
    }

    public static void init(Initializable initializable) throws Exception {
        initializable.init();
    }

    /**
     * Calls {@link #init(Object) init} for each object in the collection.  If the collection is {@code null} or empty,
     * this method returns quietly.
     *
     * @param c the collection containing objects to {@link #init init}.
     * @throws Exception if unable to initialize one or more instances.
     * @since 7.10
     */
    public static void init(Collection c) throws Exception {
        if (c == null || c.isEmpty()) {
            return;
        }
        for (Object o : c) {
            init(o);
        }
    }

    public static void destroy(Object o) {
        if (o instanceof Destroyable) {
            destroy((Destroyable) o);
        } else if (o instanceof Collection) {
            destroy((Collection)o);
        }
    }

    public static void destroy(Destroyable d) {
        if (d != null) {
            try {
                d.destroy();
            } catch (Throwable t) {
                if (log.isDebugEnabled()) {
                    String msg = "Unable to cleanly destroy instance [" + d + "] of type [" + d.getClass().getName() + "].";
                    log.debug(msg, t);
                }
            }
        }
    }

    /**
     * Calls {@link #destroy(Object) destroy} for each object in the collection.
     * If the collection is {@code null} or empty, this method returns quietly.
     *
     * @param c the collection of objects to destroy.
     * @since 7.10
     */
    public static void destroy(Collection c) {
        if (c == null || c.isEmpty()) {
            return;
        }

        for (Object o : c) {
            destroy(o);
        }
    }
}
