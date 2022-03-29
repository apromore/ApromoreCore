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
package org.apromore.commons.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DurationUnitTest {

    @Test
    void getDurationUnit() {
        Optional<DurationUnit> output = DurationUnit.getDurationUnit(1000.0D * 60 * 60 * 24 * 365.25);
        assertEquals(ChronoUnit.YEARS, output.stream().iterator().next().getUnit());

        output = DurationUnit.getDurationUnit(1000.0D * 60 * 60 * 24 * (365.25 / 12));
        assertEquals(ChronoUnit.MONTHS, output.stream().iterator().next().getUnit());
    }
}
