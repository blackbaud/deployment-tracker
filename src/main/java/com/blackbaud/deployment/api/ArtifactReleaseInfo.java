package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactReleaseInfo {

    private String artifactId;
    private String buildVersion;
    private String releaseVersion;
    private String gitSha;

}