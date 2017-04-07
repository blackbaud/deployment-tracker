package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactRelease {
    private String artifactId;
    private String buildVersion;
    private String releaseVersion;
    private String gitSha;
    private String deployJobUrl;
    private List<ArtifactInfo> dependencies;
}
