package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class DeploymentDiff {
    private DeploymentInfo dev;
    private DeploymentInfo prod;
    private Set<String> stories;
    private Set<String> developers;

    @Builder
    public DeploymentDiff(DeploymentInfo dev, DeploymentInfo prod, Set<String> stories, Set<String> developers) {
        this.dev = dev;
        this.prod = prod;
        this.stories = stories == null ? Collections.emptySet() : stories;
        this.developers = developers == null ? Collections.emptySet() : developers;
    }

    @JsonIgnore
    public String getDevSha() {
        return dev == null ? null : dev.getGitSha();
    }

    @JsonIgnore
    public String getProdSha() {
        return prod == null ? null : prod.getGitSha();
    }

}
