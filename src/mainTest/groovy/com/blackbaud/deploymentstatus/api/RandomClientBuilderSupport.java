package com.blackbaud.deploymentstatus.api;
import com.blackbaud.deploymentstatus.api.RandomDeploymentStatusBuilder;

public class RandomClientBuilderSupport {

    public RandomDeploymentStatusBuilder deploymentStatus() {
        return new RandomDeploymentStatusBuilder();
    }

}
