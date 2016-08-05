package com.blackbaud.deployment.core;

import com.blackbaud.deployment.core.domain.RandomArtifactReleaseInfoEntityBuilder;
import com.blackbaud.deployment.core.domain.RandomReleasePlanEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomArtifactReleaseInfoEntityBuilder artifactReleaseInfoEntity() {
        return new RandomArtifactReleaseInfoEntityBuilder();
    }

    public RandomReleasePlanEntityBuilder releasePlanEntity() {
        return new RandomReleasePlanEntityBuilder();
    }

}
