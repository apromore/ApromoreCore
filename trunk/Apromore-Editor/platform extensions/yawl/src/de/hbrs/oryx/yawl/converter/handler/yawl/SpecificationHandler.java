/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.handler.yawl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.unmarshal.YMetaData;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.util.YAWLUtils;

public class SpecificationHandler extends YAWLHandlerImpl {

    YSpecification specification;

    public SpecificationHandler(final YAWLConversionContext context, final YSpecification ySpec) {
        super(context);
        this.specification = ySpec;
    }

    public YSpecification getSpecification() {
        return specification;
    }

    @Override
    public void convert(final String parentId) {
        BasicDiagram specDiagram = createEmptyDiagram("diagram-" + getSpecification().getURI());

        specDiagram.setProperties(convertProperties());

        getContext().setSpecificationDiagram(specDiagram);
    }

    private HashMap<String, String> convertProperties() {
        HashMap<String, String> props = new HashMap<String, String>();

        final YSpecification spec = getSpecification();
        final YMetaData metaData = spec.getMetaData();

        // General Properties

        props.put("specname", convertNullable(spec.getName()));
        props.put("specid", convertNullable(metaData.getUniqueID()));

        // Specification Related

        props.put("spectitle", convertNullable(metaData.getTitle()));

        try {
            props.put("speccreators", convertListOfNames(metaData.getCreators()));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert YAWL specification 'creators'", e);
        }

        try {
            props.put("specsubject", convertListOfNames(metaData.getSubjects()));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert YAWL specification 'subject'", e);
        }

        props.put("specdescription", convertNullable(metaData.getDescription()));

        try {
            props.put("speccontributor", convertListOfNames(metaData.getContributors()));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert YAWL specification 'subject'", e);
        }

        props.put("speccoverage", convertNullable(metaData.getCoverage()));

        props.put("specvalidfrom", metaData.getValidFrom() != null ? new SimpleDateFormat(YAWLUtils.DATE_FORMAT).format(metaData.getValidFrom()) : "");
        props.put("specvaliduntil", metaData.getValidUntil() != null ? new SimpleDateFormat(YAWLUtils.DATE_FORMAT).format(metaData.getValidUntil())
                : "");

        props.put("speccreated", metaData.getCreated() != null ? new SimpleDateFormat(YAWLUtils.DATE_FORMAT).format(metaData.getCreated()) : "");

        props.put("specversion", convertNullable(metaData.getVersion().toString()));

        props.put("specstatus", convertNullable(metaData.getStatus()));

        props.put("specpersistent", new Boolean(metaData.isPersistent()).toString());

        props.put("specuri", spec.getURI());

        props.put("specdatatypedefinitions", convertNullable(spec.getDataValidator().getSchema()));

        return props;
    }

    private String convertListOfNames(final List<String> list) throws JSONException {
        JSONObject listOfNames = new JSONObject();
        JSONArray items = new JSONArray();
        for (String creator : list) {
            JSONObject obj = new JSONObject();
            obj.put("name", creator);
            items.put(obj);
        }
        listOfNames.put("items", items);
        return listOfNames.toString();
    }

    private BasicDiagram createEmptyDiagram(final String id) {

        String stencilSetNs = "http://b3mn.org/stencilset/yawl2.2#";
        StencilSetReference stencilSetRef = new StencilSetReference(stencilSetNs);

        BasicDiagram diagram = new BasicDiagram(id, "Diagram", stencilSetRef);
        // Set required properties to initial values
        // TODO: probably not used anymore in SIGNAVIO CORE COMPONENTS
        // diagram.setChildShapes(new ArrayList<Shape>());
        diagram.setBounds(new Bounds(new Point(0.0, 0.0), new Point(0.0, 0.0)));
        return diagram;
    }

}
