package org.apromore.logman.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogStatsBatch implements StatsCalculator {
    private List<StatsCalculator> statsList;
    
    public LogStatsBatch(StatsCalculator...registeredStats) {
        statsList = new ArrayList<StatsCalculator>();
        statsList.addAll(Arrays.asList(registeredStats));
    }

    @Override
    public void run() {
        
    }
    
    
    
}
