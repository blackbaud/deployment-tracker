package com.blackbaud.deployment.api

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseLogBuilder extends ArtifactReleaseInfo.ArtifactReleaseInfoBuilder {

    public RandomArtifactReleaseLogBuilder() {
        artifactId(aRandom.text(20))
                .buildVersion(aRandom.text(20))
                .releaseVersion(aRandom.text(20))
                .prevBuildVersion(aRandom.text(20))
                .prevReleaseVersion(aRandom.text(20))
                .deployer(aRandom.text(20))
                .releaseDate(aRandom.zonedDateTime())
    }
}
