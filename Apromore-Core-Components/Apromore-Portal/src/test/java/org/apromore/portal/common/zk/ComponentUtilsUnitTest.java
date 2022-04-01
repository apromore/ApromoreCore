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
package org.apromore.portal.common.zk;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apromore.portal.common.zk.ComponentUtils;

/**
 * Test toggle sclass
 */
public class ComponentUtilsUnitTest {

    private static Stream<Arguments> TEST_CASES() {
        Div divWithBase =  new Div();
        Div divOn =  new Div();
        Div divOff =  new Div();
        divWithBase.setSclass("base");
        divOn.setSclass("y");
        divOff.setSclass("x");

        return Stream.of(
                Arguments.of(new Div(), true, "OFF", "ON", "ON"),
                Arguments.of(new Div(), false, "disable", "enable", "disable"),
                Arguments.of(divWithBase, true, "off", "on", "base on"),
                Arguments.of(divOn, false, "x", "y", "x"),
                Arguments.of(divOff, true, "x", "y", "y")
        );
    }

    @ParameterizedTest
    @MethodSource("TEST_CASES")
    public void toggleSclass_ShouldContainCorrectSclass(HtmlBasedComponent comp, boolean newState, String sclassOff, String sclassOn, String expectedSclass) {
        ComponentUtils.toggleSclass(comp, newState, sclassOff, sclassOn);
        assertEquals(comp.getSclass().trim(), expectedSclass);
    }

}
