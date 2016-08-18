package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseInfoBuilder extends ArtifactReleaseInfo.ArtifactReleaseInfoBuilder {

    public RandomArtifactReleaseInfoBuilder() {
        artifactId(aRandom.text(100))
        buildVersion(aRandom.text(100))
        releaseVersion(aRandom.text(100))
        gitSha(aRandom.text(100))
    }

}
