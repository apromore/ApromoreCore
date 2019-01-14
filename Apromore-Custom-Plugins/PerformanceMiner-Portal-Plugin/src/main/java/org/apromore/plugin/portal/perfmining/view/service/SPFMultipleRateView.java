package org.apromore.plugin.portal.perfmining.view.service;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
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

public class SPFMultipleRateView extends SPFView {
	public SPFMultipleRateView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	public XYDataset createArrivalRateDataset() {
		TimeSeries timeseries = new TimeSeries("Arrival");
		Stage stage = bpf.getStageByName(stageName);

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				timeseries.add((RegularTimePeriod) timeP,
						1.0 * stage.getFlowCells().get(i).getCharacteristic(SPF.CHAR_SERVICE_ARRIVAL_RATE));
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}

	public XYDataset createPassedRateDataset() {
		TimeSeries timeseries = new TimeSeries("Passed");
		Stage stage = bpf.getStageByName(stageName);

		try {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				timeseries.add((RegularTimePeriod) timeP,
						1.0 * stage.getFlowCells().get(i).getCharacteristic(SPF.CHAR_SERVICE_PASSED_RATE));
			}
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
		return new TimeSeriesCollection(timeseries);
	}

	public List<XYDataset> createExitRateDataset() {
		List<XYDataset> datasetList = new ArrayList<XYDataset>();
		Stage stage = bpf.getStageByName(stageName);

		try {
			for (String exitType : stage.getServiceExitSubCounts().keySet()) {
				TimeSeries timeseries = new TimeSeries(exitType);
				for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
					TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
					timeseries.add(
							(RegularTimePeriod) timeP,
							1.0 * stage.getFlowCells().get(i)
									.getCharacteristic(SPF.CHAR_SERVICE_EXIT_RATE_TYPE + exitType));
				}
				datasetList.add(new TimeSeriesCollection(timeseries));
			}

		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}

		return datasetList;
	}
}
