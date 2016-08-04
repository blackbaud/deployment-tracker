package com.blackbaud.deployment.api;
import com.blackbaud.deploymenttracker.api.RandomReleasePlanBuilder;

public class RandomClientBuilderSupport {

    public RandomArtifactReleaseInfoBuilder artifactReleaseInfo() {
        return new RandomArtifactReleaseInfoBuilder();
    }

    public RandomArtifactInfoBuilder artifactInfo() {
        return new RandomArtifactInfoBuilder();
    }

    public RandomReleasePlanBuilder releasePlan() {
        return new RandomReleasePlanBuilder();
    }

}
