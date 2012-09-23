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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Generating unique IDs for YAWL/CPF elements. The generated UUID will stay always the same for each YAWL/CPF element.
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class ConversionUUIDGenerator {

    public static final Pattern NCNAME = Pattern.compile("[\\p{Alpha}_][\\p{Alnum}-_\\x2E]*");

    private static final String ID_PREFIX = "id-";

    private Map<String, String> uuidMap;

    /**
     * Returns an UUID for the specified originalId, which is remembered and the same UUID is returned on subsequent calls. Use null to get a random
     * new UUID that is not remembered.
     *
     * @param originalId
     *            of the YAWL/CPF element
     * @return
     */
    public String getUUID(final String originalId) {
        initMap();
        if (originalId == null) {
            return ID_PREFIX + UUID.randomUUID().toString();
        }
        if (!uuidMap.containsKey(originalId)) {
            uuidMap.put(originalId, ID_PREFIX + sanitizeId(originalId)); // + "_" + UUID.randomUUID().toString());
        }
        return uuidMap.get(originalId);
    }

    private String sanitizeId(final String originalId) {
        String sanitizedId = originalId;
        // YAWL does not like '_' in the IDs (see: http://code.google.com/p/yawl/issues/detail?id=470)
        sanitizedId = sanitizedId.replace('_', '-');
        // Should be a NCName otherwise generate UUID
        if (!NCNAME.matcher(originalId).matches()) {
            sanitizedId = ID_PREFIX + UUID.randomUUID().toString();
        }
        return sanitizedId;
    }

    private void initMap() {
        if (uuidMap == null) {
            uuidMap = new HashMap<String, String>();
        }
    }

}
