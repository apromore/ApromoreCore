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

package org.apromore.common.converters.xpdl;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hpi.bpmn2xpdl.BPMN2XPDLConverter;
import de.hpi.bpmn2xpdl.XPDLCreated;
import de.hpi.bpmn2xpdl.XPDLModificationDate;
import de.hpi.bpmn2xpdl.XPDLPackage;
import de.hpi.bpmn2xpdl.XPDLPackageHeader;
import de.hpi.bpmn2xpdl.XPDLRedefinableHeader;
import de.hpi.bpmn2xpdl.XPDLVersion;
import org.apromore.common.converters.xpdl.interceptor.BizageInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

/**
 * Converts a XPDL Stream to a Signavio/Oryx JSON Stream
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class XPDLToJSONConverter extends BPMN2XPDLConverter {

    /**
     * gets a java model representing the xml data passed
     * @param xml THE XMPDL xml to Parse.
     * @return the XPDL package.
     */
    public XPDLPackage getXPDLModel(String xml) {
        String parseXML = filterXMLString(xml);
        StringReader reader = new StringReader(parseXML);

        Xmappr xmappr = new Xmappr(XPDLPackage.class);
        XPDLPackage newPackage = (XPDLPackage) xmappr.fromXML(reader);
        newPackage.createAndDistributeMapping();

        intercept(newPackage);

        return newPackage;
    }

    /**
     * converts a java model of a process to its representive XPDL.
     * @param newPackage the XPDL Package
     * @return the XPDL JSON String
     */
    public String getXPDL(XPDLPackage newPackage) {
        newPackage.createAndDistributeMapping();

        JSONObject importObject = new JSONObject();
        newPackage.write(importObject);

        return importObject.toString();
    }

    /**
     * generates a XPDL process (XML) from its representative json. required tags of saveAs (eg. process name)
     * will be added here.
     * @param json
     * @param processVersion
     * @param processName
     * @return
     * @throws JSONException
     */
    public String getNativeXPDLToSaveAs(String json, String processVersion, String processName) throws JSONException {
        JSONObject model = new JSONObject(json);
        HashMap<String, JSONObject> mapping = new HashMap<String, JSONObject>();
        constructResourceIdShapeMapping(model, mapping);

        XPDLPackage newPackage = new XPDLPackage();
        newPackage.setResourceIdToShape(mapping);
        newPackage.parse(model);
        newPackage.setName(processName);

        ///set created
        XPDLPackageHeader packageHeader = newPackage.getPackageHeader();
        if (packageHeader == null) {
            packageHeader = new XPDLPackageHeader();
            newPackage.setPackageHeader(packageHeader);
        }
        XPDLCreated created = packageHeader.getCreated();
        if (created == null) {
            created = new XPDLCreated();
            packageHeader.setCreated(created);
        }
        Date creationDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

        created.setContent(dateFormat.format(creationDate) + "T" + timeFormat.format(creationDate));//2010-06-14T00:00:00

        // remove modification date
        newPackage.getPackageHeader().setModificationDate(null);

        // set version
        XPDLRedefinableHeader redefinableHeader = newPackage.getRedefinableHeader();
        if (redefinableHeader == null) {
            redefinableHeader = new XPDLRedefinableHeader();
            newPackage.setRedefinableHeader(redefinableHeader);
        }
        XPDLVersion version = redefinableHeader.getVersion();
        if (version == null) {
            version = new XPDLVersion();
            redefinableHeader.setVersion(version);
        }
        version.setContent(processVersion);

        StringWriter writer = new StringWriter();

        Xmappr xmappr = new Xmappr(XPDLPackage.class);
        xmappr.setPrettyPrint(true);
        xmappr.toXML(newPackage, writer);

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();

    }

    /**
     * generates a XPDL process (XML) from its representative json. required save tags (eg. modificationDate)
     * are added here
     *
     * @param json
     * @param processVersion
     * @return
     * @throws JSONException
     */
    public String getNativeXPDLToSave(String json, String processVersion) throws JSONException {
        JSONObject model = new JSONObject(json);
        HashMap<String, JSONObject> mapping = new HashMap<String, JSONObject>();
        constructResourceIdShapeMapping(model, mapping);

        XPDLPackage newPackage = new XPDLPackage();
        newPackage.setResourceIdToShape(mapping);
        newPackage.parse(model);

        ///set modification date
        XPDLPackageHeader packageHeader = newPackage.getPackageHeader();
        if (packageHeader == null) {
            packageHeader = new XPDLPackageHeader();
            newPackage.setPackageHeader(packageHeader);
        }
        XPDLModificationDate modificationDate = packageHeader.getModificationDate();
        if (modificationDate == null) {
            modificationDate = new XPDLModificationDate();
            packageHeader.setModificationDate(modificationDate);
        }
        Date creationDate = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

        modificationDate.setContent(dateFormat.format(creationDate) + "T" + timeFormat.format(creationDate));//2010-06-14T00:00:00

        // set version
        XPDLRedefinableHeader redefinableHeader = newPackage.getRedefinableHeader();
        if (redefinableHeader == null) {
            redefinableHeader = new XPDLRedefinableHeader();
            newPackage.setRedefinableHeader(redefinableHeader);
        }
        XPDLVersion version = redefinableHeader.getVersion();
        if (version == null) {
            version = new XPDLVersion();
            redefinableHeader.setVersion(version);
        }
        version.setContent(processVersion);

        StringWriter writer = new StringWriter();

        Xmappr xmappr = new Xmappr(XPDLPackage.class);
        xmappr.setPrettyPrint(true);
        xmappr.toXML(newPackage, writer);

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();

    }


    //TODO: load list of interceptors at runtime
    private void intercept(XPDLPackage newPackage) {
        try {
            if (newPackage.getPackageHeader().getVendor().getContent().startsWith("BizAgi")) {
                new BizageInterceptor(newPackage).intercept();
            }
        } catch (NullPointerException exp) { }
    }

    /*
           <PackageHeader>
             <Created>
               2010-06-15T00:00:00
             </Created>
             <ModificationDate>
               2010-06-14T00:00:00
             </ModificationDate>
             <Vendor>
               Hasso Plattner Institute
             </Vendor>
             <XPDLVersion>
               2.1
             </XPDLVersion>
           </PackageHeader>
           <RedefinableHeader>
             <Version>
               1.2
             </Version>
           </RedefinableHeader>*/


    /**
     * removes tags that make oryx confused and let processes pass canonizer
     */
    protected String filterXMLString(String xml) {
        //Remove xpdl2: from tags
        String firstTagFiltered = xml.replace("<xpdl2:", "<");
        firstTagFiltered = firstTagFiltered.replace("</xpdl2:", "</");

        //Remove xpdl: from tags
        String secondTagFiltered = firstTagFiltered.replace("<xpdl:", "<");
        secondTagFiltered = secondTagFiltered.replace("</xpdl:", "</");

        //Remove namespaces
        String nameSpaceFiltered = secondTagFiltered.replaceAll(" xmlns=\"[^\"]*\"", "");
        //Remove xml namespace lookalikes
        nameSpaceFiltered = nameSpaceFiltered.replaceAll(" \\w+:\\w+=\"[^\"]*\"", "");
        //Remove schemas
        String schemaFiltered = nameSpaceFiltered.replaceAll(" xsi=\"[^\"]*\"", "");
        //Remove starting xml tag
        String xmlTagFiltered = schemaFiltered.replaceAll("<\\?xml[^\\?]*\\?>\n?", "");

        //Remove schema locations that makes problem for the xmappr
        String schemaLocationFiltered = xmlTagFiltered.replaceAll("xsi:schemaLocation=\"[^\"]*\"", "");
        //Remove <g360:PageInfo..../> generated by tools like sketchpad
        String xmlnsg360Filtered = schemaLocationFiltered.replaceAll("<\\w+:\\w+[^>]*>", "");

        return xmlnsg360Filtered;
    }
}
