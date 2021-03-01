/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController.renderer;

import java.util.HashSet;
import java.util.List;

import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderSummaryType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.commons.datetime.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SummaryItemRenderer implements ListitemRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SummaryItemRenderer.class.getName());
    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:center";
    private static final String VERTICAL_ALIGN = "vertical-align: middle;";

    private MainController mainController;

    public SummaryItemRenderer(MainController main) {
        this.mainController = main;
    }

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
      */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
        List<PortalProcessAttributePlugin> plugins = (List<PortalProcessAttributePlugin>) SpringUtil.getBean("portalProcessAttributePlugins");

        // listItem.setStyle("height: 25px");
        if (obj instanceof ProcessSummaryType) {
            listItem.setSclass(listItem.getSclass() + " ap-item-model");
            renderProcessSummary(listItem, (ProcessSummaryType) obj, plugins);
        } else if (obj instanceof LogSummaryType) {
            listItem.setSclass(listItem.getSclass() + " ap-item-log");
            renderLogSummary(listItem, (LogSummaryType) obj, plugins);
        } else if (obj instanceof FolderSummaryType) {
            listItem.setSclass(listItem.getSclass() + " ap-item-folder");
            renderFolderSummary(listItem, (FolderSummaryType) obj, plugins);
        } else if (obj instanceof FolderType) {
            listItem.setSclass(listItem.getSclass() + " ap-item-folder");
            renderFolder(listItem, (FolderType) obj, plugins);
        } else {
            LOGGER.error("Unknown item to render in the process summary list box.");
        }
    }

    /* Used to render the process summary information into the list box. */
    private void renderProcessSummary(final Listitem listItem, final ProcessSummaryType process, final List<PortalProcessAttributePlugin> plugins) {
        listItem.appendChild(renderProcessImage());
        listItem.appendChild(renderProcessScore(process));
        listItem.appendChild(renderName(process));
        listItem.appendChild(renderId(process));
        listItem.appendChild(renderProcessNativeType(process));
        listItem.appendChild(renderDomain(process));
        listItem.appendChild(renderVersionRanking(process));
        listItem.appendChild(renderProcessLastVersion(process));
        listItem.appendChild(renderProcessLastUpdate(process));
        listItem.appendChild(renderOwner(process));

        // Append columns for any process attributes supplied via plugins
        for (PortalProcessAttributePlugin plugin: plugins) {
            listItem.appendChild(plugin.getListcell(process));
        }

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                VersionSummaryType version = getLatestVersion(process.getVersionSummaries());
                mainController.editProcess2(process, version, getNativeType(process.getOriginalNativeType()), new HashSet<RequestParameterType<?>>(), false);
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

    /* Used to render the process summary information into the list box. */
    private void renderLogSummary(final Listitem listItem, final LogSummaryType log, final List<PortalProcessAttributePlugin> plugins) {
        listItem.appendChild(renderLogImage());
        listItem.appendChild(renderNA());
        listItem.appendChild(renderName(log));
        listItem.appendChild(renderId(log));
        listItem.appendChild(renderOpenXES());
        listItem.appendChild(renderDomain(log));
        listItem.appendChild(renderNA());
        listItem.appendChild(renderNA());
        listItem.appendChild(renderNA());
        listItem.appendChild(renderOwner(log));

        // Append columns for any log attributes supplied via plugins
        for (PortalProcessAttributePlugin plugin: plugins) {
            listItem.appendChild(plugin.getListcell(log));
        }

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                mainController.visualizeLog();
            }
        });
    }

    private void renderFolderSummary(final Listitem listitem, final FolderSummaryType folder, final List<PortalProcessAttributePlugin> plugins) {
        listitem.appendChild(renderFolderImage());
        listitem.appendChild(new Listcell());
        listitem.appendChild(new Listcell(folder.getName()));
        listitem.appendChild(new Listcell(folder.getId().toString()));

        // Skip 5 columns that don't apply to folders
        Listcell spacer = new Listcell();
        spacer.setSpan(6);
        listitem.appendChild(spacer);

        /*
        // Append columns for any folder attributes supplied via plugins
        for (PortalProcessAttributePlugin plugin: plugins) {
            listitem.appendChild(plugin.getListcell(folder));  // PortalProcessAttributePlugin.getListCell(FolderSampleType) doesn't exist
        }
        */

        listitem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                // UserSessionManager.setCurrentFolder(convertFolderSummaryTypeToFolderType(folder));
                mainController.getPortalSession().setCurrentFolder(convertFolderSummaryTypeToFolderType(folder));
                mainController.reloadSummaries2();
                mainController.currentFolderChanged();
            }
        });
    }

    private FolderType convertFolderSummaryTypeToFolderType(final FolderSummaryType summary) {
        FolderType folder = new FolderType();
        folder.setFolderName(summary.getName());
        folder.setId(summary.getId());
        folder.setHasRead(summary.isHasRead());
        folder.setHasWrite(summary.isHasWrite());
        folder.setHasOwnership(summary.isHasOwnership());

        return folder;
    }

    /* Used to render folders in the list of process models. */
    private void renderFolder(final Listitem listitem, final FolderType folder, final List<PortalProcessAttributePlugin> plugins) {
        listitem.appendChild(renderFolderImage());
        listitem.appendChild(new Listcell());
        listitem.appendChild(renderFolderName(folder));
        listitem.appendChild(renderFolderId(folder));

        // Skip 5 columns that don't apply to folders
        Listcell spacer = new Listcell();
        spacer.setSpan(6);
        listitem.appendChild(spacer);

        // Append columns for any folder attributes supplied via plugins
        for (PortalProcessAttributePlugin plugin: plugins) {
            listitem.appendChild(plugin.getListcell(folder));
        }

        listitem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                // UserSessionManager.setCurrentFolder(folder);
                mainController.getPortalSession().setCurrentFolder(folder);
                mainController.reloadSummaries2();
                mainController.currentFolderChanged();
            }
        });
    }

    private Listcell renderFolderImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.FOLDER_ICON));
        lc.setStyle(CENTRE_ALIGN);
        lc.setSclass(Constants.FOLDER_ICOCLS);
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
        return lc;
    }

    private Listcell renderLogImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.LOG_ICON));
        lc.setStyle(CENTRE_ALIGN);
        lc.setSclass(Constants.LOG_ICOCLS);
        return lc;
    }

    private Listcell renderProcessImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.PROCESS_ICON));
        lc.setStyle(CENTRE_ALIGN);
        lc.setSclass(Constants.PROCESS_ICOCLS);
        return lc;
    }

    protected Listcell renderOpenXES() {
        return wrapIntoListCell(new Label("XES"));
    }

    protected Listcell renderNA() {
        return wrapIntoListCell(new Label(" "));
    }

    protected Listcell renderOwner(final SummaryType summaryType) {
        Boolean isMakePublic = summaryType.isMakePublic();
        String owner = summaryType.getOwnerName();
        Label label = new Label(owner);
        label.setClientAttribute("title", owner);
        return wrapIntoListCell(label);
    }

    protected Listcell renderProcessLastVersion(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getLastVersion()));
    }

    protected Listcell renderProcessLastUpdate(final ProcessSummaryType process) {
        List<VersionSummaryType> summaries = process.getVersionSummaries();
        int lastIndex = summaries.size() - 1;
        String lastUpdate = summaries.get(lastIndex).getLastUpdate();

        if (lastUpdate != null) {
            lastUpdate = DateTimeUtils.normalize(lastUpdate);
        }
        return wrapIntoListCell(new Label(lastUpdate));
    }

    protected Listcell renderDomain(final SummaryType summaryType) {
        return wrapIntoListCell(new Label(summaryType.getDomain()));
    }

    protected Listcell renderProcessNativeType(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOriginalNativeType()));
    }

    protected Listcell renderName(final SummaryType summaryType) {
        String name = summaryType.getName();
        Label label = new Label(name);
        label.setTooltiptext(name);
        return wrapIntoListCell(label);
    }

    private Component renderVersionRanking(final ProcessSummaryType process) {
        Hbox processRanking = new Hbox();
        if (process.getRanking() != null && process.getRanking().compareTo("") != 0) {
            // displayRanking(processRanking, process.getRanking());
            displayRating(processRanking, process.getRanking());
        } else {
            // displayRanking(processRanking, "0");
            displayRating(processRanking, "0");
        }
        processRanking.setStyle(CENTRE_ALIGN);
        return wrapIntoListCell(processRanking);
    }

    protected Listcell renderId(final SummaryType summaryType) {
        return wrapIntoListCell(new Label(summaryType.getId().toString()));
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

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        if (cp instanceof Label) {
            Label lbl = (Label)cp;
            lc.setLabel(lbl.getValue());
            lc.setTooltiptext(lbl.getTooltiptext());
        } else {
            lc.appendChild(cp);
        }
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

    /**
     * Display in hbox versionRanking, 5 stars according to ranking (0...5).
     * Pre-condition: ranking is a non empty string.
     * @param rankingHb the Horizontal box to display it
     * @param ranking the ranking to display
     */
    private void displayRating(Hbox rankingHb, String ranking) {
        Button star;
        Float rankingF = Float.parseFloat(ranking);
        int fullStars = rankingF.intValue();
        int i;
        for (i = 1; i <= fullStars; i++) {
            star = new Button();
            star.setSclass(Constants.STAR_FULL_CLS + " ap-star-" + i);
            rankingHb.appendChild(star);
        }
        if (i <= 5) {
            if (Math.floor(rankingF) != rankingF) {
                star = new Button();
                star.setSclass(Constants.STAR_HALF_CLS + " ap-star-" + i);
                rankingHb.appendChild(star);
                i = i + 1;
            }
            for (int j = i; j <= 5; j++) {
                star = new Button();
                star.setSclass(Constants.STAR_NONE_CLS);
                rankingHb.appendChild(star);
            }
        }
    }

    public static Double roundToDecimals(Double num, int places) {
        int temp = (int) ((num * Math.pow(10, places)));
        return (temp) / Math.pow(10, places);
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

}
