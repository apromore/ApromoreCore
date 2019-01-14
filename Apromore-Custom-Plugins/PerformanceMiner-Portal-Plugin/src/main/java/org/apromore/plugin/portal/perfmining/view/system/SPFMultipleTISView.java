// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space

package org.apromore.plugin.portal.perfmining.view.system;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFMultipleTISView extends SPFView {
	public SPFMultipleTISView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	private XYDataset createDataset(Stage stage) {
		TimeSeries timeseries = new TimeSeries(stage.getName());

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				timeseries.add((RegularTimePeriod) timeP,
						1.0 * stage.getFlowCells().get(i).getCharacteristic(SPF.CHAR_SERVICE_TIS));
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}

        @Override
	protected XYDataset createDataset() {
		TimeSeries timeseries = new TimeSeries("Total");

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				double total = 0;
				for (Stage stage : bpf.getStages()) {
					total += stage.getFlowCells().get(i).getCharacteristic(SPF.CHAR_SERVICE_TIS);
				}
				timeseries.add((RegularTimePeriod) timeP, total);
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}
}
