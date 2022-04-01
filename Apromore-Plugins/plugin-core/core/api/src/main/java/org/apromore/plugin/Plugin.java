/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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
package org.apromore.plugin;


/**
 * <p>
 * Interface implemented by every Apromore {@link Plugin}. Each {@link Plugin} offers theses methods, so it is possible to handle all Plugins in a generic manner.
 * Please note implementations should make sure they override {@link #equals(Object)} and {@link #hashCode()} in a way, that two Plugins are the same if
 * their name and version match.
 *
 * <p>
 * Plugins are usually only instantiated once in the whole system. (Singleton) So please be careful about the global state of the Plugin!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface Plugin {

    /**
     * Plugin name should be short and unique together with the version. The symbolic name of the OSGi bundle should be used.
     *
     * @return name of the plugin
     */
    String getName();

    /**
     * {@link Plugin} version should be kept in sync with the OSGi bundle version.
     *
     * @return version of the {@link Plugin}
     */
    String getVersion();

    /**
     * {@link Plugin} type can be used to group plugins that provide similar functionality.
     *
     * @return type of the {@link Plugin}
     */
    String getType();

    /**
     * {@link Plugin} description can be used to inform users about the functionality of a plugin.
     *
     * @return description of the {@link Plugin}
     */
    String getDescription();

    /**
     * Name of the author(s) of this {@link Plugin}.
     *
     * @return author of the {@link Plugin}
     */
    String getAuthor();


    /**
     * E-Mail of the author(s) of this {@link Plugin}
     *
     * @return email of the author of the {@link Plugin}
     */
    String getEMail();

}
