/*
 * Copyright Â© 2009-2016 The Apromore Initiative.
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

package org.apromore.plugin.portal.perfmining;

import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONException;
import org.apromore.service.perfmining.models.SPF;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Window;


public class PerfMiningShowResult { //extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private Window resultW;
    /**
     * @throws IOException if the <code>prodriftshowresult.zul</code> template can't be read from the classpath
     */
    public PerfMiningShowResult(final PortalContext portalContext, SPF spf, Window parentW) throws IOException, JSONException {
        this.portalContext = portalContext;
        this.resultW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/result.zul", null, null);
        String jsonString = Visualization.createCFDJson(DatasetFactory.createCFDDataset(spf)).toString();
        String javascript = "loadData('" + jsonString + "');";
        //System.out.println(javascript);
        Clients.evalJavaScript(javascript);
        this.resultW.setTitle("Staged Process Flow");
        this.resultW.doOverlapped();
    }



    public void showError(String error) {
        Messagebox.show(error, "Error", 0, Messagebox.ERROR);
    }

    protected void close() {
        this.resultW.detach();
    }


}

