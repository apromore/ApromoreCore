/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd. All rights reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.processsimulation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostingData {
    private static final Double DEFAULT_COST = 0.0D;

    @Getter
    private final String perspective;

    @Getter
    private final String currency;

    @Getter(AccessLevel.NONE)
    private final Map<String, Double> costRates;

    public static final CostingData EMPTY = CostingData.builder().currency("AUD").costRates(new HashMap<>()).build();

    public Double getCost(@NonNull String attributeId) {
        return costRates.getOrDefault(attributeId, DEFAULT_COST);
    }

    public Map<String, Double> getCostRates() {
        return costRates;
    }
}
