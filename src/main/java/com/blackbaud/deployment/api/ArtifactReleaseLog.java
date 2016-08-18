package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactReleaseLog {

    private String artifactId;
    private String buildVersion;
    private String releaseVersion;
    private String prevBuildVersion;
    private String prevReleaseVersion;
    private String deployer;
    private LocalDate releaseDate;
    private Set<String> stories;
    private Set<String> developers;
}
