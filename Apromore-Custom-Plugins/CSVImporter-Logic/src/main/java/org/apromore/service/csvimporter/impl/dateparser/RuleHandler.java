/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.impl.dateparser;

import com.github.sisyphsu.retree.ReMatcher;

/**
 * This class represents the standard specification of rule's handler.
 * It should parse the specified substring to fill some fields of DateTime.
 *
 * @author sulin
 * @since 2019-09-14 14:25:45
 */
@FunctionalInterface
interface RuleHandler {

    /**
     * Parse substring[from, to) of the specified string
     *
     * @param chars   The original string in char[]
     * @param matcher The underline ReMatcher
     * @param dt      DateTime to accept parsed properties.
     */
    void handle(CharSequence chars, ReMatcher matcher, DateBuilder dt);

}
