package org.apromore.portal.dialogController;

import java.util.HashSet;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.manager.client.ManagerService;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.PluginProperty;
import org.apromore.plugin.property.RequestPropertyType;
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

    private final ManagerService service;
    private final Grid propertiesGrid;

    private PluginInfoResult currentPluginInfo;
    private Rows gridRows;

    public PluginPropertiesHelper(final ManagerService service, final Grid propertiesGrid) {
        this.service = service;
        this.propertiesGrid = propertiesGrid;
    }


    /**
     *  Show properties of Plugin in Grid
     *
     * @param info basic info about a Plugin
     * @return more info about the selected Plugin
     * @throws InterruptedException
     */
    public PluginInfoResult showPluginProperties(final PluginInfo info) throws InterruptedException  {
        try {
            if (gridRows != null) {
                propertiesGrid.removeChild(gridRows);
            }
            gridRows = new Rows();
            propertiesGrid.appendChild(gridRows);

            Clients.showBusy(this.gridRows, "Reading available properties for "+info.getName()+"...");
            currentPluginInfo = service.readPluginInfo(info.getName(), info.getVersion());
            Clients.clearBusy(this.gridRows);


            for (PluginProperty prop: currentPluginInfo.getMandatoryProperties().getProperty()) {
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

            for (PluginProperty prop: currentPluginInfo.getOptionalProperties().getProperty()) {
                Row propertyRow = new Row();
                Label labelName = new Label(prop.getName() + " *");
                if (prop.getDescription() != null) {
                    labelName.setTooltip(prop.getDescription());
                }
                propertyRow.appendChild(labelName);
                Component inputValue = createInputComponent(prop, false);
                propertyRow.appendChild(inputValue);
                gridRows.appendChild(propertyRow);
            }

            return currentPluginInfo;
        } catch (Exception e) {
            Messagebox.show("Showing Plugin Properties failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
        return null;
    }


    private static Component createInputComponent(final PluginProperty prop, final boolean isRequired) throws InterruptedException {
        if (prop.getValue() instanceof String) {
            Textbox textBox = new Textbox();
            if (prop.getValue() != null) {
                textBox.setValue(prop.getValue().toString());
            }
            if (isRequired) {
                textBox.setConstraint("no empty");
            }
            textBox.addEventListener("onChange", new EventListener() {

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
                longBox.setValue((Long)prop.getValue());
            }
            if (isRequired) {
                longBox.setConstraint("no empty");
            }
            longBox.addEventListener("onChange", new EventListener() {

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
                intBox.setValue((Integer)prop.getValue());
            }
            if (isRequired) {
                intBox.setConstraint("no empty");
            }
            intBox.addEventListener("onChange", new EventListener() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof InputEvent) {
                        InputEvent inputEvent = (InputEvent) event;
                        prop.setValue(new Integer(inputEvent.getValue()));
                    }
                }
            });
            return intBox;
        } else if (prop.getValue() instanceof Boolean) {
            Checkbox booleanBox = new Checkbox();
            if (prop.getValue() != null) {
                booleanBox.setChecked((Boolean)prop.getValue());
            }
            booleanBox.addEventListener("onCheck", new EventListener() {

                @Override
                public void onEvent(final Event event) {
                    if (event instanceof CheckEvent) {
                        CheckEvent checkEvent = (CheckEvent) event;
                        prop.setValue(new Boolean(checkEvent.isChecked()));
                    }
                }
            });
            return booleanBox;
        } else if (prop.getClazz().equals("java.io.InputStream")) {
            Fileupload fileButton = new Fileupload(prop.getName());
            fileButton.setSclass("attachBtn");
            fileButton.setAttribute("upload", "true");
            fileButton.addEventListener("onUpload", new EventListener() {

                @Override
                public void onEvent(final Event event) throws Exception {
                    if (event instanceof UploadEvent) {
                        UploadEvent uploadEvent = (UploadEvent) event;
                        prop.setValue(new DataHandler(new ByteArrayDataSource(uploadEvent.getMedia().getStreamData(), "application/octet-stream")));
                    }
                }
            });
            return fileButton;
        } else {
            Messagebox.show("Unkown property type: "+prop.getClazz()+", name: "+prop.getName(), "Attention", Messagebox.OK, Messagebox.ERROR);
            return new Textbox();
        }
    }

    public Set<RequestPropertyType<?>> readPluginProperties() {
        Set<RequestPropertyType<?>> mandatoryProperties = PluginHelper.convertToRequestProperties(this.currentPluginInfo.getMandatoryProperties());
        Set<RequestPropertyType<?>> optionalProperties = PluginHelper.convertToRequestProperties(this.currentPluginInfo.getOptionalProperties());
        Set<RequestPropertyType<?>> requestProperties = new HashSet<>(mandatoryProperties);
        requestProperties.addAll(optionalProperties);
        return requestProperties;
    }



}
