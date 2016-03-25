package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomDeploymentInfoBuilder extends DeploymentInfo.DeploymentInfoBuilder {

    public RandomDeploymentInfoBuilder() {
        artifactId(aRandom.text(20))
        buildVersion(aRandom.text(20))
        releaseVersion(aRandom.text(20))
        gitSha(aRandom.text(20))
    }

}
