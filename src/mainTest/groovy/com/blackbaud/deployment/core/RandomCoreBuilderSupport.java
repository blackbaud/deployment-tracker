package com.blackbaud.deployment.core;

import com.blackbaud.deployment.core.domain.RandomArtifactReleaseInfoEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomArtifactReleaseInfoEntityBuilder artifactReleaseInfoEntity() {
        return new RandomArtifactReleaseInfoEntityBuilder();
    }

}
