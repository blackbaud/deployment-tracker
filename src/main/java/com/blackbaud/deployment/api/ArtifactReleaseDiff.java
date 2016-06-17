package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
public class ArtifactReleaseDiff {
    private ArtifactReleaseInfo dev;
    private ArtifactReleaseInfo prod;
    private Set<String> stories;
    private Set<String> developers;

    @Builder
    public ArtifactReleaseDiff(ArtifactReleaseInfo dev, ArtifactReleaseInfo prod, Set<String> stories, Set<String> developers) {
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
