package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseBuilder extends ArtifactRelease.ArtifactReleaseBuilder {

    public RandomArtifactReleaseBuilder() {
        artifactId(aRandom.text(100))
        buildVersion(aRandom.text(100))
        releaseVersion(aRandom.text(100))
        gitSha(aRandom.text(100))
    }

}
