/**
 *  Copyright 2012, Felix Mannhardt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin;

import java.util.Collection;

import org.apromore.plugin.message.PluginMessage;

/**
 * A MessageAwarePlugin can be asked for a Collection of PluginMessage which provide useful information (e.g. warnings) about the execution of the
 * Plugin.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface MessageAwarePlugin extends Plugin {

    /**
     * Returns all messages of this Plugin. Note the returned Collection should not be modified!
     *
     * @return Collection of PluginMessage
     */
    Collection<PluginMessage> getPluginMessages();

}
