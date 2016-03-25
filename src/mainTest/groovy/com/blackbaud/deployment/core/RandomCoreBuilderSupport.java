package com.blackbaud.deployment.core;
import com.blackbaud.deployment.core.domain.RandomDeploymentInfoEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomDeploymentInfoEntityBuilder deploymentInfoEntity() {
        return new RandomDeploymentInfoEntityBuilder();
    }

}
