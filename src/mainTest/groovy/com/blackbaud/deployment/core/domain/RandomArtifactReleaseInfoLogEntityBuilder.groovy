package com.blackbaud.deployment.core.domain

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseInfoLogEntityBuilder extends ArtifactReleaseInfoLogEntity.ArtifactReleaseInfoLogEntityBuilder {

    public RandomArtifactReleaseInfoLogEntityBuilder() {
        artifactId(aRandom.text(20))
                .space(aRandom.text(20))
                .foundation(aRandom.text(20))
                .buildVersion(aRandom.text(20))
                .releaseVersion(aRandom.text(20))
                .prevBuildVersion(aRandom.text(20))
                .prevReleaseVersion(aRandom.text(20))
                .deployer(aRandom.text(20))
    }
}
