package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
@NoArgsConstructor
public class ArtifactReleaseDiff {
    private String artifactId;
    private ArtifactRelease currentRelease;
    private ArtifactRelease prevRelease;
    private String foundation;
    private String space;
    private Set<String> stories;
    private Set<String> developers;
    private String deployer;
    private ZonedDateTime releaseDate;

    @Builder
    public ArtifactReleaseDiff(String artifactId, ArtifactRelease currentRelease, ArtifactRelease prevRelease, String foundation, String space, Set<String> stories, Set<String> developers, String deployer, ZonedDateTime releaseDate) {
        this.artifactId = artifactId;
        this.currentRelease = currentRelease;
        this.prevRelease = prevRelease;
        this.foundation = foundation;
        this.space = space;
        this.stories = stories == null ? Collections.emptySet() : stories;
        this.developers = developers == null ? Collections.emptySet() : developers;
        this.deployer = deployer;
        this.releaseDate = releaseDate;
    }

    @JsonIgnore
    public String getDevSha() {
        return currentRelease == null ? null : currentRelease.getGitSha();
    }

    @JsonIgnore
    public String getProdSha() {
        return prevRelease == null ? null : prevRelease.getGitSha();
    }

    public void addStories(Set<String> stories) {
        if (this.stories == null) {
            this.stories = new TreeSet<>();
        }
        this.stories.addAll(stories);
    }

    public void addDevelopers(Set<String> developers) {
        if (this.developers == null) {
            this.developers = new TreeSet<>();
        }
        this.developers.addAll(developers);
    }
}
