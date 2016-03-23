package com.blackbaud.deploymentstatus.core;
import com.blackbaud.deploymentstatus.core.domain.RandomDeploymentStatusEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomDeploymentStatusEntityBuilder deploymentStatusEntity() {
        return new RandomDeploymentStatusEntityBuilder();
    }

}
