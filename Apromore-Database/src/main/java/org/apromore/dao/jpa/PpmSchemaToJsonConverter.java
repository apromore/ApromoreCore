package org.apromore.dao.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apromore.dao.model.PpmSchema;

@Slf4j
public class PpmSchemaToJsonConverter implements AttributeConverter<PpmSchema, String> {
    private ObjectMapper objectMapper;

    public PpmSchemaToJsonConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(PpmSchema attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public PpmSchema convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, PpmSchema.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
