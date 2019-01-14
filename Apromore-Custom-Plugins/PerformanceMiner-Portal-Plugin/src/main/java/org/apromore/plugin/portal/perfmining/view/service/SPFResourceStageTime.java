package org.apromore.plugin.portal.perfmining.view.service;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFResourceStageTime extends SPFView {
	public SPFResourceStageTime(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	public XYDataset createResourceTimeRateDataset() {
		TimeSeries timeseries = new TimeSeries("Resource Time");
		bpf.getStageByName(stageName);

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				getTimePeriod(bpf.getTimeSeries().get(i));
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}

	public XYDataset createStageTimeDataset() {
		TimeSeries timeseries = new TimeSeries("Stage Time");
		bpf.getStageByName(stageName);

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				getTimePeriod(bpf.getTimeSeries().get(i));
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}
}
