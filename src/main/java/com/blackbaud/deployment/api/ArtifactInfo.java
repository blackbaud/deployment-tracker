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
    private List<ArtifactInfo> dependencies = new ArrayList<>();

    public static class ArtifactInfoBuilder {
        private List<ArtifactInfo> dependencies = new ArrayList<>();
    }
}
