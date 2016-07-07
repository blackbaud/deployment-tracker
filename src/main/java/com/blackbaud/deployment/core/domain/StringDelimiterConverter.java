package com.blackbaud.deployment.core.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringDelimiterConverter implements AttributeConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityAttribute(String attribute) {
        if(attribute == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(attribute.split(","));
    }

    @Override
    public String convertToDatabaseColumn(List<String> dbData) {
        if (dbData == null) {
            return null;
        }
        return StringUtils.join(dbData, ',');
    }
}
