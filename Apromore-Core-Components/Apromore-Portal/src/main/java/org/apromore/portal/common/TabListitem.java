/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.portal.common;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

/**
 * Created by corno on 19/08/2014.
 */
public class TabListitem extends Listitem {
    private ProcessSummaryType pst;
    private VersionSummaryType vst;
    private List<Boolean> attributesToShow;
    private static final String imageProcess="/themes/ap/common/img/icons/bpmn-model.svg";

    public TabListitem(ProcessSummaryType pst, VersionSummaryType vst, List<Boolean> attributesToShow){
        this.pst=pst;
        this.vst=vst;
        this.attributesToShow=attributesToShow;
        buildListitem();
        addEventListener(Events.ON_DOUBLE_CLICK,new EventListener<Event>() {
            private MainController mainController = MainController.getController();

            @Override
            public void onEvent(Event event) throws Exception {
                mainController.editProcess(TabListitem.this.pst, TabListitem.this.vst, TabListitem.this.pst.getOriginalNativeType(), 
                            new HashSet<RequestParameterType<?>>(), false);
            }
        });
    }

    public TabListitem(){
        buildListitem();
    }

    public ProcessSummaryType getProcessSummaryType(){
        return pst;
    }

    public List<VersionSummaryType> getVersionSummaryType(){
        List<VersionSummaryType> list=new LinkedList<>();
        list.add(vst);
        return list;
    }

    private void buildListitem(){
        Listcell image=new Listcell();
        image.setImage(imageProcess);
        image.setSclass("ap-ico-process");
        Listcell name=new Listcell();
        Listcell id=new Listcell();
        Listcell nativeType=new Listcell();
        Listcell domain=new Listcell();
        Listcell ranking=new Listcell();
        Listcell version=new Listcell();
        Listcell branch=new Listcell();
        Listcell owner=new Listcell();

        if(attributesToShow!=null) {
            if (attributesToShow.get(0))
                name.setLabel(pst.getName());
            if (attributesToShow.get(1))
                id.setLabel(pst.getId().toString());
            if (attributesToShow.get(2))
                nativeType.setLabel(pst.getOriginalNativeType());
            if (attributesToShow.get(3))
                domain.setLabel(pst.getDomain());
            if (attributesToShow.get(4))
                ranking.setLabel(pst.getRanking());
            if (attributesToShow.get(5))
                version.setLabel(vst.getVersionNumber());
            if (attributesToShow.get(6))
                branch.setLabel(vst.getName());
            if (attributesToShow.get(7))
                owner.setLabel(pst.getOwner());
        }
        if(attributesToShow!=null) {
            appendChild(image);
            appendChild(name);
            appendChild(id);
            appendChild(nativeType);
            appendChild(domain);
            appendChild(ranking);
            appendChild(version);
            appendChild(branch);
            appendChild(owner);
        }
    }
}
