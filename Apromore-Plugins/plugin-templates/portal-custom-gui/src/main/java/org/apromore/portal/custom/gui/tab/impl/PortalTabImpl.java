/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.custom.gui.tab.impl;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.custom.gui.tab.AbstractPortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 19/03/2016.
 */
public class PortalTabImpl extends AbstractPortalTab {

    private Listbox listBox;
    private int pos = 0;

    public PortalTabImpl(String image, String tabName, Tabpanel tabpanel, PortalContext portalContext) {
        super(image, tabName, portalContext);
        this.tabpanel = tabpanel;
    }

    public PortalTabImpl(String tabName, String tabRowImage, List<TabRowValue> tabRowValues, TabHeader tabHeader, TabItemExecutor tabItemExecutor, PortalContext portalContext) {
        super(tabName, portalContext);
        this.tabpanel = generateTabpanel(tabHeader, tabRowImage, tabRowValues, tabItemExecutor);
    }

    public Tabpanel generateTabpanel(TabHeader tabHeader, String tabRowImage, List<TabRowValue> tabRowValues, TabItemExecutor tabItemExecutor){
        Tabpanel tabpanel = new Tabpanel();
        tabpanel.setStyle("overflow:auto");

        listBox = generateListbox(tabRowImage, tabHeader, tabRowValues, tabItemExecutor);

        tabpanel.appendChild(listBox);

        return tabpanel;
    }

    public Listbox generateListbox(String image, TabHeader tabHeader, List<TabRowValue> tabRowValues, TabItemExecutor tabItemExecutor) {
        Listbox list = new Listbox();
        list.setMultiple(true);

        Listhead head = new Listhead();
        head.setSizable(true);

        Listheader imgHeader=new Listheader();
        imgHeader.setWidth("25px");
        imgHeader.setVisible(true);
        head.appendChild(imgHeader);
        list.appendChild(head);

        for(String valueLabel: tabHeader) {
            Listheader idListHeader = createListHeader("1", valueLabel, null, "auto", null);

            idListHeader.setSortAscending(new java.util.Comparator<TabItem>() {
                @Override
                public int compare(TabItem o1, TabItem o2) {
                    return o1.getValue(pos).compareTo(o2.getValue(pos));
                }
            });
            idListHeader.setSortDescending(new java.util.Comparator<TabItem>() {
                @Override
                public int compare(TabItem o1, TabItem o2) {
                    return o2.getValue(pos).compareTo(o1.getValue(pos));
                }
            });

            pos++;
            head.appendChild(idListHeader);
        }

        TabItem item = null;

        for(TabRowValue tabRowValue : tabRowValues) {
            item = new TabItem(image, tabRowValue, tabItemExecutor);
            list.appendChild(item);
        }

        if(item == null) {
            item = new TabItem("", null, null);
            list.appendChild(item);
        }

        return list;
    }

    private Listheader createListHeader(String hflex, String label, String id,String sort, String visible){
        Listheader header=new Listheader();
        header.setWidth("150px");
        if(hflex != null)
            header.setHflex(hflex);
        if(label != null)
            header.setLabel(label);
        if(id != null)
            header.setId(id);
        if(sort != null)
            header.setSort(sort);
        if(visible != null)
            header.setWidth(visible);
        return header;
    }
}
