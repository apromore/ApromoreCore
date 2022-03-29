package org.apromore.dao.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prediction_predictors")
@Data
@NoArgsConstructor
public class PredictionPredictors {
    @Id
    @NotNull
    @ManyToOne
    @JoinColumn(name = "prediction_id")
    private Long predictionId;

    @Id
    @NotNull
    @ManyToOne
    @JoinColumn(name = "predictor_id")
    private Long predictorId;
}
