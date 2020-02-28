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

package org.apromore.portal.dialogController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.manager.client.ManagerService;
import org.apromore.helper.PluginHelper;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.PluginParameter;
import org.apromore.model.PluginParameters;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

/**
 * Helper class that builds up a ZK Grid with inputs for Plugin properties.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class PluginPropertiesHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginPropertiesHelper.class);

    private final ManagerService service;
    private final Grid propertiesGrid;

    private PluginInfoResult currentPluginInfo;
    private Rows gridRows;

    /**
     * Helper class that builds up a ZK Grid with inputs for Plugin properties.
     *
     * @param service the portal service that does the comms to the portal.
     * @param propertiesGrid the grid to be filled
     */
    public PluginPropertiesHelper(final ManagerService service, final Grid propertiesGrid) {
        this.service = service;
        this.propertiesGrid = propertiesGrid;
    }

    /**
     * Show parameters of Plugin in a dynamically build ZK grid.
     *
     * @param info
     *            basic info about a Plugin
     * @param parameterCategory to filter by a category
     * @return more info about the selected Plugin
     * @throws InterruptedException
     */
    public PluginInfoResult showPluginProperties(final PluginInfo info, final String parameterCategory) throws InterruptedException {
        try {
            if (gridRows != null) {
                propertiesGrid.removeChild(gridRows);
            }
            gridRows = new Rows();
            propertiesGrid.appendChild(gridRows);

            Clients.showBusy(this.gridRows, "Reading available properties for " + info.getName() + "...");
            currentPluginInfo = service.readPluginInfo(info.getName(), info.getVersion());
            Clients.clearBusy(this.gridRows);

            for (PluginParameter prop : currentPluginInfo.getMandatoryParameters().getParameter()) {
                if (prop.getCategory().equals(parameterCategory) || parameterCategory == null) {
                    addMandatoryParameter(prop);
                }
            }

            for (PluginParameter prop : currentPluginInfo.getOptionalParameters().getParameter()) {
                if (prop.getCategory().equals(parameterCategory) || parameterCategory == null) {
                    addOptionalParameter(prop);
                }
            }

            return currentPluginInfo;
        } catch (Exception e) {
            Messagebox.show("Showing Plugin Properties failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
        return null;
    }

    private void addOptionalParameter(final PluginParameter prop) throws InterruptedException {
        Row propertyRow = new Row();
        Label labelName;
        if (prop.isIsMandatory()) {
            labelName = new Label(prop.getName() + " *");
        } else {
            labelName = new Label(prop.getName());
        }
        if (prop.getDescription() != null) {
            labelName.setTooltip(prop.getDescription());
        }
        propertyRow.appendChild(labelName);
        Component inputValue = createInputComponent(prop, false);
        propertyRow.appendChild(inputValue);
        gridRows.appendChild(propertyRow);
    }

    private void addMandatoryParameter(final PluginParameter prop) throws InterruptedException {
        Row propertyRow = new Row();
        Label labelName = new Label(prop.getName());
        if (prop.getDescription() != null) {
            labelName.setTooltip(prop.getDescription());
        }
        propertyRow.appendChild(labelName);
        Component inputValue = createInputComponent(prop, true);
        propertyRow.appendChild(inputValue);
        gridRows.appendChild(propertyRow);
    }

    /**
     * Creates an ZK input component based on the property type
     *
     * @param prop the plugin Parameter
     * @param isRequired is the parameter mandatory
     * @return the java build ui component
     * @throws InterruptedException
     */
    private static Component createInputComponent(final PluginParameter prop, final boolean isRequired) throws InterruptedException {
        if (prop.getValue() instanceof String) {
            Textbox textBox = new Textbox();
            if (prop.getValue() != null) {
                textBox.setValue(prop.getValue().toString());
            }
            if (isRequired) {
                textBox.setConstraint("no empty");
            }
            textBox.addEventListener("onChange", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof InputEvent) {
                        InputEvent inputEvent = (InputEvent) event;
                        prop.setValue(inputEvent.getValue());
                    }
                }
            });
            return textBox;
        } else if (prop.getValue() instanceof Long) {
            Longbox longBox = new Longbox();
            if (prop.getValue() != null) {
                longBox.setValue((Long) prop.getValue());
            }
            if (isRequired) {
                longBox.setConstraint("no empty");
            }
            longBox.addEventListener("onChange", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof InputEvent) {
                        InputEvent inputEvent = (InputEvent) event;
                        prop.setValue(new Long(inputEvent.getValue()));
                    }
                }
            });
            return longBox;
        } else if (prop.getValue() instanceof Integer) {
            Intbox intBox = new Intbox();
            if (prop.getValue() != null) {
                intBox.setValue((Integer) prop.getValue());
            }
            if (isRequired) {
                intBox.setConstraint("no empty");
            }
            intBox.addEventListener("onChange", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof InputEvent) {
                        InputEvent inputEvent = (InputEvent) event;
                        prop.setValue(Integer.valueOf(inputEvent.getValue()));
                    }
                }
            });
            return intBox;
        } else if (prop.getValue() instanceof Boolean) {
            Checkbox booleanBox = new Checkbox();
            if (prop.getValue() != null) {
                booleanBox.setChecked((Boolean) prop.getValue());
            }
            booleanBox.addEventListener("onCheck", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof CheckEvent) {
                        CheckEvent checkEvent = (CheckEvent) event;
                        prop.setValue(checkEvent.isChecked());
                    }
                }
            });
            return booleanBox;
        } else if (prop.getClazz().equals("java.io.InputStream")) {
            Fileupload fileButton = new Fileupload("Upload file");
            fileButton.setAttribute("upload", "true");
            fileButton.addEventListener("onUpload", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof UploadEvent) {
                        UploadEvent uploadEvent = (UploadEvent) event;
                        try {
                            prop.setValue(new DataHandler(new ByteArrayDataSource(uploadEvent.getMedia().getStreamData(), "application/octet-stream")));
                        } catch (IOException e) {
                            LOGGER.error("Failure setting property!", e);
                        }
                    }
                }
            });
            return fileButton;
        } else {
            Messagebox.show("Unkown property type: " + prop.getClazz() + ", name: " + prop.getName(), "Attention", Messagebox.OK, Messagebox.ERROR);
            Textbox textBox = new Textbox();
            if (prop.getValue() != null) {
                textBox.setValue(prop.getValue().toString());
            }
            if (isRequired) {
                textBox.setConstraint("no empty");
            }
            textBox.addEventListener("onChange", new EventListener<Event>() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof InputEvent) {
                        InputEvent inputEvent = (InputEvent) event;
                        prop.setValue(inputEvent.getValue());
                    }
                }
            });
            return textBox;
        }
    }

    /**
     * Return the user selection as parameter for including into a request to the back-end.
     *
     * @param parameterCategory filter the parameter for just this category
     * @return Set of RequestParameterType
     */
    public Set<RequestParameterType<?>> readPluginProperties(final String parameterCategory) {
        Set<RequestParameterType<?>> requestProperties = new HashSet<>();

        if (this.currentPluginInfo != null) {
            PluginParameters mandatoryParameters = this.currentPluginInfo.getMandatoryParameters();
            PluginParameters optionalParameters = this.currentPluginInfo.getOptionalParameters();

            for (PluginParameter prop: mandatoryParameters.getParameter()) {
                if (prop.getCategory().equals(parameterCategory) || parameterCategory == null) {
                    RequestParameterType<?> requestProp = PluginHelper.convertToRequestParameter(prop);
                    if (requestProp != null) {
                        requestProperties.add(requestProp);
                    }
                }
            }

            for (PluginParameter prop: optionalParameters.getParameter()) {
                if (prop.getCategory().equals(parameterCategory) || parameterCategory == null) {
                    RequestParameterType<?> requestProp = PluginHelper.convertToRequestParameter(prop);
                    if (requestProp != null) {
                        requestProperties.add(requestProp);
                    }
                }
            }
        } else {
            LOGGER.warn("Could not read plugin parameters from UI, maybe there are none available!");
        }

        return requestProperties;
    }

}
