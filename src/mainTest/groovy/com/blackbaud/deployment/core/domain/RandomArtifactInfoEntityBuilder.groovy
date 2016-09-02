package com.blackbaud.deployment.core.domain

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactInfoEntityBuilder extends ArtifactInfoEntity.ArtifactInfoEntityBuilder {

    public RandomArtifactInfoEntityBuilder() {
        artifactId(aRandom.text(20))
        .buildVersion(aRandom.text(20))
        .gitSha(aRandom.text(20))
        .listOrder(aRandom.intBetween(1, 20))
    }
}
