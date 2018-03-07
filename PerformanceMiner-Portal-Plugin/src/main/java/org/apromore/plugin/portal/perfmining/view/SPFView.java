package org.apromore.plugin.portal.perfmining.view;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFView {
	/**
	 * 
	 */
	private static final long serialVersionUID = -202881433419871306L;
	private static final double ZOOM_IN_FACTOR = 0.9; //the new range of axis is 90% of current range
	private static final double ZOOM_OUT_FACTOR = (1.0 / 0.909); //the new range of axis is 110% of current range
        protected String stageName;
        protected String subStageName;
	protected String viewName;
	protected SPF bpf;
	protected ChartPanel chartPanel;
	protected Map<TimePeriod, DateTime> timeMap = new HashMap<TimePeriod, DateTime>(); //to map the time on chart to time point
	BoundedRangeModel orihoriScrollbarModel = null;
        protected boolean isCategory = false;

	public SPFView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
            this.stageName = stageName;
            this.subStageName = subStageName;
            this.viewName = viewName;
            this.bpf = bpf;
            this.isCategory = isCategory;
	}

	public SPF getBPF() {
            return bpf;
	}

	public String getStageName() {
            return stageName;
	}
        
        public String getSubStageName() {
            return subStageName;
	}
        
        public String getViewName() {
            return viewName;
	}

        public String getFullName() {
            return stageName + "-" + subStageName + "-" + viewName;
	}
        
	protected TimePeriod getTimePeriod(DateTime timePoint) {
            if (bpf.getConfig().getTimeStep() <= 3600) {
                    return new Hour(timePoint.toDate());
            } else {
                    return new Day(timePoint.toDate());
            }
	}
        
        public boolean isCategory() {
            return this.isCategory;
        }

	protected XYDataset createDataset() {
            return null;
	}
        
        public void showChart(PortalContext portalContext) throws Exception {
            Window chartW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/metrics.zul", null, null);
            String jsonString = Visualization.createChartJson(this.createDataset()).toString();
            String javascript = "loadData('" + jsonString + "');";
            Clients.evalJavaScript(javascript);
            Stage stage = bpf.getStageByName(stageName);
            chartW.setTitle(this.getFullName());
            chartW.doModal();
        }
}
