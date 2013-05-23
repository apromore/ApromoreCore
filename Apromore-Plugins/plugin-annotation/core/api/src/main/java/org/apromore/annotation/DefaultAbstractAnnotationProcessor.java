/**
 *  Copyright 2013
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
package org.apromore.annotation;

import org.apromore.plugin.DefaultParameterAwarePlugin;

/**
 * Implements common functionality shared by all Annotation Post Processors and reads the supported native types from the Annotation 'plugin.config'
 * file. The key used is: 'annotation.sourceProcessType' and 'annotation.targetProcessType'.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public abstract class DefaultAbstractAnnotationProcessor extends DefaultParameterAwarePlugin implements AnnotationProcessor {

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.Annotation#getProcessFormatProcessor()
     */
    @Override
    public String getProcessFormatProcessor() {
        return getConfigurationByName("annotation.processFormatProcessor");
    }

}
