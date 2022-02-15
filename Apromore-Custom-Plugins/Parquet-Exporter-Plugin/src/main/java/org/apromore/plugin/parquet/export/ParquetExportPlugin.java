/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.plugin.parquet.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apromore.apmlog.APMLog;
import org.apromore.plugin.parquet.export.core.data.APMLogCombo;
import org.apromore.plugin.parquet.export.service.APMLogComboManager;
import org.apromore.plugin.parquet.export.service.ParquetExporterService;
import org.apromore.plugin.parquet.export.util.LabelUtil;
import org.apromore.plugin.parquet.export.util.PageUtil;
import org.apromore.plugin.parquet.export.util.SeriesIdGenerator;
import org.apromore.plugin.parquet.export.util.Util;
import org.apromore.plugin.parquet.export.util.ZKMessageCtrl;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.EventLogService;
import org.apromore.zk.label.LabelSupplier;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

@Component
public class ParquetExportPlugin extends DefaultPortalPlugin implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ParquetExportPlugin.class);
    private static final String LOG_ERR_MSG_KEY="Failed to open Parquet Exporter window";

    @Inject
    private EventLogService eventLogService;

    private String label = "Parquet download";

    private final String[][] palette = new String[][]{{"blue", "#84C7E3"}, {"red", "#C96173"},
        {"yellow", "#FCB751"}, {"green", "#61CDBA"}, {"orange", "#FB9859"}, {"olive", "#C0CA33"},
        {"violet", "#7E77C2"}, {"pink", "#F48FB1"}, {"purple", "#AB66BC"}, {"fog", "#B4CCD8"},
        {"brown", "#997766"}, {"gray", "#999"},};

    @Override
    public void execute(PortalContext portalContext) {
        try {
            List<Integer> lstList = Sessions.getCurrent().getAttributes().containsKey("logParquetDownload") ?
                (List<Integer>) Sessions.getCurrent().getAttribute("logParquetDownload") : new ArrayList<>();

            if (lstList.isEmpty()) {
                LOGGER.error("Unable to find the log data");
                ZKMessageCtrl.showInfo(getLabel("dash_select_one_plus_logs"));
                return;
            }

            APMLogComboManager apmLogComboManager = initAPMLogComboManagers(lstList);
            if (apmLogComboManager.getAPMLogComboList().size()!=1) {
                LOGGER.error("Select one log for parquet download");
                ZKMessageCtrl.showInfo(getLabel("dash_wrong_log_select"));
                return;
            }
            onExportParquetConfig(apmLogComboManager.get(0));
        } catch (Exception e) {
            LOGGER.error(LOG_ERR_MSG_KEY, e);
            ZKMessageCtrl.showInfo(getLabel("dash_parquet_gen_fail"));
        }
    }

    private void onExportParquetConfig(APMLogCombo apmLogCombo) {
        try {
            Window wd = (Window) PageUtil.getPageWithArgument(
                "parquetExporter.zul", null, Map.of("service", new ParquetExporterService(apmLogCombo)));
            wd.doModal();
        } catch (IOException e) {
            LOGGER.error(LOG_ERR_MSG_KEY, e);
        }
    }

    private APMLogComboManager initAPMLogComboManagers(List<Integer> logSummaries) {
        APMLogComboManager  apmLogComboManager = new APMLogComboManager();
        for (int i=0;i<logSummaries.size();i++) {
            Integer logIdInt=logSummaries.get(i);
            APMLog apmLog = eventLogService.getAggregatedLog(logIdInt);
            XLog xLog = eventLogService.getXLog(logIdInt);
            String filename = apmLog.getLogName();
            if (xLog == null ||  apmLog.size() != xLog.size()) {
                ZKMessageCtrl.showError(String.format(LabelUtil.getLabel(LOG_ERR_MSG_KEY), filename));
            } else {
                String color = getDefaultSeriesColor(i);
                int fileExtIndex = filename.indexOf(".");
                String nameWithoutExt = fileExtIndex != -1 ? filename.substring(0, fileExtIndex) : filename;
                String seriesId = SeriesIdGenerator.getSeriesId(getValidLogName(nameWithoutExt));
                apmLogComboManager.put(logIdInt, seriesId, apmLog, xLog, color, nameWithoutExt);
            }
        }
    return apmLogComboManager;
    }

    @Override
    public String getIconPath() {
        return "download.svg";
    }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("dash_download_parquet", label);
    }

    @Override
    public String getBundleName() {
        return "parquetexporter";
    }

    private String getDefaultSeriesColor(int seriesIndex) {
        if (seriesIndex < palette.length) {
            return palette[seriesIndex][1];
        }
        return "#757575";
    }
    private String getValidLogName(String rawLogName) {
        if (Util.isNumeric(String.valueOf(rawLogName.charAt(0))))
            rawLogName = "Log" + rawLogName;
        return rawLogName.replaceAll(" ", "");
    }

}
