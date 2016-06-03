package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {
    private Map<String, DeploymentDiff> deploymentDiffs = Collections.emptyMap();
}
