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
package org.apromore.apmlog.filter.validation;

import org.apromore.apmlog.filter.rules.LogFilterRule;

import javax.validation.constraints.NotNull;

public class ValidatedFilterRule {
    private LogFilterRule originalRule;
    private LogFilterRule substitutedRule;
    private boolean applicable;
    private boolean substituted;

    /**
     *
     * @param originalRule
     * @param substitutedRule assign the original rule if no substitution
     * @param applicable
     * @param substituted
     */
    public ValidatedFilterRule(@NotNull LogFilterRule originalRule,
                               @NotNull LogFilterRule substitutedRule,
                               boolean applicable,
                               boolean substituted) {
        this.originalRule = originalRule;
        this.substitutedRule = substitutedRule;
        this.applicable = applicable;
        this.substituted = substituted;
    }

    public LogFilterRule getFilterRule() {
        return substituted ? substitutedRule : originalRule;
    }

    public LogFilterRule getOriginalRule() {
        return originalRule;
    }

    public LogFilterRule getSubstitutedRule() {
        return substitutedRule;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public boolean isSubstituted() {
        return substituted;
    }
}
