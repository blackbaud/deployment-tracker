package com.blackbaud.deployment.api;

public class RandomClientBuilderSupport {

    public RandomDeploymentInfoBuilder deploymentInfo() {
        return new RandomDeploymentInfoBuilder();
    }

    public RandomArtifactInfoBuilder artifactInfo() {
        return new RandomArtifactInfoBuilder();
    }

}
