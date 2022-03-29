/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.custom.gui.tab.impl;

import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 19/03/2016.
 */
public class TabItem extends Listitem {

    private final String image;
    private final TabRowValue tabRowValue;
    private final Listcell[] listcells;

    public TabItem(String image, TabRowValue tabRowValue, final TabItemExecutor tabItemExecutor) {
        this.image = image;
        this.tabRowValue = tabRowValue;
        this.listcells = new Listcell[tabRowValue.size()];
        buildListitem();

        if(tabItemExecutor != null) {
            addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    tabItemExecutor.execute(TabItem.this);
                }
            });
        }
    }

    public TabRowValue getTabRowValue() {
        return tabRowValue;
    }

    public String getValue(int pos) {
        return listcells[pos].getLabel();
    }

    private void buildListitem(){
        Listcell imageCell = new Listcell();
        imageCell.setImage(image);
        if (image.contains("bpmn")) {
            imageCell.setSclass("ap-ico-process");
        } else {
            imageCell.setSclass("ap-ico-log");
        }
        imageCell.setStyle("vertical-align: middle; text-align:center");
        appendChild(imageCell);

        int pos = 0;
        for(Comparable value : tabRowValue) {
            Listcell listcell = new Listcell();

            if(value != null && value.toString() != null) {
                listcell.setLabel(value.toString());
            }

            listcells[pos] = listcell;
            pos++;

            appendChild(listcell);
        }
    }
}
