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

package org.apromore.portal.dialogController.renderer;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import org.apromore.model.AnnotationsType;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;

public class ProcessSummaryItemRenderer implements ListitemRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessSummaryItemRenderer.class.getName());
    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:center";
    private static final String VERTICAL_ALIGN = "vertical-align: middle;";

    private MainController mainController;


    public ProcessSummaryItemRenderer(MainController main) {
        this.mainController = main;
    }

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
      */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
        listItem.setStyle("height: 25px");
        if (obj instanceof ProcessSummaryType) {
            renderProcessSummary(listItem, (ProcessSummaryType) obj);
        } else if (obj instanceof FolderType) {
            renderFolder(listItem, (FolderType) obj);
        } else {
            LOGGER.error("Unknown item to render in the process summary list box.");
        }
    }

    /* Used to render the process summary information into the list box. */
    private void renderProcessSummary(final Listitem listItem, final ProcessSummaryType process) {
        listItem.appendChild(renderProcessImage());
        listItem.appendChild(renderProcessScore(process));
        listItem.appendChild(renderProcessName(process));
        listItem.appendChild(renderProcessId(process));
        listItem.appendChild(renderProcessNativeType(process));
        listItem.appendChild(renderProcessDomain(process));
        listItem.appendChild(renderVersionRanking(process));
        listItem.appendChild(renderProcessLastVersion(process));
        listItem.appendChild(renderProcessOwner(process));
        listItem.appendChild(renderProcessPQLIndexerStatus(process));

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                VersionSummaryType version = getLatestVersion(process.getVersionSummaries());
                AnnotationsType annotation = getLastestAnnotation(version.getAnnotations());
                if (annotation != null) {
                    mainController.editProcess(process, version, getNativeType(annotation.getNativeType()), annotation.getAnnotationName().get(0),
                        "false", new HashSet<RequestParameterType<?>>());
                } else {
                    mainController.editProcess(process, version, getNativeType(process.getOriginalNativeType()), null, "false",                            new HashSet<RequestParameterType<?>>());
                }
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

    /* Used to render folders in the list of process models. */
    private void renderFolder(final Listitem listitem, final FolderType folder) {
        listitem.appendChild(renderFolderImage());
        listitem.appendChild(renderFolderId(folder));
        listitem.appendChild(renderFolderName(folder));

        listitem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                UserSessionManager.setCurrentFolder(folder);
                mainController.reloadProcessSummaries();
            }
        });
    }

    private Listcell renderFolderImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.FOLDER_ICON));
        lc.setStyle(CENTRE_ALIGN);
        return lc;
    }

    private Listcell renderFolderId(FolderType folder) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(folder.getId().toString()));
        return lc;
    }

    private Listcell renderFolderName(FolderType folder) {
        Label name = new Label(folder.getFolderName());
        Listcell lc = new Listcell();
        lc.appendChild(name);
        lc.setSpan(7);
        return lc;
    }

    private Listcell renderProcessImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.PROCESS_ICON));
        lc.setStyle(CENTRE_ALIGN);
        return lc;
    }

    protected Listcell renderProcessOwner(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOwner()));
    }

    protected Listcell renderProcessLastVersion(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getLastVersion()));
    }

    protected Listcell renderProcessDomain(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getDomain()));
    }

    protected Listcell renderProcessNativeType(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOriginalNativeType()));
    }

    protected Listcell renderProcessName(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getName()));
    }

    private Component renderVersionRanking(final ProcessSummaryType process) {
        Hbox processRanking = new Hbox();
        if (process.getRanking() != null && process.getRanking().compareTo("") != 0) {
            displayRanking(processRanking, process.getRanking());
        } else {
            displayRanking(processRanking, "0");
        }
        processRanking.setStyle(CENTRE_ALIGN);
        return wrapIntoListCell(processRanking);
    }

    protected Listcell renderProcessId(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getId().toString()));
    }

    protected Listcell renderProcessScore(final ProcessSummaryType process) {
        Label processScoreLb = new Label();

        List<VersionSummaryType> processVersions = process.getVersionSummaries();
        // find the score of the latest version, if any: this a one which will  be displayed with the process
        int i = 0;
        while (i < processVersions.size() && processVersions.get(i).getName() != null && processVersions.get(i).getName().compareTo(process.getLastVersion()) != 0) {
            i++;
        }

        i = i - 1;

        // Each process should have at least one version. So it should have a legal value which
        // is the index of the process latest version. But some are faulty!!!
        if (i >= 0 && i < processVersions.size() && processVersions.get(i).getScore() != null) {
            processScoreLb.setValue(roundToDecimals(processVersions.get(i).getScore(), 4).toString());
        } else {
            processScoreLb.setValue("1.0");
        }

        return wrapIntoListCell(processScoreLb);
    }

    protected Listcell renderProcessPQLIndexerStatus(final ProcessSummaryType process) {

        // Associate an icon with the indexing status
        String iconPath;
        if (process.getPqlIndexerStatus() == null) {
            iconPath = Constants.PQL_ERROR_ICON;
        } else {
            switch (process.getPqlIndexerStatus()) {
            case UNINDEXED:   iconPath = Constants.PQL_UNINDEXED_ICON;    break;
            case INDEXING:    iconPath = Constants.PQL_INDEXING_ICON;     break;
            case INDEXED:     iconPath = Constants.PQL_INDEXED_ICON;      break;
            case CANNOTINDEX: iconPath = Constants.PQL_CANNOTINDEX_ICON;  break;
            default:          iconPath = Constants.PQL_ERROR_ICON;
            }
        }
        assert iconPath != null;

        // Return a list cell containing the indexing status icon
        Listcell lc = new Listcell();
        lc.appendChild(new Image(iconPath));
        lc.setStyle(CENTRE_ALIGN);
        return lc;
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        return lc;
    }

    /**
     * Display in hbox versionRanking, 5 stars according to ranking (0...5).
     * Pre-condition: ranking is a non empty string. TODO: allow users to rank a
     * process version directly by interacting with the stars displayed.
     * @param rankingHb the Horizontal box to display it
     * @param ranking the ranking to display
     */
    private void displayRanking(Hbox rankingHb, String ranking) {
        String imgFull = Constants.STAR_FULL_ICON;
        String imgMid = Constants.STAR_MID_ICON;
        String imgBlank = Constants.STAR_BLK_ICON;
        Image star;
        Float rankingF = Float.parseFloat(ranking);
        int fullStars = rankingF.intValue();
        int i;
        for (i = 1; i <= fullStars; i++) {
            star = new Image();
            star.setStyle(VERTICAL_ALIGN);
            rankingHb.appendChild(star);
            star.setSrc(imgFull);
        }
        if (i <= 5) {
            if (Math.floor(rankingF) != rankingF) {
                star = new Image();
                star.setStyle(VERTICAL_ALIGN);
                star.setSrc(imgMid);
                rankingHb.appendChild(star);
                i = i + 1;
            }
            for (int j = i; j <= 5; j++) {
                star = new Image();
                star.setStyle(VERTICAL_ALIGN);
                star.setSrc(imgBlank);
                rankingHb.appendChild(star);
            }
        }
    }

    public static Double roundToDecimals(Double num, int places) {
        int temp = (int) ((num * Math.pow(10, places)));
        return ((double) temp) / Math.pow(10, places);
    }

    private VersionSummaryType getLatestVersion(List<VersionSummaryType> versionSummaries) {
        VersionSummaryType result = null;
        for (VersionSummaryType version : versionSummaries) {
            if (result == null || (version.getVersionNumber().compareTo(result.getVersionNumber()) > 0)) {
                result = version;
            }
        }
        return result;
    }


    private AnnotationsType getLastestAnnotation(List<AnnotationsType> annotations) {
        if (annotations.size() > 0 && annotations.get(annotations.size() - 1) != null) {
            return annotations.get(annotations.size() - 1);
        }
        return null;
    }

}
