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
package org.apromore.plugin.message;

/**
 * Simple Implementation of a Message containing just a text Message
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class SimplePluginMessage implements PluginMessage {

    private final String message;

    public SimplePluginMessage(final String message) {
        super();
        this.message = message;
    }

    /* (non-Javadoc)
     * @see org.apromore.plugin.message.PluginMessage#getMessage()
     */
    @Override
    public String getMessage() {
        return message;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("SimplePluginMessage [message=%s]", message);
    }

}
