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
public class LinkedHashSetDelimitedStringConverter implements AttributeConverter<LinkedHashSet<String>, String> {
    @Override
    public LinkedHashSet<String> convertToEntityAttribute(String attribute) {
        if(attribute == null) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(Arrays.asList(attribute.split(",")));
    }

    @Override
    public String convertToDatabaseColumn(LinkedHashSet<String> dbData) {
        if (dbData == null) {
            return null;
        }
        return StringUtils.join(dbData, ',');
    }
}
