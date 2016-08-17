package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactReleaseInfoLogPrimaryKey implements Serializable {

    private String artifactId;
    private String releaseVersion;

}
