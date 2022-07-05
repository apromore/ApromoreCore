package org.apromore.processsimulation.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
public class SimulationAdditionalParam {
    @Value("${qbp.maxAllowedProcesses}")
    private long maxAllowedProcesses = 25000;
}
