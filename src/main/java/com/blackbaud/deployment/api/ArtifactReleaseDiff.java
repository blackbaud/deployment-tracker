package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
<<<<<<< HEAD:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
<<<<<<< HEAD:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
public class ArtifactReleaseDiff {
=======
public class DeploymentDiff {
>>>>>>> 746a757... LUM-9138 first pass at renaming:src/main/java/com/blackbaud/deployment/api/DeploymentDiff.java
=======
public class ArtifactReleaseDiff {
>>>>>>> f678692... more renaming:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
    private ArtifactReleaseInfo dev;
    private ArtifactReleaseInfo prod;
    private Set<String> stories;
    private Set<String> developers;

    @Builder
<<<<<<< HEAD:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
<<<<<<< HEAD:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
    public ArtifactReleaseDiff(ArtifactReleaseInfo dev, ArtifactReleaseInfo prod, Set<String> stories, Set<String> developers) {
=======
    public DeploymentDiff(ArtifactReleaseInfo dev, ArtifactReleaseInfo prod, Set<String> stories, Set<String> developers) {
>>>>>>> 746a757... LUM-9138 first pass at renaming:src/main/java/com/blackbaud/deployment/api/DeploymentDiff.java
=======
    public ArtifactReleaseDiff(ArtifactReleaseInfo dev, ArtifactReleaseInfo prod, Set<String> stories, Set<String> developers) {
>>>>>>> f678692... more renaming:src/main/java/com/blackbaud/deployment/api/ArtifactReleaseDiff.java
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
