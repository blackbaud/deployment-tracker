package com.blackbaud.deployment.core.domain.git;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class StoriesAndDevelopers {
    private Set<String> stories = new TreeSet<>();
    private Set<String> developers = new TreeSet<>();
}
