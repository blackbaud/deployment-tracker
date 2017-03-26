package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactDependencyPrimaryKey implements Serializable {
    private String artifactId;
    private String buildVersion;
    private String dependencyId;
    private String dependencyBuildVersion;

}
