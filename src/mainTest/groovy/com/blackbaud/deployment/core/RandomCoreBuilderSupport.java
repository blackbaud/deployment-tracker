package com.blackbaud.deployment.core;
import com.blackbaud.deployment.core.domain.RandomArtifactInfoEntityBuilder;
import com.blackbaud.deployment.core.domain.RandomArtifactReleaseInfoEntityBuilder;

public class RandomCoreBuilderSupport {

    public RandomArtifactReleaseInfoEntityBuilder artifactReleaseInfoEntity() {
        return new RandomArtifactReleaseInfoEntityBuilder();
    }

    public RandomArtifactInfoEntityBuilder artifactInfoEntity(){
        return new RandomArtifactInfoEntityBuilder();
    }

}
