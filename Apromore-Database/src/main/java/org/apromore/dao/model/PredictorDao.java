package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "predictor")
@Data
@NoArgsConstructor
public class PredictorDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "log_id")
    private int logId;
    @Column(name = "name")
    private String name;
    @Column(name = "prediction_type")
    @Enumerated(EnumType.STRING)
    private PredictionType predictionType;
    @Column(name = "target_attribute")
    private String targetAttribute;
    @Column(name = "save_location")
    private String saveLocation;
    @Column(name = "ppm_status")
    @Enumerated(EnumType.STRING)
    private PpmStatus ppmStatus;

    public PredictorDao(int logId, String name, PredictionType predictionType, String targetAttribute,
                        String saveLocation, PpmStatus ppmStatus) {
        this.logId = logId;
        this.name = name;
        this.predictionType = predictionType;
        this.targetAttribute = targetAttribute;
        this.saveLocation = saveLocation;
        this.ppmStatus = ppmStatus;
    }
}
