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
public class ArtifactInfo {
    private String artifactId;
    private String buildVersion;
    private String gitSha;
    private List<ArtifactInfo> dependencies;

    public void addDependencies(ArtifactInfo artifactInfo) {
        if (artifactInfo != null) {
            if (this.dependencies == null) {
                this.dependencies = new ArrayList<>();
            }
            this.dependencies.add(artifactInfo);
        }
    }
}
