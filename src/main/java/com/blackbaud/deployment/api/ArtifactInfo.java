package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactInfo {
    private String artifactId;
    private String buildVersion;
    private String gitSha;
    // TODO make this a list
    private String dependencyId;
    private String dependencyBuildVersion;
}
