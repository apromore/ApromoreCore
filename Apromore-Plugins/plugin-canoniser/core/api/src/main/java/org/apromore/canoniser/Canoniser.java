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
package org.apromore.canoniser;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PropertyAwarePlugin;

/**
 * Interface to an Apromore canoniser. Each canoniser is build as a OSGi plugin and has to implement this interface.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 */
public interface Canoniser extends PropertyAwarePlugin {

    /**
     * Type of the native format which this canoniser can handle. For example "EPML 2.0" or "YAWL 2.2"
     *
     * @return the type of the native format
     */
    String getNativeType();

    /**
     * Convert the data in native format to the canonical format and its annotation format.
     *
     * @param nativeInput
     *            stream of the native format
     * @param annotationFormat
     *            list to which the canonized Annotations are added
     * @param canonicalFormat
     *            list to which the canonized Canonical Processes are added
     * @throws CanoniserException
     *             in case of an Exception during conversion
     */
    void canonise(final InputStream nativeInput, List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat)
            throws CanoniserException;

    /**
     * Convert the data in annotation format and canonical format to the native format.
     *
     * @param canonicalFormat
     *            Canonical Process Type to deCanonise
     * @param annotationFormat
     *            Annotations to deCanonise
     * @param nativeOutput
     *            stream of the native format
     * @throws CanoniserException
     *             in case of an Exception during conversion
     */
    void deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput)
            throws CanoniserException;

}
