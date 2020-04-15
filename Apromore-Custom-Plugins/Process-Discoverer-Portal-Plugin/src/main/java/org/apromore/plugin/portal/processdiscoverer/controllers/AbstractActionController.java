/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.controllers;

import java.util.List;
import java.util.Objects;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Slider;

public abstract class AbstractActionController implements EventListener<Event> {
    protected PDController parent;

    private final String STATE_OFF = "ap-state-off";
    private final String STATE_ON = "ap-state-on";

    public AbstractActionController(PDController controller) {
        this.parent = controller;
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        // TODO Auto-generated method stub
    }

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
        int value = input.getValue();
        ensureSlider(slider, input, value);
    }

    public void ensureSlider(Slider slider, Intbox input, String val) throws Exception {
        if (!val.isEmpty()) {
            int value = Integer.parseInt(val);
            ensureSlider(slider, input, value);
        }
    }

    public void ensureSlider(Slider slider, Intbox input, int value) throws Exception {
        if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }
        input.setValue(value);
        slider.setCurpos(value);
    }

    public Listitem genListItem(List<String> cells) {
        Listitem listitem = new Listitem();

        for (String cell : cells) {
            Listcell listcell = new Listcell(cell);
            listitem.appendChild(listcell);
        }
        return listitem;
    }

}
