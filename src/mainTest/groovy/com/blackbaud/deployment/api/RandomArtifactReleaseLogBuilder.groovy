package com.blackbaud.deployment.api

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseLogBuilder extends ArtifactRelease.ArtifactReleaseBuilder {

    public RandomArtifactReleaseLogBuilder() {
        artifactId(aRandom.text(100))
                .buildVersion(aRandom.text(100))
                .releaseVersion(aRandom.text(100))
                .prevBuildVersion(aRandom.text(100))
                .prevReleaseVersion(aRandom.text(100))
                .deployer(aRandom.text(100))
                .releaseDate(aRandom.zonedDateTime())
    }
}
