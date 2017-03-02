/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view.system;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Window;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.joda.time.DateTime;
import org.zkoss.zul.ListModelList;

/**
 *
 * @author Administrator
 * Based on http://zkfiddle.org/sample/2vah9aj/29-add-new-row#source-2
 */
public class UpdateButtonEventListener implements EventListener<Event> {
    Window parentW = null;
    SPF bpf = null;
    
    public UpdateButtonEventListener(Window window, SPF bpf) {
        this.parentW = window;
        this.bpf = bpf;
    }
    public void onEvent(Event event) {
        JSONObject json = (JSONObject)event.getData();
        System.out.println(json.get("interval").toString());
        String[] interval = json.get("interval").toString().split(",");
        DateTime start = new DateTime(Double.valueOf(interval[0]).longValue());
        DateTime end = new DateTime(Double.valueOf(interval[1]).longValue());
        
        Grid summaryGrid = (Grid)this.parentW.getFellow("perfminingresult_summarygrid");
        summaryGrid.setRowRenderer(new SummaryRowRenderer());
        summaryGrid.setModel(new ListModelList(this.getData(start, end)));
    }
    
    private List<String[]> getData(DateTime start, DateTime end) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        Object[][] tableContent = new Object[bpf.getStages().size() * 2 + 1][7];
        try {
            tableContent[0][0] = "System";
            tableContent[0][1] = formatter.format(24 * bpf.getMeanArrivalRate(start, end)) + " / "
                            + formatter.format(24 * bpf.getMedianArrivalRate(start, end));
            tableContent[0][2] = formatter.format(24 * bpf.getMeanDepartureRate(start, end)) + " / "
                            + formatter.format(24 * bpf.getMedianDepartureRate(start, end));
            tableContent[0][3] = formatter.format(24 * bpf.getMeanExitRate(start, end)) + " / "
                            + formatter.format(24 * bpf.getMedianExitRate(start, end));
            tableContent[0][4] = formatter.format(bpf.getMeanWIP(start, end)) + " / "
                            + formatter.format(bpf.getMedianWIP(start, end));
            tableContent[0][5] = formatter.format(bpf.getMeanTIS(start, end)) + " / "
                            + formatter.format(bpf.getMedianTIS(start, end));
            if (bpf.getConfig().getCheckStartCompleteEvents()) {
                tableContent[0][6] = formatter.format(100 * bpf.getMeanFE(start, end));
            } else {
                tableContent[0][6] = "--";
            }

            int i = 1;
            for (Stage stage : bpf.getStages()) {
                tableContent[i][0] = stage.getName() + "-queue";

                //QUEUE
                tableContent[i][1] = formatter.format(24 * stage.getMean(SPF.CHAR_QUEUE_ARRIVAL_RATE, start, end))
                                + " / " + formatter.format(24 * stage.getMedian(SPF.CHAR_QUEUE_ARRIVAL_RATE, start, end));
                tableContent[i][2] = formatter.format(24 * stage.getMean(SPF.CHAR_QUEUE_DEPARTURE_RATE, start, end))
                                + " / " + formatter.format(24 * stage.getMedian(SPF.CHAR_QUEUE_DEPARTURE_RATE, start, end));
                tableContent[i][4] = formatter.format(stage.getMean(SPF.CHAR_QUEUE_CIP, start, end)) + " / "
                                + formatter.format(stage.getMedian(SPF.CHAR_QUEUE_CIP, start, end));
                tableContent[i][5] = formatter.format(stage.getMean(SPF.CHAR_QUEUE_TIS, start, end)) + " / "
                                + formatter.format(stage.getMedian(SPF.CHAR_QUEUE_TIS, start, end));

                //SERVICE
                tableContent[i + 1][0] = stage.getName() + "-service";
                tableContent[i + 1][1] = formatter
                                .format(24 * stage.getMean(SPF.CHAR_SERVICE_ARRIVAL_RATE, start, end))
                                + " / "
                                + formatter.format(24 * stage.getMedian(SPF.CHAR_SERVICE_ARRIVAL_RATE, start, end));
                tableContent[i + 1][2] = formatter.format(24 * stage.getMean(SPF.CHAR_SERVICE_DEPARTURE_RATE, start,
                                end))
                                + " / "
                                + formatter.format(24 * stage.getMedian(SPF.CHAR_SERVICE_DEPARTURE_RATE, start, end));
                tableContent[i + 1][3] = formatter.format(24 * stage.getMean(SPF.CHAR_SERVICE_EXIT_RATE, start, end))
                                + " / " + formatter.format(24 * stage.getMedian(SPF.CHAR_SERVICE_EXIT_RATE, start, end));
                tableContent[i + 1][4] = formatter.format(stage.getMean(SPF.CHAR_SERVICE_CIP, start, end)) + " / "
                                + formatter.format(stage.getMedian(SPF.CHAR_SERVICE_CIP, start, end));
                tableContent[i + 1][5] = formatter.format(stage.getMean(SPF.CHAR_SERVICE_TIS, start, end)) + " / "
                                + formatter.format(stage.getMedian(SPF.CHAR_SERVICE_TIS, start, end));

                if (bpf.getConfig().getCheckStartCompleteEvents()) {
                    tableContent[i + 1][6] = formatter.format(100 * stage.getMean(SPF.CHAR_SERVICE_FLOW_EFFICIENCY,
                                        start, end))
                                        + " / "
                                        + formatter.format(100 * stage.getMedian(SPF.CHAR_SERVICE_FLOW_EFFICIENCY, start, end));
                } else {
                    tableContent[i + 1][6] = "--";
                }

                i = i + 2;
            }
        } catch (Exception e) {
            //
        }

        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int i=0;i<tableContent.length;i++) {
            String[] row = new String[tableContent[i].length];
            for (int j=0; j<tableContent[i].length;j++) {
                if (tableContent[i][j] != null) {
                    row[j] = tableContent[i][j].toString();
                }
                else {
                    row[j] = "";
                }
                
            }
            list.add(row);
        }

        return list;
    }    
}
