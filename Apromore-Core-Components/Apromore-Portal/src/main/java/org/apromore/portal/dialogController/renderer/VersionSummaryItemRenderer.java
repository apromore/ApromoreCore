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

package org.apromore.portal.dialogController.renderer;

import org.apromore.model.AnnotationsType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.HashSet;
import java.util.List;

public class VersionSummaryItemRenderer implements ListitemRenderer {

    private MainController mainController;


    public VersionSummaryItemRenderer(MainController main) {
        this.mainController = main;
    }

    /*
     * (non-Javadoc)
     * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
     */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
        renderVersionSummary(listItem, (VersionDetailType) obj);
    }

    private void renderVersionSummary(final Listitem listItem, final VersionDetailType data) {
        listItem.appendChild(renderVersionName(data.getVersion()));
        listItem.appendChild(renderVersionVersion(data.getVersion()));
        listItem.appendChild(renderVersionLastUpdate(data.getVersion()));
        listItem.appendChild(renderVersionAnnotations(data.getVersion()));

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                AnnotationsType annotation = getLastestAnnotation(data.getVersion().getAnnotations());
                String nativeType = (annotation != null) ? getNativeType(annotation.getNativeType()) : getNativeType(data.getProcess().getOriginalNativeType());
                String annotationName = (annotation != null) ? annotation.getAnnotationName().get(0) : null;
                if (nativeType.equals("BPMN 2.0")) {
                    mainController.editProcess2(data.getProcess(), data.getVersion(), nativeType, annotationName,
                        "false", new HashSet<RequestParameterType<?>>(), false);
                } else {
                    mainController.editProcess(data.getProcess(), data.getVersion(), nativeType, null, "false",
                            new HashSet<RequestParameterType<?>>());
                }
                /*
                if (annotation != null) {
                    mainController.editProcess(data.getProcess(), data.getVersion(), getNativeType(annotation.getNativeType()),
                        annotation.getAnnotationName().get(0), "false", new HashSet<RequestParameterType<?>>());
                } else {
                    mainController.editProcess(data.getProcess(), data.getVersion(), getNativeType(data.getProcess().getOriginalNativeType()),
                            null, "false", new HashSet<RequestParameterType<?>>());
                }
                */
            }

            /* Sometimes we have merged models with no native type, we should give them a default so they can be edited. */
            private String getNativeType(String origNativeType) {
                String nativeType = origNativeType;
                if (origNativeType == null || origNativeType.isEmpty()) {
                    nativeType = "BPMN 2.0";
                }
                return nativeType;
            }
        });
    }

    private Component renderVersionVersion(VersionSummaryType version) {
        return wrapIntoListCell(new Label(version.getVersionNumber()));
    }

    private Component renderVersionAnnotations(VersionSummaryType version) {
        Listbox annotationLB = new Listbox();
        Listitem annotationsI = new Listitem();
        annotationLB.setMold("select");
        annotationLB.setRows(1);
        annotationLB.setWidth("100%");
        annotationLB.setStyle(Constants.UNSELECTED_VERSION);
        if (version.getAnnotations().size() > 0) {
            for (int i = 0; i < version.getAnnotations().size(); i++) {
                String language = version.getAnnotations().get(i).getNativeType();
                for (int k = 0; k < version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                    annotationLB.appendChild(annotationsI);
                    annotationsI.setLabel(version.getAnnotations().get(i).getAnnotationName().get(k) + " (" + language + ")");
                }
            }
            annotationLB.selectItem(annotationLB.getItemAtIndex(version.getAnnotations().size() - 1));
        } else {
            annotationLB.appendChild(annotationsI);
            annotationsI.setLabel("N/A");
            annotationLB.selectItem(annotationLB.getItemAtIndex(0));
        }
        return wrapIntoListCell(annotationLB);
    }

    private Component renderVersionLastUpdate(VersionSummaryType version) {
        if (version.getLastUpdate() == null || version.getLastUpdate().isEmpty()) {
            return wrapIntoListCell(new Label(version.getCreationDate()));
        } else {
            return wrapIntoListCell(new Label(version.getLastUpdate()));
        }
    }

    private Component renderVersionName(VersionSummaryType version) {
        return wrapIntoListCell(new Label(version.getName()));
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        return lc;
    }

    private AnnotationsType getLastestAnnotation(List<AnnotationsType> annotations) {
        if (annotations.size() > 0 && annotations.get(annotations.size() - 1) != null) {
            return annotations.get(annotations.size() - 1);
        }
        return null;
    }
}
