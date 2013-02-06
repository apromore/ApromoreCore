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
package org.apromore.toolbox;

import org.apromore.plugin.ParameterAwarePlugin;

/**
 * Interface to an Apromore Toolbox. Each Tool is build as a OSGi plugin and has to implement this interface.
 *
 * @author <a href="mailto:Cameron James">Cameron James</a>
 */
public interface Toolbox extends ParameterAwarePlugin {

    /**
     * The toolbox implementation name. Returns the name of the Tool implements e.g. Hungarian Similarity Search
     * @return the the name of the tool
     */
    String getToolName();

}
