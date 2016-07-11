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
import java.util.SortedSet;
import java.util.TreeSet;

@Converter
public class SortedSetDelimitedStringConverter implements AttributeConverter<SortedSet<String>, String> {
    @Override
    public SortedSet<String> convertToEntityAttribute(String attribute) {
        if(StringUtils.isEmpty(attribute)) {
            return new TreeSet<>();
        }
        return new TreeSet<>(Arrays.asList(attribute.split(",")));
    }

    @Override
    public String convertToDatabaseColumn(SortedSet<String> dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return StringUtils.join(dbData, ',');
    }
}
