/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.controllers;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * DataListController is the controller to show a list of data items.
 *
 */
public abstract class DataListController extends AbstractController {
    protected ArrayList<String []> rows;
    protected ListModelList records;

    public DataListController(PDController controller) {
        super(controller);
    }

    public Listitem genListItem(List<String> cells) {
        Listitem listitem = new Listitem();

        for (String cell : cells) {
            Listcell listcell = new Listcell(cell);
            listitem.appendChild(listcell);
        }
        return listitem;
    }

    public String[] getDataHeaders () {
        return new String[]{};
    }

    public String getExportFilename () {
        return "data.csv";
    }

    public ArrayList<ArrayList<String>> getData () {
        return new ArrayList<ArrayList<String>>();
    }

    public void exportData () throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));

        csvWriter.writeNext(getDataHeaders());
        for (String[] row : rows) {
            csvWriter.writeNext(row);
        }
        csvWriter.flush();
        csvWriter.close();

        AMedia media = new AMedia(
                getExportFilename(),
                "csv",
                "text/csv",
                new ByteArrayInputStream(outputStream.toByteArray()));
        Filedownload.save(media);
    }
    
    @Override
    public abstract void onEvent(Event event) throws Exception;

}
