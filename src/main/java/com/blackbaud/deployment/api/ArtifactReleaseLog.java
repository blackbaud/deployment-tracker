package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactReleaseLog {

    private String artifact_id;
    private String build_version;
    private String release_version;
    private String prev_build_version;
    private String prev_release_version;
    private String deployer;
    private ZonedDateTime releaseDate;
    private Set<String> stories;
    private Set<String> developers;
}
