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

package org.apromore.plugin.portal.processdiscoverer.components;

import java.util.Objects;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Slider;

/**
 * VisualController is the controller to show graphical controls (checkboxes, sliders, etc.).
 *
 */
public abstract class VisualController extends AbstractController {
    private final String STATE_OFF = "ap-state-off";
    private final String STATE_ON = "ap-state-on";

    public VisualController(PDController controller) {
        super(controller);
    }
    
    @Override
    public abstract void onEvent(Event event) throws Exception;

    public int selectComboboxByKey(Combobox cb, String key) {
        int selIndex = 0, i = 0;
        for (Comboitem item : cb.getItems()) {
            String value = item.getValue();
            if (value.equals(key)) {
                selIndex = i;
            }
            i++;
        }
        cb.setSelectedIndex(selIndex);
        return selIndex;
    }

    public void toggleComponentSclass(HtmlBasedComponent comp, boolean state, String stateOff, String stateOn) {
        String sclass = Objects.requireNonNull(comp.getSclass(), stateOff);
        if (!sclass.contains(stateOff)) {
            sclass = sclass + " " + stateOff;
        }
        if (state) {
            comp.setSclass(sclass.replace(stateOff, stateOn));
        } else {
            comp.setSclass(sclass.replace(stateOn, stateOff));
        }
    }

    public void toggleComponentClass(HtmlBasedComponent comp, boolean state) {
        String sclass = Objects.requireNonNull(comp.getSclass(), STATE_OFF);
        if (state) {
            comp.setSclass(sclass.replace(STATE_OFF, STATE_ON));
        } else {
            comp.setSclass(sclass.replace(STATE_ON, STATE_OFF));
        }
    }

    public void ensureSlider(Slider slider, Intbox input) throws Exception {
        int value = Objects.requireNonNullElse(input.getValue(), 0);
        ensureSlider(slider, input, value);
    }
    
    public void ensureSliders() throws Exception {
        //
    }

    private void ensureSlider(Slider slider, Intbox input, int value) throws Exception {
        if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }
        input.setValue(value);
        slider.setCurpos(value);
    }

}
