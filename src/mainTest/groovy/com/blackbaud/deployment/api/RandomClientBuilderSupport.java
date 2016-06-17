package com.blackbaud.deployment.api;

public class RandomClientBuilderSupport {

    public RandomArtifactReleaseInfoBuilder artifactReleaseInfo() {
        return new RandomArtifactReleaseInfoBuilder();
    }

    public RandomArtifactInfoBuilder artifactInfo() {
        return new RandomArtifactInfoBuilder();
    }

}
