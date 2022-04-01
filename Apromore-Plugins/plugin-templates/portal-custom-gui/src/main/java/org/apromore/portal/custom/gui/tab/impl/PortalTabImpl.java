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

import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.tab.AbstractPortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 19/03/2016.
 */
public class PortalTabImpl extends AbstractPortalTab {

    private Listbox listBox;
    private String tabName;
    private String tabRowImage;
    private List<TabRowValue> tabRowValues;
    private List<Listheader> listheaders;
    private TabItemExecutor tabItemExecutor;

    public PortalTabImpl(String tabName, String tabRowImage, List<TabRowValue> tabRowValues, List<Listheader> listheaders, TabItemExecutor tabItemExecutor, PortalContext portalContext) {
        super(tabName, portalContext);
        this.tabName = tabName;
        this.tabRowImage = tabRowImage;
        this.tabRowValues = tabRowValues;
        this.listheaders = listheaders;
        this.tabItemExecutor = tabItemExecutor;
        this.tabpanel = generateTabpanel(listheaders, tabRowImage, tabRowValues, tabItemExecutor);
    }

    public Tabpanel generateTabpanel(List<Listheader> listheaders, String tabRowImage, List<TabRowValue> tabRowValues, TabItemExecutor tabItemExecutor){
        Tabpanel tabpanel = new Tabpanel();
        tabpanel.setStyle("overflow:auto");

        listBox = generateListbox(tabRowImage, listheaders, tabRowValues, tabItemExecutor);

        tabpanel.appendChild(listBox);

        return tabpanel;
    }

    public PortalTabImpl clone() {
        return new PortalTabImpl(tabName, tabRowImage, tabRowValues, listheaders, tabItemExecutor, portalContext);
    }

    private Listbox generateListbox(String image, List<Listheader> listheaders, List<TabRowValue> tabRowValues, TabItemExecutor tabItemExecutor) {
        Listbox list = new Listbox();
        list.setMultiple(true);

        Listhead head = new Listhead();
        head.setSizable(true);

        Listheader imgHeader=new Listheader();
        imgHeader.setWidth("40px");
        imgHeader.setVisible(true);
        head.appendChild(imgHeader);
        list.appendChild(head);

        for (Listheader idListHeader: listheaders) {
            head.appendChild((Listheader) idListHeader.clone());
        }

        TabItem item = null;

        for(TabRowValue tabRowValue : tabRowValues) {
            item = new TabItem(image, tabRowValue, tabItemExecutor);
            list.appendChild(item);
        }

        if(item == null) {
            item = new TabItem("", new TabRowValue(), null);
            list.appendChild(item);
        }

        return list;
    }

    private Listheader createListHeader(String hflex, String label, String width, String id, String sort, String visible){
        Listheader header=new Listheader();
        if(hflex != null)
            header.setHflex(hflex);
        if(label != null)
            header.setLabel(label);
        header.setWidth(width);
        if(id != null)
            header.setId(id);
        if(sort != null)
            header.setSort(sort);
        if(visible != null)
            header.setWidth(visible);
        return header;
    }

}
