package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactInfo {

    private String artifactId;
    private String buildVersion;
    private String gitSha;
    private Integer releasePlanOrder;

}
