package com.blackbaud.deployment.core;

import com.blackbaud.deployment.core.domain.RandomArtifactInfoEntityBuilder;
import com.blackbaud.deployment.core.domain.RandomArtifactReleaseInfoLogEntityBuilder;
import com.blackbaud.deployment.core.domain.RandomReleasePlanEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomArtifactInfoEntityBuilder artifactInfoEntity() {
        return new RandomArtifactInfoEntityBuilder();
    }

    public RandomReleasePlanEntityBuilder releasePlanEntity() {
        return new RandomReleasePlanEntityBuilder();
    }

    public RandomArtifactReleaseInfoLogEntityBuilder releaseLogEntity() {
        return new RandomArtifactReleaseInfoLogEntityBuilder();
    }
}
