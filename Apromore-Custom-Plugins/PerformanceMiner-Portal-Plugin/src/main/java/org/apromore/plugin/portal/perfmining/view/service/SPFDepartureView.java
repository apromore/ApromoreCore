package org.apromore.plugin.portal.perfmining.view.service;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFDepartureView extends SPFView {
	public SPFDepartureView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	@Override
	protected XYDataset createDataset() {
		TimeTableXYDataset dataset = new TimeTableXYDataset();
		Stage stage = bpf.getStageByName(stageName);

		for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
			TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
			dataset.add(timeP, stage.getServicePassedCounts().get(i), stage.getName() + "-passed");
		}

		for (String exitType : stage.getServiceExitSubCounts().keySet()) {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				dataset.add(timeP, stage.getServiceExitSubCounts().get(exitType).get(i), stage.getName() + "-"
						+ exitType);
			}
		}

		return dataset;
	}
}
