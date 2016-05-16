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
import java.util.Locale;

// Third party
import org.springframework.stereotype.Component;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;

// First party
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;

/**
 * Adds the "queryable?" attribute to the Portal's process summary list.
 */
@Component("queryableProcessAttributePlugin")
public class QueryableProcessAttributePlugin extends DefaultParameterAwarePlugin implements PortalProcessAttributePlugin {

    // Icons displayed in the "Queryable?" column of the process summary list
    public static final String PQL_UNINDEXED_ICON   = "/img/add.png";
    public static final String PQL_INDEXING_ICON    = "/img/arrow_refresh.png";
    public static final String PQL_INDEXED_ICON     = "/img/select.png";
    public static final String PQL_CANNOTINDEX_ICON = "/img/cross.png";
    public static final String PQL_ERROR_ICON       = "/img/alert.png";

    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:center";

    /** {@inheritDoc} */
    public String getLabel(Locale locale) {
        return "PQL";
    }

    /**
     * Present the column value for a particular process summary row
     *
     * @param process 
     */
    public Listcell getListcell(ProcessSummaryType process) {

        // Associate an icon with the indexing status
        String iconPath;
        iconPath = PQL_ERROR_ICON;
        /*
        if (process.getPqlIndexerStatus() == null) {
            iconPath = Constants.PQL_ERROR_ICON;
        } else {
            switch (process.getPqlIndexerStatus()) {
            case UNINDEXED:   iconPath = PQL_UNINDEXED_ICON;    break;
            case INDEXING:    iconPath = PQL_INDEXING_ICON;     break;
            case INDEXED:     iconPath = PQL_INDEXED_ICON;      break;
            case CANNOTINDEX: iconPath = PQL_CANNOTINDEX_ICON;  break;
            default:          iconPath = PQL_ERROR_ICON;
            }
        }
        */
        assert iconPath != null;

        // Return a list cell containing the indexing status icon
        Listcell lc = new Listcell();
        lc.appendChild(new Image(iconPath));
        lc.setStyle(CENTRE_ALIGN);
        return lc;
    }
}

