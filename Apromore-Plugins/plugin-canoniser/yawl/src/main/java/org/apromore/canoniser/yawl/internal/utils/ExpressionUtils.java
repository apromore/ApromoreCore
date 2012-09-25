/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.utils;

import org.apromore.cpf.NetType;

/**
 * Helper class to generate correct CPF XPath/XQuery expressions
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class ExpressionUtils {

    private ExpressionUtils() {
    }

    public static String createExpressionReferencingNetObject(final String objectName, final NetType net) {
        // TODO look up Object
        return "/" + objectName;
    }

    public static String createQueryReferencingTaskVariables(final String xQuery) {
        return xQuery;
    }

    public static String createQueryReferencingNetObjects(final String xQuery) {
     // TODO look up Object
        return xQuery;
    }

}
