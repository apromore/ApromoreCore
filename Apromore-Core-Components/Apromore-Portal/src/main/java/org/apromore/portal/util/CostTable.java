/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostTable {
    private final Double DEFAULT_COST = 0.0D;

    @Getter
    private final String perspective;

    @Getter
    private final String currency;

    @Getter(AccessLevel.NONE)
    private final Map<String, Double> costRates;

    public static final CostTable EMPTY = CostTable.builder().currency("AUD").costRates(new HashMap<>()).build();

    public Double getCost(@NonNull String attributeId) {
        return costRates.getOrDefault(attributeId, DEFAULT_COST);
    }

    public Map<String, Double> getCostRates() {
        return Collections.unmodifiableMap(costRates);
    }
}
