/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
package org.apromore.plugin.portal.pql;

// Java 2 Standard
import java.util.List;
import java.util.Locale;

// Java 2 Enterprise
import javax.inject.Inject;

// Third party
import org.pql.index.IndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Popup;

// First party
import org.apromore.helper.Version;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.service.pql.ExternalId;
import org.apromore.service.pql.PQLService;

/**
 * Adds the "queryable?" attribute to the Portal's process summary list.
 */
@Component("queryableProcessAttributePlugin")
public class QueryableProcessAttributePlugin extends DefaultParameterAwarePlugin implements PortalProcessAttributePlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryableProcessAttributePlugin.class);

    // Icons displayed in the "Queryable?" column of the process summary list
    public static final String PQL_UNINDEXED_ICON   = "/img/unselect.png";
    public static final String PQL_INDEXING_ICON    = "/img/arrow_refresh.png";
    public static final String PQL_INDEXED_ICON     = "/img/select.png";
    public static final String PQL_CANNOTINDEX_ICON = "/img/cross.png";
    public static final String PQL_ERROR_ICON       = "/img/error.png";

    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:center";

    @Inject private PQLService pqlService;

    /** {@inheritDoc} */
    public Listheader getListheader() {
        Listheader listheader = new Listheader();
        listheader.setAlign("center");
        listheader.setHflex("0");
        listheader.setLabel("PQL");
        listheader.setWidth("3em");
        return listheader;
    }

    /**
     * Present the column value for a particular process summary row
     *
     * @param process 
     */
    public Listcell getListcell(ProcessSummaryType process) {
        int id = 0;
        String branch = null;
        Version version = null;
        try {
            id = process.getId();
            List<VersionSummaryType> list = process.getVersionSummaries();
            VersionSummaryType lastVersion = list.get(list.size() - 1);  // get the last version
            version = new Version(lastVersion.getVersionNumber());
            branch = "MAIN";
            IndexStatus status = pqlService.getIndexStatus(new ExternalId(id, branch, version));

            // Associate an icon with the indexing status
            switch (status) {
            case UNINDEXED:   return createListcell(PQL_UNINDEXED_ICON,   "Unindexed");
            case INDEXING:    return createListcell(PQL_INDEXING_ICON,    "Indexing");
            case INDEXED:     return createListcell(PQL_INDEXED_ICON,     "Indexed");
            case CANNOTINDEX: return createListcell(PQL_CANNOTINDEX_ICON, "Cannot index");
            default:          return createListcell(PQL_ERROR_ICON,       "Unknown index status code: " + status);
            }
        } catch (Throwable e) {
            LOGGER.warn("Unable to obtain index status for process " + id + "/" + branch + "/" + version, e);
            return createListcell(PQL_ERROR_ICON, e.toString());
        }
    }

    private Listcell createListcell(String iconPath, String tooltip) {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(iconPath));
        lc.setStyle(CENTRE_ALIGN);
        if (tooltip != null) {
            Label label = new Label(tooltip);
            label.setMultiline(true);
            Popup popup = new Popup();
            popup.appendChild(label);
            lc.appendChild(popup);
            lc.setTooltip(popup);
        }
        return lc;
    }
}
