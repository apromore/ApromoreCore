/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.plugin.portal.bebop;

import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
//import org.apromore.portal.common.UserSessionManager;
//import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.service.bebop.BebopService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Fabrizio Fornari on 18/05/2017.
 */
public class BebopController {

    private PortalContext portalContext;
    private Window enterLogWin;
    private Window resultsWin;


    private BebopService bebopService;


    public BebopController(PortalContext portalContext, BebopService bebopService) {
        this.bebopService = bebopService;
        this.portalContext = portalContext;
    }


    public void bebopShowResult(ArrayList <String> guidelinesList){
        try {

            HashMap<String, String> guidelineMap = new HashMap<String, String>();
            for (int index = 0; index < guidelinesList.size(); index++) {
                guidelineMap.put(Integer.toString(index), guidelinesList.get(index));
                System.out.println("\nguidelineMap: "+Integer.toString(index)+" "+guidelinesList.get(index));
            }

            this.enterLogWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/bebop.zul", null, guidelineMap);

        }catch (IOException e) {
              Messagebox.show("Show Result with .Zul (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
