/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.common;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import org.apromore.model.*;
import org.apromore.model.Detail;
import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.tab.AbstractPortalTab;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.dialogController.DetailsTabController;
import org.apromore.portal.dialogController.MainController;

/**
 * Created by corno on 17/07/2014.
 */
public class TabQuery extends AbstractPortalTab implements Comparable<TabQuery>{

    private static int progressID=1;

    private Listbox      listBox;
    private List<Detail> details;
    private long         timeCreation;
    private String       query;

    public TabQuery(String label, String userID, final List<Detail> details, final String query, List<ResultPQL> processes, PortalContext portalContext){
        super(label, userID, portalContext);
        if(label.equals("") || label==null){
            setLabel("Query "+progressID);
            progressID++;
        }else{
            setLabel(label);
        }
        this.details=details;
        this.query=query;
        timeCreation=System.currentTimeMillis();
//        setDraggable("true");
//        setDroppable("true");
        addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                new DetailsTabController(MainController.getController(), details, query);
            }
        });

        this.tabpanel = new Tabpanel();
        this.tabpanel.setStyle("overflow:auto");

        this.listBox = new Listbox();
        listBox.setMultiple(true);

        Listhead head = new Listhead();
        head.setSizable(true);

        Listheader imgHeader=new Listheader();
        imgHeader.setWidth("25px");
        imgHeader.setVisible(true);
        head.appendChild(imgHeader);

        head.appendChild(builtChild("1", "Name", null, "auto", null));

        Listheader idListHeader = builtChild("1", "ID", null, "auto", "3em");
        idListHeader.setSortAscending(new java.util.Comparator<TabListitem>() {
            public int compare(TabListitem o1, TabListitem o2) {
                return o1.getProcessSummaryType().getId().intValue() - o2.getProcessSummaryType().getId().intValue();
            }
	});
        idListHeader.setSortDescending(new java.util.Comparator<TabListitem>() {
            public int compare(TabListitem o1, TabListitem o2) {
                return o2.getProcessSummaryType().getId().intValue() - o1.getProcessSummaryType().getId().intValue();
            }
	});
        head.appendChild(idListHeader);

        head.appendChild(builtChild("1", "Original Language", null, "auto", "10em"));
        head.appendChild(builtChild("1", "Domain", null, "auto", "5em"));
        head.appendChild(builtChild("1", "Ranking", null, "auto", "6em"));
        head.appendChild(builtChild("1", "Version", null, "auto", "5em"));
        head.appendChild(builtChild("1", "Branch", null, "auto", "5em"));
        head.appendChild(builtChild("1", "Owner", null, "auto", "5em"));
        listBox.appendChild(head);

        TabListitem item =null;

        for (final ResultPQL process : processes) {
            item=new TabListitem(process.getPst(),process.getVst(),process.getAttributesToShow());
            listBox.appendChild(item);
        }

        if(item==null) {
            item = new TabListitem();
            listBox.appendChild(item);
        }
        this.tabpanel.appendChild(listBox);
    }

    public Listbox getListBox(){
        return listBox;
    }

    private Listheader builtChild(String hflex, String label, String id, String sort, String visible){
        Listheader header=new Listheader();
        //header.setWidth("150px");
        //if(hflex!=null)
        //    header.setHflex(hflex);
        if(label!=null)
            header.setLabel(label);
        if(id!=null)
            header.setId(id);
        if(sort!=null)
            header.setSort(sort);
        if(visible!=null)
            header.setWidth(visible);
        return header;
    }

    public int hashCode(){
        int x = userID.hashCode();
        int y = (tabpanel == null) ? 1 : tabpanel.hashCode();
        return x * y;
    }

    public boolean equals(Object o){
        if(o==null || ! (o instanceof TabQuery))
            return false;
        if(o == this)
            return true;
        TabQuery tab=(TabQuery)o;
        return tab.timeCreation == timeCreation && equal(tab.userID, userID);
    }

    private static boolean equal(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    @Override
    public int compareTo(TabQuery o) {
        return Long.compare(timeCreation, o.timeCreation);
    }

    @Override
    public Object clone() {
        TabQuery clone = (TabQuery) super.clone();

        clone.listBox      = (Listbox) this.listBox.clone();
        clone.details      = this.details;
        clone.timeCreation = this.timeCreation;
        clone.query        = this.query;

        return clone;
    }

    @Override
    public String toString() {
        return "TabQuery(super=" + super.toString() + " userID=" + this.userID + " id=" + this.getId() + " context=" + this.getContext() + " tabbox=" + this.getTabbox() + ")";
    }
}
