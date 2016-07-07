package com.blackbaud.deployment.core.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Converter
public class StringDelimiterConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public Set<String> convertToEntityAttribute(String attribute) {
        if(attribute == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(Arrays.asList(attribute.split(",")));
    }

    @Override
    public String convertToDatabaseColumn(Set<String> dbData) {
        if (dbData == null) {
            return null;
        }
        return StringUtils.join(dbData, ',');
    }
}
