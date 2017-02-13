package org.apromore.plugin.portal.perfmining.view.system;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.joda.time.DateTime;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;


public class SPFMainView extends SPFView {
	public SPFMainView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	@Override
	public XYDataset createDataset() {
		TimeTableXYDataset dataset = new TimeTableXYDataset();

		for (int i = bpf.getStages().size() - 1; i >= 0; i--) {
			Stage stage = bpf.getStages().get(i);

			if (i == bpf.getStages().size() - 1) {
				for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
					TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(j));
					dataset.add(timeP, stage.getServicePassedCounts().get(j), stage.getName() + "-Complete");
				}
			}

			//Exit band
			for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(j));
				dataset.add(timeP, stage.getServiceDepartureCounts().get(j) - stage.getServicePassedCounts().get(j),
						stage.getName() + "-Exit");
			}

			//Service band
			for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(j));
				dataset.add(timeP, stage.getServiceArrivalCounts().get(j) - stage.getServiceDepartureCounts().get(j),
						stage.getName() + "-Service");
			}

			//Queue band
			for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(j));
				dataset.add(timeP, stage.getQueueArrivalCounts().get(j) - stage.getServiceArrivalCounts().get(j),
						stage.getName() + "-Queue");
			}

		}

		return dataset;
	}

        @Override
        public void showChart(PortalContext portalContext) throws Exception {
            Window chartW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/spfview.zul", null, null);
            Button updateButton = (Button)chartW.getFellow("updateButton");
            updateButton.addEventListener("onUpdate", new UpdateButtonEventListener(chartW, bpf));
            String jsonString = Visualization.createCFDJson(this.createDataset()).toString();
            String javascript = "loadData('" + jsonString + "');";
            //System.out.println(javascript);
            Clients.evalJavaScript(javascript);
            chartW.setTitle("Staged Process Flow");
            chartW.doModal();
        }
     
        


//        private List<String[]> getUpdatedData(){
//          ArrayList<String[]> list= new ArrayList<String[]>();
//
//          list.add(new String[]{"Test Mail1-updated","TonyQ", "10k"});
//          list.add(new String[]{"Test Mail12-updated","Ryan", "100k"});
//          list.add(new String[]{"Test Mail13-updated","Simon", "15k"});
//          list.add(new String[]{"Test Mail14-updated","Jimmy", "5k"});    
//          return list;
//       }
        
//            updateButton.addEventListener("onUser", new EventListener<Event>() {
//                public void onEvent(Event event) throws Exception {
//                    ForwardEvent eventf = (ForwardEvent) event;
//                    System.out.println("Range date: " + eventf.getOrigin().getData());
//                }
//            });   
        
//        public void onUser$updateButton(Event event) throws Exception {
//            Event eventx = Events.getRealOrigin((ForwardEvent)event);
//            String test = eventx.getData().toString();
//            System.out.println(test);
//        }  
}
