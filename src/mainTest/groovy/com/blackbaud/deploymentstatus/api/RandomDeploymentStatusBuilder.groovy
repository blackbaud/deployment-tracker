package com.blackbaud.deploymentstatus.api;

import static com.blackbaud.deploymentstatus.api.ClientARandom.aRandom

class RandomDeploymentStatusBuilder extends DeploymentStatus.DeploymentStatusBuilder {

    public RandomDeploymentStatusBuilder() {
        appName(aRandom.text(20))
        buildVersion(aRandom.text(20))
        releaseVersion(aRandom.text(20))
        gitSha(aRandom.text(20))
    }

}
