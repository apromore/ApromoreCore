package org.apromore.service.logimporter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParquetColumnType {
    private String name;
    private String primitiveType;
    private String logicalType;

    public ParquetColumnType(String name) {
        this.name = name;
    }
}
