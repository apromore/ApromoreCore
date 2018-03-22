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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.YLayout;
import org.yawlfoundation.yawl.elements.YSpecVersion;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMetaData;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactory;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Converts the Diagram to a YAWL specification
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxDiagramHandler extends OryxHandlerImpl {

    private final BasicDiagram diagramShape;

    public OryxDiagramHandler(final OryxConversionContext context, final BasicDiagram diagramShape) {
        super(context);
        this.diagramShape = diagramShape;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
     */
    @Override
    public void convert() {

        BasicShape rootNet = diagramShape;

        // First extract specification details
        YSpecification yawlSpec = convertSpecification();
        getContext().setSpecification(yawlSpec);

        // Then go on converting the root net
        HandlerFactory handlerFactory = getContext().getHandlerFactory();
        handlerFactory.createOryxConverter(rootNet).convert();

        // Then convert layout using the stored information about net
        // layouts
        convertLayout();

    }

    private void convertLayout() {
        YLayout layout = getContext().getLayout();
        layout.setLocale(Locale.GERMANY);
        layout.setSize(800, 600); // ??
        layout.setGlobalFontSize(12);
    }

    private YSpecification convertSpecification() {
        YSpecification spec = new YSpecification(diagramShape.getProperty("specuri"));

        spec.setName(diagramShape.getProperty("specname"));

        try {
            if (diagramShape.hasProperty("specdatatypedefinitions")) {
                spec.setSchema(diagramShape.getProperty("specdatatypedefinitions"));
            }
        } catch (YSyntaxException e) {
            getContext().addConversionWarnings("Invalid Data Definitions", e);
        }

        YMetaData metaData = new YMetaData();
        metaData.setTitle(diagramShape.getProperty("spectitle"));
        metaData.setUniqueID(diagramShape.getProperty("specid") != null ? diagramShape.getProperty("specid") : "id" + UUID.randomUUID().toString());
        metaData.setDescription(diagramShape.getProperty("description"));

        try {
            metaData.setVersion(new YSpecVersion(diagramShape.getProperty("specversion")));
        } catch (Exception e) {
            getContext().addConversionWarnings("Could not convert metadata 'specversion' of specification", e);
            metaData.setVersion(new YSpecVersion());
        }

        try {
            if (diagramShape.getProperty("specvalidfrom") != null) {
                metaData.setValidFrom(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(diagramShape.getProperty("specvalidfrom")));
            }
        } catch (ParseException e) {
            // getContext().addConversionWarnings("Invalid Date-Format specvalidfrom",
            // e);
        }

        try {
            if (diagramShape.getProperty("specvaliduntil") != null) {
                metaData.setValidUntil(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(diagramShape.getProperty("specvaliduntil")));
            }
        } catch (ParseException e) {
            // Ignore
        }

        try {
            if (diagramShape.getProperty("speccreated") != null) {
                metaData.setValidUntil(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(diagramShape.getProperty("speccreated")));
            }
        } catch (ParseException e) {
            // Ignore
        }

        try {
            metaData.setCreators(convertListOfNames(diagramShape.getPropertyJsonObject("speccreators")));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert metadata 'creators' of specification", e);
        }

        try {
            metaData.setContributors(convertListOfNames(diagramShape.getPropertyJsonObject("speccontributor")));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert metadata 'contributor' of specification", e);
        }

        try {
            metaData.setSubjects(convertListOfNames(diagramShape.getPropertyJsonObject("specsubject")));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert metadata 'subject' of specification", e);
        }

        metaData.setCoverage(diagramShape.getProperty("speccoverage"));
        metaData.setStatus(diagramShape.getProperty("specstatus"));
        if (diagramShape.hasProperty("specpersistent")) {
            metaData.setPersistent(diagramShape.getPropertyBoolean("specpersistent"));
        }

        spec.setMetaData(metaData);
        return spec;
    }

    private List<String> convertListOfNames(final JSONObject prop) throws JSONException {
        List<String> listOfNames = new ArrayList<String>();
        if (prop != null) {

            JSONArray jsonArray = prop.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                listOfNames.add(name);
            }
        }
        return listOfNames;
    }

}
