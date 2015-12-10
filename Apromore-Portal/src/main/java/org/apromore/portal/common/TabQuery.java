/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.portal.common;

import org.apromore.model.*;
import org.apromore.model.Detail;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.DetailsTabController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersController;
import org.apromore.portal.util.SessionTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.HashSet;
import java.util.List;

/**
 * Created by corno on 17/07/2014.
 */
public class TabQuery extends Tab implements Comparable<TabQuery>{
    private static final Logger LOGGER = LoggerFactory.getLogger(TabQuery.class.getName());
    private boolean isClose=false;
    private boolean isNew=false;
    private Tabpanel tabpanel;
    private Listbox listBox;
    private String userID;
    private static int progressID=1;
    private List<Detail> details;
    private long timeCreation;

    public String getQuery() {
        return query;
    }

    public List<Detail> getDetails() {
        return details;
    }

    private String query;

    public TabQuery(String label, String userID, final List<Detail> details, final String query){
        if(label.equals("") || label==null){
            setLabel("Query "+progressID);
            progressID++;
        }else{
            setLabel(label);
        }
        this.details=details;
        this.userID=userID;
        this.query=query;
        timeCreation=System.currentTimeMillis();
        setClosable(true);
        setSelected(true);
        setNew(true);
        setTooltiptext("Double click to show more info");
        setImage("img/info25.png");
//        setDraggable("true");
//        setDroppable("true");
        addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                new DetailsTabController(MainController.getController(), details, query);
            }
        });
        addEventListener(Events.ON_CLOSE,new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                LOGGER.debug("-------------------CLOSE: ");
                TabQuery.this.close(true);
            }
        });
    }

    public int getProgressID(){
        return progressID;
    }

    public String getUserID(){
        return userID;
    }

    public void close(boolean close){
        SessionTab.getTabsSession(userID).remove(this);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setTabpanel(List<ResultPQL> processes){
        Tabpanel newTabpanel = new Tabpanel();
        newTabpanel.setStyle("overflow:auto");

        Listbox list = new Listbox();
        list.setMultiple(true);

        Listhead head = new Listhead();
        head.setSizable(true);

        Listheader imgHeader=new Listheader();
        imgHeader.setWidth("25px");
        imgHeader.setVisible(true);
        head.appendChild(imgHeader);

        head.appendChild(builtChild("1", "Name", null, "auto", null));

        Listheader idListHeader = builtChild("1", "ID", null, "auto", null);
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

        head.appendChild(builtChild("1", "Original Language", null, "auto", null));
        head.appendChild(builtChild("1", "Domain", null, "auto", null));
        head.appendChild(builtChild("1", "Ranking", null, "auto", null));
        head.appendChild(builtChild("1", "Version", null, "auto", null));
        head.appendChild(builtChild("1", "Branch", null, "auto", null));
        head.appendChild(builtChild("1", "Owner", null, "auto", null));
        list.appendChild(head);

        TabListitem item =null;

            for (final ResultPQL process : processes) {
                item=new TabListitem(process.getPst(),process.getVst(),process.getAttributesToShow());
                list.appendChild(item);
            }

        if(item==null) {
            item = new TabListitem();
            list.appendChild(item);
        }
        newTabpanel.appendChild(list);
        this.listBox=list;
        this.tabpanel=newTabpanel;
    }

    public Tabpanel getTabpanel(){
        return tabpanel;
    }

    public void setTabpanel(Tabpanel tabpanel){
        this.tabpanel=tabpanel;
    }

    public Listbox getListBox(){
        return listBox;
    }

    private Listheader builtChild(String hflex, String label, String id,String sort, String visible){
        Listheader header=new Listheader();
        header.setWidth("150px");
        if(hflex!=null)
            header.setHflex(hflex);
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
        return userID.hashCode() * tabpanel.hashCode();
    }

    public boolean equals(Object o){
        if(o==null || ! (o instanceof TabQuery))
            return false;
        if(o == this)
            return true;
        TabQuery tab=(TabQuery)o;
        return tab.getTabpanel().equals(tabpanel) && progressID==tab.getProgressID() && userID.equals(tab.getUserID());
    }

    private long getTimeCreation(){
        return timeCreation;
    }

    @Override
    public int compareTo(TabQuery o) {
        if(timeCreation - o.getTimeCreation() > 0)
            return 1;
        else if(timeCreation - o.getTimeCreation() ==0 )
            return 0;
        return -1;
    }
}
