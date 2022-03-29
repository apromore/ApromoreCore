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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.zkoss.zk.ui.HtmlBasedComponent;

public final class ComponentUtils {
    private static final String SCLASS_OFF = "ap-state-off";
    private static final String SCLASS_ON = "ap-state-on";

    public static void toggleSclass(HtmlBasedComponent comp, boolean newState, String sclassOff, String sclassOn) {
        String sclass = comp.getSclass();
        String oldClass = (newState) ? sclassOff : sclassOn;
        String newClass = (newState) ? sclassOn : sclassOff;
        if (sclass == null) {
            sclass = "";
        }
        sclass = Stream.concat(
                Arrays.stream(sclass.split("\\s+")).filter(s -> !s.equals(oldClass)),
                Stream.of(newClass)
            )
            .distinct()
            .collect(Collectors.joining(" "));
        comp.setSclass(sclass);
    }

    public static void toggleSclass(HtmlBasedComponent comp, String sclassOff, String sclassOn) {
        String sclass = comp.getSclass();
        if (sclass == null) {
            sclass = "";
        }
        if (sclass.contains(sclassOn)) {
            ComponentUtils.toggleSclass(comp, false, sclassOff, sclassOn);
        } else {
            ComponentUtils.toggleSclass(comp, true, sclassOff, sclassOn);
        }
    }

    public static void toggleSclass(HtmlBasedComponent comp, boolean newState) {
        ComponentUtils.toggleSclass(comp, newState, ComponentUtils.SCLASS_OFF, ComponentUtils.SCLASS_ON);
    }

    public static void toggleSclass(HtmlBasedComponent comp) {
        ComponentUtils.toggleSclass(comp, ComponentUtils.SCLASS_OFF, ComponentUtils.SCLASS_ON);
    }
}
