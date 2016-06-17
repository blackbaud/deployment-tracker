package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactReleaseInfoBuilder extends ArtifactReleaseInfo.ArtifactReleaseInfoBuilder {

    public RandomArtifactReleaseInfoBuilder() {
        artifactId(aRandom.text(20))
        buildVersion(aRandom.text(20))
        releaseVersion(aRandom.text(20))
        gitSha(aRandom.text(20))
    }

}
