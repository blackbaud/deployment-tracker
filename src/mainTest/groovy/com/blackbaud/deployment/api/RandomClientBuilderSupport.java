package com.blackbaud.deployment.api;

public class RandomClientBuilderSupport {

    public RandomArtifactReleaseBuilder artifactReleaseInfo() {
        return new RandomArtifactReleaseBuilder();
    }

    public RandomArtifactInfoBuilder artifactInfo() {
        return new RandomArtifactInfoBuilder();
    }

    public RandomReleasePlanBuilder releasePlan() {
        return new RandomReleasePlanBuilder();
    }

}
