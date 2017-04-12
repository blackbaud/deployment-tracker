package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
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

    public static class ArtifactReleaseDiffBuilder {
        private Set<String> stories = new TreeSet<>();
        private Set<String> developers = new TreeSet<>();
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
        if (stories == null) {
            return;
        }
        this.stories.addAll(stories);
    }

    public void addDevelopers(Set<String> developers) {
        if (developers == null) {
            return;
        }
        this.developers.addAll(developers);
    }

    public boolean currentReleaseHasDependencies() {
        return currentRelease.hasDependencies();
    }

    public boolean prevReleaseHasDependencies() {
        return prevRelease.hasDependencies();
    }
}
