/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;

/**
 *
 * @author Administrator
 */
public class DatasetFactory {
    public static XYDataset createCFDDataset(SPF bpf) {
        TimeTableXYDataset dataset = new TimeTableXYDataset();

        for (int i = bpf.getStages().size() - 1; i >= 0; i--) {
            Stage stage = bpf.getStages().get(i);

            if (i == bpf.getStages().size() - 1) {
                for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                    TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                    dataset.add(timeP, stage.getServicePassedCounts().get(j).doubleValue(), stage.getName() + "-Complete");
                }
            }

            //Exit band
            for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getServiceDepartureCounts().get(j) - stage.getServicePassedCounts().get(j),
                                stage.getName() + "-Exit");
                //System.out.println("Exit " + j + ": " + (stage.getServiceDepartureCounts().get(j) - stage.getServicePassedCounts().get(j)));
            }

            //Service band
            for (int j = 0; j <  bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getServiceArrivalCounts().get(j) - stage.getServiceDepartureCounts().get(j),
                                stage.getName() + "-Service");
            }

            //Queue band
        for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getQueueArrivalCounts().get(j) - stage.getServiceArrivalCounts().get(j),
                                stage.getName() + "-Queue");
            }

        }

        return dataset;
    }
    
    private static TimePeriod getTimePeriod(SPF bpf, DateTime timePoint) {
        if (bpf.getConfig().getTimeStep() <= 3600) {
            return new Hour(timePoint.toDate());
        } else {
            return new Day(timePoint.toDate());
        }
    }
}
