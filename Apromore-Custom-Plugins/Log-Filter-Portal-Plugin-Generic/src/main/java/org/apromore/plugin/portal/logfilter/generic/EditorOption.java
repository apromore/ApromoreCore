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
package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.FilterType;

import java.util.Map;
/**
 * @author Chii Chang (2021-03-15)
 */
public class EditorOption {
    private FilterType filterType;
    private LogFilterRule logFilterRule;
    private Map<String, Object> payload;

    /**
     * Use this method to open the defined filter editor for new criterion editing
     * @param filterType
     */
    public EditorOption(FilterType filterType) {
        this.filterType = filterType;
    }

    /**
     * Use this method to pass customised parameters when open the defined filter editor
     * @param filterType
     * @param payload
     */
    public EditorOption(FilterType filterType, Map<String, Object> payload) {
        this.filterType = filterType;
        this.payload = payload;
    }

    /**
     * Use this method to pass logFilterRule when open the defined filter editor
     * @param filterType
     * @param logFilterRule
     */
    public EditorOption(FilterType filterType, LogFilterRule logFilterRule) {
        this.filterType = filterType;
        this.logFilterRule = logFilterRule;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public LogFilterRule getLogFilterRule() {
        return logFilterRule;
    }
}
