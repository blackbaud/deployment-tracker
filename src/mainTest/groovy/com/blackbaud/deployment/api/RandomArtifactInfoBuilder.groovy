package com.blackbaud.deployment.api;

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactInfoBuilder extends ArtifactInfo.ArtifactInfoBuilder {

    public RandomArtifactInfoBuilder() {
        artifactId(aRandom.text(20))
        buildVersion(aRandom.text(20))
        gitSha(aRandom.text(20))
    }

}
