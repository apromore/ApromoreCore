/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("PackageHeader")
public class XPDLPackageHeader extends XMLConvertible {

    @Element("Created")
    protected XPDLCreated created;
    @Element("Documentation")
    protected XPDLDocumentation documentation;
    @Element("ModificationDate")
    protected XPDLModificationDate modificationDate;
    @Element("Vendor")
    protected XPDLVendor vendor = new XPDLVendor();
    @Element("XPDLVersion")
    protected XPDLXPDLVersion xpdlVersion = new XPDLXPDLVersion();

    public XPDLCreated getCreated() {
        return created;
    }

    public XPDLDocumentation getDocumentation() {
        return documentation;
    }

    public XPDLModificationDate getModificationDate() {
        return modificationDate;
    }

    public XPDLVendor getVendor() {
        return vendor;
    }

    public XPDLXPDLVersion getXpdlVersion() {
        return xpdlVersion;
    }

    public void readJSONcreationdate(JSONObject modelElement) throws JSONException {
        passInformationToCreated(modelElement, "creationdate");
    }

    public void readJSONcreationdateunknowns(JSONObject modelElement) throws JSONException {
        passInformationToCreated(modelElement, "creationdateunknowns");
    }

    public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
        passInformationToDocumentation(modelElement, "documentation");
    }

    public void readJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
        passInformationToDocumentation(modelElement, "documentationunknowns");
    }

    public void readJSONmodificationdate(JSONObject modelElement) throws JSONException {
        passInformationToModificationDate(modelElement, "modificationdate");
    }

    public void readJSONmodificationdateunknowns(JSONObject modelElement) throws JSONException {
        passInformationToModificationDate(modelElement, "modificationdateunknowns");
    }

    public void readJSONpackageheaderunknowns(JSONObject modelElement) throws JSONException {
        readUnknowns(modelElement, "packageheaderunknowns");
    }

    public void readJSONvendorunknowns(JSONObject modelElement) throws JSONException {
        passInformationToVendor(modelElement, "vendorunknowns");
    }

    public void readJSONxpdlversionunknowns(JSONObject modelElement) throws JSONException {
        passInformationToXPDLVersion(modelElement, "xpdlversionunknowns");
    }

    public void setCreated(XPDLCreated date) {
        created = date;
    }

    public void setDocumentation(XPDLDocumentation documentationValue) {
        documentation = documentationValue;
    }

    public void setModificationDate(XPDLModificationDate date) {
        modificationDate = date;
    }

    public void setVendor(XPDLVendor vendorValue) {
        vendor = vendorValue;
    }

    public void setXpdlVersion(XPDLXPDLVersion version) {
        xpdlVersion = version;
    }

    public void writeJSONcreated(JSONObject modelElement) {
        XPDLCreated creation = getCreated();
        if (creation != null) {
            creation.write(modelElement);
        }
    }

    public void writeJSONdocumentation(JSONObject modelElement) {
        XPDLDocumentation doc = getDocumentation();
        if (doc != null) {
            doc.write(modelElement);
        }
    }

    public void writeJSONmodificationdate(JSONObject modelElement) {
        XPDLModificationDate date = getModificationDate();
        if (date != null) {
            date.write(modelElement);
        }
    }

    public void writeJSONpackageheaderunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "packageheaderunknowns");
    }

    public void writeJSONvendor(JSONObject modelElement) {
        XPDLVendor vendorObject = getVendor();
        if (vendorObject != null) {
            vendorObject.write(modelElement);
        }
    }

    public void writeJSONxpdlversion(JSONObject modelElement) {
        XPDLXPDLVersion version = getXpdlVersion();
        if (version != null) {
            version.write(modelElement);
        }
    }

    protected void initializeCreated() {
        if (getCreated() == null) {
            setCreated(new XPDLCreated());
        }
    }

    protected void initializeDocumentation() {
        if (getDocumentation() == null) {
            setDocumentation(new XPDLDocumentation());
        }
    }

    protected void initializeModificationDate() {
        if (getModificationDate() == null) {
            setModificationDate(new XPDLModificationDate());
        }
    }

    protected void passInformationToCreated(JSONObject modelElement, String key) throws JSONException {
        initializeCreated();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getCreated().parse(passObject);
    }

    protected void passInformationToDocumentation(JSONObject modelElement, String key) throws JSONException {
        initializeDocumentation();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getDocumentation().parse(passObject);
    }

    protected void passInformationToModificationDate(JSONObject modelElement, String key) throws JSONException {
        initializeModificationDate();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getModificationDate().parse(passObject);
    }

    protected void passInformationToVendor(JSONObject modelElement, String key) throws JSONException {
        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getVendor().parse(passObject);
    }

    protected void passInformationToXPDLVersion(JSONObject modelElement, String key) throws JSONException {
        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getXpdlVersion().parse(passObject);
    }
}
